package de.teamlapen.vampirism.items;


import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
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

    public static class Recipe {
        public final boolean enhanced;
        public final BlessableItem input;
        public final Item output;

        public Recipe(boolean enhanced, BlessableItem input, Item output) {
            this.enhanced = enhanced;
            this.input = input;
            this.output = output;
        }
    }

    public BlessableItem(@NotNull Properties properties, Supplier<Item> blessedItem, @Nullable Supplier<Item> enhancedBlessedItem) {
        super(properties.tab(VampirismMod.creativeTab));
        this.blessedItem = blessedItem;
        this.enhancedBlessedItem = enhancedBlessedItem;
        BLESSABLE_ITEMS.add(this);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack p_77661_1_) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
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
    public void onUsingTick(ItemStack stack, @NotNull LivingEntity entity, int ticksLeft) {
        if (ticksLeft == 300 && entity.level.isClientSide() && entity instanceof Player player) {
            HunterPlayer.getOpt(player).map(HunterPlayer::getSpecialAttributes).ifPresent(att -> {
                if (att.blessingSoundReference != null) {
                    att.blessingSoundReference.stopPlaying();
                }
                att.blessingSoundReference = VampLib.proxy.createSoundReference(ModSounds.BLESSING_MUSIC.get(), SoundSource.PLAYERS, entity.blockPosition(), 1, 1);
                att.blessingSoundReference.startPlaying();

            });

        }
        if (ticksLeft % 20 == 1) {

            Vec3 mainPos = UtilLib.getItemPosition(entity, entity.getUsedItemHand() == InteractionHand.MAIN_HAND);
            for (int j = 0; j < 3; ++j) {
                Vec3 pos = mainPos.add((entity.getRandom().nextFloat() - 0.5f) * 0.1f, (entity.getRandom().nextFloat() - 0.3f) * 0.9f, (entity.getRandom().nextFloat() - 0.5f) * 0.1f);
                UtilLib.spawnParticles(entity.level, ParticleTypes.ENCHANT, pos.x, pos.y, pos.z, 0, 0, 0, 10, 0.4f);
            }
            if (ticksLeft > 21) {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2));
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 2));
            }
        }
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity livingEntity) {
        if (enhancedBlessedItem != null && livingEntity instanceof Player player) {
            IFactionPlayerHandler handler = FactionPlayerHandler.get(player);
            boolean enhanced = handler.isInFaction(VReference.HUNTER_FACTION) && handler.getCurrentFactionPlayer().map(IFactionPlayer::getSkillHandler).map(s -> s.isSkillEnabled(HunterSkills.ENHANCED_BLESSING.get())).orElse(false);
            return new ItemStack(enhanced ? enhancedBlessedItem.get() : blessedItem.get(), stack.getCount());
        }
        return new ItemStack(blessedItem.get(), stack.getCount());
    }

    @Override
    public void releaseUsing(ItemStack sttack, Level world, @NotNull LivingEntity entity, int duration) {
        if (entity.level.isClientSide() && entity instanceof Player player) {
            HunterPlayer.getOpt(player).map(HunterPlayer::getSpecialAttributes).ifPresent(att -> {
                if (att.blessingSoundReference != null) {
                    att.blessingSoundReference.stopPlaying();
                }
            });
        }
    }

    public Item getBlessedItem() {
        return blessedItem.get();
    }
}
