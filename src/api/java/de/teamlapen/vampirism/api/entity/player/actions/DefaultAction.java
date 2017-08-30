package de.teamlapen.vampirism.api.entity.player.actions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

/**
 * Default implementation for an action
 */
public abstract class DefaultAction<T extends IFactionPlayer> extends IForgeRegistryEntry.Impl<IAction> implements IAction {
    private final ResourceLocation icons;
    private final IPlayableFaction<T> faction;

    /**
     * @param faction
     * @param icons If null Vampirism's default one will be used
     */
    public DefaultAction(IPlayableFaction<T> faction, ResourceLocation icons) {
        this.icons = icons;
        this.faction = faction;
    }

    /**
     * Can be overriden to check addidional requirements
     *
     * @return
     */
    public boolean canBeUsedBy(T player) {
        return true;
    }

    @Override
    public IAction.PERM canUse(IFactionPlayer player) {
        if (!isEnabled())
            return IAction.PERM.DISABLED;
        if (faction.getFactionPlayerInterface().isInstance(player)) {
            //noinspection unchecked
            return (canBeUsedBy((T) player) ? IAction.PERM.ALLOWED : IAction.PERM.DISALLOWED);
        } else {
            throw new IllegalArgumentException("Faction player instance is of wrong class " + player.getClass() + " instead of " + faction.getFactionPlayerInterface());
        }

    }

    @Nonnull
    @Override
    public IPlayableFaction getFaction() {
        return faction;
    }

    @Override
    public ResourceLocation getIconLoc() {
        return icons;
    }

    /**
     * @return Should return false if deactivated in configs
     */
    public abstract boolean isEnabled();

    @Override
    public boolean onActivated(IFactionPlayer player) {
        if (faction.getFactionPlayerInterface().isInstance(player)) {
            //noinspection unchecked
            return activate((T) player);
        } else {
            throw new IllegalArgumentException("Faction player instance is of wrong class " + player.getClass() + " instead of " + faction.getFactionPlayerInterface());
        }
    }

    @Override
    public String toString() {
        return this.getRegistryName() + " (" + this.getClass().getSimpleName() + ")";
    }

    protected abstract boolean activate(T player);
}
