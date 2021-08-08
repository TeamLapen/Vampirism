package de.teamlapen.vampirism.entity;

import de.teamlapen.lib.lib.entity.BasicAreaParticleCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;


public class AreaParticleCloudEntity extends BasicAreaParticleCloud {
    public AreaParticleCloudEntity(EntityType<? extends AreaParticleCloudEntity> type, Level worldIn) {
        super(type, worldIn);
    }
}
