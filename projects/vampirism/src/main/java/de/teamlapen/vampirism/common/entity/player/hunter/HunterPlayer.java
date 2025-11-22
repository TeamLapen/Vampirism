package de.teamlapen.vampirism.common.entity.player.hunter;

import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.common.util.AttachmentSynchronization;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.factions.api.factions.IDisguise;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.common.advancements.critereon.VampireActionCriterionTrigger;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModAdvancements;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.common.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.common.entity.player.CommonFactionPlayer;
import de.teamlapen.vampirism.common.entity.player.LevelAttributeModifier;
import de.teamlapen.vampirism.common.entity.player.VampirismPlayerAttributes;
import de.teamlapen.factions.common.actions.ActionHandler;
import de.teamlapen.vampirism.common.entity.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.common.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.factions.common.skills.SkillHandler;
import de.teamlapen.vampirism.common.items.HunterCoatItem;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.util.OilUtils;
import de.teamlapen.vampirism.common.util.ScoreboardUtil;
import de.teamlapen.factions.common.minions.MinionWorldData;
import de.teamlapen.factions.misc.extensions.IEffectInstanceWithSource;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * Main class for hunter players
 */
public class HunterPlayer extends CommonFactionPlayer<IHunterPlayer> implements IHunterPlayer {

    public static HunterPlayer get(Player player) {
        return player.getData(ModAttachments.HUNTER_PLAYER);
    }

    private final Disguise disguise;
    private final HunterPlayerSpecialAttribute specialAttributes = new HunterPlayerSpecialAttribute();

    public HunterPlayer(Player player) {
        super(player);
        this.disguise = new Disguise();
    }

    @Override
    public AttachmentType<?> getType() {
        return ModAttachments.HUNTER_PLAYER.get();
    }

    @Override
    public AttachmentType<? extends IFactionPlayer<?>> attachmentType() {
        return ModAttachments.HUNTER_PLAYER.get();
    }

    @Override
    protected ActionHandler<IHunterPlayer> createActionHandler() {
        return new ActionHandler<>(this);
    }

    @Override
    protected SkillHandler<IHunterPlayer> createSkillHandler() {
        return new SkillHandler<>(this, ModFactions.HUNTER);
    }

    @Override
    public void breakDisguise() {
        this.getActionHandler().deactivateAction(HunterActions.DISGUISE_HUNTER);
    }

    @Override
    public boolean canLeaveFaction() {
        return true;
    }

    @Override
    public IDisguise getDisguise() {
        return this.disguise;
    }

    @Override
    public int getMaxLevel() {
        return REFERENCE.HIGHEST_HUNTER_LEVEL;
    }

    @Override
    public Predicate<LivingEntity> getNonFriendlySelector(boolean otherFactionPlayers, boolean ignoreDisguise) {
        if (otherFactionPlayers) {
            return entity -> true;
        } else {
            return VampirismAPI.factionRegistry().getPredicate(getFaction(), ignoreDisguise);
        }
    }

    /**
     * You can use {@link VampirismPlayerAttributes#getHuntSpecial()} instead if you don't have the hunter player already
     */
    public HunterPlayerSpecialAttribute getSpecialAttributes() {
        return this.specialAttributes;
    }

    @Override
    public boolean isDisguised() {
        return player.hasEffect(ModEffects.DISGUISE_AS_VAMPIRE);
    }

    @Override
    public void onDeath(DamageSource src) {
        super.onDeath(src);
        if (src.getEntity() instanceof ServerPlayer && Helper.isVampire(((Player) src.getEntity())) && this.asEntity().getEffect(ModEffects.FREEZE) != null) {
            ModAdvancements.TRIGGER_VAMPIRE_ACTION.get().trigger(((ServerPlayer) src.getEntity()), VampireActionCriterionTrigger.Action.KILL_FROZEN_HUNTER);
        }
    }

    @Override
    public void onLevelChanged(int level, int oldLevel) {
        super.onLevelChanged(level, oldLevel);
        if (!isRemote()) {
            ScoreboardUtil.updateScoreboard(player, ScoreboardUtil.HUNTER_LEVEL_CRITERIA, level);
            LevelAttributeModifier.applyModifier(player, Attributes.ATTACK_DAMAGE, "Hunter", level, getMaxLevel(), ModConfig.BALANCE.hpStrengthMaxMod.get(), ModConfig.BALANCE.hpStrengthType.get(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE, false);
            if (level == 0) {
                IEffectInstanceWithSource.removePotionEffect(player, MobEffects.JUMP_BOOST, HunterSkills.ARMOR_JUMP.getId());
                IEffectInstanceWithSource.removePotionEffect(player, MobEffects.SPEED, HunterSkills.ARMOR_SPEED.getId());
            }
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        int level = getLevel();
        if (level > 0) {
            if (!isRemote()) {
                if (player.level().getGameTime() % 100 == 8) {
                    if (Arrays.stream(EquipmentSlot.values()).filter(i -> i.getType() == EquipmentSlot.Type.HUMANOID_ARMOR).map(player::getItemBySlot).allMatch(i -> i.is(ModItemTags.HUNTER_ARMOR))) {
                        if (this.getSkillHandler().isSkillEnabled(HunterSkills.ARMOR_JUMP)) {
                            MobEffectInstance mobEffectInstance = new MobEffectInstance(MobEffects.JUMP_BOOST, -1, 0, false, false);
                            mobEffectInstance.factions$addProperty(HunterSkills.ARMOR_JUMP.getId());
                            player.addEffect(mobEffectInstance);
                        }
                        if (this.getSkillHandler().isSkillEnabled(HunterSkills.ARMOR_SPEED)) {
                            MobEffectInstance mobEffectInstance = new MobEffectInstance(MobEffects.SPEED, -1, 0, false, false);
                            mobEffectInstance.factions$addProperty(HunterSkills.ARMOR_SPEED.getId());
                            player.addEffect(mobEffectInstance);
                        }
                    } else {
                        IEffectInstanceWithSource.removePotionEffect(player, MobEffects.JUMP_BOOST, HunterSkills.ARMOR_JUMP.getId());
                        IEffectInstanceWithSource.removePotionEffect(player, MobEffects.SPEED, HunterSkills.ARMOR_SPEED.getId());
                    }
                }
            } else {
                if (getSpecialAttributes().blessingSoundReference != null && !player.isUsingItem()) {
                    //Make sure the blessing sound is stopped when player is not using {@link BlessableItem}. This is necessary because onReleaseUsing is not called for other client players.
                    getSpecialAttributes().blessingSoundReference.stopPlaying();
                    getSpecialAttributes().blessingSoundReference = null;
                }
                VampirismMod.proxy.handleSleepClient(player);

            }
        } else {
            if (this.player.level().getGameTime() % 100 == 16) {
                if (!OilUtils.getEquippedArmorOils(this.player).isEmpty()) {
                    this.player.addEffect(new MobEffectInstance(ModEffects.POISON, 120, 0, false, false));
                }
                IEffectInstanceWithSource.removePotionEffect(player, MobEffects.JUMP_BOOST, HunterSkills.ARMOR_JUMP.getId());
                IEffectInstanceWithSource.removePotionEffect(player, MobEffects.SPEED, HunterSkills.ARMOR_SPEED.getId());
            }
        }
        getSpecialAttributes().fullHunterCoat = level > 0 ? HunterCoatItem.isFullyEquipped(player) : null;

    }

    @Override
    public void updateMinionAttributes(boolean increasedStats) {
        MinionWorldData.getData(this.player.level()).ifPresent(a -> {
            a.getOrCreateController(FactionPlayerHandler.get(this.player)).contactMinions((minion) -> {
                (minion.getMinionData()).ifPresent(b -> ((HunterMinionEntity.HunterMinionData) b).setIncreasedStats(increasedStats));
//                SyncHelper.sync(minion); TODO
            });
        });
    }

    public class Disguise implements IDisguise {

        @Override
        public Holder<? extends IPlayableFaction<?>> actualFaction() {
            return getFaction();
        }

        @Override
        public Holder<? extends IPlayableFaction<?>> getViewedFaction(@Nullable Holder<? extends IFaction<?>> viewerFaction) {
            return player.hasEffect(ModEffects.DISGUISE_AS_VAMPIRE) ? ModFactions.VAMPIRE : actualFaction();
        }

        @Override
        public void disguiseAs(@Nullable Holder<? extends IFaction<?>> faction) {

        }

        @Override
        public void unDisguise() {

        }

        @Override
        public boolean isDisguised() {
            return false;
        }
    }

    public static class AttachmentOptions extends AttachmentSynchronization.PlayerOptions<HunterPlayer> {
        @Override
        protected HunterPlayer create(Player player) {
            return new HunterPlayer(player);
        }
    }
}
