package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

public class RegUtil {

    public static ResourceLocation id(@NotNull IAction<?> action) {
        return ModRegistries.ACTIONS.get().getKey(action);
    }

    public static ResourceLocation id(@NotNull ISkill<?> skill) {
        return ModRegistries.SKILLS.get().getKey(skill);
    }

    public static ResourceLocation id(@NotNull IMinionTask<?, ?> minionTask) {
        return ModRegistries.MINION_TASKS.get().getKey(minionTask);
    }

    public static ResourceLocation id(@NotNull IEntityAction entityAction) {
        return ModRegistries.ENTITY_ACTIONS.get().getKey(entityAction);
    }

    public static ResourceLocation id(@NotNull IRefinement refinement) {
        return ModRegistries.REFINEMENTS.get().getKey(refinement);
    }

    public static ResourceLocation id(@NotNull IRefinementSet refinementSet) {
        return ModRegistries.REFINEMENT_SETS.get().getKey(refinementSet);
    }

    public static ResourceLocation id(@NotNull Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }

    public static ResourceLocation id(@NotNull Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    public static ResourceLocation id(@NotNull Fluid block) {
        return ForgeRegistries.FLUIDS.getKey(block);
    }

    public static ResourceLocation id(@NotNull EntityType<?> type) {
        return ForgeRegistries.ENTITY_TYPES.getKey(type);
    }

    public static ResourceLocation id(@NotNull Biome type) {
        return ForgeRegistries.BIOMES.getKey(type);
    }

    public static ResourceLocation id(@NotNull Enchantment type) {
        return ForgeRegistries.ENCHANTMENTS.getKey(type);
    }

    public static ResourceLocation id(@NotNull VillagerProfession profession) {
        return ForgeRegistries.VILLAGER_PROFESSIONS.getKey(profession);
    }

    public static ResourceLocation id(@NotNull IOil oil) {
        return ModRegistries.OILS.get().getKey(oil);
    }

    public static Optional<ResourceKey<IAction<?>>> key(@NotNull IAction<?> action) {
        return ModRegistries.ACTIONS.get().getResourceKey(action);
    }

    public static boolean has(@NotNull IAction<?> action) {
        return ModRegistries.ACTIONS.get().containsValue(action);
    }

    public static boolean has(@NotNull ISkill<?> skill) {
        return ModRegistries.SKILLS.get().containsValue(skill);
    }

    public static boolean has(@NotNull IMinionTask<?, ?> minionTask) {
        return ModRegistries.MINION_TASKS.get().containsValue(minionTask);
    }

    public static boolean has(@NotNull IEntityAction entityAction) {
        return ModRegistries.ENTITY_ACTIONS.get().containsValue(entityAction);
    }

    public static boolean has(@NotNull IRefinement refinement) {
        return ModRegistries.REFINEMENTS.get().containsValue(refinement);
    }

    public static boolean has(@NotNull IRefinementSet refinementSet) {
        return ModRegistries.REFINEMENT_SETS.get().containsValue(refinementSet);
    }

    public static boolean has(@NotNull Item item) {
        return ForgeRegistries.ITEMS.containsValue(item);
    }

    public static boolean has(@NotNull Block block) {
        return ForgeRegistries.BLOCKS.containsValue(block);
    }


    public static IAction<?> getAction(@NotNull ResourceLocation id) {
        return get(ModRegistries.ACTIONS.get(), id);
    }

    public static ISkill<?> getSkill(@NotNull ResourceLocation id) {
        return get(ModRegistries.SKILLS.get(), id);
    }

    public static IMinionTask<?, ?> getMinionTask(@NotNull ResourceLocation id) {
        return get(ModRegistries.MINION_TASKS.get(), id);
    }

    public static IEntityAction getEntityAction(@NotNull ResourceLocation id) {
        return get(ModRegistries.ENTITY_ACTIONS.get(), id);
    }

    public static IRefinement getRefinement(@NotNull ResourceLocation id) {
        return get(ModRegistries.REFINEMENTS.get(), id);
    }

    public static IRefinementSet getRefinementSet(@NotNull ResourceLocation id) {
        return get(ModRegistries.REFINEMENT_SETS.get(), id);
    }

    public static IOil getOil(@NotNull ResourceLocation id) {
        return get(ModRegistries.OILS.get(), id);
    }

    public static <T, Z extends Registry<T>> Holder<T> getHolder(Level level, ResourceKey<Z> registry, T type) {
        return level.registryAccess().registryOrThrow(registry).wrapAsHolder(type);
    }

    public static Holder<DamageType> getHolder(Level level, DamageType type) {
        return getHolder(level, Registries.DAMAGE_TYPE, type);
    }


    public static <T> T get(@NotNull Supplier<IForgeRegistry<T>> registrySupplier,@NotNull  ResourceLocation id) {
        return registrySupplier.get().getValue(id);
    }

    public static <T> T get(@NotNull IForgeRegistry<T> registry,@NotNull  ResourceLocation id) {
        return registry.getValue(id);
    }


    public static <T> boolean has(@NotNull Supplier<IForgeRegistry<T>> registrySupplier,@NotNull  ResourceLocation id) {
        return registrySupplier.get().containsKey(id);
    }

    public static <T> boolean has(@NotNull IForgeRegistry<T> registry,@NotNull  ResourceLocation id) {
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
