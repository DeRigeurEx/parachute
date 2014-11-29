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

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//import net.minecraftforge.common.util.ForgeDirection;

public class EntityParachute extends Entity {

//	@SideOnly(Side.CLIENT)
	private double velocityX;
//	@SideOnly(Side.CLIENT)
	private double velocityY;
//	@SideOnly(Side.CLIENT)
	private double velocityZ;
	private double motionFactor;
	private double maxAltitude;
	private boolean allowThermals;
	private boolean smallCanopy;

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

		smallCanopy = Parachute.instance.isSmallCanopy();

		preventEntitySpawning = true;
		setSize(2.0F, 1.0F);
//		yOffset = height / 2F;
		motionFactor = 0.07D;
		ascendMode = false;

		allowThermals = Parachute.instance.getAllowThermals();
		maxAltitude = Parachute.instance.getMaxAltitude();
	}

	public EntityParachute(World world, double x, double y, double z)
	{
		this(world);

		setPosition(x, y/* + (double) yOffset*/, z);

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

//	@Override
//	public AxisAlignedBB getCollisionBox(Entity entity)
//	{
//		if (entity != riddenByEntity && entity.ridingEntity != this) {
//			return entity.boundingBox;
//		}
//		return null;
//	}

//	@Override
//	public boolean shouldRiderSit()
//	{
//		return isNearGround(posX, posX, posX, Math.abs(getMountedYOffset() + 1.0));
//	}

//	@Override
//	public AxisAlignedBB getBoundingBox()
//	{
//		return boundingBox;
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

//	@SideOnly(Side.CLIENT)
//	@Override
//	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int inc)
//	{
//		double deltaX = x - posX;
//		double deltaY = y - posY;
//		double deltaZ = z - posZ;
//		double magnitude = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
//
//		if (magnitude <= 1.0D) {
//			return;
//		}
//
//		// forward/vertical motion
//		motionX = velocityX;
//		motionY = velocityY;
//		motionZ = velocityZ;
//	}

//	@SideOnly(Side.CLIENT)
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
				Parachute.proxy.print("EntityParachute: 190, riddenByEntity == null");
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
			if (checkShouldDropChute(posX, posY, posZ, offset + 1.0)) {
				Parachute.proxy.print("EntityParachute: 214, checkShouldDropChute() returned true");
				return;
			}
		}

		// forward velocity for 'W' keypress
		// moveForward happens when the 'W' key is pressed. Value is either 0.0 | ~0.98
		// when allowThermals is false forwardMovement is set to the constant 'forwardSpeed'
		// and appied to motionX and motionZ
		if (riddenByEntity != null && riddenByEntity instanceof EntityLivingBase) {
			EntityLivingBase rider = (EntityLivingBase) riddenByEntity;
			double forwardMovement = allowThermals ? (double) rider.moveForward : forwardSpeed;
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

		if (!worldObj.isRemote) {
			List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.2D, 0.0D, 0.2D));
			if (list != null && list.isEmpty()) {
				for (Object list1 : list) {
					Entity entity = (Entity) list1;
					if (entity != riddenByEntity && entity.canBePushed() && (entity instanceof EntityParachute)) {
						entity.applyEntityCollision(this);
					}
				}
			}
		}
		// something bad happened, somehow the skydiver was killed.
		if (riddenByEntity != null && riddenByEntity.isDead) {
			riddenByEntity = null;
			if (!worldObj.isRemote) {
				Parachute.proxy.print("EntityParachute: 299, riddenByEntity == null && riddenByEntity.isDead");
				destroyParachute();
			}
		}
//		ascendMode = false;
	}

	public double currentDescentRate()
	{
		double descentRate = drift; // defaults to drift

		if (!allowThermals) {
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

	protected boolean checkShouldDropChute(double x, double y, double z, double distance)
	{
		boolean shouldDrop = false;

		if (isNearGround(x, y, z, distance)) {
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

	public boolean isNearGround(double posx, double posy, double posz, double distance)
	{
		boolean result = false;
		int x = MathHelper.floor_double(posx);
		int y = MathHelper.floor_double(posy - distance);
		int z = MathHelper.floor_double(posz);
		BlockPos blockPos = new BlockPos(x, y, z);
		
		if (!worldObj.isAirBlock(blockPos)) {
			return true;
		}
		return result;
	}

	public void dropParachute(Entity parachute)
	{
		if (parachute == null) {
			if (ridingEntity != null) {
				setLocationAndAngles(ridingEntity.posX, ridingEntity.getBoundingBox().minY + (double) ridingEntity.height, ridingEntity.posZ, rotationYaw, rotationPitch);
				ridingEntity.riddenByEntity = null;
			}
			ridingEntity = null;
		} else {
			if (ridingEntity != null) {
				ridingEntity.riddenByEntity = null;
			}
			ridingEntity = parachute;
			parachute.riddenByEntity = this;
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

//	@SideOnly(Side.CLIENT)
//	@Override
//	public float getShadowSize()
//	{
//		return 0.0F;
//	}

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
