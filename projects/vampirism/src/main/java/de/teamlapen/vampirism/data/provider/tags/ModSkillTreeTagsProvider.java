package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.faction.common.tags.FactionSkillTreeTags;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.tags.ModSkillTreeTags;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.entity.player.vampire.skills.VampireSkills;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModSkillTreeTagsProvider extends KeyTagProvider<ISkillTree> {

    public ModSkillTreeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, FactionRegistries.Keys.SKILL_TREE, provider, REFERENCE.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(ModSkillTreeTags.HUNTER).add(HunterSkills.Trees.LEVEL, HunterSkills.Trees.LORD);
        this.tag(ModSkillTreeTags.VAMPIRE).add(VampireSkills.Trees.LEVEL, VampireSkills.Trees.LORD);
        this.tag(FactionSkillTreeTags.LEVEL).add(HunterSkills.Trees.LEVEL, VampireSkills.Trees.LEVEL);
        this.tag(FactionSkillTreeTags.LORD).add(HunterSkills.Trees.LORD, VampireSkills.Trees.LORD);
    }
}
