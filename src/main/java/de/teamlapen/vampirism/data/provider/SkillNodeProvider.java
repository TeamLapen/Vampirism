package de.teamlapen.vampirism.data.provider;

import com.google.common.collect.Sets;
import de.teamlapen.vampirism.data.recipebuilder.FinishedSkillNode;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class SkillNodeProvider implements DataProvider {

    protected final PackOutput.PathProvider skillNodePathProvider;
    protected final String modId;

    public SkillNodeProvider(PackOutput packOutput, String modId) {
        this.skillNodePathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "vampirismskillnodes");
        this.modId = modId;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        Set<ResourceLocation> set = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();
        this.registerSkillNodes((node) -> {
            if (!set.add(node.getID())) {
                throw new IllegalStateException("Duplicate skill node " + node.getID());
            } else {
                list.add(DataProvider.saveStable(cache, node.serializeSkillNode(), this.skillNodePathProvider.json(node.getID())));
            }
        });
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    @NotNull
    @Override
    public String getName() {
        return "Skillnodes";
    }

    protected abstract void registerSkillNodes(@NotNull Consumer<FinishedSkillNode> consumer);

    protected @NotNull ResourceLocation modId(@NotNull String string) {
        return new ResourceLocation(this.modId, string);
    }

}
