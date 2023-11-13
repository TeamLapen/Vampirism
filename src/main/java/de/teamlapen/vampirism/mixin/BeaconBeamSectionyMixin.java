package de.teamlapen.vampirism.mixin;

import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BeaconBlockEntity.BeaconBeamSection.class)
public interface BeaconBeamSectionyMixin {

    @Invoker("increaseHeight")
    void invokeIncreaseHeight();
}
