package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.effects.SanguinareEffect;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class VampireFangItem extends Item {
    public VampireFangItem() {
        super(new Properties().tab(VampirismMod.creativeTab));
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand handIn) {

        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide) {
            if (VampirismConfig.SERVER.disableFangInfection.get()) {
                playerIn.displayClientMessage(Component.translatable("text.vampirism.deactivated_by_serveradmin"), true);
            } else {
                if (Helper.canBecomeVampire(playerIn)) {
                    SanguinareEffect.addRandom(playerIn, true);
                    playerIn.addEffect(new MobEffectInstance(ModEffects.POISON.get(), 60));
                } else {
                    if (Helper.isVampire(playerIn)) {
                        playerIn.displayClientMessage(Component.translatable("text.vampirism.already_vampire"), true);
                    } else {
                        playerIn.displayClientMessage(Component.translatable("text.vampirism.immune_to").append(Component.translatable(ModEffects.SANGUINARE.get().getDescriptionId())), true);
                    }
                }
                stack.shrink(1);
            }
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

}
