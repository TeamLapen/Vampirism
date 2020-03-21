package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

public class ModRegistries {
    public static final ResourceLocation SKILLS_ID = new ResourceLocation("vampirism:skills");
    public static final ResourceLocation ACTIONS_ID = new ResourceLocation("vampirism:actions");
    public static final ResourceLocation ENTITYACTIONS_ID = new ResourceLocation("vampirism:entityactions");
    public static final ResourceLocation MINION_TASKS_ID = new ResourceLocation("vampirism", "miniontasks");
    public static final ResourceLocation TASK_ID = new ResourceLocation("vampirism:tasks");

    public static final IForgeRegistry<ISkill> SKILLS;
    public static final IForgeRegistry<IAction> ACTIONS;
    public static final IForgeRegistry<IEntityAction> ENTITYACTIONS;
    public static final IForgeRegistry<IMinionTask<?>> MINION_TASKS;
    public static final IForgeRegistry<Task> TASKS;

    static {
        SKILLS = makeRegistry(SKILLS_ID, ISkill.class, Integer.MAX_VALUE >> 5);
        ACTIONS = makeRegistry(ACTIONS_ID, IAction.class, Integer.MAX_VALUE >> 5);
        ENTITYACTIONS = makeRegistry(ENTITYACTIONS_ID, IEntityAction.class, Integer.MAX_VALUE >> 5);
        //noinspection unchecked
        MINION_TASKS = ModRegistries.makeRegistry(MINION_TASKS_ID, (Class<IMinionTask<?>>) (Object) IMinionTask.class, Integer.MAX_VALUE >> 5);
        TASKS = makeRegistry(TASK_ID, Task.class, Integer.MAX_VALUE >> 5);
    }

    static void init() {

    }

    private static <T extends IForgeRegistryEntry<T>> IForgeRegistry<T> makeRegistry(ResourceLocation name, Class<T> type, int max) {
        return new RegistryBuilder<T>().setName(name).setType(type).setMaxID(max).create();
    }
}
