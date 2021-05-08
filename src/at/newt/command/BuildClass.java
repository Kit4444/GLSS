package at.newt.command;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import at.newt.api.APIs;
import at.newt.event.ScoreboardCLS;

public class BuildClass implements CommandExecutor, Listener{
	
	public static ArrayList<UUID> users = new ArrayList<>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage("Use this only ingame!");
		}else {
			Player p = (Player)sender;
			APIs api = new APIs();
			if(p.hasPermission("mlps.canBuild")) {
				if(users.contains(p.getUniqueId())) {
					users.remove(p.getUniqueId());
					api.sendMSGReady(p, "cmd.build.deactivated");
				}else {
					users.add(p.getUniqueId());
					api.sendMSGReady(p, "cmd.build.activated");
					p.getInventory().clear();
					p.setGameMode(GameMode.CREATIVE);
					long time = (System.currentTimeMillis() / 1000);
					ScoreboardCLS.buildtime.put(p.getName(), time);
				}
			}
		}
		return false;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		APIs api = new APIs();
		if(users.contains(p.getUniqueId())) {
			e.setCancelled(false);
		}else {
			e.setCancelled(true);
			api.sendMSGReady(p, "event.build.cantdothat");
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		APIs api = new APIs();
		if(users.contains(p.getUniqueId())) {
			e.setCancelled(false);
		}else {
			e.setCancelled(true);
			api.sendMSGReady(p, "event.build.cantdothat");
		}
	}
	
	@EventHandler
	public void onInteractwithWeed(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		APIs api = new APIs();
		if(e.getAction() == Action.PHYSICAL) {
			e.setCancelled(true);
			api.sendMSGReady(p, "event.wheatdestroy.cantdothat");
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if(users.contains(e.getPlayer().getUniqueId())) {
			users.remove(e.getPlayer().getUniqueId());
		}
	}
}