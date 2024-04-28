package de.teamlapen.vampirism.api.entity.player.task;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TaskRequirement {

    public static final Codec<TaskRequirement> CODEC = RecordCodecBuilder.create(inst -> {
        return inst.group(
                Requirement.CODEC.listOf().fieldOf("requirements").forGetter(i -> i.requirements.values().stream().flatMap(Collection::stream).sorted(Comparator.comparing(Requirement::id)).toList())
        ).apply(inst, TaskRequirement::new);
    });

    private final Map<Type, List<Requirement<?>>> requirements;
    private final int size;
    private final boolean hasStatBasedReq;

    public TaskRequirement(@NotNull Map<Type, List<Requirement<?>>> requirements) {
        this.requirements = requirements;
        this.size = requirements.values().stream().mapToInt(List::size).sum();
        this.hasStatBasedReq = requirements.keySet().stream().anyMatch(Type::isStatBased);
    }

    public TaskRequirement(@NotNull Collection<Requirement<?>> requirements) {
        this.requirements = requirements.stream().collect(Collectors.toMap(Requirement::getType, Lists::newArrayList, (a, b) -> {
            a.addAll(b);
            return a;
        }));
        this.size = this.requirements.values().stream().mapToInt(List::size).sum();
        this.hasStatBasedReq = this.requirements.keySet().stream().anyMatch(Type::isStatBased);
    }

    public @NotNull List<Requirement<?>> getAll() {
        return this.requirements.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public boolean isHasStatBasedReq() {
        return hasStatBasedReq;
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

    public Map<Type, List<Requirement<?>>> requirements() {
        return requirements;
    }

    public int size() {
        return size;
    }

    @SuppressWarnings("JavadocReference")
    public enum Type implements StringRepresentable {
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

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        private final boolean statBased;
        private final String translationKey;

        Type(boolean statBased, String translationKey) {
            this.statBased = statBased;
            this.translationKey = translationKey;
        }

        public String getTranslationKey() {
            return translationKey;
        }

        public boolean isStatBased() {
            return statBased;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public interface Requirement<T> {

        Codec<Requirement<?>> CODEC = Codec.lazyInitialized(() -> VampirismRegistries.TASK_REQUIREMENT.get().byNameCodec()).dispatch(Requirement::codec, Function.identity());

        /**
         * @return the needed amount of the {@link #getStat(IFactionPlayer)} to complete this requirement
         */
        default int getAmount(IFactionPlayer<?> player) {
            return 1;
        }

        @NotNull
        ResourceLocation id();

        /**
         * @param player the player who wants to complete this task
         * @return the stat the needs to be achieved with {@link #getAmount(IFactionPlayer)} to complete the requirement
         * @throws ClassCastException if Object is not applicant for the {@link #getType()}
         */
        @NotNull
        T getStat(IFactionPlayer<?> player);

        @NotNull
        default Type getType() {
            return Type.BOOLEAN;
        }

        /**
         * if needed removes the requirements from the player upon task completion
         *
         * @param player the player which completed the task
         */
        default void removeRequirement(IFactionPlayer<?> player) {
        }

        MapCodec<? extends Requirement<?>> codec();

        Component description();

    }

}
