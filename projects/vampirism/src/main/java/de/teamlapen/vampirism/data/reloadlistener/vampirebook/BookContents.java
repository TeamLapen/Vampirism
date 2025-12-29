package de.teamlapen.vampirism.data.reloadlistener.vampirebook;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.general.IBookContents;
import de.teamlapen.vampirism.common.core.ModVampireBooks;
import net.minecraft.resources.Identifier;

import java.util.List;

public record BookContents(List<String> contents, Identifier background, List<IImageEntry> images) implements IBookContents {

    public static final BookContents EMPTY = new BookContents(List.of(), null, List.of());

    public static final Codec<IBookContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.listOf().fieldOf("contents").forGetter(IBookContents::contents),
            Identifier.CODEC.optionalFieldOf("background", ModVampireBooks.DIARY_BACKGROUND).forGetter(IBookContents::background),
            ImageEntry.CODEC.listOf().optionalFieldOf("images", List.of()).forGetter(IBookContents::images)
    ).apply(instance, BookContents::new));

    public record ImageEntry(int id, Identifier texture, int page, int xOffset, int yOffset, int width, int height) implements IBookContents.IImageEntry {

        public static final Codec<IImageEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("id", 1).forGetter(IImageEntry::id),
                Identifier.CODEC.fieldOf("texture").forGetter(IImageEntry::texture),
                Codec.INT.optionalFieldOf("page", 0).forGetter(IImageEntry::page),
                Codec.INT.optionalFieldOf("xOffset", 0).forGetter(IImageEntry::xOffset),
                Codec.INT.optionalFieldOf("yOffset", 0).forGetter(IImageEntry::yOffset),
                Codec.INT.optionalFieldOf("width", 100).forGetter(IImageEntry::width),
                Codec.INT.optionalFieldOf("height", 100).forGetter(IImageEntry::height)
        ).apply(instance, ImageEntry::new));
    }
}
