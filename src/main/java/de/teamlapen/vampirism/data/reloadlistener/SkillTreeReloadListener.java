package de.teamlapen.vampirism.data.reloadlistener;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import de.teamlapen.vampirism.data.ServerSkillTreeData;
import de.teamlapen.vampirism.entity.player.skills.SkillTreeConfiguration;
import de.teamlapen.vampirism.entity.player.skills.SkillTreeHolder;
import io.netty.handler.codec.DecoderException;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public class SkillTreeReloadListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final String DIRECTORY = "vampirism/configured_skill_tree";
    private Map<ResourceLocation, SkillTreeHolder> configuration = ImmutableMap.of();

    public SkillTreeReloadListener() {
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> pObject, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        ImmutableMap.Builder<ResourceLocation, SkillTreeHolder> builder = ImmutableMap.builder();
        RegistryOps<JsonElement> registryOps = RegistryOps.create(JsonOps.INSTANCE, getRegistryLookup());
        for (Map.Entry<ResourceLocation, JsonElement> entry : pObject.entrySet()) {
            builder.put(entry.getKey(), new SkillTreeHolder(entry.getKey(), SkillTreeConfiguration.CODEC.decode(registryOps, entry.getValue()).getOrThrow(DecoderException::new).getFirst()));
        }
        this.configuration = builder.build();
        ServerSkillTreeData.init(this.configuration.values().stream().map(SkillTreeHolder::configuration).toList());
    }

    public Collection<SkillTreeHolder> getTrees() {
        return this.configuration.values();
    }
}
