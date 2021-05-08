package at.newt.event;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import at.newt.api.APIs;
import at.newt.command.BuildClass;
import at.newt.command.MoneyAPI;
import at.newt.main.Main;
import at.newt.main.Serverupdater;
import at.newt.mysql.lb.MySQL;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ScoreboardCLS implements Listener{
	
	public static HashMap<String, Long> buildtime = new HashMap<>();
	
	@SuppressWarnings("deprecation")
	public void setScoreboard(Player p) throws SQLException {
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective o = sb.registerNewObjective("aaa", "dummy");
		PermissionUser po = PermissionsEx.getUser(p);
		APIs api = new APIs();
		int puser = api.getPlayers("BungeeCord", "currPlayers");
		int pusermax = api.getPlayers("BungeeCord", "maxPlayers");
		int puserperc = (puser*100/pusermax);
		String sbpr = api.prefix("scoreboard");
		
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		if(puserperc <= 20) {
			o.setDisplayName(sbpr + " » §a" + puser + " §7/§9 " + pusermax);
		}else if(puserperc >= 21 && puserperc <= 40) {
			o.setDisplayName(sbpr + " » §2" + puser + " §7/§9 " + pusermax);
		}else if(puserperc >= 41 && puserperc <= 60) {
			o.setDisplayName(sbpr + " » §e" + puser + " §7/§9 " + pusermax);
		}else if(puserperc >= 61 && puserperc <= 80) {
			o.setDisplayName(sbpr + " » §6" + puser + " §7/§9 " + pusermax);
		}else if(puserperc >= 81 && puserperc <= 95) {
			o.setDisplayName(sbpr + " » §c" + puser + " §7/§9 " + pusermax);
		}else if(puserperc >= 96) {
			o.setDisplayName(sbpr + " » §4" + puser + " §7/§9 " + pusermax);
		}
		if(BuildClass.users.contains(p.getUniqueId())) {
			ItemStack is = p.getItemInHand();
			String itemid = is.getType().toString();
			o.getScore("§7Buildtime:").setScore(4);
			o.getScore("§a" + getBuildTime(p.getName())).setScore(3);
			o.getScore("§0").setScore(2);
			o.getScore(api.returnStringReady(p, "scoreboard.sideboard.block")).setScore(1);
			o.getScore("  §a" + itemid).setScore(0);
		}else {
			o.getScore(api.returnStringReady(p, "scoreboard.sideboard.money")).setScore(8);
			o.getScore("  §a" + MoneyAPI.getBankMoney(p.getUniqueId()) + " §7Bank").setScore(7);
			o.getScore("  §a" + MoneyAPI.getMoney(p.getUniqueId()) + " §7Cash").setScore(6);
			o.getScore("§f").setScore(5);
			o.getScore(api.returnStringReady(p, "scoreboard.sideboard.rank")).setScore(4);
			if (po.inGroup("Developer")) {
			    o.getScore("  §dDeveloper").setScore(3);
			}else if(po.inGroup("HumanR")) {
				o.getScore("  §5Human Resources").setScore(3);
			}else if (po.inGroup("PMan")) {
			    o.getScore("  §6Project Manager").setScore(3);
			}else if (po.inGroup("CMan")) {
			    o.getScore("  §2Community Manager").setScore(3);
			}else if (po.inGroup("AMan")) {
			    o.getScore("  §4Game Moderation Manager").setScore(3);
			}else if (po.inGroup("Admin")) {
			    o.getScore("  §cAdministrator").setScore(3);
			}else if (po.inGroup("Support")) {
			    o.getScore("  §9Support").setScore(3);
			}else if(po.inGroup("Translator")) {
				o.getScore("  §eTranslator").setScore(3);
			}else if (po.inGroup("Mod")) {
			    o.getScore("  §aModerator").setScore(3);
			}else if (po.inGroup("Builder")) {
			    o.getScore("  §bBuilder").setScore(3);
			}else if (po.inGroup("RLTM")) {
			    o.getScore("  §3Retired Legend Team Member").setScore(3);
			}else if (po.inGroup("RTM")) {
			    o.getScore("  §3Retired Team Member").setScore(3);
			}else if(po.inGroup("Partner")) {
			    o.getScore("  §aPartner").setScore(3);
			}else if (po.inGroup("Beta")) {
			    o.getScore("  §5Beta-Tester").setScore(3);
			}else if(po.inGroup("Patron")) {
			    o.getScore("  §ePatron").setScore(3);
			}else if(po.inGroup("NitroBooster")) {
			    o.getScore("  §5Nitro Booster").setScore(3);
			}else if (po.inGroup("Friend")) {
			    o.getScore("  §3Friend").setScore(3);
			}else {
				o.getScore("  §7Player").setScore(3);
			}
			o.getScore("§9").setScore(2);
			o.getScore(api.returnStringReady(p, "scoreboard.sideboard.playerid")).setScore(1);
			o.getScore("  §7" + igid(p) + " §f" + igpre(p)).setScore(0);
		}
		p.setScoreboard(sb);
		
		Team pm = getTeam(sb, "00000", retPrefix("pm", "prefix_tab"), ChatColor.GRAY); //gold
		Team hr = getTeam(sb, "00005", retPrefix("hr", "Prefix_tab"), ChatColor.GRAY);
		Team cman = getTeam(sb, "00010", retPrefix("cman", "prefix_tab"), ChatColor.GRAY); //dark-green
		Team aman = getTeam(sb, "00020", retPrefix("aman", "prefix_tab"), ChatColor.GRAY); //dark-red
		Team dev = getTeam(sb, "00030", retPrefix("dev", "prefix_tab"), ChatColor.GRAY); //light-purple
		Team admin = getTeam(sb, "00040", retPrefix("admin", "prefix_tab"), ChatColor.GRAY); //red
		Team mod = getTeam(sb, "00050", retPrefix("mod", "prefix_tab"), ChatColor.GRAY); //green
		Team translator = getTeam(sb, "00055", retPrefix("translator", "prefix_tab"), ChatColor.GRAY);
		Team support = getTeam(sb, "00060", retPrefix("support", "prefix_tab"), ChatColor.GRAY); //blue
		Team builder = getTeam(sb, "00070", retPrefix("builder", "prefix_tab"), ChatColor.GRAY); //aqua
		Team rltm = getTeam(sb, "00080", retPrefix("rltm", "prefix_tab"), ChatColor.GRAY); //dark-purple
		Team rtm = getTeam(sb, "00090", retPrefix("rtm", "prefix_tab"), ChatColor.GRAY); //dark-purple
		Team partner = getTeam(sb, "00100", retPrefix("partner", "prefix_tab"), ChatColor.GRAY); //green
		Team beta = getTeam(sb, "00110", retPrefix("beta", "prefix_tab"), ChatColor.GRAY); //dark-blue & red
		Team patron = getTeam(sb, "00120", retPrefix("patron", "prefix_tab"), ChatColor.GRAY); //dark-aqua
		Team nitrobooster = getTeam(sb, "00130", retPrefix("dcnitro", "prefix_tab"), ChatColor.GRAY); //dark-blue
		Team freund = getTeam(sb, "00140", retPrefix("freund", "prefix_tab"), ChatColor.GRAY); //dark-aqua
		Team spieler = getTeam(sb, "00150", retPrefix("spieler", "prefix_tab"), ChatColor.GRAY); //gray
		Team tafk = getTeam(sb, "00160", retPrefix("tafk", "prefix_tab"), ChatColor.GRAY);
		Team afk = getTeam(sb, "00170", retPrefix("safk", "prefix_tab"), ChatColor.GRAY);
		
		for(Player all : Bukkit.getOnlinePlayers()) {
			PermissionUser pp = PermissionsEx.getUser(all);
			HashMap<String, Object> hm = new HashMap<>();
	    	hm.put("uuid", all.getUniqueId().toString().replace("-", ""));
	    	ResultSet rs = Main.mysql.select("redicore_userstats", hm);
	    	rs.next();
			if(pp.inGroup("PMan")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						tafk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						pm.addPlayer(all);
						all.setDisplayName(retPrefix("pm", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("pm", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7| " + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						spieler.addPlayer(all);
						all.setDisplayName(retPrefix("spieler", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("spieler", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("HumanR")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						tafk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						hr.addPlayer(all);
						all.setDisplayName(retPrefix("hr", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("hr", "prefix_chat") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7| " + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						spieler.addPlayer(all);
						all.setDisplayName(retPrefix("spieler", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("spieler", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("Developer")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						tafk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						dev.addPlayer(all);
						all.setDisplayName(retPrefix("dev", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("dev", "prefix_tab") + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7| " + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						spieler.addPlayer(all);
						all.setDisplayName(retPrefix("spieler", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("spieler", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("CMan")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						tafk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						cman.addPlayer(all);
						all.setDisplayName(retPrefix("cman", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("cman", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7| " + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						spieler.addPlayer(all);
						all.setDisplayName(retPrefix("spieler", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("spieler", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("AMan")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						tafk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						aman.addPlayer(all);
						all.setDisplayName(retPrefix("aman", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("aman", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7| " + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						spieler.addPlayer(all);
						all.setDisplayName(retPrefix("spieler", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("spieler", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("Admin")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						tafk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						admin.addPlayer(all);
						all.setDisplayName(retPrefix("admin", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("admin", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7| " + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						spieler.addPlayer(all);
						all.setDisplayName(retPrefix("spieler", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("spieler", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("Mod")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						tafk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						mod.addPlayer(all);
						all.setDisplayName(retPrefix("mod", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("mod", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7| " + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						spieler.addPlayer(all);
						all.setDisplayName(retPrefix("spieler", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("spieler", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("Translator")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						tafk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						translator.addPlayer(all);
						all.setDisplayName(retPrefix("translator", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("translator", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7| " + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						spieler.addPlayer(all);
						all.setDisplayName(retPrefix("spieler", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("spieler", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("Support")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						tafk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						support.addPlayer(all);
						all.setDisplayName(retPrefix("support", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("support", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7| " + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						spieler.addPlayer(all);
						all.setDisplayName(retPrefix("spieler", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("spieler", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("Builder")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						tafk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						builder.addPlayer(all);
						all.setDisplayName(retPrefix("builder", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("builder", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7| " + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						spieler.addPlayer(all);
						all.setDisplayName(retPrefix("spieler", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("spieler", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("RLTM")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						rltm.addPlayer(all);
						all.setDisplayName(retPrefix("rltm", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("rltm", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7| " + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						spieler.addPlayer(all);
						all.setDisplayName(retPrefix("spieler", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("spieler", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("RTM")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						rtm.addPlayer(all);
						all.setDisplayName(retPrefix("rtm", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("rtm", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7| " + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						spieler.addPlayer(all);
						all.setDisplayName(retPrefix("spieler", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("spieler", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("partner")) {
				if(rs.getBoolean("loggedin")) {
					afk.addPlayer(all);
					all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
				}else {
					partner.addPlayer(all);
					all.setDisplayName(retPrefix("partner", "prefix_chat") + all.getName());
					all.setPlayerListName(retPrefix("partner", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
				}
			}else if(pp.inGroup("beta")) {
				if(isAFK(all)) {
					afk.addPlayer(all);
					all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
				}else {
					beta.addPlayer(all);
					all.setDisplayName(retPrefix("beta", "prefix_chat") + all.getName());
					all.setPlayerListName(retPrefix("beta", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
				}
			}else if(pp.inGroup("Patron")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						patron.addPlayer(all);
						all.setDisplayName(retPrefix("patron", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("patron", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7| " + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						spieler.addPlayer(all);
						all.setDisplayName(retPrefix("spieler", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("spieler", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("nitrobooster")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						nitrobooster.addPlayer(all);
						all.setDisplayName(retPrefix("dcnitro", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("dcnitro", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7| " + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						spieler.addPlayer(all);
						all.setDisplayName(retPrefix("spieler", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("spieler", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("Friend")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						freund.addPlayer(all);
						all.setDisplayName(retPrefix("freund", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("freund", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7| " + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						spieler.addPlayer(all);
						all.setDisplayName(retPrefix("spieler", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("spieler", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else {
				if(isAFK(all)) {
					afk.addPlayer(all);
					all.setPlayerListName("§9AFK §7| " + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
				}else {
					spieler.addPlayer(all);
					all.setDisplayName(retPrefix("spieler", "prefix_chat") + all.getName());
					all.setPlayerListName(retPrefix("spieler", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
				}
			}
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		String msg = e.getMessage().replace("%", "%%");
		if(p.hasPermission("mlps.colorchat")) {
			e.setFormat(p.getDisplayName() + " §7(§9" + igid(p) + "§7): " + ChatColor.translateAlternateColorCodes('&', msg));
		}else {
			e.setFormat(p.getDisplayName() + " §7(§9" + igid(p) + "§7): " + msg);
		}
	}
	
	private String retPrefix(String rank, String type) {
		String prefix = "";
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM redicore_ranks WHERE rank = ?");
			ps.setString(1, rank);
			ResultSet rs = ps.executeQuery();
			rs.next();
			prefix = rs.getString(type);
			rs.close();
			ps.close();
		}catch (SQLException e) {
			prefix = "§0ERR";
		}
		return prefix.replace("&", "§");
	}
	
	private static boolean isAFK(Player p) {
		boolean boo = false;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM redicore_userstats WHERE uuid = ?");
			ps.setString(1, p.getUniqueId().toString().replace("-", ""));
			ResultSet rs = ps.executeQuery();
			rs.next();
			boo = rs.getBoolean("afk");
			rs.close();
			ps.close();
		}catch (SQLException e) { e.printStackTrace(); }
		return boo;
	}
	
	public Team getTeam(Scoreboard sb, String Team, String prefix, ChatColor cc) {
		Team team = sb.registerNewTeam(Team);
		team.setPrefix(prefix);
		team.setColor(cc);
		return team;
	}
	
	private String igid(Player p) {
		String s = "";
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM redicore_userstats WHERE uuid = ?");
			ps.setString(1, p.getUniqueId().toString().replaceAll("-", ""));
		    ResultSet rs = ps.executeQuery();
		    rs.next();
		    s = rs.getString("userid");
		    rs.close();
		    ps.close();
		}catch (SQLException e) { }
		return s;
	}
	
	String getBuildTime(String player) {
		if(buildtime.containsKey(player)) {
			long btime = buildtime.get(player);
			long current = System.currentTimeMillis() / 1000;
			long tst = (current - btime);
			long seconds = tst;
			long minutes = 0;
			long hours = 0;
			while(seconds > 59) {
				seconds -= 60;
				minutes++;
			}
			while(minutes > 59) {
				minutes -= 60;
				hours++;
			}
			String hr = "";
			String min = "";
			String sec = "";
			if(hours < 10) {
				hr = "0" + hours;
			}else {
				hr = "" + hours;
			}
			if(minutes < 10) {
				min = "0" + minutes;
			}else {
				min = "" + minutes;
			}
			if(seconds < 10) {
				sec = "0" + seconds;
			}else {
				sec = "" + seconds;
			}
			return hr + ":" + min + ":" + sec;
		}else {
			return "Errored";
		}
	}
	
	private static String igpre(Player p) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM redicore_userstats WHERE uuid = ?");
			ps.setString(1, p.getUniqueId().toString().replaceAll("-", ""));
			ResultSet rs = ps.executeQuery();
			rs.next();
			String pre = rs.getString("userprefix");
			String prefix = "";
			if(pre.equalsIgnoreCase("RESET")) {
				prefix = "";
			}else {
				prefix = pre;
			}
			rs.close();
			ps.close();
			return prefix;
		} catch (SQLException e) {
			e.printStackTrace();
			return "ERROR";
		}
	}
	
	int pause = 0;
	
	public void SBSched(int delay, int sbsched) {
		new BukkitRunnable() {
			@Override
			public void run() {
				APIs api = new APIs();
				SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
				String stime = time.format(new Date());
				Serverupdater su = new Serverupdater();
				su.Serverrestarter();
				pause++;
				if(pause == 5) {
					pause = 0;
					su.updateServer();
					su.updateWorlds();
				}
				for(Player all : Bukkit.getOnlinePlayers()) {
					all.setPlayerListHeaderFooter(api.returnStringReady(all, "scoreboard.playerlist.top").replace("|", "\n"), api.returnStringReady(all, "scoreboard.playerlist.bottom").replace("|", "\n").replace("%time", stime).replace("%servername", api.getServerName()).replace("%serverid", api.getServerId()));
					try {
						setScoreboard(all);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}.runTaskTimer(Main.instance, delay, sbsched);
	}
}