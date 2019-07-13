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
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.SleepInMultiplayerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Handle general client side events
 */
@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {
    private final Logger LOGGER = LogManager.getLogger();
    private final static ResourceLocation INVENTORY_SKILLS = new ResourceLocation("vampirism", "textures/gui/inventory_skills.png");


    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.world != null && mc.world != null) {
                if ((mc.currentScreen == null || mc.currentScreen instanceof SleepInMultiplayerScreen) && mc.player.isSleeping()) {
                    BlockState state = mc.player.getEntityWorld().getBlockState(mc.player.getBedLocation());
                    if (state.getBlock().equals(ModBlocks.block_coffin)) {
                        mc.displayGuiScreen(new SleepCoffinScreen());
                    }
                } else if (mc.currentScreen != null && mc.currentScreen instanceof SleepCoffinScreen && !mc.player.isSleeping()) {
                    mc.displayGuiScreen(null);
                }
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onInitGuiEventPost(GuiScreenEvent.InitGuiEvent.Post event) {
        if (Configs.gui_skill_button_enable && event.getGui() instanceof InventoryScreen && FactionPlayerHandler.get(event.getGui().getMinecraft().player).getCurrentFactionPlayer() != null) {
            Button button = new ImageButton(((InventoryScreen) event.getGui()).getGuiLeft() + 125, event.getGui().height / 2 - 22, 20, 18, 178, 0, 19, INVENTORY_SKILLS, (context) -> {
                //TODO open SkillGui
            });
            event.addWidget(button);
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

            Function<ResourceLocation, TextureAtlasSprite> textureGetter = location -> Minecraft.getInstance().getTextureMap().getAtlasSprite(location.toString());

            IModel<?> retexturedModel;

            //For each registered fluid: Replace the fluid model texture by fluid (still) texture and cache the retextured model

            Set<Fluid> temp = new LinkedHashSet<>();
            temp.add(ModFluids.blood);
            temp.add(ModFluids.impure_blood);
            for (Fluid f : temp) {//TODO 1.14 create for all fluids
                for (int x = 0; x < containerFluidModels.length; x++) {
                    retexturedModel = containerFluidModels[x].retexture(new ImmutableMap.Builder<String, String>()
                            .put("fluid", f.getStill().toString())
                            .build());

                    BakedBloodContainerModel.FLUID_MODELS[x].put(f.getName(), retexturedModel.bake(ModelLoader.defaultModelGetter(), ModelLoader.defaultTextureGetter(), retexturedModel.getDefaultState(), false, Attributes.DEFAULT_BAKED_FORMAT));

                }
            }

            // get ModelResourceLocations of all tank block variants from the registry

            Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();
            ArrayList<ResourceLocation> modelLocations = Lists.newArrayList();

            for (ResourceLocation modelLoc : registry.keySet()) {
                if (modelLoc.getNamespace().equals(REFERENCE.MODID)
                        && modelLoc.getPath().equals(BloodContainerBlock.regName)
                        ) {
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
            LOGGER.error("ModelBake", e, "Failed to load fluid models for blood container");

            return;
        }


        try {
            Function<ResourceLocation, TextureAtlasSprite> textureGetter = location -> Minecraft.getInstance().getTextureMap().getAtlasSprite(location.toString());
            for (int x = 0; x < BakedAltarInspirationModel.FLUID_LEVELS; x++) {
                IModel<?> model = ModelLoaderRegistry.getModel(new ResourceLocation(REFERENCE.MODID + ":block/altar_inspiration/blood" + (x + 1)));
                BakedAltarInspirationModel.FLUID_MODELS[x] = model.bake(ModelLoader.defaultModelGetter(), ModelLoader.defaultTextureGetter(), model.getDefaultState(), false, Attributes.DEFAULT_BAKED_FORMAT);
            }
            Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();
            ArrayList<ResourceLocation> modelLocations = Lists.newArrayList();

            for (ResourceLocation modelLoc : registry.keySet()) {
                if (modelLoc.getNamespace().equals(REFERENCE.MODID) && modelLoc.getPath().equals(AltarInspirationBlock.regName) && !modelLoc.getVariant().equals("inventory")) {
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
            LOGGER.error("ModelBake", e, "Failed to load fluid models for altar inspiration");

        }

        try {
            Function<ResourceLocation, TextureAtlasSprite> textureGetter = location -> Minecraft.getInstance().getTextureMap().getAtlasSprite(location.toString());
            for (int x = 0; x < BakedWeaponTableModel.FLUID_LEVELS; x++) {
                IModel<?> model = ModelLoaderRegistry.getModel(new ResourceLocation(REFERENCE.MODID + ":block/weapon_table/weapon_table_lava" + (x + 1)));
                BakedWeaponTableModel.FLUID_MODELS[x] = model.bake(ModelLoader.defaultModelGetter(), ModelLoader.defaultTextureGetter(), model.getDefaultState(), false, Attributes.DEFAULT_BAKED_FORMAT);
            }
            Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();
            ArrayList<ResourceLocation> modelLocations = Lists.newArrayList();

            for (ResourceLocation modelLoc : registry.keySet()) {
                if (modelLoc.getNamespace().equals(REFERENCE.MODID) && modelLoc.getPath().equals(WeaponTableBlock.regName) && !modelLoc.getVariant().equals("inventory")) {
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
            LOGGER.error("ModelBake", e, "Failed to load fluid models for weapon crafting table");

        }
    }
}
