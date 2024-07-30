package de.teamlapen.vampirism.mixin;

import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.entity.player.IVampirismPlayer;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class MixinPlayerEntity extends LivingEntity implements IVampirismPlayer {

    @Shadow public abstract Either<Player.BedSleepingProblem, Unit> startSleepInBed(BlockPos pBedPos);

    @Unique
    private final VampirismPlayerAttributes vampirismPlayerAttributes = new VampirismPlayerAttributes();

    private MixinPlayerEntity(@NotNull EntityType<? extends LivingEntity> type, @NotNull Level worldIn) {
        super(type, worldIn);
    }

    @Unique
    @Override
    public VampirismPlayerAttributes getVampAtts() {
        return vampirismPlayerAttributes;
    }

    @Inject(method = "canTakeItem", at = @At("HEAD"), cancellable = true)
    private void canTakeItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof IFactionExclusiveItem item) {
            IFaction<?> exclusiveFaction = item.getExclusiveFaction(stack);
            if (exclusiveFaction != null && exclusiveFaction != this.vampirismPlayerAttributes.faction) {
                cir.setReturnValue(false);
            }
        }
    }
}
