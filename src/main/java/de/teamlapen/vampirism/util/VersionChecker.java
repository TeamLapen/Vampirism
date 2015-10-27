package de.teamlapen.vampirism.util;

import com.google.gson.Gson;
import cpw.mods.fml.common.Loader;
import de.teamlapen.vampirism.VampirismMod;
import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.annotation.Nullable;

import java.net.URL;
import java.util.List;

/**
 * VersionChecker which can be used when the mod "Version Checker" by @Dynious is not installed
 */
public class VersionChecker implements Runnable {

    private static VersionChecker instance = new VersionChecker();
    private static final String TAG = "VersionChecker";
    /**
     * If a new version is available it is stored here
     */
    public static
    @Nullable
    VersionContainer.Version newVersion = null;

    public static void execute() {
        new Thread(instance).start();
    }

    public static String addVersionInfo(String template) {
        return template.replaceAll("@download@", VersionChecker.newVersion.getUpdateURL()).replaceAll("@forum@", "http://teamlapen.de/projects/vampirism");
    }

    @Override
    public void run() {
        try {
            Gson gson = new Gson();
            String json = IOUtils.toString(new URL(REFERENCE.UPDATE_FILE_LINK));

            VersionContainer versionContainer = gson.fromJson(json, VersionContainer.class);
            VersionContainer.Version latest = versionContainer.getLatestFromMcVersion(Loader.instance().getMCVersionString());

            if (latest != null) {
                if (latest.getModVersion().equalsIgnoreCase(REFERENCE.VERSION)) {
                    Logger.d(TAG, "%s is up-to-date", REFERENCE.MODID);
                } else if (VampirismMod.inDev) {
                    Logger.d(TAG, "In dev, but the newest offical version of %s is %s", REFERENCE.MODID, latest.getModVersion());
                } else {
                    Logger.d(TAG, "%s (%s) is out-of-date. A new version (%s) is available", REFERENCE.MODID, REFERENCE.VERSION, latest.getModVersion());
                    newVersion = latest;
                }
            } else {
                Logger.d(TAG, "Did not find a %s version for this Minecraft Version (%s)", REFERENCE.MODID, Loader.instance().getMCVersionString());
            }
        } catch (Exception e) {
            Logger.e(TAG, e, "Failed to retrieve the newest version of %s", REFERENCE.MODID);
        }
    }


    /**
     * VersionContainer by @Dynious, https://github.com/Dynious/VersionChecker/blob/master/src/main/java/com/dynious/versionchecker/api/VersionContainer.java
     * Included here to be used when Version Checker is not installed.
     *
     * @author Dynious
     */
    public class VersionContainer {
        public List<Version> versionList;

        public VersionContainer(List<Version> versionList) {
            this.versionList = versionList;
        }

        public Version getLatestFromMcVersion(String McVersion) {
            for (Version version : versionList) {
                if (doStringsMatch(McVersion, version.getMcVersion())) {
                    return version;
                }
            }
            return null;
        }

        public class Version {
            private String mcVersion;
            private String modVersion;
            private List<String> changeLog;
            private String updateURL;
            private boolean isDirectLink;
            private String newFileName;

            private Version(String mcVersion, String modVersion, List<String> changeLog, String updateURL, boolean isDirectLink, String newFileName) {
                this.mcVersion = mcVersion;
                this.modVersion = modVersion;
                this.changeLog = changeLog;
                this.updateURL = updateURL;
                this.isDirectLink = isDirectLink;
                this.newFileName = newFileName;
            }

            public String getMcVersion() {
                return mcVersion;
            }

            public String getModVersion() {
                return modVersion;
            }

            public List<String> getChangeLog() {
                return changeLog;
            }

            public String getUpdateURL() {
                return updateURL;
            }
//        Unused
//        public boolean isDirectLink()
//        {
//            return isDirectLink;
//        }
//
//        public String getNewFileName()
//        {
//            return newFileName;
//        }
        }

        /**
         * https://github.com/Dynious/VersionChecker/blob/master/src/main/java/com/dynious/versionchecker/helper/MatchHelper.java
         *
         * @param first
         * @param second
         * @return
         */
        private boolean doStringsMatch(String first, String second) {
            if (first.startsWith("Minecraft "))
                first = first.substring("Minecraft ".length());

            if (second.startsWith("Minecraft "))
                second = second.substring("Minecraft ".length());

            if (first.equals(second))
                return true;

            String[] firstTokens = first.split("\\.");
            String[] secondTokens = second.split("\\.");

            String[] mostTokens = firstTokens.length > secondTokens.length ? firstTokens : secondTokens;
            String[] leastTokens = mostTokens == firstTokens ? secondTokens : firstTokens;

            if (firstTokens.length != secondTokens.length && !isVersionWildcardPattern(leastTokens[leastTokens.length - 1]))
                return false;

            for (int i = 0; i < leastTokens.length; i++) {
                if (!leastTokens[i].equals(mostTokens[i]) && !(isVersionWildcardPattern(leastTokens[i]) || isVersionWildcardPattern(mostTokens[i])))
                    return false;
            }

            return true;
        }

        /**
         * https://github.com/Dynious/VersionChecker/blob/master/src/main/java/com/dynious/versionchecker/helper/MatchHelper.java
         *
         * @param str
         * @return
         */
        private boolean isVersionWildcardPattern(String str) {
            return str.equals("*") || str.equals("x");
        }
    }
}
