package de.teamlapen.faction.data.provider.base;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import de.teamlapen.faction.common.factions.skills.SkillTreeConfiguration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class SkillTreeProvider implements DataProvider {

    protected final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;
    private final String modId;
    private HolderLookup.@UnknownNullability RegistryLookup<ISkillTree> trees = null;
    private HolderLookup.@UnknownNullability RegistryLookup<ISkillNode> nodes = null;

    public SkillTreeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "faction/configured_skill_tree");
        this.lookupProvider = lookupProvider;
        this.modId = modId;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        return this.lookupProvider.thenCompose(provider -> {
            this.trees = provider.lookupOrThrow(FactionRegistries.Keys.SKILL_TREE);
            this.nodes = provider.lookupOrThrow(FactionRegistries.Keys.SKILL_NODE);

            Set<Identifier> set = new HashSet<>();
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

    protected SkillTreeConfiguration.RootBuilder tree(ResourceKey<ISkillTree> tree, ResourceKey<ISkillNode> root) {
        return SkillTreeConfiguration.builder(this.trees.getOrThrow(tree), this.nodes.getOrThrow(root));
    }

    protected SkillTreeConfiguration.RootBuilder.Builder node(ResourceKey<ISkillNode> root) {
        return new SkillTreeConfiguration.RootBuilder.Builder(this.nodes.getOrThrow(root));
    }

    protected abstract void buildSkillTrees(HolderLookup.Provider provider, SkillTreeOutput output);

    @Override
    public String getName() {
        return "Skill tree config";
    }

    protected Identifier modId(String string) {
        return Identifier.fromNamespaceAndPath(this.modId, string);
    }

    public interface SkillTreeOutput {

        Identifier accept(Identifier id, SkillTreeConfiguration skillTree);
    }
}
