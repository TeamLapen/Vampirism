package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.faction.api.factions.skills.ISkillHandler;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Repairable;
import net.minecraft.world.item.equipment.Equippable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ShatteredArmorItem extends Item {

    public ShatteredArmorItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    /**
     * Replaces the armor piece with its shattered remains wherever it currently is, instead of letting it break.
     *
     * @return whether the piece was replaced and must not be broken by the caller
     */
    public static boolean shatterOnBreak(ItemStack armor, @Nullable LivingEntity owner) {
        if (!(owner instanceof Player player) || !armor.isDamageableItem()) return false;

        Equippable equippable = armor.get(DataComponents.EQUIPPABLE);
        if (equippable == null || equippable.slot().getType() != EquipmentSlot.Type.HUMANOID_ARMOR) return false;
        if (!ISkillHandler.isSkillEnabled(player, HunterSkills.NEAR_BREACH_REFORGING)) return false;

        return replace(player, armor, shatter(armor));
    }

    /**
     * Locates where the armor piece is located, might be outside the expected slots, and replaces it.
     * If it cannot find anything, the item is just handed back to the player.
     */
    private static boolean replace(Player player, ItemStack armor, ItemStack shattered) {
        for (EquipmentSlot slot : EquipmentSlot.VALUES) {
            if (player.getItemBySlot(slot) == armor) {
                player.setItemSlot(slot, shattered);
                return true;
            }
        }

        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i) == armor) {
                inventory.setItem(i, shattered);
                return true;
            }
        }

        inventory.placeItemBackInInventory(shattered);
        return true;
    }

    public static ItemStack shatter(ItemStack armor) {
        ItemStack broken = armor.copy();
        if (broken.isDamageableItem()) {
            broken.setDamageValue(broken.getMaxDamage());
        }

        ItemStack shattered = ModItems.SHATTERED_ARMOR.toStack();
        shattered.set(ModDataComponents.SHATTERED_ARMOR, ItemStackTemplate.fromNonEmptyStack(broken).withCount(1));
        Equippable equippable = armor.get(DataComponents.EQUIPPABLE);
        if (equippable != null) {
            // Only take the necessary just in case
            shattered.set(DataComponents.EQUIPPABLE, Equippable.builder(equippable.slot())
                    .setEquipSound(equippable.equipSound())
                    .setDispensable(equippable.dispensable())
                    .setSwappable(equippable.swappable())
                    .setDamageOnHurt(false)
                    .build());
        }

        return shattered;
    }

    public static ItemStack repair(ItemStack shattered) {
        ItemStack armor = contained(shattered);
        if (armor.isEmpty() || !armor.isDamageableItem()) return armor;

        armor.setDamageValue(Math.max(0, armor.getDamageValue() - Math.max(1, armor.getMaxDamage() / 4)));

        return armor;
    }

    public static ItemStack contained(ItemStack shattered) {
        ItemStackTemplate armor = shattered.get(ModDataComponents.SHATTERED_ARMOR);
        return armor == null ? ItemStack.EMPTY : armor.create();
    }

    public static List<Item> getValidRecoveryItems(ItemStack shattered) {
        ItemStack contained = contained(shattered);

        Repairable repairable = contained.get(DataComponents.REPAIRABLE);
        if (repairable == null) return List.of();

        List<Item> items = new ArrayList<>(repairable.items().stream().map(Holder::value).toList());
        if (items.contains(Items.NETHERITE_INGOT) && !items.contains(Items.DIAMOND)) {
            items.add(Items.DIAMOND);
        }
        if (items.isEmpty()) {
            items.add(Items.LEATHER);
        }

        return items;
    }

    @Override
    public Component getName(ItemStack stack) {
        ItemStackTemplate armor = stack.get(ModDataComponents.SHATTERED_ARMOR);
        if (armor == null) return super.getName(stack);

        return armor.create().getStyledHoverName();
    }

    @Override
    public @Nullable String getCreatorModId(HolderLookup.Provider registries, ItemStack itemStack) {
        ItemStackTemplate armor = itemStack.get(ModDataComponents.SHATTERED_ARMOR);
        if (armor == null) return super.getCreatorModId(registries, itemStack);

        return BuiltInRegistries.ITEM.getKey(armor.item().value()).getNamespace();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        ItemStackTemplate armor = stack.get(ModDataComponents.SHATTERED_ARMOR);
        if (armor == null) return;

        builder.accept(Component.translatable("tooltip.vampirism.shattered_armor.desc").withStyle(ChatFormatting.DARK_GRAY));
        getValidRecoveryItems(stack).forEach(item ->
                builder.accept(Component.literal("  ").append(item.getDefaultInstance().getItemName().copy().withStyle(ChatFormatting.GRAY)))
        );
    }
}