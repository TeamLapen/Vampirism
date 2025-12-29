package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.api.world.items.components.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

import java.util.function.Supplier;

import static de.teamlapen.vampirism.api.APIUtil.supplyDataComponent;

/**
 * This API class does not necessarily contains all data components from Vampirism, but {@link de.teamlapen.vampirism.api.VampirismDataComponents.Keys} contains all keys.
 */
@SuppressWarnings({"unused"})
public class VampirismDataComponents {

    public static final Supplier<DataComponentType<IOilContent>> OIL_CONTENT = supplyDataComponent(VampirismDataComponents.Keys.OIL_CONTENTS);
    public static final Supplier<DataComponentType<IAppliedOilContent>> APPLIED_OIL = supplyDataComponent(VampirismDataComponents.Keys.APPLIED_OIL);
    public static final Supplier<DataComponentType<IVampireBook>> VAMPIRE_BOOK = supplyDataComponent(VampirismDataComponents.Keys.VAMPIRE_BOOK);
    public static final Supplier<DataComponentType<IContainedFluid>> BLOOD_CONTAINER = supplyDataComponent(VampirismDataComponents.Keys.BLOOD_CONTAINER);
    public static final Supplier<DataComponentType<IContainedProjectiles>> CONTAINED_PROJECTILES = supplyDataComponent(VampirismDataComponents.Keys.CONTAINED_PROJECTILES);
    public static final Supplier<DataComponentType<Unit>> DO_NOT_NAME = supplyDataComponent(VampirismDataComponents.Keys.DO_NOT_NAME);
    public static final Supplier<DataComponentType<ISwordTraining>> VAMPIRE_SWORD = supplyDataComponent(VampirismDataComponents.Keys.VAMPIRE_SWORD);
    public static final Supplier<DataComponentType<IBloodCharged>> BLOOD_CHARGED = supplyDataComponent(VampirismDataComponents.Keys.BLOOD_CHARGED);
    public static final Supplier<DataComponentType<Float>> TRAINING_CACHE = supplyDataComponent(VampirismDataComponents.Keys.TRAINING_CACHE);
    public static final Supplier<DataComponentType<ISelectedAmmunition>> SELECTED_AMMUNITION = supplyDataComponent(VampirismDataComponents.Keys.SELECTED_AMMUNITION);
    public static final Supplier<DataComponentType<IBottleBlood>> BOTTLE_BLOOD = supplyDataComponent(VampirismDataComponents.Keys.BOTTLE_BLOOD);


    public static class Keys {
        public static final Identifier OIL_CONTENTS = VResourceLocation.mod( "oil_contents");
        public static final Identifier APPLIED_OIL = VResourceLocation.mod( "applied_oil");
        public static final Identifier VAMPIRE_BOOK = VResourceLocation.mod( "vampire_book");
        public static final Identifier BLOOD_CONTAINER = VResourceLocation.mod( "blood_container");
        public static final Identifier CONTAINED_PROJECTILES = VResourceLocation.mod( "contained_projectiles");
        public static final Identifier DO_NOT_NAME = VResourceLocation.mod( "do_not_name");
        public static final Identifier VAMPIRE_SWORD = VResourceLocation.mod( "vampire_sword");
        public static final Identifier TRAINING_CACHE = VResourceLocation.mod( "training_cache");
        public static final Identifier SELECTED_AMMUNITION = VResourceLocation.mod( "selected_ammunition");
        public static final Identifier BLOOD_CHARGED = VResourceLocation.mod( "blood_charged");
        public static final Identifier BOTTLE_BLOOD = VResourceLocation.mod( "bottle_blood");
        public static final Identifier FRUGALITY = VResourceLocation.mod( "frugality");
        public static final Identifier VAMPIRE_FOOD = VResourceLocation.mod("vampire_food");
        public static final Identifier DROP_VAMPIRE_SOUL = VResourceLocation.mod("drop_vampire_soul");
        public static final Identifier PURE_LEVEL = VResourceLocation.mod("pure_level");
        public static final Identifier QUARREL_POUCH_CONTENTS = VResourceLocation.mod("quarrel_pouch_contents");
        public static final Identifier ACTIVE = VResourceLocation.mod("active");
        public static final Identifier SHIFT_DESCRIPTION = VResourceLocation.mod("shift_description");
        public static final Identifier BLOCK_DESCRIPTION = VResourceLocation.mod("block_description");
    }
}
