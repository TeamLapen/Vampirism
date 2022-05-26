package de.teamlapen.vampirism.client.core;

import de.teamlapen.lib.lib.client.render.RenderAreaParticleCloud;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.client.model.*;
import de.teamlapen.vampirism.client.model.armor.*;
import de.teamlapen.vampirism.client.render.entities.*;
import de.teamlapen.vampirism.client.render.layers.VampireEntityLayer;
import de.teamlapen.vampirism.client.render.layers.VampirePlayerHeadLayer;
import de.teamlapen.vampirism.client.render.layers.WingsLayer;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.BatRenderer;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Handles entity render registration
 */
@OnlyIn(Dist.CLIENT)
public class ModEntitiesRender {
    private final static Logger LOGGER = LogManager.getLogger();

    public static ModelLayerLocation HUNTER = new ModelLayerLocation(new ResourceLocation("vampirism:hunter"), "main");
    public static ModelLayerLocation HUNTER_SLIM = new ModelLayerLocation(new ResourceLocation("vampirism:slim_hunter"), "main");
    public static ModelLayerLocation COFFIN = new ModelLayerLocation(new ResourceLocation("vampirism:coffin"), "main");
    public static ModelLayerLocation WING = new ModelLayerLocation(new ResourceLocation("vampirism:wing"), "main");
    public static ModelLayerLocation BARON = new ModelLayerLocation(new ResourceLocation("vampirism:baron"), "main");
    public static ModelLayerLocation BARONESS = new ModelLayerLocation(new ResourceLocation("vampirism:baroness"), "main");
    public static ModelLayerLocation BARON_ATTIRE = new ModelLayerLocation(new ResourceLocation("vampirism:baron"), "attire");
    public static ModelLayerLocation BARONESS_ATTIRE = new ModelLayerLocation(new ResourceLocation("vampirism:baroness"), "attire");
    public static ModelLayerLocation CLOAK = new ModelLayerLocation(new ResourceLocation("vampirism:cloak"), "main");
    public static ModelLayerLocation CLOTHING_BOOTS = new ModelLayerLocation(new ResourceLocation("vampirism:clothing"), "boots");
    public static ModelLayerLocation CLOTHING_CROWN = new ModelLayerLocation(new ResourceLocation("vampirism:clothing"), "crown");
    public static ModelLayerLocation CLOTHING_PANTS = new ModelLayerLocation(new ResourceLocation("vampirism:clothing"), "pants");
    public static ModelLayerLocation CLOTHING_HAT = new ModelLayerLocation(new ResourceLocation("vampirism:clothing"), "hat");
    public static ModelLayerLocation HUNTER_HAT0 = new ModelLayerLocation(new ResourceLocation("vampirism:hunter_hat0"), "main");
    public static ModelLayerLocation HUNTER_HAT1 = new ModelLayerLocation(new ResourceLocation("vampirism:hunter_hat1"), "main");
    public static ModelLayerLocation HUNTER_EQUIPMENT = new ModelLayerLocation(new ResourceLocation("vampirism:hunter_equipment"), "main");
    public static ModelLayerLocation VILLAGER_WITH_ARMS = new ModelLayerLocation(new ResourceLocation("vampirism:villager_with_arms"), "main");
    public static ModelLayerLocation GENERIC_BIPED = new ModelLayerLocation(new ResourceLocation("vampirism:generic_biped"), "main");
    public static ModelLayerLocation GENERIC_BIPED_SLIM = new ModelLayerLocation(new ResourceLocation("vampirism:generic_biped"), "main");
    public static ModelLayerLocation GENERIC_BIPED_ARMOR_OUTER = new ModelLayerLocation(new ResourceLocation("vampirism:generic_biped"), "outer_armor");
    public static ModelLayerLocation GENERIC_BIPED_ARMOR_INNER = new ModelLayerLocation(new ResourceLocation("vampirism:generic_biped"), "inner_armor");
    public static ModelLayerLocation TASK_MASTER = new ModelLayerLocation(new ResourceLocation("vampirism:task_master"), "main");


    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.blinding_bat.get(), BatRenderer::new);
        event.registerEntityRenderer(ModEntities.converted_creature_imob.get(), ConvertedCreatureRenderer::new);
        event.registerEntityRenderer(ModEntities.converted_creature.get(), (ConvertedCreatureRenderer::new));
        event.registerEntityRenderer(ModEntities.converted_horse.get(), renderingManager -> {
            HorseRenderer renderer = new HorseRenderer(renderingManager);
            renderer.addLayer(new VampireEntityLayer<>(renderer, new ResourceLocation(REFERENCE.MODID, "textures/entity/vanilla/horse_overlay.png"), false));
            return renderer;
        });
        event.registerEntityRenderer(ModEntities.converted_donkey.get(), (context) -> new ConvertedChestedHorseRenderer<>(context, ModelLayers.DONKEY));
        event.registerEntityRenderer(ModEntities.converted_mule.get(), (context -> new ConvertedChestedHorseRenderer<>(context, ModelLayers.MULE)));
        event.registerEntityRenderer(ModEntities.converted_sheep.get(), (ConvertedCreatureRenderer::new));
        event.registerEntityRenderer(ModEntities.converted_cow.get(), (ConvertedCreatureRenderer::new));
        event.registerEntityRenderer(ModEntities.hunter.get(), (BasicHunterRenderer::new));
        event.registerEntityRenderer(ModEntities.hunter_imob.get(), (BasicHunterRenderer::new));
        event.registerEntityRenderer(ModEntities.vampire.get(), (BasicVampireRenderer::new));
        event.registerEntityRenderer(ModEntities.vampire_imob.get(), (BasicVampireRenderer::new));
        event.registerEntityRenderer(ModEntities.hunter_trainer.get(), e -> new HunterTrainerRenderer(e, true));
        event.registerEntityRenderer(ModEntities.vampire_baron.get(), (VampireBaronRenderer::new));
        event.registerEntityRenderer(ModEntities.advanced_hunter.get(), (AdvancedHunterRenderer::new));
        event.registerEntityRenderer(ModEntities.advanced_hunter_imob.get(), (AdvancedHunterRenderer::new));
        event.registerEntityRenderer(ModEntities.advanced_vampire.get(), (AdvancedVampireRenderer::new));
        event.registerEntityRenderer(ModEntities.advanced_vampire_imob.get(), (AdvancedVampireRenderer::new));
        event.registerEntityRenderer(ModEntities.villager_converted.get(), (ConvertedVillagerRenderer::new));
        event.registerEntityRenderer(ModEntities.villager_angry.get(), HunterVillagerRenderer::new);
        event.registerEntityRenderer(ModEntities.crossbow_arrow.get(), (CrossbowArrowRenderer::new));
        event.registerEntityRenderer(ModEntities.particle_cloud.get(), (RenderAreaParticleCloud::new));
        event.registerEntityRenderer(ModEntities.throwable_item.get(), ThrowableItemRenderer::new);
        event.registerEntityRenderer(ModEntities.dark_blood_projectile.get(), (DarkBloodProjectileRenderer::new));
        event.registerEntityRenderer(ModEntities.soul_orb.get(), SoulOrbRenderer::new);
        event.registerEntityRenderer(ModEntities.hunter_trainer_dummy.get(), e -> new HunterTrainerRenderer(e, false));
        event.registerEntityRenderer(ModEntities.dummy_creature.get(), (DummyRenderer::new));
        event.registerEntityRenderer(ModEntities.vampire_minion.get(), (VampireMinionRenderer::new));
        event.registerEntityRenderer(ModEntities.hunter_minion.get(), (HunterMinionRenderer::new));
        event.registerEntityRenderer(ModEntities.task_master_vampire.get(), (VampireTaskMasterRenderer::new));
        event.registerEntityRenderer(ModEntities.task_master_hunter.get(), (HunterTaskMasterRenderer::new));
    }

    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(HUNTER, BasicHunterModel::createBodyLayer);
        event.registerLayerDefinition(HUNTER_SLIM, BasicHunterModel::createSlimBodyLayer);
        event.registerLayerDefinition(COFFIN, CoffinModel::createLayer);
        event.registerLayerDefinition(WING, WingModel::createLayer);
        event.registerLayerDefinition(BARON, BaronModel::createLayer);
        event.registerLayerDefinition(BARONESS, BaronessModel::createLayer);
        event.registerLayerDefinition(BARON_ATTIRE, BaronAttireModel::createLayer);
        event.registerLayerDefinition(BARONESS_ATTIRE, BaronessAttireModel::createLayer);
        event.registerLayerDefinition(CLOAK, CloakModel::createLayer);
        event.registerLayerDefinition(CLOTHING_BOOTS, ClothingBootsModel::createLayer);
        event.registerLayerDefinition(CLOTHING_CROWN, ClothingCrownModel::createLayer);
        event.registerLayerDefinition(CLOTHING_PANTS, ClothingPantsModel::createLayer);
        event.registerLayerDefinition(CLOTHING_HAT, VampireHatModel::createLayer);
        event.registerLayerDefinition(HUNTER_HAT0, () -> HunterHatModel.createLayer(0, 0));
        event.registerLayerDefinition(HUNTER_HAT1, () -> HunterHatModel.createLayer(0, 1));
        event.registerLayerDefinition(HUNTER_EQUIPMENT, HunterEquipmentModel::createLayer);
        event.registerLayerDefinition(VILLAGER_WITH_ARMS, () -> VillagerWithArmsModel.createLayer(0));
        event.registerLayerDefinition(GENERIC_BIPED, () -> LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, false), 64, 64));
        event.registerLayerDefinition(GENERIC_BIPED_SLIM, () -> LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, true), 64, 64));
        event.registerLayerDefinition(GENERIC_BIPED_ARMOR_INNER, () -> LayerDefinition.create(HumanoidModel.createMesh(LayerDefinitions.INNER_ARMOR_DEFORMATION, 0.0F), 64, 32));
        event.registerLayerDefinition(GENERIC_BIPED_ARMOR_OUTER, () -> LayerDefinition.create(HumanoidModel.createMesh(LayerDefinitions.OUTER_ARMOR_DEFORMATION, 0.0F), 64, 32));
        event.registerLayerDefinition(TASK_MASTER, () -> LayerDefinition.create(VillagerModel.createBodyModel(), 64, 64));
    }

    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        _onAddLayers(event);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Player, Q extends EntityModel<T>, Z extends HumanoidModel<T>, I extends LivingEntity, U extends EntityModel<I>> void _onAddLayers(EntityRenderersEvent.AddLayers event) {

        for (String s : event.getSkins()) {
            LivingEntityRenderer<T,Q> renderPlayer = event.getSkin(s);
            if (renderPlayer != null && renderPlayer.getModel() instanceof HumanoidModel) {
                LivingEntityRenderer<T, Z> renderPlayer2 = (LivingEntityRenderer<T, Z>) renderPlayer;
                renderPlayer2.addLayer(new VampirePlayerHeadLayer<>(renderPlayer2));
                renderPlayer2.addLayer(new WingsLayer<>(renderPlayer2, Minecraft.getInstance().getEntityModels(), player -> VampirePlayer.getOpt(player).map(VampirePlayer::getWingCounter).filter(i -> i > 0).isPresent(), (e, m) -> m.body));
            }
        }
        for (Map.Entry<EntityType<? extends PathfinderMob>, ResourceLocation> entry : VampirismAPI.entityRegistry().getConvertibleOverlay().entrySet()) {
            EntityType<? extends PathfinderMob> type = entry.getKey();
            LivingEntityRenderer<I, U> render = (LivingEntityRenderer<I, U>)event.getRenderer(type);
            if (render == null) {
                LOGGER.error("Did not find renderer for {}", type);
                continue;
            }
            render.addLayer(new VampireEntityLayer<>(render, entry.getValue(), true));
        }
    }

}
