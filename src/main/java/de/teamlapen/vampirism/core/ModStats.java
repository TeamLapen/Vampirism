package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModStats {
    private static final DeferredRegister<ResourceLocation> CUSTOM_STAT = DeferredRegister.create(Registries.CUSTOM_STAT, REFERENCE.MODID);
    private static final Map<ResourceLocation, StatFormatter> ALL_STATS = new HashMap<>();
    private static final Map<ResourceLocation, Holder<ResourceLocation>> ACTION_USED_STATS = new HashMap<>();
    private static final Map<ResourceLocation, Holder<ResourceLocation>> ACTION_TIME_STATS = new HashMap<>();

    private static final StatFormatter BUCKED_FORMATTER = (stat) -> StatFormatter.DEFAULT.format(stat) + " mb";

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

    static void registerCustomStats() {
//        ModRegistries.allActions().forEach(entry -> {
//            ResourceLocation location = entry.getKey().location();
//            var used = add("action_used_" + location.toString().replace(':', '_'));
//            ACTION_USED_STATS.put(used.getId(), used);
//            var time = add("action_time_" + location.toString().replace(':', '_'), StatFormatter.TIME);
//            ACTION_TIME_STATS.put(time.getId(), time);
//        });
        ALL_STATS.forEach(Stats.CUSTOM::get);
    }

    public static Optional<Component> getStatDisplay(ResourceLocation stat) {
        if (ACTION_TIME_STATS.containsKey(stat)) {
            return Optional.of(Component.translatable("stat." + REFERENCE.MODID + ".action_time", Component.translatable("action." + stat.getNamespace() + "." + stat.getPath().substring(12))));
        } else if (ACTION_USED_STATS.containsKey(stat)) {
            return Optional.of(Component.translatable("stat." + REFERENCE.MODID + ".action_used", Component.translatable("action." + stat.getNamespace() + "." + stat.getPath().substring(12))));
        }
        return Optional.empty();
    }

    private static DeferredHolder<ResourceLocation, ResourceLocation>  add(String name) {
        return add(name, StatFormatter.DEFAULT);
    }

    @ApiStatus.Internal
    public static void register(IEventBus eventBus) {
        CUSTOM_STAT.register(eventBus);
    }

    private static DeferredHolder<ResourceLocation, ResourceLocation> add(String name, StatFormatter formatter) {
        var id = new ResourceLocation(CUSTOM_STAT.getNamespace(), name);
        var holder = CUSTOM_STAT.register(name, () -> id);
        ALL_STATS.put(id, formatter);
        return holder;
    }

    public static void updateActionTime(Player player, ILastingAction<?> action) {
        RegUtil.key(action).map(ResourceKey::location).map(ACTION_TIME_STATS::get).ifPresent(location -> {
            player.awardStat(location.value());
        });
    }

    public static void updateActionUsed(Player player, IAction<?> action) {
        RegUtil.key(action).map(ResourceKey::location).map(ACTION_USED_STATS::get).ifPresent(location -> {
            player.awardStat(location.value());
        });
        player.awardStat(actions_used.get());
    }
}
