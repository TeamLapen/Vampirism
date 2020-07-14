package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.vampirism.player.tasks.TaskBuilder.builder;

@SuppressWarnings("unused")
@ObjectHolder(REFERENCE.MODID)
public class ModTasks {

    public static void registerTasks(IForgeRegistry<Task> registry) {
        registry.register(builder().addRequirement("test1", ModEntities.vampire, 1).setReward(new ItemStack(ModItems.vampire_fang)).build("test1"));
        registry.register(builder().setUnique().addRequirement("test2", ModEntities.vampire, 1).setReward(new ItemStack(ModItems.vampire_fang)).build("test2"));
        registry.register(builder().addRequirement("test3", ModEntities.vampire, 1).setReward(new ItemStack(ModItems.vampire_fang)).build("test3"));
        registry.register(builder().setUnique().addRequirement("test4", ModEntities.vampire, 1).setReward(new ItemStack(ModItems.vampire_fang)).build("test4"));
        registry.register(builder().addRequirement("test5", new ItemStack(ModItems.vampire_fang)).setReward(new ItemStack(ModItems.vampire_fang)).build("test5"));
        registry.register(builder().setUnique().addRequirement("test6", new ItemStack(ModItems.vampire_fang)).setReward(new ItemStack(ModItems.vampire_fang)).build("test6"));
    }
}
