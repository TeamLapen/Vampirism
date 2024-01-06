package de.teamlapen.vampirism.client.extensions;

import de.teamlapen.vampirism.client.model.armor.*;
import de.teamlapen.vampirism.items.HunterHatItem;
import de.teamlapen.vampirism.proxy.ClientProxy;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class ItemExtensions {

    public static final IClientItemExtensions VAMPIRE_CLOTHING = new IClientItemExtensions() {
        @Override
        public @NotNull Model getGenericArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
            return switch (RegUtil.id(itemStack.getItem()).getPath()) {
                case "vampire_clothing_crown" -> ClothingCrownModel.getAdjustedInstance(original);
                case "vampire_clothing_legs" -> ClothingPantsModel.getAdjustedInstance(original);
                case "vampire_clothing_boots" -> ClothingBootsModel.getAdjustedInstance(original);
                case "vampire_clothing_hat" -> VampireHatModel.getAdjustedInstance(original);
                default -> DummyClothingModel.getAdjustedInstance(original);
            };
        }
    };

    public static final IClientItemExtensions VAMPIRE_CLOAK = new IClientItemExtensions() {
        @Override
        public @NotNull Model getGenericArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
            return CloakModel.getAdjustedCloak(original, livingEntity);
        }
    };

    public static final IClientItemExtensions HUNTER_HAT = new IClientItemExtensions() {
        @Override
        public @NotNull Model getGenericArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
            return switch (((HunterHatItem) itemStack.getItem()).getHateType()) {
                case TYPE_1 -> HunterHatModel.getAdjustedInstance0(original);
                case TYPE_2 -> HunterHatModel.getAdjustedInstance1(original);
            };
        }
    };

    public static final IClientItemExtensions MOTHER_TROPHY = new IClientItemExtensions() {
        @Override
        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return ClientProxy.get().getBlockEntityItemRenderer();
        }
    };
}
