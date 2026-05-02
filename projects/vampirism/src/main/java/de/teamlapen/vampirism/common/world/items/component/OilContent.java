package de.teamlapen.vampirism.common.world.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.world.items.components.IOilContent;
import de.teamlapen.vampirism.api.world.items.oil.IOil;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModOils;
import de.teamlapen.vampirism.common.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

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

    public static ItemStackTemplate createTemplate(Item item, Holder<? extends IOil> oil) {
        return new ItemStackTemplate(item, DataComponentPatch.builder()
                .set(ModDataComponents.OIL.get(), new OilContent((Holder<IOil>) oil))
                .build());
    }

    public OilContent withOil(Holder<IOil> oil) {
        return new OilContent(oil);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IOil> OilContent of(Holder<T> oil) {
        return new OilContent((Holder<IOil>) oil);
    }

    public static Holder<IOil> getOil(DataComponentGetter stack) {
        return stack.getOrDefault(ModDataComponents.OIL, EMPTY).oil();
    }
}
