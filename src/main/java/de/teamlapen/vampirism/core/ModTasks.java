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
        registry.register(builder().addRequirement("1", new ItemStack(ModItems.vampire_fang)).setReward(new ItemStack(ModItems.human_heart)).build("1"));
        registry.register(builder().addRequirement("2", new ItemStack(ModItems.vampire_fang)).setReward(new ItemStack(ModItems.human_heart)).build("2"));
        registry.register(builder().addRequirement("3", new ItemStack(ModItems.vampire_fang)).setReward(new ItemStack(ModItems.human_heart)).build("3"));
        registry.register(builder().addRequirement("4", new ItemStack(ModItems.vampire_fang)).setReward(new ItemStack(ModItems.human_heart)).build("4"));
        registry.register(builder().addRequirement("5", new ItemStack(ModItems.vampire_fang)).setReward(new ItemStack(ModItems.human_heart)).build("5"));
        registry.register(builder().addRequirement("6", new ItemStack(ModItems.vampire_fang)).setReward(new ItemStack(ModItems.human_heart)).build("6"));
        registry.register(builder().addRequirement("7", new ItemStack(ModItems.vampire_fang)).setReward(new ItemStack(ModItems.human_heart)).build("7"));
        registry.register(builder().addRequirement("8", new ItemStack(ModItems.vampire_fang)).setReward(new ItemStack(ModItems.human_heart)).build("8"));
        registry.register(builder().addRequirement("9", new ItemStack(ModItems.vampire_fang)).setReward(new ItemStack(ModItems.human_heart)).build("9"));
        registry.register(builder().addRequirement("10", new ItemStack(ModItems.vampire_fang)).setReward(new ItemStack(ModItems.human_heart)).build("10"));
        registry.register(builder().addRequirement("11", ModEntities.vampire, 3).setReward(new ItemStack(ModItems.human_heart)).build("11"));
        registry.register(builder().setUnique().addRequirement("12", ModEntities.hunter, 3).setReward(new ItemStack(ModItems.human_heart)).build("12"));
    }
}
