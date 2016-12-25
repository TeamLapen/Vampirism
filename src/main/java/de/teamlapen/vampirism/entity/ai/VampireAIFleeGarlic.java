package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBase;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class VampireAIFleeGarlic extends EntityAIFlee {

    private final EntityVampireBase theCreature;

    public VampireAIFleeGarlic(EntityVampireBase theCreature, double movementSpeed, boolean restrictHome) {
        super(theCreature, movementSpeed, restrictHome);
        this.theCreature = theCreature;
    }


    @Override
    protected boolean isPositionAcceptable(World world, BlockPos pos) {
        return theCreature.doesResistGarlic(Helper.getGarlicStrengthAt(world, pos));
    }

    @Override
    protected boolean shouldFlee() {
        return theCreature.isGettingGarlicDamage() != EnumStrength.NONE;
    }
}
