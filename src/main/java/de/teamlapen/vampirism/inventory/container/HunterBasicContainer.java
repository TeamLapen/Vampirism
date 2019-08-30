package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

/**
 * Container for interacting with basic hunters to level up as a hunter
 */
public class HunterBasicContainer extends InventoryContainer {
    private static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[]{new SelectorInfo(Ingredient.fromItems(ModItems.vampire_blood_bottle), 27, 32)};
    private final IHunterPlayer player;
    @Nullable
    private final BasicHunterEntity entity;

    @Deprecated
    public HunterBasicContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, null);
    }

    public HunterBasicContainer(int id, PlayerInventory playerInventory, @Nullable BasicHunterEntity hunter) {
        super(ModContainer.hunter_basic, id, hunter == null ? IWorldPosCallable.DUMMY : IWorldPosCallable.of(hunter.world, hunter.getPosition()), SELECTOR_INFOS);
        player = HunterPlayer.get(playerInventory.player);
        this.addPlayerSlots(playerInventory);
        this.entity = hunter;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        if (entity == null) return false;
        return new Vec3d(playerIn.posX, playerIn.posY, playerIn.posZ).distanceTo(new Vec3d(entity.posX, entity.posY, entity.posZ)) < 5;
    }

    public boolean canLevelUp() {
        return getMissingCount() == 0;
    }

    /**
     * @return The number of missing vampire blood bottles to level up. -1 if wrong level
     */
    public int getMissingCount() {
        int targetLevel = player.getLevel() + 1;
        ItemStack blood = this.itemHandler.getStackInSlot(0);

        HunterLevelingConf conf = HunterLevelingConf.instance();
        if (!conf.isLevelValidForBasicHunter(targetLevel)) return -1;
        int required = conf.getVampireBloodCountForBasicHunter(targetLevel);
        return (blood.isEmpty() || !blood.getItem().equals(ModItems.vampire_blood_bottle)) ? required : Math.max(0, required - blood.getCount());
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        if (!playerIn.getEntityWorld().isRemote) {
            this.clearContainer(playerIn);
        }
    }

    public void onLevelUpClicked() {
        if (!canLevelUp()) return;
        int target = player.getLevel() + 1;
        itemHandler.extractItem(0, HunterLevelingConf.instance().getVampireBloodCountForBasicHunter(target), false);
        FactionPlayerHandler.get(player.getRepresentingPlayer()).setFactionLevel(VReference.HUNTER_FACTION, target);
        player.getRepresentingPlayer().sendMessage(new TranslationTextComponent("container.vampirism.basic_hunter.levelup"));
        player.getRepresentingPlayer().closeScreen();

    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerEntity, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            result = slotStack.copy();
            if (index >= 1) {
                if (index < 27) {
                    if (!this.mergeItemStack(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    } else if (this.mergeItemStack(slotStack, 27, 36, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.mergeItemStack(slotStack, 0, 27, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.mergeItemStack(slotStack, 1, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (slotStack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerEntity, slotStack);
        }

        return result;
    }
}
