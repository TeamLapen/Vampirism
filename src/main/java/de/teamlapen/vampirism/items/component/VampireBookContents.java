package de.teamlapen.vampirism.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.components.IVampireBookContent;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record VampireBookContents(String id, String author, String title) implements IVampireBookContent {
    private static final VampireBookContents EMPTY = new VampireBookContents(VampireBookManager.OLD_ID, "Unknown", "Unknown");
    public static final Codec<VampireBookContents> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.STRING.fieldOf("id").forGetter(o -> o.id),
                    Codec.STRING.fieldOf("author").forGetter(o -> o.author),
                    Codec.STRING.fieldOf("title").forGetter(o -> o.title)
            ).apply(inst, VampireBookContents::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, VampireBookContents> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, VampireBookContents::id,
            ByteBufCodecs.STRING_UTF8, VampireBookContents::author,
            ByteBufCodecs.STRING_UTF8, VampireBookContents::title,
            VampireBookContents::new
    );

    public static void addFromBook(ItemStack stack, VampireBookManager.BookContext bookContext) {
        stack.set(ModDataComponents.VAMPIRE_BOOK, new VampireBookContents(bookContext.id(), bookContext.book().author(), bookContext.book().title()));
    }

    public static VampireBookContents get(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.VAMPIRE_BOOK, EMPTY);
    }
}
