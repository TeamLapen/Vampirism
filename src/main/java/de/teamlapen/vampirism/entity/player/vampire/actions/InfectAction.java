package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public class InfectAction extends DefaultVampireAction {

    @Override
    public int getCooldown(IVampirePlayer player) {
        return 10;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    protected boolean activate(@NotNull IVampirePlayer vampire, @NotNull ActivationContext context) {
        Player player = vampire.asEntity();
        Entity creature =  context.targetEntity().filter(LivingEntity.class::isInstance).filter(target -> {
            if (UtilLib.canReallySee((LivingEntity) target, player, false)) {
                return false;
            }
            return deriveBiteableEntry(target).map(e -> e.tryInfect(vampire)).orElse(false);
        }).orElse(null);
        if(creature != null ){
            player.awardStat(ModStats.INFECTED_CREATURES.get());
            player.level().playSound(null, creature.getX(), creature.getY() + 1.5d, creature.getZ(), ModSounds.VAMPIRE_BITE.get(), SoundSource.PLAYERS, 1, 1);
        } else {
            player.playNotifySound(SoundEvents.NOTE_BLOCK_BANJO.value(), SoundSource.PLAYERS, 1, 1);
        }
        return creature != null;
    }


    @Override
    public boolean canBeUsedBy(@NotNull IVampirePlayer player) {
        if (player.asEntity().level().getDifficulty() == Difficulty.PEACEFUL) return false;
        if (player.isRemote()) {
            Entity target = VampirismMod.proxy.getMouseOverEntity();
            if (target != null) {
                if (UtilLib.canReallySee((LivingEntity) target, player.asEntity(), false)) {
                    return false;
                }
                return deriveBiteableEntry(target).map(b -> b.canBeInfected(player)).orElse(false);
            }
            return false;
        }
        return true;
    }

    private @NotNull Optional<? extends IBiteableEntity> deriveBiteableEntry(Entity target) {
        if (target instanceof IBiteableEntity) {
            return Optional.of((IBiteableEntity) target);
        } else if (target instanceof PathfinderMob) {
            return ExtendedCreature.getSafe(target);
        } else if (target instanceof Player) {
            return Optional.of(VampirePlayer.get((Player) target));
        }
        return Optional.empty();
    }
}
