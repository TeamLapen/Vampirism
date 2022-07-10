package de.teamlapen.vampirism.api.entity.player.actions;

import de.teamlapen.vampirism.api.entity.effect.EffectInstanceWithSource;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Optional;

/**
 * Default implementation for an action
 */
public abstract class DefaultAction<T extends IFactionPlayer> extends ForgeRegistryEntry<IAction> implements IAction {
    private ITextComponent name;

    public void addEffectInstance(T player, EffectInstance instance) {
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
    public final IAction.PERM canUse(IFactionPlayer player) {
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
    public ITextComponent getName() {
        return name == null ? name = new TranslationTextComponent(getTranslationKey()) : name;
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

    @Deprecated
    @Override
    public boolean onActivated(IFactionPlayer player) {
        return onActivated(player, new ActivationContext() {
            @Override
            public Optional<BlockPos> targetBlock() {
                return Optional.empty();
            }

            @Override
            public Optional<Entity> targetEntity() {
                return Optional.empty();
            }
        });
    }

    @Override
    public boolean onActivated(IFactionPlayer player, ActivationContext context) {
        if (this.getFaction().getFactionPlayerInterface().isInstance(player)) {
            //noinspection unchecked
            return activate((T) player, context);
        } else {
            throw new IllegalArgumentException("Faction player instance is of wrong class " + player.getClass() + " instead of " + this.getFaction().getFactionPlayerInterface());
        }
    }

    public void removePotionEffect(T player, Effect effect) {
        EffectInstance ins = player.getRepresentingPlayer().getEffect(effect);
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
     * Overwrite context-sensitive version instead
     * TODO 1.19 remove
     */
    @Deprecated
    protected boolean activate(T player){
        return false;
    }

    /**
     * Called when the action is activated. Only called server side
     *
     * @return Whether the action was successfully activated. !Does not give any feedback to the user!
     */
    protected boolean activate(T player, ActivationContext context){
        return this.activate(player); //TODO 1.19 make abstract
    }
}
