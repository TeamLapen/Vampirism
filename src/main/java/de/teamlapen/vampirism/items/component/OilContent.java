package de.teamlapen.vampirism.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.components.IOilContent;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.ModOils;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record OilContent(Holder<IOil> oil) implements IOilContent {
    public static final OilContent EMPTY = new OilContent(ModOils.EMPTY);
    private static final Codec<OilContent> FULL_CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    ModRegistries.OILS.holderByNameCodec().optionalFieldOf("oil", ModOils.EMPTY).forGetter(o -> o.oil)
                    ).apply(inst, OilContent::new)
    );
    public static final Codec<OilContent> CODEC = Codec.withAlternative(FULL_CODEC, ModRegistries.OILS.holderByNameCodec(), OilContent::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, OilContent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(VampirismRegistries.Keys.OIL), OilContent::oil, OilContent::new
    );

    public static ItemStack createItemStack(Item item, Holder<? extends IOil> oil) {
        ItemStack stack = new ItemStack(item);
        stack.set(ModDataComponents.OIL.get(), new OilContent((Holder<IOil>) oil));
        return stack;
    }

    public OilContent withOil(Holder<IOil> oil) {
        return new OilContent(oil);
    }

    public static Holder<IOil> getOil(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.OIL, EMPTY).oil();
    }
}
