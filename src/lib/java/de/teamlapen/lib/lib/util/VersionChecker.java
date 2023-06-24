package de.teamlapen.lib.lib.util;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import de.teamlapen.lib.VampLib;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Version Checker. Not a very good implementation, but it does what it should do.
 * Uses an update file format which is an extension to forge one's, so you can use the same file for forge, but have additional information here e.g. download link.
 * Requires a versioning system, which is like Vampirism's
 */
@Deprecated
public class VersionChecker implements Runnable {
    private final static Logger LOGGER = LogManager.getLogger();
    private static final int MAX_HTTP_REDIRECTS = Integer.getInteger("http.maxRedirects", 20);

    /**
     * Execute an async version check.
     *
     * @param stats if to send very basic stats
     * @return a version info object, which is update when the check is finished
     */
    public static VersionInfo executeVersionCheck(String updateUrl, @NotNull ArtifactVersion currentVersion, boolean stats) {
        VersionChecker checker = new VersionChecker(updateUrl, currentVersion, stats);
        new Thread(checker).start();
        return checker.versionInfo;
    }

    private final boolean stats;
    private final String UPDATE_FILE_URL;
    private final @NotNull VersionInfo versionInfo;
    private final ArtifactVersion currentVersion;

    protected VersionChecker(String update_file_url, @NotNull ArtifactVersion currentVersion, boolean stats) {
        UPDATE_FILE_URL = update_file_url;
        this.currentVersion = currentVersion;
        versionInfo = new VersionInfo(currentVersion);
        this.stats = stats;
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

        InputStream con = openUrlStream(url);
        String data = new String(ByteStreams.toByteArray(con), StandardCharsets.UTF_8);
        con.close();


        @SuppressWarnings("unchecked")
        Map<String, Object> json = new Gson().fromJson(data, Map.class);
        //noinspection unchecked
        Map<String, String> promos = (Map<String, String>) json.get("promos");
        versionInfo.homePage = (String) json.getOrDefault("homepage", "");
        String mcVersion = MCPVersion.getMCVersion();
        String rec = promos.get(mcVersion + "-recommended");
        String lat = promos.get(mcVersion + "-latest");

        Version current = Version.from(currentVersion);
        versionInfo.currentVersion = current;
        Version possibleTarget = null;
        if (current.type == Version.TYPE.RELEASE) {
            if (rec != null) possibleTarget = Version.parse(rec);
        } else {
            if (lat != null) possibleTarget = Version.parse(lat);
        }
        if (possibleTarget == null) {
            LOGGER.info("Did not find a version of type {} for {} ({})", current.type, mcVersion, current.type == Version.TYPE.RELEASE ? rec : lat);
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
                if (ver.compareTo(current) > 0 && ver.compareTo(possibleTarget) < 1) {
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
        //noinspection unchecked
        Map<String, Object> downloads = (Map<String, Object>) json.get("downloads");
        if (downloads != null) {
            //noinspection unchecked
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
            LOGGER.debug("Found new version {}, but in dev", possibleTarget);
        } else {
            LOGGER.info("Found new version {}", possibleTarget);
            versionInfo.newVersion = possibleTarget;
        }


    }

    private @NotNull String getStatsString() {
        return "?" +
                "current=" + URLEncoder.encode(currentVersion.getMajorVersion() + "." + currentVersion.getMinorVersion() + "." + currentVersion.getIncrementalVersion(), StandardCharsets.UTF_8) +
                '&' +
                "mc=" + URLEncoder.encode(MCPVersion.getMCVersion(), StandardCharsets.UTF_8) +
                '&' +
                "count=" + URLEncoder.encode("" + ModList.get().size(), StandardCharsets.UTF_8) +
                '&' +
                "side=" + (EffectiveSide.get() == LogicalSide.CLIENT ? "client" : "server");
    }

    /**
     * Opens stream for given URL while following redirects
     */
    private InputStream openUrlStream(URL url) throws IOException {
        URL currentUrl = url;
        for (int redirects = 0; redirects < MAX_HTTP_REDIRECTS; redirects++) {
            URLConnection c = currentUrl.openConnection();
            if (c instanceof HttpURLConnection huc) {
                huc.setInstanceFollowRedirects(false);
                int responseCode = huc.getResponseCode();
                if (responseCode >= 300 && responseCode <= 399) {
                    try {
                        String loc = huc.getHeaderField("Location");
                        currentUrl = new URL(currentUrl, loc);
                        continue;
                    } finally {
                        huc.disconnect();
                    }
                }
            }

            return c.getInputStream();
        }
        throw new IOException("Too many redirects while trying to fetch " + url);
    }

    /**
     * Holds new version, current version and more
     */
    public static class VersionInfo {
        private Version newVersion;
        private Version currentVersion;
        private boolean checked = false;
        private String homePage = "";

        public VersionInfo(@NotNull ArtifactVersion current) {
            currentVersion = Version.from(current);
        }

        public
        @NotNull
        Version getCurrentVersion() {
            return currentVersion;
        }

        public
        @NotNull
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

        static @NotNull Version from(@NotNull ArtifactVersion version) {
            try {
                String extra = null;
                String qualifier = version.getQualifier();
                TYPE type = TYPE.RELEASE;
                if (qualifier != null) {
                    if (qualifier.contains("alpha")) {
                        type = TYPE.ALPHA;
                        int i = qualifier.indexOf('+');
                        if (i != -1) {
                            extra = qualifier.substring(i + 1);
                        }
                    } else if (qualifier.contains("beta")) {
                        type = TYPE.BETA;
                        int i = qualifier.indexOf('.', qualifier.indexOf("beta"));
                        if (i != -1) {
                            extra = qualifier.substring(i + 1);
                        }
                    } else if (qualifier.contains("test")) {
                        type = TYPE.TEST;
                    }
                }
                return new Version(version.toString(), version.getMajorVersion(), version.getMinorVersion(), version.getIncrementalVersion(), type, extra);
            } catch (Exception e) {
                LOGGER.error("Parsing version failed", e);
                return new Version("unknown", 0, 0, 0, TYPE.ALPHA, null);
            }
        }

        static @NotNull Version parse(@NotNull String s) {
            return from(new DefaultArtifactVersion(s));
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
         */
        @Override
        public int compareTo(@NotNull Version version) {
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
                    int cb = extra == null ? 0 : Integer.parseInt(extra);
                    int nb = version.extra == null ? 0 : Integer.parseInt(version.extra);
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
         * @return If this is a test version
         */
        public boolean isTestVersion() {
            return this.type == TYPE.TEST;
        }

        @Override
        public @NotNull String toString() {
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
         */
        private int compareDate(@NotNull String one, @NotNull String two) {
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
             */
            public int compare(@NotNull TYPE type) {
                return Integer.compare(type.ORDER, this.ORDER);
            }
        }


    }
}
