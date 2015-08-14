package de.teamlapen.vampirism.entity;

import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.world.World;

/**
 * Created by Max on 09.08.2015.
 */
public class EntityHunterBase extends EntityVampirism {
    public EntityHunterBase(World world) {
        super(world);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
    }
}
