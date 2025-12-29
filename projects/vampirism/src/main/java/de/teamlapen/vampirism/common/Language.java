package de.teamlapen.vampirism.common;

import de.teamlapen.vampirism.client.VampirismModClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;

public class Language {

    public static @NotNull String getActiveLanguage() {
        return FMLEnvironment.getDist() == Dist.CLIENT ? VampirismModClient.getActiveLanguage() : "English";
    }

    public static String getActiveLanguageCode() {
        return FMLEnvironment.getDist() == Dist.CLIENT ? VampirismModClient.getActiveLanguageCode() : "en_us";
    }
}
