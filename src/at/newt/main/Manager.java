package at.newt.main;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;

import at.newt.mysql.lpb.MySQL;

public class Manager {
	
	public void init() {
		//commands
		
		//eventhandlers
		PluginManager pl = Bukkit.getPluginManager();
		
		//configs
		File config = new File("plugins/RCGLSS/config.yml");
		File file = new File("plugins/RCGLSS");
		if(!file.exists()) {
			file.mkdir();
			if(!config.exists()) {
				try {
					config.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else {
			if(!config.exists()) {
				try {
					config.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(config);
		cfg.addDefault("MySQL.Host", "localhost");
		cfg.addDefault("MySQL.Port", 3306);
		cfg.addDefault("MySQL.Database", "database");
		cfg.addDefault("MySQL.Username", "username");
		cfg.addDefault("MySQL.Password", "password");
		cfg.options().copyDefaults(true);
		try {
			cfg.save(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String host = cfg.getString("MySQL.Host");
		int port = cfg.getInt("MySQL.Port");
		String db = cfg.getString("MySQL.Database");
		String user = cfg.getString("MySQL.Username");
		String pass = cfg.getString("MySQL.Password");
		at.newt.mysql.lb.MySQL.connect(host, String.valueOf(port), db, user, pass);
		Main.mysql = new MySQL(host, port, db, user, pass);
		try {
			Main.mysql.connect();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}