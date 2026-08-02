package de.teamlapen.vampirism.client.core;

import de.teamlapen.faction.api.util.SafeCast;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IDraculaPlayer;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IWingsEntity;
import de.teamlapen.vampirism.api.world.items.IItemWithTier;
import de.teamlapen.vampirism.client.renderer.entities.DualSplitBipedRenderer;
import de.teamlapen.vampirism.client.renderer.entities.HunterVillagerRenderer;
import de.teamlapen.vampirism.client.renderer.entities.VampireBaronRenderer;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.blocks.CoffinBlock;
import de.teamlapen.vampirism.common.world.entity.ExtendedCreature;
import de.teamlapen.vampirism.common.world.entity.hunter.AggressiveVillagerEntity;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.entity.vampire.VampireBaronEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

public class ModEntityRenderStates {

    public static final ContextKey<Integer> BLOOD = create("blood");
    public static final ContextKey<Integer> MAX_BLOOD = create("max_blood");
    public static final ContextKey<Boolean> POISON_BLOOD = create("poisonous_blood");
    public static final ContextKey<Float> ATTACK_TIME = create("attack_time");
    public static final ContextKey<HumanoidArm> ATTACK_ARM = create("attack_arm");
    public static final ContextKey<Identifier> OVERLAY = create("overlay");
    public static final ContextKey<Boolean> HUNTER = create("hunter");
    public static final ContextKey<Boolean> HUNTER_DISGUISED = create("hunter/disguised");
    public static final ContextKey<Boolean> HUNTER_FULL_COAT = create("hunter/full_coat");
    public static final ContextKey<Boolean> VAMPIRE_DISGUISE = create("vampire/disguise");
    public static final ContextKey<Integer> VAMPIRE_LEVEL = create("vampire/level");
    public static final ContextKey<Integer> VAMPIRE_EYE_TYPE = create("vampire/eye_type");
    public static final ContextKey<Integer> VAMPIRE_FANG_TYPE = create("vampire/fang_type");
    public static final ContextKey<Boolean> VAMPIRE_GLOWING_EYES = create("vampire/glowing_eyes");
    public static final ContextKey<Boolean> VAMPIRE_INVISIBLE = create("vampire/invisible");
    public static final ContextKey<Bat> VAMPIRE_BAT = create("vampire/bat");
    public static final ContextKey<Boolean> VAMPIRE_DBNO = create("vampire/dbno");
    public static final ContextKey<Boolean> VAMPIRE_SLEEPING_IN_COFFIN = create("vampire/sleeping_in_coffin");
    public static final ContextKey<DualSplitBipedRenderer.RenderPart> SPLIT_RENDER_PART = create("split_render_part");
    public static final ContextKey<IWingsEntity.WingsState> DRACULA_WINGS_STATE = create("dracula/wings_state");
    public static final ContextKey<AnimationState> DRACULA_WINGS_FLY = create("dracula/wings_fly");
    public static final ContextKey<AnimationState> DRACULA_WINGS_GROW = create("dracula/wings_grow");
    public static final ContextKey<IWingsEntity.Texture> DRACULA_WINGS_TEXTURE = create("dracula/wings_texture");

    private static <T> ContextKey<T> create(String id) {
        return new ContextKey<>(VIdentifier.mod(id));
    }



    public static void addRenderStateModifier(RegisterRenderStateModifiersEvent event) {
        event.registerEntityModifier(SafeCast.<Class<? extends EntityRenderer<? extends LivingEntity, ? extends LivingEntityRenderState>>>cast(LivingEntityRenderer.class), ModEntityRenderStates::extractExtendedCreature);
        event.registerEntityModifier(SafeCast.<Class<? extends EntityRenderer<? extends LivingEntity, ? extends LivingEntityRenderState>>>cast(LivingEntityRenderer.class), ModEntityRenderStates::extractHunterEntity);
        event.registerEntityModifier(SafeCast.<Class<? extends EntityRenderer<? extends Avatar, ? extends AvatarRenderState>>>cast(AvatarRenderer.class), ModEntityRenderStates::extractHunterPlayer);
        event.registerEntityModifier(SafeCast.<Class<? extends EntityRenderer<? extends Avatar, ? extends AvatarRenderState>>>cast(AvatarRenderer.class), ModEntityRenderStates::extractVampirePlayer);
        event.registerEntityModifier(SafeCast.<Class<? extends EntityRenderer<? extends LivingEntity, ? extends LivingEntityRenderState>>>cast(LivingEntityRenderer.class), ModEntityRenderStates::extractCoffinSleepPosition);
        event.registerEntityModifier(VampireBaronRenderer.class, ModEntityRenderStates::extractWings);
        event.registerEntityModifier(SafeCast.<Class<? extends EntityRenderer<? extends AggressiveVillagerEntity, ? extends VillagerRenderState>>>cast(HunterVillagerRenderer.class), ModEntityRenderStates::extractHunterVillager);
    }

    private static void extractHunterVillager(AggressiveVillagerEntity entity, VillagerRenderState renderState) {
        renderState.setRenderData(ModEntityRenderStates.ATTACK_TIME, entity.getAttackAnim(renderState.partialTick));
        renderState.setRenderData(ModEntityRenderStates.ATTACK_ARM, entity.getMainArm());
    }

    private static void extractWings(VampireBaronEntity livingEntity, VampireBaronRenderer.VampireBaronRenderState livingEntityRenderState) {
        livingEntityRenderState.setRenderData(ModEntityRenderStates.DRACULA_WINGS_STATE, livingEntity.getWingsState());
        livingEntityRenderState.setRenderData(ModEntityRenderStates.DRACULA_WINGS_GROW, livingEntity.growAnimationState());
        livingEntityRenderState.setRenderData(ModEntityRenderStates.DRACULA_WINGS_FLY, livingEntity.flyAnimationState());
    }

    private static void extractExtendedCreature(LivingEntity entity, LivingEntityRenderState renderState) {
        ExtendedCreature.getSafe(entity).ifPresent(creature -> {
            renderState.setRenderData(BLOOD, creature.getBlood());
            renderState.setRenderData(MAX_BLOOD, creature.getMaxBlood());
            renderState.setRenderData(POISON_BLOOD, creature.hasPoisonousBlood());
        });
    }

    private static void extractHunterEntity(LivingEntity entity, LivingEntityRenderState renderState) {
        if (Helper.isHunter(entity)) {
            renderState.setRenderData(HUNTER, true);
        }
    }

    private static void extractHunterPlayer(Avatar avatar, AvatarRenderState state) {
        if (avatar instanceof Player player && Helper.isHunter(player)) {
            HunterPlayer hunter = HunterPlayer.get(player);

            state.setRenderData(HUNTER_DISGUISED, hunter.isDisguised());
            state.setRenderData(HUNTER_FULL_COAT, hunter.getSpecialAttributes().fullHunterCoat == IItemWithTier.Tier.ENHANCED || hunter.getSpecialAttributes().fullHunterCoat == IItemWithTier.Tier.ULTIMATE);
        }
    }

    private static void extractVampirePlayer(Avatar avatar, AvatarRenderState state) {
        if (avatar instanceof Player player && Helper.isVampire(player)) {

            VampirePlayer vampire = VampirePlayer.get(player);

            state.setRenderData(VAMPIRE_DISGUISE, vampire.isDisguised());
            state.setRenderData(VAMPIRE_LEVEL, vampire.getLevel());
            state.setRenderData(VAMPIRE_EYE_TYPE, vampire.getEyeType());
            state.setRenderData(VAMPIRE_FANG_TYPE, vampire.getFangType());
            state.setRenderData(VAMPIRE_GLOWING_EYES, vampire.getGlowingEyes());
            state.setRenderData(VAMPIRE_INVISIBLE, vampire.getSkillProperties().invisible);
            state.setRenderData(VAMPIRE_DBNO, vampire.isDBNO());

            if (vampire.getSkillProperties().bat) {
                Bat bat = player.getData(ModAttachments.VAMPIRE_BAT.get());
                bat.yHeadRot = player.yHeadRot;
                bat.yBodyRot = player.yBodyRot;
                bat.yHeadRotO = player.yHeadRotO;
                bat.yBodyRotO = player.yBodyRotO;
                state.setRenderData(VAMPIRE_BAT, bat);
            }

            IDraculaPlayer.getPresentDracula(player).ifPresent(dracula -> {
                state.setRenderData(DRACULA_WINGS_STATE, dracula.getWingsState());
                state.setRenderData(DRACULA_WINGS_GROW, dracula.growAnimationState());
                state.setRenderData(DRACULA_WINGS_FLY, dracula.flyAnimationState());
                state.setRenderData(DRACULA_WINGS_TEXTURE, vampire.getCustomization().wingsTexture());
            });
        }
    }

    private static void extractCoffinSleepPosition(LivingEntity entity, LivingEntityRenderState renderState) {
        entity.getSleepingPos().map(x -> entity.level().getBlockState(x)).map(BlockBehaviour.BlockStateBase::getBlock).filter(CoffinBlock.class::isInstance).ifPresent(x -> {
            renderState.setRenderData(VAMPIRE_SLEEPING_IN_COFFIN, true);
        });
    }
}
