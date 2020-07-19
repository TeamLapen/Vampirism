package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.player.tasks.TaskBuilder;
import de.teamlapen.vampirism.player.tasks.unlock.LvlUnlocker;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@SuppressWarnings("unused")
@ObjectHolder(REFERENCE.MODID)
public class ModTasks {

    public static final Task feeding_adapter = getNull();

    public static void registerTasks(IForgeRegistry<Task> registry) {
        registry.register(TaskBuilder.builder().withFaction(VReference.VAMPIRE_FACTION).unlockedBy(new LvlUnlocker(4)).addRequirement("advanced_hunter", ModEntities.advanced_hunter, 10).addRequirement("item", new ItemStack(Items.GOLD_INGOT, 5)).setReward(new ItemStack(ModItems.feeding_adapter)).build("feeding_adapter"));
    }
}
