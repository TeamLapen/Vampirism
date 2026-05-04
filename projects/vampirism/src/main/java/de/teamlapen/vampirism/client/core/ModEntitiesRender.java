package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.models.armor.*;
import de.teamlapen.vampirism.client.models.blocks.BloodSphereModel;
import de.teamlapen.vampirism.client.models.blocks.CoffinModel;
import de.teamlapen.vampirism.client.models.entities.*;
import de.teamlapen.vampirism.client.models.entities.dracula.DraculaPhase1Model;
import de.teamlapen.vampirism.client.models.entities.dracula.DraculaPhase2Model;
import de.teamlapen.vampirism.client.models.entities.dracula.DraculaPhase3Model;
import de.teamlapen.vampirism.client.models.entities.flying_needle.FlyingNeedleModel;
import de.teamlapen.vampirism.client.renderer.entities.*;
import de.teamlapen.vampirism.client.renderer.entities.layers.ConvertedVampireEntityLayer;
import de.teamlapen.vampirism.client.renderer.entities.layers.VampirePlayerHeadLayer;
import de.teamlapen.vampirism.common.core.ModEntities;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Handles entity render registration
 */
public class ModEntitiesRender {
    public static final ModelLayerLocation COFFIN = new ModelLayerLocation(VIdentifier.mod("coffin"), "main");
    public static final ModelLayerLocation BLOOD_SPHERE = new ModelLayerLocation(VIdentifier.mod("blood_sphere"), "main");
    public static final ModelLayerLocation WING = new ModelLayerLocation(VIdentifier.mod("wing"), "main");
    public static final ModelLayerLocation BARON = new ModelLayerLocation(VIdentifier.mod("baron"), "main");
    public static final ModelLayerLocation BARONESS = new ModelLayerLocation(VIdentifier.mod("baroness"), "main");
    public static final ModelLayerLocation DRACULA_PHASE_1 = new ModelLayerLocation(VIdentifier.mod("dracula/phase1"), "main");
    public static final ModelLayerLocation DRACULA_PHASE_2 = new ModelLayerLocation(VIdentifier.mod("dracula/phase2"), "main");
    public static final ModelLayerLocation DRACULA_PHASE_3 = new ModelLayerLocation(VIdentifier.mod("dracula/phase3"), "main");
    public static final ModelLayerLocation BARON_ATTIRE = new ModelLayerLocation(VIdentifier.mod("baron"), "attire");
    public static final ModelLayerLocation CLOAK = new ModelLayerLocation(VIdentifier.mod("cloak"), "main");
    public static final ModelLayerLocation BARONESS_ATTIRE = new ModelLayerLocation(VIdentifier.mod("baroness"), "attire");
    public static final ModelLayerLocation CLOTHING_BOOTS = new ModelLayerLocation(VIdentifier.mod("clothing"), "boots");
    public static final ModelLayerLocation CLOTHING_CROWN = new ModelLayerLocation(VIdentifier.mod("clothing"), "crown");
    public static final ModelLayerLocation CLOTHING_PANTS = new ModelLayerLocation(VIdentifier.mod("clothing"), "pants");
    public static final ModelLayerLocation CLOTHING_HAT = new ModelLayerLocation(VIdentifier.mod("clothing"), "hat");
    public static final ModelLayerLocation HUNTER_HAT_TALL = new ModelLayerLocation(VIdentifier.mod("hunter_hat_tall"), "main");
    public static final ModelLayerLocation HUNTER_HAT_BROAD = new ModelLayerLocation(VIdentifier.mod("hunter_hat_broad"), "main");
    public static final ModelLayerLocation HUNTER_EQUIPMENT = new ModelLayerLocation(VIdentifier.mod("hunter_equipment"), "main");
    public static final ModelLayerLocation VILLAGER_WITH_ARMS = new ModelLayerLocation(VIdentifier.mod("villager_with_arms"), "main");
    public static final ModelLayerLocation TASK_MASTER = new ModelLayerLocation(VIdentifier.mod("task_master"), "main");
    public static final ModelLayerLocation REMAINS_DEFENDER = new ModelLayerLocation(VIdentifier.mod("remains_defender"), "main");
    public static final ModelLayerLocation GHOST = new ModelLayerLocation(VIdentifier.mod("ghost"), "main");
    public static final ModelLayerLocation DARK_SPRUCE_BOAT = new ModelLayerLocation(VIdentifier.mod("boat/dark_spruce"), "main");
    public static final ModelLayerLocation DARK_SPRUCE_CHEST_BOAT = new ModelLayerLocation(VIdentifier.mod("chest_boat/dark_spruce"), "main");
    public static final ModelLayerLocation CURSED_SPRUCE_BOAT = new ModelLayerLocation(VIdentifier.mod("boat/cursed_spruce"), "main");
    public static final ModelLayerLocation CURSED_SPRUCE_CHEST_BOAT = new ModelLayerLocation(VIdentifier.mod("chest_boat/cursed_spruce"), "main");
    public static final ModelLayerLocation FLYING_NEEDLE = new ModelLayerLocation(VIdentifier.mod("flying_needle"), "main");


    public static void onRegisterRenderers(EntityRenderersEvent.@NotNull RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.BLINDING_BAT.get(), BatRenderer::new);
        event.registerEntityRenderer(ModEntities.CONVERTED_CREATURE_IMOB.get(), ConvertedCreatureRenderer::new);
        event.registerEntityRenderer(ModEntities.CONVERTED_CREATURE.get(), (ConvertedCreatureRenderer::new));
        event.registerEntityRenderer(ModEntities.CONVERTED_HORSE.get(), convertedRenderer(HorseRenderer::new));
        event.registerEntityRenderer(ModEntities.CONVERTED_DONKEY.get(), convertedRenderer(context -> new DonkeyRenderer<>(context, EquipmentClientInfo.LayerType.DONKEY_SADDLE, ModelLayers.DONKEY_SADDLE, DonkeyRenderer.Type.DONKEY, DonkeyRenderer.Type.DONKEY_BABY)));
        event.registerEntityRenderer(ModEntities.CONVERTED_MULE.get(), convertedRenderer(context -> new DonkeyRenderer<>(context, EquipmentClientInfo.LayerType.MULE_SADDLE, ModelLayers.MULE_SADDLE, DonkeyRenderer.Type.MULE, DonkeyRenderer.Type.MULE_BABY)));
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
        event.registerEntityRenderer(ModEntities.PARTICLE_CLOUD.get(), (NoopRenderer::new));
        event.registerEntityRenderer(ModEntities.THROWABLE_ITEM.get(), ThrowableItemRenderer::new);
        event.registerEntityRenderer(ModEntities.DARK_BLOOD_PROJECTILE.get(), (DarkBloodProjectileRenderer::new));
        event.registerEntityRenderer(ModEntities.BLOOD_PROJECTILE.get(), (DarkBloodProjectileRenderer::new));
        event.registerEntityRenderer(ModEntities.SOUL_ORB.get(), SoulOrbRenderer::new);
        event.registerEntityRenderer(ModEntities.HUNTER_TRAINER_DUMMY.get(), e -> new HunterTrainerRenderer(e, false));
        event.registerEntityRenderer(ModEntities.DUMMY_CREATURE.get(), (DummyRenderer::new));
        event.registerEntityRenderer(ModEntities.VAMPIRE_MINION.get(), (VampireMinionRenderer::new));
        event.registerEntityRenderer(ModEntities.HUNTER_MINION.get(), (HunterMinionRenderer::new));
        event.registerEntityRenderer(ModEntities.TASK_MASTER_VAMPIRE.get(), (VampireTaskMasterRenderer::new));
        event.registerEntityRenderer(ModEntities.TASK_MASTER_HUNTER.get(), (HunterTaskMasterRenderer::new));
        event.registerEntityRenderer(ModEntities.SIT.get(), DummyRenderer::new);
        event.registerEntityRenderer(ModEntities.CONVERTED_FOX.get(), convertedRenderer(FoxRenderer::new));
        event.registerEntityRenderer(ModEntities.CONVERTED_GOAT.get(), convertedRenderer(GoatRenderer::new));
        event.registerEntityRenderer(ModEntities.VULNERABLE_REMAINS_DUMMY.get(), DummyRenderer::new);
        event.registerEntityRenderer(ModEntities.REMAINS_DEFENDER.get(), RemainsDefenderRenderer::new);
        event.registerEntityRenderer(ModEntities.GHOST.get(), GhostRenderer::new);
        event.registerEntityRenderer(ModEntities.CONVERTED_CAMEL.get(), convertedRenderer(CamelRenderer::new));
        event.registerEntityRenderer(ModEntities.CONVERTED_CAT.get(), convertedRenderer(CatRenderer::new));
        event.registerEntityRenderer(ModEntities.DARK_SPRUCE_BOAT.get(), context -> new BoatRenderer(context, DARK_SPRUCE_BOAT));
        event.registerEntityRenderer(ModEntities.DARK_SPRUCE_CHEST_BOAT.get(), context -> new BoatRenderer(context, DARK_SPRUCE_CHEST_BOAT));
        event.registerEntityRenderer(ModEntities.CURSED_SPRUCE_BOAT.get(), context -> new BoatRenderer(context, CURSED_SPRUCE_BOAT));
        event.registerEntityRenderer(ModEntities.CURSED_SPRUCE_CHEST_BOAT.get(), context -> new BoatRenderer(context, CURSED_SPRUCE_CHEST_BOAT));
        event.registerEntityRenderer(ModEntities.DRACULA.get(), DraculaRenderer::new);
        event.registerEntityRenderer(ModEntities.FLYING_SWORD.get(), FlyingSwordRenderer::new);
        event.registerEntityRenderer(ModEntities.FLYING_NEEDLE.get(), FlyingNeedleRenderer::new);
    }

    public static void onRegisterLayers(EntityRenderersEvent.@NotNull RegisterLayerDefinitions event) {
        event.registerLayerDefinition(COFFIN, CoffinModel::createLayer);
        event.registerLayerDefinition(BLOOD_SPHERE, BloodSphereModel::createLayer);
        event.registerLayerDefinition(WING, WingModel::createLayer);
        event.registerLayerDefinition(BARON, BaronModel::createLayer);
        event.registerLayerDefinition(BARONESS, BaronessModel::createLayer);
        event.registerLayerDefinition(BARON_ATTIRE, BaronAttireModel::createLayer);
        event.registerLayerDefinition(CLOAK, CloakModel::createCloakLayer);
        event.registerLayerDefinition(BARONESS_ATTIRE, BaronessAttireModel::createLayer);
        event.registerLayerDefinition(CLOTHING_BOOTS, VampirismArmorLayerDefinitions::vampireBootsDefinition);
        event.registerLayerDefinition(CLOTHING_CROWN, VampirismArmorLayerDefinitions::vampireCrownDefinition);
        event.registerLayerDefinition(CLOTHING_PANTS, VampirismArmorLayerDefinitions::vampirePantsDefinition);
        event.registerLayerDefinition(CLOTHING_HAT, VampirismArmorLayerDefinitions::vampireHatDefinition);
        event.registerLayerDefinition(HUNTER_HAT_TALL, VampirismArmorLayerDefinitions::hunterTallHatDefinition);
        event.registerLayerDefinition(HUNTER_HAT_BROAD, VampirismArmorLayerDefinitions::hunterBroadHatLayer);
        event.registerLayerDefinition(VILLAGER_WITH_ARMS, () -> VillagerWithArmsModel.createLayer(0));
        event.registerLayerDefinition(TASK_MASTER, () -> LayerDefinition.create(VillagerModel.createBodyModel(), 64, 64));
        event.registerLayerDefinition(REMAINS_DEFENDER, RemainsDefenderModel::createBodyLayer);
        event.registerLayerDefinition(GHOST, GhostModel::createMesh);
        LayerDefinition boatDefinition = BoatModel.createBoatModel();
        LayerDefinition chestBoatDefinition = BoatModel.createChestBoatModel();
        event.registerLayerDefinition(DARK_SPRUCE_BOAT, () -> boatDefinition);
        event.registerLayerDefinition(DARK_SPRUCE_CHEST_BOAT, () -> chestBoatDefinition);
        event.registerLayerDefinition(CURSED_SPRUCE_BOAT, () -> boatDefinition);
        event.registerLayerDefinition(CURSED_SPRUCE_CHEST_BOAT, () -> chestBoatDefinition);
        event.registerLayerDefinition(DRACULA_PHASE_1, DraculaPhase1Model::createBodyLayer);
        event.registerLayerDefinition(DRACULA_PHASE_2, DraculaPhase2Model::createBodyLayer);
        event.registerLayerDefinition(DRACULA_PHASE_3, DraculaPhase3Model::createBodyLayer);
        event.registerLayerDefinition(FLYING_NEEDLE, FlyingNeedleModel::createBodyLayer);
    }

    public static void onAddLayers(EntityRenderersEvent.@NotNull AddLayers event) {
        _onAddLayers(event);
    }

    @SuppressWarnings("unchecked")
    private static <S extends Player, T extends AvatarRenderState, Q extends EntityModel<T>> void _onAddLayers(EntityRenderersEvent.@NotNull AddLayers event) {

        for (PlayerModelType s : event.getSkins()) {
            AvatarRenderer<AbstractClientPlayer> renderPlayer = event.getPlayerRenderer(s);
            if (renderPlayer != null && renderPlayer.getModel() instanceof PlayerModel) {
                LivingEntityRenderer<S, T, PlayerModel> renderPlayer2 = (LivingEntityRenderer<S, T, PlayerModel>) renderPlayer;
                renderPlayer2.addLayer(new VampirePlayerHeadLayer<>(renderPlayer2));
            }
        }
    }

    private static @NotNull <T extends LivingEntity, U extends LivingEntityRenderState, Z extends EntityModel<? super U>, O extends LivingEntityRenderState, P extends EntityModel<O>> EntityRendererProvider<T> convertedRenderer(LivingEntityRendererProvider<T,U,Z> provider) {
        return context -> {
            //noinspection unchecked
            var renderer = (LivingEntityRenderer<T, O, P>) provider.create(context);
            renderer.addLayer(new ConvertedVampireEntityLayer<>(renderer, false));
            return renderer;
        };
    }

    private interface LivingEntityRendererProvider<T extends LivingEntity, U extends LivingEntityRenderState, Z extends EntityModel<? super U>> extends EntityRendererProvider<T> {
        @Override
        @NotNull
        LivingEntityRenderer<T, U, Z> create(EntityRendererProvider.@NotNull Context pContext);
    }

}
