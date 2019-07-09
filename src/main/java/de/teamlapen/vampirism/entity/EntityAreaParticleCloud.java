package de.teamlapen.vampirism.entity;

import de.teamlapen.lib.lib.entity.BasicEntityAreaParticleCloud;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;


public class EntityAreaParticleCloud extends BasicEntityAreaParticleCloud {
    public EntityAreaParticleCloud(EntityType<? extends EntityAreaParticleCloud> type, World worldIn) {
        super(type, worldIn);
    }
}
