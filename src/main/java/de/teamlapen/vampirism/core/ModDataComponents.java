package de.teamlapen.vampirism.core;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismDataComponents;
import de.teamlapen.vampirism.items.component.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModDataComponents {

    public static final DeferredRegister.DataComponents ITEM_DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, REFERENCE.MODID);
    public static final DeferredRegister.DataComponents ENCHANTMENT_DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, REFERENCE.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<OilContent>> OIL = ITEM_DATA_COMPONENTS.register(VampirismDataComponents.Keys.OIL_CONTENTS.getPath(), () -> DataComponentType.<OilContent>builder().persistent(OilContent.CODEC).networkSynchronized(OilContent.STREAM_CODEC).cacheEncoding().build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AppliedOilContent>> APPLIED_OIL = ITEM_DATA_COMPONENTS.register(VampirismDataComponents.Keys.APPLIED_OIL.getPath(), () -> DataComponentType.<AppliedOilContent>builder().persistent(AppliedOilContent.CODEC).networkSynchronized(AppliedOilContent.STREAM_CODEC).cacheEncoding().build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<VampireBookContents>> VAMPIRE_BOOK = ITEM_DATA_COMPONENTS.register(VampirismDataComponents.Keys.VAMPIRE_BOOK.getPath(), () -> DataComponentType.<VampireBookContents>builder().persistent(VampireBookContents.CODEC).networkSynchronized(VampireBookContents.STREAM_CODEC).cacheEncoding().build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ContainedFluid>> BLOOD_CONTAINER = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.BLOOD_CONTAINER.getPath(), (builder) -> builder.persistent(ContainedFluid.CODEC).networkSynchronized(ContainedFluid.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ContainedProjectiles>> CONTAINED_PROJECTILES = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.CONTAINED_PROJECTILES.getPath(), (builder) -> builder.persistent(ContainedProjectiles.CODEC).networkSynchronized(ContainedProjectiles.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EffectiveRefinementSet>> REFINEMENT_SET = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.REFINEMENT_SET.getPath(), (builder) -> builder.persistent(EffectiveRefinementSet.CODEC).networkSynchronized(EffectiveRefinementSet.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> DO_NOT_NAME = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.DO_NOT_NAME.getPath(), (builder) -> builder.persistent(Codec.unit(Unit.INSTANCE)).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SwordTraining>> VAMPIRE_SWORD = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.VAMPIRE_SWORD.getPath(), (builder) -> builder.persistent(SwordTraining.CODEC).networkSynchronized(SwordTraining.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BloodCharged>> BLOOD_CHARGED = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.BLOOD_CHARGED.getPath(), (builder) -> builder.persistent(BloodCharged.CODEC).networkSynchronized(BloodCharged.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> TRAINING_CACHE = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.TRAINING_CACHE.getPath(), (builder) -> builder.persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SelectedAmmunition>> SELECTED_AMMUNITION = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.SELECTED_AMMUNITION.getPath(), (builder) -> builder.persistent(SelectedAmmunition.CODEC).networkSynchronized(SelectedAmmunition.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BottleBlood>> BOTTLE_BLOOD = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.BOTTLE_BLOOD.getPath(), (builder) -> builder.persistent(BottleBlood.CODEC).networkSynchronized(BottleBlood.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> CROSSBOW_FRUGALITY_TRIGGERED = ITEM_DATA_COMPONENTS.registerComponentType("crossbow_frugality_triggered", (builder) -> builder.persistent(Codec.unit(Unit.INSTANCE)).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> IS_FACTION_BANNER = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.IS_FACTION_BANNER.getPath(), (builder) -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> FRUGALITY = ENCHANTMENT_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.FRUGALITY.getPath(), (builder) -> builder.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf()));

    static void register(IEventBus eventBus) {
        ITEM_DATA_COMPONENTS.register(eventBus);
        ENCHANTMENT_DATA_COMPONENTS.register(eventBus);
    }
}
