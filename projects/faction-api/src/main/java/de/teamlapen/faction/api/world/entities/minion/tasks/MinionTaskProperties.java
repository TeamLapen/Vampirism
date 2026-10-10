package de.teamlapen.faction.api.world.entities.minion.tasks;

import com.mojang.serialization.Codec;
import de.teamlapen.faction.api.FactionDataComponents;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.world.entities.minion.IMinionData;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.DependantName;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public class MinionTaskProperties {

    private static final DependantName<IMinionTask<?>, String> NAME_ID = (id) -> Util.makeDescriptionId("task", id.identifier());
    private DataComponentInitializers.Initializer<IMinionTask<?>> componentInitializer = (builder, context, id) -> {};
    private @Nullable ResourceKey<IMinionTask<?>> id;

    //<editor-fold desc="Components">

    public MinionTaskProperties factions(TagKey<IFaction<?>> key) {
        return component(FactionDataComponents.MINION_TASK_FACTIONS, key);
    }

    public MinionTaskProperties requires(Holder<? extends ISkill<?>> skill) {
        return component(FactionDataComponents.MINION_TASK_SKILL_REQUIREMENT, skill);
    }

    public MinionTaskProperties markGlobal() {
        return component(FactionDataComponents.MINION_TASK_GLOBAL_COMMAND, Unit.INSTANCE);
    }

    //</editor-fold>

    //<editor-fold desc="Value Getter">

    public MinionTaskProperties setId(ResourceKey<IMinionTask<?>> id) {
        this.id = id;
        return this;
    }

    public ResourceKey<IMinionTask<?>> taskIdOrThrow() {
        return Objects.requireNonNull(this.id, "MinionTask id not set");
    }

    public String effectiveNameId() {
        return NAME_ID.get(this.taskIdOrThrow());
    }

    //</editor-fold>

    //<editor-fold desc="Generic Components">

    public <Z> MinionTaskProperties component(Supplier<DataComponentType<Z>> type, Z value) {
        return component(type.get(), value);
    }

    public <Z> MinionTaskProperties component(DataComponentType<Z> type, Z value) {
        this.componentInitializer = this.componentInitializer.add(type, value);
        return this;
    }

    //</editor-fold>

    //<editor-fold desc="Finalizer">

    public DataComponentInitializers.Initializer<IMinionTask<?>> finalizeInitializer(Component name) {
        return this.componentInitializer
                .andThen((builder, _, key) -> {
                    builder.set(FactionDataComponents.MINION_TASK_NAME, name);
                });
    }

    //</editor-fold>
}
