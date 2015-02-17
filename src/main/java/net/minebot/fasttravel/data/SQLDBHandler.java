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
        List<UUID> players = new ArrayList<>();

        ResultSet names = db.query("SELECT name FROM FastTravelSigns");
        ResultSet creators = db.query("SELECT creator FROM FastTravelSigns");
        ResultSet signLoc_world = db.query("SELECT signloc_World FROM FastTravelSigns");
        ResultSet signLoc_x = db.query("SELECT signloc_X FROM FastTravelSigns");
        ResultSet singLoc_y = db.query("SELECT signloc_Y FROM FastTravelSigns");
        ResultSet singLoc_z = db.query("SELECT signloc_Z FROM FastTravelSigns");
        ResultSet singLoc_yaw = db.query("SELECT signloc_Yaw FROM FastTravelSigns");
        ResultSet tpgLoc_world = db.query("SELECT tploc_World FROM FastTravelSigns");
        ResultSet tpLoc_x = db.query("SELECT tploc_X FROM FastTravelSigns");
        ResultSet tpLoc_y = db.query("SELECT tploc_Y FROM FastTravelSigns");
        ResultSet tpLoc_z = db.query("SELECT tploc_Z FROM FastTravelSigns");
        ResultSet tpLoc_yaw = db.query("SELECT tploc_Yaw FROM FastTravelSigns");
        ResultSet automatics = db.query("SELECT automatic FROM FastTravelSigns");
        ResultSet prices = db.query("SELECT price FROM FastTravelSigns");
        ResultSet ranges = db.query("SELECT range FROM FastTravelSigns");

        for (int i = 0; i < entries; i++) {
            players = db.getList(names.getString(i));
            Location signLoc = new Location(plugin.getServer().getWorld(signLoc_world.getString(i)), signLoc_x.getDouble(i),
                    singLoc_y.getDouble(i), singLoc_z.getDouble(i));
            signLoc.setYaw(((float) singLoc_yaw.getDouble(i)));
            Location tpLoc = new Location(plugin.getServer().getWorld(tpgLoc_world.getString(i)), tpLoc_x.getDouble(i),
                    tpLoc_y.getDouble(i), tpLoc_z.getDouble(i));
            tpLoc.setYaw((float) tpLoc_yaw.getDouble(i));
            FastTravelSignDB.addSign(new FastTravelSign(names.getString(i), UUID.fromString(creators.getString(i)),
                    prices.getDouble(i), signLoc, tpLoc, automatics.getBoolean(i), ranges.getInt(i), players));
        }

        plugin.getLogger().info("Loaded " + FastTravelSignDB.getAllSigns().size() + " fast travel signs.");
    }

    public static void save(){
        for (String signName : FastTravelSignDB.getSignMap().keySet()) {
            FastTravelSign sign = FastTravelSignDB.getSign(signName);
            if (db.tableContains("name", signName)){
                addNew(FastTravelSignDB.getSign(signName));
                return;
            }

            try {
                PreparedStatement prepStatement = db.dbConn.prepareStatement("UPDATE FastTravelSigns SET name = '" +
                        signName.toString() + ", creator = '" + sign.getCreator().toString() + "', signloc_World = '" +
                        sign.getSignLocation().getWorld().toString() + "', singloc_X = '" +
                        sign.getSignLocation().getX() + "', singloc_Y = '" + sign.getSignLocation().getY() +
                        "', singloc_Z = '" + sign.getSignLocation().getZ() + "', signloc_Yaw  = '" +
                        sign.getSignLocation().getYaw() + "', tploc_World = '" +
                        sign.getTPLocation().getWorld().toString() + ", tploc_X = '" + sign.getTPLocation().getX() +
                        "', tploc_Y = '"+ sign.getTPLocation().getY() + "', tploc_Z = '" +
                        sign.getTPLocation().getZ() + "', tploc_Yaw = '" + sign.getTPLocation().getYaw() +
                        "', automatic = '" + sign.isAutomatic() + "', price = '" + sign.getPrice() +
                        "', range = '" + sign.getPrice() + "', players = ? WHERE name = '" +
                        signName.toString() + "';");

                prepStatement.setBytes(1, db.updateList(sign.getPlayers()));
                prepStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void addNew(FastTravelSign sign){
        try {
            PreparedStatement prepStatement = db.dbConn.prepareStatement("UPDATE FastTravelSigns SET name = '" +
                    sign.getName() + ", creator = '" + sign.getCreator().toString() + "', signloc_World = '" +
                    sign.getSignLocation().getWorld().toString() + "', singloc_X = '" +
                    sign.getSignLocation().getX() + "', singloc_Y = '" + sign.getSignLocation().getY() +
                    "', singloc_Z = '" + sign.getSignLocation().getZ() + "', signloc_Yaw  = '" +
                    sign.getSignLocation().getYaw() + "', tploc_World = '" +
                    sign.getTPLocation().getWorld().toString() + ", tploc_X = '" + sign.getTPLocation().getX() +
                    "', tploc_Y = '"+ sign.getTPLocation().getY() + "', tploc_Z = '" +
                    sign.getTPLocation().getZ() + "', tploc_Yaw = '" + sign.getTPLocation().getYaw() +
                    "', automatic = '" + sign.isAutomatic() + "', price = '" + sign.getPrice() +
                    "', range = '" + sign.getPrice() + "', players = ? WHERE name = '" +
                    sign.getName() + "';");

            prepStatement.setBytes(1, db.updateList(sign.getPlayers()));
            prepStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
