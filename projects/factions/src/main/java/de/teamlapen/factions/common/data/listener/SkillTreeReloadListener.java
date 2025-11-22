package de.teamlapen.factions.common.data.listener;

import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.common.skills.ServerSkillTreeData;
import de.teamlapen.factions.common.skills.SkillTreeConfiguration;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class SkillTreeReloadListener extends SimpleJsonResourceReloadListener<SkillTreeConfiguration> {

    public static final ResourceLocation SKILL_TREE_ID = FResourceLocation.mc("skill_tree");
    private static final String DIRECTORY = "vampirism/configured_skill_tree";

    public SkillTreeReloadListener() {
        super(SkillTreeConfiguration.CODEC, FileToIdConverter.json(DIRECTORY));
    }

    @Override
    protected void apply(Map<ResourceLocation, SkillTreeConfiguration> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        ServerSkillTreeData.init(pObject.values().stream().toList());
    }
}
