package de.teamlapen.vampirism.player.actions;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.DefaultAction;
import de.teamlapen.vampirism.player.IVampirismPlayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

public abstract class LordRangeEffectAction<T extends IFactionPlayer> extends DefaultAction<T> {

    private final Supplier<Effect> effect;
    private final IPlayableFaction faction;

    public LordRangeEffectAction(Supplier<Effect> effect, IPlayableFaction faction) {
        this.effect = effect;
        this.faction = faction;
    }

    @Override
    protected boolean activate(T player, ActivationContext context) {
        List<LivingEntity> entitiesOfClass = player.getRepresentingPlayer().level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(player.getRepresentingPlayer().blockPosition()).inflate(10, 10, 10), e -> player.getFaction() == VampirismAPI.factionRegistry().getFaction(e));
        for (LivingEntity entity : entitiesOfClass) {
            entity.addEffect(new EffectInstance(effect.get(), getEffectDuration(player), getEffectAmplifier(player)));
        }
        return !entitiesOfClass.isEmpty();
    }

    protected abstract int getEffectDuration(IFactionPlayer player);

    @Override
    public int getCooldown(IFactionPlayer player) {
        return getEffectDuration(player);
    }

    protected int getEffectAmplifier(IFactionPlayer player){
        return ((IVampirismPlayer) player.getRepresentingPlayer()).getVampAtts().lordLevel - 1;
    }

    @Nonnull
    @Override
    public IPlayableFaction getFaction() {
        return this.faction;
    }
}
