package de.teamlapen.factions.data.provider.tags;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.skills.ISkillTree;
import de.teamlapen.factions.api.util.REFERENCE;
import de.teamlapen.factions.common.tags.FactionSkillTreeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;

import java.util.concurrent.CompletableFuture;

public class ModSkillTreeTagProvider extends KeyTagProvider<ISkillTree> {

    public ModSkillTreeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, FactionRegistries.Keys.SKILL_TREE, provider, REFERENCE.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(FactionSkillTreeTags.LORD);
        this.tag(FactionSkillTreeTags.LEVEL);
        this.tag(FactionSkillTreeTags.DEFAULT)
                .addTag(FactionSkillTreeTags.LEVEL)
                .addTag(FactionSkillTreeTags.LORD);
    }
}
