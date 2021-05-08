package at.newt.command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.newt.api.APIs;
import at.newt.main.Main;
import at.newt.mysql.lb.MySQL;

public class MoneyAPI implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(Main.consolesend);
		}else {
			Player p = (Player)sender;
			APIs api = new APIs();
			if(cmd.getName().equalsIgnoreCase("money")) {
				if(args.length == 0) {
					p.sendMessage(api.prefix("main") + api.returnStringReady(p, "cmd.money.player.own").replace("%money", String.valueOf(getMoney(p.getUniqueId()))));
					p.sendMessage(api.prefix("main") + api.returnStringReady(p, "cmd.money.bank.own").replace("%money", String.valueOf(getBankMoney(p.getUniqueId()))));
				}else if(args.length == 1) {
					Player p2 = Bukkit.getPlayerExact(args[0]);
					p.sendMessage(api.prefix("main") + api.returnStringReady(p, "cmd.money.player.other").replace("%money", String.valueOf(getMoney(p2.getUniqueId())).replace("%displayer", p2.getDisplayName())));
					p.sendMessage(api.prefix("main") + api.returnStringReady(p, "cmd.money.bank.other").replace("%money", String.valueOf(getBankMoney(p2.getUniqueId())).replace("%displayer", p2.getDisplayName())));
				}else {
					p.sendMessage(api.prefix("main") + api.returnStringReady(p, "usage") + " §7/money [Player]");
				}
			}else if(cmd.getName().equalsIgnoreCase("setmoney")) {
				if(!p.hasPermission("mlps.setmoney")) {
					api.noPerm(p);
				}else {
					if(args.length == 0) {
						p.sendMessage(api.prefix("main") + api.returnStringReady(p, "usage") + " §7/setmoney <Player> <Money>");
					}else if(args.length >= 1 && args.length <= 2) {
						Player p2 = Bukkit.getPlayerExact(args[0]);
						if(p2 == null) {
							api.sendMSGReady(p, "cmd.setmoney.playernotonline");
						}else {
							int money = Integer.parseInt(args[1]);
							if(money < 0) {
								api.sendMSGReady(p, "cmd.setmoney.belowzero");
							}else {
								setMoney(p2.getUniqueId(), money);
								p.sendMessage(api.prefix("main") + api.returnStringReady(p, "cmd.setmoney.successfull").replace("%money", args[1]).replace("%displayer", p2.getDisplayName()));
								//api.sendMSGReady(p, "cmd.setmoney.successfull");
							}
						}
					}else {
						p.sendMessage(api.prefix("main") + api.returnStringReady(p, "usage") + " §7/setmoney <Player> <Money>");
					}
				}
			}else if(cmd.getName().equalsIgnoreCase("addmoney")) {
				if(!p.hasPermission("mlps.addmoney")) {
					api.noPerm(p);
				}else {
					if(args.length >= 1 && args.length <= 2) {
						Player p2 = Bukkit.getPlayerExact(args[0]);
						if(p2 == null) {
							api.sendMSGReady(p, "cmd.addmoney.playernotonline");
						}else {
							int money = Integer.parseInt(args[1]);
							int oldmoney = getMoney(p2.getUniqueId());
							setMoney(p2.getUniqueId(), (money + oldmoney));
							p.sendMessage(api.prefix("main") + api.returnStringReady(p, "cmd.addmoney.successfull").replace("%money", String.valueOf(money)).replace("%displayer", p2.getDisplayName()));
							//api.sendMSGReady(p, "cmd.addmoney.successfull");
						}
					}else {
						p.sendMessage(api.prefix("main") + api.returnStringReady(p, "usage") + " §7/addmoney <Player> <Money>");
					}
				}
			}else if(cmd.getName().equalsIgnoreCase("removemoney")) {
				if(!p.hasPermission("mlps.removemoney")) {
					api.noPerm(p);
				}else {
					if(args.length >= 1 && args.length <= 2) {
						Player p2 = Bukkit.getPlayerExact(args[0]);
						if(p2 == null) {
							api.sendMSGReady(p, "cmd.removemoney.playernotonline");
						}else {
							int money = Integer.parseInt(args[1]);
							int oldmoney = getMoney(p2.getUniqueId());
							if(money <= oldmoney) {
								setMoney(p2.getUniqueId(), (oldmoney - money));
								p.sendMessage(api.prefix("main") + api.returnStringReady(p, "cmd.removemoney.successfull").replace("%money", String.valueOf(money)).replace("%displayer", p2.getDisplayName()));
								//api.sendMSGReady(p, "cmd.removemoney.successfull");
							}else {
								api.sendMSGReady(p, "cmd.removemoney.belowzero");
							}
						}
					}else {
						p.sendMessage(api.prefix("main") + api.returnStringReady(p, "usage") + " §7/removemoney <Player> <Money>");
					}
				}
			}else if(cmd.getName().equalsIgnoreCase("pay")) {
				if(args.length >= 1 && args.length <= 2) {
					Player p2 = Bukkit.getPlayerExact(args[0]);
					if(p2 == null) {
						api.sendMSGReady(p, "cmd.pay.playernotonline");
					}else {
						int money = Integer.parseInt(args[1]);
						int oldmoney2 = getMoney(p2.getUniqueId());
						int oldmoney1 = getMoney(p.getUniqueId());
						if(money <= oldmoney1) {
							setMoney(p2.getUniqueId(), (oldmoney2 + money));
							setMoney(p.getUniqueId(), (oldmoney1 - money));
							p.sendMessage(api.prefix("main") + api.returnStringReady(p, "cmd.pay.successfull").replace("%displayer", p2.getDisplayName()).replace("%money", args[1]));
							//api.sendMSGReady(p, "cmd.pay.successfull");
						}else {
							api.sendMSGReady(p, "cmd.pay.belowzero");
						}
					}
				}else {
					p.sendMessage(api.prefix("main") + api.returnStringReady(p, "usage") + " §7/pay <Player> <Money>");
				}
			}else if(cmd.getName().equalsIgnoreCase("topmoney")) {
				try {
					PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM redicore_money ORDER BY money DESC LIMIT 10");
					ResultSet rs = ps.executeQuery();
					int i = 0;
					while(rs.next()) {
						i++;
						p.sendMessage("§7Place §a" + i + " §7| User: §a" + api.getNamefromUUID(rs.getString("uuid_ut")) + " §7| Balance: §a" + rs.getInt("money") + " §7Coins");
					}
				}catch (SQLException e) {
				}
			}else if(cmd.getName().equalsIgnoreCase("setbankmoney")) {
				if(args.length >= 1 && args.length <= 2) {
					Player p2 = Bukkit.getPlayerExact(args[0]);
					if(p2 == null) {
						api.sendMSGReady(p, "cmd.setbankmoney.playernotonline");
					}else {
						if(p.hasPermission("mlps.setbankmoney")) {
							int money = Integer.parseInt(args[1]);
							if(money <= 0) {
								api.sendMSGReady(p, "cmd.setbankmoney.belowzero");
							}else {
								setBankMoney(p2.getUniqueId(), money);
								p.sendMessage(api.prefix("main") + api.returnStringReady(p, "cmd.setbankmoney.successfull").replace("%money", args[1]).replace("%displayer", p2.getDisplayName()));
							}
						}else {
							api.noPerm(p);
						}
					}
				}else {
					p.sendMessage(api.prefix("main") + api.returnStringReady(p, "usage") + " §7/setbankmoney <Player> <Money>");
				}
			}else if(cmd.getName().equalsIgnoreCase("bankdeposit")) { //add to bankaccount
				if(args.length == 1) {
					int money = Integer.parseInt(args[0]);
					int currMoney = getMoney(p.getUniqueId());
					int bankcurrMoney = getBankMoney(p.getUniqueId());
					if(money <= currMoney) {
						setBankMoney(p.getUniqueId(), (bankcurrMoney + money));
						setMoney(p.getUniqueId(), (currMoney - money));
						p.sendMessage(api.prefix("main") + api.returnStringReady(p, "cmd.bankdeposit.successfull").replace("%money", args[0]));
					}else {
						api.sendMSGReady(p, "cmd.bankdeposit.moreaspossible");
					}
				}else {
					p.sendMessage(api.prefix("main") + api.returnStringReady(p, "usage") + " §7/bankdeposit <Money>");
				}
			}else if(cmd.getName().equalsIgnoreCase("bankwithdraw")) { //remove from bankaccount
				if(args.length == 1) {
					int money = Integer.parseInt(args[0]);
					int currMoney = getBankMoney(p.getUniqueId());
					int usercurrMoney = getMoney(p.getUniqueId());
					if(money <= currMoney) {
						setBankMoney(p.getUniqueId(), (currMoney - money));
						setMoney(p.getUniqueId(), (usercurrMoney + money));
						p.sendMessage(api.prefix("main") + api.returnStringReady(p, "cmd.bankwithdraw.successfull").replace("%money", args[0]));
					}else {
						api.sendMSGReady(p, "cmd.bankwithdraw.moreaspossible");
					}
				}else {
					p.sendMessage(api.prefix("main") + api.returnStringReady(p, "usage") + " §7/bankwithdraw <Money>");
				}
			}
		}
		return false;
	}
	
	public static int getBankMoney(UUID uuid) {
		int i = 0;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM redicore_userstats WHERE uuid = ?");
			ps.setString(1, uuid.toString().replace("-", ""));
			ResultSet rs = ps.executeQuery();
			rs.next();
			i = rs.getInt("bankmoney");
			rs.close();
			ps.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}
	
	public static int getMoney(UUID uuid) {
		int i = 0;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM redicore_userstats WHERE uuid = ?");
			ps.setString(1, uuid.toString().replace("-", ""));
			ResultSet rs = ps.executeQuery();
			rs.next();
			i = rs.getInt("money");
			rs.close();
			ps.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}
	
	public static void addMoney(UUID uuid, int moneytoadd) {
		int currentMoney = getMoney(uuid);
		setMoney(uuid, (currentMoney + moneytoadd));
	}
	
	public static void setBankMoney(UUID uuid, int money) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE redicore_userstats SET bankmoney = ? WHERE uuid = ?");
			ps.setInt(1, money);
			ps.setString(2, uuid.toString().replace("-", ""));
			ps.executeUpdate();
			ps.close();
		}catch (SQLException e) {
		}
	}
	
	public static void setMoney(UUID uuid, int money) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE redicore_userstats SET money = ? WHERE uuid = ?");
			ps.setInt(1, money);
			ps.setString(2, uuid.toString().replace("-", ""));
			ps.executeUpdate();
			ps.close();
		}catch (SQLException e) {
		}
	}
	
	public static boolean hasAccount(UUID uuid) {
		boolean boo = false;
		HashMap<String, Object> hm = new HashMap<>();
		hm.put("uuid_ut", uuid);
		try {
			if(Main.mysql.isInDatabase("redicore_userstats", hm)) {
				boo = true;
			}else {
				boo = false;
			}
		}catch (SQLException e) {
			boo = false;
		}
		return boo;
	}
	
	public static boolean hasenoughmoney(int usermoney, int needmoney) {
		boolean boo = false;
		if(needmoney <= usermoney) {
			boo = true;
		}else {
			boo = false;
		}
		return boo;
	}
}