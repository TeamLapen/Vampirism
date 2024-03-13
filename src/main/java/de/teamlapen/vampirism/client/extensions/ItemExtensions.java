package de.teamlapen.vampirism.client.extensions;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.api.items.ICrossbow;
import de.teamlapen.vampirism.api.items.IHunterCrossbow;
import de.teamlapen.vampirism.client.model.armor.*;
import de.teamlapen.vampirism.items.HunterHatItem;
import de.teamlapen.vampirism.items.crossbow.VampirismCrossbowItem;
import de.teamlapen.vampirism.mixin.client.accessor.ItemInHandRendererAccessor;
import de.teamlapen.vampirism.proxy.ClientProxy;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IArmPoseTransformer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemExtensions {

    public static final IClientItemExtensions VAMPIRE_CLOTHING = new IClientItemExtensions() {
        @Override
        public @NotNull Model getGenericArmorModel(@NotNull LivingEntity livingEntity, ItemStack itemStack, @NotNull EquipmentSlot equipmentSlot, @NotNull HumanoidModel<?> original) {
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
        public @NotNull Model getGenericArmorModel(@NotNull LivingEntity livingEntity, @NotNull ItemStack itemStack, @NotNull EquipmentSlot equipmentSlot, @NotNull HumanoidModel<?> original) {
            return CloakModel.getAdjustedCloak(original, livingEntity);
        }
    };

    public static final IClientItemExtensions HUNTER_HAT = new IClientItemExtensions() {
        @Override
        public @NotNull Model getGenericArmorModel(@NotNull LivingEntity livingEntity, ItemStack itemStack, @NotNull EquipmentSlot equipmentSlot, @NotNull HumanoidModel<?> original) {
            return switch (((HunterHatItem) itemStack.getItem()).getHateType()) {
                case TYPE_1 -> HunterHatModel.getAdjustedInstance0(original);
                case TYPE_2 -> HunterHatModel.getAdjustedInstance1(original);
            };
        }
    };

    public static final IClientItemExtensions MOTHER_TROPHY = new IClientItemExtensions() {
        @Override
        public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return ClientProxy.get().getBlockEntityItemRenderer();
        }
    };

    public static final HumanoidModel.ArmPose DOUBLE_CROSSBOW_CHARGE = HumanoidModel.ArmPose.create("vampirism:double_crossbow_charge", true, (model, entity, arm) -> {
        AnimationUtils.animateCrossbowCharge(model.rightArm, model.leftArm, entity, arm == HumanoidArm.LEFT);

        InteractionHand hand = entity.getMainArm() == arm ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack itemInHand = entity.getItemInHand(hand);
        if (itemInHand.getItem() instanceof VampirismCrossbowItem crossbow) {
            ItemStack otherItemStack = entity.getItemInHand(entity.getMainArm() == arm ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
            if (otherItemStack.getItem() instanceof IHunterCrossbow) {
                int combinedUseDuration = crossbow.getCombinedUseDuration(itemInHand, entity, hand);
                int itemUseDuration = crossbow.getUseDuration(itemInHand);
                int combinedChargeDurationMod = crossbow.getCombinedChargeDuration(itemInHand, entity, hand);
                int itemChargeDurationMod = crossbow.getChargeDuration(itemInHand);
                float itemUseTicks = entity.getUseItemRemainingTicks();
                ModelPart part1;
                ModelPart part2;
                boolean pRightHanded;
                float f;
                float f1;
                if (itemUseTicks < combinedUseDuration - itemUseDuration) {
                    part1 = arm == HumanoidArm.RIGHT ? model.leftArm : model.rightArm;
                    part2 = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
                    pRightHanded = arm == HumanoidArm.LEFT;
                    f = combinedChargeDurationMod- itemChargeDurationMod;
                    f1 = Mth.clamp(combinedChargeDurationMod - itemChargeDurationMod - itemUseTicks, 0.0F, f);
                } else {
                    part1 = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
                    part2 = arm == HumanoidArm.RIGHT ? model.leftArm: model.rightArm;
                    pRightHanded = arm == HumanoidArm.RIGHT;
                    f = itemChargeDurationMod;
                    f1 = Mth.clamp((combinedChargeDurationMod-(combinedChargeDurationMod - itemChargeDurationMod)) - (itemUseTicks - (combinedUseDuration - itemUseDuration)), 0.0F, f);
                }
                part1.yRot = pRightHanded ? -0.8F : 0.8F;
                part1.xRot = -0.97079635F;
                part2.xRot = part1.xRot;
                float f2 = f1 / f;
                part2.yRot = Mth.lerp(f2, 0.4F, 0.85F) * (float)(pRightHanded ? 1 : -1);
                part2.xRot = Mth.lerp(f2, part2.xRot, (float) (-Math.PI / 2));
            } else {
                ModelPart modelpart = model.rightArm;
                ModelPart modelpart1 = model.leftArm;
                boolean pRightHanded = entity.getMainArm() == HumanoidArm.RIGHT;
                modelpart.yRot = pRightHanded ? -0.8F : 0.8F;
                modelpart.xRot = -0.97079635F;
                modelpart1.xRot = modelpart.xRot;
                float f = crossbow.getChargeDuration(entity.getUseItem());
                float f1 = Mth.clamp((float)entity.getTicksUsingItem(), 0.0F, f);
                float f2 = f1 / f;
                modelpart1.yRot = Mth.lerp(f2, 0.4F, 0.85F) * (float)(pRightHanded ? 1 : -1);
                modelpart1.xRot = Mth.lerp(f2, modelpart1.xRot, (float) (-Math.PI / 2));
            }
        }
    });

    public static final HumanoidModel.ArmPose DOUBLE_CROSSBOW_HOLD = HumanoidModel.ArmPose.create("vampirism:double_crossbow_hold", true, new IArmPoseTransformer() {
        @Override
        public void applyTransform(@NotNull HumanoidModel<?> model, @NotNull LivingEntity entity, @NotNull HumanoidArm arm) {
            model.rightArm.yRot = -0F + model.head.yRot;
            model.leftArm.yRot = 0F + model.head.yRot;
            model.rightArm.xRot = (float) (-Math.PI / 2) + model.head.xRot + 0.1F;
            model.leftArm.xRot = -1.5F + model.head.xRot;
        }
    });
    public static final IClientItemExtensions HUNTER_CROSSBOW = new IClientItemExtensions() {
        @Nullable
        @Override
        public HumanoidModel.ArmPose getArmPose(@NotNull LivingEntity entityLiving, @NotNull InteractionHand hand, ItemStack itemStack) {
            if (itemStack.getItem() instanceof VampirismCrossbowItem crossbow) {
                ItemStack otherStack = entityLiving.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
                if (crossbow.canUseDoubleCrossbow(entityLiving) && otherStack.getItem() instanceof ICrossbow otherCrossbow) {
                    if (entityLiving.getUseItemRemainingTicks() > 0) {
                        if (otherCrossbow.isCharged(otherStack)) {
                            return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                        }
                        return DOUBLE_CROSSBOW_CHARGE;
                    } else if (!entityLiving.swinging) {
                        boolean charged1 = crossbow.isCharged(itemStack);
                        boolean charged2 = otherCrossbow.isCharged(otherStack);
                        if (charged1 && charged2) {
                            return DOUBLE_CROSSBOW_HOLD;
                        } else if (charged1) {
                            return HumanoidModel.ArmPose.CROSSBOW_HOLD;
                        } else if (charged2) {
                            return HumanoidModel.ArmPose.ITEM;
                        }
                    }
                } else {
                    if (entityLiving.getUsedItemHand() == hand && entityLiving.getUseItemRemainingTicks() > 0) {
                        return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                    } else if (!entityLiving.swinging && crossbow.isCharged(itemStack)) {
                        return HumanoidModel.ArmPose.CROSSBOW_HOLD;
                    }
                }
            }
            return HumanoidModel.ArmPose.ITEM;
        }

        @Override
        public boolean applyForgeHandTransform(@NotNull PoseStack pPoseStack, LocalPlayer pPlayer, @NotNull HumanoidArm humanoidarm, ItemStack pStack, float pPartialTicks, float pEquippedProgress, float pSwingProgress) {
            boolean flag = humanoidarm == pPlayer.getMainArm();
            InteractionHand pHand = flag ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            if (pStack.getItem() instanceof VampirismCrossbowItem) {
                applyCrossbowTransform(pPoseStack, pPlayer, humanoidarm, pStack, pPartialTicks, pEquippedProgress, pSwingProgress, flag, !flag && pPlayer.isUsingItem() && pPlayer.getUsedItemHand() != pHand);
            }
            return true;
        }

        private void applyCrossbowTransform(PoseStack pPoseStack, LocalPlayer pPlayer, HumanoidArm humanoidarm, ItemStack pStack, float pPartialTicks, float pEquippedProgress, float pSwingProgress, boolean isMainArm, boolean usingMainHand) {
            InteractionHand pHand = isMainArm ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            IHunterCrossbow item = (IHunterCrossbow) pStack.getItem();
            boolean flag1 = item.isCharged(pStack);
            boolean flag2 = humanoidarm == HumanoidArm.RIGHT;
            int i = flag2 ? 1 : -1;

            int totalDuration = item.getCombinedUseDuration(pStack, pPlayer, pHand);
            int itemDuration = item.asItem().getUseDuration(pStack);
            float itemUseDuration = pPlayer.getUseItemRemainingTicks();
            if (usingMainHand && itemUseDuration > itemDuration) {
                itemUseDuration = 0;
            } else if (!usingMainHand && totalDuration != itemDuration) {
                itemUseDuration = Math.max(0, itemUseDuration - (totalDuration - itemDuration));
            }
            if (pPlayer.isUsingItem() && itemUseDuration > 0 && (pPlayer.getUsedItemHand() == pHand) != usingMainHand && (item.canUseDoubleCrossbow(pPlayer) || !usingMainHand) && !flag1) {
                ((ItemInHandRendererAccessor) Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer()).invokeApplyItemArmTransform(pPoseStack, humanoidarm, pEquippedProgress);
                pPoseStack.translate((float)i * -0.4785682F, -0.094387F, 0.05731531F);
                pPoseStack.mulPose(Axis.XP.rotationDegrees(-11.935F));
                pPoseStack.mulPose(Axis.YP.rotationDegrees((float)i * 65.3F));
                pPoseStack.mulPose(Axis.ZP.rotationDegrees((float)i * -9.785F));
                float f9 = (float)pStack.getUseDuration() - (itemUseDuration - pPartialTicks + 1.0F);
                float f13 = f9 / item.getChargeDuration(pStack);
                if (f13 > 1.0F) {
                    f13 = 1.0F;
                }

                if (f13 > 0.1F) {
                    float f16 = Mth.sin((f9 - 0.1F) * 1.3F);
                    float f3 = f13 - 0.1F;
                    float f4 = f16 * f3;
                    pPoseStack.translate(f4 * 0.0F, f4 * 0.004F, f4 * 0.0F);
                }

                pPoseStack.translate(f13 * 0.0F, f13 * 0.0F, f13 * 0.04F);
                pPoseStack.scale(1.0F, 1.0F, 1.0F + f13 * 0.2F);
                pPoseStack.mulPose(Axis.YN.rotationDegrees((float)i * 45.0F));
            } else {
                float f = -0.4F * Mth.sin(Mth.sqrt(pSwingProgress) * (float) Math.PI);
                float f1 = 0.2F * Mth.sin(Mth.sqrt(pSwingProgress) * (float) (Math.PI * 2));
                float f2 = -0.2F * Mth.sin(pSwingProgress * (float) Math.PI);
                pPoseStack.translate((float)i * f, f1, f2);
                ((ItemInHandRendererAccessor) Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer()).invokeApplyItemArmTransform(pPoseStack, humanoidarm, pEquippedProgress);
                ((ItemInHandRendererAccessor) Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer()).invokeApplyItemArmAttackTransform(pPoseStack, humanoidarm, pSwingProgress);
                if (flag1 && pSwingProgress < 0.001F && isMainArm && (!(pPlayer.getOffhandItem().getItem() instanceof IHunterCrossbow otherCrossbow) || !otherCrossbow.isCharged(pPlayer.getOffhandItem()))) {
                    pPoseStack.translate((float)i * -0.641864F, 0.0F, 0.0F);
                    pPoseStack.mulPose(Axis.YP.rotationDegrees((float)i * 10.0F));
                }
            }
        }
    };
}
