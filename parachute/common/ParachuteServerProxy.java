package com.parachute.common;

import static com.parachute.common.Parachute.modid;

public class ParachuteServerProxy extends ParachuteCommonProxy {
	@Override
	public void preInit()
	{
		super.preInit();
		info(modid + " DedicatedServer preInit is complete");
	}
	
	@Override
	public void Init()
	{
		super.Init();
		info(modid + " DedicatedServer Init is complete");
	}
	
	@Override
	public void postInit()
	{
		info(modid + " DedicatedServer postInit is complete.");
	}
}
