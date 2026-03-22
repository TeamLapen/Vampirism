package de.teamlapen.vampirism.common.config;

import de.teamlapen.vampirism.VampirismMod;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Intermediate builder stage for Vampirism's balance configuration ({@link BalanceConfig}).
 * <p>
 * The balance configuration is statically built with this builder.
 * During mod construction, addon mods can register modifications to the default values and comments.
 * Finally, during RegistryEvent&lt;Block&gt;, the configuration is transferred to the NeoForge system
 * respecting the registered modifications and thereby finalized.
 */
public class BalanceBuilder {

    private static final Logger LOGGER = LogManager.getLogger();

    private static void setVal(BalanceConfig conf, String name, Object value) {
        try {
            Field field = BalanceConfig.class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(conf, value);
        } catch (NoSuchFieldException e) {
            LOGGER.error("Failed to set balance config value '{}'", name, e);
        } catch (IllegalAccessException e) {
            LOGGER.error("Illegal access when setting balance config value '{}'", name, e);
        }
    }

    /**
     * Ordered map of categories to their config entries, preserving declaration order.
     */
    private final SequencedMap<String, SequencedMap<String, Conf>> categoryConfigMap = new LinkedHashMap<>();
    private final Map<String, String> categoryPrefixMap = new HashMap<>();

    /**
     * Holds (potentially concurrently) added config modifications that are applied during build.
     */
    private final ConcurrentHashMap<String, Consumer<? extends Conf>> balanceModifications = new ConcurrentHashMap<>();

    /**
     * The active category map, updated each time {@link #category(String, String)} is called.
     */
    private SequencedMap<String, Conf> activeCategory;

    /**
     * The pending comment to attach to the next config entry, reset after use.
     */
    @Nullable
    private String currentComment;

    /**
     * Registers a modification to a config entry identified by its full key (category prefix + field name).
     * If a modifier for the given key already exists, it will be overridden with a warning in dev environments.
     */
    public void addBalanceModifier(String key, Consumer<? extends Conf> modifier) {
        if (balanceModifications.put(key, modifier) != null) {
            if (VampirismMod.inDev) LOGGER.warn("Overriding existing config modifier for '{}'", key);
        }
    }

    /**
     * Builds the registered configuration, applying any registered modifiers, and injects
     * the resulting {@link ModConfigSpec.ConfigValue} instances into the given {@link BalanceConfig}
     * via reflection.
     */
    public void build(BalanceConfig conf, ModConfigSpec.Builder builder) {
        if (!balanceModifications.isEmpty()) {
            LOGGER.info("Building balance configuration with {} modifier(s)", balanceModifications.size());
        }
        for (Map.Entry<String, SequencedMap<String, Conf>> categoryEntry : categoryConfigMap.entrySet()) {
            String category = categoryEntry.getKey();
            String catPrefix = categoryPrefixMap.getOrDefault(category, category);
            builder.push(category);
            for (Map.Entry<String, Conf> confEntry : categoryEntry.getValue().entrySet()) {
                String name = confEntry.getKey();
                String fullName = catPrefix.isEmpty() ? name : catPrefix + name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
                Conf c = confEntry.getValue();
                @SuppressWarnings("unchecked")
                Consumer<Conf> modifier = (Consumer<Conf>) balanceModifications.get(fullName);
                if (modifier != null) {
                    try {
                        modifier.accept(c);
                    } catch (Exception e) {
                        LOGGER.error("Failed to apply balance config modifier for '{}'", fullName, e);
                    }
                }
                setVal(conf, fullName, c.build(builder));
            }
            builder.pop();
        }
        categoryConfigMap.clear();
        balanceModifications.clear();
        categoryPrefixMap.clear();
        currentComment = null;
    }

    /**
     * Starts a new config category. All entries defined after this call belong to this category
     * until the next call to {@code category()}.
     *
     * @param name   the category name used as the TOML section header
     * @param prefix the prefix prepended to field names to form the full config key (use {@code ""} for none)
     */
    public BalanceBuilder category(String name, String prefix) {
        activeCategory = new LinkedHashMap<>();
        categoryConfigMap.put(name, activeCategory);
        categoryPrefixMap.put(name, prefix);
        return this;
    }

    /**
     * Checks that all fields in {@link BalanceConfig} have been assigned a value.
     *
     * @throws IllegalStateException if any field is still {@code null}
     */
    public void checkFields(BalanceConfig config) throws IllegalStateException {
        try {
            for (Field field : BalanceConfig.class.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.get(config) == null) {
                    throw new IllegalStateException("Balance config value '" + field.getName() + "' was not set.");
                }
            }
        } catch (IllegalAccessException e) {
            LOGGER.error("Illegal access when checking balance config fields", e);
        }
    }

    /**
     * Attaches a comment to the next config entry defined via {@link #define}, {@link #defineInRange}, or {@link #defineList}.
     */
    public BalanceBuilder comment(String comment) {
        this.currentComment = comment;
        return this;
    }

    /**
     * Adds a pre-built {@link Conf} directly to the active category.
     * Prefer using {@link #define}, {@link #defineInRange}, and {@link #defineList} where possible.
     */
    public BalanceBuilder config(Conf value) {
        activeCategory.put(value.name, value);
        return this;
    }

    /**
     * Defines a boolean config entry in the active category.
     *
     * @return {@code null} — acts as a drop-in replacement for {@link ModConfigSpec.Builder#define(String, boolean)}
     */
    @SuppressWarnings("SameReturnValue")
    public ModConfigSpec.@UnknownNullability BooleanValue define(String name, boolean defaultValue) {
        add(new BoolConf(name, defaultValue));
        return null;
    }

    /**
     * Defines an integer config entry with a range in the active category.
     *
     * @return {@code null} — acts as a drop-in replacement for {@link ModConfigSpec.Builder#defineInRange(String, int, int, int)}
     */
    @SuppressWarnings("SameReturnValue")
    public ModConfigSpec.@UnknownNullability IntValue defineInRange(String name, int def, int min, int max) {
        add(new IntConf(name, def, min, max));
        return null;
    }

    /**
     * Defines a double config entry with a range in the active category.
     *
     * @return {@code null} — acts as a drop-in replacement for {@link ModConfigSpec.Builder#defineInRange(String, double, double, double)}
     */
    @SuppressWarnings("SameReturnValue")
    public ModConfigSpec.@UnknownNullability DoubleValue defineInRange(String name, double def, double min, double max) {
        add(new DoubleConf(name, def, min, max));
        return null;
    }

    /**
     * Defines a string list config entry in the active category.
     *
     * @return {@code null} — acts as a drop-in replacement for {@link ModConfigSpec.Builder#defineList}
     */
    @SuppressWarnings("SameReturnValue")
    public ModConfigSpec.@UnknownNullability ConfigValue<List<? extends String>> defineList(String name, List<String> defaultValues, Supplier<String> emptyValueSupplier, Predicate<Object> validator) {
        add(new StringList(name, defaultValues, emptyValueSupplier, validator));
        return null;
    }

    private void add(Conf c) {
        if (currentComment != null) {
            c.comment(currentComment);
            currentComment = null;
        }
        activeCategory.put(c.name, c);
    }

    /**
     * Base class for all config entry descriptors.
     */
    public static abstract class Conf {

        protected final String name;

        @Nullable
        private String comment;

        protected Conf(String name) {
            this.name = name;
        }

        public final ModConfigSpec.ConfigValue<?> build(ModConfigSpec.Builder builder) {
            if (comment != null) builder.comment(comment);
            return buildInternal(builder);
        }

        public void comment(String comment) {
            this.comment = comment;
        }

        @Nullable
        public String getComment() {
            return comment;
        }

        protected abstract ModConfigSpec.ConfigValue<?> buildInternal(ModConfigSpec.Builder builder);
    }

    /**
     * Builds a {@link ModConfigSpec.DoubleValue}.
     */
    public static class DoubleConf extends Conf {

        private final double min;
        private final double max;
        private double defaultValue;

        DoubleConf(String name, double defaultValue, double min, double max) {
            super(name);
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;
        }

        public double getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(double defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        protected ModConfigSpec.ConfigValue<?> buildInternal(ModConfigSpec.Builder builder) {
            return builder.defineInRange(name, defaultValue, min, max);
        }
    }

    /**
     * Builds a {@link ModConfigSpec.BooleanValue}.
     */
    public static class BoolConf extends Conf {

        private boolean defaultValue;

        protected BoolConf(String name, boolean defaultValue) {
            super(name);
            this.defaultValue = defaultValue;
        }

        public boolean isDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(boolean defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        protected ModConfigSpec.ConfigValue<?> buildInternal(ModConfigSpec.Builder builder) {
            return builder.define(name, defaultValue);
        }
    }

    /**
     * Builds a {@link ModConfigSpec.IntValue}.
     */
    public static class IntConf extends Conf {

        private final int minValue;
        private final int maxValue;
        private int defaultValue;

        IntConf(String name, int defaultValue, int minValue, int maxValue) {
            super(name);
            this.defaultValue = defaultValue;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public int getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(int defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public ModConfigSpec.ConfigValue<?> buildInternal(ModConfigSpec.Builder builder) {
            return builder.defineInRange(name, defaultValue, minValue, maxValue);
        }
    }

    /**
     * Builds a string list config value via {@link ModConfigSpec.Builder#defineList}.
     */
    public static class StringList extends Conf {

        private final List<String> defaultValue;
        private final Predicate<Object> elementValidator;
        private final Supplier<String> emptyValueSupplier;

        StringList(String name, List<String> defaultValue, Supplier<String> emptyValueSupplier, Predicate<Object> validator) {
            super(name);
            this.defaultValue = new ArrayList<>(defaultValue);
            this.elementValidator = validator;
            this.emptyValueSupplier = emptyValueSupplier;
        }

        public void addValue(String s) {
            if (elementValidator.test(s)) {
                defaultValue.add(s);
            }
        }

        public void removeValue(String s) {
            defaultValue.remove(s);
        }

        @Override
        public ModConfigSpec.ConfigValue<?> buildInternal(ModConfigSpec.Builder builder) {
            return builder.defineList(name, Collections.unmodifiableList(defaultValue), emptyValueSupplier, elementValidator);
        }
    }
}
