package de.teamlapen.vampirism.common.world.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.Weapon;

public class VampirismSwordItem extends Item {

    public VampirismSwordItem(ToolMaterial material, int attackDamageIn, float attackSpeedIn, Properties builder) {
        this(material, attackDamageIn, attackSpeedIn, builder, 0);
    }

    public VampirismSwordItem(ToolMaterial material, int attackDamageIn, float attackSpeedIn, Properties builder, float disableShield) {
        this(builder.sword(material, attackDamageIn, attackSpeedIn).component(DataComponents.WEAPON, new Weapon(1, disableShield)));
    }

    /**
     * For subclasses that apply the sword components themselves
     */
    protected VampirismSwordItem(Properties builder) {
        super(builder.factions$descriptionWithout("_normal|_enhanced|_ultimate"));
    }
}
