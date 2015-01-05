package com.parachute.common;

import static com.parachute.common.Parachute.modid;

public class ServerProxyParachute extends CommonProxyParachute {
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
		info(modid + " DedicatedServer initialization is complete.");
	}
}
