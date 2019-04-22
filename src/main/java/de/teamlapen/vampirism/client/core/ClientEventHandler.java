package de.teamlapen.vampirism.client.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.BlockAltarInspiration;
import de.teamlapen.vampirism.blocks.BlockBloodContainer;
import de.teamlapen.vampirism.blocks.BlockWeaponTable;
import de.teamlapen.vampirism.client.gui.GuiSkills;
import de.teamlapen.vampirism.client.gui.GuiSleepCoffin;
import de.teamlapen.vampirism.client.model.blocks.BakedAltarInspirationModel;
import de.teamlapen.vampirism.client.model.blocks.BakedBloodContainerModel;
import de.teamlapen.vampirism.client.model.blocks.BakedWeaponTableModel;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistrySimple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Handle general client side events
 */
@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {

    private final static int SKILLBUTTONID = 27496;
    private final static ResourceLocation INVENTORY_SKILLS = new ResourceLocation("vampirism", "textures/gui/inventory_skills.png");


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onActionPerformedPre(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if (Configs.gui_skill_button_enable && event.getGui() instanceof GuiInventory) {
            if (event.getButton().id == SKILLBUTTONID) {
                event.getGui().mc.displayGuiScreen(new GuiSkills());
            } else if (event.getButton().id == 10) {
                for (GuiButton e : event.getButtonList()) {
                    if (e.id == SKILLBUTTONID) {
                        ((GuiButtonImage) e).setPosition(((GuiInventory) event.getGui()).getGuiLeft() + 125, event.getGui().height / 2 - 22);
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.world != null && mc.world != null) {
                if ((mc.currentScreen == null || mc.currentScreen instanceof GuiSleepMP) && mc.player.isPlayerSleeping()) {
                    IBlockState state = mc.player.getEntityWorld().getBlockState(mc.player.bedLocation);
                    if (state.getBlock().equals(ModBlocks.block_coffin)) {
                        mc.displayGuiScreen(new GuiSleepCoffin());
                    }
                } else if (mc.currentScreen != null && mc.currentScreen instanceof GuiSleepCoffin && !mc.player.isPlayerSleeping()) {
                    mc.displayGuiScreen(null);
                }
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onInitGuiEventPost(GuiScreenEvent.InitGuiEvent.Post event) {
        if (Configs.gui_skill_button_enable && event.getGui() instanceof GuiInventory && FactionPlayerHandler.get(event.getGui().mc.player).getCurrentFactionPlayer() != null) {
            List<GuiButton> buttonList = event.getButtonList();
            GuiButton button = new GuiButtonImage(SKILLBUTTONID, ((GuiInventory) event.getGui()).getGuiLeft() + 125, event.getGui().height / 2 - 22, 20, 18, 178, 0, 19, INVENTORY_SKILLS);
            buttonList.add(button);
            event.setButtonList(buttonList);
        }
    }

    @SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent event) {
        IModel[] containerFluidModels = new IModel[BakedBloodContainerModel.FLUID_LEVELS];
        try {
            // load the fluid models for the different levels from the .json files

            for (int x = 0; x < BakedBloodContainerModel.FLUID_LEVELS; x++) {
                containerFluidModels[x] = ModelLoaderRegistry.getModel(new ResourceLocation(REFERENCE.MODID + ":block/blood_container/fluid_" + (x + 1)));
            }

            Function<ResourceLocation, TextureAtlasSprite> textureGetter = location -> Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(location.toString());

            IModel retexturedModel;

            //For each registered fluid: Replace the fluid model texture by fluid (still) texture and cache the retextured model

            for (Map.Entry<String, Fluid> entry : FluidRegistry.getRegisteredFluids().entrySet()) {
                for (int x = 0; x < containerFluidModels.length; x++) {
                    retexturedModel = containerFluidModels[x].retexture(new ImmutableMap.Builder<String, String>()
                            .put("fluid", entry.getValue().getStill().toString())
                            .build());

                    BakedBloodContainerModel.FLUID_MODELS[x].put(
                            entry.getKey(),
                            retexturedModel.bake(retexturedModel.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter));

                }
            }

            // get ModelResourceLocations of all tank block variants from the registry

            RegistrySimple<ModelResourceLocation, IBakedModel> registry = (RegistrySimple<ModelResourceLocation, IBakedModel>) event.getModelRegistry();
            ArrayList<ModelResourceLocation> modelLocations = Lists.newArrayList();

            for (ModelResourceLocation modelLoc : registry.getKeys()) {
                if (modelLoc.getNamespace().equals(REFERENCE.MODID)
                        && modelLoc.getPath().equals(BlockBloodContainer.regName)
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
            Function<ResourceLocation, TextureAtlasSprite> textureGetter = location -> Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(location.toString());
            for (int x = 0; x < BakedAltarInspirationModel.FLUID_LEVELS; x++) {
                IModel model = ModelLoaderRegistry.getModel(new ResourceLocation(REFERENCE.MODID + ":block/altar_inspiration/blood" + (x + 1)));
                BakedAltarInspirationModel.FLUID_MODELS[x] = model.bake(model.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter);
            }
            RegistrySimple<ModelResourceLocation, IBakedModel> registry = (RegistrySimple<ModelResourceLocation, IBakedModel>) event.getModelRegistry();
            ArrayList<ModelResourceLocation> modelLocations = Lists.newArrayList();

            for (ModelResourceLocation modelLoc : registry.getKeys()) {
                if (modelLoc.getNamespace().equals(REFERENCE.MODID)
                        && modelLoc.getPath().equals(BlockAltarInspiration.regName)
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

        try {
            Function<ResourceLocation, TextureAtlasSprite> textureGetter = location -> Minecraft.getInstance().getTextureMapBlocks().getAtlasSprite(location.toString());
            for (int x = 0; x < BakedWeaponTableModel.FLUID_LEVELS; x++) {
                IModel model = ModelLoaderRegistry.getModel(new ResourceLocation(REFERENCE.MODID + ":block/weapon_table/weapon_table_lava" + (x + 1)));
                BakedWeaponTableModel.FLUID_MODELS[x] = model.bake(model.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter);
            }
            RegistrySimple<ModelResourceLocation, IBakedModel> registry = (RegistrySimple<ModelResourceLocation, IBakedModel>) event.getModelRegistry();
            ArrayList<ModelResourceLocation> modelLocations = Lists.newArrayList();

            for (ModelResourceLocation modelLoc : registry.getKeys()) {
                if (modelLoc.getNamespace().equals(REFERENCE.MODID)
                        && modelLoc.getPath().equals(BlockWeaponTable.regName)
                        && !modelLoc.getVariant().equals("inventory")) {
                    modelLocations.add(modelLoc);
                }
            }

            // replace the registered tank block variants with TankModelFactories

            IBakedModel registeredModel;
            IBakedModel newModel;
            for (ModelResourceLocation loc : modelLocations) {
                registeredModel = event.getModelRegistry().getObject(loc);
                newModel = new BakedWeaponTableModel(registeredModel);
                event.getModelRegistry().putObject(loc, newModel);
            }
        } catch (Exception e) {
            VampirismMod.log.e("ModelBake", e, "Failed to load fluid models for weapon crafting table");

        }
    }
}
