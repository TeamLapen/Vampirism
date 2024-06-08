package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.jetbrains.annotations.NotNull;

public class ModProfessionTags {
    public static final TagKey<VillagerProfession> HAS_FACTION = tag("has_faction");
    public static final TagKey<VillagerProfession> IS_VAMPIRE = tag("has_faction/vampire");
    public static final TagKey<VillagerProfession> IS_HUNTER = tag("has_faction/hunter");

    private static @NotNull TagKey<VillagerProfession> tag(@NotNull String name) {
        return TagKey.create(Registries.VILLAGER_PROFESSION, new ResourceLocation(REFERENCE.MODID, name));
    }
}
