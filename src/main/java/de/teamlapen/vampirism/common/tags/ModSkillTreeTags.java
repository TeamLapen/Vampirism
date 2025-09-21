package de.teamlapen.vampirism.common.tags;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.tags.TagKey;

public class ModSkillTreeTags {
    public static final TagKey<ISkillTree> HUNTER = tag("faction/hunter");
    public static final TagKey<ISkillTree> VAMPIRE = tag("faction/vampire");
    public static final TagKey<ISkillTree> LEVEL = tag("type/level");
    public static final TagKey<ISkillTree> LORD = tag("type/lord");
    public static final TagKey<ISkillTree> DEFAULT = tag("default");

    private static TagKey<ISkillTree> tag(String name) {
        return TagKey.create(VampirismRegistries.Keys.SKILL_TREE, VResourceLocation.mod(name));
    }
}
