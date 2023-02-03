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
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.client.gui.screens.SelectMinionTaskRadialScreen;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = REFERENCE.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientConfigHelper {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(IAction.class, new IActionTypeAdapter())
            .registerTypeHierarchyAdapter(ResourceLocation.class, new ResourceLocationTypeAdapter())
            .registerTypeHierarchyAdapter(SelectMinionTaskRadialScreen.Entry.class, new EntryTypeAdapter())
            .create();

    private static final ResourceLocation none = new ResourceLocation("none");
    private static Map<ResourceLocation, List<IAction<?>>> actionOrder = new HashMap<>();
    private static Map<ResourceLocation, List<SelectMinionTaskRadialScreen.Entry>> minionTaskOrder = new HashMap<>();

    @SubscribeEvent
    public static void onConfigChanged(@NotNull ModConfigEvent event) {
        if (VampirismConfig.isClientConfigSpec(event.getConfig().getSpec())) {
            try {
                String string = VampirismConfig.CLIENT.actionOrder.get();
                actionOrder = Objects.requireNonNullElseGet(GSON.fromJson(string, new TypeToken<>() {}), HashMap::new);
            } catch (JsonSyntaxException | IllegalArgumentException e) {
                VampirismConfig.LOGGER.error("Failed to parse action order config", e);
                VampirismConfig.CLIENT.actionOrder.set(VampirismConfig.CLIENT.actionOrder.getDefault());
                actionOrder = new HashMap<>();
            }
            try {
                String string = VampirismConfig.CLIENT.minionTaskOrder.get();
                minionTaskOrder = Objects.requireNonNullElseGet(GSON.fromJson(string, new TypeToken<>() {}), HashMap::new);
            } catch (JsonSyntaxException | IllegalArgumentException e) {
                VampirismConfig.LOGGER.error("Failed to parse minion task order config", e);
                VampirismConfig.CLIENT.minionTaskOrder.set(VampirismConfig.CLIENT.minionTaskOrder.getDefault());
                minionTaskOrder = new HashMap<>();
            }
        }
    }

    public static boolean testActions(Object string) {
        try {
            GSON.fromJson((String) string, new TypeToken<Map<ResourceLocation, List<IAction<?>>>>() {});
        } catch (JsonSyntaxException | ClassCastException | IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public static boolean testTasks(Object string) {
        try {
            GSON.fromJson((String) string, new TypeToken<Map<ResourceLocation, List<SelectMinionTaskRadialScreen.Entry>>>() {});
        } catch (JsonSyntaxException | ClassCastException | IllegalArgumentException  e) {
            return false;
        }
        return true;
    }

    public static List<IAction<?>> getActionOrder(@NotNull ResourceLocation id) {
        return Objects.requireNonNullElseGet(actionOrder.get(id), ArrayList::new);
    }

    public static List<SelectMinionTaskRadialScreen.Entry> getMinionTaskOrder(@Nullable IFaction<?> faction) {
        return Objects.requireNonNullElseGet(minionTaskOrder.get(Optional.ofNullable(faction).map(IFaction::getID).orElse(none)), ArrayList::new);
    }

    public static void setActionOrder(@NotNull ResourceLocation id, @NotNull  List<IAction<?>> actions) {
        actionOrder.put(id, actions);
        String object = GSON.toJson(actionOrder);
        VampirismConfig.CLIENT.actionOrder.set(object);
    }

    public static void setMinionTaskOrder(@Nullable IFaction<?> faction, @NotNull  List<SelectMinionTaskRadialScreen.Entry> tasks) {
        minionTaskOrder.put(Optional.ofNullable(faction).map(IFaction::getID).orElse(none), tasks);
        String object = GSON.toJson(minionTaskOrder);
        VampirismConfig.CLIENT.minionTaskOrder.set(object);

    }

    public static final class IActionTypeAdapter extends TypeAdapter<IAction<?>> {

        @Override
        public @NotNull IAction<?> read(@NotNull JsonReader in) throws IOException {
            return RegUtil.getAction(new ResourceLocation(in.nextString()));
        }

        @Override
        public void write(@NotNull JsonWriter out, @Nullable IAction<?> value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.value(RegUtil.id(value).toString());
        }
    }

    public static final class EntryTypeAdapter extends TypeAdapter<SelectMinionTaskRadialScreen.Entry> {

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
