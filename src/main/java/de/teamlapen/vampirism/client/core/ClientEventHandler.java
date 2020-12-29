package de.teamlapen.vampirism.client.core;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.blocks.AltarInspirationBlock;
import de.teamlapen.vampirism.blocks.BloodContainerBlock;
import de.teamlapen.vampirism.blocks.WeaponTableBlock;
import de.teamlapen.vampirism.client.gui.OptifineWarningScreen;
import de.teamlapen.vampirism.client.model.blocks.BakedAltarInspirationModel;
import de.teamlapen.vampirism.client.model.blocks.BakedBloodContainerModel;
import de.teamlapen.vampirism.client.model.blocks.BakedWeaponTableModel;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.player.LevelAttributeModifier;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.SharedMonsterAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
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
    public static void onModelBakeEvent(ModelBakeEvent event) {
        /*
         * Not really a clean solution but it works
         * Bake the each model, then replace the fluid texture with impure blood, then bake it again.
         * Use {@link BakedBloodContainerModel} to render to appropriate one
         */
        try {
            for (int x = 0; x < BakedBloodContainerModel.FLUID_LEVELS; x++) {
                ResourceLocation loc = new ResourceLocation(REFERENCE.MODID, "block/blood_container/fluid_" + (x + 1));
                IUnbakedModel model = event.getModelLoader().getModelOrMissing(loc);
                BakedBloodContainerModel.BLOOD_FLUID_MODELS[x] = model.bakeModel(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y0, loc);
                if (model instanceof BlockModel) {
                    ((BlockModel) model).textures.put("fluid", Either.left(ForgeHooksClient.getBlockMaterial(ModFluids.impure_blood.getAttributes().getStillTexture())));
                    BakedBloodContainerModel.IMPURE_BLOOD_FLUID_MODELS[x] = model.bakeModel(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y0, loc);
                } else {
                    LOGGER.error("Cannot apply impure blood texture to blood container model {}", model);
                    BakedBloodContainerModel.IMPURE_BLOOD_FLUID_MODELS[x] = BakedBloodContainerModel.BLOOD_FLUID_MODELS[x];
                }
            }
            Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();
            ArrayList<ResourceLocation> modelLocations = Lists.newArrayList();

            for (ResourceLocation modelLoc : registry.keySet()) {
                if (modelLoc.getNamespace().equals(REFERENCE.MODID) && modelLoc.getPath().equals(BloodContainerBlock.regName)) {
                    modelLocations.add(modelLoc);
                }
            }

            // replace the registered tank block variants with TankModelFactories

            IBakedModel registeredModel;
            IBakedModel newModel;
            for (ResourceLocation loc : modelLocations) {
                registeredModel = event.getModelRegistry().get(loc);
                newModel = new BakedBloodContainerModel(registeredModel);
                event.getModelRegistry().put(loc, newModel);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load fluid models for blood container", e);
        }

        try {
            for (int x = 0; x < BakedAltarInspirationModel.FLUID_LEVELS; x++) {
                ResourceLocation loc = new ResourceLocation(REFERENCE.MODID, "block/altar_inspiration/blood" + (x + 1));
                IUnbakedModel model = event.getModelLoader().getModelOrMissing(loc);
                BakedAltarInspirationModel.FLUID_MODELS[x] = model.bakeModel(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y0, loc);
            }
            Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();
            ArrayList<ResourceLocation> modelLocations = Lists.newArrayList();

            for (ResourceLocation modelLoc : registry.keySet()) {
                if (modelLoc.getNamespace().equals(REFERENCE.MODID) && modelLoc.getPath().equals(AltarInspirationBlock.regName)) {
                    modelLocations.add(modelLoc);
                }
            }

            // replace the registered tank block variants with TankModelFactories

            IBakedModel registeredModel;
            IBakedModel newModel;
            for (ResourceLocation loc : modelLocations) {
                registeredModel = event.getModelRegistry().get(loc);
                newModel = new BakedAltarInspirationModel(registeredModel);
                event.getModelRegistry().put(loc, newModel);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load fluid models for altar inspiration", e);
        }

        try {
            for (int x = 0; x < BakedWeaponTableModel.FLUID_LEVELS; x++) {
                ResourceLocation loc = new ResourceLocation(REFERENCE.MODID, "block/weapon_table/weapon_table_lava" + (x + 1));
                IUnbakedModel model = event.getModelLoader().getModelOrMissing(loc);
                BakedWeaponTableModel.FLUID_MODELS[x][0] = model.bakeModel(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y180, loc);
                BakedWeaponTableModel.FLUID_MODELS[x][1] = model.bakeModel(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y270, loc);
                BakedWeaponTableModel.FLUID_MODELS[x][2] = model.bakeModel(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y0, loc);
                BakedWeaponTableModel.FLUID_MODELS[x][3] = model.bakeModel(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y90, loc);
            }
            Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();
            ArrayList<ResourceLocation> modelLocations = Lists.newArrayList();

            for (ResourceLocation modelLoc : registry.keySet()) {
                if (modelLoc.getNamespace().equals(REFERENCE.MODID) && modelLoc.getPath().equals(WeaponTableBlock.regName)) {
                    modelLocations.add(modelLoc);
                }
            }

            // replace the registered tank block variants with TankModelFactories

            IBakedModel registeredModel;
            IBakedModel newModel;
            for (ResourceLocation loc : modelLocations) {
                registeredModel = event.getModelRegistry().get(loc);
                newModel = new BakedWeaponTableModel(registeredModel);
                event.getModelRegistry().put(loc, newModel);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load fluid models for weapon crafting table", e);

        }
    }

    public static boolean skipOptifineWarningOnce = false;

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Pre event) {
        if (event.getGui() instanceof WorldSelectionScreen) {
            if (DynamicRegistries.func_239770_b_().func_230521_a_(Registry.BIOME_KEY).map(r -> r.getValueForKey(ModBiomes.VAMPIRE_FOREST_KEY)).orElse(null) == null) {
                if (!skipOptifineWarningOnce) {
                    event.setCanceled(true);
                    Minecraft.getInstance().displayGuiScreen(new OptifineWarningScreen(event.getGui()));
                } else {
                    skipOptifineWarningOnce = false;
                }
            }
        }
    }

    @SubscribeEvent
    public void onFovOffsetUpdate(FOVUpdateEvent event) {
        if (VampirismConfig.CLIENT.disableFovChange.get() && Helper.isVampire(event.getEntity())) {
            ModifiableAttributeInstance speed = event.getEntity().getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
            AttributeModifier vampirespeed = speed.getModifier(LevelAttributeModifier.getUUID(SharedMonsterAttributes.MOVEMENT_SPEED));
            if (vampirespeed == null)
                return;
            //removes speed buffs, add speed buffs without the vampire speed
            event.setNewfov((float) (((double) (event.getFov()) * ((vampirespeed.getAmount() + 1) * (double) (event.getEntity().abilities.getWalkSpeed()) + speed.getValue())) / ((vampirespeed.getAmount() + 1) * ((double) (event.getEntity().abilities.getWalkSpeed()) + speed.getValue()))));
        }
    }
}
