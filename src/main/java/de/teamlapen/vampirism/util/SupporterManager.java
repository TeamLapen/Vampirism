package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.VampirismMod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Handles the download, parsing and access of supporter information
 */
public class SupporterManager {
    private static SupporterManager instance;
    private final static String TAG = "SupporterManager";
    private Supporter[] supporters;

    protected SupporterManager() {
        supporters = new Supporter[]{new Supporter("Steve", "steve")};
    }

    public static SupporterManager getInstance() {
        if (instance == null) {
            instance = new SupporterManager();
        }
        return instance;
    }

    public void initAsync() {
        Thread thread = new Thread(REFERENCE.MODID + ":" + TAG) {

            public void run() {
                init();
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    private void init() {
        Supporter[] supporters = null;
        try {
            supporters = retrieveSupporter(getStreamFromUrl(new URL(REFERENCE.SUPPORTER_FILE_LINK)));
        } catch (IOException e) {
            Logger.e(TAG, e, "Failed to retrieve supporters from url");
        }
        if (supporters == null) {
            try {
                supporters = retrieveSupporter(getStreamFromRes("/supporters.txt"));
            } catch (IOException e) {
                Logger.e(TAG, "Failed to retrieve supporters from resources");
            }
        }
        if (supporters != null) {
            this.supporters = supporters;
        }

    }

    public String getSupporterString() {
        return Arrays.toString(supporters);
    }

    public Supporter getRandom(Random rnd) {
        return supporters[rnd.nextInt(supporters.length)];
    }

    private Supporter[] retrieveSupporter(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        List<Supporter> supporters = new ArrayList<Supporter>();
        try {
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(":");
                if (parts.length != 2) {
                    Logger.w(TAG, "Cannot understand line '%s' -> Skipping", line);
                } else {
                    supporters.add(new Supporter(parts[0], parts[1]));
                }
            }
            reader.close();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return supporters.toArray(new Supporter[supporters.size()]);
    }

    private InputStream getStreamFromUrl(URL url) throws IOException {
        InputStream is = null;
        try {
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", System.getProperty("java.version"));
            connection.connect();

            is = connection.getInputStream();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return is;
    }

    private InputStream getStreamFromRes(String path) {
        return VampirismMod.class.getResourceAsStream(path);
    }

    public class Supporter {
        public final String textureName;
        public final String senderName;

        public Supporter(String senderName, String textureName) {
            if (senderName.equals("null")) {
                senderName = null;
            }
            this.textureName = textureName;
            this.senderName = senderName;
        }

        @Override
        public String toString() {
            return "[" + textureName + " as '" + senderName + "']";
        }
    }
}
