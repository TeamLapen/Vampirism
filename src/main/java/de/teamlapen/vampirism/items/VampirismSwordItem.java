package de.teamlapen.vampirism.items;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import org.jetbrains.annotations.NotNull;

public class VampirismSwordItem extends SwordItem {
    private String translation_key;


    public VampirismSwordItem(@NotNull Tier material, int attackDamageIn, float attackSpeedIn, @NotNull Properties builder) {
        super(material, builder.attributes(SwordItem.createAttributes(material, attackDamageIn, attackSpeedIn)));
    }

    @NotNull
    @Override
    protected String getOrCreateDescriptionId() {
        if (this.translation_key == null) {
            this.translation_key = super.getOrCreateDescriptionId().replaceAll("_normal|_enhanced|_ultimate", "");
        }

        return this.translation_key;
    }
}
