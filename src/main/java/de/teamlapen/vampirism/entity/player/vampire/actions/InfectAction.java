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
import net.minecraft.sounds.SoundSource;
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
        return context.targetEntity().filter(LivingEntity.class::isInstance).map(target -> {
            if (!UtilLib.canReallySee((LivingEntity) target, player, false)) {
                return false;
            }
            if (deriveBiteableEntry(target).map(e -> e.tryInfect(vampire)).orElse(false)) {
                player.awardStat(ModStats.infected_creatures);
                player.level.playSound(null, target.getX(), target.getY() + 1.5d, target.getZ(), ModSounds.PLAYER_BITE.get(), SoundSource.PLAYERS, 1, 1);
                return true;
            }
            return false;
        }).orElse(false);
    }


    @Override
    public boolean canBeUsedBy(@NotNull IVampirePlayer player) {
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
