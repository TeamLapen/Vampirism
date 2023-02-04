package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.minion.IMinionData;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface IMinionBuilder<T extends IFactionPlayer<T>> {

    IMinionBuilder<T> minionData(@NotNull Supplier<IMinionData> data);

    ILordPlayerBuilder<T> build();
}
