package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.util.MixinHooks;
import net.minecraft.world.gen.feature.jigsaw.EmptyJigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;
import java.util.List;

@Mixin(JigsawManager.Assembler.class)
public abstract class AssemblerMixin {
    @Shadow
    @Final
    private List<? super AbstractVillagePiece> pieces;

    private AssemblerMixin() {
    }

    @Redirect(method = "tryPlacingChildren(Lnet/minecraft/world/gen/feature/structure/AbstractVillagePiece;Lorg/apache/commons/lang3/mutable/MutableObject;IIZ)V", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;", ordinal = 1))
    private Object inject(Iterator<JigsawPiece> iterator) {
        while (iterator.hasNext()) {
            JigsawPiece piece = iterator.next();
            if (!MixinHooks.checkStructures(this.pieces, piece)) {
                return piece;
            }
        }
        return EmptyJigsawPiece.INSTANCE;
    }
}
