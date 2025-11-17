package de.teamlapen.vampirism.common.recipes;

import de.teamlapen.vampirism.common.core.ModRecipes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RecipesSync {

    @NotNull
    private RecipeMap recipeMap = RecipeMap.EMPTY;

    public RecipeMap getRecipes() {
        return this.recipeMap;
    }
    @Nullable
    private Collection<RecipeType<?>> recipeTypes;

    private Collection<RecipeType<?>> syncRecipeTypes() {
        if (this.recipeTypes == null) {
            this.recipeTypes = List.of(
                    ModRecipes.WEAPONTABLE_CRAFTING_TYPE.get(),
                    ModRecipes.ALCHEMICAL_CAULDRON_TYPE.get(),
                    ModRecipes.ALCHEMICAL_TABLE_TYPE.get(),
                    ModRecipes.INFUSER_TYPE.get()
            );
        }
        return this.recipeTypes;
    }

    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(RecipeType<T> type, I input, Level level, RecipeManager.CachedCheck<I, T> check) {
        if (level instanceof ServerLevel serverLevel) {
            return check.getRecipeFor(input, serverLevel);
        } else {
            return this.recipeMap.getRecipesFor(type, input, level).findFirst();
        }
    }

    @SubscribeEvent
    public void onRecipesSync(OnDatapackSyncEvent event) {
        event.sendRecipes(syncRecipeTypes());
    }

    @SubscribeEvent
    public <I extends RecipeInput,T extends Recipe<I>> void onRecipesReceived(RecipesReceivedEvent event) {
        this.recipeMap = RecipeMap.create(syncRecipeTypes().stream().flatMap(x -> getRecipeHolders(event.getRecipeMap(), (RecipeType<T>)x).stream()).collect(Collectors.toUnmodifiableList()));
    }

    private <I extends RecipeInput,T extends Recipe<I>> Collection<RecipeHolder<T>> getRecipeHolders(RecipeMap map, RecipeType<T> type) {
        return map.byType(type);
    }

    @SubscribeEvent
    public void loggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        this.recipeMap = RecipeMap.EMPTY;
    }
}
