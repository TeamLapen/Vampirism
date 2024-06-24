package de.teamlapen.vampirism.client;

import com.google.gson.*;
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
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.gui.screens.SelectMinionTaskRadialScreen;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EventBusSubscriber(value = Dist.CLIENT, modid = REFERENCE.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ClientConfigHelper {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(TypeToken.getParameterized(List.class, TypeToken.getParameterized(Holder.class, IAction.class).getType()).getType(), new IActionListTypeAdapter())
            .registerTypeAdapter(TypeToken.getParameterized(List.class, SelectMinionTaskRadialScreen.Entry.class).getType(), new EntryListTypeAdapter())
            .registerTypeHierarchyAdapter(ResourceLocation.class, new ResourceLocationTypeAdapter())
            .create();

    @SuppressWarnings("unchecked")
    private static final TypeToken<Map<ResourceLocation, List<Holder<IAction<?>>>>> ACTION_TOKEN = (TypeToken<Map<ResourceLocation, List<Holder<IAction<?>>>>>) TypeToken.getParameterized(Map.class, ResourceLocation.class, TypeToken.getParameterized(List.class, TypeToken.getParameterized(Holder.class, IAction.class).getType()).getType());
    @SuppressWarnings("unchecked")
    private static final TypeToken<Map<ResourceLocation, List<SelectMinionTaskRadialScreen.Entry>>> MINION_TASK_TOKEN = (TypeToken<Map<ResourceLocation, List<SelectMinionTaskRadialScreen.Entry>>>) TypeToken.getParameterized(Map.class, ResourceLocation.class, TypeToken.getParameterized(List.class, SelectMinionTaskRadialScreen.Entry.class).getType());

    /**
     * Dummy task order identifier id no faction is given, but this should never happen
     */
    private static final ResourceLocation NONE = VResourceLocation.mod("none");
    /**
     * Cache for the action order
     */
    private static Map<ResourceLocation, List<Holder<IAction<?>>>> ACTION_ORDER = new HashMap<>();
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
                ACTION_ORDER = Objects.requireNonNullElseGet(GSON.fromJson(string, ACTION_TOKEN), HashMap::new);
            } catch (JsonSyntaxException | IllegalArgumentException e) {
                VampirismConfig.LOGGER.error("Failed to parse action order config", e);
                VampirismConfig.CLIENT.actionOrder.set(VampirismConfig.CLIENT.actionOrder.getDefault());
                ACTION_ORDER = new HashMap<>();
            }
            try {
                String string = VampirismConfig.CLIENT.minionTaskOrder.get();
                MINION_TASK_ORDER = Objects.requireNonNullElseGet(GSON.fromJson(string, MINION_TASK_TOKEN), HashMap::new);
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
            GSON.fromJson((String) string, ACTION_TOKEN);
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
            GSON.fromJson((String) string, MINION_TASK_TOKEN);
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
    public static List<Holder<IAction<?>>> getActionOrder(@NotNull ResourceLocation id) {
        return ACTION_ORDER.get(id);
    }

    /**
     * @implSpec if no order is set for the given faction, the default order is returned and set
     * @param faction the faction for which the order should be returned
     * @return a valid order for the given faction
     */
    @NotNull
    public static List<Holder<IAction<?>>> getActionOrder(@NotNull Holder<? extends IPlayableFaction<?>> faction) {
        ResourceLocation id = faction.unwrapKey().map(ResourceKey::location).orElseThrow();
        return Objects.requireNonNullElseGet(ACTION_ORDER.get(id), () -> {
            List<Holder<IAction<?>>> order = getDefaultActionOrder(faction);
            saveActionOrder(id, order);
            return order;
        });
    }

    public static List<Holder<IAction<?>>> getDefaultActionOrder(Holder<? extends IPlayableFaction<?>> faction) {
        return ModRegistries.ACTIONS.holders().filter(s -> s.value().matchesFaction(faction)).collect(Collectors.toList());
    }

    /**
     * @implSpec if no order is set for the given faction, the default order is returned and set
     * @param faction the faction for which the order should be returned. If no faction is given a default identifier is used
     * @return a valid order for the given faction
     */
    @NotNull
    public static List<SelectMinionTaskRadialScreen.Entry> getMinionTaskOrder(@Nullable Holder<? extends IFaction<?>> faction) {
        return Objects.requireNonNullElseGet(MINION_TASK_ORDER.get(Optional.ofNullable(faction).flatMap(Holder::unwrapKey).map(ResourceKey::location).orElse(NONE)),() -> {
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
    public static List<SelectMinionTaskRadialScreen.Entry> getDefaultMinionTaskOrder(@Nullable Holder<? extends IFaction<?>> faction) {
        return Stream.concat(RegUtil.values(ModRegistries.MINION_TASKS).stream().filter(task -> !(task instanceof INoGlobalCommandTask<?,?>)).filter(task -> {
            if (task instanceof IFactionMinionTask<?, ?> factionTask) {
                return factionTask.getFaction() == null || IFaction.is(factionTask.getFaction(), faction);
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
    public static void saveActionOrder(@NotNull ResourceLocation id, @NotNull  List<Holder<IAction<?>>> actions) {
        ACTION_ORDER.put(id, actions);
        try {
            String object = GSON.toJson(ACTION_ORDER, ACTION_TOKEN.getType());
            VampirismConfig.CLIENT.actionOrder.set(object);
        } catch (JsonParseException e) {
            VampirismConfig.LOGGER.error("Failed to save action order", e);
        }
    }

    /**
     * Saves the given order for the given faction
     *
     * @param faction the faction for which the order should be saved. If no faction is given a default identifier is used
     * @param tasks the ordering
     */
    public static void saveMinionTaskOrder(@Nullable Holder<? extends IFaction<?>> faction, @NotNull  List<SelectMinionTaskRadialScreen.Entry> tasks) {
        MINION_TASK_ORDER.put(Optional.ofNullable(faction).flatMap(Holder::unwrapKey).map(ResourceKey::location).orElse(NONE), tasks);
        try {
            String object = GSON.toJson(MINION_TASK_ORDER, MINION_TASK_TOKEN.getType());
            VampirismConfig.CLIENT.minionTaskOrder.set(object);
        } catch (JsonParseException e) {
            VampirismConfig.LOGGER.error("Failed to save minion task order", e);
        }

    }

    private static final class IActionListTypeAdapter extends TypeAdapter<List<Holder<IAction<?>>>> {

        @Override
        public @NotNull List<Holder<IAction<?>>> read(@NotNull JsonReader in) throws IOException {
            List<Holder<IAction<?>>> actions = new ArrayList<>();
            in.beginArray();
            while (in.hasNext()) {
                ModRegistries.ACTIONS.getHolder(ResourceLocation.parse(in.nextString())).ifPresent(actions::add);
            }
            in.endArray();
            return actions;
        }

        @Override
        public void write(@NotNull JsonWriter out, @Nullable List<Holder<IAction<?>>> value) throws IOException {
            out.beginArray();
            if (value != null) {
                for (Holder<IAction<?>> action : value) {
                    ResourceLocation location = action.unwrapKey().map(ResourceKey::location).orElse(null);
                    if (location != null) {
                        out.value(location.toString());
                    }
                }
            }
            out.endArray();
        }
    }

    private static final class EntryListTypeAdapter extends TypeAdapter<List<SelectMinionTaskRadialScreen.Entry>> {

        @Override
        public @NotNull List<SelectMinionTaskRadialScreen.Entry> read(@NotNull JsonReader in) throws IOException {
            List<SelectMinionTaskRadialScreen.Entry> actions = new ArrayList<>();
            in.beginArray();
            while (in.hasNext()) {
                ResourceLocation resourceLocation = ResourceLocation.parse(in.nextString());
                IMinionTask<?, ?> minionTask = RegUtil.getMinionTask(resourceLocation);
                SelectMinionTaskRadialScreen.Entry entry = SelectMinionTaskRadialScreen.CUSTOM_ENTRIES.get(resourceLocation);
                if (entry != null) {
                    actions.add(entry);
                } else if (minionTask != null) {
                    actions.add(new SelectMinionTaskRadialScreen.Entry(minionTask));
                }
            }
            in.endArray();
            return actions;
        }

        @Override
        public void write(@NotNull JsonWriter out, @Nullable List<SelectMinionTaskRadialScreen.Entry> value) throws IOException {
            out.beginArray();
            if (value != null) {
                for (SelectMinionTaskRadialScreen.Entry action : value) {
                    out.value(action.getId().toString());
                }
            }
            out.endArray();
        }
    }
}
