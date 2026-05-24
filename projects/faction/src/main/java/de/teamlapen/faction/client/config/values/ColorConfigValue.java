package de.teamlapen.faction.client.config.values;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ColorConfigValue {

    private static final Pattern HEX_PATTERN = Pattern.compile("^#?[0-9a-fA-F]{6}$");

    private static final ThreadLocal<@Nullable List<ColorConfigValue>> ACTIVE_COLLECTION = new ThreadLocal<>();
    private static final Map<IConfigSpec, List<ColorConfigValue>> BY_SPEC = new IdentityHashMap<>();
    private static final Set<IEventBus> SUBSCRIBED_BUSES = Collections.newSetFromMap(new IdentityHashMap<>());

    private final ModConfigSpec.ConfigValue<String> value;
    private final int defaultColor;
    private int cached;

    private ColorConfigValue(ModConfigSpec.ConfigValue<String> value, int defaultColor) {
        this.value = value;
        this.defaultColor = defaultColor;
        this.cached = defaultColor;
    }

    public static ColorConfigValue define(ModConfigSpec.Builder builder, String path, String defaultHex, String comment) {
        int defaultColor = parse(defaultHex, 0);
        ModConfigSpec.ConfigValue<String> value = builder
                .comment(comment)
                .comment(" HEX format (e.g. #e0b74f), with or without the leading '#', no alpha channel.")
                .define(path, defaultHex, ColorConfigValue::isValid);
        ColorConfigValue color = new ColorConfigValue(value, defaultColor);
        List<ColorConfigValue> active = ACTIVE_COLLECTION.get();
        if (active != null) {
            active.add(color);
        }
        return color;
    }

    public static <T> Pair<T, ModConfigSpec> configure(Function<ModConfigSpec.Builder, T> consumer) {
        ACTIVE_COLLECTION.set(new ArrayList<>());
        Pair<T, ModConfigSpec> result = new ModConfigSpec.Builder().configure(consumer);
        List<ColorConfigValue> list = ACTIVE_COLLECTION.get();
        ACTIVE_COLLECTION.remove();
        if (list != null && !list.isEmpty()) {
            BY_SPEC.put(result.getRight(), list);
        }
        return result;
    }

    public static void subscribe(IEventBus modBus) {
        if (SUBSCRIBED_BUSES.add(modBus)) {
            modBus.addListener(ColorConfigValue::onConfigEvent);
        }
    }

    private static void onConfigEvent(ModConfigEvent event) {
        List<ColorConfigValue> list = BY_SPEC.get(event.getConfig().getSpec());
        if (list == null) return;
        for (ColorConfigValue color : list) {
            color.reload();
        }
    }

    public int get() {
        return cached;
    }

    public void reload() {
        this.cached = parse(value.get(), defaultColor);
    }

    private static boolean isValid(Object o) {
        return o instanceof String s && HEX_PATTERN.matcher(s).matches();
    }

    private static int parse(String hex, int fallback) {
        try {
            return Integer.parseInt(hex.replaceFirst("^#", ""), 16);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}