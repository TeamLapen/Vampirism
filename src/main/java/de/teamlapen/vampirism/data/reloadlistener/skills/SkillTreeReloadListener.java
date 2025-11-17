package de.teamlapen.vampirism.data.reloadlistener.skills;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.data.ServerSkillTreeData;
import de.teamlapen.vampirism.common.entity.player.skills.SkillTreeConfiguration;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SkillTreeReloadListener extends SimpleJsonResourceReloadListener<SkillTreeConfiguration> {

    public static final ResourceLocation SKILL_TREE_ID = VResourceLocation.mc("skill_tree");
    private static final String DIRECTORY = "vampirism/configured_skill_tree";

    public SkillTreeReloadListener() {
        super(SkillTreeConfiguration.CODEC, FileToIdConverter.json(DIRECTORY));
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, SkillTreeConfiguration> pObject, @NotNull ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        ServerSkillTreeData.init(pObject.values().stream().toList());
    }
}
