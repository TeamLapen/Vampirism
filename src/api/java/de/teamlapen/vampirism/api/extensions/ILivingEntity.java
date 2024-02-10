package de.teamlapen.vampirism.api.extensions;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface ILivingEntity extends IEntity {
    @Override
    @NotNull
    LivingEntity asEntity();
}
