package de.teamlapen.vampirism.api.settings;

import de.teamlapen.vampirism.api.VReference;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Supporter(@NotNull ResourceLocation faction, @NotNull String name, @NotNull String texture, int typeId, @Nullable String bookId) {

    public record Old(@NotNull String name, String texture, int type, int status) {
        public Supporter toNew(ResourceLocation faction) {
            return new Supporter(faction, name, texture, type, null);
        }
    }
    public record OldList(@NotNull String comment, @NotNull List<Old> vampires, @NotNull List<Old> hunters) {
        public Collection<Supporter> toNew() {
            return Stream.concat(vampires.stream().map(s -> s.toNew(VReference.VAMPIRE_FACTION.getID())), hunters.stream().map(s -> s.toNew(VReference.HUNTER_FACTION.getID()))).collect(Collectors.toList());
        }
    }
}
