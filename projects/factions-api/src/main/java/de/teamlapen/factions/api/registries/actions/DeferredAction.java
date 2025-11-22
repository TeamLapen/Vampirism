package de.teamlapen.factions.api.registries.actions;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.actions.IAction;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.skills.ISkillPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

@SuppressWarnings("unused")
public class DeferredAction<Z extends IFactionPlayer<Z> & ISkillPlayer<Z>, L extends IAction<Z>, T extends L> extends DeferredHolder<L, T> {

    protected DeferredAction(ResourceKey<L> key) {
        super(key);
    }

    public static <Z extends IFactionPlayer<Z> & ISkillPlayer<Z>, L extends IAction<Z>, T extends L> DeferredAction<Z, L, T> createAction(ResourceKey<L> key) {
        return new DeferredAction<>(key);
    }

    @SuppressWarnings("unchecked")
    public static <Z extends IFactionPlayer<Z> & ISkillPlayer<Z>, L extends IAction<Z>, T extends L> DeferredAction<Z, L, T> createAction(ResourceLocation key) {
        return createAction((ResourceKey<L>) ResourceKey.create(FactionRegistries.Keys.ACTION, key));
    }
}
