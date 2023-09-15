package de.teamlapen.vampirism.client.core;

import de.teamlapen.lib.lib.client.render.RenderAreaParticleCloud;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.model.*;
import de.teamlapen.vampirism.client.model.armor.*;
import de.teamlapen.vampirism.client.renderer.entity.*;
import de.teamlapen.vampirism.client.renderer.entity.layers.ConvertedVampireEntityLayer;
import de.teamlapen.vampirism.client.renderer.entity.layers.VampirePlayerHeadLayer;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.entity.IVampirismBoat;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Handles entity render registration
 */
@OnlyIn(Dist.CLIENT)
public class ModEntitiesRender {
    private final static Logger LOGGER = LogManager.getLogger();

    public static final ModelLayerLocation HUNTER = new ModelLayerLocation(new ResourceLocation("vampirism:hunter"), "main");
    public static final ModelLayerLocation HUNTER_SLIM = new ModelLayerLocation(new ResourceLocation("vampirism:slim_hunter"), "main");
    public static final ModelLayerLocation COFFIN = new ModelLayerLocation(new ResourceLocation("vampirism:coffin"), "main");
    public static final ModelLayerLocation WING = new ModelLayerLocation(new ResourceLocation("vampirism:wing"), "main");
    public static final ModelLayerLocation BARON = new ModelLayerLocation(new ResourceLocation("vampirism:baron"), "main");
    public static final ModelLayerLocation BARONESS = new ModelLayerLocation(new ResourceLocation("vampirism:baroness"), "main");
    public static final ModelLayerLocation BARON_ATTIRE = new ModelLayerLocation(new ResourceLocation("vampirism:baron"), "attire");
    public static final ModelLayerLocation BARONESS_ATTIRE = new ModelLayerLocation(new ResourceLocation("vampirism:baroness"), "attire");
    public static final ModelLayerLocation CLOAK = new ModelLayerLocation(new ResourceLocation("vampirism:cloak"), "main");
    public static final ModelLayerLocation CLOTHING_BOOTS = new ModelLayerLocation(new ResourceLocation("vampirism:clothing"), "boots");
    public static final ModelLayerLocation CLOTHING_CROWN = new ModelLayerLocation(new ResourceLocation("vampirism:clothing"), "crown");
    public static final ModelLayerLocation CLOTHING_PANTS = new ModelLayerLocation(new ResourceLocation("vampirism:clothing"), "pants");
    public static final ModelLayerLocation CLOTHING_HAT = new ModelLayerLocation(new ResourceLocation("vampirism:clothing"), "hat");
    public static final ModelLayerLocation HUNTER_HAT0 = new ModelLayerLocation(new ResourceLocation("vampirism:hunter_hat0"), "main");
    public static final ModelLayerLocation HUNTER_HAT1 = new ModelLayerLocation(new ResourceLocation("vampirism:hunter_hat1"), "main");
    public static final ModelLayerLocation HUNTER_EQUIPMENT = new ModelLayerLocation(new ResourceLocation("vampirism:hunter_equipment"), "main");
    public static final ModelLayerLocation VILLAGER_WITH_ARMS = new ModelLayerLocation(new ResourceLocation("vampirism:villager_with_arms"), "main");
    public static final ModelLayerLocation GENERIC_BIPED = new ModelLayerLocation(new ResourceLocation("vampirism:generic_biped"), "main");
    public static final ModelLayerLocation GENERIC_BIPED_SLIM = new ModelLayerLocation(new ResourceLocation("vampirism:generic_biped"), "main");
    public static final ModelLayerLocation GENERIC_BIPED_ARMOR_OUTER = new ModelLayerLocation(new ResourceLocation("vampirism:generic_biped"), "outer_armor");
    public static final ModelLayerLocation GENERIC_BIPED_ARMOR_INNER = new ModelLayerLocation(new ResourceLocation("vampirism:generic_biped"), "inner_armor");
    public static final ModelLayerLocation TASK_MASTER = new ModelLayerLocation(new ResourceLocation("vampirism:task_master"), "main");


    static void onRegisterRenderers(EntityRenderersEvent.@NotNull RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.BLINDING_BAT.get(), BatRenderer::new);
        event.registerEntityRenderer(ModEntities.CONVERTED_CREATURE_IMOB.get(), ConvertedCreatureRenderer::new);
        event.registerEntityRenderer(ModEntities.CONVERTED_CREATURE.get(), (ConvertedCreatureRenderer::new));
        event.registerEntityRenderer(ModEntities.CONVERTED_HORSE.get(), convertedRenderer(HorseRenderer::new));
        event.registerEntityRenderer(ModEntities.CONVERTED_DONKEY.get(), convertedRenderer(context -> new ConvertedChestedHorseRenderer<>(context, 0.87f, ModelLayers.DONKEY)));
        event.registerEntityRenderer(ModEntities.CONVERTED_MULE.get(), convertedRenderer(context -> new ConvertedChestedHorseRenderer<>(context, 0.92F, ModelLayers.MULE)));
        event.registerEntityRenderer(ModEntities.CONVERTED_SHEEP.get(), convertedRenderer(SheepRenderer::new));
        event.registerEntityRenderer(ModEntities.CONVERTED_COW.get(), convertedRenderer(CowRenderer::new));
        event.registerEntityRenderer(ModEntities.HUNTER.get(), (BasicHunterRenderer::new));
        event.registerEntityRenderer(ModEntities.HUNTER_IMOB.get(), (BasicHunterRenderer::new));
        event.registerEntityRenderer(ModEntities.VAMPIRE.get(), (BasicVampireRenderer::new));
        event.registerEntityRenderer(ModEntities.VAMPIRE_IMOB.get(), (BasicVampireRenderer::new));
        event.registerEntityRenderer(ModEntities.HUNTER_TRAINER.get(), e -> new HunterTrainerRenderer(e, true));
        event.registerEntityRenderer(ModEntities.VAMPIRE_BARON.get(), (VampireBaronRenderer::new));
        event.registerEntityRenderer(ModEntities.ADVANCED_HUNTER.get(), (AdvancedHunterRenderer::new));
        event.registerEntityRenderer(ModEntities.ADVANCED_HUNTER_IMOB.get(), (AdvancedHunterRenderer::new));
        event.registerEntityRenderer(ModEntities.ADVANCED_VAMPIRE.get(), (AdvancedVampireRenderer::new));
        event.registerEntityRenderer(ModEntities.ADVANCED_VAMPIRE_IMOB.get(), (AdvancedVampireRenderer::new));
        event.registerEntityRenderer(ModEntities.VILLAGER_CONVERTED.get(), convertedRenderer(VillagerRenderer::new));
        event.registerEntityRenderer(ModEntities.VILLAGER_ANGRY.get(), HunterVillagerRenderer::new);
        event.registerEntityRenderer(ModEntities.CROSSBOW_ARROW.get(), (CrossbowArrowRenderer::new));
        event.registerEntityRenderer(ModEntities.PARTICLE_CLOUD.get(), (RenderAreaParticleCloud::new));
        event.registerEntityRenderer(ModEntities.THROWABLE_ITEM.get(), ThrowableItemRenderer::new);
        event.registerEntityRenderer(ModEntities.DARK_BLOOD_PROJECTILE.get(), (DarkBloodProjectileRenderer::new));
        event.registerEntityRenderer(ModEntities.SOUL_ORB.get(), SoulOrbRenderer::new);
        event.registerEntityRenderer(ModEntities.HUNTER_TRAINER_DUMMY.get(), e -> new HunterTrainerRenderer(e, false));
        event.registerEntityRenderer(ModEntities.DUMMY_CREATURE.get(), (DummyRenderer::new));
        event.registerEntityRenderer(ModEntities.VAMPIRE_MINION.get(), (VampireMinionRenderer::new));
        event.registerEntityRenderer(ModEntities.HUNTER_MINION.get(), (HunterMinionRenderer::new));
        event.registerEntityRenderer(ModEntities.TASK_MASTER_VAMPIRE.get(), (VampireTaskMasterRenderer::new));
        event.registerEntityRenderer(ModEntities.TASK_MASTER_HUNTER.get(), (HunterTaskMasterRenderer::new));
        event.registerEntityRenderer(ModEntities.dummy_sit_entity.get(), DummyRenderer::new);
        event.registerEntityRenderer(ModEntities.BOAT.get(), context -> new VampirismBoatRenderer(context, false));
        event.registerEntityRenderer(ModEntities.CHEST_BOAT.get(), context -> new VampirismBoatRenderer(context, true));
        event.registerEntityRenderer(ModEntities.CONVERTED_FOX.get(), convertedRenderer(FoxRenderer::new));
        event.registerEntityRenderer(ModEntities.CONVERTED_GOAT.get(), convertedRenderer(GoatRenderer::new));
    }

    static void onRegisterLayers(EntityRenderersEvent.@NotNull RegisterLayerDefinitions event) {
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

        LayerDefinition boatDefinition = BoatModel.createBodyModel();
        LayerDefinition chestBoatDefinition = ChestBoatModel.createBodyModel();
        for (IVampirismBoat.BoatType type : IVampirismBoat.BoatType.values()) {
            event.registerLayerDefinition(createBoatModelName(type), () -> boatDefinition);
            event.registerLayerDefinition(createChestBoatModelName(type), () -> chestBoatDefinition);
        }

    }

    static void onAddLayers(EntityRenderersEvent.@NotNull AddLayers event) {
        _onAddLayers(event);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Player, Q extends EntityModel<T>, Z extends HumanoidModel<T>, I extends LivingEntity, U extends EntityModel<I>> void _onAddLayers(EntityRenderersEvent.@NotNull AddLayers event) {

        for (String s : event.getSkins()) {
            LivingEntityRenderer<T, Q> renderPlayer = event.getSkin(s);
            if (renderPlayer != null && renderPlayer.getModel() instanceof HumanoidModel) {
                LivingEntityRenderer<T, Z> renderPlayer2 = (LivingEntityRenderer<T, Z>) renderPlayer;
                renderPlayer2.addLayer(new VampirePlayerHeadLayer<>(renderPlayer2));
            }
        }
    }

    public static @NotNull ModelLayerLocation createBoatModelName(IVampirismBoat.@NotNull BoatType type) {
        return new ModelLayerLocation(new ResourceLocation(REFERENCE.MODID, "boat/" + type.getName()), "main");
    }

    public static @NotNull ModelLayerLocation createChestBoatModelName(IVampirismBoat.@NotNull BoatType type) {
        return new ModelLayerLocation(new ResourceLocation(REFERENCE.MODID, "chest_boat/" + type.getName()), "main");
    }

    private static @NotNull <T extends LivingEntity, Z extends EntityModel<T>> EntityRendererProvider<T> convertedRenderer(LivingEntityRendererProvider<T,Z> provider) {
        return context -> {
            LivingEntityRenderer<T, Z> renderer = provider.create(context);
            renderer.addLayer(new ConvertedVampireEntityLayer<>(renderer, false));
            return renderer;
        };
    }

    private interface LivingEntityRendererProvider<T extends LivingEntity,Z extends EntityModel<T>> extends EntityRendererProvider<T> {
        @Override
        @NotNull
        LivingEntityRenderer<T,Z> create(EntityRendererProvider.@NotNull Context pContext);
    }

}
