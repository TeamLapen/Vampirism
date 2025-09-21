package de.teamlapen.vampirism.common.tags;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.VillagerProfession;

public class ModProfessionTags {
    public static final TagKey<VillagerProfession> HAS_FACTION = tag("has_faction");
    public static final TagKey<VillagerProfession> IS_VAMPIRE = tag("has_faction/vampire");
    public static final TagKey<VillagerProfession> IS_HUNTER = tag("has_faction/hunter");

    private static TagKey<VillagerProfession> tag(String name) {
        return TagKey.create(Registries.VILLAGER_PROFESSION, VResourceLocation.mod(name));
    }
}
