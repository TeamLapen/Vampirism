package de.teamlapen.vampirism.common.world.blockentity;

import de.teamlapen.faction.api.factions.skills.ISkillHandler;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.vampirism.api.VampirismApi;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.world.items.IExtendedBrewingRecipeRegistry;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.inventory.VaporStillMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class VaporStillBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, MenuProvider {

    public static final String KEY_BREW_TIME = "BrewTime";
    public static final String KEY_FUEL = "Fuel";
    public static final String KEY_CONFIG = "Config";
    public static final String KEY_OWNER = "Owner";
    public static final String KEY_OWNER_NAME = "OwnerName";

    /*
     * 0: Fuel
     * 1: Extra ingredient
     * 2: Main (vanilla) ingredient
     * 3-5: Main bottle slots
     * 6-7: Extra bottle slots
     */
    private static final int CONTAINER_SIZE = 8;

    private static final int[] SLOTS_FOR_UP = {0, 1, 2};
    private static final int[] SLOTS_FOR_DOWN = {3, 4, 5, 1, 2};
    private static final int[] SLOTS_FOR_DOWN_EXTENDED = {3, 4, 5, 6, 7, 1, 2};
    private static final int[] OUTPUT_SLOTS = {3, 4, 5, 0};
    private static final int[] OUTPUT_SLOTS_EXTENDED = {3, 4, 5, 6, 7, 0};

    private static final int BREW_TIME_NORMAL = 200;
    private static final int BREW_TIME_SWIFT  = 400;

    private static final int CAPABILITY_UPDATE_INTERVAL = 64;

    private final BrewingCapabilities config = new BrewingCapabilities();

    private @Nullable UUID ownerID;
    private @Nullable Component ownerName;

    private NonNullList<ItemStack> brewingItemStacks = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    private int brewTime;
    private int fuel;
    private Item ingredientID = Items.AIR;
    private Item extraIngredientID = Items.AIR;

    protected final ContainerData syncedProperties = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> brewTime;
                case 1 -> fuel;
                case 2 -> getMaxBrewTime();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> brewTime = value;
                case 1 -> fuel = value;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    public VaporStillBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VAPOR_STILL.get(), pos, state);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.brewingItemStacks = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.brewingItemStacks);
        this.brewTime = input.getShortOr(KEY_BREW_TIME, (short) 0);
        this.fuel = input.getByteOr(KEY_FUEL, (byte) 0);
        this.config.fromByte(input.getByteOr(KEY_CONFIG, (byte) 0));
        this.ownerID = input.read(KEY_OWNER, UUIDUtil.CODEC).orElse(null);
        this.ownerName = input.read(KEY_OWNER_NAME, ComponentSerialization.CODEC).orElse(null);
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putShort(KEY_BREW_TIME, (short) this.brewTime);
        output.putByte(KEY_FUEL, (byte) this.fuel);
        output.putByte(KEY_CONFIG, this.config.toByte());
        ContainerHelper.saveAllItems(output, this.brewingItemStacks);
        if (this.ownerID != null) {
            output.store(KEY_OWNER, UUIDUtil.CODEC, this.ownerID);
            if (this.ownerName != null) {
                output.store(KEY_OWNER_NAME, ComponentSerialization.CODEC, this.ownerName);
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VaporStillBlockEntity blockEntity) {
        ItemStack fuelStack = blockEntity.brewingItemStacks.getFirst();
        if (blockEntity.fuel <= 0 && fuelStack.getItem() == Items.BLAZE_POWDER) {
            blockEntity.fuel = 20;
            fuelStack.shrink(1);
            blockEntity.setChanged();
        }

        // Periodically sync capabilities from the owning player if they are loaded
        if (blockEntity.ownerID != null && level.getGameTime() % CAPABILITY_UPDATE_INTERVAL == 0) {
            Player owner = level.getPlayerByUUID(blockEntity.ownerID);
            if (owner != null) {
                blockEntity.config.deriveFromHunter(HunterPlayer.get(owner));
                blockEntity.ownerName = owner.getName();
            }
        }

        boolean canBrew  = blockEntity.canBrew();
        boolean isBrewing = blockEntity.brewTime > 0;

        if (isBrewing) {
            blockEntity.brewTime--;
            boolean ingredientsChanged = blockEntity.ingredientID != blockEntity.brewingItemStacks.get(2).getItem() || blockEntity.extraIngredientID != blockEntity.brewingItemStacks.get(1).getItem();
            if (blockEntity.brewTime == 0 && canBrew) {
                blockEntity.brewPotions();
                blockEntity.setChanged();
            } else if (!canBrew || ingredientsChanged) {
                blockEntity.brewTime = 0;
                blockEntity.setChanged();
            }
        } else if (canBrew && blockEntity.fuel > 0) {
            blockEntity.fuel--;
            blockEntity.brewTime = blockEntity.getMaxBrewTime();
            blockEntity.ingredientID = blockEntity.brewingItemStacks.get(2).getItem();
            blockEntity.extraIngredientID = blockEntity.brewingItemStacks.get(1).getItem();
            blockEntity.setChanged();
        }
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.brewingItemStacks;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.brewingItemStacks = items;
    }

    @Override
    public ItemStack getItem(int index) {
        return index >= 0 && index < this.brewingItemStacks.size() ? this.brewingItemStacks.get(index) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index >= 0 && index < this.brewingItemStacks.size()) {
            this.brewingItemStacks.set(index, stack);
        }
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(this.brewingItemStacks, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.brewingItemStacks, index);
    }

    @Override
    public void clearContent() {
        this.brewingItemStacks.clear();
    }

    @Override
    public boolean isEmpty() {
        return this.brewingItemStacks.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.UP) return SLOTS_FOR_UP;
        if (side == Direction.DOWN) return this.config.multiTaskBrewing ? SLOTS_FOR_DOWN_EXTENDED : SLOTS_FOR_DOWN;
        return this.config.multiTaskBrewing ? OUTPUT_SLOTS_EXTENDED : OUTPUT_SLOTS;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return switch (index) {
            case 0 -> stack.getItem() == Items.BLAZE_POWDER;
            case 1 -> VampirismApi.services().extendedBrewingRecipeRegistry().isValidExtraIngredient(stack);
            case 2 -> this.level != null && VampirismApi.services().extendedBrewingRecipeRegistry().isValidIngredient(this.level.potionBrewing(), stack);
            default -> this.level != null && this.level.potionBrewing().isInput(stack) && this.getItem(index).isEmpty();
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return this.canPlaceItem(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index != 1 && index != 2 || stack.getItem() == Items.GLASS_BOTTLE;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return new VaporStillMenu(id, playerInventory, this, this.config.multiTaskBrewing, this.syncedProperties);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("tile.vampirism.vapor_still.display", getOwnerName(), getDefaultName());
    }

    public Component getOwnerName() {
        return this.ownerName != null ? this.ownerName : Component.translatable("text.vampirism.unknown");
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("tile.vampirism.vapor_still");
    }

    @Override
    public boolean canOpen(Player player) {
        if (!super.canOpen(player)) return false;

        HunterPlayer hunter = HunterPlayer.get(player);
        if (hunter.getLevel() <= 0) {
            player.displayClientMessage(FactionRestriction.getFactionRestrictionMessage(ModFactions.HUNTER.get()), true);
            return false;
        }

        if (this.ownerID == null) {
            setOwnerID(player);
            this.config.deriveFromHunter(hunter);
            return true;
        }

        if (this.ownerID.equals(player.getUUID())) {
            this.config.deriveFromHunter(hunter);
            return true;
        }

        player.displayClientMessage(Component.translatable("text.vampirism.vapor_still.other", getOwnerName()), true);
        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) return false;
        return player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    private void brewPotions() {
        ItemStack ingredient = this.brewingItemStacks.get(2);
        ItemStack extraIngredient = this.brewingItemStacks.get(1);
        int[] outputSlots = this.config.multiTaskBrewing ? OUTPUT_SLOTS_EXTENDED : OUTPUT_SLOTS;

        boolean brewed = VampirismApi.services().extendedBrewingRecipeRegistry().brewPotions(this.level, this.brewingItemStacks, ingredient, extraIngredient, this.config, outputSlots, true);

        if (!brewed) {
            NonNullList<ItemStack> copy = NonNullList.of(ItemStack.EMPTY,
                    this.brewingItemStacks.get(3).copy(),
                    this.brewingItemStacks.get(4).copy(),
                    this.brewingItemStacks.get(5).copy(),
                    ingredient.copy(),
                    this.brewingItemStacks.get(0).copy()
            );

            if (!EventHooks.onPotionAttemptBrew(copy)) {
                VampirismApi.services().extendedBrewingRecipeRegistry().brewPotions(this.level, brewingItemStacks, ingredient, extraIngredient, this.config, outputSlots, false);
                copy = NonNullList.of(ItemStack.EMPTY,
                        this.brewingItemStacks.get(3).copy(),
                        this.brewingItemStacks.get(4).copy(),
                        this.brewingItemStacks.get(5).copy(),
                        ingredient.copy(),
                        this.brewingItemStacks.get(0).copy()
                );
                EventHooks.onPotionBrewed(brewingItemStacks);
            }

            this.brewingItemStacks.set(3, copy.get(0));
            this.brewingItemStacks.set(4, copy.get(1));
            this.brewingItemStacks.set(5, copy.get(2));
            ingredient = copy.get(3);
            extraIngredient = copy.get(4);
        }

        BlockPos blockpos = this.getBlockPos();
        ingredient = dropRemainder(ingredient, blockpos);
        extraIngredient = dropRemainder(extraIngredient, blockpos);

        this.brewingItemStacks.set(2, ingredient);
        this.brewingItemStacks.set(1, extraIngredient);

        if (this.level == null) return;

        this.level.playSound(null, blockpos.getX(), blockpos.getY(), blockpos.getZ(), ModSounds.VAPOR_STILL_CRAFTING.get(), SoundSource.BLOCKS, 1f, 1f);
        this.level.levelEvent(LevelEvent.SOUND_BREWING_STAND_BREW, blockpos, 0);
    }

    private ItemStack dropRemainder(ItemStack stack, BlockPos pos) {
        ItemStack remainder = stack.getCraftingRemainder();
        if (remainder.isEmpty()) return stack;
        if (stack.isEmpty()) return remainder;
        if (this.level != null && !this.level.isClientSide()) {
            Containers.dropItemStack(this.level, pos.getX(), pos.getY(), pos.getZ(), remainder);
        }
        return stack;
    }

    private boolean canBrew() {
        ItemStack ingredient = this.brewingItemStacks.get(2);
        if (ingredient.isEmpty()) return false;
        ItemStack extraIngredient = this.brewingItemStacks.get(1);
        int[] outputSlots = this.config.multiTaskBrewing ? OUTPUT_SLOTS_EXTENDED : OUTPUT_SLOTS;
        return VampirismApi.services().extendedBrewingRecipeRegistry().canBrew(this.level, this.brewingItemStacks, ingredient, extraIngredient, this.config, outputSlots);
    }

    public int getMaxBrewTime() {
        return this.config.isSwiftBrewing() ? BREW_TIME_SWIFT : BREW_TIME_NORMAL;
    }

    public boolean isExtended() {
        return this.config.isMultiTaskBrewing();
    }

    public void setOwnerID(Player player) {
        this.ownerID = player.getUUID();
        this.ownerName = player.getName();
        this.setChanged();
    }

    protected static class BrewingCapabilities implements IExtendedBrewingRecipeRegistry.IExtendedBrewingCapabilities {

        boolean durableBrewing;
        boolean concentratedBrewing;
        boolean swiftBrewing;
        boolean masterBrewing;
        boolean efficientBrewing;
        boolean multiTaskBrewing;

        public void deriveFromHunter(IHunterPlayer player) {
            ISkillHandler<IHunterPlayer> skills = player.getSkillHandler();
            durableBrewing = skills.isSkillEnabled(HunterSkills.DURABLE_BREWING) || skills.isSkillEnabled(HunterSkills.CONCENTRATED_DURABLE_BREWING);
            concentratedBrewing = skills.isSkillEnabled(HunterSkills.CONCENTRATED_BREWING) || skills.isSkillEnabled(HunterSkills.CONCENTRATED_DURABLE_BREWING);
            swiftBrewing = skills.isSkillEnabled(HunterSkills.SWIFT_BREWING);
            masterBrewing = skills.isSkillEnabled(HunterSkills.MASTER_BREWER);
            efficientBrewing = skills.isSkillEnabled(HunterSkills.EFFICIENT_BREWING);
            multiTaskBrewing = skills.isSkillEnabled(HunterSkills.MULTITASK_BREWING);
        }

        public void fromByte(byte d) {
            durableBrewing = (d & 0b000001) != 0;
            concentratedBrewing = (d & 0b000010) != 0;
            swiftBrewing = (d & 0b000100) != 0;
            masterBrewing = (d & 0b001000) != 0;
            efficientBrewing = (d & 0b010000) != 0;
            multiTaskBrewing = (d & 0b100000) != 0;
        }

        public byte toByte() {
            byte d = 0;
            if (durableBrewing) d |= 0b000001;
            if (concentratedBrewing) d |= 0b000010;
            if (swiftBrewing) d |= 0b000100;
            if (masterBrewing) d |= 0b001000;
            if (efficientBrewing) d |= 0b010000;
            if (multiTaskBrewing) d |= 0b100000;
            return d;
        }

        public void reset() {
            durableBrewing = concentratedBrewing = swiftBrewing = masterBrewing  = efficientBrewing = multiTaskBrewing = false;
        }

        @Override
        public boolean isConcentratedBrewing() {
            return concentratedBrewing;
        }

        @Override
        public boolean isDurableBrewing() {
            return durableBrewing;
        }

        @Override
        public boolean isEfficientBrewing() {
            return efficientBrewing;
        }

        @Override
        public boolean isMasterBrewing() {
            return masterBrewing;
        }

        @Override
        public boolean isMultiTaskBrewing() {
            return multiTaskBrewing;
        }

        @Override
        public boolean isSwiftBrewing() {
            return swiftBrewing;
        }
    }
}
