package de.teamlapen.vampirism.items;


import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillPlayer;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BlessableItem extends Item {

    private final Supplier<Item> blessedItem;
    @Nullable
    private final Supplier<Item> enhancedBlessedItem;

    private final static List<BlessableItem> BLESSABLE_ITEMS = new ArrayList<>();

    public static List<Recipe> getBlessableRecipes() {
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

    public BlessableItem(Properties properties, Supplier<Item> blessedItem, @Nullable Supplier<Item> enhancedBlessedItem) {
        super(properties);
        this.blessedItem = blessedItem;
        this.enhancedBlessedItem = enhancedBlessedItem;
        BLESSABLE_ITEMS.add(this);
    }

    @Override
    public UseAction getUseAnimation(ItemStack p_77661_1_) {
        return UseAction.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 316;
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() == ModBlocks.CHURCH_ALTAR.get()) {
            if (!Helper.isHunter(context.getPlayer())) return ActionResultType.PASS;
            context.getPlayer().startUsingItem(context.getHand());
            context.getPlayer().addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 40, 2));
            context.getPlayer().addEffect(new EffectInstance(Effects.BLINDNESS, 40, 2));
            return ActionResultType.CONSUME;
        }
        return super.useOn(context);
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int ticksLeft) {
        if (ticksLeft == 300 && player.level.isClientSide() && player instanceof PlayerEntity) {
            HunterPlayer.getOpt((PlayerEntity) player).map(HunterPlayer::getSpecialAttributes).ifPresent(att -> {
                if (att.blessingSoundReference != null) {
                    att.blessingSoundReference.stopPlaying();
                }
                att.blessingSoundReference = VampLib.proxy.createSoundReference(ModSounds.BLESSING_MUSIC.get(), SoundCategory.PLAYERS, player.blockPosition(), 1, 1);
                att.blessingSoundReference.startPlaying();

            });

        }
        if (ticksLeft % 20 == 1) {

            Vector3d mainPos = UtilLib.getItemPosition(player, player.getUsedItemHand() == Hand.MAIN_HAND);
            for (int j = 0; j < 3; ++j) {
                Vector3d pos = mainPos.add((player.getRandom().nextFloat() - 0.5f) * 0.1f, (player.getRandom().nextFloat() - 0.3f) * 0.9f, (player.getRandom().nextFloat() - 0.5f) * 0.1f);
                UtilLib.spawnParticles(player.level, ParticleTypes.ENCHANT, pos.x, pos.y, pos.z, 0, 0, 0, 10, 0.4f);
            }
            if (ticksLeft > 21) {
                player.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 40, 2));
                player.addEffect(new EffectInstance(Effects.BLINDNESS, 40, 2));
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity livingEntity) {
        if (enhancedBlessedItem != null && livingEntity instanceof PlayerEntity) {
            IFactionPlayerHandler handler = FactionPlayerHandler.get((PlayerEntity) livingEntity);
            boolean enhanced = handler.isInFaction(VReference.HUNTER_FACTION) && handler.getCurrentFactionPlayer().map(ISkillPlayer::getSkillHandler).map(s -> s.isSkillEnabled(HunterSkills.ENHANCED_BLESSING.get())).orElse(false);
            return new ItemStack(enhanced ? enhancedBlessedItem.get() : blessedItem.get(), stack.getCount());
        }
        return new ItemStack(blessedItem.get(), stack.getCount());
    }

    @Override
    public void releaseUsing(ItemStack sttack, World world, LivingEntity player, int duration) {
        if (player.level.isClientSide() && player instanceof PlayerEntity) {
            HunterPlayer.getOpt((PlayerEntity) player).map(HunterPlayer::getSpecialAttributes).ifPresent(att -> {
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
