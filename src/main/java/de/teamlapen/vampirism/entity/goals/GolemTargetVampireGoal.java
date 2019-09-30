package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.structure.Structures;

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
        if (Structures.VILLAGE.isPositionInStructure(golem.world, golem.getPosition())) {
            BlockPos pos = TotemTileEntity.getTotemPosition(Structures.VILLAGE.getStart(golem.world, golem.getPosition(), false));
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