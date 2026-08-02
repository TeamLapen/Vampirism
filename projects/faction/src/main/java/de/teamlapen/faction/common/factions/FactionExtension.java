package de.teamlapen.faction.common.factions;

import de.teamlapen.faction.api.FactionsApi;
import de.teamlapen.faction.api.factions.*;
import de.teamlapen.faction.api.world.entities.extensions.IPlayer;
import de.teamlapen.sync.AttachmentSync;
import de.teamlapen.sync.PropertySync;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class FactionExtension extends AttachmentSync implements IFactionExtension, IPlayer, IFactionEntity, IFactionExtensionGetter {

    protected final Player player;
    @Nullable
    private IFactionPlayerHandler factionPlayerHandler;

    public FactionExtension(Player player) {
        this.player = player;
    }

    protected IFactionPlayerHandler handler() {
        if (this.factionPlayerHandler == null) {
            this.factionPlayerHandler = FactionsApi.factionPlayerHandler(this.player);
        }
        return this.factionPlayerHandler;
    }

    @Override
    public Player asEntity() {
        return this.player;
    }

    @Override
    public Holder<? extends IPlayableFaction<?>> getFaction() {
        return handler().getFaction();
    }

    @Override
    public <TInterface> Optional<TInterface> getExtension(Class<TInterface> type) {
        return this.handler().getExtension(type);
    }
}
