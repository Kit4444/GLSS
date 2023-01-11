package at.newt.event;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import at.newt.api.APIs;
import at.newt.main.Main;
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

import at.newt.command.BuildClass;
import at.newt.command.MoneyAPI;
import at.newt.main.Serverupdater;
import at.newt.mysql.lb.MySQL;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ScoreboardCLS implements Listener{
	
	public static HashMap<String, Long> buildtime = new HashMap<>();
	
	@SuppressWarnings("deprecation")
	public void setScoreboard(Player p) throws SQLException {
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective o = sb.registerNewObjective("aaa", "dummy", "InfoBoard");
		PermissionUser po = PermissionsEx.getUser(p);
		APIs api = new APIs();
		int puser = api.getPlayers("BungeeCord", "currPlayers");
		int pusermax = api.getPlayers("BungeeCord", "maxPlayers");
		int puserperc = (puser*100/pusermax);
		String sbpr = api.prefix("scoreboard");
		
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		if(puserperc <= 20) {
			o.setDisplayName(sbpr + " § §a" + puser + " §7/§9 " + pusermax);
		}else if(puserperc >= 21 && puserperc <= 40) {
			o.setDisplayName(sbpr + " § §2" + puser + " §7/§9 " + pusermax);
		}else if(puserperc >= 41 && puserperc <= 60) {
			o.setDisplayName(sbpr + " § §e" + puser + " §7/§9 " + pusermax);
		}else if(puserperc >= 61 && puserperc <= 80) {
			o.setDisplayName(sbpr + " § §6" + puser + " §7/§9 " + pusermax);
		}else if(puserperc >= 81 && puserperc <= 95) {
			o.setDisplayName(sbpr + " § §c" + puser + " §7/§9 " + pusermax);
		}else if(puserperc >= 96) {
			o.setDisplayName(sbpr + " § §4" + puser + " §7/§9 " + pusermax);
		}
		if(BuildClass.users.contains(p.getUniqueId())) {
			ItemStack is = p.getItemInHand();
			String itemid = is.getType().toString();
			o.getScore("§7Buildtime:").setScore(4);
			o.getScore("§7» §a" + getBuildTime(p.getName())).setScore(3);
			o.getScore("§0").setScore(2);
			o.getScore(api.returnStringReady(p, "scoreboard.sideboard.block")).setScore(1);
			o.getScore("§7» §a" + itemid).setScore(0);
		}else {
			o.getScore(api.returnStringReady(p, "scoreboard.sideboard.money")).setScore(8);
			o.getScore("§7» §a" + MoneyAPI.getBankMoney(p.getUniqueId()) + " §7Bank").setScore(7);
			o.getScore("§7» §a" + MoneyAPI.getMoney(p.getUniqueId()) + " §7Cash").setScore(6);
			o.getScore("§f").setScore(5);
			o.getScore(api.returnStringReady(p, "scoreboard.sideboard.rank")).setScore(4);
			if(po.inGroup("pman")) {
				o.getScore("§7» §9Project Manager").setScore(3);
			}else if(po.inGroup("cman")) {
				o.getScore("§7» §2Community Manager").setScore(3);
			}else if(po.inGroup("gmmman")) {
				o.getScore("§7» §4Game Mod. Manager").setScore(3);
			}else if(po.inGroup("dev")) {
				o.getScore("§7» §5Developer").setScore(3);
			}else if(po.inGroup("hr")) {
				o.getScore("§7» §6Human Resources").setScore(3);
			}else if(po.inGroup("cm")) {
				o.getScore("§7» §aCommunity Moderator").setScore(3);
			}else if(po.inGroup("ct")) {
				o.getScore("§7» §1Content Team").setScore(3);
			}else if(po.inGroup("st")) {
				o.getScore("§7» §eSupport Team").setScore(3);
			}else if(po.inGroup("bd")) {
				o.getScore("§7» §bBuilder").setScore(3);
			}else if(po.inGroup("gm")) {
				o.getScore("§7» §cGame Moderator").setScore(3);
			}else if(po.inGroup("aot")) {
				o.getScore("§7» §dAdd-On Team").setScore(3);
			}else if(po.inGroup("train")) {
				o.getScore("§7» §bTrainee").setScore(3);
			}else if(po.inGroup("rltm")) {
				o.getScore("§7» §3Retired Legend").setScore(3);
			}else if(po.inGroup("rtm")) {
				o.getScore("§7» §3Retired Team Member").setScore(3);
			}else if(po.inGroup("part")) {
				o.getScore("§7» §2Partner").setScore(3);
			}else if(po.inGroup("fs")) {
				o.getScore("§7» §dForum Supporter").setScore(3);
			}else if(po.inGroup("nb")) {
				o.getScore("§7» §dNitro Booster").setScore(3);
			}else if(po.inGroup("bt")) {
				o.getScore("§7» §dBeta Tester").setScore(3);
			}else if(po.inGroup("friend")) {
				o.getScore("§7» Friend").setScore(3);
			}else if(po.inGroup("default")) {
				o.getScore("§7» §fPlayer").setScore(3);
			}else {
				o.getScore("§7» §cunknown Role").setScore(3);
			}
			o.getScore("§9").setScore(2);
			o.getScore(api.returnStringReady(p, "scoreboard.sideboard.playerid")).setScore(1);
			o.getScore("§7» §7" + igid(p) + " §f" + igpre(p)).setScore(0);
		}
		p.setScoreboard(sb);
		
		Team pman = getTeam(sb, "00000", retPrefix("pman", "prefix_tab"), ChatColor.GRAY);
		Team cman = getTeam(sb, "00010", retPrefix("cman", "prefix_tab"), ChatColor.GRAY);
		Team gmmman = getTeam(sb, "00020", retPrefix("gmmman", "prefix_tab"), ChatColor.GRAY);
		Team dev = getTeam(sb, "00030", retPrefix("dev", "prefix_tab"), ChatColor.GRAY);
		Team hr = getTeam(sb, "00040", retPrefix("hr", "prefix_tab"), ChatColor.GRAY);
		Team cm = getTeam(sb, "00050", retPrefix("cm", "prefix_tab"), ChatColor.GRAY);
		Team ct = getTeam(sb, "00060", retPrefix("ct", "prefix_tab"), ChatColor.GRAY);
		Team st = getTeam(sb, "00070", retPrefix("st", "prefix_tab"), ChatColor.GRAY);
		Team bd = getTeam(sb, "00080", retPrefix("bd", "prefix_tab"), ChatColor.GRAY);
		Team gm = getTeam(sb, "00090", retPrefix("gm", "prefix_tab"), ChatColor.GRAY);
		Team aot = getTeam(sb, "00100", retPrefix("aot", "prefix_tab"), ChatColor.GRAY);
		Team train = getTeam(sb, "00110", retPrefix("train", "prefix_tab"), ChatColor.GRAY);
		Team rltm = getTeam(sb, "00120", retPrefix("rltm", "prefix_tab"), ChatColor.GRAY);
		Team rtm = getTeam(sb, "00130", retPrefix("rtm", "prefix_tab"), ChatColor.GRAY);
		Team part = getTeam(sb, "00140", retPrefix("part", "prefix_tab"), ChatColor.GRAY);
		Team fs = getTeam(sb, "00150", retPrefix("fs", "prefix_tab"), ChatColor.GRAY);
		Team nb = getTeam(sb, "00160", retPrefix("nb", "prefix_tab"), ChatColor.GRAY);
		Team bt = getTeam(sb, "00170", retPrefix("bt", "prefix_tab"), ChatColor.GRAY);
		Team friend = getTeam(sb, "00180", retPrefix("friend", "prefix_tab"), ChatColor.GRAY);
		Team player = getTeam(sb, "00190", retPrefix("default", "prefix_tab"), ChatColor.GRAY);
		Team afk = getTeam(sb, "00200", retPrefix("safk", "prefix_tab"), ChatColor.GRAY);
		
		for(Player all : Bukkit.getOnlinePlayers()) {
			PermissionUser pp = PermissionsEx.getUser(all);
			HashMap<String, Object> hm = new HashMap<>();
	    	hm.put("uuid", all.getUniqueId().toString().replace("-", ""));
	    	ResultSet rs = Main.mysql.select("redicore_userstats", hm);
	    	rs.next();
			if(pp.inGroup("pman")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						pman.addPlayer(all);
						all.setDisplayName(retPrefix("pman", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("pman", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						player.addPlayer(all);
						all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("hr")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						hr.addPlayer(all);
						all.setDisplayName(retPrefix("hr", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("hr", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						player.addPlayer(all);
						all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("dev")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						dev.addPlayer(all);
						all.setDisplayName(retPrefix("dev", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("dev", "prefix_tab") + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						player.addPlayer(all);
						all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("cman")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						cman.addPlayer(all);
						all.setDisplayName(retPrefix("cman", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("cman", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						player.addPlayer(all);
						all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("gmmman")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						gmmman.addPlayer(all);
						all.setDisplayName(retPrefix("gmmman", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("gmmman", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						player.addPlayer(all);
						all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("gm")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						gm.addPlayer(all);
						all.setDisplayName(retPrefix("gm", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("gm", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						player.addPlayer(all);
						all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("cm")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						cm.addPlayer(all);
						all.setDisplayName(retPrefix("cm", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("cm", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						player.addPlayer(all);
						all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("ct")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						ct.addPlayer(all);
						all.setDisplayName(retPrefix("ct", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("ct", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						player.addPlayer(all);
						all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("st")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						st.addPlayer(all);
						all.setDisplayName(retPrefix("st", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("st", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						player.addPlayer(all);
						all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("bd")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						bd.addPlayer(all);
						all.setDisplayName(retPrefix("bd", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("bd", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						player.addPlayer(all);
						all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("aot")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						aot.addPlayer(all);
						all.setDisplayName(retPrefix("aot", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("aot", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						player.addPlayer(all);
						all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("train")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						train.addPlayer(all);
						all.setDisplayName(retPrefix("train", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("train", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						player.addPlayer(all);
						all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("rltm")) {
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
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						player.addPlayer(all);
						all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("rtm")) {
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
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						player.addPlayer(all);
						all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("part")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						part.addPlayer(all);
						all.setDisplayName(retPrefix("part", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("part", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						player.addPlayer(all);
						all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("bt")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						bt.addPlayer(all);
						all.setDisplayName(retPrefix("bt", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("bt", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						player.addPlayer(all);
						all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("fs")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						fs.addPlayer(all);
						all.setDisplayName(retPrefix("fs", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("fs", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						player.addPlayer(all);
						all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("nb")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						nb.addPlayer(all);
						all.setDisplayName(retPrefix("nb", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("nb", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						player.addPlayer(all);
						all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("friend")) {
				if(rs.getBoolean("loggedin")) {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName("§9AFK §7- " + all.getName() + " §7| ID§7: §a" + igid(all) + " §f" + igpre(all));
					}else {
						friend.addPlayer(all);
						all.setDisplayName(retPrefix("friend", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("friend", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}else {
					if(isAFK(all)) {
						afk.addPlayer(all);
						all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
					}else {
						player.addPlayer(all);
						all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
						all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
					}
				}
			}else if(pp.inGroup("default")) {
				if(isAFK(all)) {
					afk.addPlayer(all);
					all.setPlayerListName(retPrefix("safk", "prefix_tab") + all.getName() + "7| ID: §a" + igid(all) + " " + igpre(all));
				}else {
					player.addPlayer(all);
					all.setDisplayName(retPrefix("default", "prefix_chat") + all.getName());
					all.setPlayerListName(retPrefix("default", "prefix_tab") + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
				}
			}else {
				player.addPlayer(all);
				all.setDisplayName("§cunknown Role " + all.getName());
				all.setPlayerListName("§cunknown Role " + all.getName() + " §7| ID: §a" + igid(all) + " §f" + igpre(all));
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
	
	static int pause = 0;
	
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