package de.teamlapen.factions.data.provider.base;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import de.teamlapen.factions.common.factions.skills.SkillTreeConfiguration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class SkillTreeProvider implements DataProvider {

    protected final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;
    private final String modId;

    public SkillTreeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "factions/configured_skill_tree");
        this.lookupProvider = lookupProvider;
        this.modId = modId;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        return this.lookupProvider.thenCompose(provider -> {
            Set<ResourceLocation> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, provider);
            this.buildSkillTrees(provider, (id, skillTree) -> {
                if (!set.add(id)) {
                    throw new IllegalStateException("Duplicate skill tree " + id);
                } else {
                    list.add(DataProvider.saveStable(pOutput, provider, SkillTreeConfiguration.CODEC, skillTree, pathProvider.json(id)));
                }
                return id;
            });
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    protected abstract void buildSkillTrees(HolderLookup.Provider provider, SkillTreeOutput output);

    @Override
    public String getName() {
        return "Skill tree config";
    }

    protected ResourceLocation modId(String string) {
        return ResourceLocation.fromNamespaceAndPath(this.modId, string);
    }

    public interface SkillTreeOutput {

        ResourceLocation accept(ResourceLocation id, SkillTreeConfiguration skillTree);
    }
}
