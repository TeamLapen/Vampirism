package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.core.ModOils;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class SmeltItemLootModifier extends LootModifier {
    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    protected SmeltItemLootModifier(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        ItemStack stack = context.getParamOrNull(LootParameters.TOOL);
        Entity entity = context.getParamOrNull(LootParameters.THIS_ENTITY);
        if (!(entity instanceof LivingEntity) || stack == null || !OilUtils.getAppliedOil(stack).filter(oil -> oil == ModOils.SMELT.get()).isPresent()) {
            return generatedLoot;
        }
        ItemStack entityStack = ((LivingEntity) entity).getMainHandItem();
        stack = entityStack;
        OilUtils.reduceAppliedOilDuration(stack);
        return trySmelting(generatedLoot, context.getLevel());
    }

    private List<ItemStack> trySmelting(List<ItemStack> generatedLoot, ServerWorld level) {
        RecipeManager recipeManager = level.getRecipeManager();
        return generatedLoot.stream().map(stack -> recipeManager.getRecipeFor(IRecipeType.SMELTING, new Inventory(stack), level).map(AbstractCookingRecipe::getResultItem).filter(result -> !result.isEmpty()).orElse(stack)).collect(Collectors.toList());
    }

    public static class Serializer extends GlobalLootModifierSerializer<SmeltItemLootModifier> {

        @Override
        public SmeltItemLootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition) {
            return new SmeltItemLootModifier(ailootcondition);
        }

        @Override
        public JsonObject write(SmeltItemLootModifier instance) {
            return null;
        }
    }
}
