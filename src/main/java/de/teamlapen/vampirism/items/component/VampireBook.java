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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public record VampireBook(ResourceLocation id, ResourceLocation itemModel, ResourceLocation backgroundTexture, int pages) implements IVampireBook {

    private static final VampireBook EMPTY = new VampireBook(VResourceLocation.mod("unknown_id"), ModVampireBooks.DEFAULT_ITEM_MODEL, ModVampireBooks.DEFAULT_BACKGROUND_TEXTURE, 1);

    public static final Codec<IVampireBook> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(IVampireBook::id),
            ResourceLocation.CODEC.optionalFieldOf("item_model", ModVampireBooks.DEFAULT_ITEM_MODEL).forGetter(IVampireBook::itemModel),
            ResourceLocation.CODEC.optionalFieldOf("background_texture", ModVampireBooks.DEFAULT_BACKGROUND_TEXTURE).forGetter(IVampireBook::backgroundTexture),
            Codec.INT.optionalFieldOf("pages", 1).forGetter(IVampireBook::pages)
            ).apply(instance, VampireBook::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, IVampireBook> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, IVampireBook::id,
            ResourceLocation.STREAM_CODEC, IVampireBook::itemModel,
            ResourceLocation.STREAM_CODEC, IVampireBook::backgroundTexture,
            ByteBufCodecs.INT, IVampireBook::pages,
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

    public MutableComponent author() {
        return Component.translatable("vampire_book." + id().toLanguageKey() + ".author");
    }

    public List<MutableComponent> text() {
        return VampireBookLoader.loadBookContents(this).stream().map(Component::literal).toList();
    }
}
