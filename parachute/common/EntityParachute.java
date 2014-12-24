//  =====GPL=============================================================
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; version 2 dated June, 1991.
// 
//  This program is distributed in the hope that it will be useful, 
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
// 
//  You should have received a copy of the GNU General Public License
//  along with this program;  if not, write to the Free Software
//  Foundation, Inc., 675 Mass Ave., Cambridge, MA 02139, USA.
//  =====================================================================
//
//
// Copyright 2011-2014 Michael Sheppard (crackedEgg)
//
package com.parachute.common;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;

public class EntityParachute extends Entity {

	private double velocityX;
	private double velocityY;
	private double velocityZ;
	private double motionFactor;
	private double maxAltitude;
	private boolean allowThermals;
	private boolean smallCanopy;
	private boolean lavaThermals;
	private boolean ridingThermals;
	private double lavaDistance;
	private double maxThermalRise;
	private double curLavaDistance;
	private boolean weatherAffectsDrift;
	private boolean allowTurbulence;

	final static double drift = 0.004;
	final static double ascend = drift * -10.0;

	final static int modeDrift = 0; // key up
	final static int modeAscend = 1; // key down

	final static double forwardSpeed = 0.75;

	private final double d2r = 0.0174532925199433; // degrees to radians
	private final double r2d = 57.2957795130823;   // radians to degrees

	private static boolean ascendMode;

	public EntityParachute(World world)
	{
		super(world);
		worldObj = world;

		smallCanopy = Parachute.instance.isSmallCanopy();
		weatherAffectsDrift = Parachute.instance.getWeatherAffectsDrift();
		allowTurbulence = Parachute.instance.getAllowturbulence();

		preventEntitySpawning = true;
		setSize(2.0F, 1.0F);
		motionFactor = 0.07D;
		ascendMode = false;
		ridingThermals = false;
		lavaDistance = Parachute.instance.getMinLavaDistance();
		maxThermalRise = 48;
		curLavaDistance = lavaDistance;

		allowThermals = Parachute.instance.getAllowThermals();
		maxAltitude = Parachute.instance.getMaxAltitude();
		lavaThermals = Parachute.instance.getAllowLavaThermals();
	}

	public EntityParachute(World world, double x, double y, double z)
	{
		this(world);

		setPosition(x, y, z);

		motionX = 0.0D;
		motionY = 0.0D;
		motionZ = 0.0D;

		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
	}

	static public void setAscendMode(boolean mode)
	{
		ascendMode = mode;
	}

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	protected void entityInit()
	{
		dataWatcher.addObject(17, 0); // time since last hit
		dataWatcher.addObject(18, 1); // forward direction
		dataWatcher.addObject(19, 0.0F); // damage taken | current damage
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity)
	{
		if (entity != riddenByEntity && entity.ridingEntity != this) {
			return entity.getEntityBoundingBox();
		}
		return null;
	}

//	@Override
//	public boolean shouldRiderSit()
//	{
//		return isNearGround(new BlockPos(this).subtract(new Vec3i(0.0, Math.abs(getMountedYOffset() + 1.0), 0.0)));
//	}
	
	@Override
	public boolean canBePushed()
	{
		return true;
	}

	@Override
	public double getMountedYOffset()
	{
		return smallCanopy ? -2.5 : -3.5;
	}

	public void destroyParachute()
	{
		this.setDead();
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return !isDead;
	}

	@Override
	public void func_180426_a(double x, double y, double z, float yaw, float pitch, int inc, boolean unused)
	{
		double deltaX = x - posX;
		double deltaY = y - posY;
		double deltaZ = z - posZ;
		double magnitude = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;

		if (magnitude <= 1.0D) {
			return;
		}

		// forward/vertical motion
		motionX = velocityX;
		motionY = velocityY;
		motionZ = velocityZ;
	}

	@Override
	public void setVelocity(double x, double y, double z)
	{
		velocityX = motionX = x;
		velocityY = motionY = y;
		velocityZ = motionZ = z;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		// the player has probably been killed or pressed LSHIFT
		if (riddenByEntity == null) {
			if (!worldObj.isRemote) {
				destroyParachute();
			}
			return;
		}

		if (getTimeSinceHit() > 0) {
			setTimeSinceHit(getTimeSinceHit() - 1);
		}
		if (getDamageTaken() > 0.0F) {
			setDamageTaken(0.0F);
		}

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		// forward velocity
		double velocity = Math.sqrt(motionX * motionX + motionZ * motionZ);

		// drop the chute when close to ground
		if (Parachute.instance.isAutoDismount()) {
			double offset = Math.abs(getMountedYOffset());
			if (checkShouldDropChute(new BlockPos(this).subtract(new Vec3i(0.0, offset + 1.0, 0.0)))) {
				return;
			}
		}

		// forward velocity for 'W' keypress
		// moveForward happens when the 'W' key is pressed. Value is either 0.0 | ~0.98
		// when allowThermals is false forwardMovement is set to the constant 'forwardSpeed'
		// and appied to motionX and motionZ
		if (riddenByEntity != null && riddenByEntity instanceof EntityLivingBase) {
			EntityLivingBase rider = (EntityLivingBase) riddenByEntity;
			double forwardMovement = (allowThermals || lavaThermals) ? (double) rider.moveForward : forwardSpeed;
			if (forwardMovement > 0.0) {
				double f = rider.rotationYaw + -rider.moveStrafing * 90.0;
				motionX += (-Math.sin((double) (f * d2r)) * motionFactor * 0.05) * forwardMovement;
				motionZ += (Math.cos((double) (f * d2r)) * motionFactor * 0.05) * forwardMovement;
			}
		}

		// forward velocity when drifting
		double localvelocity = Math.sqrt(motionX * motionX + motionZ * motionZ);

		if (localvelocity > 0.35D) {
			double motionAdj = 0.35D / localvelocity;
			motionX *= motionAdj;
			motionZ *= motionAdj;
			localvelocity = 0.35D;
		}

		if (localvelocity > velocity && motionFactor < 0.35D) {
			motionFactor += (0.35D - motionFactor) / 35.0D;
			if (motionFactor > 0.35D) {
				motionFactor = 0.35D;
			}
		} else {
			motionFactor -= (motionFactor - 0.07D) / 35.0D;
			if (motionFactor < 0.07D) {
				motionFactor = 0.07D;
			}
		}

		motionY -= currentDescentRate();

		moveEntity(motionX, motionY, motionZ);

		// apply drag
		motionX *= 0.99D;
		motionY *= 0.95D;
		motionZ *= 0.99D;

		rotationPitch = 0.0F;
		double yaw = rotationYaw;
		double delta_X = prevPosX - posX;
		double delta_Z = prevPosZ - posZ;

		if (delta_X * delta_X + delta_Z * delta_Z > 0.001D) {
			yaw = (float) ((Math.atan2(delta_Z, delta_X) * r2d));
		}

		double adjustedYaw = MathHelper.wrapAngleTo180_double(yaw - (double) rotationYaw);

		if (adjustedYaw > 20.0D) {
			adjustedYaw = 20.0D;
		}
		if (adjustedYaw < -20.0D) {
			adjustedYaw = -20.0D;
		}

		rotationYaw += adjustedYaw;
		setRotation(rotationYaw, rotationPitch);

		if ((isBadWeather() || allowTurbulence) && rand.nextBoolean() == true) {
			applyTurbulence(worldObj.isThundering());
		}

		// something bad happened, somehow the skydiver was killed.
		if (riddenByEntity != null && riddenByEntity.isDead) {
			riddenByEntity = null;
			if (!worldObj.isRemote) {
				destroyParachute();
			}
		}
	}
	
	public boolean isBadWeather()
	{
		return weatherAffectsDrift && (worldObj.isRaining() || worldObj.isThundering());
	}

	public double currentDescentRate()
	{
		double descentRate = drift; // defaults to drift

		if (weatherAffectsDrift) {
			if (worldObj.isRaining()) { // rain makes you fall faster
				descentRate += 0.002;
			}

			if (worldObj.isThundering()) { // more rain really makes you fall faster
				descentRate += 0.004;
			}
		}

		if (!allowThermals && !lavaThermals) {
			return descentRate;
		}

		if (lavaThermals) {
			descentRate = doLavaThermals();
			return descentRate;
		}

		if (ascendMode) {
			descentRate = ascend;
		}

		if (maxAltitude > 0.0D) { // altitude limiting
			if (posY >= maxAltitude) {
				descentRate = drift;
			}
		}

		return descentRate;
	}

	public double doLavaThermals()
	{
		double thermals = drift;
		double offset = Math.abs(getMountedYOffset());
		final double inc = 1.0; // todo: try 0.5 for increment

		BlockPos blockPos = new BlockPos(posX, posY - offset - curLavaDistance, posZ);
		Block block = worldObj.getBlockState(blockPos).getBlock();

		if (block == Blocks.lava || block == Blocks.flowing_lava) {
			ridingThermals = true;
			curLavaDistance += inc;
			thermals = ascend;
		} else if (ridingThermals && curLavaDistance < maxThermalRise) {
			curLavaDistance += inc;
			thermals = ascend;
		} else {
			ridingThermals = false;
			curLavaDistance = lavaDistance;
		}
		return thermals;
	}

	protected boolean checkShouldDropChute(BlockPos bp)
	{
		boolean shouldDrop = false;

		if (isNearGround(bp)) {
			if (riddenByEntity != null) {
				dropParachute(this);
				if (!worldObj.isRemote) {
					destroyParachute();
				} else {
					riddenByEntity = null;
				}
			}
			shouldDrop = true;
		}
		return shouldDrop;
	}

	public boolean isNearGround(BlockPos bp)
	{
		boolean result = false;

		boolean isAirBlock = worldObj.isAirBlock(bp);
		boolean isSolidBlock = worldObj.isSideSolid(bp, EnumFacing.UP);
		Block block =  worldObj.getBlockState(bp).getBlock();
		boolean isWaterBlock = (block == Blocks.water || block == Blocks.flowing_water);
//		boolean isLavaBlock = (block == Blocks.lava || block == Blocks.flowing_lava);
		
		if (!isAirBlock && (isSolidBlock || isWaterBlock)) {
			result = true;
		}
		return result;
	}

	public void dropParachute(Entity parachute)
	{
		if (parachute == null) {
			if (ridingEntity != null) {
				setLocationAndAngles(ridingEntity.posX, ridingEntity.getEntityBoundingBox().minY + (double) ridingEntity.height, ridingEntity.posZ, rotationYaw, rotationPitch);
				ridingEntity.riddenByEntity = null;
			}
			ridingEntity = null;
		} else {
			if (ridingEntity != null) {
				ridingEntity.riddenByEntity = null;
			}
			if (parachute != null) {
				for (Entity entity = parachute.ridingEntity; entity != null; entity = entity.ridingEntity) {
					if (entity == this) {
						return;
					}
				}
			}
			ridingEntity = parachute;
			parachute.riddenByEntity = this;
		}
	}

	public void applyTurbulence(boolean roughWeather)
	{
		double rmin = 0.1;
		double rmax = roughWeather ? 0.8 : 0.5;
		double deltaX = rmin + (rmax - rmin) * rand.nextDouble();
		double deltaY = rmin + 0.2 * rand.nextDouble();
		double deltaZ = rmin + (rmax - rmin) * rand.nextDouble();
		double deltaPos = rand.nextDouble();

		if (deltaPos >= 0.5) {
			deltaPos = MathHelper.sqrt_double(deltaPos);
			deltaX /= deltaPos;
			deltaY /= deltaPos;
			deltaZ /= deltaPos;
			double deltaInv = 1.0 / deltaPos;

			if (deltaInv > 1.0) {
				deltaInv = 1.0;
			}

			deltaX *= deltaInv;
			deltaY *= deltaInv;
			deltaZ *= deltaInv;
			
			deltaX *= 0.05;
			deltaY *= 0.05;
			deltaZ *= 0.05;

			if (rand.nextBoolean()) {
				addVelocity(-deltaX, -deltaY, -deltaZ);
			} else {
				addVelocity(deltaX, deltaY, deltaZ);
			}
		}
	}

	@Override
	public void updateRiderPosition()
	{
		if (riddenByEntity != null) {
			double cosYaw = Math.cos((double) rotationYaw * d2r) * 0.4D;
			double sinYaw = Math.sin((double) rotationYaw * d2r) * 0.4D;
			riddenByEntity.setPosition(posX + cosYaw, posY + getMountedYOffset() + riddenByEntity.getYOffset(), posZ + sinYaw);
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
	}

	public void setDamageTaken(float f)
	{
		dataWatcher.updateObject(19, f);
	}

	public float getDamageTaken()
	{
		return dataWatcher.getWatchableObjectFloat(19);
	}

	public void setTimeSinceHit(int time)
	{
		dataWatcher.updateObject(17, time);
	}

	public int getTimeSinceHit()
	{
		return dataWatcher.getWatchableObjectInt(17);
	}

	public void setForwardDirection(int forward)
	{
		dataWatcher.updateObject(18, forward);
	}

	public int getForwardDirection()
	{
		return dataWatcher.getWatchableObjectInt(18);
	}
}
