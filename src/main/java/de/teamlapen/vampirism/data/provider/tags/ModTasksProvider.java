package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.ModTasks;
import de.teamlapen.vampirism.core.tags.ModTaskTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModTasksProvider extends TagsProvider<Task> {

    protected ModTasksProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, VampirismRegistries.Keys.TASK, provider, REFERENCE.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(ModTaskTags.HAS_FACTION).addTags(ModTaskTags.IS_VAMPIRE, ModTaskTags.IS_HUNTER);
        this.tag(ModTaskTags.IS_VAMPIRE).add(
                ModTasks.FEEDING_ADAPTER,
                ModTasks.VAMPIRE_LORD_1,
                ModTasks.VAMPIRE_LORD_2,
                ModTasks.VAMPIRE_LORD_3,
                ModTasks.VAMPIRE_LORD_4,
                ModTasks.VAMPIRE_LORD_5,
                ModTasks.FIRE_RESISTANCE_1,
                ModTasks.FIRE_RESISTANCE_2,
                ModTasks.VAMPIRE_MINION_BINDING,
                ModTasks.VAMPIRE_MINION_UPGRADE_SIMPLE,
                ModTasks.VAMPIRE_MINION_UPGRADE_ENHANCED,
                ModTasks.VAMPIRE_MINION_UPGRADE_SPECIAL,
                ModTasks.V_INFECT_1,
                ModTasks.V_INFECT_2,
                ModTasks.V_INFECT_3,
                ModTasks.V_CAPTURE_1,
                ModTasks.V_CAPTURE_2,
                ModTasks.V_KILL_1,
                ModTasks.V_KILL_2,
                ModTasks.RANDOM_REFINEMENT_1,
                ModTasks.RANDOM_REFINEMENT_2,
                ModTasks.RANDOM_REFINEMENT_3,
                ModTasks.RANDOM_RARE_REFINEMENT
        );
        this.tag(ModTaskTags.IS_HUNTER).add(
                ModTasks.HUNTER_LORD_1,
                ModTasks.HUNTER_LORD_2,
                ModTasks.HUNTER_LORD_3,
                ModTasks.HUNTER_LORD_4,
                ModTasks.HUNTER_LORD_5,
                ModTasks.HUNTER_MINION_EQUIPMENT,
                ModTasks.HUNTER_MINION_UPGRADE_SIMPLE,
                ModTasks.HUNTER_MINION_UPGRADE_ENHANCED,
                ModTasks.HUNTER_MINION_UPGRADE_SPECIAL,
                ModTasks.H_KILL_1,
                ModTasks.H_KILL_2,
                ModTasks.H_CAPTURE_1
        );
        this.tag(ModTaskTags.AWARDS_LORD_LEVEL).add(
                ModTasks.HUNTER_LORD_1,
                ModTasks.HUNTER_LORD_2,
                ModTasks.HUNTER_LORD_3,
                ModTasks.HUNTER_LORD_4,
                ModTasks.HUNTER_LORD_5,
                ModTasks.VAMPIRE_LORD_1,
                ModTasks.VAMPIRE_LORD_2,
                ModTasks.VAMPIRE_LORD_3,
                ModTasks.VAMPIRE_LORD_4,
                ModTasks.VAMPIRE_LORD_5
        );
        this.tag(ModTaskTags.IS_UNIQUE)
                .addTag(ModTaskTags.AWARDS_LORD_LEVEL)
        ;
    }
}
