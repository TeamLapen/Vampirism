package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.world.entity.IBiteableEntity;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.world.entity.ExtendedCreature;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;

public class ItemEventHandler {

    @SubscribeEvent
    public void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof VampireSwordItem sword) {
            event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(BuiltInRegistries.ITEM.getKey(sword), sword.getAttackDamageModifier(stack), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(BuiltInRegistries.ITEM.getKey(sword), sword.getSpeedModifier(stack), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            event.addModifier(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(BuiltInRegistries.ITEM.getKey(sword).withSuffix("_purity"), sword.getPurityArmorToughnessModifier(stack) , AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            event.addModifier(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(BuiltInRegistries.ITEM.getKey(sword).withSuffix("_purity"), sword.getPurityInteractionRangeModifier(stack) , AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        }
    }

    @SubscribeEvent
    public void onSyringeMobInteraction(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        ItemStack heldStack = event.getItemStack();
        Entity target = event.getTarget();

        if (heldStack.is(ModItems.SYRINGE_EMPTY)) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);

            if (level.isClientSide()) return;

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
                        player.addItem(filledStack);
                    }
                } else {
                    player.addItem(filledStack);
                }

                level.playSound(null, player.blockPosition(), ModSounds.VAMPIRE_BITE.get(), SoundSource.PLAYERS, 1.0f,  1.0f);
            });
        } else if (heldStack.is(ModItems.INJECTION_GARLIC)) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);

            if (level.isClientSide()) return;

            ExtendedCreature.getSafe(target).ifPresent(entity -> entity.setPoisonousBlood(ExtendedCreature.POISONOUS_BLOOD_DOSE_DURATION));

            if (!player.isCreative()) {
                if (heldStack.getCount() == 1) {
                    player.setItemInHand(event.getHand(), heldStack.getCraftingRemainder());
                } else {
                    heldStack.shrink(1);
                    player.addItem(heldStack.getCraftingRemainder());
                }
            }

            // TODO: Find some other sound for vaccinating mobs
            level.playSound(null, player.blockPosition(), ModSounds.VAMPIRE_BITE.get(), SoundSource.PLAYERS, 1.0f,  1.0f);
        }
    }
}
