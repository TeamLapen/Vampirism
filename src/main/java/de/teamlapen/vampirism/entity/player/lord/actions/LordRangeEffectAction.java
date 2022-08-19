package de.teamlapen.vampirism.entity.player.lord.actions;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.DefaultAction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.IVampirismPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class LordRangeEffectAction<T extends IFactionPlayer<T>> extends DefaultAction<T> {

    private final Supplier<MobEffect> effect;

    public LordRangeEffectAction(Supplier<MobEffect> effect) {
        this.effect = effect;
    }

    @Override
    protected boolean activate(@NotNull T player, ActivationContext context) {
        int lordLevel = FactionPlayerHandler.getOpt(player.getRepresentingPlayer()).map(FactionPlayerHandler::getLordLevel).orElse(0);
        List<LivingEntity> entitiesOfClass = player.getRepresentingPlayer().level.getEntitiesOfClass(LivingEntity.class, new AABB(player.getRepresentingPlayer().blockPosition()).inflate(10, 10, 10), e -> player.getFaction() == VampirismAPI.factionRegistry().getFaction(e));
        for (LivingEntity entity : entitiesOfClass) {
            if (entity instanceof Player && FactionPlayerHandler.getOpt(((Player) entity)).map(FactionPlayerHandler::getLordLevel).filter(l -> l >= lordLevel).isPresent()) {
                continue;
            }
            entity.addEffect(new MobEffectInstance(effect.get(), getEffectDuration(player), getEffectAmplifier(player)));
        }
        return !entitiesOfClass.isEmpty();
    }

    protected abstract int getEffectDuration(T player);

    @Override
    public int getCooldown(T player) {
        return getEffectDuration(player);
    }

    protected int getEffectAmplifier(@NotNull T player) {
        return ((IVampirismPlayer) player.getRepresentingPlayer()).getVampAtts().lordLevel - 1;
    }

    @NotNull
    @Override
    public Optional<IPlayableFaction<?>> getFaction() {
        return Optional.empty();
    }
}
