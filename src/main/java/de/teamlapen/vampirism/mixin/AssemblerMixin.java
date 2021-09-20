package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.util.MixinHooks;
import net.minecraft.world.level.levelgen.feature.structures.EmptyPoolElement;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;
import java.util.List;

@Mixin(JigsawPlacement.Placer.class)
public abstract class AssemblerMixin {
    @Shadow
    @Final
    private List<? super PoolElementStructurePiece> pieces;

    private AssemblerMixin() {
    }

    @Redirect(method = "tryPlacingChildren(Lnet/minecraft/world/level/levelgen/structure/PoolElementStructurePiece;Lorg/apache/commons/lang3/mutable/MutableObject;IIZLnet/minecraft/world/level/LevelHeightAccessor;)V", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;", ordinal = 1))
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
