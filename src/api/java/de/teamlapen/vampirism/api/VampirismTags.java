package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

public class VampirismTags {


    public static class Factions {
        public static final TagKey<IFaction<?>> ALL_FACTIONS = tag("all");
        public static final TagKey<IFaction<?>> IS_HUNTER = tag("is_hunter");
        public static final TagKey<IFaction<?>> IS_VAMPIRE = tag("is_vampire");

        private static @NotNull TagKey<IFaction<?>> tag(@NotNull String name) {
            return TagKey.create(VampirismRegistries.Keys.FACTION, VResourceLocation.mod(name));
        }

    }

    public static class CommonKeys {
        public static final ResourceKey<MobEffect> ACTION_DISABLES = ResourceKey.create(Registries.MOB_EFFECT, VResourceLocation.mod("action_disable"));
    }

}
