package de.teamlapen.vampirism.core;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismDataComponents;
import de.teamlapen.vampirism.api.components.IVampireBook;
import de.teamlapen.vampirism.items.component.*;
import de.teamlapen.vampirism.items.consume.BloodFoodProperties;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {

    public static final DeferredRegister.DataComponents ITEM_DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, REFERENCE.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<OilContent>> OIL = ITEM_DATA_COMPONENTS.register(VampirismDataComponents.Keys.OIL_CONTENTS.getPath(), () -> DataComponentType.<OilContent>builder().persistent(OilContent.CODEC).networkSynchronized(OilContent.STREAM_CODEC).cacheEncoding().build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AppliedOilContent>> APPLIED_OIL = ITEM_DATA_COMPONENTS.register(VampirismDataComponents.Keys.APPLIED_OIL.getPath(), () -> DataComponentType.<AppliedOilContent>builder().persistent(AppliedOilContent.CODEC).networkSynchronized(AppliedOilContent.STREAM_CODEC).cacheEncoding().build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IVampireBook>> VAMPIRE_BOOK = ITEM_DATA_COMPONENTS.register(VampirismDataComponents.Keys.VAMPIRE_BOOK.getPath(), () -> DataComponentType.<IVampireBook>builder().persistent(VampireBook.CODEC).networkSynchronized(VampireBook.STREAM_CODEC).cacheEncoding().build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> BLOOD_CONTAINER = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.BLOOD_CONTAINER.getPath(), builder -> builder.persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ContainedProjectiles>> CONTAINED_PROJECTILES = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.CONTAINED_PROJECTILES.getPath(), builder -> builder.persistent(ContainedProjectiles.CODEC).networkSynchronized(ContainedProjectiles.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EffectiveRefinementSet>> REFINEMENT_SET = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.REFINEMENT_SET.getPath(), builder -> builder.persistent(EffectiveRefinementSet.CODEC).networkSynchronized(EffectiveRefinementSet.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> DO_NOT_NAME = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.DO_NOT_NAME.getPath(), builder -> builder.persistent(Codec.unit(Unit.INSTANCE)).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SwordTraining>> VAMPIRE_SWORD = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.VAMPIRE_SWORD.getPath(), builder -> builder.persistent(SwordTraining.CODEC).networkSynchronized(SwordTraining.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BloodCharged>> BLOOD_CHARGED = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.BLOOD_CHARGED.getPath(), builder -> builder.persistent(BloodCharged.CODEC).networkSynchronized(BloodCharged.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> TRAINING_CACHE = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.TRAINING_CACHE.getPath(), builder -> builder.persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SelectedAmmunition>> SELECTED_AMMUNITION = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.SELECTED_AMMUNITION.getPath(), builder -> builder.persistent(SelectedAmmunition.CODEC).networkSynchronized(SelectedAmmunition.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BottleBlood>> BOTTLE_BLOOD = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.BOTTLE_BLOOD.getPath(), builder -> builder.persistent(BottleBlood.CODEC).networkSynchronized(BottleBlood.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> CROSSBOW_FRUGALITY_TRIGGERED = ITEM_DATA_COMPONENTS.registerComponentType("crossbow_frugality_triggered", builder -> builder.persistent(Codec.unit(Unit.INSTANCE)).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> IS_FACTION_BANNER = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.IS_FACTION_BANNER.getPath(), builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BloodFoodProperties>> VAMPIRE_FOOD = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.VAMPIRE_FOOD.getPath(), builder -> builder.persistent(BloodFoodProperties.DIRECT_CODEC).networkSynchronized(BloodFoodProperties.DIRECT_STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FactionRestriction>> FACTION_RESTRICTION = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.FACTION_RESTRICTION.getPath(), builder -> builder.persistent(FactionRestriction.CODEC).networkSynchronized(FactionRestriction.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FactionSlayer>>  FACTION_SLAYER = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.FACTION_SLAYER.getPath(), builder -> builder.persistent(FactionSlayer.CODEC).networkSynchronized(FactionSlayer.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> DROP_VAMPIRE_SOUL = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.DROP_VAMPIRE_SOUL.getPath(), builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PureLevel>> PURE_LEVEL = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.PURE_LEVEL.getPath(), builder -> builder.persistent(PureLevel.CODEC).networkSynchronized(PureLevel.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<QuarrelPouchContents>> QUARREL_POUCH_CONTENTS = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.QUARREL_POUCH_CONTENTS.getPath(), builder -> builder.persistent(QuarrelPouchContents.CODEC).networkSynchronized(QuarrelPouchContents.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> ACTIVE = ITEM_DATA_COMPONENTS.registerComponentType(VampirismDataComponents.Keys.ACTIVE.getPath(), builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));

    static void register(IEventBus eventBus) {
        ITEM_DATA_COMPONENTS.register(eventBus);
    }
}
