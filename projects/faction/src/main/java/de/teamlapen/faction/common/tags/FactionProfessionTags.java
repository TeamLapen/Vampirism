package de.teamlapen.faction.common.tags;

import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.villager.VillagerProfession;

public class FactionProfessionTags {

    public static final TagKey<VillagerProfession> HAS_FACTION = tag("has_faction");

    private static TagKey<VillagerProfession> tag(String name) {
        return TagKey.create(Registries.VILLAGER_PROFESSION, FIdentifier.mod(name));
    }
}
