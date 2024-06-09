package de.teamlapen.vampirism.api.registries;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DeferredAction<Z extends IFactionPlayer<Z>, L extends IAction<Z>, T extends L> extends DeferredHolder<L, T> {

    protected DeferredAction(ResourceKey<L> key) {
        super(key);
    }

    public static <Z extends IFactionPlayer<Z>, L extends IAction<Z>, T extends L> DeferredAction<Z, L, T> createAction(ResourceKey<L> key) {
        return new DeferredAction<>(key);
    }

    public static <Z extends IFactionPlayer<Z>, L extends IAction<Z>, T extends L> DeferredAction<Z, L, T> createAction(ResourceLocation key) {
        return createAction((ResourceKey<L>) ResourceKey.create(VampirismRegistries.Keys.ACTION, key));
    }
}
