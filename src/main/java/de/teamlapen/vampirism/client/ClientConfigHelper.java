package de.teamlapen.vampirism.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.teamlapen.lib.lib.util.ResourceLocationTypeAdapter;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.minion.IFactionMinionTask;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.minion.INoGlobalCommandTask;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.client.gui.screens.SelectMinionTaskRadialScreen;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = REFERENCE.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientConfigHelper {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(List.class, new IActionListTypeAdapter())
            .registerTypeHierarchyAdapter(ResourceLocation.class, new ResourceLocationTypeAdapter())
            .registerTypeHierarchyAdapter(SelectMinionTaskRadialScreen.Entry.class, new EntryTypeAdapter())
            .create();

    /**
     * Dummy task order identifier id no faction is given, but this should never happen
     */
    private static final ResourceLocation NONE = new ResourceLocation("none");
    /**
     * Cache for the action order
     */
    private static Map<ResourceLocation, List<IAction<?>>> ACTION_ORDER = new HashMap<>();
    /**
     * Cache for the minion task order
     */
    private static Map<ResourceLocation, List<SelectMinionTaskRadialScreen.Entry>> MINION_TASK_ORDER = new HashMap<>();

    /**
     * Caches the action and minion task order for faster access that does not require deserialization on every access
     */
    @SubscribeEvent
    public static void onConfigChanged(@NotNull ModConfigEvent event) {
        if (VampirismConfig.isClientConfigSpec(event.getConfig().getSpec())) {
            try {
                String string = VampirismConfig.CLIENT.actionOrder.get();
                ACTION_ORDER = Objects.requireNonNullElseGet(GSON.fromJson(string, new TypeToken<>() {}), HashMap::new);
            } catch (JsonSyntaxException | IllegalArgumentException e) {
                VampirismConfig.LOGGER.error("Failed to parse action order config", e);
                VampirismConfig.CLIENT.actionOrder.set(VampirismConfig.CLIENT.actionOrder.getDefault());
                ACTION_ORDER = new HashMap<>();
            }
            try {
                String string = VampirismConfig.CLIENT.minionTaskOrder.get();
                MINION_TASK_ORDER = Objects.requireNonNullElseGet(GSON.fromJson(string, new TypeToken<>() {}), HashMap::new);
            } catch (JsonSyntaxException | IllegalArgumentException e) {
                VampirismConfig.LOGGER.error("Failed to parse minion task order config", e);
                VampirismConfig.CLIENT.minionTaskOrder.set(VampirismConfig.CLIENT.minionTaskOrder.getDefault());
                MINION_TASK_ORDER = new HashMap<>();
            }
        }
    }

    /**
     * tests if a serialized action order is in a valid format
     *
     * @param string the serialized order
     * @return true if the order is valid
     */
    public static boolean testActions(Object string) {
        try {
            GSON.fromJson((String) string, new TypeToken<Map<ResourceLocation, List<IAction<?>>>>() {});
        } catch (JsonSyntaxException | ClassCastException | IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    /**
     * tests if a serialized minion task order is in a valid format
     * @param string the serialized order
     * @return true if the order is valid
     */
    public static boolean testTasks(Object string) {
        try {
            GSON.fromJson((String) string, new TypeToken<Map<ResourceLocation, List<SelectMinionTaskRadialScreen.Entry>>>() {});
        } catch (JsonSyntaxException | ClassCastException | IllegalArgumentException  e) {
            return false;
        }
        return true;
    }

    /**
     *
     * @deprecated may be null and does not ensure a valid order
     */
    @Deprecated
    @Nullable
    public static List<IAction<?>> getActionOrder(@NotNull ResourceLocation id) {
        return ACTION_ORDER.get(id);
    }

    /**
     * @implSpec if no order is set for the given faction, the default order is returned and set
     * @param faction the faction for which the order should be returned
     * @return a valid order for the given faction
     */
    @NotNull
    public static List<IAction<?>> getActionOrder(@NotNull IPlayableFaction<?> faction) {
        return Objects.requireNonNullElseGet(ACTION_ORDER.get(faction.getID()), () -> {
            List<IAction<?>> order = getDefaultActionOrder(faction);
            saveActionOrder(faction.getID(), order);
            return order;
        });
    }

    public static List<IAction<?>> getDefaultActionOrder(IPlayableFaction<?> faction) {
        return RegUtil.values(ModRegistries.ACTIONS).stream().filter(action -> action.matchesFaction(faction)).collect(Collectors.toList());
    }

    /**
     * @implSpec if no order is set for the given faction, the default order is returned and set
     * @param faction the faction for which the order should be returned. If no faction is given a default identifier is used
     * @return a valid order for the given faction
     */
    @NotNull
    public static List<SelectMinionTaskRadialScreen.Entry> getMinionTaskOrder(@Nullable IFaction<?> faction) {
        return Objects.requireNonNullElseGet(MINION_TASK_ORDER.get(Optional.ofNullable(faction).map(IFaction::getID).orElse(NONE)),() -> {
            List<SelectMinionTaskRadialScreen.Entry> order = getDefaultMinionTaskOrder(faction);
            saveMinionTaskOrder(faction, order);
            return order;
        });
    }

    /**
     * creates a default order for minion tasks for the given faction. Only matching tasks are included.
     *
     * @param faction the faction for which the order should be created.
     * @return a valid order for the given faction
     */
    public static List<SelectMinionTaskRadialScreen.Entry> getDefaultMinionTaskOrder(@Nullable IFaction<?> faction) {
        return Stream.concat(RegUtil.values(ModRegistries.MINION_TASKS).stream().filter(task -> !(task instanceof INoGlobalCommandTask<?,?>)).filter(task -> {
            if (task instanceof IFactionMinionTask<?, ?> factionTask) {
                return factionTask.getFaction() == null || factionTask.getFaction() == faction;
            } else {
                return true;
            }
        }).map(SelectMinionTaskRadialScreen.Entry::new), SelectMinionTaskRadialScreen.CUSTOM_ENTRIES.values().stream()).collect(Collectors.toList());
    }

    /**
     * Saves the given order for the given faction
     *
     * @param id the ordering identifier (faction id)
     * @param actions the ordering
     */
    public static void saveActionOrder(@NotNull ResourceLocation id, @NotNull  List<IAction<?>> actions) {
        ACTION_ORDER.put(id, actions);
        String object = GSON.toJson(ACTION_ORDER);
        VampirismConfig.CLIENT.actionOrder.set(object);
    }

    /**
     * Saves the given order for the given faction
     *
     * @param faction the faction for which the order should be saved. If no faction is given a default identifier is used
     * @param tasks the ordering
     */
    public static void saveMinionTaskOrder(@Nullable IFaction<?> faction, @NotNull  List<SelectMinionTaskRadialScreen.Entry> tasks) {
        MINION_TASK_ORDER.put(Optional.ofNullable(faction).map(IFaction::getID).orElse(NONE), tasks);
        String object = GSON.toJson(MINION_TASK_ORDER);
        VampirismConfig.CLIENT.minionTaskOrder.set(object);

    }

    private static final class IActionListTypeAdapter extends TypeAdapter<List<IAction<?>>> {

        @Override
        public @NotNull List<IAction<?>> read(@NotNull JsonReader in) throws IOException {
            List<IAction<?>> actions = new ArrayList<>();
            in.beginArray();
            while (in.hasNext()) {
                IAction<?> action = RegUtil.getAction(new ResourceLocation(in.nextString()));
                if (action != null) {
                    actions.add(action);
                }
            }
            in.endArray();
            return actions;
        }

        @Override
        public void write(@NotNull JsonWriter out, @Nullable List<IAction<?>> value) throws IOException {
            out.beginArray();
            if (value != null) {
                for (IAction<?> action : value) {
                    out.value(RegUtil.id(action).toString());
                }
            }
            out.endArray();
        }
    }

    /**
     * Gson type adapter for {@link SelectMinionTaskRadialScreen.Entry}
     */
    private static final class EntryTypeAdapter extends TypeAdapter<SelectMinionTaskRadialScreen.Entry> {

        @Override
        public void write(JsonWriter out, @Nullable SelectMinionTaskRadialScreen.Entry value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.value(value.getId().toString());
        }

        @Override
        public SelectMinionTaskRadialScreen.Entry read(JsonReader in) throws IOException {
            ResourceLocation id = new ResourceLocation(in.nextString());
            IMinionTask<?, ?> minionTask = RegUtil.getMinionTask(id);
            if (minionTask != null) {
                return new SelectMinionTaskRadialScreen.Entry(minionTask);
            } else {
                return SelectMinionTaskRadialScreen.CUSTOM_ENTRIES.get(id);
            }
        }
    }

}
