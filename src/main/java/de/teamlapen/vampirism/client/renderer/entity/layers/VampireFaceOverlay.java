package de.teamlapen.vampirism.client.renderer.entity.layers;

import org.jetbrains.annotations.Nullable;

/** Pixel bounds of the two motifs in the existing 64x64 player-head overlays. */
public final class VampireFaceOverlay {

    private static final SpritePair[] EYES = {
            symmetricEye(9, 12, 2, 1), symmetricEye(10, 12, 1, 1),
            symmetricEye(9, 11, 2, 2), symmetricEye(9, 12, 1, 1),
            symmetricEye(9, 11, 2, 2), symmetricEye(10, 13, 1, 2),
            symmetricEye(9, 10, 2, 3)
    };

    private static final SpritePair[] FANGS = {
            pair(10, 14, 2, 1, 12, 14, 1, 1),
            pair(10, 14, 2, 1, 12, 14, 2, 1),
            pair(10, 14, 2, 1, 12, 14, 1, 1),
            pair(10, 14, 2, 2, 12, 14, 2, 2),
            pair(10, 14, 2, 2, 13, 14, 1, 2),
            pair(9, 12, 2, 1, 13, 12, 2, 1)
    };

    private VampireFaceOverlay() {}

    @Nullable
    public static SpritePair eye(int type) {
        return type >= 0 && type < EYES.length ? EYES[type] : null;
    }

    @Nullable
    public static SpritePair fang(int type) {
        return type >= 0 && type < FANGS.length ? FANGS[type] : null;
    }

    @Nullable
    public static SpriteBounds fangMotif(int type) {
        SpritePair pair = fang(type);
        if (pair == null) return null;
        int left = Math.min(pair.left().x(), pair.right().x());
        int top = Math.min(pair.left().y(), pair.right().y());
        int right = Math.max(pair.left().x() + pair.left().width(), pair.right().x() + pair.right().width());
        int bottom = Math.max(pair.left().y() + pair.left().height(), pair.right().y() + pair.right().height());
        return new SpriteBounds(left, top, right - left, bottom - top);
    }

    private static SpritePair symmetricEye(int leftX, int top, int width, int height) {
        int faceStart = leftX >= 40 ? 40 : 8;
        int rightX = faceStart + 8 - (leftX - faceStart) - width;
        return pair(leftX, top, width, height, rightX, top, width, height);
    }

    private static SpritePair pair(int leftX, int leftY, int leftWidth, int leftHeight,
                                   int rightX, int rightY, int rightWidth, int rightHeight) {
        return new SpritePair(new SpriteBounds(leftX, leftY, leftWidth, leftHeight),
                new SpriteBounds(rightX, rightY, rightWidth, rightHeight));
    }

    public record SpriteBounds(int x, int y, int width, int height) {}

    public record SpritePair(SpriteBounds left, SpriteBounds right) {
        public int width() {
            return Math.max(this.left.width, this.right.width);
        }

        public int height() {
            return Math.max(this.left.height, this.right.height);
        }
    }
}
