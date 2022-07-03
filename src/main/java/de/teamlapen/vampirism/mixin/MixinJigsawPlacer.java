package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.util.MixinHooks;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;
import java.util.List;

@Mixin(JigsawPlacement.Placer.class)
public abstract class MixinJigsawPlacer {
    @Shadow
    @Final
    private List<? super PoolElementStructurePiece> pieces;

    private MixinJigsawPlacer() {
    }

    @Redirect(method = "tryPlacingChildren", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;", ordinal = 1))
    private Object inject(Iterator<StructurePoolElement> iterator) {
        while (iterator.hasNext()) {
            StructurePoolElement piece = iterator.next();
            if (!MixinHooks.checkStructures(this.pieces, piece)) {
                return piece;
            }
        }
        return EmptyPoolElement.INSTANCE;
    }
}