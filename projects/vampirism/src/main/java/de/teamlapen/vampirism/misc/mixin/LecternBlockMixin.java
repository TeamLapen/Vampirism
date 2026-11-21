package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.vampirism.common.world.heritage.HeritageLectern;
import de.teamlapen.vampirism.common.world.heritage.HeritageLibrary;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LecternBlock.class)
public class LecternBlockMixin {

    @Inject(method = "openScreen", at = @At("HEAD"), cancellable = true)
    private void vampirism$openHeritageBook(Level level, BlockPos pos, Player player, CallbackInfo ci) {
        if (HeritageLectern.tryOpen(level, pos, player)) {
            ci.cancel();
        }
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void vampirism$rejectUnnamedHeritageBook(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!state.getValue(LecternBlock.HAS_BOOK) && HeritageLectern.shouldRejectPlacement(stack)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Inject(method = "placeBook", at = @At("TAIL"))
    private static void vampirism$rememberResearcher(@Nullable LivingEntity sourceEntity, Level level, BlockPos pos, BlockState state, ItemStack book, CallbackInfo ci) {
        if (level.getBlockEntity(pos) instanceof LecternBlockEntity lectern) {
            HeritageLibrary.onBookPlacedBy(lectern, sourceEntity);
        }
    }
}
