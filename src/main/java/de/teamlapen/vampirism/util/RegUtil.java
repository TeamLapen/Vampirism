package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Supplier;

public class RegUtil {

    public static ResourceLocation id(IAction<?> action) {
        return ModRegistries.ACTIONS.get().getKey(action);
    }

    public static ResourceLocation id(ISkill<?> skill) {
        return ModRegistries.SKILLS.get().getKey(skill);
    }

    public static ResourceLocation id(IMinionTask<?, ?> minionTask) {
        return ModRegistries.MINION_TASKS.get().getKey(minionTask);
    }

    public static ResourceLocation id(IEntityAction entityAction) {
        return ModRegistries.ENTITY_ACTIONS.get().getKey(entityAction);
    }

    public static ResourceLocation id(Task skill) {
        return ModRegistries.TASKS.get().getKey(skill);
    }

    public static ResourceLocation id(IRefinement refinement) {
        return ModRegistries.REFINEMENTS.get().getKey(refinement);
    }

    public static ResourceLocation id(IRefinementSet refinementSet) {
        return ModRegistries.REFINEMENT_SETS.get().getKey(refinementSet);
    }

    public static ResourceLocation id(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }

    public static ResourceLocation id(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    public static ResourceLocation id(Fluid block) {
        return ForgeRegistries.FLUIDS.getKey(block);
    }

    public static ResourceLocation id(EntityType<?> type) {
        return ForgeRegistries.ENTITY_TYPES.getKey(type);
    }

    public static ResourceLocation id(Biome type) {
        return ForgeRegistries.BIOMES.getKey(type);
    }

    public static ResourceLocation id(Enchantment type) {
        return ForgeRegistries.ENCHANTMENTS.getKey(type);
    }

    public static ResourceLocation id(VillagerProfession profession) {
        return ForgeRegistries.VILLAGER_PROFESSIONS.getKey(profession);
    }

    public static ResourceLocation id(IOil oil) {
        return ModRegistries.OILS.get().getKey(oil);
    }


    public static boolean has(IAction<?> action) {
        return ModRegistries.ACTIONS.get().containsValue(action);
    }

    public static boolean has(ISkill<?> skill) {
        return ModRegistries.SKILLS.get().containsValue(skill);
    }

    public static boolean has(IMinionTask<?, ?> minionTask) {
        return ModRegistries.MINION_TASKS.get().containsValue(minionTask);
    }

    public static boolean has(IEntityAction entityAction) {
        return ModRegistries.ENTITY_ACTIONS.get().containsValue(entityAction);
    }

    public static boolean has(Task skill) {
        return ModRegistries.TASKS.get().containsValue(skill);
    }

    public static boolean has(IRefinement refinement) {
        return ModRegistries.REFINEMENTS.get().containsValue(refinement);
    }

    public static boolean has(IRefinementSet refinementSet) {
        return ModRegistries.REFINEMENT_SETS.get().containsValue(refinementSet);
    }

    public static boolean has(Item item) {
        return ForgeRegistries.ITEMS.containsValue(item);
    }

    public static boolean has(Block block) {
        return ForgeRegistries.BLOCKS.containsValue(block);
    }


    public static IAction<?> getAction(ResourceLocation id) {
        return get(ModRegistries.ACTIONS.get(), id);
    }

    public static ISkill<?> getSkill(ResourceLocation id) {
        return get(ModRegistries.SKILLS.get(), id);
    }

    public static IMinionTask<?, ?> getMinionTask(ResourceLocation id) {
        return get(ModRegistries.MINION_TASKS.get(), id);
    }

    public static IEntityAction getEntityAction(ResourceLocation id) {
        return get(ModRegistries.ENTITY_ACTIONS.get(), id);
    }

    public static Task getTask(ResourceLocation id) {
        return get(ModRegistries.TASKS.get(), id);
    }

    public static IRefinement getRefinement(ResourceLocation id) {
        return get(ModRegistries.REFINEMENTS.get(), id);
    }

    public static IRefinementSet getRefinementSet(ResourceLocation id) {
        return get(ModRegistries.REFINEMENT_SETS.get(), id);
    }

    public static IOil getOil(ResourceLocation id) {
        return get(ModRegistries.OILS.get(), id);
    }


    public static <T> T get(@NotNull Supplier<IForgeRegistry<T>> registrySupplier, ResourceLocation id) {
        return registrySupplier.get().getValue(id);
    }

    public static <T> T get(@NotNull IForgeRegistry<T> registry, ResourceLocation id) {
        return registry.getValue(id);
    }


    public static <T> boolean has(@NotNull Supplier<IForgeRegistry<T>> registrySupplier, ResourceLocation id) {
        return registrySupplier.get().containsKey(id);
    }

    public static <T> boolean has(@NotNull IForgeRegistry<T> registry, ResourceLocation id) {
        return registry.containsKey(id);
    }

    public static <T> @NotNull Collection<T> values(@NotNull Supplier<IForgeRegistry<T>> registrySupplier) {
        return registrySupplier.get().getValues();
    }

    public static <T> @NotNull Collection<T> values(@NotNull IForgeRegistry<T> registry) {
        return registry.getValues();
    }

    public static <T> @NotNull Collection<ResourceLocation> keys(@NotNull Supplier<IForgeRegistry<T>> registry) {
        return registry.get().getKeys();
    }

}
