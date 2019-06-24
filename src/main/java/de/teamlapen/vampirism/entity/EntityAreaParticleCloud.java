package de.teamlapen.vampirism.entity;

import de.teamlapen.lib.lib.entity.BasicEntityAreaParticleCloud;
import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.world.World;


public class EntityAreaParticleCloud extends BasicEntityAreaParticleCloud {
    public EntityAreaParticleCloud(World worldIn) {
        super(ModEntities.particle_cloud, worldIn);
    }
}
