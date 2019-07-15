//package de.teamlapen.vampirism.inventory.crafting;
//
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//
//import de.teamlapen.lib.lib.util.ItemStackUtil;
//import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
//import de.teamlapen.vampirism.inventory.recipes.AlchemicalCauldronRecipe;
//import net.minecraft.block.Block;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraftforge.fluids.FluidStack;
//
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//import java.util.List;
//import java.util.Map;
//
//
//public class AlchemicalCauldronCraftingManager implements IAlchemicalCauldronCraftingManager {
//
//    private final static String TAG = "ACCraftingManager";
//    private final static AlchemicalCauldronCraftingManager ourInstance = new AlchemicalCauldronCraftingManager();
//
//    public static AlchemicalCauldronCraftingManager getInstance() {
//        return ourInstance;
//    }
//
//    private final List<AbstractAlchemicalCauldronRecipe> recipes = Lists.newLinkedList();
//    private final Map<Object, Integer> liquidColors = Maps.newHashMap();
//
//    private AlchemicalCauldronCraftingManager() {
//    }
//
//    @Override
//    public void addRecipe(AbstractAlchemicalCauldronRecipe recipe) {
//        for (AbstractAlchemicalCauldronRecipe r : recipes) {
//            if (r.areSameIngredients(recipe)) {
//                throw new IllegalArgumentException(TAG + ": Duplicate recipe " + r + " and " + recipe);
//            }
//        }
//        recipes.add(recipe);
//    }
//
//    @Override
//    public AbstractAlchemicalCauldronRecipe addRecipe(@Nonnull Object output, @Nonnull Object liquid, @Nullable Object ingredient) {
//        AbstractAlchemicalCauldronRecipe recipe;
//        if (liquid instanceof Item) {
//            liquid = new ItemStack((Item) liquid);
//        } else if (liquid instanceof Block) {
//            liquid = new ItemStack((Block) liquid);
//        }
//        if (liquid instanceof ItemStack && !((ItemStack) liquid).isEmpty()) {
//            recipe = new AlchemicalCauldronRecipe(getItemStackCopy(output), ((ItemStack) liquid).copy(), getItemStackCopy(ingredient));
//        } else if (liquid instanceof FluidStack) {
//            recipe = new AlchemicalCauldronRecipe(getItemStackCopy(output), ((FluidStack) liquid).copy(), getItemStackCopy(ingredient));
//        } else {
//            throw new IllegalArgumentException(TAG + ": Liquid has to be either a ItemStack or a FluidStack");
//        }
//        addRecipe(recipe);
//        return recipe;
//    }
//
//    @Override
//    public AbstractAlchemicalCauldronRecipe addRecipe(@Nonnull Object output, @Nonnull Object liquid, @Nullable Object ingredient, int ticks, int exp, int reqLevel, ISkill... reqSkills) {
//        AbstractAlchemicalCauldronRecipe recipe = addRecipe(output, liquid, ingredient);
//        recipe.configure(ticks, exp, reqLevel, reqSkills);
//        return recipe;
//    }
//
//    @Nullable
//    @Override
//    public AbstractAlchemicalCauldronRecipe findRecipe(@Nonnull ItemStack liquid, @Nonnull ItemStack ingredient) {
//        assert !liquid.isEmpty();
//        for (AbstractAlchemicalCauldronRecipe r : recipes) {
//            if (r.isValidLiquidItem(liquid) || r.isValidFluidItem(liquid) != null) {
//                if (ItemStackUtil.doesStackContain(ingredient, r.getIngredient())) {
//                    return r;
//                }
//            }
//        }
//        return null;
//    }
//
//    @Nullable
//    @Override
//    public AbstractAlchemicalCauldronRecipe findRecipe(FluidStack liquid, @Nonnull ItemStack ingredient) {
//
//        for (AbstractAlchemicalCauldronRecipe r : recipes) {
//            if (r.isValidFluidStack(liquid) != null) {
//                if (ItemStackUtil.doesStackContain(ingredient, r.getIngredient())) {
//                    return r;
//                }
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public int getLiquidColor(@Nonnull ItemStack stack) {
//        if (!stack.isEmpty()) {
//            if (liquidColors.containsKey(stack)) {
//                return liquidColors.get(stack);
//            }
//            if (liquidColors.containsKey(stack.getItem())) {
//                return liquidColors.get(stack.getItem());
//            }
//        }
//        return -1;
//    }
//
//    public List<AbstractAlchemicalCauldronRecipe> getRecipes() {
//        return recipes;
//    }
//
//    @Override
//    public void registerLiquidColor(@Nonnull ItemStack stack, int color) {
//        assert !stack.isEmpty();
//        liquidColors.put(stack, color);
//    }
//
//    @Override
//    public void registerLiquidColor(Item item, int color) {
//        liquidColors.put(item, color);
//    }
//
//    private @Nonnull
//    ItemStack getItemStackCopy(@Nullable Object o) {
//        if (o == null) return ItemStack.EMPTY;
//        if (o instanceof ItemStack) return ((ItemStack) o).copy();
//        if (o instanceof Item) return new ItemStack((Item) o);
//        if (o instanceof Block) return new ItemStack((Block) o);
//        throw new IllegalArgumentException(TAG + ": Argument has to be one of the following: ItemStack, Item or Block");
//
//    }
//}
