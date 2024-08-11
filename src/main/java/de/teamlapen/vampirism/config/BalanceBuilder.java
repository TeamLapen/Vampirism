package de.teamlapen.vampirism.config;

import de.teamlapen.vampirism.VampirismMod;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Intermediate builder stage for Vampirism's balance configuration ({@link BalanceConfig})
 * The balance configuration is statically built with this builder.
 * Then, during mod construct, addon mods can register modifications to this configuration setup (the default values and comments in particular)
 * Finally, during RegistryEvent<Block>, the configuration is transferred to the Forge system respecting the registered modifications and thereby finalized.
 */
public class BalanceBuilder {
    private static final Logger LOGGER = LogManager.getLogger();

    private static void setVal(BalanceConfig conf, @NotNull String name, Object value) {
        try {
            Field f = BalanceConfig.class.getDeclaredField(name);
            f.setAccessible(true);
            f.set(conf, value);
        } catch (NoSuchFieldException e) {
            LOGGER.error("Failed to set Balance value as expected", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("Illegal access when trying to set Balance value as expected", e);
        }
    }

    private final @NotNull Map<String, Map<String, Conf>> categoryConfigMap;
    private final @NotNull Map<String, String> categoryPrefixMap;
    /**
     * Holds (potentially concurrently) added config modifications that are applied during build
     */
    private final ConcurrentHashMap<String, Consumer<? extends Conf>> balanceModifications = new ConcurrentHashMap<>();
    /**
     * Holds the latest created category config map
     */
    private Map<String, BalanceBuilder.Conf> activeCategory;
    /**
     * The latest added comment. Is added to the next config option and reset afterwards
     */
    @Nullable
    private String currentComment;

    BalanceBuilder() {
        categoryConfigMap = new HashMap<>();
        categoryPrefixMap = new HashMap<>();
    }

    public void addBalanceModifier(@NotNull String key, @NotNull Consumer<? extends Conf> modifier) {
        if (balanceModifications.put(key, modifier) != null) {
            if (VampirismMod.inDev) LOGGER.warn("Overriding existing config modifier for {}", key);
        }
    }

    /**
     * Build the registered configuration considering the modifiers using the give Forge level and inject the created {@link ModConfigSpec.ConfigValue} into the given BalanceConfig using reflection
     */
    public void build(BalanceConfig conf, ModConfigSpec.@NotNull Builder builder) {
        if (!balanceModifications.isEmpty()) {
            LOGGER.info("Building balance configuration with {} modifications", balanceModifications.size());
        }
        for (Map.Entry<String, Map<String, Conf>> stringMapEntry : categoryConfigMap.entrySet()) {
            String category = stringMapEntry.getKey();
            String catPrefix = categoryPrefixMap.getOrDefault(category, category);
            builder.push(category);
            for (Map.Entry<String, Conf> stringConfEntry : stringMapEntry.getValue().entrySet()) {
                String fullName;
                String name = stringConfEntry.getKey();
                if (catPrefix.isEmpty()) {
                    fullName = name;
                } else {
                    fullName = catPrefix + name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);

                }
                Conf c = stringConfEntry.getValue();
                @SuppressWarnings("unchecked")
                Consumer<Conf> modifier = (Consumer<Conf>) balanceModifications.get(fullName);
                if (modifier != null) {
                    try {
                        modifier.accept(c);
                    } catch (Exception e) {
                        LOGGER.error("Failed to apply balance config modifier for " + fullName, e);
                    }
                }
                ModConfigSpec.ConfigValue<?> val = c.build(builder);
                setVal(conf, fullName, val);
            }
            builder.pop();
        }
        categoryConfigMap.clear();
        balanceModifications.clear();
        categoryPrefixMap.clear();
        currentComment = null;
    }

    public @NotNull BalanceBuilder category(String name, String prefix) {
        activeCategory = new HashMap<>();
        categoryConfigMap.put(name, activeCategory);
        categoryPrefixMap.put(name, prefix);
        return this;
    }

    /**
     * Checks if all fields in {@link BalanceConfig} have been set. Throws otherwise
     *
     * @throws IllegalStateException If an unset field is found
     */
    public void checkFields(BalanceConfig config) throws IllegalStateException {
        try {
            for (Field declaredField : BalanceConfig.class.getDeclaredFields()) {
                declaredField.setAccessible(true);
                if (declaredField.get(config) == null) {
                    throw new IllegalStateException("Config value " + declaredField.getName() + " is not set");
                }
            }
        } catch (IllegalAccessException e) {
            LOGGER.error("Illegal access when checking balance fields", e);
        }
    }

    /**
     * Add a comment to the next config entry
     */
    public @NotNull BalanceBuilder comment(String comment) {
        this.currentComment = comment;
        return this;
    }

    public @NotNull BalanceBuilder config(BalanceBuilder.@NotNull Conf value) {
        activeCategory.put(value.name, value);
        return this;
    }

    /**
     * @return null, for drop-in replacement
     */
    @SuppressWarnings("SameReturnValue")
    public ModConfigSpec.@UnknownNullability BooleanValue define(String name, boolean defaultValue) {
        add(new BalanceBuilder.BoolConf(name, defaultValue));
        return null;
    }

    /**
     * @return null, for drop-in replacement
     */
    @SuppressWarnings("SameReturnValue")
    public ModConfigSpec.@UnknownNullability IntValue defineInRange(String name, int def, int min, int max) {
        add(new BalanceBuilder.IntConf(name, def, min, max));
        return null;
    }

    /**
     * @return null, for drop-in replacement
     */
    @SuppressWarnings("SameReturnValue")
    public ModConfigSpec.@UnknownNullability  DoubleValue defineInRange(String name, double def, double min, double max) {
        add(new BalanceBuilder.DoubleConf(name, def, min, max));
        return null;
    }

    /**
     * @return null, for drop-in replacement
     */
    @SuppressWarnings("SameReturnValue")
    public ModConfigSpec.@UnknownNullability ConfigValue<List<? extends String>> defineList(String name, @NotNull List<String> defaultValues, Supplier<String> emptyValueSupplier, Predicate<Object> validator) {
        add(new BalanceBuilder.StringList(name, defaultValues, emptyValueSupplier, validator));
        return null;
    }

    private void add(@NotNull Conf c) {
        if (currentComment != null) {
            c.comment(currentComment);
            currentComment = null;
        }
        this.activeCategory.put(c.name, c);
    }

    public static abstract class Conf {
        protected final String name;
        @Nullable
        private String comment = null;

        protected Conf(String name) {
            this.name = name;
        }

        public final ModConfigSpec.ConfigValue<?> build(ModConfigSpec.@NotNull Builder builder) {
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
     * Builds a {@link net.neoforged.neoforge.common.ModConfigSpec.DoubleValue}
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
        protected ModConfigSpec.ConfigValue<?> buildInternal(ModConfigSpec.@NotNull Builder builder) {
            return builder.defineInRange(name, defaultValue, min, max);
        }
    }

    /**
     * Builds a {@link ModConfigSpec.BooleanValue}
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
        protected ModConfigSpec.ConfigValue<?> buildInternal(ModConfigSpec.@NotNull Builder builder) {
            return builder.define(name, defaultValue);
        }
    }

    /**
     * Builds a {@link net.neoforged.neoforge.common.ModConfigSpec.IntValue}
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

        @Override
        public ModConfigSpec.ConfigValue<?> buildInternal(ModConfigSpec.@NotNull Builder builder) {
            return builder.defineInRange(name, defaultValue, minValue, maxValue);
        }

        public int getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(int defaultValue) {
            this.defaultValue = defaultValue;
        }
    }

    /**
     * Builds a {@link net.neoforged.neoforge.common.ModConfigSpec.IntValue}
     */
    public static class StringList extends Conf {
        private final @NotNull List<String> defaultValue;
        private final Predicate<Object> elementValidator;
        private final Supplier<String> emptyValueSupplier;

        StringList(String name, @NotNull List<String> defaultValue, Supplier<String> emptyValueSupplier, Predicate<Object> validator) {
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

        @Override
        public ModConfigSpec.ConfigValue<?> buildInternal(ModConfigSpec.@NotNull Builder builder) {
            return builder.defineList(name, Collections.unmodifiableList(defaultValue), emptyValueSupplier, elementValidator);
        }

        public void removeValue(String s) {
            defaultValue.remove(s);
        }


    }
}
