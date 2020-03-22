package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@SuppressWarnings("unused")
@ObjectHolder(REFERENCE.MODID)
public class ModTasks {
    public static final Task vampire_killer = getNull();
    public static final Task hunter_killer = getNull();

    public static void registerTasks(IForgeRegistry<Task> registry) {
        registry.register(Task.builder().withFaction(VReference.VAMPIRE_FACTION).addItemRequirement(new ItemStack(ModItems.vampire_fang)).build().setRegistryName(REFERENCE.MODID, "vampire_killer"));
        registry.register(Task.builder().withFaction(VReference.VAMPIRE_FACTION).addEntityRequirement(ModEntities.hunter, 1).build().setRegistryName(REFERENCE.MODID, "hunter_killer"));
    }
}
