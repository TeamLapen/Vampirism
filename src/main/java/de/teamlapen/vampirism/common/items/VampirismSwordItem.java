package de.teamlapen.vampirism.common.items;

import de.teamlapen.vampirism.api.ItemPropertiesExtension;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ToolMaterial;

public class VampirismSwordItem extends SwordItem {

    public VampirismSwordItem(ToolMaterial material, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(material, attackDamageIn, attackSpeedIn, ItemPropertiesExtension.descriptionWithout(builder, "_normal|_enhanced|_ultimate"));
    }
}
