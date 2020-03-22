package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistry;

public class ModTasks {
    public static final Task vampire_killer = new Task(VReference.VAMPIRE_FACTION, TaskRequirement.builder().addItemRequirement(new ItemStack(ModItems.vampire_fang)).build(), null);
    public static final Task hunter_killer = new Task(VReference.VAMPIRE_FACTION, TaskRequirement.builder().addEntityRequirement(ModEntities.hunter, 1).build(), null);

    public static void registerTasks(IForgeRegistry<Task> registry) {
        registry.register(vampire_killer.setRegistryName(REFERENCE.MODID, "vampire_killer"));
        registry.register(hunter_killer.setRegistryName(REFERENCE.MODID, "hunter_killer"));
    }
}
