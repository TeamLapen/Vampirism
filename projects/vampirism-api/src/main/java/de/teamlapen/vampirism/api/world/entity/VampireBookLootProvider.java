package de.teamlapen.vampirism.api.world.entity;

import de.teamlapen.vampirism.api.world.items.components.IVampireBook;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface VampireBookLootProvider {

    @NotNull
    Optional<Holder<IVampireBook>> getBookLootId();
}
