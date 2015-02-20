/*
 * FastTravelSigns - The Simple Exploration and RPG-Friendly Teleportation Plugin
 *
 * Copyright (c) 2011-2015 craftycreeper, minebot.net, oneill011990
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.minebot.fasttravel.data;

import net.minebot.fasttravel.FastTravelSignsPlugin;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by oneill011990 on 06.02.2015.
 */
public class SQLDBHandler {

    private static Database db;
    private static FastTravelSignsPlugin plugin;
    private static List<String> filePlayers;
    private static int entries;

    static {
        db = Database.getDatabaseBySystem("SQL");
        plugin = FastTravelSignsPlugin.getInstance();
        filePlayers = new ArrayList<>();
    }

    public static void load() throws SQLException, IOException {
        db.init();

        entries = db.query("SELECT COUNT(*) FROM FastTravelSigns").getInt(1);

        if (entries == 0){
            plugin.getLogger().info("No signs found in the database");
            return;
        }

        ResultSet rs = db.query("SELECT * From FastTravelSigns");

        while (rs.next()) {
            String name = rs.getString(1);
            UUID creator = UUID.fromString(rs.getString(2));
            World signloc_World = plugin.getServer().getWorld(rs.getString(3));
            int signloc_X = rs.getInt(4);
            int signloc_Y = rs.getInt(5);
            int signloc_Z = rs.getInt(6);
            float signloc_Yaw = rs.getFloat(7);
            World tploc_World = plugin.getServer().getWorld(rs.getString(8));
            int tploc_X = rs.getInt(9);
            int tploc_Y = rs.getInt(10);
            int tploc_Z = rs.getInt(11);
            float tploc_Yaw = rs.getFloat(12);
            boolean automatic = db.parseBoolean(rs.getInt(13));
            float price = rs.getFloat(14);
            int range = rs.getInt(15);

            List<UUID> players = db.getList(rs.getBytes(16));

            if (!players.contains(creator)){
                players.add(creator);
            }

            Location signLoc = new Location(signloc_World, signloc_X, signloc_Y, signloc_Z);
            signLoc.setYaw(signloc_Yaw);

            Location tpLoc = new Location(tploc_World, tploc_X, tploc_Y, tploc_Z);
            tpLoc.setYaw(tploc_Yaw);

            FastTravelSignDB.addSign(new FastTravelSign(name, creator, price, signLoc, tpLoc, automatic, range,
                    players));

        }

        plugin.getLogger().info("Loaded " + FastTravelSignDB.getAllSigns().size() + " FastTravelSigns.");
    }

    public static void save(){
        for (String signName : FastTravelSignDB.getSignMap().keySet()) {
            FastTravelSign sign = FastTravelSignDB.getSign(signName);
            if (!db.tableContains("name", signName)){
                addNew(FastTravelSignDB.getSign(signName));
                return;
            }

            try {
                PreparedStatement prepStatement = db.dbConn.prepareStatement("UPDATE FastTravelSigns SET " +
                        "tploc_World = '" + sign.getTPLocation().getWorld().getName() + "', tploc_X = '" +
                        sign.getTPLocation().getX() + "', tploc_Y = '"+ sign.getTPLocation().getY() +
                        "', tploc_Z = '" + sign.getTPLocation().getZ() + "', tploc_Yaw = '" +
                        sign.getTPLocation().getYaw() + "', automatic = '" + db.parseBoolean(sign.isAutomatic()) +
                        "', price = '" + sign.getPrice() + "', range = '" + sign.getPrice() +
                        "', players = ? WHERE name = '" + signName.toString() + "';");

                prepStatement.setBytes(1, db.updateList(sign.getPlayers()));
                prepStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void addNew(FastTravelSign sign){
        try {
            List<UUID> creator = new ArrayList<>();
            creator.add(sign.getCreator());

            PreparedStatement prepStatement = db.dbConn.prepareStatement("INSERT INTO FastTravelSigns (name, creator," +
                    " signloc_World, signloc_X, signloc_Y, signloc_Z, signloc_Yaw, tploc_World, tploc_X," +
                    " tploc_Y, tploc_Z, tploc_Yaw, automatic, price, range, players) VALUES ('" + sign.getName() +
                    "', '" + sign.getCreator().toString() + "', '" + sign.getSignLocation().getWorld().getName() +
                    "', '" + sign.getSignLocation().getX() + "', '" + sign.getSignLocation().getY() + "', '" +
                    sign.getSignLocation().getZ() + "', '" + sign.getSignLocation().getYaw() + "', '" +
                    sign.getTPLocation().getWorld().getName() + "', ' " + sign.getTPLocation().getX() + "', '" +
                    sign.getTPLocation().getY() + "', '" + sign.getTPLocation().getZ() + "', '" +
                    sign.getTPLocation().getYaw() + "', '" + db.parseBoolean(sign.isAutomatic()) + "', '" +
                    sign.getPrice() + "', '" + sign.getRange() + "', ?;");

            prepStatement.setBytes(1, db.updateList(creator));
            prepStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
