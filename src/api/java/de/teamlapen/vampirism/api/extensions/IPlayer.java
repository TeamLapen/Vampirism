package de.teamlapen.vampirism.api.extensions;

import de.teamlapen.vampirism.api.extensions.ILivingEntity;
import net.minecraft.world.entity.player.Player;

public interface IPlayer extends ILivingEntity {

    @Override
    Player asEntity();
}
