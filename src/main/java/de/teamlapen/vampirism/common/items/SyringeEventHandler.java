package de.teamlapen.vampirism.common.items;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.entity.ExtendedCreature;
import de.teamlapen.vampirism.common.entity.player.vampire.VampirePlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.Optional;

@EventBusSubscriber(modid = REFERENCE.MODID)
public class SyringeEventHandler {

    @SubscribeEvent
    public static void onSyringeMobInteraction(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        ItemStack heldStack = event.getItemStack();
        Entity target = event.getTarget();

        if (heldStack.is(ModItems.SYRINGE_EMPTY)) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);

            if (level.isClientSide) return;

            Optional<? extends IBiteableEntity> biteableOpt = switch (target) {
                case PathfinderMob mob when mob.isAlive() -> ExtendedCreature.getSafe(mob);
                case Player targetPlayer -> Optional.of(VampirePlayer.get(targetPlayer));
                case IBiteableEntity biteableEntity -> Optional.of(biteableEntity);
                default -> Optional.empty();
            };

            biteableOpt.filter(biteable -> biteable.canBeBitten(null)).ifPresent(biteable -> {
                int drained = biteable.onSyringeUse(BloodSyringeFluidHandler.LEVELS_PER_FILL);
                if (drained <= 0) return;

                ItemStack filledStack = new ItemStack(ModItems.SYRINGE_BLOOD.get());

                if (!player.isCreative()) {
                    if (heldStack.getCount() == 1) {
                        player.setItemInHand(event.getHand(), filledStack);
                    } else {
                        heldStack.shrink(1);
                        ItemHandlerHelper.giveItemToPlayer(player, filledStack);
                    }
                } else {
                    ItemHandlerHelper.giveItemToPlayer(player, filledStack);
                }

                level.playSound(null, player.blockPosition(), ModSounds.VAMPIRE_BITE.get(), SoundSource.PLAYERS, 1.0f,  1.0f);
            });
        } else if (heldStack.is(ModItems.INJECTION_GARLIC)) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);

            if (level.isClientSide) return;

            ExtendedCreature.getSafe(target).ifPresent(entity -> entity.setPoisonousBlood(ExtendedCreature.POISONOUS_BLOOD_DOSE_DURATION));

            if (!player.isCreative()) {
                if (heldStack.getCount() == 1) {
                    player.setItemInHand(event.getHand(), heldStack.getCraftingRemainder());
                } else {
                    heldStack.shrink(1);
                    ItemHandlerHelper.giveItemToPlayer(player, heldStack.getCraftingRemainder());
                }
            }

            // TODO: Find some other sound for vaccinating mobs
            level.playSound(null, player.blockPosition(), ModSounds.VAMPIRE_BITE.get(), SoundSource.PLAYERS, 1.0f,  1.0f);
        }
    }
}
