package de.teamlapen.vampirism.items.enchantment;

import de.teamlapen.vampirism.core.tags.ModItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

/**
 * Makes tech crossbows use less arrows
 */
public class ArrowFrugalityEnchantment extends Enchantment {

    public ArrowFrugalityEnchantment() {
        super(Enchantment.definition(
                        ModItemTags.CROSSBOW_ENCHANTABLE,
                        1,
                        2,
                        Enchantment.dynamicCost(10, 5),
                        Enchantment.constantCost(50),
                        2,
                        EquipmentSlot.MAINHAND));
    }

    @Override
    protected boolean checkCompatibility(@NotNull Enchantment ench) {
        return super.checkCompatibility(ench) && ench != Enchantments.INFINITY;
    }
}
