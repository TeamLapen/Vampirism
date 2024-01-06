package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModStats {
    private static final Map<ResourceLocation, StatFormatter> ALL_STATS = new HashMap<>();
    private static final Map<ResourceKey<IAction<?>>, ResourceLocation> ACTION_USED_STATS = new HashMap<>();
    private static final Map<ResourceKey<IAction<?>>, ResourceLocation> ACTION_TIME_STATS = new HashMap<>();

    private static final StatFormatter BUCKED_FORMATTER = (stat) -> {
        return StatFormatter.DEFAULT.format(stat) + " mb";
    };

    public static final ResourceLocation weapon_table = add("weapon_table");
    public static final ResourceLocation interact_alchemical_cauldron = add("interact_alchemical_cauldron");
    public static final ResourceLocation interact_with_alchemy_table = add("interact_with_alchemy_table");
    public static final ResourceLocation interact_with_altar_of_infusion = add("interact_with_altar_of_infusion");
    public static final ResourceLocation interact_with_altar_inspiration = add("interact_with_altar_inspiration");
    public static final ResourceLocation interact_with_blood_grinder = add("interact_with_blood_grinder");
    public static final ResourceLocation interact_with_garlic_diffuser = add("interact_with_garlic_diffuser");
    public static final ResourceLocation interact_with_fog_diffuser = add("interact_with_fog_diffuser");
    public static final ResourceLocation interact_with_research_table = add("interact_with_research_table");
    public static final ResourceLocation interact_with_ancient_beacon = add("interact_with_ancient_beacon");
    public static final ResourceLocation interact_with_totem = add("interact_with_totem");
    public static final ResourceLocation interact_with_potion_table = add("interact_with_potion_table");
    public static final ResourceLocation interact_with_injection_chair = add("interact_with_injection_chair");
    public static final ResourceLocation interact_with_coffin = add("interact_with_coffin");
    public static final ResourceLocation interact_with_throne = add("interact_with_throne");
    public static final ResourceLocation capture_village = add("capture_village");
    public static final ResourceLocation defend_village = add("defend_village");
    public static final ResourceLocation win_village_capture = add("win_village_capture");
    public static final ResourceLocation infected_creatures = add("infected_creatures");
    public static final ResourceLocation mother_defeated = add("mother_defeated");
    public static final ResourceLocation killed_with_stake = add("killed_with_stake");
    public static final ResourceLocation resurrected = add("resurrected");
    public static final ResourceLocation actions_used = add("actions_used");
    public static final ResourceLocation skills_unlocked = add("skills_unlocked");
    public static final ResourceLocation skills_reset = add("skills_reset");
    public static final ResourceLocation tasks_accepted = add("tasks_accepted");
    public static final ResourceLocation tasks_completed = add("tasks_completed");
    public static final ResourceLocation blood_drunk = add("blood_drunk", BUCKED_FORMATTER);
    public static final ResourceLocation amount_bitten = add("amount_bitten");
    public static final ResourceLocation altar_of_inspiration_rituals_performed = add("altar_of_inspiration_rituals_performed");
    public static final ResourceLocation altar_of_infusion_rituals_performed = add("altar_of_infusion_rituals_performed");
    public static final ResourceLocation items_filled_on_blood_pedestal = add("items_filled_on_blood_pedestal");

    static void registerCustomStats() {
        ModRegistries.ACTIONS.holders().forEach(entry -> {
            ResourceLocation location = entry.key().location();
            ACTION_USED_STATS.put(entry.key(), add(location.withPrefix("action_used_")));
        });
        ModRegistries.ACTIONS.holders().filter(s -> s.value() instanceof ILastingAction<?>).forEach(entry -> {
            ResourceLocation location = entry.key().location();
            ACTION_TIME_STATS.put(entry.key(), add(location.withPrefix("action_time_"), StatFormatter.TIME));
        });
        ALL_STATS.forEach(ModStats::register);
    }

    public static Optional<Component> getStatDisplay(ResourceLocation stat) {
        if (ACTION_TIME_STATS.containsValue(stat)) {
            return Optional.of(Component.translatable("stat." + REFERENCE.MODID + ".action_time", Component.translatable("action." + stat.getNamespace() + "." + stat.getPath().substring(12))));
        } else if (ACTION_USED_STATS.containsValue(stat)) {
            return Optional.of(Component.translatable("stat." + REFERENCE.MODID + ".action_used", Component.translatable("action." + stat.getNamespace() + "." + stat.getPath().substring(12))));
        }
        return Optional.empty();
    }

    private static ResourceLocation add(String name) {
        return add(name, StatFormatter.DEFAULT);
    }

    private static ResourceLocation add(String name, @SuppressWarnings("SameParameterValue") StatFormatter formatter) {
        return add(new ResourceLocation(REFERENCE.MODID, name), formatter);
    }

    private static ResourceLocation add(ResourceLocation id) {
        return add(id, StatFormatter.DEFAULT);
    }

    private static ResourceLocation add(ResourceLocation id, @SuppressWarnings("SameParameterValue") StatFormatter formatter) {
        ALL_STATS.put(id, formatter);
        return id;
    }

    private static void register(@NotNull ResourceLocation id, StatFormatter formatter) {
        Registry.register(BuiltInRegistries.CUSTOM_STAT, id, id);
        Stats.CUSTOM.get(id, formatter);
    }

    public static void updateActionTime(Player player, ILastingAction<?> action) {
        RegUtil.key(action).ifPresent(key -> {
            ResourceLocation location = ACTION_TIME_STATS.get(key);
            player.awardStat(location);
        });
    }

    public static void updateActionUsed(Player player, IAction<?> action) {
        RegUtil.key(action).ifPresent(key -> {
            ResourceLocation location = ACTION_USED_STATS.get(key);
            player.awardStat(location);
        });
        player.awardStat(actions_used);
    }
}
