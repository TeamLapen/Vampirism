package de.teamlapen.vampirism.common.world.items;


import de.teamlapen.factions.api.factions.IFactionPlayerHandler;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.factions.common.sounds.ISoundHandler;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.util.UtilLib;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterSkillProperties;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class BlessableItem extends Item {

    private final Supplier<? extends Item> blessedItem;
    @Nullable
    private final Supplier<? extends Item> enhancedBlessedItem;

    public BlessableItem(Properties properties, Supplier<? extends Item> blessedItem, @Nullable Supplier<? extends Item> enhancedBlessedItem) {
        super(properties);
        this.blessedItem = blessedItem;
        this.enhancedBlessedItem = enhancedBlessedItem;
    }

    public Item getBlessedItem() {
        return this. blessedItem.get();
    }

    @Nullable
    public Item getEnhancedBlessedItem() {
        return this.enhancedBlessedItem == null ? null : this.enhancedBlessedItem.get();
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack pStack) {
        return ItemUseAnimation.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity p_344979_) {
        return 316;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() == ModBlocks.ALTAR_CLEANSING.get() && context.getPlayer() != null) {
            if (!Helper.isHunter(context.getPlayer())) return InteractionResult.PASS;
            context.getPlayer().startUsingItem(context.getHand());
            context.getPlayer().addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 2));
            context.getPlayer().addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 2));
            return InteractionResult.CONSUME;
        }
        return super.useOn(context);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (remainingUseDuration == 300 && livingEntity.level().isClientSide() && livingEntity instanceof Player player) {
            HunterSkillProperties att = HunterPlayer.get(player).getSpecialAttributes();
            if (att.blessingSoundReference != null) {
                att.blessingSoundReference.stopPlaying();
            }
            att.blessingSoundReference = ISoundHandler.getSoundHandler().createSoundReference(ModSounds.BLESSING_MUSIC.get(), SoundSource.PLAYERS, livingEntity.blockPosition(), 1, 1);
            att.blessingSoundReference.startPlaying();
        }
        if (remainingUseDuration % 20 == 1) {

            Vec3 mainPos = UtilLib.getItemPosition(livingEntity, livingEntity.getUsedItemHand() == InteractionHand.MAIN_HAND);
            for (int j = 0; j < 3; ++j) {
                Vec3 pos = mainPos.add((livingEntity.getRandom().nextFloat() - 0.5f) * 0.1f, (livingEntity.getRandom().nextFloat() - 0.3f) * 0.9f, (livingEntity.getRandom().nextFloat() - 0.5f) * 0.1f);
                UtilLib.spawnParticles(livingEntity.level(), ParticleTypes.ENCHANT, pos.x, pos.y, pos.z, 0, 0, 0, 10, 0.4f);
            }
            if (remainingUseDuration > 21) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 2));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 2));
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (enhancedBlessedItem != null && livingEntity instanceof Player player) {
            IFactionPlayerHandler handler = FactionPlayerHandler.get(player);
            boolean enhanced = handler.isInFaction(ModFactions.HUNTER) && handler.getSkillHandler().map(s -> s.isSkillEnabled(HunterSkills.ENHANCED_BLESSING)).orElse(false);
            return new ItemStack(enhanced ? enhancedBlessedItem.get() : blessedItem.get(), stack.getCount());
        }
        return new ItemStack(blessedItem.get(), stack.getCount());
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity.level().isClientSide() && entity instanceof Player player) {
            HunterSkillProperties att = HunterPlayer.get(player).getSpecialAttributes();
            if (att.blessingSoundReference != null) {
                att.blessingSoundReference.stopPlaying();
            }
        }
        return false;
    }
}
