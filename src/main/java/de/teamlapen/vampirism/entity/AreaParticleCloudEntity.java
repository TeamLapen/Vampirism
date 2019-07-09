package de.teamlapen.vampirism.entity;

import de.teamlapen.lib.lib.entity.BasicEntityAreaParticleCloud;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;


public class AreaParticleCloudEntity extends BasicEntityAreaParticleCloud {
    public AreaParticleCloudEntity(EntityType<? extends AreaParticleCloudEntity> type, World worldIn) {
        super(type, worldIn);
    }
}
