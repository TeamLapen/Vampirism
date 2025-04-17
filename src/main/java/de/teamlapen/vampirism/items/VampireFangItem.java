package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.effects.SanguinareEffect;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VampireFangItem extends Item {
    public VampireFangItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn) {

        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide) {
            if (VampirismConfig.SERVER.disableFangInfection.get()) {
                playerIn.displayClientMessage(Component.translatable("text.vampirism.deactivated_by_serveradmin"), true);
            } else {
                if (Helper.canBecomeVampire(playerIn)) {
                    SanguinareEffect.addRandom(playerIn, true);
                    playerIn.addEffect(new MobEffectInstance(MobEffects.POISON, 60));
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
        return InteractionResult.SUCCESS_SERVER;
    }

}
