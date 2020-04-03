package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;
import static de.teamlapen.vampirism.player.tasks.TaskBuilder.builder;

@SuppressWarnings("unused")
@ObjectHolder(REFERENCE.MODID)
public class ModTasks {
    public static final Task vampire_killer = getNull();
    public static final Task hunter_killer = getNull();
    public static final Task hunter_killer2 = getNull();

    public static void registerTasks(IForgeRegistry<Task> registry) { //TODO revert/adjust tasks
        registry.register(builder().withFaction(VReference.VAMPIRE_FACTION).addRequirement(new ItemStack(ModItems.vampire_fang)).addReward(new ItemStack(Items.DIAMOND)).build("vampire_killer"));
        registry.register(builder().withFaction(VReference.VAMPIRE_FACTION).addRequirement(new ItemStack(ModItems.vampire_fang)).addReward(new ItemStack(Items.DIAMOND)).build("vampire_killer2"));
        registry.register(builder().withFaction(VReference.VAMPIRE_FACTION).addRequirement(new ItemStack(ModItems.vampire_fang)).addReward(new ItemStack(Items.DIAMOND)).build("vampire_killer3"));
        registry.register(builder().withFaction(VReference.VAMPIRE_FACTION).addRequirement(new ItemStack(ModItems.vampire_fang)).addReward(new ItemStack(Items.DIAMOND)).build("vampire_killer4"));
        registry.register(builder().withFaction(VReference.VAMPIRE_FACTION).addRequirement(new ItemStack(ModItems.vampire_fang)).addReward(new ItemStack(Items.DIAMOND)).enableDescription().build("vampire_killer5"));
        registry.register(builder().withFaction(VReference.VAMPIRE_FACTION).addRequirement(new ItemStack(ModItems.vampire_fang)).addReward(new ItemStack(Items.DIAMOND)).enableDescription().build("vampire_killer6"));
        registry.register(builder().withFaction(VReference.VAMPIRE_FACTION).addRequirement(new ItemStack(ModItems.vampire_fang)).addReward(new ItemStack(Items.DIAMOND)).enableDescription().build("vampire_killer7"));
        registry.register(builder().withFaction(VReference.VAMPIRE_FACTION).addRequirement(new ItemStack(ModItems.vampire_fang)).addReward(new ItemStack(Items.DIAMOND)).build("vampire_killer8"));
        registry.register(builder().withFaction(VReference.VAMPIRE_FACTION).addRequirement(new ItemStack(ModItems.vampire_fang)).addReward(new ItemStack(Items.DIAMOND)).build("vampire_killer9"));
        registry.register(builder().withFaction(VReference.VAMPIRE_FACTION).addRequirement(new ItemStack(ModItems.vampire_fang)).addReward(new ItemStack(Items.DIAMOND)).build("vampire_killer10"));
        registry.register(builder().withFaction(VReference.VAMPIRE_FACTION).addRequirement(ModEntities.hunter, 1).addReward(new ItemStack(Items.DIAMOND)).build("hunter_killer"));
        registry.register(builder().withFaction(VReference.VAMPIRE_FACTION).addRequirement(Stats.BELL_RING, 3).requireParent(() -> hunter_killer).build("hunter_killer2"));
    }
}
