package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

import java.util.Optional;


public class InfectAction extends DefaultVampireAction {

    @Override
    public int getCooldown() {
        return 10;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    protected boolean activate(IVampirePlayer vampire) {
        PlayerEntity player = vampire.getRepresentingPlayer();
        RayTraceResult hit = UtilLib.getPlayerLookingSpot(player, player.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue() + 1);
        if (hit.getType() == RayTraceResult.Type.ENTITY && hit instanceof EntityRayTraceResult && ((EntityRayTraceResult) hit).getEntity() instanceof LivingEntity) {
            Entity target = ((EntityRayTraceResult) hit).getEntity();
            if (!UtilLib.canReallySee((LivingEntity) target, player, false)) {
                return false;
            }
            Optional<? extends IBiteableEntity> b = Optional.empty();
            if (target instanceof IBiteableEntity) {
                b = Optional.of((IBiteableEntity) target);
            } else if (target instanceof CreatureEntity) {
                b = ExtendedCreature.getSafe(target).resolve();
            } else if (target instanceof PlayerEntity) {
                b = VampirePlayer.getOpt((PlayerEntity) target).resolve();
            }
            if (b.map(e -> e.tryInfect(vampire)).orElse(false)) {
                vampire.getRepresentingPlayer().awardStat(ModStats.infected_creatures);
                return true;
            }
        }
        return false;
    }

}
