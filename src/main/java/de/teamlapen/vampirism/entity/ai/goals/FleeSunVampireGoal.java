package de.teamlapen.vampirism.entity.ai.goals;

import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;


public class FleeSunVampireGoal<T extends PathfinderMob & IVampire> extends FleeGoal {
    private final @NotNull T vampire;


    public FleeSunVampireGoal(@NotNull T vampire, double movementSpeed, boolean restrictToHome) {
        super(vampire, movementSpeed, restrictToHome);
        this.vampire = vampire;
    }


    @Override
    protected boolean isPositionAcceptable(@NotNull Level world, @NotNull BlockPos pos) {
        return !world.canSeeSkyFromBelowWater(pos);
    }

    @Override
    protected boolean shouldFlee() {
        return vampire.isGettingSundamage(vampire.level()) && !vampire.isIgnoringSundamage();
    }
}
