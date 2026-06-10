package de.teamlapen.vampirism.client.extensions;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.api.world.items.IHunterCrossbow;
import de.teamlapen.vampirism.client.core.ModClientEnums;
import de.teamlapen.vampirism.common.world.items.crossbow.HunterCrossbowItem;
import de.teamlapen.vampirism.misc.mixin.client.accessor.ItemInHandRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.IArmPoseTransformer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ItemExtensions {

    public static final IClientItemExtensions CRUCIFIX = new IClientItemExtensions() {

        @Override
        public HumanoidModel.@Nullable ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
            if (entityLiving.isUsingItem() && entityLiving.getUseItemRemainingTicks() > 0) {
                return HumanoidModel.ArmPose.BLOCK;
            }
            return null;
        }
    };

    public static final IArmPoseTransformer DOUBLE_CROSSBOW_CHARGE_ARM_POSE_TRANSFORMER = (model, entity, arm) -> {
//        AnimationUtils.animateCrossbowCharge(model.rightArm, model.leftArm, entity, arm == HumanoidArm.LEFT);
//
//        InteractionHand hand = entity.getMainArm() == arm ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
//        ItemStack itemInHand = entity.getItemInHand(hand);
//        if (itemInHand.getItem() instanceof HunterCrossbowItem crossbow) {
//            ItemStack otherItemStack = entity.getItemInHand(entity.getMainArm() == arm ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
//            if (otherItemStack.getItem() instanceof IHunterCrossbow otherCrossbow) {
//                int combinedUseDuration = crossbow.getCombinedUseDuration(itemInHand, entity, hand);
//                int itemUseDuration = crossbow.getUseDuration(itemInHand, entity);
//                int combinedChargeDurationMod = crossbow.getCombinedChargeDurationMod(itemInHand, entity, hand);
//                int itemChargeDurationMod = crossbow.getChargeDurationMod(itemInHand, entity.level());
//                float itemUseTicks = entity.getUseItemRemainingTicks();
//                ModelPart modelpart;
//                ModelPart modelpart1;
//                boolean pRightHanded;
//                float f;
//                float f1;
//                if (itemUseTicks < combinedUseDuration - itemUseDuration) {
//                    modelpart = arm == HumanoidArm.RIGHT ? model.leftArm : model.rightArm;
//                    modelpart1 = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
//                    pRightHanded = arm == HumanoidArm.LEFT;
//                    f = combinedChargeDurationMod - itemChargeDurationMod;
//                    f1 = Mth.clamp(combinedChargeDurationMod - itemChargeDurationMod - itemUseTicks, 0.0F, f);
//                } else {
//                    modelpart = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
//                    modelpart1 = arm == HumanoidArm.RIGHT ? model.leftArm : model.rightArm;
//                    pRightHanded = arm == HumanoidArm.RIGHT;
//                    f = itemChargeDurationMod;
//                    f1 = Mth.clamp((combinedChargeDurationMod - (combinedChargeDurationMod - itemChargeDurationMod)) - (itemUseTicks - (combinedUseDuration - itemUseDuration)), 0.0F, f);
//                }
//                modelpart.yRot = pRightHanded ? -0.8F : 0.8F;
//                modelpart.xRot = -0.97079635F;
//                modelpart1.xRot = modelpart.xRot;
//                float f2 = f1 / f;
//                modelpart1.yRot = Mth.lerp(f2, 0.4F, 0.85F) * (float) (pRightHanded ? 1 : -1);
//                modelpart1.xRot = Mth.lerp(f2, modelpart1.xRot, (float) (-Math.PI / 2));
//            } else {
//                ModelPart modelpart = model.rightArm;
//                ModelPart modelpart1 = model.leftArm;
//                boolean pRightHanded = entity.getMainArm() == HumanoidArm.RIGHT;
//                modelpart.yRot = pRightHanded ? -0.8F : 0.8F;
//                modelpart.xRot = -0.97079635F;
//                modelpart1.xRot = modelpart.xRot;
//                float f = crossbow.getChargeDurationMod(entity.getUseItem(), entity.level());
//                float f1 = Mth.clamp((float) entity.getTicksUsingItem(), 0.0F, f);
//                float f2 = f1 / f;
//                modelpart1.yRot = Mth.lerp(f2, 0.4F, 0.85F) * (float) (pRightHanded ? 1 : -1);
//                modelpart1.xRot = Mth.lerp(f2, modelpart1.xRot, (float) (-Math.PI / 2));
//            }
//        }
    };

    public static final IArmPoseTransformer DOUBLE_CROSSBOW_HOLD_ARM_POSE_TRANSFORMER = (model, entity, arm) -> {
        model.rightArm.yRot = -0F + model.head.yRot;
        model.leftArm.yRot = 0F + model.head.yRot;
        model.rightArm.xRot = (float) (-Math.PI / 2) + model.head.xRot + 0.1F;
        model.leftArm.xRot = -1.5F + model.head.xRot;
    };


    public static final IClientItemExtensions HUNTER_CROSSBOW = new IClientItemExtensions() {

        @Nullable
        @Override
        public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
            if (itemStack.getItem() instanceof HunterCrossbowItem crossbow) {
                ItemStack otherStack = entityLiving.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
                boolean thisCharged = CrossbowItem.isCharged(itemStack);
                if (crossbow.canUseDoubleCrossbow(entityLiving) && otherStack.getItem() instanceof IHunterCrossbow) {
                    if (thisCharged && CrossbowItem.isCharged(otherStack)) {
                        return entityLiving.swinging ? HumanoidModel.ArmPose.ITEM : ModClientEnums.DOUBLE_CROSSBOW_HOLD.getValue();
                    }
                    if (crossbow.isChargingHand(entityLiving, hand, itemStack)) {
                        return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                    }
                    if (thisCharged && !entityLiving.swinging) {
                        return HumanoidModel.ArmPose.CROSSBOW_HOLD;
                    }
                } else {
                    if (entityLiving.getUsedItemHand() == hand && entityLiving.isUsingItem() && entityLiving.getUseItemRemainingTicks() > 0 && !thisCharged) {
                        return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                    } else if (!entityLiving.swinging && thisCharged) {
                        return HumanoidModel.ArmPose.CROSSBOW_HOLD;
                    }
                }
            }
            return HumanoidModel.ArmPose.ITEM;
        }

        @Override
        public boolean applyForgeHandTransform(PoseStack pPoseStack, LocalPlayer pPlayer, HumanoidArm humanoidarm, ItemStack pStack, float pPartialTicks, float pEquippedProgress, float pSwingProgress) {
            if (pStack.getItem() instanceof HunterCrossbowItem crossbow) {
                boolean mainArm = humanoidarm == pPlayer.getMainArm();
                InteractionHand hand = mainArm ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                applyCrossbowTransform(pPoseStack, pPlayer, humanoidarm, pStack, crossbow, hand, pPartialTicks, pEquippedProgress, pSwingProgress, mainArm);
            }
            return true;
        }

        private void applyCrossbowTransform(PoseStack pPoseStack, LocalPlayer pPlayer, HumanoidArm humanoidarm, ItemStack pStack, HunterCrossbowItem crossbow, InteractionHand hand, float pPartialTicks, float pEquippedProgress, float pSwingProgress, boolean mainArm) {
            boolean flag2 = humanoidarm == HumanoidArm.RIGHT;
            int i = flag2 ? 1 : -1;
            ItemInHandRendererAccessor renderer = (ItemInHandRendererAccessor) Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            if (crossbow.isChargingHand(pPlayer, hand, pStack)) {
                float charge = crossbow.getChargeProgress(pStack, pPlayer, hand);
                renderer.invokeApplyItemArmTransform(pPoseStack, humanoidarm, pEquippedProgress);
                pPoseStack.translate((float) i * -0.4785682F, -0.094387F, 0.05731531F);
                pPoseStack.mulPose(Axis.XP.rotationDegrees(-11.935F));
                pPoseStack.mulPose(Axis.YP.rotationDegrees((float) i * 65.3F));
                pPoseStack.mulPose(Axis.ZP.rotationDegrees((float) i * -9.785F));
                float ticks = charge * crossbow.getChargeDurationMod(pStack, pPlayer.level());
                if (charge > 0.1F) {
                    float f4 = Mth.sin((ticks - 0.1F) * 1.3F) * (charge - 0.1F);
                    pPoseStack.translate(0.0F, f4 * 0.004F, 0.0F);
                }
                pPoseStack.translate(0.0F, 0.0F, charge * 0.04F);
                pPoseStack.scale(1.0F, 1.0F, 1.0F + charge * 0.2F);
                pPoseStack.mulPose(Axis.YN.rotationDegrees((float) i * 45.0F));
            } else {
                float f = -0.4F * Mth.sin(Mth.sqrt(pSwingProgress) * (float) Math.PI);
                float f1 = 0.2F * Mth.sin(Mth.sqrt(pSwingProgress) * (float) (Math.PI * 2));
                float f2 = -0.2F * Mth.sin(pSwingProgress * (float) Math.PI);
                pPoseStack.translate((float) i * f, f1, f2);
                renderer.invokeApplyItemArmTransform(pPoseStack, humanoidarm, pEquippedProgress);
                renderer.invokeApplyItemArmAttackTransform(pPoseStack, humanoidarm, pSwingProgress);
                if (CrossbowItem.isCharged(pStack) && pSwingProgress < 0.001F && mainArm && (!(pPlayer.getOffhandItem().getItem() instanceof HunterCrossbowItem) || !CrossbowItem.isCharged(pPlayer.getOffhandItem()))) {
                    pPoseStack.translate((float) i * -0.641864F, 0.0F, 0.0F);
                    pPoseStack.mulPose(Axis.YP.rotationDegrees((float) i * 10.0F));
                }
            }
        }
    };

    public static final IClientItemExtensions SYRINGE = new IClientItemExtensions() {

        @Override
        public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
            return entityLiving.isUsingItem() && entityLiving.getUseItem() == itemStack ? HumanoidModel.ArmPose.TOOT_HORN : HumanoidModel.ArmPose.EMPTY;
        }

        @Override
        public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
            if (player.isUsingItem() && player.getUseItem() == itemInHand) {
                poseStack.mulPose(Axis.ZN.rotationDegrees(30F));
                poseStack.mulPose(Axis.XP.rotationDegrees(45F));
                poseStack.mulPose(Axis.YP.rotationDegrees(35F));
                poseStack.translate(0.35F, -0.15F, 0.1F);
                poseStack.scale(1.5F, 1.5F, 1.5F);
            }

            return false;
        }
    };

    public static class VampireArmorItemExtension implements IClientItemExtensions {

        private static final Map<ModelLayerLocation, Model<?>> MODELS = new HashMap<>();
        private static final Map<ModelLayerLocation, Function<ModelPart, Model<?>>> MODEL_FACTORIES = new HashMap<>();

        private final ModelLayerLocation location;

        public VampireArmorItemExtension(ModelLayerLocation location, Function<ModelPart, Model<?>> modelFactory) {
            MODEL_FACTORIES.put(location, modelFactory);
            this.location = location;
        }

        @Override
        public Model<?> getHumanoidArmorModel(ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Model original) {
            return MODELS.getOrDefault(this.location, original);
        }

        @SubscribeEvent
        public static void onLayersLoaded(EntityRenderersEvent.AddLayers event) {
            MODELS.clear();
            MODEL_FACTORIES.forEach((location, factory) -> MODELS.put(location, factory.apply(event.getEntityModels().bakeLayer(location))));
        }
    }
}
