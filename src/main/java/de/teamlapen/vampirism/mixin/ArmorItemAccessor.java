package de.teamlapen.vampirism.mixin;

import net.minecraft.item.ArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(ArmorItem.class)
public interface ArmorItemAccessor {

    @Accessor("ARMOR_MODIFIER_UUID_PER_SLOT")
    static UUID[] getModifierUUID_vampirism() {
        throw new IllegalStateException("Mixin not applied");
    }
}
