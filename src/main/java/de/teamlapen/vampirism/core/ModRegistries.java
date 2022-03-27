package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

public class ModRegistries {
    public static final ResourceLocation SKILLS_ID = new ResourceLocation("vampirism:skills");
    public static final ResourceLocation ACTIONS_ID = new ResourceLocation("vampirism:actions");
    public static final ResourceLocation ENTITYACTIONS_ID = new ResourceLocation("vampirism:entityactions");
    public static final ResourceLocation MINION_TASKS_ID = new ResourceLocation("vampirism", "miniontasks");
    public static final ResourceLocation TASK_ID = new ResourceLocation("vampirism:tasks");
    public static final ResourceLocation REFINEMENT_ID = new ResourceLocation("vampirism:refinement");
    public static final ResourceLocation REFINEMENT_SET_ID = new ResourceLocation("vampirism:refinement_set");

    public static IForgeRegistry<ISkill<?>> SKILLS;
    public static IForgeRegistry<IAction<?>> ACTIONS;
    public static IForgeRegistry<IEntityAction> ENTITYACTIONS;
    public static IForgeRegistry<IMinionTask<?, ?>> MINION_TASKS;
    public static IForgeRegistry<Task> TASKS;
    public static IForgeRegistry<IRefinement> REFINEMENTS;
    public static IForgeRegistry<IRefinementSet> REFINEMENT_SETS;


    static void init(NewRegistryEvent event) {
        //noinspection unchecked
        event.create(makeRegistry(SKILLS_ID, (Class<ISkill<?>>) (Object)ISkill.class, Integer.MAX_VALUE >> 5), r -> SKILLS = r);
        //noinspection unchecked
        event.create(makeRegistry(ACTIONS_ID, (Class<IAction<?>>) (Object)IAction.class, Integer.MAX_VALUE >> 5), r -> ACTIONS = r);
        event.create(makeRegistry(ENTITYACTIONS_ID, IEntityAction.class, Integer.MAX_VALUE >> 5), r -> ENTITYACTIONS = r);
        //noinspection unchecked
        event.create(ModRegistries.makeRegistry(MINION_TASKS_ID, (Class<IMinionTask<?, ?>>) (Object) IMinionTask.class, Integer.MAX_VALUE >> 5), r -> MINION_TASKS =r);
        event.create(makeRegistry(TASK_ID, Task.class, Integer.MAX_VALUE >> 5), r -> TASKS =r );
        event.create(makeRegistry(REFINEMENT_ID, IRefinement.class, Integer.MAX_VALUE >> 5), r -> REFINEMENTS = r);
        event.create( makeRegistry(REFINEMENT_SET_ID, IRefinementSet.class, Integer.MAX_VALUE >> 5), r -> REFINEMENT_SETS = r);
    }

    private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(ResourceLocation name, Class<T> type, int max) {
        return new RegistryBuilder<T>().setName(name).setType(type).setMaxID(max);
    }
}
