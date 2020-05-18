package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.gen.feature.structure.Structures;
import net.minecraft.world.server.ServerWorld;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Targets vampires if the golem as a non vampire village assigned
 */

public class GolemTargetVampireGoal extends NearestAttackableTargetGoal<LivingEntity> {
    private final IronGolemEntity golem;

    public GolemTargetVampireGoal(IronGolemEntity creature) {
        super(creature, LivingEntity.class, 4, false, false, VampirismAPI.factionRegistry().getPredicate(VReference.HUNTER_FACTION, true, true, false, false, VReference.VAMPIRE_FACTION));
        this.golem = creature;
    }

    @Override
    public boolean shouldExecute() {
        Collection<PointOfInterest> points = ((ServerWorld)this.golem.world).getPointOfInterestManager().func_219146_b(p->true,this.golem.getPosition(),35, PointOfInterestManager.Status.ANY).collect(Collectors.toList());
        if (points.size()>0) {
            BlockPos pos = TotemTileEntity.getTotemPosition(points);
            if (pos != null) {
                TotemTileEntity tile = ((TotemTileEntity) golem.world.getTileEntity(pos));
                if (tile != null && VReference.VAMPIRE_FACTION.equals(tile.getControllingFaction())) {
                    return false;
                }
            }
        }
        return super.shouldExecute();
    }
}