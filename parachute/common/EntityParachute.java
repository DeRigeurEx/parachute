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

import com.parachute.client.AltitudeDisplay;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLeaves;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
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
	private boolean showContrails;

	final static double drift = 0.004;
	final static double ascend = drift * -10.0;

	final static int modeDrift = 0;  // key up
	final static int modeAscend = 1; // key down

	final static double forwardSpeed = 0.75;

	private final double d2r = 0.0174532925199433; // degrees to radians
	private final double r2d = 57.2957795130823;   // radians to degrees

	private static boolean ascendMode;

	public EntityParachute(World world)
	{
		super(world);

		smallCanopy = ConfigHandler.isSmallCanopy();
		weatherAffectsDrift = ConfigHandler.getWeatherAffectsDrift();
		allowTurbulence = ConfigHandler.getAllowturbulence();
		showContrails = ConfigHandler.getShowContrails();
		lavaDistance = ConfigHandler.getMinLavaDistance();
		allowThermals = ConfigHandler.getAllowThermals();
		maxAltitude = ConfigHandler.getMaxAltitude();
		lavaThermals = ConfigHandler.getAllowLavaThermals();
		
		curLavaDistance = lavaDistance;
		worldObj = world;
		preventEntitySpawning = true;
		setSize(smallCanopy ? 3.0f : 4.0f, 0.0625f);
		motionFactor = 0.07D;
		ascendMode = false;
		ridingThermals = false;
		maxThermalRise = 48;
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
	protected void entityInit() {}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity)
	{
		if (entity != riddenByEntity && entity.ridingEntity != this) {
			return entity.getEntityBoundingBox();
		}
		return null;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox()
    {
        return getEntityBoundingBox();
    }
    
    //
	// skydiver should 'hang' when on the parachute and then
	// 'pick up legs' when landing.
	//
	// FIXME: Unfortunately this stopped working around 1.6.x, movement packets
	// are not sent to server if shouldRiderSit returns false. need for shouldRiderSit
	// to return true in order to receive packets. need for it to return false for
	// player to not be in the sitting position on the parachute.
	//
//	@Override
//	public boolean shouldRiderSit()
//	{
//		return isNearGround(new BlockPos(this).add(new Vec3i(0.0, -(Math.abs(getMountedYOffset()) + 1.0), 0.0)));
//	}
	
	@Override
	public boolean shouldDismountInWater(Entity rider)
	{
		return true;
	}

	@Override
	public double getMountedYOffset()
	{
		return smallCanopy ? -2.5 : -3.5;
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return !isDead;
	}

	@Override
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int inc, boolean unused)
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
		
		// the player has pressed LSHIFT or been killed,
		// this is necessary for LSHIFT to kill the parachute
		if (riddenByEntity == null && !worldObj.isRemote) {
			ParachuteCommonProxy.setDeployed(false);
			setDead();
			return;
		}

		// initial forward velocity
		double initialVelocity = Math.sqrt(motionX * motionX + motionZ * motionZ);
		
		if (showContrails && initialVelocity > 0.2) {
			showContrails(initialVelocity);
		}
		
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		
		if (!worldObj.isRemote) {
			AltitudeDisplay.setAltitudeString(String.format("%.1f", posY));
		}

		// drop the chute when close to ground
		if (ConfigHandler.isAutoDismount()) {
			if (riddenByEntity != null) {
				BlockPos bp = new BlockPos(riddenByEntity.posX, riddenByEntity.posY - 1.0, riddenByEntity.posZ);
				if (checkForGroundProximity(bp)) {
					Parachute.proxy.info(bp.toString());
					Parachute.proxy.info(this.toString());
					dropParachute(this);
					ParachuteCommonProxy.setDeployed(false);
					setDead();
					return;
				}
			}
		}

		// forward velocity for 'W' key press
		// moveForward happens when the 'W' key is pressed. Value is either 0.0 | ~0.98
		// when allowThermals is false forwardMovement is set to the constant 'forwardSpeed'
		// and applied to motionX and motionZ
		if (riddenByEntity != null && riddenByEntity instanceof EntityLivingBase) {
			EntityLivingBase rider = (EntityLivingBase) riddenByEntity;
			double forwardMovement = (allowThermals || lavaThermals) ? rider.moveForward : forwardSpeed;
			if (forwardMovement > 0.0) {
				double yaw = rider.rotationYaw + -rider.moveStrafing * 90.0;
				motionX += (-Math.sin((yaw * d2r)) * motionFactor * 0.05) * forwardMovement;
				motionZ += (Math.cos((yaw * d2r)) * motionFactor * 0.05) * forwardMovement;
			}
		}

		// forward velocity after forwardMovement is applied
		double adjustedVelocity = Math.sqrt(motionX * motionX + motionZ * motionZ);

		if (adjustedVelocity > 0.35D) {
			double motionAdj = 0.35D / adjustedVelocity;
			motionX *= motionAdj;
			motionZ *= motionAdj;
			adjustedVelocity = 0.35D;
		}

		if (adjustedVelocity > initialVelocity && motionFactor < 0.35D) {
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
			yaw = ((Math.atan2(delta_Z, delta_X) * r2d));
		}

		double adjustedYaw = MathHelper.wrapAngleTo180_double(yaw - rotationYaw);

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
		if (!worldObj.isRemote && riddenByEntity != null && riddenByEntity.isDead) {
			riddenByEntity = null;
			ParachuteCommonProxy.setDeployed(false);
			setDead();
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

	public boolean isLavaAt(BlockPos bp)
	{
		Block block = worldObj.getBlockState(bp).getBlock();
		return (block == Blocks.lava || block == Blocks.flowing_lava);
	}

	public boolean isLavaBelowInRange(BlockPos bp)
	{
		Vec3 v1 = new Vec3(posX, posY, posZ);
		Vec3 v2 = new Vec3(bp.getX(), bp.getY(), bp.getZ());
		MovingObjectPosition mop = worldObj.rayTraceBlocks(v1, v2, true);
		if (mop != null) {
			if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				BlockPos blockpos = mop.getBlockPos();
				if (isLavaAt(blockpos)) {
					return true;
				}
			}
		}
		return false;
	}

	public double doLavaThermals()
	{
		double thermals = drift;
		final double inc = 0.5;

		BlockPos blockPos = new BlockPos(posX, posY - Math.abs(getMountedYOffset()) - maxThermalRise, posZ);

		if (isLavaBelowInRange(blockPos)) {
			ridingThermals = true;
			curLavaDistance += inc;
			thermals = ascend;
			if (curLavaDistance >= maxThermalRise) {
				ridingThermals = false;
				curLavaDistance = lavaDistance;
				thermals = drift;
			}
		} else {
			ridingThermals = false;
			curLavaDistance = lavaDistance;
		}
		return thermals;
	}

//	protected boolean checkShouldDropChute(BlockPos bp)
//	{
//		if (!worldObj.isRemote && !isDead) {
//			if (checkForGroundProximity(bp)) {
//				return true;
//			}
//		}
//		return false;
//	}
	
	public boolean checkForGroundProximity(BlockPos bp)
	{
		boolean result = false;
		
		if (!worldObj.isRemote && !isDead) {
			Block block = worldObj.getBlockState(bp).getBlock();
			boolean isAir = (block == Blocks.air);
			boolean isVegetation = (block instanceof BlockFlower) && (block instanceof BlockGrass) && (block instanceof BlockLeaves);
			result = (!isAir && !isVegetation);
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
		double deltaPos = rmin + 0.9 * rand.nextDouble();

		if (deltaPos >= 0.20) {
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

	public void showContrails(double velocity)
	{
		double cosYaw = 2.0 * Math.cos(rotationYaw * d2r);
		double sinYaw = 2.0 * Math.sin(rotationYaw * d2r);

		for (int j = 0; (double) j < 1.0 + velocity * 8.0; ++j) {
			double s1 = (rand.nextDouble() * 2.0 - 1.0) * 0.2;
			double s2 = (double)(rand.nextInt(2) * 2 - 1) * 0.7;
			double particleX = prevPosX - cosYaw * s1 * 0.8 + sinYaw * s2;
			double particleZ = prevPosZ - sinYaw * s1 * 0.8 - cosYaw * s2;

			worldObj.spawnParticle(EnumParticleTypes.CLOUD, particleX, posY - 0.25, particleZ, motionX, motionY, motionZ, new int[0]);
		}
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {}
	
	@Override
	public String toString()
	{
		return String.format("%s: {x=%.1f, y=%.1f, z=%.1f}, {yaw=%.1f, pitch=%.1f}", getClass().getSimpleName(), posX, posY, posZ, rotationYaw, rotationPitch);
	}
	
}
