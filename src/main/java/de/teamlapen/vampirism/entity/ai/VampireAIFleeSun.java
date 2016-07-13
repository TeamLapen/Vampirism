package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class VampireAIFleeSun extends EntityAIFlee {
    private final IVampire vampire;


    /**
     * @param vampire Has to implement  {@link IVampire}
     */
    public VampireAIFleeSun(EntityCreature vampire, double movementSpeed, boolean restrictToHome) {
        super(vampire, movementSpeed, restrictToHome);
        this.vampire = (IVampire) vampire;
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
