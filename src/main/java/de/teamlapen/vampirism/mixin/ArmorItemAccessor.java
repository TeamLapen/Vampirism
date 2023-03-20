package de.teamlapen.vampirism.mixin;

import net.minecraft.world.item.ArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.EnumMap;
import java.util.UUID;

@Mixin(ArmorItem.class)
public interface ArmorItemAccessor {

    @Accessor("ARMOR_MODIFIER_UUID_PER_TYPE")
    static EnumMap<ArmorItem.Type, UUID> getModifierUUID_vampirism() {
        throw new IllegalStateException("Mixin not applied");
    }
}
