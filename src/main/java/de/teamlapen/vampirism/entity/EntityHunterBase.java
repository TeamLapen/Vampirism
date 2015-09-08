package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.Configs;
import de.teamlapen.vampirism.util.Helper18;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.world.World;

/**
 * Base class for all hunters
 */
public class EntityHunterBase extends EntityVampirism {
    public EntityHunterBase(World world) {
        super(world);
        Helper18.setAvoidsWater(this,true);
        this.tasks.addTask(0, new EntityAISwimming(this));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (Configs.disable_hunter) {
            this.setDead();
        }
    }
}
