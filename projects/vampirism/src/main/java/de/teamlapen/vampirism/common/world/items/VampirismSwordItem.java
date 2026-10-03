package de.teamlapen.vampirism.common.world.items;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.Weapon;

public class VampirismSwordItem extends Item {

    public VampirismSwordItem(ToolMaterial material, int attackDamageIn, float attackSpeedIn, Properties builder) {
        this(material, attackDamageIn, attackSpeedIn, builder, 0);
    }

    public VampirismSwordItem(ToolMaterial material, int attackDamageIn, float attackSpeedIn, Properties builder, float disableShield) {
        this(material, attackDamageIn, attackSpeedIn, builder, disableShield, true);
    }

    public VampirismSwordItem(ToolMaterial material, int attackDamageIn, float attackSpeedIn, Properties builder, float disableShield, boolean hasDurability) {
        builder.component(DataComponents.WEAPON, new Weapon(1, disableShield)).factions$descriptionWithout("_normal|_enhanced|_ultimate");
            builder.sword(material, attackDamageIn, attackSpeedIn);
            if (!hasDurability) {
                builder.delayedComponent(DataComponents.MAX_DAMAGE, _ -> null);
                builder.delayedComponent(DataComponents.DAMAGE, _ -> null);
                builder.delayedComponent(DataComponents.REPAIRABLE, _ -> null);
            }
        super(builder);
    }
}
