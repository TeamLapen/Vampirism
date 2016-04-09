package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.vampire.EntityVampireBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class VampireAIFleeSun extends EntityAIFlee {
    private final EntityVampireBase theCreature;


    public VampireAIFleeSun(EntityVampireBase theCreature, double movementSpeed, boolean restrictToHome) {
        super(theCreature, movementSpeed, restrictToHome);
        this.theCreature = theCreature;
    }


    @Override
    protected boolean isPositionAcceptable(World world, BlockPos pos) {
        return !world.canBlockSeeSky(pos);
    }

    @Override
    protected boolean shouldFlee() {
        return theCreature.isGettingSundamage();
    }
}
