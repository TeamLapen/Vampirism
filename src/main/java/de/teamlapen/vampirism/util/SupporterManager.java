package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.VampirismMod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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
        InputStream inputStream = null;
        try {
            inputStream = new URL(REFERENCE.SUPPORTER_FILE_LINK).openStream();
            supporters = retrieveSupporter(inputStream);
            inputStream.close();
            Logger.t("Loaded supporters from url %s", Arrays.toString(supporters));
        } catch (IOException e) {
            Logger.e(TAG, e, "Failed to retrieve supporters from url");
        }
        if (supporters == null) {
            try {
                inputStream = VampirismMod.class.getResourceAsStream("/supporters.txt");
                supporters = retrieveSupporter(inputStream);
                inputStream.close();
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
