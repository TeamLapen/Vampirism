package de.teamlapen.vampirism.common.world.entity.player.hunter;

import de.teamlapen.faction.common.factions.actions.ActionHandler;
import de.teamlapen.faction.common.factions.skills.SkillHandler;
import de.teamlapen.faction.common.util.AttachmentSynchronization;
import de.teamlapen.faction.misc.extensions.IEffectInstanceWithSource;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IMarshallPlayer;
import de.teamlapen.vampirism.common.advancements.critereon.VampireActionCriterionTrigger;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModAdvancements;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.util.OilUtils;
import de.teamlapen.vampirism.common.util.ScoreboardUtil;
import de.teamlapen.vampirism.common.world.entity.player.CommonFactionPlayer;
import de.teamlapen.vampirism.common.world.entity.player.LevelAttributeModifier;
import de.teamlapen.vampirism.common.world.entity.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.common.world.entity.player.hunter.properties.HunterDisguise;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.items.HunterCoatItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Main class for hunter players
 */
public class HunterPlayer extends CommonFactionPlayer<IHunterPlayer> implements IHunterPlayer {

    public static HunterPlayer get(Player player) {
        return player.getData(ModAttachments.HUNTER_PLAYER);
    }

    private final HunterDisguise disguise;
    private final HunterSkillProperties specialAttributes = new HunterSkillProperties();

    public HunterPlayer(Player player) {
        super(player);
        this.disguise = new HunterDisguise(this);
    }

    @Override
    public AttachmentType<?> getType() {
        return ModAttachments.HUNTER_PLAYER.get();
    }

    @Override
    protected ActionHandler<IHunterPlayer> createActionHandler() {
        return new ActionHandler<>(this);
    }

    @Override
    protected SkillHandler<IHunterPlayer> createSkillHandler() {
        return new SkillHandler<>(this);
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
    public HunterDisguise getDisguise() {
        return this.disguise;
    }

    public HunterSkillProperties getSpecialAttributes() {
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
    public void leaveFaction() {
        IEffectInstanceWithSource.removePotionEffect(player, MobEffects.JUMP_BOOST, HunterSkills.ARMOR_JUMP.getId());
        IEffectInstanceWithSource.removePotionEffect(player, MobEffects.SPEED, HunterSkills.ARMOR_SPEED.getId());
        super.leaveFaction();
    }

    @Override
    public void onLevelChanged(int newLevel) {
        ScoreboardUtil.updateScoreboard(player, ScoreboardUtil.HUNTER_LEVEL_CRITERIA, newLevel);
        applyAttributes();
    }

    private void applyAttributes() {
        LevelAttributeModifier.applyModifier(player, Attributes.ATTACK_DAMAGE, "Hunter", getLevel(), getMaxLevel(), ModConfig.balance().hpStrengthMaxMod.get(), ModConfig.balance().hpStrengthType.get(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE, false);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!isRemote() && player.level().getGameTime() % 5 == 0) {
            tickDeferredArmor();
        }
        int level = getLevel();
        if (level > 0) {
            if (!isRemote()) {
                if (player.level().getGameTime() % 100 == 8) {
                    if (Arrays.stream(EquipmentSlot.values()).filter(i -> i.getType() == EquipmentSlot.Type.HUMANOID_ARMOR).map(player::getItemBySlot).allMatch(i -> i.is(ModItemTags.HUNTER_COAT))) {
                        if (this.getSkillHandler().isSkillEnabled(HunterSkills.ARMOR_JUMP)) {
                            MobEffectInstance mobEffectInstance = new MobEffectInstance(MobEffects.JUMP_BOOST, -1, 0, false, false);
                            mobEffectInstance.factions$addProperty(HunterSkills.ARMOR_JUMP.getId());
                            player.addEffect(mobEffectInstance);
                        } else {
                            IEffectInstanceWithSource.removePotionEffect(player, MobEffects.JUMP_BOOST, HunterSkills.ARMOR_JUMP.getId());
                        }
                        if (this.getSkillHandler().isSkillEnabled(HunterSkills.ARMOR_SPEED)) {
                            MobEffectInstance mobEffectInstance = new MobEffectInstance(MobEffects.SPEED, -1, 0, false, false);
                            mobEffectInstance.factions$addProperty(HunterSkills.ARMOR_SPEED.getId());
                            player.addEffect(mobEffectInstance);
                        } else {
                            IEffectInstanceWithSource.removePotionEffect(player, MobEffects.SPEED, HunterSkills.ARMOR_SPEED.getId());
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
                    this.player.addEffect(new MobEffectInstance(ModEffects.TOXICANT, 120, 0, false, false));
                }
                IEffectInstanceWithSource.removePotionEffect(player, MobEffects.JUMP_BOOST, HunterSkills.ARMOR_JUMP.getId());
                IEffectInstanceWithSource.removePotionEffect(player, MobEffects.SPEED, HunterSkills.ARMOR_SPEED.getId());
            }
        }
        getSpecialAttributes().fullHunterCoat = level > 0 ? HunterCoatItem.isFullyEquipped(player) : null;

    }

    private void tickDeferredArmor() {
        long time = player.level().getGameTime();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;

            ItemStack stack = player.getItemBySlot(slot);
            Long deadline = stack.get(ModDataComponents.DESTRUCTION_DEFERRED_UNTIL);
            if (deadline == null) continue;

            if (time >= deadline) {
                stack.set(ModDataComponents.DESTRUCTION_DEFERRED_UNTIL, (long) -1);
            }
        }
    }

    @Override
    public Component getShortLevelDisplay() {
        if (IMarshallPlayer.getPresent(this.player).isPresent()) {
            return Component.translatable("marshall_title.vampirism.short");
        }
        return super.getShortLevelDisplay();
    }

    @Override
    public Component getLevelDisplay() {
        if (IMarshallPlayer.getPresent(this.player).isPresent()) {
            return Component.translatable("marshall_title.vampirism");
        }
        return super.getLevelDisplay();
    }

    @Nullable
    @Override
    public Component getChatDisplay() {
        if (IMarshallPlayer.getPresent(this.player).isPresent()) {
            return Component.translatable("marshall_title.vampirism");
        }
        return super.getChatDisplay();
    }

    public static class AttachmentOptions extends AttachmentSynchronization.PlayerOptions<HunterPlayer> {
        @Override
        protected HunterPlayer create(Player player) {
            return new HunterPlayer(player);
        }
    }
}
