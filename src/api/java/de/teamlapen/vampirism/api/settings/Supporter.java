package de.teamlapen.vampirism.api.settings;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Supporter(@NotNull ResourceLocation faction, @NotNull String name, @NotNull String texture, int typeId, @Nullable String bookId) {
}
