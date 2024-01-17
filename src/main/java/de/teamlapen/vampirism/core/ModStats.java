package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.util.CustomStatType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
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

    public static final DeferredHolder<ResourceLocation, ResourceLocation> weapon_table = add("weapon_table");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> interact_alchemical_cauldron = add("interact_alchemical_cauldron");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> interact_with_alchemy_table = add("interact_with_alchemy_table");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> interact_with_altar_of_infusion = add("interact_with_altar_of_infusion");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> interact_with_altar_inspiration = add("interact_with_altar_inspiration");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> interact_with_blood_grinder = add("interact_with_blood_grinder");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> interact_with_garlic_diffuser = add("interact_with_garlic_diffuser");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> interact_with_fog_diffuser = add("interact_with_fog_diffuser");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> interact_with_research_table = add("interact_with_research_table");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> interact_with_ancient_beacon = add("interact_with_ancient_beacon");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> interact_with_totem = add("interact_with_totem");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> interact_with_potion_table = add("interact_with_potion_table");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> interact_with_injection_chair = add("interact_with_injection_chair");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> interact_with_coffin = add("interact_with_coffin");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> interact_with_throne = add("interact_with_throne");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> capture_village = add("capture_village");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> defend_village = add("defend_village");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> win_village_capture = add("win_village_capture");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> infected_creatures = add("infected_creatures");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> mother_defeated = add("mother_defeated");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> killed_with_stake = add("killed_with_stake");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> resurrected = add("resurrected");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> actions_used = add("actions_used");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> skills_unlocked = add("skills_unlocked");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> skills_reset = add("skills_reset");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> tasks_accepted = add("tasks_accepted");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> tasks_completed = add("tasks_completed");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> blood_drunk = add("blood_drunk", BUCKED_FORMATTER);
    public static final DeferredHolder<ResourceLocation, ResourceLocation> amount_bitten = add("amount_bitten");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> altar_of_inspiration_rituals_performed = add("altar_of_inspiration_rituals_performed");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> altar_of_infusion_rituals_performed = add("altar_of_infusion_rituals_performed");
    public static final DeferredHolder<ResourceLocation, ResourceLocation> items_filled_on_blood_pedestal = add("items_filled_on_blood_pedestal");

    private static DeferredHolder<ResourceLocation, ResourceLocation> add(String name) {
        return add(name, StatFormatter.DEFAULT);
    }

    private static DeferredHolder<ResourceLocation, ResourceLocation> add(String name, StatFormatter formatter) {
        var id = new ResourceLocation(CUSTOM_STAT.getNamespace(), name);
        var holder = CUSTOM_STAT.register(name, () -> id);
        CUSTOM_STAT_FORMATTERS.put(id, formatter);
        return holder;
    }

    @ApiStatus.Internal
    public static void register(IEventBus eventBus) {
        CUSTOM_STAT.register(eventBus);
        STAT_TYPES.register(eventBus);
    }

    public static void skillUnlocked(Player player, ISkill<?> skill) {
        player.awardStat(SKILL_UNLOCKED.get().get(skill));
    }

    public static void skillForgotten(Player player, ISkill<?> skill) {
        player.awardStat(SKILL_FORGOTTEN.get().get(skill));
    }

    public static void updateActionTime(Player player, ILastingAction<?> action) {
        player.awardStat(ACTION_TIME.get().get(action));
    }

    public static void updateActionCooldownTime(Player player, IAction<?> action) {
        player.awardStat(ACTION_COOLDOWN_TIME.get().get(action));
    }

    public static void actionUsed(Player player, IAction<?> action) {
        player.awardStat(actions_used.get());
        player.awardStat(ACTION_USED.get().get(action));
    }

    @ApiStatus.Internal
    public static void registerFormatter() {
        CUSTOM_STAT_FORMATTERS.forEach(Stats.CUSTOM::get);
    }
}
