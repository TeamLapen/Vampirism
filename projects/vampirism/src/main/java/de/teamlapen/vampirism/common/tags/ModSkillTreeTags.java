package de.teamlapen.vampirism.common.tags;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.skills.ISkillTree;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.tags.TagKey;

public class ModSkillTreeTags {
    public static final TagKey<ISkillTree> HUNTER = tag("faction/hunter");
    public static final TagKey<ISkillTree> VAMPIRE = tag("faction/vampire");

    private static TagKey<ISkillTree> tag(String name) {
        return TagKey.create(FactionRegistries.Keys.SKILL_TREE, VResourceLocation.mod(name));
    }
}
