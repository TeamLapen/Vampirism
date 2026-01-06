package de.teamlapen.faction.api.world.entities.extensions;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public interface IPlayer extends ILivingEntity {

    @Override
    @NotNull
    Player asEntity();
}
