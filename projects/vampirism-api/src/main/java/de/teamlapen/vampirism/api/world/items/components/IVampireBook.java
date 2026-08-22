package de.teamlapen.vampirism.api.world.items.components;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.general.IBookBackground;
import de.teamlapen.vampirism.api.general.IBookContents;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.tags.TagKey;

import java.util.List;

/**
 * A component that stores vampire book data, its id and author.
 */
public interface IVampireBook {

    Codec<Holder<IVampireBook>> HOLDER_CODEC = RegistryFixedCodec.create(VampirismRegistries.Keys.VAMPIRE_BOOK);

    StreamCodec<RegistryFriendlyByteBuf, Holder<IVampireBook>> HOLDER_STREAM_CODEC = ByteBufCodecs.holderRegistry(VampirismRegistries.Keys.VAMPIRE_BOOK);

    /**
     * The id of the book.
     */
    Identifier id();

    /**
     * The book author displayed in the description of the item.
     */
    Component author();

    /**
     * @return If the book is tagged with a certain tag.
     */
    boolean is(TagKey<IVampireBook> tag, RegistryAccess registryAccess);

    /**
     * @return If the book has no contents.
     */
    boolean isEmpty();

    /**
     * The book title displayed as the item name.
     */
    MutableComponent title();

    /**
     * The contents of the book. Stores its text, background id and images.
     */
    IBookContents bookContents();

    /**
     * The pre-translated text of the book.
     */
    List<MutableComponent> translations();

    /**
     * The background of the book.
     */
    IBookBackground background();

    /**
     * A list of image entries, driven by the contents file, to be rendered on the specific pages of the book.
     */
    List<IBookContents.IImageEntry> images();
}
