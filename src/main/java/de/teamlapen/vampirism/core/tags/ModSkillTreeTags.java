package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public class ModSkillTreeTags {
    public static final TagKey<ISkillTree> HUNTER = tag("faction/hunter");
    public static final TagKey<ISkillTree> VAMPIRE = tag("faction/vampire");
    public static final TagKey<ISkillTree> LEVEL = tag("type/level");
    public static final TagKey<ISkillTree> LORD = tag("type/lord");

    private static @NotNull TagKey<ISkillTree> tag(@NotNull String name) {
        return TagKey.create(VampirismRegistries.Keys.SKILL_TREE, new ResourceLocation(REFERENCE.MODID, name));
    }

}
