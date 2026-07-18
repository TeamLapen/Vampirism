package de.teamlapen.faction.data.listener;

import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.factions.skills.ServerSkillTreeData;
import de.teamlapen.faction.common.factions.skills.SkillTreeConfiguration;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class SkillTreeReloadListener extends SimpleJsonResourceReloadListener<SkillTreeConfiguration> {

    public static final Identifier SKILL_TREE_ID = FIdentifier.mc("skill_tree");
    private static final String DIRECTORY = REFERENCE.MOD_ID + "/configured_skill_tree";

    public SkillTreeReloadListener() {
        super(SkillTreeConfiguration.CODEC, FileToIdConverter.json(DIRECTORY));
    }

    @Override
    protected void apply(Map<Identifier, SkillTreeConfiguration> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        ServerSkillTreeData.init(pObject.values().stream().toList());
    }
}
