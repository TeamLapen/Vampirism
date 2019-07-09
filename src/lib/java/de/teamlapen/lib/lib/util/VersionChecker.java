package de.teamlapen.lib.lib.util;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import de.teamlapen.lib.VampLib;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Version Checker. Not a very good implementation, but it does what it should do.
 * Uses a update file format which is an extension to forge one's so you can use the same file for forge, but have additional information here e.g. download link.
 * Requires a versioning system, which is like Vampirism's
 */
public class VersionChecker implements Runnable {
    private final static Logger LOGGER = LogManager.getLogger();

    private final boolean stats;

    protected VersionChecker(String update_file_url, String currentVersion, boolean stats) {
        UPDATE_FILE_URL = update_file_url;
        this.currentVersion = currentVersion;
        versionInfo = new VersionInfo(currentVersion);
        if (stats) {
            this.stats = EffectiveSide.get() == LogicalSide.CLIENT ? Minecraft.getInstance().getSnooper().isSnooperRunning() : ServerLifecycleHooks.getCurrentServer().getSnooper().isSnooperRunning();
        } else {
            this.stats = false;
        }
    }

    private final String UPDATE_FILE_URL;
    private final VersionInfo versionInfo;
    private final String currentVersion;

    /**
     * Use the other one
     */
    @Deprecated
    public static VersionInfo executeVersionCheck(String updateUrl, String currentVersion) {
        VersionChecker checker = new VersionChecker(updateUrl, currentVersion, false);
        new Thread(checker).start();
        return checker.versionInfo;
    }

    /**
     * Execute an async version check.
     *
     * @param updateUrl
     * @param currentVersion
     * @param stats if to send very basic stats
     * @return a version info object, which is update when the check is finished
     */
    public static VersionInfo executeVersionCheck(String updateUrl, String currentVersion, boolean stats) {
        VersionChecker checker = new VersionChecker(updateUrl, currentVersion, stats);
        new Thread(checker).start();
        return checker.versionInfo;
    }

    @Override
    public void run() {
        LOGGER.info("Starting version check at {}", UPDATE_FILE_URL);
        String fullUrl = stats ? UPDATE_FILE_URL + getStatsString() : UPDATE_FILE_URL;
        try {
            URL url = new URL(fullUrl);
            check(url);
        } catch (MalformedURLException e) {
            LOGGER.error("Failed to parse update file url ({})", fullUrl);
        } catch (IOException e) {
            if (e instanceof ConnectException) {
                LOGGER.error("Failed to connect to version check url {}", UPDATE_FILE_URL);
            } else {
                LOGGER.error("Failed to perform version check", e);
            }
        } catch (JsonSyntaxException e) {
            LOGGER.error("Failed to parse update file. It seems not well formatted", e);
        }
        versionInfo.checked = true;
    }

    private void check(URL url) throws IOException, JsonSyntaxException {

        InputStream con = (url).openStream();
        String data = new String(ByteStreams.toByteArray(con));
        con.close();


        @SuppressWarnings("unchecked")
        Map<String, Object> json = new Gson().fromJson(data, Map.class);
        Map<String, String> promos = (Map<String, String>) json.get("promos");
        versionInfo.homePage = (String) json.get("homepage");
        String rec = promos.get(MCPVersion.getMCVersion() + "-recommended");
        String lat = promos.get(MCPVersion.getMCVersion() + "-latest");

        Version current = Version.parse(currentVersion);
        if (current == null) {
            LOGGER.warn("Failed to parse current version ({}), aborting version check", currentVersion);
            return;
        }
        versionInfo.currentVersion = current;
        Version possibleTarget = null;
        if (current.type == Version.TYPE.RELEASE) {
            if (rec != null) possibleTarget = Version.parse(rec);
        } else {
            if (lat != null) possibleTarget = Version.parse(lat);
        }
        if (possibleTarget == null) {
            LOGGER.info("Did not find a version of type {} for {} ({})", current.type, MCPVersion.getMCVersion(), current.type == Version.TYPE.RELEASE ? rec : lat);
            return;
        }
        int res = possibleTarget.compareTo(current);
        if (res <= 0) {
            return;
        }

        List<String> changes = new ArrayList<>();
        @SuppressWarnings("unchecked")
        Map<String, String> tmp = (Map<String, String>) json.get(MCPVersion.getMCVersion());
        if (tmp != null) {
            List<Version> ordered = new ArrayList<>();
            for (String key : tmp.keySet()) {
                Version ver = Version.parse(key);
                if (ver != null && ver.compareTo(current) > 0 && (ver.compareTo(possibleTarget) < 1)) {
                    ordered.add(ver);
                }
            }
            Collections.sort(ordered);

            for (Version ver : ordered) {
                if (ver.type == Version.TYPE.RELEASE || current.type != Version.TYPE.RELEASE) {
                    changes.add(ver.name + ":\n" + tmp.get(ver.name));
                }

            }

        } else {
            LOGGER.info("No changelog provided for new version {}", possibleTarget.name);
        }
        possibleTarget.setChanges(changes);
        Map<String, Object> downloads = (Map<String, Object>) json.get("downloads");
        if (downloads != null) {
            Map<String, String> tmp2 = (Map<String, String>) downloads.get(MCPVersion.getMCVersion());
            if (tmp2 != null) {
                String download = tmp2.get(possibleTarget.name);
                if (download != null) {
                    possibleTarget.setUrl(download);
                }
            }
        }
        if (possibleTarget.getUrl() == null) {
            LOGGER.info("No download link provided for new version {}", possibleTarget.name);
        }
        if (VampLib.inDev) {
            LOGGER.trace("Found new version {}, but in dev", possibleTarget);
        } else {
            LOGGER.info("Found new version {}", possibleTarget);
            versionInfo.newVersion = possibleTarget;
        }


    }

    private String getStatsString() {
        try {
            return "?" +
                    "current=" +
                    URLEncoder.encode(currentVersion.trim(), "UTF-8") +
                    '&' +
                    "mc=" +
                    URLEncoder.encode(MCPVersion.getMCVersion(), "UTF-8") +
                    '&' +
                    "count=" +
                    URLEncoder.encode("" + ModList.get().size(), "UTF-8") +
                    '&' +
                    "side=" +
                    (EffectiveSide.get() == LogicalSide.CLIENT ? "client" : "server");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * Holds new version, current version and moar
     */
    public static class VersionInfo {
        private Version newVersion;
        private Version currentVersion;
        private boolean checked = false;
        private String homePage;

        public VersionInfo(String current) {
            currentVersion = Version.parse(current);
            if (currentVersion == null) {
                currentVersion = new Version("current", 0, 0, 0, Version.TYPE.TEST, null);
            }
        }

        public
        @Nonnull
        Version getCurrentVersion() {
            return currentVersion;
        }

        public
        @Nullable
        String getHomePage() {
            return homePage;
        }

        public
        @Nullable
        Version getNewVersion() {
            return newVersion;
        }

        public boolean isChecked() {
            return checked;
        }

        public boolean isNewVersionAvailable() {
            return newVersion != null;
        }

    }

    /**
     * Comparable version descriptor, which can store additional information like download url
     */
    public static class Version implements Comparable<Version> {
        public static
        @Nullable
        Version parse(String version) {
            String name = version;
            try {

                int i = version.indexOf('+');
                String extra = null;
                if (i != -1) {
                    extra = version.substring(i);
                    version = version.substring(0, i);
                }
                i = version.indexOf('.');
                int main = Integer.parseInt(version.substring(0, i));
                version = version.substring(i + 1);
                i = version.indexOf('.');
                int major = Integer.parseInt(version.substring(0, i));
                version = version.substring(i + 1);


                i = version.indexOf('-');
                if (i == -1) i = version.length();
                int minor = Integer.parseInt(version.substring(0, i));
                TYPE type = TYPE.RELEASE;
                if (version.contains("alpha")) {
                    type = TYPE.ALPHA;
                } else if (version.contains("beta")) {
                    type = TYPE.BETA;
                    i = version.indexOf('.', version.indexOf("beta"));
                    extra = version.substring(i + 1);
                } else if (version.contains("test")) {
                    type = TYPE.TEST;
                }
                return new Version(name, main, major, minor, type, extra);
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                LOGGER.error("Failed to parse version {} {}", name, e);
                return null;
            }
        }

        public final String name;
        public final
        @Nullable
        String extra;
        private final TYPE type;
        private final int main, major, minor;
        private String url;
        private List<String> changes;

        public Version(String name, int main, int major, int minor, TYPE type, @Nullable String extra) {
            this.name = name;
            this.main = main;
            this.major = major;
            this.minor = minor;
            this.type = type;
            this.extra = extra;
        }

        /**
         * 1 if the given version is older, 0 if they are equal and -1 if the given version is newer
         *
         * @param version
         * @return
         */
        @Override
        public int compareTo(@Nonnull Version version) {
            if (version.main > this.main) return -1;
            if (version.main < this.main) return 1;
            if (version.major > this.major) return -1;
            if (version.major < this.major) return 1;
            if (version.minor > this.minor) return -1;
            if (version.minor < this.minor) return 1;
            int i = this.type.compare(version.type);
            if (i != 0) return i;
            if (type == TYPE.BETA) {
                try {
                    int cb = Integer.parseInt(extra);
                    int nb = Integer.parseInt(version.extra);
                    if (nb > cb) return -1;
                    if (nb < cb) return 1;
                } catch (NumberFormatException e) {
                    LOGGER.error("Failed to parse beta number ({}) {}", extra, e);
                }
                return 0;
            } else if (type == TYPE.ALPHA) {
                if (this.extra != null && version.extra != null) return compareDate(this.extra, version.extra);
            }
            return 0;
        }

        public List<String> getChanges() {
            return changes;
        }

        public void setChanges(List<String> changes) {
            this.changes = changes;
        }

        public
        @Nullable
        String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * @return If this is an alpha or test version
         */
        public boolean isTestVersion() {
            return type == TYPE.ALPHA || type == TYPE.TEST;
        }

        @Override
        public String toString() {
            return "Version{" +
                    "main=" + main +
                    ", major=" + major +
                    ", minor=" + minor +
                    ", type=" + type +
                    ", name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }

        /**
         * two if the given version is older, 0 if they are equal and -1 if the two is newer
         *
         * @return
         */
        private int compareDate(String one, String two) {
            try {
                String[] ones = one.split("-");
                String[] twos = two.split("-");
                for (int i = 0; i < ones.length && i < twos.length; i++) {
                    int o = Integer.parseInt(ones[i]);
                    int t = Integer.parseInt(twos[i]);
                    if (t > o) return -1;
                    if (t < o) return 1;
                }
            } catch (Exception e) {
                LOGGER.error("Failed to parse date {}/{} {}", one, two, e);
            }
            return 0;
        }

        enum TYPE {
            RELEASE(0), BETA(1), ALPHA(2), TEST(3);
            public final int ORDER;

            TYPE(int pos) {
                this.ORDER = pos;
            }

            /**
             * 1 if the given type is less recommend, 0 if equal, -1 if the given type is more recommend
             *
             * @param type
             * @return
             */
            public int compare(TYPE type) {
                if (type.ORDER < this.ORDER) return -1;
                if (type.ORDER > this.ORDER) return 1;
                return 0;
            }
        }


    }
}
