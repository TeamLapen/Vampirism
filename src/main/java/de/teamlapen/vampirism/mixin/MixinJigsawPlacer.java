package de.teamlapen.vampirism.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.teamlapen.vampirism.util.MixinHooks;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Iterator;
import java.util.List;

@Debug(export = true)
@Mixin(JigsawPlacement.Placer.class)
public abstract class MixinJigsawPlacer {
    @Shadow
    @Final
    private List<? super PoolElementStructurePiece> pieces;

    private MixinJigsawPlacer() {
    }

    @ModifyExpressionValue(method = "tryPlacingChildren", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;", ordinal = 1))
    private Object skipExistingPieces(Object original, @Local(ordinal = 1) Iterator<Object> itr) {
        StructurePoolElement piece = (StructurePoolElement) original;
        do {
            if (!MixinHooks.checkStructures(this.pieces, piece)) {
                return piece;
            }
            piece = itr.hasNext() ? (StructurePoolElement) itr.next() : null;
        } while (piece != null);
        return EmptyPoolElement.INSTANCE;
    }
}