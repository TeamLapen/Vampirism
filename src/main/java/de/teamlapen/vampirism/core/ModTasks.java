package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@SuppressWarnings("unused")
@ObjectHolder(REFERENCE.MODID)
public class ModTasks {
    public static final Task vampire_killer = getNull();
    public static final Task hunter_killer = getNull();
    public static final Task hunter_killer2 = getNull();

    public static void registerTasks(IForgeRegistry<Task> registry) { //TODO revert/adjust tasks
        registry.register(Task.builder().withFaction(VReference.VAMPIRE_FACTION).addItemRequirement(new ItemStack(ModItems.vampire_fang)).build().setRegistryName(REFERENCE.MODID, "vampire_killer"));
        registry.register(Task.builder().withFaction(VReference.VAMPIRE_FACTION).addItemRequirement(new ItemStack(ModItems.vampire_fang)).build().setRegistryName(REFERENCE.MODID, "vampire_killer2"));
        registry.register(Task.builder().withFaction(VReference.VAMPIRE_FACTION).addItemRequirement(new ItemStack(ModItems.vampire_fang)).build().setRegistryName(REFERENCE.MODID, "vampire_killer3"));
        registry.register(Task.builder().withFaction(VReference.VAMPIRE_FACTION).addItemRequirement(new ItemStack(ModItems.vampire_fang)).build().setRegistryName(REFERENCE.MODID, "vampire_killer4"));
        registry.register(Task.builder().withFaction(VReference.VAMPIRE_FACTION).addItemRequirement(new ItemStack(ModItems.vampire_fang)).build().setRegistryName(REFERENCE.MODID, "vampire_killer5"));
        registry.register(Task.builder().withFaction(VReference.VAMPIRE_FACTION).addItemRequirement(new ItemStack(ModItems.vampire_fang)).build().setRegistryName(REFERENCE.MODID, "vampire_killer6"));
        registry.register(Task.builder().withFaction(VReference.VAMPIRE_FACTION).addItemRequirement(new ItemStack(ModItems.vampire_fang)).build().setRegistryName(REFERENCE.MODID, "vampire_killer7"));
        registry.register(Task.builder().withFaction(VReference.VAMPIRE_FACTION).addItemRequirement(new ItemStack(ModItems.vampire_fang)).build().setRegistryName(REFERENCE.MODID, "vampire_killer8"));
        registry.register(Task.builder().withFaction(VReference.VAMPIRE_FACTION).addItemRequirement(new ItemStack(ModItems.vampire_fang)).build().setRegistryName(REFERENCE.MODID, "vampire_killer9"));
        registry.register(Task.builder().withFaction(VReference.VAMPIRE_FACTION).addItemRequirement(new ItemStack(ModItems.vampire_fang)).build().setRegistryName(REFERENCE.MODID, "vampire_killer10"));
        registry.register(Task.builder().withFaction(VReference.VAMPIRE_FACTION).addEntityRequirement(ModEntities.hunter, 1).build().setRegistryName(REFERENCE.MODID, "hunter_killer"));
        registry.register(Task.builder().withFaction(VReference.VAMPIRE_FACTION).addStatRequirement(Stats.ENTITY_KILLED, ModEntities.hunter, 3).build().setRegistryName(REFERENCE.MODID, "hunter_killer2"));
    }
}
