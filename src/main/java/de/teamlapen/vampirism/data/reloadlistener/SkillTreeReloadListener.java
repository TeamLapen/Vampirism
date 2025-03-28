package de.teamlapen.vampirism.data.reloadlistener;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.data.ServerSkillTreeData;
import de.teamlapen.vampirism.entity.player.skills.SkillTreeConfiguration;
import de.teamlapen.vampirism.entity.player.skills.SkillTreeHolder;
import io.netty.handler.codec.DecoderException;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public class SkillTreeReloadListener extends SimpleJsonResourceReloadListener<SkillTreeConfiguration> {
    public static final ResourceLocation SKILL_TREE_ID = VResourceLocation.mc("skill_tree");
    private static final String DIRECTORY = "vampirism/configured_skill_tree";
    private Map<ResourceLocation, SkillTreeConfiguration> configuration = ImmutableMap.of();

    public SkillTreeReloadListener() {
        super(SkillTreeConfiguration.CODEC, FileToIdConverter.json(DIRECTORY));
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, SkillTreeConfiguration> pObject, @NotNull ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        this.configuration = pObject;
        ServerSkillTreeData.init(this.configuration.values().stream().toList());
    }

}
