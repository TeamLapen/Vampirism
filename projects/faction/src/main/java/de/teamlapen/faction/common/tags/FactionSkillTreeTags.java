package de.teamlapen.faction.common.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.tags.TagKey;

public class FactionSkillTreeTags {
    public static final TagKey<ISkillTree> LEVEL = tag("type/level");
    public static final TagKey<ISkillTree> LORD = tag("type/lord");
    public static final TagKey<ISkillTree> DEFAULT = tag("default");

    private static TagKey<ISkillTree> tag(String name) {
        return TagKey.create(FactionRegistries.Keys.SKILL_TREE, FIdentifier.mod(name));
    }
}
