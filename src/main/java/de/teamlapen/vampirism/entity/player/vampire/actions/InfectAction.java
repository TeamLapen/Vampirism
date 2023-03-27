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
        Player player = vampire.getRepresentingPlayer();
        Entity creature =  context.targetEntity().filter(LivingEntity.class::isInstance).filter(target -> {
            if (UtilLib.canReallySee((LivingEntity) target, player, false)) {
                return false;
            }
            return deriveBiteableEntry(target).map(e -> e.tryInfect(vampire)).orElse(false);
        }).orElse(null);
        if(creature != null ){
            player.awardStat(ModStats.infected_creatures);
            player.level.playSound(null, creature.getX(), creature.getY() + 1.5d, creature.getZ(), ModSounds.PLAYER_BITE.get(), SoundSource.PLAYERS, 1, 1);
        }
        else{
            player.level.playSound(null, vampire.getRepresentingPlayer().getX(), vampire.getRepresentingPlayer().getY() + 1.5d, vampire.getRepresentingPlayer().getZ(), SoundEvents.NOTE_BLOCK_BANJO.get(), SoundSource.PLAYERS, 1, 1);
        }
        return creature != null;
    }


    @Override
    public boolean canBeUsedBy(@NotNull IVampirePlayer player) {
        if (player.getRepresentingPlayer().level.getDifficulty() == Difficulty.PEACEFUL) return false;
        if (player.isRemote()) {
            Entity target = VampirismMod.proxy.getMouseOverEntity();
            if (target != null) {
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
            return ExtendedCreature.getSafe(target).resolve();
        } else if (target instanceof Player) {
            return VampirePlayer.getOpt((Player) target).resolve();
        }
        return Optional.empty();
    }
}
