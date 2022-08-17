package de.teamlapen.vampirism.world.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.core.ModOils;
import de.teamlapen.vampirism.util.OilUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import org.jetbrains.annotations.NotNull;
import java.util.stream.Collector;

public class SmeltItemLootModifier extends LootModifier {

    public static final Codec<SmeltItemLootModifier> CODEC = RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, SmeltItemLootModifier::new));

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    protected SmeltItemLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @NotNull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ItemStack stack = context.getParamOrNull(LootContextParams.TOOL);
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof LivingEntity) || stack == null || !OilUtils.getAppliedOil(stack).filter(oil -> oil == ModOils.SMELT.get()).isPresent()) {
            return generatedLoot;
        }
        ItemStack entityStack = ((LivingEntity) entity).getMainHandItem();
        stack = entityStack;
        OilUtils.reduceAppliedOilDuration(stack);
        return trySmelting(generatedLoot, context.getLevel());
    }

    private ObjectArrayList<ItemStack> trySmelting(ObjectArrayList<ItemStack> generatedLoot, ServerLevel level) {
        RecipeManager recipeManager = level.getRecipeManager();
        return generatedLoot.stream().map(stack -> recipeManager.getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), level).map(AbstractCookingRecipe::getResultItem).filter(result -> !result.isEmpty()).orElse(stack)).collect(Collector.of(ObjectArrayList::new, ObjectArrayList::add, (left, right) -> {
            left.addAll(right);
            return left;
        }));
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return ModLoot.smelting.get();
    }
}
