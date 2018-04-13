package de.teamlapen.vampirism.advancements;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;


/**
 * Implements some general function used in most criterion triggers.
 * The concept is more or less copied from vanilla.
 * <p>
 * It is quite complex/strange but I guess MC has it's reasons
 *
 * @param <T>
 */
public abstract class AbstractCriterionTrigger<T extends ICriterionInstance> implements ICriterionTrigger<T> {

    protected final Map<PlayerAdvancements, GenericListeners<T>> listenersForPlayers = Maps.newHashMap();
    private final ResourceLocation id;
    private final Function<PlayerAdvancements, GenericListeners<T>> listenerConstructor;


    public AbstractCriterionTrigger(ResourceLocation id, Function<PlayerAdvancements, GenericListeners<T>> listenerConstructor) {
        this.id = id;
        this.listenerConstructor = listenerConstructor;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<T> listener) {
        GenericListeners listeners = this.listenersForPlayers.get(playerAdvancementsIn);
        if (listeners == null) {
            listeners = listenerConstructor.apply(playerAdvancementsIn);
            this.listenersForPlayers.put(playerAdvancementsIn, listeners);
        }
        listeners.add(listener);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
        this.listenersForPlayers.remove(playerAdvancementsIn);
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<T> listener) {
        GenericListeners listeners = this.listenersForPlayers.get(playerAdvancementsIn);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                this.listenersForPlayers.remove(playerAdvancementsIn);
            }
        }
    }

    protected abstract static class GenericListeners<T extends ICriterionInstance> {
        protected final PlayerAdvancements playerAdvancements;
        protected final Set<Listener<T>> playerListeners = Sets.newHashSet();

        public GenericListeners(PlayerAdvancements playerAdvancementsIn) {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public void add(ICriterionTrigger.Listener<T> listener) {
            this.playerListeners.add(listener);
        }

        public boolean isEmpty() {
            return this.playerListeners.isEmpty();
        }

        public void remove(ICriterionTrigger.Listener<T> listener) {
            this.playerListeners.remove(listener);
        }
    }
}
