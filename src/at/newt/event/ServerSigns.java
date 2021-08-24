package at.newt.event;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
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

public class ServerSigns implements Listener{
	
	static int ladeInt = 0;
	
	private static Main plugin;
	public ServerSigns(Main m) {
		plugin = m;
	}
	
	public static void startSignScheduler(int delay, int period) {
		new BukkitRunnable() {
			@Override
			public void run() {
				ladeInt++;
				if(ladeInt == 3) {
					ladeInt = 0;
				}
				int players = Bukkit.getOnlinePlayers().size();
				if(players != 0) {
					try {
						PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM redicore_serversigns");
						ResultSet rs = ps.executeQuery();
						APIs api = new APIs();
						while(rs.next()) {
							String serverto = rs.getString("server_to");
							String world = rs.getString("world");
							int x = rs.getInt("x");
							int y = rs.getInt("y");
							int z = rs.getInt("z");
							Block matSign = Bukkit.getWorld(world).getBlockAt(x, y, z);
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
									s.setLine(2, "");
									s.setLine(3, "§coffline" + loadAnim(ladeInt));
								}
								s.update(true);
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
				Player p = e.getPlayer();
				Sign s = (Sign) e.getClickedBlock().getState();
				String serverto = s.getLine(1);
				String state = s.getLine(3);
				if(state.equalsIgnoreCase("§aonline")) {
					s.setLine(0, "");
					s.setLine(1, "§ateleporting");
					s.setLine(2, "§a...");
					s.setLine(3, "");
					s.update(true);
					switch(serverto) {
					case "lobby": sendPlayer(p, "lobby"); break;
					case "gameslobby": sendPlayer(p, "gameslobby"); break;
					case "creative": sendPlayer(p, "creative"); break;
					case "survival": sendPlayer(p, "survival"); break;
					case "skyblock": sendPlayer(p, "skyblock"); break;
					case "towny": sendPlayer(p, "towny"); break;
					case "farmserver": sendPlayer(p, "farmserver"); break;
					}
				}else {
					s.setLine(0, "");
					s.setLine(1, "§cServer is");
					s.setLine(2, "§cunreachable");
					s.setLine(3, "");
					s.update(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Block b = e.getBlock();
		if(b.getType() == Material.OAK_SIGN || b.getType() == Material.OAK_WALL_SIGN) {
			Location loc = b.getLocation();
			APIs api = new APIs();
			if(isServerSign(api.getServerName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName())) {
				e.setCancelled(true);
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
				if(e.getLine(1).equalsIgnoreCase("lobby")) {
					e.setLine(0, "§7ServerSigns");
					e.setLine(1, "§7by RediCraft");
					e.setLine(3, e.getLine(1));
					Location loc = e.getBlock().getLocation();
					addServerSign("Lobby", api.getServerName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
					p.sendMessage(api.prefix("main") +  "§7Created Serversign to §aLobby §7successfully.");
				}else if(e.getLine(1).equalsIgnoreCase("creative")) {
					e.setLine(0, "§7ServerSigns");
					e.setLine(1, "§7by RediCraft");
					e.setLine(3, e.getLine(1));
					Location loc = e.getBlock().getLocation();
					addServerSign("Creative", api.getServerName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
					p.sendMessage(api.prefix("main") +  "§7Created Serversign to §aCreative §7successfully.");
				}else if(e.getLine(1).equalsIgnoreCase("survival")) {
					e.setLine(0, "§7ServerSigns");
					e.setLine(1, "§7by RediCraft");
					e.setLine(3, e.getLine(1));
					Location loc = e.getBlock().getLocation();
					addServerSign("Survival", api.getServerName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
					p.sendMessage(api.prefix("main") +  "§7Created Serversign to §aSurvival §7successfully.");
				}else if(e.getLine(1).equalsIgnoreCase("skyblock")) {
					e.setLine(0, "§7ServerSigns");
					e.setLine(1, "§7by RediCraft");
					e.setLine(3, e.getLine(1));
					Location loc = e.getBlock().getLocation();
					addServerSign("SkyBlock", api.getServerName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
					p.sendMessage(api.prefix("main") +  "§7Created Serversign to §aSkyBlock §7successfully.");
				}else if(e.getLine(1).equalsIgnoreCase("towny")) {
					e.setLine(0, "§7ServerSigns");
					e.setLine(1, "§7by RediCraft");
					e.setLine(3, e.getLine(1));
					Location loc = e.getBlock().getLocation();
					addServerSign("Towny", api.getServerName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
					p.sendMessage(api.prefix("main") +  "§7Created Serversign to §aTowny §7successfully.");
				}else if(e.getLine(1).equalsIgnoreCase("farmserver")) {
					e.setLine(0, "§7ServerSigns");
					e.setLine(1, "§7by RediCraft");
					e.setLine(3, e.getLine(1));
					Location loc = e.getBlock().getLocation();
					addServerSign("Farmserver", api.getServerName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
					p.sendMessage(api.prefix("main") +  "§7Created Serversign to §aFarmserver §7successfully.");
				}else if(e.getLine(1).equalsIgnoreCase("gameslob")) {
					e.setLine(0, "§7ServerSigns");
					e.setLine(1, "§7by RediCraft");
					e.setLine(3, e.getLine(1));
					Location loc = e.getBlock().getLocation();
					addServerSign("Gameslobby", api.getServerName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
					p.sendMessage(api.prefix("main") +  "§7Created Serversign to §aGameslobby §7successfully.");
				}
			}
		}
	}
	
	private static void sendPlayer(Player p, String server) {
		APIs api = new APIs();
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			p.sendMessage(api.prefix("main") + api.returnStringReady(p, "event.navigator.sendPlayer.success").replace("%server", server));
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
		if(time == 0) {
			lade = ".";
		}else if(time == 1) {
			lade = "..";
		}else if(time == 2) {
			lade = "...";
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
	
	private void addServerSign(String server_to, String server_from, int x, int y, int z, String world) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO redicore_serversigns SET server_from = ?, server_to = ?, x = ?, y = ?, z = ?, world = ?");
			ps.setString(1, server_from);
			ps.setString(2, server_to);
			ps.setInt(3, x);
			ps.setInt(4, y);
			ps.setInt(5, z);
			ps.setString(6, world);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isServerSign(String server_from, int x, int y, int z, String world) {
		boolean boo = false;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM redicore_serversigns WHERE server_from = ? AND x = ? AND Y = ? AND z = ? AND world = ?");
			ps.setString(1, server_from);
			ps.setInt(2, x);
			ps.setInt(3, y);
			ps.setInt(4, z);
			ps.setString(5, world);
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
	
	@SuppressWarnings("unused")
	private void removeServerSign(String server_from, int x, int y, int z, String world) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("DELETE FROM redicore_serversigns WHERE server_from = ? AND x = ? AND y = ? AND z = ? AND world = ?");
			ps.setString(1, server_from);
			ps.setInt(2, x);
			ps.setInt(3, y);
			ps.setInt(4, z);
			ps.setString(5, world);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
