package de.teamlapen.vampirism.api.entity.player.task;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskRequirement {

    private final Map<Type, List<Requirement<?>>> requirements;
    private final int size;
    private final boolean hasStatBasedReq;

    public TaskRequirement(Map<Type, List<Requirement<?>>> requirements) {
        this.requirements = requirements;
        this.size = requirements.values().stream().mapToInt(List::size).sum();
        this.hasStatBasedReq = requirements.keySet().stream().anyMatch(Type::isStatBased);
    }

    public Map<Type, List<Requirement<?>>> requirements() {
        return requirements;
    }

    public int size() {
        return size;
    }

    /**
     * if needed removes the requirements from the player upon task completion
     *
     * @param player the player which completed the task
     */
    public void removeRequirement(IFactionPlayer<?> player) {
        for (Type type : this.requirements.keySet()) {
            for (Requirement<?> requirement : this.requirements.get(type)) {
                requirement.removeRequirement(player);
            }
        }
    }

    public List<Requirement<?>> getAll() {
        return this.requirements.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public boolean isHasStatBasedReq() {
        return hasStatBasedReq;
    }

    public interface Requirement<T> {
        @Nonnull
        default Type getType() {
            return Type.BOOLEAN;
        }

        /**
         * @param player the player who wants to complete this task
         * @return the stat the needs to be achieved with {@link #getAmount(IFactionPlayer)} to complete the requirement
         * @throws ClassCastException if Object is not applicant for the {@link #getType()}
         */
        @Nonnull
        T getStat(IFactionPlayer<?> player);

        /**
         * @return the needed amount of the {@link #getStat(IFactionPlayer)} to complete this requirement
         */
        default int getAmount(IFactionPlayer<?> player) {
            return 1;
        }

        /**
         * if needed removes the requirements from the player upon task completion
         *
         * @param player the player which completed the task
         */
        default void removeRequirement(IFactionPlayer<?> player) {
        }

        @Nonnull
        ResourceLocation getId();

    }

    @SuppressWarnings("JavadocReference")
    public enum Type {
        /**
         * based on {@link net.minecraft.stats.Stats.CUSTOM} stat increase
         */
        STATS(true, "gui.vampirism.taskmaster.stat_req"),
        /**
         * based on item in inventory
         */
        ITEMS(false, "gui.vampirism.taskmaster.item_req"),
        /**
         * based on {@link net.minecraft.stats.Stats.ENTITY_KILLED} stat increased
         */
        ENTITY(true, "gui.vampirism.taskmaster.entity_req"),
        /**
         * based on {@link net.minecraft.stats.Stats.ENTITY_KILLED} stat increased, but for multiple entities.
         */
        ENTITY_TAG(true, "gui.vampirism.taskmaster.entity_tag_req"),
        /**
         * based on boolean supplier
         */
        BOOLEAN(false, "gui.vampirism.taskmaster.bool_req");

        private final boolean statBased;
        private final String translationKey;

        Type(boolean statBased, String translationKey) {
            this.statBased = statBased;
            this.translationKey = translationKey;
        }

        public boolean isStatBased() {
            return statBased;
        }

        public String getTranslationKey() {
            return translationKey;
        }
    }

}
