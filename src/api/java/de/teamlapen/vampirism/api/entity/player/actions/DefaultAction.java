package de.teamlapen.vampirism.api.entity.player.actions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;

/**
 * Default implementation for an action
 */
public abstract class DefaultAction<T extends IFactionPlayer> extends ForgeRegistryEntry<IAction> implements IAction {
    private final IPlayableFaction<T> faction;
    private String translationKey;

    /**
     * @param faction
     */
    public DefaultAction(IPlayableFaction<T> faction) {
        this.faction = faction;
    }

    /**
     * Can be overridden to check additional requirements
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
    public String getTranslationKey() {
        return translationKey == null ? translationKey = "action." + getRegistryName().getNamespace() + "." + getRegistryName().getPath() : translationKey;
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

    /**
     * Called when the action is activated. Only called server side
     *
     * @return Whether the action was successfully activated. !Does not give any feedback to the user!
     */
    protected abstract boolean activate(T player);
}
