package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class FleeGarlicVampireGoal extends FleeGoal {

    private final VampireBaseEntity theCreature;

    public FleeGarlicVampireGoal(VampireBaseEntity theCreature, double movementSpeed, boolean restrictHome) {
        super(theCreature, movementSpeed, restrictHome);
        this.theCreature = theCreature;
    }


    @Override
    protected boolean isPositionAcceptable(World world, BlockPos pos) {
        return theCreature.doesResistGarlic(Helper.getGarlicStrengthAt(world, pos));
    }

    @Override
    protected boolean shouldFlee() {
        return theCreature.isGettingGarlicDamage(theCreature.level) != EnumStrength.NONE;
    }
}
