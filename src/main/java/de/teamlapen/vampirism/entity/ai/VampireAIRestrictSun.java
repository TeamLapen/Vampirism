package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.ResourceLocation;


public class VampireAIRestrictSun extends EntityAIBase {
    private final IVampire vampire;
    private final EntityCreature creature;
    private boolean cache = false;

    /**
     * @param creature Has to implement {@link IVampire}
     */
    public VampireAIRestrictSun(EntityCreature creature) {
        this.vampire = (IVampire) creature;
        this.creature = creature;
    }

    public void resetTask() {
        ((PathNavigateGround) this.creature.getNavigator()).setAvoidSun(false);
    }

    @Override
    public boolean shouldExecute() {
        if (creature.ticksExisted % 10 == 3) {
            ResourceLocation biome = creature.getEntityWorld().getBiome(creature.getPosition()).getRegistryName();
            cache = VampirismAPI.sundamageRegistry().getSundamageInDim(creature.getEntityWorld().provider.getDimension()) && VampirismAPI.sundamageRegistry().getSundamageInBiome(biome);
        }
        return cache && creature.getEntityWorld().isDaytime() && !vampire.isIgnoringSundamage();
    }

    public void startExecuting() {
        ((PathNavigateGround) this.creature.getNavigator()).setAvoidSun(true);
    }
}
