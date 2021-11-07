package de.teamlapen.vampirism.items;


import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
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

import java.util.function.Supplier;

public class BlessableItem extends VampirismItem {

    private final Supplier<Item> blessedVersion;

    public BlessableItem(String regName, Properties properties, Supplier<Item> blessedVersion) {
        super(regName, properties);
        this.blessedVersion = blessedVersion;
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
        if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() == ModBlocks.church_altar) {
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
                att.blessingSoundReference = VampLib.proxy.createSoundReference(ModSounds.blessing_music, SoundCategory.PLAYERS, player.blockPosition(), 1, 1);
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
        return new ItemStack(blessedVersion.get(), stack.getCount());
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
}
