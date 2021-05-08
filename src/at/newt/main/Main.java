package at.newt.main;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import at.newt.api.APIs;
import at.newt.mysql.lpb.MySQL;

public class Main extends JavaPlugin{
	
	static /*
	 * Welcome to the Gameslobby Serversystem
	 * This system is like the Lobbysystem, just without the Core-System but most same functions
	 * I wanted to keep it as two different projects, so nothing bad can happen.
	 */
	APIs api = new APIs();
	public static Main instance;
	public static String mysqlprefix = "§eMySQL §7- ";
	public static MySQL mysql;
	public static String consolesend = api.prefix("main") + "§7Please use this ingame.";
	
	public void onEnable() {
		instance = this;
		Manager manager = new Manager();
		manager.init();
	}
	
	public void onDisable() {
		instance = null;
	}
	
	public static void setPlayerBar(Player p) {
		APIs api = new APIs();
		p.getInventory().clear();
		p.getInventory().setItem(0, api.defItem(Material.COMPASS, 1, ""));
	}
}