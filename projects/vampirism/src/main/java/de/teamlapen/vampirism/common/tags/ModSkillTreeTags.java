package de.teamlapen.vampirism.common.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.tags.TagKey;

public class ModSkillTreeTags {
    public static final TagKey<ISkillTree> HUNTER = tag("faction/hunter");
    public static final TagKey<ISkillTree> VAMPIRE = tag("faction/vampire");

    public static final TagKey<ISkillTree> DRACULA = tag("type/dracula");

    private static TagKey<ISkillTree> tag(String name) {
        return TagKey.create(FactionRegistries.Keys.SKILL_TREE, VIdentifier.mod(name));
    }
}
