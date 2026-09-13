package de.teamlapen.faction.common.config;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.TranslatableEnum;

import java.util.Locale;

public interface TranslatableConfigEnum extends TranslatableEnum {

    String configTranslationKey();

    @Override
    default Component getTranslatedName() {
        return Component.translatable(configTranslationKey() + "." + ((Enum<?>) this).name().toLowerCase(Locale.ROOT));
    }
}
