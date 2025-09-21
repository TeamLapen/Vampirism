package de.teamlapen.lib.common;

import de.teamlapen.lib.client.Client;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;

public class Language {

    public static @NotNull String getActiveLanguage() {
        return FMLEnvironment.dist == Dist.CLIENT ? Client.getActiveLanguage() : "English";
    }

    public static String getActiveLanguageCode() {
        return FMLEnvironment.dist == Dist.CLIENT ? Client.getActiveLanguageCode() : "en_us";
    }
}
