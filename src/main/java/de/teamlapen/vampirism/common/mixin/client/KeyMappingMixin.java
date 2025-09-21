package de.teamlapen.vampirism.common.mixin.client;

import de.teamlapen.lib.util.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(KeyMapping.class)
public class KeyMappingMixin implements KeyMappingAccessor {

    @Shadow private int clickCount;

    @Unique
    @Override
    public void vampirism$clicked() {
        this.clickCount++;
    }
}
