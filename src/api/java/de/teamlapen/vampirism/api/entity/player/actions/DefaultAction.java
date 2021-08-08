package de.teamlapen.vampirism.api.entity.player.actions;

import de.teamlapen.vampirism.api.entity.effect.EffectInstanceWithSource;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Default implementation for an action
 */
public abstract class DefaultAction<T extends IFactionPlayer> extends ForgeRegistryEntry<IAction> implements IAction {
    private Component name;

    public void addEffectInstance(T player, MobEffectInstance instance) {
        ((EffectInstanceWithSource) instance).setSource(this.getRegistryName());
        player.getRepresentingPlayer().addEffect(instance);
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
        if (this.getFaction().getFactionPlayerInterface().isInstance(player)) {
            //noinspection unchecked
            return (canBeUsedBy((T) player) ? IAction.PERM.ALLOWED : IAction.PERM.DISALLOWED);
        } else {
            throw new IllegalArgumentException("Faction player instance is of wrong class " + player.getClass() + " instead of " + this.getFaction().getFactionPlayerInterface());
        }

    }

    @Override
    public Component getName() {
        return name == null ? name = new TranslatableComponent(getTranslationKey()) : name;
    }

    @Deprecated
    @Override
    public String getTranslationKey() {
        return "action." + getRegistryName().getNamespace() + "." + getRegistryName().getPath();
    }

    /**
     * @return Should return false if deactivated in configs
     */
    public abstract boolean isEnabled();

    @Override
    public boolean onActivated(IFactionPlayer player) {
        if (this.getFaction().getFactionPlayerInterface().isInstance(player)) {
            //noinspection unchecked
            return activate((T) player);
        } else {
            throw new IllegalArgumentException("Faction player instance is of wrong class " + player.getClass() + " instead of " + this.getFaction().getFactionPlayerInterface());
        }
    }

    public void removePotionEffect(T player, MobEffect effect) {
        MobEffectInstance ins = player.getRepresentingPlayer().getEffect(effect);
        while (ins != null) {
            EffectInstanceWithSource insM = ((EffectInstanceWithSource) ins);
            if (insM.hasSource()) {
                if (insM.getSource().equals(this.getRegistryName())) {
                    insM.removeEffect();
                    break;
                }
            }
            ins = insM.getHiddenEffect();
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
