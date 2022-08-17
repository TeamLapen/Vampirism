package de.teamlapen.vampirism.client.core;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.model.blocks.BakedAltarInspirationModel;
import de.teamlapen.vampirism.client.model.blocks.BakedBloodContainerModel;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.effects.VampirismPotion;
import de.teamlapen.vampirism.player.LevelAttributeModifier;
import de.teamlapen.vampirism.proxy.ClientProxy;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.ModelEvent.BakingCompleted;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

/**
 * Handle general client side events
 */
@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {
    public static final ResourceLocation COFFIN_SHEET = new ResourceLocation(REFERENCE.MODID, "textures/atlas/coffins.png");
    private final static Logger LOGGER = LogManager.getLogger();


    @SubscribeEvent
    public static void onModelBakeEvent(@NotNull BakingCompleted event) {
        /*
         * Not really a clean solution but it works
         * Bake each model, then replace the fluid texture with impure blood, then bake it again.
         * Use {@link BakedBloodContainerModel} to render to appropriate one
         */
        try {
            for (int x = 0; x < BakedBloodContainerModel.FLUID_LEVELS; x++) {
                ResourceLocation loc = new ResourceLocation(REFERENCE.MODID, "block/blood_container/fluid_" + (x + 1));
                UnbakedModel model = event.getModelBakery().getModel(loc);
                BakedBloodContainerModel.BLOOD_FLUID_MODELS[x] = model.bake(event.getModelBakery(), event.getModelBakery().getAtlasSet()::getSprite, BlockModelRotation.X0_Y0, loc);
                if (model instanceof BlockModel) {
                    ((BlockModel) model).textureMap.put("fluid", Either.left(ForgeHooksClient.getBlockMaterial(IClientFluidTypeExtensions.of(ModFluids.IMPURE_BLOOD.get()).getStillTexture())));
                    BakedBloodContainerModel.IMPURE_BLOOD_FLUID_MODELS[x] = model.bake(event.getModelBakery(), event.getModelBakery().getAtlasSet()::getSprite, BlockModelRotation.X0_Y0, loc);
                } else {
                    LOGGER.error("Cannot apply impure blood texture to blood container model {}", model);
                    BakedBloodContainerModel.IMPURE_BLOOD_FLUID_MODELS[x] = BakedBloodContainerModel.BLOOD_FLUID_MODELS[x];
                }
            }
            Map<ResourceLocation, BakedModel> registry = event.getModels();
            ArrayList<ResourceLocation> modelLocations = Lists.newArrayList();

            for (ResourceLocation modelLoc : registry.keySet()) {
                if (modelLoc.getNamespace().equals(REFERENCE.MODID) && modelLoc.getPath().equals(ModBlocks.BLOOD_CONTAINER.getId().getPath())) {
                    modelLocations.add(modelLoc);
                }
            }

            // replace the registered tank block variants with TankModelFactories

            BakedModel registeredModel;
            BakedModel newModel;
            for (ResourceLocation loc : modelLocations) {
                registeredModel = event.getModels().get(loc);
                newModel = new BakedBloodContainerModel(registeredModel);
                event.getModels().put(loc, newModel);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load fluid models for blood container", e);
        }

        try {
            for (int x = 0; x < BakedAltarInspirationModel.FLUID_LEVELS; x++) {
                ResourceLocation loc = new ResourceLocation(REFERENCE.MODID, "block/altar_inspiration/blood" + (x + 1));
                UnbakedModel model = event.getModelBakery().getModel(loc);
                BakedAltarInspirationModel.FLUID_MODELS[x] = model.bake(event.getModelBakery(), event.getModelBakery().getAtlasSet()::getSprite, BlockModelRotation.X0_Y0, loc);
            }
            Map<ResourceLocation, BakedModel> registry = event.getModels();
            ArrayList<ResourceLocation> modelLocations = Lists.newArrayList();

            for (ResourceLocation modelLoc : registry.keySet()) {
                if (modelLoc.getNamespace().equals(REFERENCE.MODID) && modelLoc.getPath().equals(ModBlocks.ALTAR_INSPIRATION.getId().getPath())) {
                    modelLocations.add(modelLoc);
                }
            }

            // replace the registered tank block variants with TankModelFactories

            BakedModel registeredModel;
            BakedModel newModel;
            for (ResourceLocation loc : modelLocations) {
                registeredModel = event.getModels().get(loc);
                newModel = new BakedAltarInspirationModel(registeredModel);
                event.getModels().put(loc, newModel);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load fluid models for altar inspiration", e);
        }
    }

    @SubscribeEvent
    public void onFovOffsetUpdate(@NotNull ComputeFovModifierEvent event) {
        if (VampirismConfig.CLIENT.disableFovChange.get() && Helper.isVampire(event.getPlayer())) {
            AttributeInstance speed = event.getPlayer().getAttribute(Attributes.MOVEMENT_SPEED);
            AttributeModifier vampirespeed = speed.getModifier(LevelAttributeModifier.getUUID(Attributes.MOVEMENT_SPEED));
            if (vampirespeed == null)
                return;
            //removes speed buffs, add speed buffs without the vampire speed
            event.setNewFovModifier((float) (((double) (event.getFovModifier()) * ((vampirespeed.getAmount() + 1) * (double) (event.getPlayer().getAbilities().getWalkingSpeed()) + speed.getValue())) / ((vampirespeed.getAmount() + 1) * ((double) (event.getPlayer().getAbilities().getWalkingSpeed()) + speed.getValue()))));
        }
    }

    @SubscribeEvent
    public void onToolTip(@NotNull ItemTooltipEvent event) {
        if (VampirismPotion.isHunterPotion(event.getItemStack(), true).map(Potion::getEffects).map(effectInstances -> effectInstances.stream().map(MobEffectInstance::getEffect).anyMatch(MobEffect::isBeneficial)).orElse(false) && (event.getEntity() == null || !Helper.isHunter(event.getEntity()))) {
            event.getToolTip().add(Component.translatable("text.vampirism.hunter_potion.deadly").withStyle(ChatFormatting.DARK_RED));
        }

    }

    @SubscribeEvent
    public void onWorldClosed(LevelEvent.Unload event) {
        ((ClientProxy) VampirismMod.proxy).clearBossBarOverlay();
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelEvent.@NotNull RegisterAdditional event) {
        for (DyeColor dye : DyeColor.values()) {
            event.register(new ResourceLocation(REFERENCE.MODID, "block/coffin/coffin_bottom_" + dye.getName()));
            event.register(new ResourceLocation(REFERENCE.MODID, "block/coffin/coffin_top_" + dye.getName()));
            event.register(new ResourceLocation(REFERENCE.MODID, "block/coffin/coffin_" + dye.getName()));
        }
    }

    @SubscribeEvent
    public void onItemToolTip(@NotNull ItemTooltipEvent event) {
        OilUtils.getAppliedOilStatus(event.getItemStack()).flatMap(pair -> pair.getLeft().getToolTipLine(event.getItemStack(), pair.getKey(), pair.getValue(), event.getFlags())).ifPresent(tooltipLine -> {
            int position = 1;
            int flags = getHideFlags(event.getItemStack());
            if (shouldShowInTooltip(flags, ItemStack.TooltipPart.ADDITIONAL)) ++position;

            event.getToolTip().add(position, tooltipLine);
        });
    }

    private static boolean shouldShowInTooltip(int p_242394_0_, ItemStack.@NotNull TooltipPart p_242394_1_) {
        return (p_242394_0_ & p_242394_1_.getMask()) == 0;
    }

    private int getHideFlags(@NotNull ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("HideFlags", 99) ? stack.getTag().getInt("HideFlags") : 0;
    }
}
