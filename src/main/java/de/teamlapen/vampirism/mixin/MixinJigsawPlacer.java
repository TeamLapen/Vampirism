package de.teamlapen.vampirism.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import de.teamlapen.vampirism.util.MixinHooks;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import org.jetbrains.annotations.NotNull;
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

    @ModifyReceiver(method = "tryPlacingChildren", at =@At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", ordinal = 1))
    private Iterator inj(Iterator instance) {
        while (instance.hasNext()) {
            StructurePoolElement piece = (StructurePoolElement) instance.next();
            if (!MixinHooks.checkStructures(this.pieces, piece)) {
                return instance;
            }
        }
        return instance;
    }
}