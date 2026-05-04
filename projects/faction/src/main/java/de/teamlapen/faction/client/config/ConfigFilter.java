package de.teamlapen.faction.client.config;

import net.neoforged.neoforge.client.gui.ConfigurationScreen.ConfigurationSectionScreen.Context;
import net.neoforged.neoforge.client.gui.ConfigurationScreen.ConfigurationSectionScreen.Element;
import net.neoforged.neoforge.client.gui.ConfigurationScreen.ConfigurationSectionScreen.Filter;
import org.jetbrains.annotations.Nullable;

public class ConfigFilter implements Filter {

    @Override
    public @Nullable Element filterEntry(Context context, String key, Element original) {
        return switch (key) {
            case "actionOrder", "minionTaskOrder" -> null;
            default -> original;
        };
    }
}
