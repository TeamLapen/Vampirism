package de.teamlapen.vampirism.api;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.components.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

import static de.teamlapen.vampirism.api.APIUtil.supplyDataComponent;

@SuppressWarnings({"unused"})
public class VampirismDataComponents {

    public static final Supplier<DataComponentType<IOilContent>> OIL_CONTENT = supplyDataComponent(VampirismDataComponents.Keys.OIL_CONTENTS);
    public static final Supplier<DataComponentType<IAppliedOilContent>> APPLIED_OIL = supplyDataComponent(VampirismDataComponents.Keys.APPLIED_OIL);
    public static final Supplier<DataComponentType<IVampireBookContent>> VAMPIRE_BOOK = supplyDataComponent(VampirismDataComponents.Keys.VAMPIRE_BOOK);
    public static final Supplier<DataComponentType<IContainedFluid>> BLOOD_CONTAINER = supplyDataComponent(VampirismDataComponents.Keys.BLOOD_CONTAINER);
    public static final Supplier<DataComponentType<IContainedProjectiles>> CONTAINED_PROJECTILES = supplyDataComponent(VampirismDataComponents.Keys.CONTAINED_PROJECTILES);
    public static final Supplier<DataComponentType<IEffectiveRefinementSet>> REFINEMENT_SET = supplyDataComponent(VampirismDataComponents.Keys.REFINEMENT_SET);
    public static final Supplier<DataComponentType<Unit>> DO_NOT_NAME = supplyDataComponent(VampirismDataComponents.Keys.DO_NOT_NAME);
    public static final Supplier<DataComponentType<ISwordTraining>> VAMPIRE_SWORD = supplyDataComponent(VampirismDataComponents.Keys.VAMPIRE_SWORD);
    public static final Supplier<DataComponentType<IBloodCharged>> BLOOD_CHARGED = supplyDataComponent(VampirismDataComponents.Keys.BLOOD_CHARGED);
    public static final Supplier<DataComponentType<Float>> TRAINING_CACHE = supplyDataComponent(VampirismDataComponents.Keys.TRAINING_CACHE);
    public static final Supplier<DataComponentType<ISelectedAmmunition>> SELECTED_AMMUNITION = supplyDataComponent(VampirismDataComponents.Keys.SELECTED_AMMUNITION);
    public static final Supplier<DataComponentType<IBottleBlood>> BOTTLE_BLOOD = supplyDataComponent(VampirismDataComponents.Keys.BOTTLE_BLOOD);


    public static class Keys {
        public static final ResourceLocation OIL_CONTENTS = new ResourceLocation(VReference.MODID, "oil_contents");
        public static final ResourceLocation APPLIED_OIL = new ResourceLocation(VReference.MODID, "applied_oil");
        public static final ResourceLocation VAMPIRE_BOOK = new ResourceLocation(VReference.MODID, "vampire_book");
        public static final ResourceLocation BLOOD_CONTAINER = new ResourceLocation(VReference.MODID, "blood_container");
        public static final ResourceLocation CONTAINED_PROJECTILES = new ResourceLocation(VReference.MODID, "contained_projectiles");
        public static final ResourceLocation REFINEMENT_SET = new ResourceLocation(VReference.MODID, "refinement_set");
        public static final ResourceLocation DO_NOT_NAME = new ResourceLocation(VReference.MODID, "do_not_name");
        public static final ResourceLocation VAMPIRE_SWORD = new ResourceLocation(VReference.MODID, "vampire_sword");
        public static final ResourceLocation TRAINING_CACHE = new ResourceLocation(VReference.MODID, "training_cache");
        public static final ResourceLocation SELECTED_AMMUNITION = new ResourceLocation(VReference.MODID, "selected_ammunition");
        public static final ResourceLocation BLOOD_CHARGED = new ResourceLocation(VReference.MODID, "blood_charged");
        public static final ResourceLocation BOTTLE_BLOOD = new ResourceLocation(VReference.MODID, "bottle_blood");
    }
}
