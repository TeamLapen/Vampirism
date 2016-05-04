package de.teamlapen.vampirism.client.core;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.BlockAltarInspiration;
import de.teamlapen.vampirism.blocks.BlockBloodContainer;
import de.teamlapen.vampirism.client.gui.GuiSleepCoffin;
import de.teamlapen.vampirism.client.model.blocks.BakedAltarInspirationModel;
import de.teamlapen.vampirism.client.model.blocks.BakedBloodContainerModel;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistrySimple;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Map;

/**
 * Handle general client side events
 */
public class ClientEventHandler {
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer != null && mc.theWorld != null) {
                if ((mc.currentScreen == null || mc.currentScreen instanceof GuiSleepMP) && mc.thePlayer.isPlayerSleeping()) {
                    IBlockState state = mc.thePlayer.worldObj.getBlockState(mc.thePlayer.playerLocation);
                    if (state.getBlock().equals(ModBlocks.coffin)) {
                        mc.displayGuiScreen(new GuiSleepCoffin());
                    }
                } else if (mc.currentScreen != null && mc.currentScreen instanceof GuiSleepCoffin && !mc.thePlayer.isPlayerSleeping()) {
                    mc.displayGuiScreen(null);
                }
            }

        }
    }

    @SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent event) {
        IRetexturableModel[] containerFluidModels = new IRetexturableModel[BakedBloodContainerModel.FLUID_LEVELS];
        try {
            // load the fluid models for the different levels from the .json files

            for (int x = 0; x < BakedBloodContainerModel.FLUID_LEVELS; x++) {
                containerFluidModels[x] = (IRetexturableModel) ModelLoaderRegistry.getModel(new ResourceLocation(REFERENCE.MODID + ":block/bloodContainer/fluid_" + String.valueOf(x + 1)));
            }

            Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
                @Override
                public TextureAtlasSprite apply(ResourceLocation location) {
                    return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
                }
            };

            IModel retexturedModel;

            //For each registered fluid: Replace the fluid model texture by fluid (still) texture and cache the retextured model

            for (Map.Entry<String, Fluid> entry : FluidRegistry.getRegisteredFluids().entrySet()) {
                for (int x = 0; x < containerFluidModels.length; x++) {
                    retexturedModel = containerFluidModels[x].retexture(new ImmutableMap.Builder()
                            .put("fluid", entry.getValue().getStill().toString())
                            .build());

                    BakedBloodContainerModel.FLUID_MODELS[x].put(
                            entry.getKey(),
                            retexturedModel.bake(retexturedModel.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter));

                }
            }

            // get ModelResourceLocations of all tank block variants from the registry

            RegistrySimple<ModelResourceLocation, IBakedModel> registry = (RegistrySimple) event.getModelRegistry();
            ArrayList<ModelResourceLocation> modelLocations = Lists.newArrayList();

            for (ModelResourceLocation modelLoc : registry.getKeys()) {
                if (modelLoc.getResourceDomain().equals(REFERENCE.MODID)
                        && modelLoc.getResourcePath().equals(BlockBloodContainer.regName)
                        ) {
                    modelLocations.add(modelLoc);
                }
            }

            // replace the registered tank block variants with TankModelFactories

            IBakedModel registeredModel;
            IBakedModel newModel;
            for (ModelResourceLocation loc : modelLocations) {
                registeredModel = event.getModelRegistry().getObject(loc);
                newModel = new BakedBloodContainerModel(registeredModel);
                event.getModelRegistry().putObject(loc, newModel);
            }

        } catch (Exception e) {
            VampirismMod.log.e("ModelBake", e, "Failed to load fluid models for blood container");

            return;
        }


        try {
            Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
                @Override
                public TextureAtlasSprite apply(ResourceLocation location) {
                    return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
                }
            };
            for (int x = 0; x < BakedAltarInspirationModel.FLUID_LEVELS; x++) {
                IModel model = ModelLoaderRegistry.getModel(new ResourceLocation(REFERENCE.MODID + ":block/altarInspiration/blood" + String.valueOf(x + 1)));
                BakedAltarInspirationModel.FLUID_MODELS[x] = model.bake(model.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter);
            }
            RegistrySimple<ModelResourceLocation, IBakedModel> registry = (RegistrySimple) event.getModelRegistry();
            ArrayList<ModelResourceLocation> modelLocations = Lists.newArrayList();

            for (ModelResourceLocation modelLoc : registry.getKeys()) {
                if (modelLoc.getResourceDomain().equals(REFERENCE.MODID)
                        && modelLoc.getResourcePath().equals(BlockAltarInspiration.regName)
                        && !modelLoc.getVariant().equals("inventory")) {
                    modelLocations.add(modelLoc);
                }
            }

            // replace the registered tank block variants with TankModelFactories

            IBakedModel registeredModel;
            IBakedModel newModel;
            for (ModelResourceLocation loc : modelLocations) {
                registeredModel = event.getModelRegistry().getObject(loc);
                newModel = new BakedAltarInspirationModel(registeredModel);
                event.getModelRegistry().putObject(loc, newModel);
            }
        } catch (Exception e) {
            VampirismMod.log.e("ModelBake", e, "Failed to load fluid models for altar inspiration");

        }
    }
}
