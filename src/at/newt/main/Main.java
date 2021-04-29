package at.newt.main;

import org.bukkit.plugin.java.JavaPlugin;

import at.newt.mysql.lpb.MySQL;

public class Main extends JavaPlugin{
	
	/*
	 * Welcome to the Gameslobby Serversystem
	 * This system is like the Lobbysystem, just without the Core-System but most same functions
	 * I wanted to keep it as two different projects, so nothing bad can happen.
	 */
	
	public static Main instance;
	public static String mysqlprefix = "§eMySQL §7- ";
	public static MySQL mysql;
	
	public void onEnable() {
		instance = this;
		Manager manager = new Manager();
		manager.init();
	}
	
	public void onDisable() {
		instance = null;
	}
}