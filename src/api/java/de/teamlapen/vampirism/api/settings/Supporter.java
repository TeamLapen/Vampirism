package de.teamlapen.vampirism.api.settings;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Supporter information for advanced hunters / vampires
 *
 * @param faction faction for this supporter
 * @param name display name that will be shown in game
 * @param texture minecraft username to download the skin
 * @param bookId if set it will be used to drop a specific book on loot drops
 * @param appearance appearance options for the renderer
 */
public record Supporter(@NotNull ResourceLocation faction, @NotNull String name, @NotNull String texture, @Nullable String bookId, @NotNull Map<String, String> appearance) {

}
