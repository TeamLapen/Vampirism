package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.vampirism.common.world.blocks.CoffinBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(BedBlock.class)
public class BedBlockMixin {

    @Inject(method = "findStandUpPosition(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/CollisionGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;F)Ljava/util/Optional;", at = @At("HEAD"), cancellable = true)
    private static void vampirism$findStandUpPosition(EntityType<?> entityType, CollisionGetter collisionGetter, BlockPos pos, Direction p_direction, float yRot, CallbackInfoReturnable<Optional<Vec3>> cir) {
        BlockState state = collisionGetter.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof CoffinBlock) {
            cir.setReturnValue(CoffinBlock.findStandUpPosition(entityType, collisionGetter, pos, yRot));
            cir.cancel();
        }
    }
}
