package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;


public class FleeSunVampireGoal<T extends PathfinderMob & IVampire> extends FleeGoal {
    private final T vampire;


    public FleeSunVampireGoal(T vampire, double movementSpeed, boolean restrictToHome) {
        super(vampire, movementSpeed, restrictToHome);
        this.vampire = vampire;
    }


    @Override
    protected boolean isPositionAcceptable(Level world, BlockPos pos) {
        return !world.canSeeSkyFromBelowWater(pos);
    }

    @Override
    protected boolean shouldFlee() {
        return vampire.isGettingSundamage(vampire.level) && !vampire.isIgnoringSundamage();
    }
}
