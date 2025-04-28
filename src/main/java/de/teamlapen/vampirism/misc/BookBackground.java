package de.teamlapen.vampirism.misc;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.api.general.IBookBackground;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import static de.teamlapen.vampirism.util.VampirismGsonHelper.*;

public record BookBackground(ResourceLocation texture, @Nullable ResourceLocation textureFirstPage, @Nullable ResourceLocation textureLastPage, boolean twoPages, int textureWidth, int textureHeight, int textColor, int textWidth, int textHeight, int firstPageTextX, int leftPageTextX, int rightPageTextX, int textY, int pageNumberXOffset, int pageNumberYOffset, int pageButtonXOffset, int pageButtonYOffset) implements IBookBackground {

    public JsonObject encode() {
        JsonObject json = new JsonObject();

        json.addProperty("texture", this.texture.toString());
        if (this.textureFirstPage != null) {
            json.addProperty("textureFirstPage", this.textureFirstPage.toString());
        }
        if (this.textureLastPage != null) {
            json.addProperty("textureLastPage", this.textureLastPage.toString());
        }
        addProperty(json, "twoPages", this.twoPages, true);
        json.addProperty("textureWidth", this.textureWidth);
        json.addProperty("textureHeight", this.textureHeight);

        addProperty(json, "textColor", this.textColor, 0x362511);
        addProperty(json, "textWidth", this.textWidth, 134);
        addProperty(json, "textHeight", this.textHeight, 150);
        addProperty(json, "firstPageTextX", this.firstPageTextX, 156);
        addProperty(json, "leftPageTextX", this.leftPageTextX, 20);
        addProperty(json, "rightPageTextX", this.rightPageTextX, 160);
        addProperty(json, "textY", this.textY, 16);
        addProperty(json, "pageNumberXOffset", this.pageNumberXOffset, 79);
        addProperty(json, "pageNumberYOffset", this.pageNumberYOffset, 22);
        addProperty(json, "pageButtonXOffset", this.pageButtonXOffset, 22);
        addProperty(json, "pageButtonYOffset", this.pageButtonYOffset, 12);

        return json;
    }

    public static BookBackground decode(JsonObject json) {
        return new BookBackground(
                getAsResourceLocation(json, "texture"),
                getAsResourceLocation(json, "textureFirstPage", null),
                getAsResourceLocation(json, "textureLastPage", null),
                getAsBoolean(json, "twoPages", true),
                getAsInt(json, "textureWidth"),
                getAsInt(json, "textureHeight"),
                getAsInt(json, "textColor", 0x362511),
                getAsInt(json, "textWidth", 134),
                getAsInt(json, "textHeight", 150),
                getAsInt(json, "firstPageTextX", 156),
                getAsInt(json, "leftPageTextX", 20),
                getAsInt(json, "rightPageTextX", 160),
                getAsInt(json, "textY", 16),
                getAsInt(json, "pageNumberXOffset", 79),
                getAsInt(json, "pageNumberYOffset", 22),
                getAsInt(json, "pageButtonXOffset", 22),
                getAsInt(json, "pageButtonYOffset", 12)
        );
    }

    public static Builder builder(ResourceLocation texture, int textureWidth, int textureHeight) {
        return new Builder(texture, textureWidth, textureHeight);
    }

    @SuppressWarnings("unused")
    public static class Builder {
        private final ResourceLocation texture;
        private final int textureWidth;
        private final int textureHeight;

        private @Nullable ResourceLocation textureFirstPage = null;
        private @Nullable ResourceLocation textureLastPage = null;
        private boolean twoPages = true;
        private int textColor = 0x362511;
        private int textWidth = 134;
        private int textHeight = 150;
        private int firstPageTextX = 156;
        private int leftPageTextX = 20;
        private int rightPageTextX = 160;
        private int textY = 16;
        private int pageNumberXOffset = 79;
        private int pageNumberYOffset = 22;
        private int pageButtonXOffset = 22;
        private int pageButtonYOffset = 12;

        public Builder(ResourceLocation texture, int textureWidth, int textureHeight) {
            this.texture = texture;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
        }

        public Builder textureFirstPage(ResourceLocation textureFirstPage) {
            this.textureFirstPage = textureFirstPage;
            return this;
        }

        public Builder textureLastPage(ResourceLocation textureLastPage) {
            this.textureLastPage = textureLastPage;
            return this;
        }

        public Builder twoPages(boolean twoPages) {
            this.twoPages = twoPages;
            return this;
        }

        public Builder textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder textWidth(int textWidth) {
            this.textWidth = textWidth;
            return this;
        }

        public Builder textHeight(int textHeight) {
            this.textHeight = textHeight;
            return this;
        }

        public Builder firstPageTextX(int firstPageTextX) {
            this.firstPageTextX = firstPageTextX;
            return this;
        }

        public Builder leftPageTextX(int leftPageTextX) {
            this.leftPageTextX = leftPageTextX;
            return this;
        }

        public Builder rightPageTextX(int rightPageTextX) {
            this.rightPageTextX = rightPageTextX;
            return this;
        }

        public Builder textY(int textY) {
            this.textY = textY;
            return this;
        }

        public Builder pageNumberXOffset(int pageNumberXOffset) {
            this.pageNumberXOffset = pageNumberXOffset;
            return this;
        }

        public Builder pageNumberYOffset(int pageNumberYOffset) {
            this.pageNumberYOffset = pageNumberYOffset;
            return this;
        }

        public Builder pageButtonXOffset(int pageButtonXOffset) {
            this.pageButtonXOffset = pageButtonXOffset;
            return this;
        }

        public Builder pageButtonYOffset(int pageButtonYOffset) {
            this.pageButtonYOffset = pageButtonYOffset;
            return this;
        }

        public BookBackground build() {
            return new BookBackground(this.texture, this.textureFirstPage, this.textureLastPage, this.twoPages, this.textureWidth, this.textureHeight, this.textColor, this.textWidth, this.textHeight, this.firstPageTextX, this.leftPageTextX, this.rightPageTextX, this.textY, this.pageNumberXOffset, this.pageNumberYOffset, this.pageButtonXOffset, this.pageButtonYOffset);
        }
    }
}
