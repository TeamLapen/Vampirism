package de.teamlapen.vampirism.api.extensions;

import net.minecraft.world.entity.LivingEntity;

public interface ILivingEntity extends IEntity {
    @Override
    LivingEntity asEntity();
}
