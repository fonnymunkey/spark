/*
 * This file is part of spark.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Modified on 8/9/2023 by fonnymunkey under GNU GPLv3 for 1.12.2 backport
 */

package me.lucko.spark.forge;

import me.lucko.spark.forge.plugin.ForgeClientSparkPlugin;
import me.lucko.spark.forge.plugin.ForgeLoadingSparkPlugin;
import me.lucko.spark.forge.plugin.ForgeServerSparkPlugin;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mod(
        modid = "spark-unforged",
        name = "Spark Unforged",
        version = "@version@",
        acceptableRemoteVersions = "*"
)
public class ForgeSparkMod {

    private static final Path configDirectory = Paths.get(new File("config").toPath() + File.separator + "spark");
    private ForgeServerSparkPlugin activeServerPlugin;
    private static ForgeLoadingSparkPlugin activeLoadingPlugin;

    public static String getVersion() {
        return ForgeSparkMod.class.getAnnotation(Mod.class).version();
    }

    public static Path getConfigDirectory() {
        return configDirectory;
    }

    @Mod.EventHandler
    public void clientInit(FMLInitializationEvent e) {
        if(FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ForgeClientSparkPlugin.register(this);
        }
    }

    @Mod.EventHandler
    public void serverInit(FMLServerStartingEvent e) {
        this.activeServerPlugin = ForgeServerSparkPlugin.register(this, e);
    }

    @Mod.EventHandler
    public void serverStop(FMLServerStoppingEvent e) {
        if(this.activeServerPlugin != null) {
            this.activeServerPlugin.disable();
            this.activeServerPlugin = null;
        }
    }

    public static void startLoadingPlugin() {
        activeLoadingPlugin = ForgeLoadingSparkPlugin.register();
        //Register first to init config, then check for enable/disable
        if(!ForgeSparkMod.getActiveLoadingPlugin().getPlatform().getConfiguration().getOrSaveBoolean("loadingProfiler_ENABLED", false)) {
            activeLoadingPlugin.disable();
            activeLoadingPlugin = null;
        }
    }

    public static ForgeLoadingSparkPlugin getActiveLoadingPlugin() {
        return activeLoadingPlugin;
    }

    public static void endLoadingPlugin() {
        if(activeLoadingPlugin != null) {
            ForgeLoadingSamplerModule.clearHangingSamplers(ForgeSparkMod.getActiveLoadingPlugin());
            activeLoadingPlugin.disable();
            activeLoadingPlugin = null;
        }
    }
}
