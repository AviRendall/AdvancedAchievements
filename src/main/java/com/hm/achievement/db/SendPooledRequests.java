package com.hm.achievement.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.hm.achievement.AdvancedAchievements;

public class SendPooledRequests implements Runnable {

	private AdvancedAchievements plugin;

	public SendPooledRequests(AdvancedAchievements plugin, boolean init) {
		if (init == true)
			DatabasePools.databasePoolsInit();
		this.plugin = plugin;
	}

	public void run() {

		sendRequests();

	}

	public void sendRequests() {

		try {
			Connection conn = plugin.getDb().getSQLConnection();
			Statement st = conn.createStatement();

			for (String player : DatabasePools.getArrowHashMap().keySet())
				st.addBatch("replace into `arrows` (playername, arrows) VALUES ('"
						+ player
						+ "', "
						+ DatabasePools.getArrowHashMap().get(player) + ")");

			for (String player : DatabasePools.getShearHashMap().keySet())
				st.addBatch("replace into `shears` (playername, shears) VALUES ('"
						+ player
						+ "', "
						+ DatabasePools.getShearHashMap().get(player) + ")");

			for (String player : DatabasePools.getSnowballHashMap().keySet())
				st.addBatch("replace into `snowballs` (playername, snowballs) VALUES ('"
						+ player
						+ "', "
						+ DatabasePools.getSnowballHashMap().get(player) + ")");

			for (String player : DatabasePools.getEggHashMap().keySet())
				st.addBatch("replace into `eggs` (playername, eggs) VALUES ('"
						+ player + "', "
						+ DatabasePools.getEggHashMap().get(player) + ")");

			for (String player : DatabasePools.getBlockBreakHashMap().keySet())
				st.addBatch("replace into `breaks` (playername, blockid, breaks) VALUES ('"
						+ player.substring(0, 36)
						+ "',"
						+ player.substring(36)
						+ ", "
						+ DatabasePools.getBlockBreakHashMap().get(player)
						+ ")");

			for (String player : DatabasePools.getBlockPlaceHashMap().keySet())
				st.addBatch("replace into `places` (playername, blockid, places) VALUES ('"
						+ player.substring(0, 36)
						+ "',"
						+ player.substring(36)
						+ ", "
						+ DatabasePools.getBlockPlaceHashMap().get(player)
						+ ")");

			for (String player : DatabasePools.getEntityDeathHashMap().keySet())
				st.addBatch("replace into `kills` (playername, mobname, kills) VALUES ('"
						+ player.substring(0, 36)
						+ "', '"
						+ player.substring(36)
						+ "', "
						+ DatabasePools.getEntityDeathHashMap().get(player)
						+ ")");

			st.executeBatch();

			st.close();
			conn.close();

			DatabasePools.getEntityDeathHashMap().clear();
			DatabasePools.getBlockPlaceHashMap().clear();
			DatabasePools.getBlockBreakHashMap().clear();
			DatabasePools.getEggHashMap().clear();
			DatabasePools.getSnowballHashMap().clear();
			DatabasePools.getShearHashMap().clear();
			DatabasePools.getArrowHashMap().clear();

		} catch (SQLException e) {

			plugin.getLogger().severe(
					"Error while sending pooled requests to database.");
			e.printStackTrace();
		}
	}

}
