package parachute.common;

import net.minecraft.src.INetworkManager;

public class PlayerInfo
{
	public String Name;
	public int mode; // 0 = drift, 1 = ascend, 2 = descend
    public INetworkManager networkManager;
//    public boolean autoDeploy;
    
    public PlayerInfo(String name, INetworkManager nm) {
        Name = name;
        networkManager = nm;
//        autoDeploy = false;
    }
    
    public void setLiftMode(int m) {
    	mode = m;
    }
    
//    public void toggleAutoDeploy() {
//    	autoDeploy = !autoDeploy;
//    }
    
}
