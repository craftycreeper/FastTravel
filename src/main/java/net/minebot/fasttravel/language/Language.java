/*
 *
 *  * FastTravelSigns - The Simple Exploration and RPG-Friendly Teleportation Plugin
 *  *
 *  * Copyright (c) 2011-2014 craftycreeper, minebot.net, oneill011990
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy of
 *  * this software and associated documentation files (the "Software"), to deal in
 *  * the Software without restriction, including without limitation the rights to
 *  * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 *  * of the Software, and to permit persons to whom the Software is furnished to do
 *  * so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package net.minebot.fasttravel.language;

import net.minebot.fasttravel.FastTravelSignsPlugin;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oneill011990 on 16.09.2014.
 */
public abstract class Language {

    private static List<Language> langs = new ArrayList<Language>();
    protected YamlConfiguration lang;
    protected File langFile;
    protected static Configuration config;

    static {
        langs = new ArrayList<Language>();
        config = FastTravelSignsPlugin.getConfiguration();
    }

    public abstract void createLanguageFile();

    public abstract void updateLanguage();

    public String translate(String key){
        return lang.getString(key);
    }

    public File getFile(){
        return langFile;
    }

    public List<Language> getLangs(){
        return langs;
    }

    public YamlConfiguration getKeys(){
        return lang;
    }
}
