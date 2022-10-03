package de.teamlapen.vampirism.entity.ai.goals;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;


public class FleeGarlicVampireGoal extends FleeGoal {

    private final @NotNull VampireBaseEntity theCreature;

    public FleeGarlicVampireGoal(@NotNull VampireBaseEntity theCreature, double movementSpeed, boolean restrictHome) {
        super(theCreature, movementSpeed, restrictHome);
        this.theCreature = theCreature;
    }


    @Override
    protected boolean isPositionAcceptable(Level world, @NotNull BlockPos pos) {
        return theCreature.doesResistGarlic(Helper.getGarlicStrengthAt(world, pos));
    }

    @Override
    protected boolean shouldFlee() {
        return theCreature.isGettingGarlicDamage(theCreature.level) != EnumStrength.NONE;
    }
}
