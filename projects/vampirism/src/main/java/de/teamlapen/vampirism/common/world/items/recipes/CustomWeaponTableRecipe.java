package de.teamlapen.vampirism.common.world.items.recipes;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.*;

public abstract class CustomWeaponTableRecipe implements IWeaponTableRecipe {

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public abstract RecipeSerializer<? extends CustomWeaponTableRecipe> getSerializer();

    public static class Serializer<T extends CustomWeaponTableRecipe> implements RecipeSerializer<T> {

        private final MapCodec<T> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

        public Serializer(Factory<T> factory) {
            this.codec = MapCodec.unit(factory::create);
            this.streamCodec = new StreamCodec<>() {
                @Override
                public T decode(RegistryFriendlyByteBuf buf) {
                    return factory.create();
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, T value) {
                }
            };
        }

        @Override
        public MapCodec<T> codec() {
            return this.codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
            return this.streamCodec;
        }

        @FunctionalInterface
        public interface Factory<T extends CustomWeaponTableRecipe> {
            T create();
        }
    }
}
