package at.newt.main;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;

import at.newt.api.APIs;
import at.newt.command.AFK_CMD;
import at.newt.command.BuildClass;
import at.newt.command.CMD_SetID_SetPf;
import at.newt.command.LogSystem;
import at.newt.command.MoneyAPI;
import at.newt.command.Pinfo;
import at.newt.event.JoinQuitEvents;
import at.newt.event.ScoreboardCLS;
import at.newt.event.ServerSigns;
import at.newt.mysql.lpb.MySQL;

public class Manager {
	
	public void init() {
		//commands
		Main.instance.getCommand("afk").setExecutor(new AFK_CMD());
		Main.instance.getCommand("build").setExecutor(new BuildClass());
		Main.instance.getCommand("setid").setExecutor(new CMD_SetID_SetPf());
		Main.instance.getCommand("setpf").setExecutor(new CMD_SetID_SetPf());
		Main.instance.getCommand("money").setExecutor(new MoneyAPI());
		Main.instance.getCommand("setmoney").setExecutor(new MoneyAPI());
		Main.instance.getCommand("removemoney").setExecutor(new MoneyAPI());
		Main.instance.getCommand("addmoney").setExecutor(new MoneyAPI());
		Main.instance.getCommand("topmoney").setExecutor(new MoneyAPI());
		Main.instance.getCommand("setbankmoney").setExecutor(new MoneyAPI());
		Main.instance.getCommand("bankdeposit").setExecutor(new MoneyAPI());
		Main.instance.getCommand("bankwithdraw").setExecutor(new MoneyAPI());
		Main.instance.getCommand("pay").setExecutor(new MoneyAPI());
		Main.instance.getCommand("pinfo").setExecutor(new Pinfo());
		Main.instance.getCommand("login").setExecutor(new LogSystem());
		Main.instance.getCommand("logout").setExecutor(new LogSystem());
		Main.instance.getCommand("togglegroup").setExecutor(new LogSystem());
		
		//eventhandlers
		PluginManager pl = Bukkit.getPluginManager();
		pl.registerEvents(new JoinQuitEvents(), Main.instance);
		pl.registerEvents(new ScoreboardCLS(), Main.instance);
		pl.registerEvents(new Serverupdater(), Main.instance);
		pl.registerEvents(new BuildClass(), Main.instance);
		pl.registerEvents(new AFK_CMD(), Main.instance);
		pl.registerEvents(new ServerSigns(Main.instance), Main.instance);
		
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
		
		ScoreboardCLS sb = new ScoreboardCLS();
		sb.SBSched(0, 20);
		APIs api = new APIs();
		api.loadConfig();
		api.onLoad();
		ServerSigns.startSignScheduler(0, 20);
	}
}