package de.teamlapen.vampirism.client.config;

import net.neoforged.neoforge.client.gui.ConfigurationScreen.ConfigurationSectionScreen.Context;
import net.neoforged.neoforge.client.gui.ConfigurationScreen.ConfigurationSectionScreen.Element;
import net.neoforged.neoforge.client.gui.ConfigurationScreen.ConfigurationSectionScreen.Filter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ModFilter implements Filter {

    @Override
    public @Nullable Element filterEntry(Context context, String key, Element original) {
        return switch (key) {
            case "integrationsNotifier", "optifineBloodvisionWarning", "actionOrder", "minionTaskOrder", "infoAboutGuideAPI", "oldVampireBiomeGen" -> null;
            default -> original;
        };
    }
}
