package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.components.IAppliedOilContent;
import de.teamlapen.vampirism.api.components.IOilContent;
import de.teamlapen.vampirism.api.components.IVampireBookContent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

import static de.teamlapen.vampirism.api.APIUtil.supplyDataComponent;

@SuppressWarnings({"unused"})
public class VampirismDataComponents {

    public static final Supplier<DataComponentType<IOilContent>> OIL_CONTENT = supplyDataComponent(VampirismDataComponents.Keys.OIL_CONTENTS);
    public static final Supplier<DataComponentType<IAppliedOilContent>> APPLIED_OIL = supplyDataComponent(VampirismDataComponents.Keys.APPLIED_OIL);
    public static final Supplier<DataComponentType<IVampireBookContent>> VAMPIRE_BOOK = supplyDataComponent(VampirismDataComponents.Keys.VAMPIRE_BOOK);


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
