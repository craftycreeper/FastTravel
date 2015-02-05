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

package net.minebot.fasttravel.Util;

import net.minebot.fasttravel.FastTravelSignsPlugin;

import java.io.File;
import java.sql.*;

public class SQLite {

    private Connection connection;
    public FastTravelSignsPlugin plugin;
    private Statement dbStatement;

    public SQLite(FastTravelSignsPlugin plugin) {
        this.plugin = plugin;
    }

    private void connect(){
        File dbFile = new File(plugin.getDataDir(), "signs.db");
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
            dbStatement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //TODO Move somewhere else
    private void setupTables(){
        createTables("FastTravelSigns", "name TEXT, creator TEXT, signloc_World TEXT, signloc_X INTEGER," +
                " signloc_Y INTEGER, signloc_Z INTEGER, signloc_Yaw REAL, " +
                "tploc_World TEXT, tploc_X INTEGER, tploc_Y INTEGER, tploc_Z INTEGER, tploc_Yaw REAL," +
                "automatic BOOLEAN, price REAL, range INTEGER");

        createTables("Players", "Name TEXT, UUID TEXT");
    }

    public boolean createTables(String tableName, String colums){
        try {
            dbStatement.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " (" + colums  + ")");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public ResultSet query(String sql) {
        try {
            ResultSet rs = dbStatement.executeQuery(sql);
            if (rs == null) return null;
            if (rs.isAfterLast()) return null;
            if (rs.isBeforeFirst()) rs.next();
            return rs;
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String queryString(String sql) {
        try {
            ResultSet rs = dbStatement.executeQuery(sql);
            if (rs == null) return null;
            if (rs.isAfterLast()) return null;
            if (rs.isBeforeFirst()) rs.next();
            return rs.getString(1);
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String queryString(String sql, String column) {
        try {
            ResultSet rs = dbStatement.executeQuery(sql);
            if (rs == null) return null;
            if (rs.isAfterLast()) return null;
            if (rs.isBeforeFirst()) rs.next();
            return rs.getString(column);
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int queryInt(String sql) {
        try {
            ResultSet rs = dbStatement.executeQuery(sql);
            if (rs == null) return -1;
            if (rs.isAfterLast()) return -1;
            if (rs.isBeforeFirst()) rs.next();
            return rs.getInt(1);
        }catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int queryInt(String sql, String column) {
        try {
            ResultSet rs = dbStatement.executeQuery(sql);
            if (rs == null) return -1;
            if (rs.isAfterLast()) return -1;
            if (rs.isBeforeFirst()) rs.next();
            return rs.getInt(column);
        }catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public float queryFloat(String sql) {
        try {
            ResultSet rs = dbStatement.executeQuery(sql);
            if (rs == null) return -1F;
            if (rs.isAfterLast()) return -1F;
            if (rs.isBeforeFirst()) rs.next();
            return rs.getFloat(1);
        }catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public float queryFloat(String sql, String column) {
        try {
            ResultSet rs = dbStatement.executeQuery(sql);
            if (rs == null) return -1F;
            if (rs.isAfterLast()) return -1F;
            if (rs.isBeforeFirst()) rs.next();
            return rs.getFloat(column);
        }catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double queryDouble(String sql) {
        try {
            ResultSet rs = dbStatement.executeQuery(sql);
            if (rs == null) return -1F;
            if (rs.isAfterLast()) return -1F;
            if (rs.isBeforeFirst()) rs.next();
            return rs.getDouble(1);
        }catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double queryDouble(String sql, String column) {
        try {
            ResultSet rs = dbStatement.executeQuery(sql);
            if (rs == null) return -1F;
            if (rs.isAfterLast()) return -1F;
            if (rs.isBeforeFirst()) rs.next();
            return rs.getDouble(column);
        }catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean queryBoolean(String sql) {
        try {
            ResultSet rs = dbStatement.executeQuery(sql);
            if (rs == null) return false;
            if (rs.isAfterLast()) return false;
            if (rs.isBeforeFirst()) rs.next();
            return rs.getBoolean(1);
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean queryBoolean(String sql, String column) {
        try {
            ResultSet rs = dbStatement.executeQuery(sql);
            if (rs == null) return false;
            if (rs.isAfterLast()) return false;
            if (rs.isBeforeFirst()) rs.next();
            return rs.getBoolean(column);
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int update(String sql) {
        try {
            return dbStatement.executeUpdate(sql);
        }catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean tableContains(String table, String column, String value) {
        try {
            ResultSet rs = query("SELECT COUNT(" + column + ") AS " + column + "Count FROM " + table + " WHERE " + column + "='" + value + "'");
            if (rs == null) return false;
            if (rs.isAfterLast()) return false;
            if (rs.isBeforeFirst()) rs.next();
            if (rs.getInt(1) == 0) return false;
            else return true;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void insert(String table, String columns, String values) {
        try {
            dbStatement.executeUpdate("INSERT INTO " + table + " (" + columns + ") VALUES (" + values + ");");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if(connection != null && (!(connection.isClosed()))) {
                connection.close();
                if(connection.isClosed()) {
                    System.out.println("Closed SQLite database");
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

}
