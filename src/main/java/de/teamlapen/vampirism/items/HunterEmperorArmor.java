package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.util.ArmorMaterial;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class HunterEmperorArmor extends VampirismHunterArmorItem {

    public static final ArmorMaterial ARMOR_MATERIAL = new ArmorMaterial(REFERENCE.MODID + ":hunter_emperor", 30, ArmorMaterial.createReduction(1, 1, 1, 1), 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 0, 0, () -> Ingredient.EMPTY);
    public static final ArmorMaterial ARMOR_MATERIAL_CURSED = new ArmorMaterial(REFERENCE.MODID + ":hunter_emperor_cursed", 0, ArmorMaterial.createReduction(1, 1, 1, 1), 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 0, 0, () -> Ingredient.EMPTY);

    private final boolean cursed;

    public HunterEmperorArmor(boolean cursed, @NotNull ArmorMaterial materialIn, @NotNull Type type, @NotNull Properties props) {
        super(materialIn, type, props);
        this.cursed = cursed;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        return entity instanceof Player player && this.isBoundToPlayer(stack, player) && super.canEquip(stack, armorType, entity);
    }

    public void bindToPlayer(ItemStack stack, Player player) {
        stack.getOrCreateTag().putUUID("bound", player.getUUID());
    }

    public boolean isBoundToPlayer(ItemStack stack, Player player) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            return tag.getUUID("bound").equals(player.getUUID());
        }
        return false;
    }
}
