package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.vampirism.common.world.heritage.HeritageLibrary;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LecternBlockEntity.class)
public class LecternBlockEntityMixin {

    @Inject(method = "setBook(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)V", at = @At("TAIL"))
    private void vampirism$startHeritageResearch(ItemStack book, @Nullable Player resolutionContext, CallbackInfo ci) {
        HeritageLibrary.onBookPlaced((LecternBlockEntity) (Object) this);
    }
}
