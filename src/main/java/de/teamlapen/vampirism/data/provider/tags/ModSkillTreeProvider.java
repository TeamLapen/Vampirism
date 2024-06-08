package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.core.tags.ModSkillTreeTags;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModSkillTreeProvider extends TagsProvider<ISkillTree> {

    protected ModSkillTreeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, VampirismRegistries.Keys.SKILL_TREE, provider, REFERENCE.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(ModSkillTreeTags.HUNTER).add(HunterSkills.Trees.LEVEL, HunterSkills.Trees.LORD);
        this.tag(ModSkillTreeTags.VAMPIRE).add(VampireSkills.Trees.LEVEL, VampireSkills.Trees.LORD);
        this.tag(ModSkillTreeTags.LEVEL).add(HunterSkills.Trees.LEVEL, VampireSkills.Trees.LEVEL);
        this.tag(ModSkillTreeTags.LORD).add(HunterSkills.Trees.LORD, VampireSkills.Trees.LORD);
    }
}
