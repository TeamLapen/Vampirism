package de.teamlapen.vampirism.client.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import de.teamlapen.vampirism.blocks.AltarInspirationBlock;
import de.teamlapen.vampirism.blocks.BloodContainerBlock;
import de.teamlapen.vampirism.blocks.WeaponTableBlock;
import de.teamlapen.vampirism.client.gui.SleepCoffinScreen;
import de.teamlapen.vampirism.client.model.blocks.BakedAltarInspirationModel;
import de.teamlapen.vampirism.client.model.blocks.BakedBloodContainerModel;
import de.teamlapen.vampirism.client.model.blocks.BakedWeaponTableModel;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.SleepInMultiplayerScreen;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.fluid.EmptyFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
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
        IModel[] containerFluidModels = new IModel[BakedBloodContainerModel.FLUID_LEVELS];
        try {
            // load the fluid models for the different levels from the .json files

            for (int x = 0; x < BakedBloodContainerModel.FLUID_LEVELS; x++) {
                containerFluidModels[x] = ModelLoaderRegistry.getModel(new ResourceLocation(REFERENCE.MODID, "block/blood_container/fluid_" + (x + 1)));
            }

            //For each registered fluid: Replace the fluid model texture by fluid (still) texture and cache the retextured model

            for (Fluid f : ForgeRegistries.FLUIDS) {
                if (f instanceof EmptyFluid)
                    continue;
                for (int x = 0; x < BakedBloodContainerModel.FLUID_LEVELS; x++) {
                    IModel<?> retexturedModel = containerFluidModels[x].retexture(new ImmutableMap.Builder<String, String>().put("fluid", f.getAttributes().getStill(null).toString()).build());

                    BakedBloodContainerModel.FLUID_MODELS[x].put(f, retexturedModel.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y0, Attributes.DEFAULT_BAKED_FORMAT));

                }
            }

            // get ModelResourceLocations of all tank block variants from the registry

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
                IModel<?> model = ModelLoaderRegistry.getModel(new ResourceLocation(REFERENCE.MODID, "block/altar_inspiration/blood" + (x + 1)));
                BakedAltarInspirationModel.FLUID_MODELS[x] = model.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y0, Attributes.DEFAULT_BAKED_FORMAT);
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
                IModel<?> model = ModelLoaderRegistry.getModel(new ResourceLocation(REFERENCE.MODID, "block/weapon_table/weapon_table_lava" + (x + 1)));
                BakedWeaponTableModel.FLUID_MODELS[x][0] = model.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y180, Attributes.DEFAULT_BAKED_FORMAT);
                BakedWeaponTableModel.FLUID_MODELS[x][1] = model.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y270, Attributes.DEFAULT_BAKED_FORMAT);
                BakedWeaponTableModel.FLUID_MODELS[x][2] = model.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y0, Attributes.DEFAULT_BAKED_FORMAT);
                BakedWeaponTableModel.FLUID_MODELS[x][3] = model.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y90, Attributes.DEFAULT_BAKED_FORMAT);
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

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.world != null) {
                if ((mc.currentScreen == null || mc.currentScreen instanceof SleepInMultiplayerScreen) && mc.player.isSleeping()) {
                    BlockState state = mc.player.getEntityWorld().getBlockState(mc.player.getBedLocation());
                    if (state.getBlock().equals(ModBlocks.coffin)) {
                        mc.displayGuiScreen(new SleepCoffinScreen());
                    }
                } else if (mc.currentScreen instanceof SleepCoffinScreen && !mc.player.isSleeping()) {
                    mc.displayGuiScreen(null);
                }
            }

        }
    }
}
