package de.teamlapen.vampirism.api.extensions;

import de.teamlapen.vampirism.api.extensions.ILivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public interface IPlayer extends ILivingEntity {

    @Override
    @NotNull
    Player asEntity();
}
