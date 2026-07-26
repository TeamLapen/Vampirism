package de.teamlapen.vampirism.common.world.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.general.IBookBackground;
import de.teamlapen.vampirism.api.general.IBookContents;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.items.components.IVampireBook;
import de.teamlapen.vampirism.client.VampirismModClient;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.tags.ModVampireBookTags;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public record VampireBook(Identifier id, Component author) implements IVampireBook {

    public static final MutableComponent UNKNOWN_AUTHOR = Component.translatable("vampire_book.vampirism.author.unknown");

    public static final VampireBook EMPTY = new VampireBook(VIdentifier.mod("unknown"), UNKNOWN_AUTHOR);

    public static final Codec<IVampireBook> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(IVampireBook::id),
            ComponentSerialization.CODEC.optionalFieldOf("author", UNKNOWN_AUTHOR).forGetter(IVampireBook::author)
            ).apply(instance, VampireBook::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, IVampireBook> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, IVampireBook::id,
            ComponentSerialization.STREAM_CODEC, IVampireBook::author,
            VampireBook::new
    );

    public static void addToStack(ItemStack stack, IVampireBook vampireBook) {
        stack.set(ModDataComponents.VAMPIRE_BOOK.get(), vampireBook);
    }

    public static IVampireBook get(ItemStack stack) {
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
        return VampirismModClient.services().vampireBooks().getContentsFor(this);
    }

    public List<MutableComponent> translations() {
        return bookContents().contents().stream().map(Component::literal).toList();
    }

    public IBookBackground background() {
        return VampirismModClient.services().vampireBooks().getBackground(bookContents().background());
    }

    public List<IBookContents.IImageEntry> images() {
        return bookContents().images();
    }

    public static VampireBook.Builder builder(ResourceKey<IVampireBook> id) {
        return new VampireBook.Builder(id);
    }

    public static class Builder {

        public final ResourceKey<IVampireBook> id;
        public @Nullable Component author;

        public Builder(ResourceKey<IVampireBook> id) {
            this.id = id;
        }

        /**
         * Sets the author using a translatable component key based on the author id.
         * <p>
         * The translation key will look like this:
         * {@code vampire_book.<mod_id>.author.<author_id>}
         * <p>
         * Recommended to use for names that should be localized.
         */
        public Builder translatableAuthor(String authorId) {
            this.author = Component.translatable("vampire_book." + id.identifier().getNamespace() + ".author." + authorId);
            return this;
        }

        /**
         * Sets the author using a literal component which is untranslatable.
         * <p>
         * Recommended to use for nicknames (e.g., {@code "Sinister Solace"}).
         * Avoid using this for names that aren't problematic to localize.
         */
        public Builder literalAuthor(String author) {
            this.author = Component.literal(author);
            return this;
        }

        public VampireBook build() {
            return new VampireBook(id.identifier(), author == null ? VampireBook.UNKNOWN_AUTHOR : author);
        }
    }
}
