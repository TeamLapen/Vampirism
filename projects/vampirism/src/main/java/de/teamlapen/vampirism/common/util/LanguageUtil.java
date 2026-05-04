package de.teamlapen.vampirism.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Utility class for handling language-related operations in a way that is compatible with both client and server environments.
 * <p>
 * On the server it defaults to "en_us" or "English" as a fallback, as no client language is accessible.
 * </p>
 */
public class LanguageUtil {

    public static @NotNull LanguageInfo getLanguageInfo(String code) {
        return Optional.ofNullable(Minecraft.getInstance().getLanguageManager().getLanguage(code)).orElse(LanguageManager.DEFAULT_LANGUAGE);
    }

    public static @NotNull String getActiveLanguageName() {
        return getLanguageInfo(getActiveLanguageCode()).name();
    }

    public static @NotNull String getActiveLanguageCode() {
        return FMLEnvironment.getDist() == Dist.CLIENT ? Minecraft.getInstance().getLanguageManager().getSelected() : "en_us";
    }
}
