package at.newt.main;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import at.newt.api.APIs;
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
	public static String consolesend = "§7Please use this ingame.";
	
	public void onEnable() {
		instance = this;
		Bukkit.getMessenger().registerOutgoingPluginChannel(instance, "BungeeCord");
		Manager manager = new Manager();
		manager.init();
		updateOnline(true);
	}
	
	public void onDisable() {
		instance = null;
		updateOnline(false);
		try {
			mysql.disconnect();
			at.newt.mysql.lb.MySQL.disconnect();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void setPlayerBar(Player p) {
		APIs api = new APIs();
		p.getInventory().clear();
		p.getInventory().setItem(2, api.defItem(Material.COMPASS, 1, ""));
		p.getInventory().setItem(4, api.defItem(Material.COMPASS, 1, ""));
		p.getInventory().setItem(6, api.defItem(Material.COMPASS, 1, ""));
	}
	
	private void updateOnline(boolean boo) {
		APIs api = new APIs();
		try {
			PreparedStatement ps = at.newt.mysql.lb.MySQL.getConnection().prepareStatement("UPDATE redicore_serverstats SET online = ? WHERE servername = ?");
			ps.setBoolean(1, boo);
			ps.setString(2, api.getServerName());
			ps.executeUpdate();
			ps.close();
			if(boo == true) {
				PreparedStatement ps1 = at.newt.mysql.lb.MySQL.getConnection().prepareStatement("UPDATE redicore_serverstats SET onlinesince = ? WHERE servername = ?");
				SimpleDateFormat time = new SimpleDateFormat("dd/MM/yy - HH:mm:ss");
				ps1.setString(1, time.format(new Date()));
				ps1.setString(2, api.getServerName());
				ps1.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}