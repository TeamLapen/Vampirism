package de.teamlapen.factions.common.tags;

import de.teamlapen.factions.api.util.FResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.VillagerProfession;

public class FactionProfessionTags {

    public static final TagKey<VillagerProfession> HAS_FACTION = tag("has_faction");

    private static TagKey<VillagerProfession> tag(String name) {
        return TagKey.create(Registries.VILLAGER_PROFESSION, FResourceLocation.mod(name));
    }
}
