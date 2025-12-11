package de.teamlapen.vampirism.common.entity.player.hunter.properties;

import de.teamlapen.factions.api.factions.IDisguise;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.entity.player.hunter.HunterPlayer;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;

public class HunterDisguise implements IDisguise {

    private final HunterPlayer player;

    public HunterDisguise(HunterPlayer hunterPlayer) {
        this.player = hunterPlayer;
    }

    @Override
    public Holder<? extends IPlayableFaction<?>> actualFaction() {
        return this.player.getFaction();
    }

    @Override
    public Holder<? extends IPlayableFaction<?>> getViewedFaction(@Nullable Holder<? extends IFaction<?>> viewerFaction) {
        return this.player.asEntity().hasEffect(ModEffects.DISGUISE_AS_VAMPIRE) ? ModFactions.VAMPIRE : actualFaction();
    }

    @Override
    public void disguiseAs(@Nullable Holder<? extends IFaction<?>> faction) {

    }

    @Override
    public void unDisguise() {

    }

    @Override
    public boolean isDisguised() {
        return this.player.asEntity().hasEffect(ModEffects.DISGUISE_AS_VAMPIRE);
    }
}
