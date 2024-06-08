package de.teamlapen.vampirism.items;


import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayerSpecialAttribute;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.util.Helper;
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
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BlessableItem extends Item {

    private final Supplier<Item> blessedItem;
    @Nullable
    private final Supplier<Item> enhancedBlessedItem;

    private final static List<BlessableItem> BLESSABLE_ITEMS = new ArrayList<>();

    public static @NotNull List<Recipe> getBlessableRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        for (BlessableItem i : BLESSABLE_ITEMS) {
            recipes.add(new Recipe(false, i, i.blessedItem.get()));
            if (i.enhancedBlessedItem != null) {
                recipes.add(new Recipe(true, i, i.enhancedBlessedItem.get()));
            }
        }
        return recipes;
    }

    public record Recipe(boolean enhanced, BlessableItem input, Item output) {
    }

    public BlessableItem(@NotNull Properties properties, Supplier<Item> blessedItem, @Nullable Supplier<Item> enhancedBlessedItem) {
        super(properties);
        this.blessedItem = blessedItem;
        this.enhancedBlessedItem = enhancedBlessedItem;
        BLESSABLE_ITEMS.add(this);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 316;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() == ModBlocks.ALTAR_CLEANSING.get()) {
            if (!Helper.isHunter(context.getPlayer())) return InteractionResult.PASS;
            context.getPlayer().startUsingItem(context.getHand());
            context.getPlayer().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2));
            context.getPlayer().addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 2));
            return InteractionResult.CONSUME;
        }
        return super.useOn(context);
    }

    @Override
    public void onUseTick(@NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack pStack, int pRemainingUseDuration) {
        if (pRemainingUseDuration == 300 && pLivingEntity.level().isClientSide() && pLivingEntity instanceof Player player) {
            HunterPlayerSpecialAttribute att = HunterPlayer.get(player).getSpecialAttributes();
            if (att.blessingSoundReference != null) {
                att.blessingSoundReference.stopPlaying();
            }
            att.blessingSoundReference = VampLib.proxy.createSoundReference(ModSounds.BLESSING_MUSIC.get(), SoundSource.PLAYERS, pLivingEntity.blockPosition(), 1, 1);
            att.blessingSoundReference.startPlaying();
        }
        if (pRemainingUseDuration % 20 == 1) {

            Vec3 mainPos = UtilLib.getItemPosition(pLivingEntity, pLivingEntity.getUsedItemHand() == InteractionHand.MAIN_HAND);
            for (int j = 0; j < 3; ++j) {
                Vec3 pos = mainPos.add((pLivingEntity.getRandom().nextFloat() - 0.5f) * 0.1f, (pLivingEntity.getRandom().nextFloat() - 0.3f) * 0.9f, (pLivingEntity.getRandom().nextFloat() - 0.5f) * 0.1f);
                UtilLib.spawnParticles(pLivingEntity.level(), ParticleTypes.ENCHANT, pos.x, pos.y, pos.z, 0, 0, 0, 10, 0.4f);
            }
            if (pRemainingUseDuration > 21) {
                pLivingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2));
                pLivingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 2));
            }
        }
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level world, @NotNull LivingEntity livingEntity) {
        if (enhancedBlessedItem != null && livingEntity instanceof Player player) {
            IFactionPlayerHandler handler = FactionPlayerHandler.get(player);
            boolean enhanced = handler.isInFaction(VReference.HUNTER_FACTION) && handler.getCurrentFactionPlayer().map(IFactionPlayer::getSkillHandler).map(s -> s.isSkillEnabled(HunterSkills.ENHANCED_BLESSING)).orElse(false);
            return new ItemStack(enhanced ? enhancedBlessedItem.get() : blessedItem.get(), stack.getCount());
        }
        return new ItemStack(blessedItem.get(), stack.getCount());
    }

    @Override
    public void releaseUsing(@NotNull ItemStack pStack, @NotNull Level world, @NotNull LivingEntity entity, int duration) {
        if (entity.level().isClientSide() && entity instanceof Player player) {
            HunterPlayerSpecialAttribute att = HunterPlayer.get(player).getSpecialAttributes();
            if (att.blessingSoundReference != null) {
                att.blessingSoundReference.stopPlaying();
            }
        }
    }

    public Item getBlessedItem() {
        return blessedItem.get();
    }
}
