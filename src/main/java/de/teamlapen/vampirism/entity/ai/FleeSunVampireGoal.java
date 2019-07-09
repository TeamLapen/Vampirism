package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class FleeSunVampireGoal<T extends CreatureEntity & IVampire> extends FleeGoal {
    private final T vampire;


    public FleeSunVampireGoal(T vampire, double movementSpeed, boolean restrictToHome) {
        super(vampire, movementSpeed, restrictToHome);
        this.vampire = vampire;
    }


    @Override
    protected boolean isPositionAcceptable(World world, BlockPos pos) {
        return !world.canBlockSeeSky(pos);
    }

    @Override
    protected boolean shouldFlee() {
        return vampire.isGettingSundamage() && !vampire.isIgnoringSundamage();
    }
}
