package de.teamlapen.vampirism.client.core;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.model.blocks.BakedAltarInspirationModel;
import de.teamlapen.vampirism.client.model.blocks.BakedBloodContainerModel;
import de.teamlapen.vampirism.client.model.blocks.BakedWeaponTableModel;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.effects.VampirismPotion;
import de.teamlapen.vampirism.player.LevelAttributeModifier;
import de.teamlapen.vampirism.proxy.ClientProxy;
import de.teamlapen.vampirism.util.Helper;
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
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.ModelEvent.BakingCompleted;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Map;

/**
 * Handle general client side events
 */
@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {
    private final static Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onModelBakeEvent(BakingCompleted event) {
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

        try {
            for (int x = 0; x < BakedWeaponTableModel.FLUID_LEVELS; x++) {
                ResourceLocation loc = new ResourceLocation(REFERENCE.MODID, "block/weapon_table/weapon_table_lava" + (x + 1));
                UnbakedModel model = event.getModelBakery().getModel(loc);
                BakedWeaponTableModel.FLUID_MODELS[x][0] = model.bake(event.getModelBakery(), event.getModelBakery().getAtlasSet()::getSprite, BlockModelRotation.X0_Y180, loc);
                BakedWeaponTableModel.FLUID_MODELS[x][1] = model.bake(event.getModelBakery(), event.getModelBakery().getAtlasSet()::getSprite, BlockModelRotation.X0_Y270, loc);
                BakedWeaponTableModel.FLUID_MODELS[x][2] = model.bake(event.getModelBakery(), event.getModelBakery().getAtlasSet()::getSprite, BlockModelRotation.X0_Y0, loc);
                BakedWeaponTableModel.FLUID_MODELS[x][3] = model.bake(event.getModelBakery(), event.getModelBakery().getAtlasSet()::getSprite, BlockModelRotation.X0_Y90, loc);
            }
            Map<ResourceLocation, BakedModel> registry = event.getModels();
            ArrayList<ResourceLocation> modelLocations = Lists.newArrayList();

            for (ResourceLocation modelLoc : registry.keySet()) {
                if (modelLoc.getNamespace().equals(REFERENCE.MODID) && modelLoc.getPath().equals(ModBlocks.WEAPON_TABLE.getId().getPath())) {
                    modelLocations.add(modelLoc);
                }
            }

            // replace the registered tank block variants with TankModelFactories

            BakedModel registeredModel;
            BakedModel newModel;
            for (ResourceLocation loc : modelLocations) {
                registeredModel = event.getModels().get(loc);
                newModel = new BakedWeaponTableModel(registeredModel);
                event.getModels().put(loc, newModel);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load fluid models for weapon crafting table", e);

        }
    }

    @SubscribeEvent
    public void onFovOffsetUpdate(ComputeFovModifierEvent event) {
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
    public void onToolTip(ItemTooltipEvent event) {
        if (VampirismPotion.isHunterPotion(event.getItemStack(), true).map(Potion::getEffects).map(effectInstances -> effectInstances.stream().map(MobEffectInstance::getEffect).anyMatch(MobEffect::isBeneficial)).orElse(false) && (event.getPlayer() == null || !Helper.isHunter(event.getPlayer()))) {
            event.getToolTip().add(Component.translatable("text.vampirism.hunter_potion.deadly").withStyle(ChatFormatting.DARK_RED));
        }

    }

    @SubscribeEvent
    public void onWorldClosed(WorldEvent.Unload event) {
        ((ClientProxy) VampirismMod.proxy).clearBossBarOverlay();
    }
}
