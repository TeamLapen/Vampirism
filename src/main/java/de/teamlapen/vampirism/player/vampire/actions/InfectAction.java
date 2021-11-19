package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

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
    protected boolean activate(IVampirePlayer vampire) {
        Player player = vampire.getRepresentingPlayer();
        HitResult hit = UtilLib.getPlayerLookingSpot(player, player.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue() + 1);
        if (hit.getType() == EntityHitResult.Type.ENTITY && hit instanceof EntityHitResult && ((EntityHitResult) hit).getEntity() instanceof LivingEntity) {
            Entity target = ((EntityHitResult) hit).getEntity();
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
        }
        return false;
    }

}
