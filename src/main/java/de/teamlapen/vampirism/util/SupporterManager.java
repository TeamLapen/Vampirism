package de.teamlapen.vampirism.util;

import com.google.common.io.ByteStreams;
import com.google.gson.*;
import de.teamlapen.vampirism.VampirismMod;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;

/**
 * Handles the download, parsing and access of supporter information
 */
public class SupporterManager {
    private final static String TAG = "SupporterManager";
    private static SupporterManager instance = new SupporterManager();

    public static SupporterManager getInstance() {
        return instance;
    }

    private Supporter[][] supporters;

    private SupporterManager() {
        supporters = new Supporter[2][0];

    }

    public String getDebugString() {
        return "Vampires: " + Arrays.toString(supporters[0]) + " Hunters: " + Arrays.toString(supporters[1]);
    }

    /**
     * Returns a randomly picked hunter
     *
     * @param rnd
     * @return
     */
    public Supporter getRandomHunter(Random rnd) {
        if (supporters[1].length > 0) {
            return supporters[1][rnd.nextInt(supporters[1].length)];
        }
        return new Supporter(null, null, 0);
    }

    /**
     * Returns a randomly picked vampire
     *
     * @param rnd
     * @return
     */
    public Supporter getRandomVampire(Random rnd) {
        if (supporters[0].length > 0) {
            return supporters[0][rnd.nextInt(supporters[0].length)];
        }
        return new Supporter(null, null, 0);
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
        Supporter[][] supporters = null;
        InputStream inputStream = null;
        try {
            inputStream = new URL(REFERENCE.SUPPORTER_FILE).openStream();
            String data = new String(ByteStreams.toByteArray(inputStream));

            inputStream.close();
            supporters = retrieveSupporter(data);
        } catch (IOException e) {
            VampirismMod.log.e(TAG, e, "Failed to retrieve supporters from url");
        } finally {
        }
        if (supporters == null) {
            try {
                inputStream = VampirismMod.class.getResourceAsStream("/supporters.json");
                String data = new String(ByteStreams.toByteArray(inputStream));

                inputStream.close();
                supporters = retrieveSupporter(data);
            } catch (IOException e) {
                VampirismMod.log.e(TAG, e, "Failed to retrieve supporters from resources");
            }
        }
        if (supporters != null) {
            this.supporters = supporters;
            VampirismMod.log.t("Supporters %s", getDebugString());
        }
    }

    private Supporter parseSupporter(JsonObject object) {
        String name = null;
        String texture = null;
        int type = 0;
        if (object.has("name")) {
            name = object.get("name").getAsString();
        }
        if (object.has("texture")) {
            texture = object.get("texture").getAsString();
        }
        if (object.has("type")) {
            type = object.get("type").getAsInt();
        }
        return new Supporter(name, texture, type);
    }

    @SuppressWarnings("unchecked")
    private Supporter[][] retrieveSupporter(String data) {

        try {
            Supporter[][] supporters = new Supporter[2][];
            JsonElement main = new JsonParser().parse(data);
            JsonArray vampires = main.getAsJsonObject().getAsJsonArray("vampires");
            supporters[0] = new Supporter[vampires.size()];
            for (int i = 0; i < supporters[0].length; i++) {
                supporters[0][i] = parseSupporter(vampires.get(i).getAsJsonObject());
            }
            JsonArray hunters = main.getAsJsonObject().getAsJsonArray("hunters");
            supporters[1] = new Supporter[hunters.size()];
            for (int i = 0; i < supporters[1].length; i++) {
                supporters[1][i] = parseSupporter(hunters.get(i).getAsJsonObject());
            }
            return supporters;
        } catch (JsonSyntaxException e) {
            VampirismMod.log.e(TAG, e, "Failed to parse supporter list");
        }
        return null;

    }

    public class Supporter {
        public
        @Nullable
        final String textureName;
        public
        @Nullable
        final String senderName;
        public final int typeId;

        public Supporter(@Nullable String senderName, @Nullable String textureName, int typeId) {
            this.typeId = typeId;
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
