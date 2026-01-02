package de.teamlapen.vampirism.common.world.items.dispenser;

import de.teamlapen.factions.common.core.FactionItems;
import de.teamlapen.vampirism.api.world.entity.IBiteableEntity;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.world.entity.ExtendedCreature;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.items.BloodSyringeFluidHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class SyringeDispenseBehavior extends DefaultDispenseItemBehavior {

    @Override
    protected @NotNull ItemStack execute(@NotNull BlockSource source, ItemStack stack) {
        if (!stack.is(FactionItems.SYRINGE_EMPTY.get())) {
            return super.execute(source, stack);
        }

        ServerLevel level = source.level();
        Direction facing = source.state().getValue(DispenserBlock.FACING);
        BlockPos pos = source.pos().relative(facing);

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos));
        if (entities.isEmpty()) {
            return stack;
        }

        LivingEntity target = entities.getFirst();

        Optional<? extends IBiteableEntity> biteableOpt = switch (target) {
            case PathfinderMob mob when mob.isAlive() -> ExtendedCreature.getSafe(mob);
            case Player targetPlayer -> Optional.of(VampirePlayer.get(targetPlayer));
            case IBiteableEntity biteableEntity -> Optional.of(biteableEntity);
            default -> Optional.empty();
        };

        if (biteableOpt.isEmpty() || !biteableOpt.get().canBeBitten(null)) {
            return stack;
        }

        int drained = biteableOpt.get().onSyringeUse(BloodSyringeFluidHandler.LEVELS_PER_FILL);
        if (drained <= 0) {
            return stack;
        }

        if (!level.isClientSide()) {
            level.playSound(null, target, ModSounds.VAMPIRE_BITE.get(), SoundSource.PLAYERS, 1.0f,  1.0f);
        }

        stack.shrink(1);

        ItemStack filled = new ItemStack(ModItems.SYRINGE_BLOOD.get());

        ItemStack leftover = source.blockEntity().insertItem(filled.copy());
        if (!leftover.isEmpty()) {
            dispense(source, leftover);
        }

        return stack;
    }
}
