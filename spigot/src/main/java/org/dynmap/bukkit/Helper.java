package org.dynmap.bukkit;

import java.lang.reflect.Constructor;

import org.bukkit.Bukkit;
import org.dynmap.Log;
import org.dynmap.bukkit.helper.BukkitVersionHelper;

public class Helper {

    private static BukkitVersionHelper loadVersionHelper(String classname) {
        try {
            Class<?> c = Class.forName(classname);
            Constructor<?> cons = c.getConstructor();
            return (BukkitVersionHelper) cons.newInstance();
        } catch (Exception x) {
            Log.severe("Error loading " + classname, x);
            return null;
        }
    }

    public static BukkitVersionHelper getHelper() {
        if (BukkitVersionHelper.helper != null) {
            return BukkitVersionHelper.helper;
        }

        String version = Bukkit.getServer().getVersion();
        Log.info("version=" + version);

        if (version.contains("MCPC")) {
            logUnsupportedPlatform("MCPC-Plus");
            return null;
        }

        if (version.contains("BukkitForge")) {
            logUnsupportedPlatform("BukkitForge");
            return null;
        }

        if (Bukkit.getServer().getClass().getName().contains("GlowServer")) {
            Log.info("Loading Glowstone support");
            BukkitVersionHelper.helper = loadVersionHelper("org.dynmap.bukkit.helper.BukkitVersionHelperGlowstone");
            return BukkitVersionHelper.helper;
        }

        String mcVersion = extractMcVersion(version);
        String helperClass = buildHelperClassName(mcVersion);
        BukkitVersionHelper.helper = loadVersionHelper(helperClass);

        return BukkitVersionHelper.helper;
    }

    private static String extractMcVersion(String version) {
        int start = version.indexOf("(MC: ") + 5;
        int end = version.indexOf(")", start);
        return version.substring(start, end);
    }

    private static String buildHelperClassName(String mcVersion) {
        String[] parts = mcVersion.split("\\.");
        String major = parts[0];
        String minor = parts.length > 1 ? parts[1] : "0";
        String patch = parts.length > 2 ? parts[2] : "";

        String versionSuffix = major + minor + (patch.isEmpty() ? "" : "_" + patch);

        return "org.dynmap.bukkit.helper.v" + versionSuffix +
                ".BukkitVersionHelperSpigot" + versionSuffix;
    }

    private static void logUnsupportedPlatform(String platform) {
        Log.severe("*********************************************************************************");
        Log.severe("* " + platform + " is no longer supported via the Bukkit version of Dynmap." + spaces(52 - platform.length()) + "*");
        Log.severe("* Install the appropriate Forge version of Dynmap.                              *");
        Log.severe("* Add the DynmapCBBridge plugin to enable support for Dynmap-compatible plugins *");
        Log.severe("*********************************************************************************");
    }

    private static String spaces(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }
}
