package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIRestrictSun;


public class VampireAIRestrictSun extends EntityAIRestrictSun {
    private final IVampire vampire;

    /**
     * @param creature Has to implement {@link IVampire}
     */
    public VampireAIRestrictSun(EntityCreature creature) {
        super(creature);
        this.vampire = (IVampire) creature;
    }

    @Override
    public boolean shouldExecute() {
        return super.shouldExecute() && !vampire.isIgnoringSundamage();
    }
}
