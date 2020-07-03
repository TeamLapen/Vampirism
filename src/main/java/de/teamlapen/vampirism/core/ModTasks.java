package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;
import static de.teamlapen.vampirism.player.tasks.TaskBuilder.builder;

@SuppressWarnings("unused")
@ObjectHolder(REFERENCE.MODID)
public class ModTasks {

    public static void registerTasks(IForgeRegistry<Task> registry) {
        registry.register(builder().setReward(new ItemStack(ModItems.item_med_chair)).setRequirement(new ItemStack(ModItems.human_heart,3)).build("test"));
    }
}
