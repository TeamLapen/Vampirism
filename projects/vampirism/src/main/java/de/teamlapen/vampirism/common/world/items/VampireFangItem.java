package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.effects.SanguinareMobEffect;
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
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            if (ModConfig.server().disableFangInfection.get()) {
                player.displayClientMessage(Component.translatable("text.vampirism.deactivated_by_serveradmin"), true);
            } else {
                if (Helper.canBecomeVampire(player)) {
                    SanguinareMobEffect.addRandom(player, true);
                    player.addEffect(new MobEffectInstance(MobEffects.POISON, 60));
                } else {
                    if (Helper.isVampire(player)) {
                        player.displayClientMessage(Component.translatable("text.vampirism.already_vampire"), true);
                    } else {
                        player.displayClientMessage(Component.translatable("text.vampirism.immune_to").append(Component.translatable(ModEffects.SANGUINARE.get().getDescriptionId())), true);
                    }
                }
                stack.shrink(1);
            }
        }

        return InteractionResult.SUCCESS_SERVER;
    }
}
