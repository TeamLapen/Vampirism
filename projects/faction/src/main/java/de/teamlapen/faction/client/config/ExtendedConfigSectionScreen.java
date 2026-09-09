package de.teamlapen.faction.client.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * An extended variant of the config screen that allows translating and giving tooltips to enum values that extend {@link TranslatableEnum}
 * <p>
 * The translation keys have the following pattern:<br>
 * "{mod_id}.configuration.{config_name}.{enum_name_lowercase}"
 * <p>
 * And tooltips have the same one but with a ".tooltip" suffix:<br>
 * "{mod_id}.configuration.{config_name}.{enum_name_lowercase}.tooltip"
 */
public class ExtendedConfigSectionScreen extends ConfigurationScreen.ConfigurationSectionScreen {

    public ExtendedConfigSectionScreen(Screen parent, ModConfig.Type type, ModConfig modConfig, Component title, Filter filter) {
        super(parent, type, modConfig, title, filter);
    }

    protected ExtendedConfigSectionScreen(Context parentContext, Screen parent, Map<String, Object> valueSpecs, String key, Set<? extends UnmodifiableConfig.Entry> entrySet, Component title) {
        super(parentContext, parent, valueSpecs, key, entrySet, title);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T extends Enum<T>> @Nullable Element createEnumValue(String key, ModConfigSpec.ValueSpec spec, Supplier<T> source, Consumer<T> target) {
        Element original = super.createEnumValue(key, spec, source, target);
        if (original == null || spec.getClazz() == null || !TranslatableEnum.class.isAssignableFrom(spec.getClazz()) || !(original.option() instanceof OptionInstance<?> option)) {
            return original;
        }

        OptionInstance<T> delegate = (OptionInstance<T>) option;
        return new Element(original.name() == null ? Component.empty() : original.name(), original.tooltip() == null ? Component.empty() : original.tooltip(), new OptionInstance<>(getTranslationKey(key), value -> Tooltip.create(getValueTooltipComponent(key, value)), (_, value) -> getValueTranslationComponent(key, value), delegate.values(), delegate.get(), delegate::set));
    }

    @Override
    @SuppressWarnings("deprecation")
    protected @Nullable Element createSection(String key, UnmodifiableConfig subConfig, UnmodifiableConfig subSection) {
        if (!subConfig.isEmpty()) {
            sectionCache.computeIfAbsent(key, _ -> new ExtendedConfigSectionScreen(context, this, subConfig.valueMap(), key, subSection.entrySet(), Component.translatable(getTranslationKey(key))));
        }

        return super.createSection(key, subConfig, subSection);
    }

    protected Component getValueTranslationComponent(String key, Enum<?> value) {
        String valueKey = getValueTranslationKey(key, value);
        if (I18n.exists(valueKey)) {
            return Component.translatable(valueKey);
        }

        return value instanceof TranslatableEnum translatable ? translatable.getTranslatedName() : Component.literal(value.name());
    }

    protected Component getValueTooltipComponent(String key, Enum<?> value) {
        MutableComponent component = getTooltipComponent(key, null).copy();
        String tooltipKey = getValueTranslationKey(key, value) + ".tooltip";
        if (I18n.exists(tooltipKey)) {
            component.append("\n\n").append(getValueTranslationComponent(key, value).copy().withStyle(ChatFormatting.BOLD)).append("\n").append(Component.translatable(tooltipKey));
        }

        return component;
    }

    protected String getValueTranslationKey(String key, Enum<?> value) {
        return getTranslationKey(key) + "." + value.name().toLowerCase(Locale.ROOT);
    }
}
