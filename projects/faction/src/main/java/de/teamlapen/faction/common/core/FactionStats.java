package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.util.CustomStatType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public class FactionStats {

    private static final DeferredRegister<StatType<?>> STAT_TYPES = DeferredRegister.create(Registries.STAT_TYPE, REFERENCE.MOD_ID);
    private static final DeferredRegister<Identifier> CUSTOM_STAT = DeferredRegister.create(Registries.CUSTOM_STAT, REFERENCE.MOD_ID);

    private static final Map<Identifier, StatFormatter> CUSTOM_STAT_FORMATTERS = new HashMap<>();

    public static final DeferredHolder<StatType<?>, StatType<ISkill<?>>> SKILL_UNLOCKED = STAT_TYPES.register("skill_unlocked", () -> new StatType<>(ModRegistries.SKILLS, Component.translatable("stat_type." + REFERENCE.MOD_ID + ".skill_unlocked")));
    public static final DeferredHolder<StatType<?>, StatType<ISkill<?>>> SKILL_FORGOTTEN = STAT_TYPES.register("skill_forgotten", () -> new StatType<>(ModRegistries.SKILLS, Component.translatable("stat_type." + REFERENCE.MOD_ID + ".skill_forgotten")));
    public static final DeferredHolder<StatType<?>, StatType<IAction<?>>> ACTION_USED = STAT_TYPES.register("action_used", () -> new StatType<>(ModRegistries.ACTIONS, Component.translatable("stat_type." + REFERENCE.MOD_ID + ".action_used")));
    public static final DeferredHolder<StatType<?>, CustomStatType<IAction<?>>> ACTION_TIME = STAT_TYPES.register("action_time", () -> new CustomStatType<>(ModRegistries.ACTIONS, Component.translatable("stat_type." + REFERENCE.MOD_ID + ".action_time"), StatFormatter.TIME));
    public static final DeferredHolder<StatType<?>, CustomStatType<IAction<?>>> ACTION_COOLDOWN_TIME = STAT_TYPES.register("action_cooldown", () -> new CustomStatType<>(ModRegistries.ACTIONS, Component.translatable("stat_type." + REFERENCE.MOD_ID + ".action_cooldown_time"), StatFormatter.TIME));

    public static final DeferredHolder<Identifier, Identifier> TASKS_ACCEPTED = add("tasks_accepted");
    public static final DeferredHolder<Identifier, Identifier> TASKS_COMPLETED = add("tasks_completed");
    public static final DeferredHolder<Identifier, Identifier> INTERACT_WITH_TOTEM = add("interact_with_totem");
    public static final DeferredHolder<Identifier, Identifier> CAPTURE_VILLAGE = add("capture_village");
    public static final DeferredHolder<Identifier, Identifier> DEFEND_VILLAGE = add("defend_village");
    public static final DeferredHolder<Identifier, Identifier> WIN_VILLAGE_CAPTURE = add("win_village_capture");


    public static void register(IEventBus eventBus) {
        CUSTOM_STAT.register(eventBus);
        STAT_TYPES.register(eventBus);
    }

    private static DeferredHolder<Identifier, Identifier> add(String name) {
        return add(name, StatFormatter.DEFAULT);
    }

    @SuppressWarnings("SameParameterValue")
    private static DeferredHolder<Identifier, Identifier> add(String name, StatFormatter formatter) {
        var id = FIdentifier.loc(CUSTOM_STAT.getNamespace(), name);
        var holder = CUSTOM_STAT.register(name, () -> id);
        CUSTOM_STAT_FORMATTERS.put(id, formatter);
        return holder;
    }

    @ApiStatus.Internal
    public static void registerFormatter() {
        CUSTOM_STAT_FORMATTERS.forEach(Stats.CUSTOM::get);
    }
}
