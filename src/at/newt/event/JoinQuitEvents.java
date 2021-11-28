package at.newt.event;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import at.newt.api.APIs;
import at.newt.main.Main;
import at.newt.mysql.lb.MySQL;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class JoinQuitEvents implements Listener{
	
	static File spawnfile = new File("plugins/RCGLSS/spawn.yml");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onLogin(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		APIs api = new APIs();
		String level = getEntryLevel(api.getServerName());
		if(level.equalsIgnoreCase("All")) {
			e.allow();
		}else if(level.equalsIgnoreCase("Staff")) {
			if(p.hasPermission("mlps.isStaff")) {
				e.allow();
			}else {
				e.disallow(Result.KICK_OTHER, "§aRedi§cCraft\n \n§7This Server is just for Staff members allowed.");
			}
		}else if(level.equalsIgnoreCase("Alpha")) {
			if(p.hasPermission("mlps.isAlphatester")) {
				e.allow();
			}else {
				e.disallow(Result.KICK_OTHER, "§aRedi§cCraft\n \n§7This Server is just for Alpha-Testers free.");
			}
		}else if(level.equalsIgnoreCase("Beta")) {
			if(p.hasPermission("mlps.isBetatester")) {
				e.allow();
			}else {
				e.disallow(Result.KICK_OTHER, "§aRedi§cCraft\n \n§7This Server is just for Beta-Testers free.");
			}
		}else {
			Bukkit.getConsoleSender().sendMessage("Level: " + level);
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		APIs api = new APIs();
		e.setJoinMessage(null);
		Player p = e.getPlayer();
		SimpleDateFormat time = new SimpleDateFormat("dd/MM/yy - HH:mm:ss");
        String stime = time.format(new Date());
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String uuid = p.getUniqueId().toString().replace("-", "");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(spawnfile);
        try {
			PermissionUser po = PermissionsEx.getUser(p);
    		PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE redicore_userstats SET userrank = ?, rankcolor = ?, online = ?, server = ?, lastjoints = ?, lastjoinstring = ?, lastloginip = ?, isstaff = ?, username = ? WHERE uuid = ?");
    		if(po.inGroup("pman")) {
    			ps.setString(1, "Project Manager");
    			ps.setString(2, "#5555ff");
			}else if(po.inGroup("cman")) {
				ps.setString(1, "Community Manager");
    			ps.setString(2, "#00aa00");
			}else if(po.inGroup("gmmman")) {
				ps.setString(1, "Game Mod. Manager");
    			ps.setString(2, "#aa0000");
			}else if(po.inGroup("dev")) {
				ps.setString(1, "Developer");
    			ps.setString(2, "#aa00aa");
			}else if(po.inGroup("hr")) {
				ps.setString(1, "Human Resources");
    			ps.setString(2, "#ffaa00");
			}else if(po.inGroup("cm")) {
				ps.setString(1, "Community Moderator");
    			ps.setString(2, "#55ff55");
			}else if(po.inGroup("ct")) {
				ps.setString(1, "Content Team");
    			ps.setString(2, "#0000aa");
			}else if(po.inGroup("st")) {
				ps.setString(1, "Support Team");
    			ps.setString(2, "#ffff55");
			}else if(po.inGroup("bd")) {
				ps.setString(1, "Builder");
    			ps.setString(2, "#55ffff");
			}else if(po.inGroup("gm")) {
				ps.setString(1, "Game Moderator");
    			ps.setString(2, "#ff5555");
			}else if(po.inGroup("aot")) {
				ps.setString(1, "Add-On Team");
    			ps.setString(2, "#ff55ff");
			}else if(po.inGroup("train")) {
				ps.setString(1, "Trainee");
    			ps.setString(2, "#55ffff");
			}else if(po.inGroup("rltm")) {
				ps.setString(1, "Retired Legend");
    			ps.setString(2, "#00aaaa");
			}else if(po.inGroup("rtm")) {
				ps.setString(1, "Retired Team Member");
    			ps.setString(2, "#00aaaa");
			}else if(po.inGroup("part")) {
				ps.setString(1, "Partner");
    			ps.setString(2, "#00aa00");
			}else if(po.inGroup("fs")) {
				ps.setString(1, "Forum Supporter");
    			ps.setString(2, "#ff55ff");
			}else if(po.inGroup("nb")) {
				ps.setString(1, "Nitro Booster");
    			ps.setString(2, "#ff55ff");
			}else if(po.inGroup("bt")) {
				ps.setString(1, "Beta Tester");
    			ps.setString(2, "#ff55ff");
			}else if(po.inGroup("friend")) {
				ps.setString(1, "Friend");
    			ps.setString(2, "#aaaaaa");
			}else if(po.inGroup("default")) {
				ps.setString(1, "Player");
    			ps.setString(2, "#ffffff");
			}else {
				ps.setString(1, "unknown Role");
				ps.setString(1, "#7c4dff");
			}
    		ps.setBoolean(3, true);
    		ps.setString(4, api.getServerName());
    		ps.setLong(5, ts.getTime());
    		ps.setString(6, stime);
    		ps.setString(7, p.getAddress().getHostString());
    		if(p.hasPermission("mlps.isStaff")) {
    			ps.setBoolean(8, true);
    		}else {
    			ps.setBoolean(8, false);
    		}
    		ps.setString(9, p.getName());
    		ps.setString(10, uuid);
    		ps.executeUpdate();
    		ps.close();
		}catch (SQLException ex) {
			ex.printStackTrace();
		}
        if(!p.hasPlayedBefore()) {
        	Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
				@Override
				public void run() {
					p.teleport(retLoc(cfg, "spawn"));
				}
			}, 20);
		}
        int notify_pm = 0;
        int notify_tpar = 0;
        try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT disablePMs,disableTPAR FROM redicore_userstats WHERE uuid = ?");
			ps.setString(1, p.getUniqueId().toString().replace("-", ""));
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				if(rs.getBoolean("disablePMs")) {
					notify_pm = 1;
				}else {
					notify_pm = 0;
				}
				if(rs.getBoolean("disableTPAR")) {
					notify_tpar = 1;
				}else {
					notify_tpar = 0;
				}
			}else {
				notify_pm = -1;
				notify_tpar = -1;
			}
        } catch (SQLException e1) {
			e1.printStackTrace();
		}
        if(notify_pm == 1) {
        	p.sendMessage(api.prefix("system") + api.returnStringReady(p, "event.join.blockpmnotify"));
        }
        if(notify_tpar == 1) {
        	p.sendMessage(api.prefix("system") + api.returnStringReady(p, "event.join.tparnotify"));
        }
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		e.setQuitMessage(null);
		Player p = e.getPlayer();
		String uuid = p.getUniqueId().toString().replace("-", "");
		SimpleDateFormat time = new SimpleDateFormat("dd/MM/yy - HH:mm:ss");
        String stime = time.format(new Date());
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        PermissionUser po = PermissionsEx.getUser(p);
        try {
        	PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE redicore_userstats SET userrank = ?, rankcolor = ?, lastjoints = ?, lastjoinstring = ?, lastloginip = ?, online = ? WHERE uuid = ?");
        	if(po.inGroup("pman")) {
    			ps.setString(1, "Project Manager");
    			ps.setString(2, "#5555ff");
			}else if(po.inGroup("cman")) {
				ps.setString(1, "Community Manager");
    			ps.setString(2, "#00aa00");
			}else if(po.inGroup("gmmman")) {
				ps.setString(1, "Game Mod. Manager");
    			ps.setString(2, "#aa0000");
			}else if(po.inGroup("dev")) {
				ps.setString(1, "Developer");
    			ps.setString(2, "#aa00aa");
			}else if(po.inGroup("hr")) {
				ps.setString(1, "Human Resources");
    			ps.setString(2, "#ffaa00");
			}else if(po.inGroup("cm")) {
				ps.setString(1, "Community Moderator");
    			ps.setString(2, "#55ff55");
			}else if(po.inGroup("ct")) {
				ps.setString(1, "Content Team");
    			ps.setString(2, "#0000aa");
			}else if(po.inGroup("st")) {
				ps.setString(1, "Support Team");
    			ps.setString(2, "#ffff55");
			}else if(po.inGroup("bd")) {
				ps.setString(1, "Builder");
    			ps.setString(2, "#55ffff");
			}else if(po.inGroup("gm")) {
				ps.setString(1, "Game Moderator");
    			ps.setString(2, "#ff5555");
			}else if(po.inGroup("aot")) {
				ps.setString(1, "Add-On Team");
    			ps.setString(2, "#ff55ff");
			}else if(po.inGroup("train")) {
				ps.setString(1, "Trainee");
    			ps.setString(2, "#55ffff");
			}else if(po.inGroup("rltm")) {
				ps.setString(1, "Retired Legend");
    			ps.setString(2, "#00aaaa");
			}else if(po.inGroup("rtm")) {
				ps.setString(1, "Retired Team Member");
    			ps.setString(2, "#00aaaa");
			}else if(po.inGroup("part")) {
				ps.setString(1, "Partner");
    			ps.setString(2, "#00aa00");
			}else if(po.inGroup("fs")) {
				ps.setString(1, "Forum Supporter");
    			ps.setString(2, "#ff55ff");
			}else if(po.inGroup("nb")) {
				ps.setString(1, "Nitro Booster");
    			ps.setString(2, "#ff55ff");
			}else if(po.inGroup("bt")) {
				ps.setString(1, "Beta Tester");
    			ps.setString(2, "#ff55ff");
			}else if(po.inGroup("friend")) {
				ps.setString(1, "Friend");
    			ps.setString(2, "#aaaaaa");
			}else if(po.inGroup("default")) {
				ps.setString(1, "Player");
    			ps.setString(2, "#ffffff");
			}else {
				ps.setString(1, "unknown Role");
				ps.setString(1, "#7c4dff");
			}
        	ps.setLong(3, ts.getTime());
        	ps.setString(4, stime);
        	ps.setString(5, p.getAddress().getHostString());
        	ps.setBoolean(6, false);
        	ps.setString(7, uuid);
        	ps.executeUpdate();
        	ps.close();
        }catch (SQLException ex) { ex.printStackTrace(); }
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		e.setDeathMessage(null);
	}
	
	private Location retLoc(YamlConfiguration cfg, String type) {
		Location loc = null;
		if(cfg.contains("Spawn." + type + ".WORLD")) {
			loc = new Location(Bukkit.getWorld(cfg.getString("Spawn." + type + ".WORLD")), cfg.getDouble("Spawn." + type + ".X"), cfg.getDouble("Spawn." + type + ".Y"), cfg.getDouble("Spawn." + type + ".Z"), (float)cfg.getDouble("Spawn." + type + ".YAW"), (float)cfg.getDouble("Spawn." + type + ".PITCH"));
		}else {
			loc = null;
		}
		return loc;
	}
	
	public String getEntryLevel(String server) {
		String level = "";
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT entrylevel FROM redicore_serverstats WHERE servername = ?");
			ps.setString(1, server);
			ResultSet rs = ps.executeQuery();
			rs.next();
			level = rs.getString("entrylevel");
			rs.close();
			ps.closeOnCompletion();
		} catch (SQLException e) {
			level = "NONE";
			e.printStackTrace();
		}
		Bukkit.getConsoleSender().sendMessage("OUTPUT #315 " + level);
		return level;
	}
}