package com.hm.achievement.listener;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

import com.hm.achievement.AdvancedAchievements;
import com.hm.achievement.db.DatabasePools;

public class AchieveArrowListener implements Listener {
	private AdvancedAchievements plugin;

	public AchieveArrowListener(AdvancedAchievements plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityShootBowEvent(EntityShootBowEvent event) {

		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		if (!player.hasPermission("achievement.get")
				|| plugin.isRestrictCreative()
				&& player.getGameMode() == GameMode.CREATIVE
				|| plugin.isInExludedWorld(player))
			return;

		Integer arrows = 0;
		if (!DatabasePools.getArrowHashMap().containsKey(
				player.getUniqueId().toString()))
			arrows = plugin.getDb().getArrows(player) + 1;
		else
			arrows = DatabasePools.getArrowHashMap().get(
					player.getUniqueId().toString()) + 1;

		DatabasePools.getArrowHashMap().put(player.getUniqueId().toString(),
				arrows);

		String configAchievement = "Arrows." + arrows;
		if (plugin.getReward().checkAchievement(configAchievement)) {
			String name = plugin.getConfig().getString(
					configAchievement + ".Name");
			String msg = plugin.getConfig().getString(
					configAchievement + ".Message");
			plugin.getAchievementDisplay()
					.displayAchievement(player, name, msg);
			Date now = new Date();
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			plugin.getDb().registerAchievement(
					player,
					plugin.getConfig().getString(configAchievement + ".Name"),
					plugin.getConfig()
							.getString(configAchievement + ".Message"),
					"&0" + format.format(now));

			plugin.getReward().checkConfig(player, configAchievement);
		}
	}
}