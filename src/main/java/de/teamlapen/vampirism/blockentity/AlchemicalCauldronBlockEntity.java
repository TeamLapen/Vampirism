package de.teamlapen.vampirism.blockentity;

import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.blocks.AlchemicalCauldronBlock;
import de.teamlapen.vampirism.core.ModDataMaps;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.inventory.AlchemicalCauldronMenu;
import de.teamlapen.vampirism.recipes.AlchemicalCauldronRecipe;
import de.teamlapen.vampirism.recipes.AlchemicalCauldronRecipeInput;
import de.teamlapen.vampirism.recipes.BrewingRecipeInput;
import de.teamlapen.vampirism.util.Helper;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * slots:  0: liquid, 1: ingredient, 2: result, 3: fuel
 */
public class AlchemicalCauldronBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int[] SLOTS_DOWN = new int[]{0, 1, 2};
    private static final int[] SLOTS_UP = new int[]{0};
    private static final int[] SLOTS_WEST = new int[]{1};
    private static final int[] SLOTS_FUEL = new int[]{3};

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
        public int get(int p_58431_) {
            switch (p_58431_) {
                case 0:
                    if (litDuration > Short.MAX_VALUE) {
                        // Neo: preserve litTime / litDuration ratio on the client as data slots are synced as shorts.
                        return net.minecraft.util.Mth.floor(((double) litTime / litDuration) * Short.MAX_VALUE);
                    }

                    return AlchemicalCauldronBlockEntity.this.litTime;
                case 1:
                    return Math.min(AlchemicalCauldronBlockEntity.this.litDuration, Short.MAX_VALUE);
                case 2:
                    return AlchemicalCauldronBlockEntity.this.cookingProgress;
                case 3:
                    return AlchemicalCauldronBlockEntity.this.cookingTotalTime;
                default:
                    return 0;
            }
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

    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
    private final RecipeManager.CachedCheck<AlchemicalCauldronRecipeInput, AlchemicalCauldronRecipe> quickCheck;


    public AlchemicalCauldronBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        super(ModTiles.ALCHEMICAL_CAULDRON.get(), pos, state);
        this.recipeType = ModRecipes.ALCHEMICAL_CAULDRON_TYPE.get();
        this.items = NonNullList.withSize(4, ItemStack.EMPTY);
        this.quickCheck = RecipeManager.createCheck(ModRecipes.ALCHEMICAL_CAULDRON_TYPE.get());
    }

    public ItemStack getFluid() {
        return this.items.get(0);
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
            if (HunterPlayer.get(player).getSkillHandler().isSkillEnabled(HunterSkills.BASIC_ALCHEMY.get())) {
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
        CompoundTag compound = super.getUpdateTag(holderProvider);
        if (ownerID != null) compound.putUUID("owner", ownerID);
        if (ownerName != null) compound.putString("owner_name", ownerName);
        ContainerHelper.saveAllItems(compound, this.items, holderProvider);
        return compound;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag compound, HolderLookup.Provider holderProvider) {
        super.handleUpdateTag(compound, holderProvider);
        ownerID = compound.hasUUID("owner") ? compound.getUUID("owner") : null;
        ownerName = compound.contains("owner_name") ? compound.getString("owner_name") : null;
        ContainerHelper.loadAllItems(compound, this.items, holderProvider);
    }

    protected int getBurnDuration(ItemStack pFuel) {
        if (pFuel.isEmpty()) {
            return 0;
        } else {
            return pFuel.getBurnTime(this.recipeType);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider holderProvider) {
        super.loadAdditional(pTag, holderProvider);
        ownerID = pTag.hasUUID("owner") ? pTag.getUUID("owner") : null;
        ownerName = pTag.contains("owner_name") ? pTag.getString("owner_name") : null;
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, this.items, holderProvider);
        this.litTime = pTag.getInt("BurnTime");
        this.cookingProgress = pTag.getInt("CookTime");
        this.cookingTotalTime = pTag.getInt("CookTimeTotal");
        this.litDuration = this.getBurnDuration(this.items.get(1));
        CompoundTag compoundtag = pTag.getCompound("RecipesUsed");

        for (String s : compoundtag.getAllKeys()) {
            this.recipesUsed.put(ResourceLocation.parse(s), compoundtag.getInt(s));
        }
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    @Override
    public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider holderProvider) {
        CompoundTag nbt = pkt.getTag();
        if (hasLevel()) {
            handleUpdateTag(nbt, holderProvider);
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag pTag, HolderLookup.Provider holderProvider) {
        super.saveAdditional(pTag, holderProvider);
        pTag.putInt("BurnTime", this.litTime);
        pTag.putInt("CookTime", this.cookingProgress);
        pTag.putInt("CookTimeTotal", this.cookingTotalTime);
        ContainerHelper.saveAllItems(pTag, this.items, holderProvider);
        CompoundTag compoundtag = new CompoundTag();
        this.recipesUsed.forEach((p_187449_, p_187450_) -> compoundtag.putInt(p_187449_.toString(), p_187450_));
        pTag.put("RecipesUsed", compoundtag);
        if (ownerID != null) {
            pTag.putUUID("owner", ownerID);
        }
        if (ownerName != null) {
            pTag.putString("owner_name", ownerName);

        }
    }

    @Override
    public void setChanged() {
        if (level != null) {
            super.setChanged();
            BlockState old = level.getBlockState(this.worldPosition);
            BlockState state = old.setValue(AbstractFurnaceBlock.LIT, this.isBurning()).setValue(AlchemicalCauldronBlock.LIQUID, this.items.getFirst().isEmpty() ? 0 : this.isBurning() ? 2 : 1);
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
            this.cookingTotalTime = getTotalCookTime(this.level, this);
            this.cookingProgress = 0;
            this.setChanged();
        }
    }

    public void setOwnerID(@NotNull Player player) {
        ownerID = player.getUUID();
        ownerName = player.getGameProfile().getName();
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
                recipeholder = pBlockEntity.quickCheck.getRecipeFor(new AlchemicalCauldronRecipeInput(ingredient, fluid, pBlockEntity.getPlayerSkillHandler()), pLevel).orElse(null);
            } else {
                recipeholder = null;
            }

            int i = pBlockEntity.getMaxStackSize();
            if (!pBlockEntity.isLit() && canBurn(pLevel.registryAccess(), recipeholder, pBlockEntity.items, i, pBlockEntity) && pBlockEntity.canPlayerCook(recipeholder)) {
                pBlockEntity.litTime = pBlockEntity.getBurnDuration(fuel);
                pBlockEntity.litDuration = pBlockEntity.litTime;
                if (pBlockEntity.isLit()) {
                    flag1 = true;
                    if (fuel.hasCraftingRemainingItem())
                        pBlockEntity.items.set(3, fuel.getCraftingRemainingItem());
                    else
                    if (flag3) {
                        Item item = fuel.getItem();
                        fuel.shrink(1);
                        if (fuel.isEmpty()) {
                            pBlockEntity.items.set(3, fuel.getCraftingRemainingItem());
                        }
                    }
                }
            }

            if (pBlockEntity.isLit() && canBurn(pLevel.registryAccess(), recipeholder, pBlockEntity.items, i, pBlockEntity)  && pBlockEntity.canPlayerCook(recipeholder)) {
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
            ItemStack itemstack1result = recipe.value().getResultItem(access);
            ItemStack itemstackoutput = this.items.get(2);
            if (itemstackoutput.isEmpty()) {
                this.items.set(2, itemstack1result.copy());
            } else if (itemstackoutput.getItem() == itemstack1result.getItem()) {
                itemstackoutput.grow(itemstack1result.getCount());
            }

            if (this.level != null && !this.level.isClientSide) {
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
                    return itemstack1.getCount() + itemstack.getCount() <= pMaxStackSize && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize() // Neo fix: make furnace respect stack sizes in furnace recipes
                            ? true
                            : itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Neo fix: make furnace respect stack sizes in furnace recipes
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
        return pBlockEntity.quickCheck.getRecipeFor(brewingRecipeInput, pLevel).map(p_300840_ -> p_300840_.value().getCookingTime()).orElse(200);
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
            ResourceLocation resourcelocation = pRecipe.id();
            this.recipesUsed.addTo(resourcelocation, 1);
        }
    }

    @Nullable
    @Override
    public RecipeHolder<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void fillStackedContents(StackedContents pContents) {
        for (ItemStack itemstack : this.items) {
            pContents.accountStack(itemstack);
        }
    }
}
