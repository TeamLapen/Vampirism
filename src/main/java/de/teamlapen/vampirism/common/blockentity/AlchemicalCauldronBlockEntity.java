package de.teamlapen.vampirism.common.blockentity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.common.blocks.AlchemicalCauldronBlock;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModDataMaps;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.inventory.AlchemicalCauldronMenu;
import de.teamlapen.vampirism.common.recipes.AlchemicalCauldronRecipe;
import de.teamlapen.vampirism.common.recipes.AlchemicalCauldronRecipeInput;
import de.teamlapen.vampirism.common.util.Helper;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * slots:  0: liquid, 1: ingredient, 2: result, 3: fuel
 */
public class AlchemicalCauldronBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Codec<Map<ResourceKey<Recipe<?>>, Integer>> RECIPES_USED_CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);

    private static final int[] SLOTS_DOWN = new int[] {0, 1, 2};
    private static final int[] SLOTS_UP = new int[] {0};
    private static final int[] SLOTS_WEST = new int[] {1};
    private static final int[] SLOTS_FUEL = new int[] {3};

    @Nullable
    private UUID ownerID;
    @Nullable
    private String ownerName;
    private @Nullable RecipeHolder<AlchemicalCauldronRecipe> recipeChecked;
    private final RecipeType<? extends AlchemicalCauldronRecipe> recipeType;
    protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    protected int litTime;
    protected int litDuration;
    protected int cookingProgress;
    protected int cookingTotalTime;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int slot) {
            return switch (slot) {
                case 0 -> {
                    if (litDuration > Short.MAX_VALUE) {
                        // Neo: preserve litTime / litDuration ratio on the client as data slots are synced as shorts.
                        yield Mth.floor(((double) litTime / litDuration) * Short.MAX_VALUE);
                    }

                    yield AlchemicalCauldronBlockEntity.this.litTime;
                }
                case 1 -> Math.min(AlchemicalCauldronBlockEntity.this.litDuration, Short.MAX_VALUE);
                case 2 -> AlchemicalCauldronBlockEntity.this.cookingProgress;
                case 3 -> AlchemicalCauldronBlockEntity.this.cookingTotalTime;
                default -> 0;
            };
        }

        @Override
        public void set(int p_58433_, int p_58434_) {
            switch (p_58433_) {
                case 0:
                    AlchemicalCauldronBlockEntity.this.litTime = p_58434_;
                    break;
                case 1:
                    AlchemicalCauldronBlockEntity.this.litDuration = p_58434_;
                    break;
                case 2:
                    AlchemicalCauldronBlockEntity.this.cookingProgress = p_58434_;
                    break;
                case 3:
                    AlchemicalCauldronBlockEntity.this.cookingTotalTime = p_58434_;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    private final Object2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsed = new Object2IntOpenHashMap<>();
    private final RecipeManager.CachedCheck<AlchemicalCauldronRecipeInput, AlchemicalCauldronRecipe> quickCheck;


    public AlchemicalCauldronBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        super(ModBlockEntities.ALCHEMICAL_CAULDRON.get(), pos, state);
        this.recipeType = ModRecipes.ALCHEMICAL_CAULDRON_TYPE.get();
        this.items = NonNullList.withSize(4, ItemStack.EMPTY);
        this.quickCheck = RecipeManager.createCheck(ModRecipes.ALCHEMICAL_CAULDRON_TYPE.get());
    }

    public ItemStack getFluid() {
        return this.items.getFirst();
    }

    public ItemStack getIngredient() {
        return this.items.get(1);
    }


    @Override
    public boolean canOpen(@NotNull Player player) {
        if (super.canOpen(player)) {
            if (!Helper.isHunter(player)) {
                player.displayClientMessage(Component.translatable("text.vampirism.unfamiliar"), true);
                return false;
            }
            if (HunterPlayer.get(player).getSkillHandler().isSkillEnabled(HunterSkills.BASIC_ALCHEMY)) {
                if (ownerID == null) {
                    setOwnerID(player);
                    return true;
                } else if (ownerID.equals(player.getUUID())) {
                    return true;
                } else {
                    player.displayClientMessage(Component.translatable("text.vampirism.alchemical_cauldron.other", getOwnerName()), true);
                }
            } else {
                player.displayClientMessage(Component.translatable("text.vampirism.not_learned"), true);
            }
        }
        return false;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> pItems) {
        this.items = pItems;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @NotNull
    @Override
    public Component getCustomName() {
        return Component.translatable("tile.vampirism.alchemical_cauldron");
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return Component.translatable("tile.vampirism.alchemical_cauldron.display", ownerName, Component.translatable("tile.vampirism.alchemical_cauldron"));
    }

    public int getLiquidColorClient() {
        ItemStack liquidItem = this.items.getFirst();
        return FluidUtil.getFluidContained(liquidItem).map(fluidStack -> IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack)).orElseGet(() -> {
            var color = liquidItem.getItemHolder().getData(ModDataMaps.LIQUID_COLOR_MAP);
            return color != null ? color : 0x00003B;
        });
    }

    public @NotNull Component getOwnerName() {
        return Component.literal(ownerName == null ? "Unknown" : ownerName);
    }

    @NotNull
    @Override
    public int[] getSlotsForFace(@NotNull Direction side) {
        if (side == Direction.DOWN) {
            return SLOTS_DOWN;
        } else {
            return side == Direction.UP ? SLOTS_UP : side == Direction.WEST ? SLOTS_WEST : SLOTS_FUEL;
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return false;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider holderProvider) {
        return saveCustomOnly(holderProvider);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.ownerID = input.read("owner", UUIDUtil.CODEC).orElse(null);
        this.ownerName = input.getString("owner_name").orElse(null);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.litTime = input.getIntOr("BurnTime", this.litTime);
        this.cookingProgress = input.getIntOr("CookTime", this.cookingProgress);
        this.cookingTotalTime = input.getIntOr("CookTimeTotal", this.cookingTotalTime);
        this.litDuration = this.getBurnDuration(this.items.get(1));
        this.recipesUsed.clear();
        this.recipesUsed.putAll(input.read("RecipesUsed", RECIPES_USED_CODEC).orElse(Map.of()));
    }

    protected int getBurnDuration(ItemStack pFuel) {
        if (pFuel.isEmpty()) {
            return 0;
        } else {
            return pFuel.getBurnTime(this.recipeType, this.level.fuelValues());
        }
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    @Override
    public void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("BurnTime", this.litTime);
        output.putInt("CookTime", this.cookingProgress);
        output.putInt("CookTimeTotal", this.cookingTotalTime);
        ContainerHelper.saveAllItems(output, this.items);
        output.store("RecipesUsed", RECIPES_USED_CODEC, this.recipesUsed);
        if (ownerID != null) {
            output.store("owner", UUIDUtil.CODEC, ownerID);
        }
        if (ownerName != null) {
            output.putString("owner_name", ownerName);

        }
    }

    @Override
    public void setChanged() {
        if (level != null) {
            super.setChanged();
            BlockState old = level.getBlockState(this.worldPosition);
            BlockState state = old.setValue(AbstractFurnaceBlock.LIT, this.isBurning()).setValue(AlchemicalCauldronBlock.LIQUID, this.items.getFirst().isEmpty() ? AlchemicalCauldronBlock.LiquidState.NONE : this.isBurning() ? AlchemicalCauldronBlock.LiquidState.BOILING : AlchemicalCauldronBlock.LiquidState.FILLED);
            if (old.equals(state)) {
                this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
            } else {
                this.level.setBlock(this.worldPosition, state, 3);
            }
        }
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public void setItem(int pIndex, ItemStack pStack) {
        ItemStack itemstack = this.items.get(pIndex);
        boolean flag = !pStack.isEmpty() && ItemStack.isSameItemSameComponents(itemstack, pStack);
        this.items.set(pIndex, pStack);
        pStack.limitSize(this.getMaxStackSize(pStack));
        if ((pIndex == AlchemicalCauldronMenu.FLUID_SLOT || pIndex == AlchemicalCauldronMenu.INGREDIENT_SLOT) && !flag) {
            if (this.level instanceof ServerLevel serverLevel) {
                this.cookingTotalTime = getTotalCookTime(serverLevel, this);
            }
            this.cookingProgress = 0;
            this.setChanged();
        }
    }

    public void setOwnerID(@NotNull Player player) {
        ownerID = player.getUUID();
        ownerName = player.getGameProfile().name();
        this.setChanged();
    }

    public Optional<ISkillHandler<?>> getPlayerSkillHandler() {
        return Optional.ofNullable(this.level.getPlayerByUUID(ownerID)).map(HunterPlayer::get).map(HunterPlayer::getSkillHandler);
    }

    /**
     * copy of AbstractFurnaceTileEntity#tick() with modification
     */
    public static void serverTick(@NotNull Level pLevel, BlockPos pPos, BlockState pState, @NotNull AlchemicalCauldronBlockEntity pBlockEntity) {
        boolean flag = pBlockEntity.isLit();
        boolean flag1 = false;
        if (pBlockEntity.isLit()) {
            pBlockEntity.litTime--;
        }

        ItemStack fuel = pBlockEntity.items.get(3);
        ItemStack ingredient = pBlockEntity.items.get(1);
        ItemStack fluid = pBlockEntity.items.get(0);
        boolean flag2 = !ingredient.isEmpty();
        boolean flag3 = !fuel.isEmpty();
        boolean flag4 = !fluid.isEmpty();
        if (pBlockEntity.isLit() || flag3 && flag2 && flag4) {
            RecipeHolder<AlchemicalCauldronRecipe> recipeholder;
            if (flag2 && flag4) {
                recipeholder = pBlockEntity.quickCheck.getRecipeFor(new AlchemicalCauldronRecipeInput(ingredient, fluid, pBlockEntity.getPlayerSkillHandler()), (ServerLevel) pLevel).orElse(null);
            } else {
                recipeholder = null;
            }

            int i = pBlockEntity.getMaxStackSize();
            if (!pBlockEntity.isLit() && canBurn(pLevel.registryAccess(), recipeholder, pBlockEntity.items, i, pBlockEntity) && pBlockEntity.canPlayerCook(recipeholder)) {
                pBlockEntity.litTime = pBlockEntity.getBurnDuration(fuel);
                pBlockEntity.litDuration = pBlockEntity.litTime;
                if (pBlockEntity.isLit()) {
                    flag1 = true;
                    var remainder = fuel.getCraftingRemainder();
                    if (!remainder.isEmpty()) {
                        pBlockEntity.items.set(3, remainder);
                    } else if (flag3) {
                        Item item = fuel.getItem();
                        fuel.shrink(1);
                        if (fuel.isEmpty()) {
                            pBlockEntity.items.set(3, remainder);
                        }
                    }
                }
            }

            if (pBlockEntity.isLit() && canBurn(pLevel.registryAccess(), recipeholder, pBlockEntity.items, i, pBlockEntity) && pBlockEntity.canPlayerCook(recipeholder)) {
                pBlockEntity.cookingProgress++;
                if (pBlockEntity.cookingProgress == pBlockEntity.cookingTotalTime) {
                    pBlockEntity.cookingProgress = 0;
                    pBlockEntity.cookingTotalTime = getTotalCookTime(pLevel, pBlockEntity);
                    if (burn(pLevel.registryAccess(), recipeholder, pBlockEntity.items, i, pBlockEntity)) {
                        pBlockEntity.setRecipeUsed(recipeholder);
                    }

                    flag1 = true;
                }
            } else {
                pBlockEntity.cookingProgress = 0;
            }
        } else if (!pBlockEntity.isLit() && pBlockEntity.cookingProgress > 0) {
            pBlockEntity.cookingProgress = Mth.clamp(pBlockEntity.cookingProgress - 2, 0, pBlockEntity.cookingTotalTime);
        }

        if (flag != pBlockEntity.isLit()) {
            flag1 = true;
            pState = pState.setValue(AbstractFurnaceBlock.LIT, pBlockEntity.isLit());
            pLevel.setBlock(pPos, pState, 3);
        }

        if (flag1) {
            setChanged(pLevel, pPos, pState);
        }
    }

    @NotNull
    @Override
    protected AbstractContainerMenu createMenu(int id, @NotNull Inventory player) {
        return new AlchemicalCauldronMenu(id, player, this, this.dataAccess);
    }

    @NotNull
    @Override
    protected Component getDefaultName() {
        return Component.translatable("tile.vampirism.alchemical_cauldron");
    }

    private boolean canPlayerCook(@Nullable RecipeHolder<AlchemicalCauldronRecipe> recipe) {
        if (recipe == null) return false;
        if (level == null) return false;
        if (recipeChecked == recipe) return true;
        if (ownerID == null) return false;
        Player playerEntity = this.level.getPlayerByUUID(ownerID);
        if (playerEntity == null || !playerEntity.isAlive()) return false;
        HunterPlayer hunter = HunterPlayer.get(playerEntity);
        boolean canCook = recipe.value().canBeCooked(hunter.getLevel(), hunter.getSkillHandler());
        if (canCook) {
            recipeChecked = recipe;
            return true;
        } else {
            recipeChecked = null;
            return false;
        }
    }

    /**
     * copy of AbstractFurnaceTileEntity#finishCooking(IRecipe) with modification
     */
    private void finishCooking(RegistryAccess access, @Nullable RecipeHolder<AlchemicalCauldronRecipe> recipe) {
        if (recipe != null && canBurn(access, recipe, items, getMaxStackSize(), this) && canPlayerCook(recipe)) {
            ItemStack itemstackfluid = this.items.get(0);
            ItemStack itemstackingredient = this.items.get(1);
            ItemStack itemstack1result = recipe.value().result();
            ItemStack itemstackoutput = this.items.get(2);
            if (itemstackoutput.isEmpty()) {
                this.items.set(2, itemstack1result.copy());
            } else if (itemstackoutput.getItem() == itemstack1result.getItem()) {
                itemstackoutput.grow(itemstack1result.getCount());
            }

            if (this.level != null && !this.level.isClientSide()) {
                this.setRecipeUsed(recipe);
            }

            Either<Ingredient, FluidStack> fluid = recipe.value().getFluid();
            fluid.ifLeft(ingredient -> itemstackfluid.shrink(1));
            fluid.ifRight(fluidStack -> this.items.set(0, FluidUtil.getFluidHandler(itemstackfluid).map(handler -> {
                FluidStack drained = handler.drain(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                if (drained.getAmount() < fluidStack.getAmount()) {
                    handler.drain(new FluidStack(fluidStack.getFluid(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE); //For bucket containers we need to draw at least one bucket size
                }
                return handler.getContainer();
            }).orElse(ItemStack.EMPTY)));
            itemstackingredient.shrink(1);
            recipeChecked = null;
        }
    }

    private static boolean canBurn(RegistryAccess pRegistryAccess, @Nullable RecipeHolder<?> pRecipe, NonNullList<ItemStack> pInventory, int pMaxStackSize, AlchemicalCauldronBlockEntity furnace) {
        if (!pInventory.get(0).isEmpty() && pRecipe != null) {
            ItemStack itemstack = ((RecipeHolder<? extends AlchemicalCauldronRecipe>) pRecipe).value().assemble(new AlchemicalCauldronRecipeInput(furnace.getIngredient(), furnace.getFluid()), pRegistryAccess);
            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack itemstack1 = pInventory.get(2);
                if (itemstack1.isEmpty()) {
                    return true;
                } else if (!ItemStack.isSameItemSameComponents(itemstack1, itemstack)) {
                    return false;
                } else {
                    // Neo fix: make furnace respect stack sizes in furnace recipes
                    return itemstack1.getCount() + itemstack.getCount() <= pMaxStackSize && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize() || itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Neo fix: make furnace respect stack sizes in furnace recipes
                }
            }
        } else {
            return false;
        }
    }

    private static boolean burn(RegistryAccess pRegistryAccess, @javax.annotation.Nullable RecipeHolder<?> pRecipe, NonNullList<ItemStack> pInventory, int pMaxStackSize, AlchemicalCauldronBlockEntity furnace) {
        if (pRecipe != null && canBurn(pRegistryAccess, pRecipe, pInventory, pMaxStackSize, furnace)) {
            ItemStack fluid = pInventory.get(AlchemicalCauldronMenu.FLUID_SLOT);
            ItemStack ingredient = pInventory.get(AlchemicalCauldronMenu.INGREDIENT_SLOT);
            ItemStack newResult = ((RecipeHolder<? extends AlchemicalCauldronRecipe>) pRecipe).value().assemble(new AlchemicalCauldronRecipeInput(furnace.getIngredient(), furnace.getFluid()), pRegistryAccess);
            ItemStack currentResult = pInventory.get(AlchemicalCauldronMenu.RESULT_SLOT);
            if (currentResult.isEmpty()) {
                pInventory.set(AlchemicalCauldronMenu.RESULT_SLOT, newResult.copy());
            } else if (ItemStack.isSameItemSameComponents(currentResult, newResult)) {
                currentResult.grow(newResult.getCount());
            }
            fluid.shrink(1);
            ingredient.shrink(1);
            return true;
        } else {
            return false;
        }
    }

    private static int getTotalCookTime(Level pLevel, AlchemicalCauldronBlockEntity pBlockEntity) {
        AlchemicalCauldronRecipeInput brewingRecipeInput = new AlchemicalCauldronRecipeInput(pBlockEntity.getIngredient(), pBlockEntity.getFluid());
        return VampirismMod.proxy.getRecipeFor(ModRecipes.ALCHEMICAL_CAULDRON_TYPE.get(), brewingRecipeInput, pLevel, pBlockEntity.quickCheck).map(p_300840_ -> p_300840_.value().getCookingTime()).orElse(200);
    }

    private boolean isBurning() {
        return this.dataAccess.get(0) > 0;
    }

    private boolean isCooking() {
        return this.dataAccess.get(2) > 0;
    }

    @Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> pRecipe) {
        if (pRecipe != null) {
            this.recipesUsed.addTo(pRecipe.id(), 1);
        }
    }

    @Nullable
    @Override
    public RecipeHolder<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void fillStackedContents(StackedItemContents contents) {
        for (ItemStack item : this.items) {
            contents.accountStack(item);
        }
    }
}
