package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;

@Mixin(BeaconBlockEntity.class)
public interface BeaconBlockEntityMixin {

    @Invoker("applyEffects")
    static void applyEffects(Level pLevel, BlockPos pPos, int pLevels, @Nullable MobEffect pPrimary, @Nullable MobEffect pSecondary) {
        throw new IllegalStateException("Mixin was not applied");
    }
}
