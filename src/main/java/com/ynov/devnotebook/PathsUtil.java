package com.ynov.devnotebook;

import java.nio.file.Path;

/**
 * PathsUtil: utilities for user override directory and file resolution.
 * 
 * Usage examples:
 * - PathsUtil.getOverrideFile("readability") → C:\\Users\\<user>\\.dev-notebook\\readability.html
 * - PathsUtil.getUserOverrideDir() → C:\\Users\\<user>\\.dev-notebook\\
 */
public final class PathsUtil {
    public static final String APP_DIR_NAME = ".dev-notebook";

    private PathsUtil() {}

    public static Path getUserOverrideDir() {
        String home = System.getProperty("user.home");
        return Path.of(home, APP_DIR_NAME);
    }

    public static Path getOverrideFile(String sectionKey) {
        return getUserOverrideDir().resolve(sectionKey + ".html");
    }
}
