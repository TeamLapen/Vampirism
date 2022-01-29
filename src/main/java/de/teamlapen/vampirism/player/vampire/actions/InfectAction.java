package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;

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
    protected boolean activate(IVampirePlayer vampire, ActivationContext context) {
        Player player = vampire.getRepresentingPlayer();
        return context.targetEntity().filter(LivingEntity.class::isInstance).map(target -> {
            if (!UtilLib.canReallySee((LivingEntity) target, player, false)) {
                return false;
            }
            Optional<? extends IBiteableEntity> b = Optional.empty();
            if (target instanceof IBiteableEntity) {
                b = Optional.of((IBiteableEntity) target);
            } else if (target instanceof PathfinderMob) {
                b = ExtendedCreature.getSafe(target).resolve();
            } else if (target instanceof Player) {
                b = VampirePlayer.getOpt((Player) target).resolve();
            }
            if (b.map(e -> e.tryInfect(vampire)).orElse(false)) {
                vampire.getRepresentingPlayer().awardStat(ModStats.infected_creatures);
                return true;
            }
            return false;
        }).orElse(false);
    }

}
