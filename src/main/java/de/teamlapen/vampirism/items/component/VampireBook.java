package de.teamlapen.vampirism.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.components.IVampireBook;
import de.teamlapen.vampirism.api.general.IBookBackground;
import de.teamlapen.vampirism.api.general.IBookContents;
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
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public record VampireBook(ResourceLocation id, Component author, ResourceLocation backgroundId) implements IVampireBook {

    public static final MutableComponent UNKNOWN_AUTHOR = Component.translatable("vampire_book.vampirism.unknown.author");

    public static final VampireBook EMPTY = new VampireBook(VResourceLocation.mod("unknown"), UNKNOWN_AUTHOR, ModVampireBooks.DIARY_BACKGROUND_ID);

    public static final Codec<IVampireBook> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(IVampireBook::id),
            ComponentSerialization.CODEC.optionalFieldOf("author", UNKNOWN_AUTHOR).forGetter(IVampireBook::author),
            ResourceLocation.CODEC.optionalFieldOf("backgroundId", ModVampireBooks.DIARY_BACKGROUND_ID).forGetter(IVampireBook::backgroundId)
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

    public IBookContents bookContents() {
        return VampireBookLoader.loadBookContents(this);
    }

    public List<MutableComponent> contents() {
        return bookContents().contents().stream().map(Component::literal).toList();
    }

    public List<IBookContents.IImageEntry> images() {
        return bookContents().images();
    }

    public IBookBackground background() {
        return VampireBookLoader.loadBackground(backgroundId);
    }

    public static VampireBook.Builder builder(ResourceKey<IVampireBook> id) {
        return new VampireBook.Builder(id);
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

        public VampireBook build() {
            return new VampireBook(id.location(), author == null ? VampireBook.UNKNOWN_AUTHOR : author, backgroundTexture == null ? ModVampireBooks.DIARY_BACKGROUND_ID : backgroundTexture);
        }
    }
}
