package at.newt.event;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import at.newt.api.APIs;
import at.newt.main.Main;
import at.newt.mysql.lb.MySQL;

public class ServerSigns implements Listener, CommandExecutor{
	
	static int ladeInt = 0;
	static HashMap<UUID, Boolean> delSign = new HashMap<>();
	
	private static Main plugin;
	public ServerSigns(Main m) {
		plugin = m;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			APIs api = new APIs();
			Player p = (Player) sender;
			if(p.hasPermission("mlps.deleteSign")) {
				delSign.put(p.getUniqueId(), true);
				api.sendMSGReady(p, "event.serverSign.deleteNow");
			}else {
				api.noPerm(p);
			}
		}
		return false;
	}
	
	public static void startSignScheduler(int delay, int period) {
		APIs api = new APIs();
		new BukkitRunnable() {
			@Override
			public void run() {
				ladeInt++;
				if(ladeInt == 25) {
					ladeInt = 0;
				}
				int players = Bukkit.getOnlinePlayers().size();
				if(players != 0) {
					try {
						PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM redicore_serversigns WHERE server_from = ?");
						ps.setString(1, api.getServerName());
						ResultSet rs = ps.executeQuery();
						APIs api = new APIs();
						while(rs.next()) {
							String serverto = rs.getString("server_to");
							String world = rs.getString("world");
							int x = rs.getInt("x");
							int y = rs.getInt("y");
							int z = rs.getInt("z");
							Block matSign = Bukkit.getWorld(world).getBlockAt(x, y, z);
							if(matSign != null) {
								if(matSign.getType() == Material.OAK_SIGN || matSign.getType() == Material.OAK_WALL_SIGN) {
									Sign s = (Sign) matSign.getState();
									if(getOnline(serverto)) {
										s.setLine(0, api.prefix("scoreboard"));
										s.setLine(1, "§7" + serverto);
										s.setLine(2, "§a" + getPlayers("currPlayers", serverto) + " §7/ §c" + getPlayers("maxPlayers", serverto) + " §7Players");
										s.setLine(3, "§aonline");
									}else {
										s.setLine(0, api.prefix("scoreboard"));
										s.setLine(1, "§7" + serverto);
										s.setLine(2, "§cpolling");
										s.setLine(3, "§c" + loadAnim(ladeInt));
									}
									s.update(true);
								}
							}
						}
						rs.close();
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}.runTaskTimer(Main.instance, delay, period);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Action a = e.getAction();
		if(e.getClickedBlock().getType() == Material.OAK_SIGN || e.getClickedBlock().getType() == Material.OAK_WALL_SIGN) {
			if(a == Action.LEFT_CLICK_BLOCK || a == Action.RIGHT_CLICK_BLOCK) {
				boolean breaker = false;
				Player p = e.getPlayer();
				if(delSign.containsKey(p.getUniqueId())) {
					breaker = delSign.get(p.getUniqueId());
				}
				if(!breaker) {
					Sign s = (Sign) e.getClickedBlock().getState();
					Location loc = e.getClickedBlock().getLocation();
					APIs api = new APIs();
					String serverto = getTargetServer(api.getServerName(), loc);
					String state = s.getLine(3);
					if(state.equalsIgnoreCase("§aonline")) {
						switch(serverto.toLowerCase()) {
						case "lobby": sendPlayer(p, serverto.toLowerCase(), s.getLine(1)); break;
						case "gameslobby": sendPlayer(p, serverto.toLowerCase(), s.getLine(1)); break;
						case "creative": sendPlayer(p, serverto.toLowerCase(), s.getLine(1)); break;
						case "survival": sendPlayer(p, serverto.toLowerCase(), s.getLine(1)); break;
						case "farmserver": sendPlayer(p, serverto.toLowerCase(), s.getLine(1)); break;
						case "skyblock": sendPlayer(p, serverto.toLowerCase(), s.getLine(1)); break;
						case "bw_1": sendPlayer(p, serverto.toLowerCase(), s.getLine(1)); break;
						case "bw_2": sendPlayer(p, serverto.toLowerCase(), s.getLine(1)); break;
						case "gg_1": sendPlayer(p, serverto.toLowerCase(), s.getLine(1)); break;
						case "gg_2": sendPlayer(p, serverto.toLowerCase(), s.getLine(1)); break;
						case "au_1": sendPlayer(p, serverto.toLowerCase(), s.getLine(1)); break;
						case "au_2": sendPlayer(p, serverto.toLowerCase(), s.getLine(1)); break;
						case "grubgame_1": sendPlayer(p, serverto.toLowerCase(), s.getLine(1)); break;
						case "grubgame_2": sendPlayer(p, serverto.toLowerCase(), s.getLine(1)); break;
						case "sgrlgl_1": sendPlayer(p, serverto.toLowerCase(), s.getLine(1)); break;
						case "sgrlgl_2": sendPlayer(p, serverto.toLowerCase(), s.getLine(1)); break;
						case "mb_1": sendPlayer(p, serverto.toLowerCase(), s.getLine(1)); break;
						case "mb_2": sendPlayer(p, serverto.toLowerCase(), s.getLine(1)); break;
						}
					}else {
						s.setLine(0, "");
						s.setLine(1, "§cServer");
						s.setLine(2, "§cOffline");
						s.setLine(3, "");
						s.update(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Block b = e.getBlock();
		Player p = e.getPlayer();
		if(b.getType() == Material.OAK_SIGN || b.getType() == Material.OAK_WALL_SIGN) {
			Location loc = b.getLocation();
			APIs api = new APIs();
			if(isServerSign(api.getServerName(), loc)) {
				if(delSign.containsKey(p.getUniqueId())) {
					if(delSign.get(p.getUniqueId())) {
						e.setCancelled(false);
						delSign.put(p.getUniqueId(), false);
						removeServerSign(api.getServerName(), loc);
						api.sendMSGReady(p, "event.serverSign.deletedSuccessfully");
					}else {
						e.setCancelled(true);
						api.sendMSGReady(p, "event.serverSign.deletedSuccessfully");
					}
				}
				e.setCancelled(true);
				api.sendMSGReady(p, "event.serverSign.deletedSuccessfully");
			}else {
				e.setCancelled(false);
			}
		}
	}
	
	@EventHandler
	public void onSign(SignChangeEvent e) {
		Player p = e.getPlayer();
		APIs api = new APIs();
		if(e.getLine(0).equalsIgnoreCase("[servsign]")) {
			if(p.hasPermission("mlps.serversign.add")) {
				e.setLine(0, "§7ServerSigns");
				e.setLine(1, "§7by RediCraft");
				Location loc = e.getBlock().getLocation();
				switch(e.getLine(1)) {
				case "lobby": e.setLine(3, e.getLine(1)); addServerSign("Lobby", api.getServerName(), loc, p, api); break; 
				case "creative": e.setLine(3, e.getLine(1)); addServerSign("Creative", api.getServerName(), loc, p, api); break;
				case "survival": e.setLine(3, e.getLine(1)); addServerSign("Survival", api.getServerName(), loc, p, api); break;
				case "skyblock": e.setLine(3, e.getLine(1)); addServerSign("SkyBlock", api.getServerName(), loc, p, api); break;
				case "farm": e.setLine(3, e.getLine(1)); addServerSign("Farmserver", api.getServerName(), loc, p, api); break;
				case "bw1": e.setLine(3, e.getLine(1)); addServerSign("BedWars 1", api.getServerName(), loc, p, api); break;
				case "bw2": e.setLine(3, e.getLine(1)); addServerSign("BedWars 2", api.getServerName(), loc, p, api); break;
				case "gg1": e.setLine(3, e.getLine(1)); addServerSign("GunGames 1", api.getServerName(), loc, p, api); break;
				case "gg2": e.setLine(3, e.getLine(1)); addServerSign("GunGames 2", api.getServerName(), loc, p, api); break;
				case "mb1": e.setLine(3, e.getLine(1)); addServerSign("MasterBuilders 1", api.getServerName(), loc, p, api); break;
				case "mb2": e.setLine(3, e.getLine(1)); addServerSign("MasterBuilders 2", api.getServerName(), loc, p, api); break;
				case "sg1": e.setLine(3, e.getLine(1)); addServerSign("SquidGame 1", api.getServerName(), loc, p, api); break;
				case "sg2": e.setLine(3, e.getLine(1)); addServerSign("SquidGame 2", api.getServerName(), loc, p, api); break;
				case "au1": e.setLine(3, e.getLine(1)); addServerSign("Among Us 1", api.getServerName(), loc, p, api); break;
				case "au2": e.setLine(3, e.getLine(1)); addServerSign("Among Us 2", api.getServerName(), loc, p, api); break;
				case "grub1": e.setLine(3, e.getLine(1)); addServerSign("GrubGame 1", api.getServerName(), loc, p, api); break;
				case "grub2": e.setLine(3, e.getLine(1)); addServerSign("GrubGame 2", api.getServerName(), loc, p, api); break;
				default: p.sendMessage(api.prefix("main") + "§7Definition for §c" + e.getLine(1) + " §7not found.");
				}
				
			}
		}
	}
	
	private static void sendPlayer(Player p, String server, String trueName) {
		APIs api = new APIs();
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			p.sendMessage(api.prefix("main") + api.returnStringReady(p, "event.navigator.sendPlayer.success").replace("%server", trueName));
			out.writeUTF("Connect");
			out.writeUTF(String.valueOf(server).toLowerCase());
			p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			api.sendMSGReady(p, "event.navigator.sendPlayer.failed");
		}
	}
	
	private static String loadAnim(int time) {
		String lade = "";
		switch(time) {
		case 0: lade = "               "; break;
		case 1: lade = "              l"; break;
		case 2: lade = "             lo"; break;
		case 3: lade = "            loa"; break;
		case 4: lade = "           load"; break;
		case 5: lade = "          loadi"; break;
		case 6: lade = "         loadin"; break;
		case 7: lade = "        loading"; break;
		case 8: lade = "       loading."; break;
		case 9: lade = "      loading.."; break;
		case 10: lade = "     loading..."; break;
		case 11: lade = "    loading... "; break;
		case 12: lade = "   loading...  "; break;
		case 13: lade = "  loading...   "; break;
		case 14: lade = " loading...    "; break;
		case 15: lade = "loading...     "; break;
		case 16: lade = "oading...      "; break;
		case 17: lade = "ading...       "; break;
		case 18: lade = "ding...        "; break;
		case 19: lade = "ing...         "; break;
		case 20: lade = "ng...          "; break;
		case 21: lade = "g...           "; break;
		case 22: lade = "...            "; break;
		case 23: lade = "..             "; break;
		case 24: lade = ".             "; break;
		}
		return lade;
	}
	
	private static int getPlayers(String col, String server) {
		int i = 0;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM redicore_serverstats WHERE servername = ?");
			ps.setString(1, server);
			ResultSet rs = ps.executeQuery();
			rs.next();
			i = rs.getInt(col);
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
			i = -1;
		}
		return i;
	}
	
	private static boolean getOnline(String server) {
		boolean state = false;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM redicore_serverstats WHERE servername = ?");
			ps.setString(1, server);
			ResultSet rs = ps.executeQuery();
			rs.next();
			state = rs.getBoolean("online");
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return state;
	}
	
	private void addServerSign(String server_to, String server_from, Location loc, Player p, APIs api) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO redicore_serversigns SET server_from = ?, server_to = ?, x = ?, y = ?, z = ?, world = ?, server_to_bc = ?");
			ps.setString(1, server_from);
			ps.setString(2, server_to);
			ps.setInt(3, loc.getBlockX());
			ps.setInt(4, loc.getBlockY());
			ps.setInt(5, loc.getBlockZ());
			ps.setString(6, loc.getWorld().getName());
			ps.setString(7, translateClearnameToBC(server_to));
			ps.executeUpdate();
			ps.close();
			p.sendMessage(api.prefix("main") +  api.returnStringReady(p, "event.serverSwitchSign.create").replace("%server", server_to));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String translateClearnameToBC(String server_to) {
		String name = "";
		switch(server_to) {
		case "Lobby": name = "lobby"; break; //Server where you spawn on.
		case "Creative": name = "creative"; break; //Build your things in creative, however just on a plotworld, since the freebuild world was badly destroyed.
		case "Survival": name = "survival"; break; //Build your things in survival. Either random generated world or on a plot world to be safe.
		case "Farmserver": name = "farmserver"; break; //Farm your materials what you need. The server's worlds will get reset randomly.
		case "Gameslobby": name = "gameslobby"; break; //sublobby for all the games.
		case "BedWars 1": name = "bw_1"; break; //Play against others and see that your bed wont get destroyed. The last one standing wins.
		case "BedWars 2": name = "bw_2"; break;
		case "GunGames 1": name = "gg_1"; break; //Infinity game - you join with a simple wooden sword and the more people you kill the better equipment you will get. Be aware, when you die, you will loose your progress!
		case "GunGames 2": name = "gg_2"; break;
		case "MasterBuilders 1": name = "mb_1"; break; //Build something against the time, the best will win a prize!
		case "MasterBuilders 2": name = "mb_2"; break;
		case "SkyBlock": name = "skyblock"; break;
		case "SquidGame 1": name = "sgrlgl_1"; break; //We define Squid Game as the game Red Light, Green Light. You may walk when it's green, you may die when moving when red.
		case "SquidGame 2": name = "sgrlgl_2"; break;
		case "Among Us 1": name = "au_1"; break; //Play on 3 maps (which will be chosen), choose your color and find with the crewmates the impostor and save the ship.
		case "Among Us 2": name = "au_2"; break;
		case "GrubGame 1": name = "grubgame_1"; break; //GrubGame is just a placeholder for a game of Grubsic himself. Thus the name. Subject of change.
		case "GrubGame 2": name = "grubgame_2"; break;
		}
		return name;
	}
	
	private boolean isServerSign(String server_from, Location loc) {
		boolean boo = false;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM redicore_serversigns WHERE server_from = ? AND x = ? AND Y = ? AND z = ? AND world = ?");
			ps.setString(1, server_from);
			ps.setInt(2, loc.getBlockX());
			ps.setInt(3, loc.getBlockY());
			ps.setInt(4, loc.getBlockZ());
			ps.setString(5, loc.getWorld().getName());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				boo = true;
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return boo;
	}
	
	private void removeServerSign(String server_from, Location loc) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("DELETE FROM redicore_serversigns WHERE server_from = ? AND x = ? AND y = ? AND z = ? AND world = ?");
			ps.setString(1, server_from);
			ps.setInt(2, loc.getBlockX());
			ps.setInt(3, loc.getBlockY());
			ps.setInt(4, loc.getBlockZ());
			ps.setString(5, loc.getWorld().getName());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String getTargetServer(String server_from, Location loc) {
		String target = "";
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT server_to_bc FROM redicore_serversigns WHERE server_from = ? AND world = ? AND x = ? AND y = ? AND z = ?");
			ps.setString(1, server_from);
			ps.setString(2, loc.getWorld().getName());
			ps.setInt(3, loc.getBlockX());
			ps.setInt(4, loc.getBlockY());
			ps.setInt(5, loc.getBlockZ());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				target = rs.getString("server_to_bc");
			}else {
				target = "none";
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return target;
	}
}