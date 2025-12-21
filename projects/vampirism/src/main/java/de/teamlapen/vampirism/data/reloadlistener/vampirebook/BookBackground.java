package de.teamlapen.vampirism.data.reloadlistener.vampirebook;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.general.IBookBackground;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record BookBackground(Identifier texture, Optional<Identifier> textureFirstPage, Optional<Identifier> textureLastPage, boolean twoPages, int textureWidth, int textureHeight, TextProperties textProperties, PageNumbering pageNumbering) implements IBookBackground {

    public static final Codec<BookBackground> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("texture").forGetter(BookBackground::texture),
            Identifier.CODEC.optionalFieldOf("textureFirstPage").forGetter(BookBackground::textureFirstPage),
            Identifier.CODEC.optionalFieldOf("textureLastPage").forGetter(BookBackground::textureLastPage),
            Codec.BOOL.optionalFieldOf("twoPages", true).forGetter(BookBackground::twoPages),
            Codec.INT.fieldOf("textureWidth").forGetter(BookBackground::textureWidth),
            Codec.INT.fieldOf("textureHeight").forGetter(BookBackground::textureHeight),
            TextProperties.CODEC.optionalFieldOf("textProperties", TextProperties.DEFAULT).forGetter(BookBackground::textProperties),
            PageNumbering.CODEC.optionalFieldOf("pageNumbering", PageNumbering.DEFAULT).forGetter(BookBackground::pageNumbering)
    ).apply(instance, BookBackground::new));

    public record TextProperties(int textColor, int textWidth, int textHeight, int firstPageTextX, int leftPageTextX, int rightPageTextX, int textY) {

        public static final TextProperties DEFAULT = new TextProperties(0x362511, 134, 150, 156, 20, 160, 16);

        public static final Codec<TextProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("textColor", DEFAULT.textColor()).forGetter(TextProperties::textColor),
                Codec.INT.optionalFieldOf("textWidth", DEFAULT.textWidth()).forGetter(TextProperties::textWidth),
                Codec.INT.optionalFieldOf("textHeight", DEFAULT.textHeight()).forGetter(TextProperties::textHeight),
                Codec.INT.optionalFieldOf("firstPageTextX", DEFAULT.firstPageTextX()).forGetter(TextProperties::firstPageTextX),
                Codec.INT.optionalFieldOf("leftPageTextX", DEFAULT.leftPageTextX()).forGetter(TextProperties::leftPageTextX),
                Codec.INT.optionalFieldOf("rightPageTextX", DEFAULT.rightPageTextX()).forGetter(TextProperties::rightPageTextX),
                Codec.INT.optionalFieldOf("textY", DEFAULT.textY()).forGetter(TextProperties::textY)
        ).apply(instance, TextProperties::new));
    }

    public record PageNumbering(int pageNumberXOffset, int pageNumberYOffset, int pageButtonXOffset, int pageButtonYOffset) {

        public static final PageNumbering DEFAULT = new PageNumbering(79, 22, 22, 12);

        public static final Codec<PageNumbering> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("pageNumberXOffset", DEFAULT.pageNumberXOffset()).forGetter(PageNumbering::pageNumberXOffset),
                Codec.INT.optionalFieldOf("pageNumberYOffset", DEFAULT.pageNumberYOffset()).forGetter(PageNumbering::pageNumberYOffset),
                Codec.INT.optionalFieldOf("pageButtonXOffset", DEFAULT.pageButtonXOffset()).forGetter(PageNumbering::pageButtonXOffset),
                Codec.INT.optionalFieldOf("pageButtonYOffset", DEFAULT.pageButtonYOffset()).forGetter(PageNumbering::pageButtonYOffset)
        ).apply(instance, PageNumbering::new));
    }

    @Override
    public int textColor() {
        return this.textProperties.textColor;
    }

    @Override
    public int textWidth() {
        return this.textProperties.textWidth;
    }

    @Override
    public int textHeight() {
        return this.textProperties.textHeight;
    }

    @Override
    public int firstPageTextX() {
        return this.textProperties.firstPageTextX;
    }

    @Override
    public int leftPageTextX() {
        return this.textProperties.leftPageTextX;
    }

    @Override
    public int rightPageTextX() {
        return this.textProperties.rightPageTextX;
    }

    @Override
    public int textY() {
        return this.textProperties.textY;
    }

    @Override
    public int pageNumberXOffset() {
        return this.pageNumbering.pageNumberXOffset;
    }

    @Override
    public int pageNumberYOffset() {
        return this.pageNumbering.pageNumberYOffset;
    }

    @Override
    public int pageButtonXOffset() {
        return this.pageNumbering.pageButtonXOffset;
    }

    @Override
    public int pageButtonYOffset() {
        return this.pageNumbering.pageButtonYOffset;
    }

    public static Builder builder(Identifier texture, int textureWidth, int textureHeight) {
        return new Builder(texture, textureWidth, textureHeight);
    }

    @SuppressWarnings("unused")
    public static class Builder {
        private final Identifier texture;
        private final int textureWidth;
        private final int textureHeight;

        private @Nullable Identifier textureFirstPage = null;
        private @Nullable Identifier textureLastPage = null;
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

        public Builder(Identifier texture, int textureWidth, int textureHeight) {
            this.texture = texture;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
        }

        public Builder textureFirstPage(Identifier textureFirstPage) {
            this.textureFirstPage = textureFirstPage;
            return this;
        }

        public Builder textureLastPage(Identifier textureLastPage) {
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
            return new BookBackground(this.texture, this.textureFirstPage != null ? Optional.of(this.textureFirstPage) : Optional.empty(), this.textureLastPage != null ? Optional.of(this.textureLastPage) : Optional.empty(), this.twoPages, this.textureWidth, this.textureHeight, new TextProperties(this.textColor, this.textWidth, this.textHeight, this.firstPageTextX, this.leftPageTextX, this.rightPageTextX, this.textY), new PageNumbering(this.pageNumberXOffset, this.pageNumberYOffset, this.pageButtonXOffset, this.pageButtonYOffset));
        }
    }
}
