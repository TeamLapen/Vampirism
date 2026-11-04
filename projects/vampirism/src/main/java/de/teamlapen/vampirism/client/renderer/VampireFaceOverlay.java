package de.teamlapen.vampirism.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class VampireFaceOverlay {

    public static final int FACE_U = 8;
    public static final int FACE_V = 8;
    public static final int FACE_SIZE = 8;
    public static final int HALF = FACE_SIZE / 2;
    public static final int TEXTURE_SIZE = 64;

    private static final Map<Identifier, Optional<Data>> CACHE = new HashMap<>();

    private VampireFaceOverlay() {
    }

    public record Box(int x, int y, int width, int height) {
    }

    public record Pair(Box left, Box right) {
        public int width() {
            return Math.max(left.width, right.width);
        }

        public int height() {
            return Math.max(left.height, right.height);
        }
    }

    public record Sprite(Box source, int x, int y) {
    }

    private record Data(@Nullable Box left, @Nullable Box right, Box all) {
    }

    @Nullable
    public static Pair eyes(Identifier texture) {
        Data data = data(texture);
        if (data == null || data.left == null || data.right == null) return null;
        return new Pair(data.left, data.right);
    }

    @Nullable
    public static Box motif(Identifier texture) {
        Data data = data(texture);
        return data == null ? null : data.all;
    }

    @Nullable
    public static List<Sprite> defaultSprites(Identifier texture, boolean eyes) {
        if (eyes) {
            Pair pair = eyes(texture);
            if (pair == null) return null;
            return List.of(new Sprite(pair.left, pair.left.x, pair.left.y), new Sprite(pair.right, pair.right.x, pair.right.y));
        }
        Box motif = motif(texture);
        return motif == null ? null : List.of(new Sprite(motif, motif.x, motif.y));
    }

    @Nullable
    public static List<Sprite> sprites(Identifier texture, boolean eyes, int position) {
        int x = position % FACE_SIZE;
        int y = position / FACE_SIZE;
        if (eyes) {
            Pair pair = eyes(texture);
            if (pair == null) return null;
            x = clamp(x, HALF - pair.width());
            y = clamp(y, FACE_SIZE - pair.height());
            return List.of(new Sprite(pair.left, x, y), new Sprite(pair.right, FACE_SIZE - x - pair.right.width, y));
        }
        Box motif = motif(texture);
        if (motif == null) return null;
        return List.of(new Sprite(motif, clamp(x, FACE_SIZE - motif.width), clamp(y, FACE_SIZE - motif.height)));
    }

    public static int positionAt(Identifier texture, boolean eyes, int x, int y) {
        int width = 1;
        int height = 1;
        if (eyes) {
            Pair pair = eyes(texture);
            if (pair != null) {
                width = pair.width();
                height = pair.height();
            }
            if (x >= HALF) x = FACE_SIZE - x - width;
            x = clamp(x, HALF - width);
        } else {
            Box motif = motif(texture);
            if (motif != null) {
                width = motif.width;
                height = motif.height;
            }
            x = clamp(x, FACE_SIZE - width);
        }
        y = clamp(y, FACE_SIZE - height);
        return y * FACE_SIZE + x;
    }

    private static int clamp(int value, int max) {
        return Math.max(0, Math.min(Math.max(0, max), value));
    }

    public static void clearCache() {
        CACHE.clear();
    }

    @Nullable
    private static Data data(Identifier texture) {
        return CACHE.computeIfAbsent(texture, VampireFaceOverlay::load).orElse(null);
    }

    private static Optional<Data> load(Identifier texture) {
        var resource = Minecraft.getInstance().getResourceManager().getResource(texture);
        if (resource.isEmpty()) return Optional.empty();
        try (InputStream in = resource.get().open(); NativeImage image = NativeImage.read(in)) {
            Box all = bounds(image, 0, FACE_SIZE);
            if (all == null) return Optional.empty();
            return Optional.of(new Data(bounds(image, 0, HALF), bounds(image, HALF, FACE_SIZE), all));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Nullable
    private static Box bounds(NativeImage image, int fromX, int toX) {
        int minX = FACE_SIZE, minY = FACE_SIZE, maxX = -1, maxY = -1;
        for (int y = 0; y < FACE_SIZE; y++) {
            for (int x = fromX; x < toX; x++) {
                int px = FACE_U + x;
                int py = FACE_V + y;
                if (px >= image.getWidth() || py >= image.getHeight()) continue;
                if ((image.getPixel(px, py) >>> 24) != 0) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        if (maxX < 0) return null;
        return new Box(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }
}
