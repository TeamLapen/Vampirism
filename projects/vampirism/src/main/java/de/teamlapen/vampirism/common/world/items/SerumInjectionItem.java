package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

public class SerumInjectionItem extends Item {

    public static final Component MASSAGE_RESTRICTION_SYRINGE = Component.translatable("message.vampirism.restriction.syringe");

    public SerumInjectionItem(Properties properties) {
        super(properties);
    }

    public static boolean isBlockedPotion(Holder<Potion> potion) {
        return ModConfig.balance().itBlockedSerumPotions.get().contains(potion.getRegisteredName());
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
        return stack;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        PotionContents potionContents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        potionContents.applyToLivingEntity(player, stack.getOrDefault(DataComponents.POTION_DURATION_SCALE, 0.5F));
        stack.consume(1, player);
        ItemStack emptySyringe = ModItems.SYRINGE_EMPTY.get().getDefaultInstance();
        if (!player.hasInfiniteMaterials() && !player.getInventory().add(emptySyringe)) {
            player.drop(emptySyringe, false);
        }

        return InteractionResult.SUCCESS.withoutItem();
    }
}
