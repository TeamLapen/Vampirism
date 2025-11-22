package de.teamlapen.vampirism.common.util;

import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.common.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public class RegUtil extends de.teamlapen.factions.common.util.RegUtil {

    public static ResourceLocation id(@NotNull Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static ResourceLocation id(@NotNull ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem());
    }

    public static ResourceLocation id(@NotNull Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public static ResourceLocation id(@NotNull Fluid block) {
        return BuiltInRegistries.FLUID.getKey(block);
    }

    public static ResourceLocation id(@NotNull EntityType<?> type) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(type);
    }

    public static ResourceLocation id(Level level, @NotNull Biome type) {
        return level.registryAccess().lookupOrThrow(Registries.BIOME).getKey(type);
    }

    public static ResourceLocation id(@NotNull VillagerProfession profession) {
        return BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession);
    }

    public static ResourceLocation id(@NotNull IOil oil) {
        return ModRegistries.OILS.getKey(oil);
    }

    public static boolean has(@NotNull Item item) {
        return BuiltInRegistries.ITEM.containsValue(item);
    }

    public static boolean has(@NotNull Block block) {
        return BuiltInRegistries.BLOCK.containsValue(block);
    }

    public static boolean has(@NotNull ServerLevel level, @NotNull Biome biome) {
        return level.registryAccess().lookupOrThrow(Registries.BIOME).containsValue(biome);
    }


    public static Item getItem(@NotNull ResourceLocation id) {
        return BuiltInRegistries.ITEM.getValue(id);
    }

    public static Biome getBiome(ServerLevel level, @NotNull ResourceLocation id) {
        return level.registryAccess().lookupOrThrow(Registries.BIOME).getValue(id);
    }

    public static IOil getOil(@NotNull ResourceLocation id) {
        return ModRegistries.OILS.getValue(id);
    }

    public static <T, Z extends Registry<T>> Holder<T> getHolder(Level level, ResourceKey<Z> registry, T type) {
        return level.registryAccess().lookupOrThrow(registry).wrapAsHolder(type);
    }

    public static Holder<DamageType> getHolder(Level level, DamageType type) {
        return getHolder(level, Registries.DAMAGE_TYPE, type);
    }

    public static <T> boolean has(@NotNull Registry<T> registry, @NotNull ResourceLocation id) {
        return registry.containsKey(id);
    }



}
