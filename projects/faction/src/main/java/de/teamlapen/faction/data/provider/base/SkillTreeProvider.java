package de.teamlapen.faction.data.provider.base;

import de.teamlapen.faction.api.factions.skills.ISkillNode;
import de.teamlapen.faction.common.factions.skills.SkillTreeConfiguration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

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
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "faction/configured_skill_tree");
        this.lookupProvider = lookupProvider;
        this.modId = modId;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        return this.lookupProvider.thenCompose(provider -> {
            Set<Identifier> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
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

    protected SkillTreeConfiguration.SkillTreeNodeConfiguration node(HolderLookup.RegistryLookup<ISkillNode> nodes, ResourceKey<ISkillNode> key, SkillTreeConfiguration.SkillTreeNodeConfiguration... children) {
        return new SkillTreeConfiguration.SkillTreeNodeConfiguration(nodes.getOrThrow(key), children);
    }

    @SafeVarargs
    protected final SkillTreeConfiguration.SkillTreeNodeConfiguration chain(HolderLookup.RegistryLookup<ISkillNode> nodes, ResourceKey<ISkillNode>... keys) {
        SkillTreeConfiguration.SkillTreeNodeConfiguration current = node(nodes, keys[keys.length - 1]);
        for (int i = keys.length - 2; i >= 0; i--) {
            current = node(nodes, keys[i], current);
        }
        return current;
    }

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
