package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class VampireAIFleeSun<T extends EntityCreature & IVampire> extends EntityAIFlee {
    private final T vampire;


    public VampireAIFleeSun(T vampire, double movementSpeed, boolean restrictToHome) {
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
