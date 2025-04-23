package de.teamlapen.vampirism.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.components.IVampireBook;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.ModVampireBooks;
import de.teamlapen.vampirism.core.tags.ModVampireBookTags;
import de.teamlapen.vampirism.util.VampireBookLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record VampireBook(ResourceLocation id, Component author, ResourceLocation backgroundId) implements IVampireBook {

    public static final MutableComponent UNKNOWN_AUTHOR = Component.translatable("vampire_book.vampirism.unknown.author");

    public static final VampireBook EMPTY = new VampireBook(VResourceLocation.mod("unknown"), UNKNOWN_AUTHOR, ModVampireBooks.DIARY_BACKGROUND);

    public static final Codec<IVampireBook> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(IVampireBook::id),
            ComponentSerialization.CODEC.optionalFieldOf("author", UNKNOWN_AUTHOR).forGetter(IVampireBook::author),
            ResourceLocation.CODEC.optionalFieldOf("backgroundId", ModVampireBooks.DIARY_BACKGROUND).forGetter(IVampireBook::backgroundId)
            ).apply(instance, VampireBook::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, IVampireBook> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, IVampireBook::id,
            ComponentSerialization.STREAM_CODEC, IVampireBook::author,
            ResourceLocation.STREAM_CODEC, IVampireBook::backgroundId,
            VampireBook::new
    );

    public static void addToStack(ItemStack stack, IVampireBook vampireBook) {
        stack.set(ModDataComponents.VAMPIRE_BOOK.get(), vampireBook);
    }

    public static @NotNull IVampireBook get(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.VAMPIRE_BOOK.get(), EMPTY);
    }

    public boolean is(TagKey<IVampireBook> tag, RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(VampirismRegistries.Keys.VAMPIRE_BOOK).wrapAsHolder(this).is(tag);
    }

    @Override
    public boolean isEmpty() {
        return this == VampireBook.EMPTY;
    }

    /**
     * @return A random non-unique vampire book
     */
    public static IVampireBook getRandomBook(@Nullable TagKey<IVampireBook> tag, LootContext context) {
        RegistryAccess registryAccess = context.getLevel().registryAccess();
        Registry<IVampireBook> registry = registryAccess.lookupOrThrow(VampirismRegistries.Keys.VAMPIRE_BOOK);
        Stream<IVampireBook> stream = registry.stream().filter(vampireBook -> !vampireBook.is(ModVampireBookTags.NON_TREASURE, registryAccess));

        if (tag != null) {
            stream = stream.filter(vampireBook -> vampireBook.is(tag, registryAccess));
        }

        List<IVampireBook> list = stream.toList();
        return list.isEmpty() ? EMPTY : list.get(context.getRandom().nextInt(0, list.size()));
    }

    public MutableComponent title() {
        return Component.translatable("vampire_book." + id().toLanguageKey());
    }

    public List<MutableComponent> contents() {
        return VampireBookLoader.loadBookContents(this).stream().map(Component::literal).toList();
    }

    public IBookBackground background() {
        return VampireBookLoader.loadBackground(backgroundId);
    }

    public static VampireBook.Builder builder(ResourceKey<IVampireBook> id) {
        return new VampireBook.Builder(id);
    }

    public record BookBackground(ResourceLocation texture, Optional<ResourceLocation> textureFirstPage, Optional<ResourceLocation> textureLastPage, boolean twoPages, int textureWidth, int textureHeight, IBookTextProperties textProperties, IBookPageNumbering pageNumbering) implements IBookBackground {

        public static final BookBackground DEFAULT = new BookBackground(VResourceLocation.mod("textures/gui/diary.png"), Optional.of(VResourceLocation.mod("textures/gui/diary_first.png")), Optional.of(VResourceLocation.mod("textures/gui/diary_last.png")), true, 304, 200, BookTextProperties.DEFAULT, BookPageNumbering.DEFAULT);

        public static final Codec<IBookBackground> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(IBookBackground::texture),
                ResourceLocation.CODEC.optionalFieldOf("textureFirstPage").forGetter(IBookBackground::textureFirstPage),
                ResourceLocation.CODEC.optionalFieldOf("textureLastPage").forGetter(IBookBackground::textureLastPage),
                Codec.BOOL.fieldOf("twoPages").forGetter(IBookBackground::twoPages),
                Codec.INT.fieldOf("textureWidth").forGetter(IBookBackground::textureWidth),
                Codec.INT.fieldOf("textureHeight").forGetter(IBookBackground::textureHeight),
                BookTextProperties.CODEC.optionalFieldOf("textProperties", BookTextProperties.DEFAULT).forGetter(IBookBackground::textProperties),
                BookPageNumbering.CODEC.optionalFieldOf("pageNumbering", BookPageNumbering.DEFAULT).forGetter(IBookBackground::pageNumbering)
        ).apply(instance, BookBackground::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, IBookBackground> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, IBookBackground::texture,
                ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), IBookBackground::textureFirstPage,
                ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), IBookBackground::textureLastPage,
                ByteBufCodecs.BOOL, IBookBackground::twoPages,
                ByteBufCodecs.INT, IBookBackground::textureWidth,
                ByteBufCodecs.INT, IBookBackground::textureHeight,
                BookTextProperties.STREAM_CODEC, IBookBackground::textProperties,
                BookPageNumbering.STREAM_CODEC, IBookBackground::pageNumbering,
                BookBackground::new
        );
    }

    public record BookTextProperties(int textColor, int textWidth, int textHeight, int firstPageTextX, int leftPageTextX, int rightPageTextX, int textY) implements IBookTextProperties {

        public static final BookTextProperties DEFAULT = new BookTextProperties(0x362511, 134, 150, 156, 20, 160, 16);

        public static final Codec<IBookTextProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("textColor", DEFAULT.textColor()).forGetter(IBookTextProperties::textColor),
                Codec.INT.optionalFieldOf("textWidth", DEFAULT.textWidth()).forGetter(IBookTextProperties::textWidth),
                Codec.INT.optionalFieldOf("textHeight", DEFAULT.textHeight()).forGetter(IBookTextProperties::textHeight),
                Codec.INT.optionalFieldOf("firstPageTextX", DEFAULT.firstPageTextX()).forGetter(IBookTextProperties::firstPageTextX),
                Codec.INT.optionalFieldOf("leftPageTextX", DEFAULT.leftPageTextX()).forGetter(IBookTextProperties::leftPageTextX),
                Codec.INT.optionalFieldOf("rightPageTextX", DEFAULT.rightPageTextX()).forGetter(IBookTextProperties::rightPageTextX),
                Codec.INT.optionalFieldOf("textY", DEFAULT.textY()).forGetter(IBookTextProperties::textY)
        ).apply(instance, BookTextProperties::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, IBookTextProperties> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, IBookTextProperties::textColor,
                ByteBufCodecs.INT, IBookTextProperties::textWidth,
                ByteBufCodecs.INT, IBookTextProperties::textHeight,
                ByteBufCodecs.INT, IBookTextProperties::firstPageTextX,
                ByteBufCodecs.INT, IBookTextProperties::leftPageTextX,
                ByteBufCodecs.INT, IBookTextProperties::rightPageTextX,
                ByteBufCodecs.INT, IBookTextProperties::textY,
                BookTextProperties::new
        );
    }

    public record BookPageNumbering(int pageNumberXOffset, int pageNumberYOffset, int pageButtonXOffset, int pageButtonYOffset) implements IBookPageNumbering {

        public static final BookPageNumbering DEFAULT = new BookPageNumbering(79, 22, 22, 12);

        public static final Codec<IBookPageNumbering> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("pageNumberXOffset", DEFAULT.pageNumberXOffset()).forGetter(IBookPageNumbering::pageNumberXOffset),
                Codec.INT.optionalFieldOf("pageNumberYOffset", DEFAULT.pageNumberYOffset()).forGetter(IBookPageNumbering::pageNumberYOffset),
                Codec.INT.optionalFieldOf("pageButtonXOffset", DEFAULT.pageButtonXOffset()).forGetter(IBookPageNumbering::pageButtonXOffset),
                Codec.INT.optionalFieldOf("pageButtonYOffset", DEFAULT.pageButtonYOffset()).forGetter(IBookPageNumbering::pageButtonYOffset)
        ).apply(instance, BookPageNumbering::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, IBookPageNumbering> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, IBookPageNumbering::pageNumberXOffset,
                ByteBufCodecs.INT, IBookPageNumbering::pageNumberYOffset,
                ByteBufCodecs.INT, IBookPageNumbering::pageButtonXOffset,
                ByteBufCodecs.INT, IBookPageNumbering::pageButtonYOffset,
                BookPageNumbering::new
        );
    }

    public static class Builder {

        public final ResourceKey<IVampireBook> id;
        public Component author;
        public ResourceLocation backgroundTexture;

        public Builder(ResourceKey<IVampireBook> id) {
            this.id = id;
        }

        public Builder customAuthor() {
            this.author = Component.translatable("vampire_book." + id.location().toLanguageKey() + ".author");
            return this;
        }

        public Builder author(String author) {
            this.author = Component.literal(author);
            return this;
        }

        public Builder background(ResourceLocation backgroundTexture) {
            this.backgroundTexture = backgroundTexture;
            return this;
        }

        public Builder letter() {
            return background(ModVampireBooks.LETTER_BACKGROUND);
        }

        public VampireBook build() {
            return new VampireBook(id.location(), author == null ? VampireBook.UNKNOWN_AUTHOR : author, backgroundTexture == null ? ModVampireBooks.DIARY_BACKGROUND : backgroundTexture);
        }
    }
}
