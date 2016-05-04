package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class VampireAIFleeSun extends EntityAIFlee {
    private final IVampire theCreature;


    /**
     * @param theCreature Has to implement  {@link IVampire}
     */
    public VampireAIFleeSun(EntityCreature theCreature, double movementSpeed, boolean restrictToHome) {
        super(theCreature, movementSpeed, restrictToHome);
        this.theCreature = (IVampire) theCreature;
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
