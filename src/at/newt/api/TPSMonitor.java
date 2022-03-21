package at.newt.api;

import org.bukkit.Bukkit;

import at.newt.main.Main;

public class TPSMonitor {
private static int tps = 0;
	
	public static void startTPSMonitor() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, new Runnable() {
			long sec;
			long currentSec;
			int ticks;
			int delay;
			
			@Override
			public void run() {
				sec = (System.currentTimeMillis() / 1000);
				if(currentSec == sec) {
					ticks++;
				}else {
					currentSec = sec;
					tps = (tps == 0 ? ticks : ((tps + ticks) / 2));
					ticks = 0;
					if((++delay % 300) == 0) {
						delay = 0;
					}
				}
			}
		}, 0, 1);
	}
	
	public static int getTPSasINT() {
		return tps;
	}
	
	public static String getTPSasString() {
		return String.valueOf(tps);
	}
	
	public static String getColorTPS() {
		String ret = "";
		if(tps <= 18) {
			ret = "§c" + tps;
		}else if(tps >= 18 && tps <= 19) {
			ret = "§e" + tps;
		}else if(tps >= 19 && tps <= 21) {
			ret = "§a" + tps;
		}else if(tps >= 21 && tps <= 22) {
			ret = "§e" + tps;
		}else {
			ret = "§c" + tps;
		}
		return ret;
	}
}