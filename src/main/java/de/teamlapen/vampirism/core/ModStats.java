package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.util.CustomStatType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public class ModStats {
    private static final DeferredRegister<StatType<?>> STAT_TYPES = DeferredRegister.create(Registries.STAT_TYPE, REFERENCE.MODID);
    private static final DeferredRegister<ResourceLocation> CUSTOM_STAT = DeferredRegister.create(Registries.CUSTOM_STAT, REFERENCE.MODID);
    private static final Map<ResourceLocation, StatFormatter> CUSTOM_STAT_FORMATTERS = new HashMap<>();

    private static final StatFormatter BUCKED_FORMATTER = (stat) -> StatFormatter.DEFAULT.format(stat) + " mb";

    public static final DeferredHolder<StatType<?>, StatType<ISkill<?>>> SKILL_UNLOCKED = STAT_TYPES.register("skill_unlocked", () -> new StatType<>(ModRegistries.SKILLS, Component.translatable("stat_type." + REFERENCE.MODID + ".skill_unlocked")));
    public static final DeferredHolder<StatType<?>, StatType<ISkill<?>>> SKILL_FORGOTTEN = STAT_TYPES.register("skill_forgotten", () -> new StatType<>(ModRegistries.SKILLS, Component.translatable("stat_type." + REFERENCE.MODID + ".skill_forgotten")));
    public static final DeferredHolder<StatType<?>, StatType<IAction<?>>> ACTION_USED = STAT_TYPES.register("action_used", () -> new StatType<>(ModRegistries.ACTIONS, Component.translatable("stat_type." + REFERENCE.MODID + ".action_used")));
    public static final DeferredHolder<StatType<?>, CustomStatType<IAction<?>>> ACTION_TIME = STAT_TYPES.register("action_time", () -> new CustomStatType<>(ModRegistries.ACTIONS, Component.translatable("stat_type." + REFERENCE.MODID + ".action_time"), StatFormatter.TIME));
    public static final DeferredHolder<StatType<?>, CustomStatType<IAction<?>>> ACTION_COOLDOWN_TIME = STAT_TYPES.register("action_cooldown", () -> new CustomStatType<>(ModRegistries.ACTIONS, Component.translatable("stat_type." + REFERENCE.MODID + ".action_cooldown_time"), StatFormatter.TIME));

    public static final DeferredHolder<ResourceLocation, ResourceLocation> WEAPON_TABLE = add("weapon_table");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> INTERACT_ALCHEMICAL_CAULDRON = add("interact_alchemical_cauldron");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> INTERACT_WITH_ALCHEMY_TABLE = add("interact_with_alchemy_table");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> INTERACT_WITH_ALTAR_OF_INFUSION = add("interact_with_altar_of_infusion");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> INTERACT_WITH_ALTAR_INSPIRATION = add("interact_with_altar_inspiration");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> INTERACT_WITH_BLOOD_GRINDER = add("interact_with_blood_grinder");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> INTERACT_WITH_GARLIC_DIFFUSER = add("interact_with_garlic_diffuser");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> INTERACT_WITH_FOG_DIFFUSER = add("interact_with_fog_diffuser");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> INTERACT_WITH_RESEARCH_TABLE = add("interact_with_research_table");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> INTERACT_WITH_ANCIENT_BEACON = add("interact_with_ancient_beacon");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> INTERACT_WITH_TOTEM = add("interact_with_totem");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> INTERACT_WITH_POTION_TABLE = add("interact_with_potion_table");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> INTERACT_WITH_INJECTION_CHAIR = add("interact_with_injection_chair");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> INTERACT_WITH_COFFIN = add("interact_with_coffin");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> INTERACT_WITH_THRONE = add("interact_with_throne");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> CAPTURE_VILLAGE = add("capture_village");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> DEFEND_VILLAGE = add("defend_village");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> WIN_VILLAGE_CAPTURE = add("win_village_capture");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> INFECTED_CREATURES = add("infected_creatures");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> MOTHER_DEFEATED = add("mother_defeated");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> KILLED_WITH_STAKE = add("killed_with_stake");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> RESURRECTED = add("resurrected");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> TASKS_ACCEPTED = add("tasks_accepted");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> TASKS_COMPLETED = add("tasks_completed");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> BLOOD_DRUNK = add("blood_drunk", BUCKED_FORMATTER);
    public static final DeferredHolder<ResourceLocation, ResourceLocation> AMOUNT_BITTEN = add("amount_bitten");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> ALTAR_OF_INSPIRATION_RITUALS_PERFORMED = add("altar_of_inspiration_rituals_performed");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> ALTAR_OF_INFUSION_RITUALS_PERFORMED = add("altar_of_infusion_rituals_performed");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> ITEMS_FILLED_ON_BLOOD_PEDESTAL = add("items_filled_on_blood_pedestal");

    private static DeferredHolder<ResourceLocation, ResourceLocation> add(String name) {
        return add(name, StatFormatter.DEFAULT);
    }

    private static DeferredHolder<ResourceLocation, ResourceLocation> add(String name, StatFormatter formatter) {
        var id = VResourceLocation.loc(CUSTOM_STAT.getNamespace(), name);
        var holder = CUSTOM_STAT.register(name, () -> id);
        CUSTOM_STAT_FORMATTERS.put(id, formatter);
        return holder;
    }

    static void register(IEventBus eventBus) {
        CUSTOM_STAT.register(eventBus);
        STAT_TYPES.register(eventBus);
    }

    @ApiStatus.Internal
    public static void registerFormatter() {
        CUSTOM_STAT_FORMATTERS.forEach(Stats.CUSTOM::get);
    }
}
