package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.EntityVampireBase;
import net.minecraft.world.World;

/**
 * Vampire AI to flee garlic
 */
public class VampireAIFleeGarlic extends EntityAIFlee {

    protected final EntityVampireBase vampire;


    public VampireAIFleeGarlic(EntityVampireBase vampire, double speed) {
        this(vampire, speed, false);
    }

    /**
     * @param vampire
     * @param speed
     * @param restrictToHome If the entitys home should be respected, if there is one.
     */
    public VampireAIFleeGarlic(EntityVampireBase vampire, double speed, boolean restrictToHome) {
        super(vampire, speed, restrictToHome);
        this.vampire = vampire;

    }


    @Override
    protected boolean isPositionAcceptable(World world, int x, int y, int z) {
        return vampire.isGarlicOkAt(x, y, z);
    }

    @Override
    protected boolean shouldFlee() {
        return vampire.isInGarlic() > vampire.getResitsGarlic();
    }
}
