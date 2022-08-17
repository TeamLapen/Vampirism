package de.teamlapen.vampirism.entity;

import de.teamlapen.lib.lib.entity.BasicAreaParticleCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;


public class AreaParticleCloudEntity extends BasicAreaParticleCloud {
    public AreaParticleCloudEntity(@NotNull EntityType<? extends AreaParticleCloudEntity> type, @NotNull Level worldIn) {
        super(type, worldIn);
    }
}
