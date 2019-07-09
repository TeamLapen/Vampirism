package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.lib.lib.inventory.InventorySlot;
import de.teamlapen.lib.lib.inventory.SimpleInventory;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.items.HunterIntelItem;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;

/**
 * Container which handles hunter levelup at an hunter trainer
 */
public class HunterTrainerContainer extends InventoryContainer {
    private final PlayerEntity player;
    private boolean changed = false;
    private ItemStack missing = ItemStack.EMPTY;

    public HunterTrainerContainer(PlayerEntity player) {
        super(player.inventory, new HunterTrainerInventory());
        ((HunterTrainerInventory) this.tile).setChangeListener(this);
        this.player = player;
        this.onInventoryChanged();
    }

    /**
     * @return If the player can levelup with the given items
     */
    public boolean canLevelup() {
        int targetLevel = FactionPlayerHandler.get(player).getCurrentLevel(VReference.HUNTER_FACTION) + 1;
        HunterLevelingConf levelingConf = HunterLevelingConf.instance();
        if (!levelingConf.isLevelValidForTrainer(targetLevel)) return false;
        int[] req = levelingConf.getItemRequirementsForTrainer(targetLevel);
        int level = levelingConf.getHunterIntelMetaForLevel(targetLevel);
        missing = InventoryHelper.checkItems(tile, new Item[]{Items.IRON_INGOT, Items.GOLD_INGOT, HunterIntelItem.getIntelForLevel(level)}, new int[]{req[0], req[1], 1});
        return missing.isEmpty();
    }

    public HunterTrainerInventory getHunterTrainerInventory() {
        return (HunterTrainerInventory) tile;
    }

    /**
     * @return The missing Itemstack or null if nothing is missing
     */
    public ItemStack getMissingItems() {
        return missing;
    }

    /**
     * @return If the inventory has changed since the last call
     */
    public boolean hasChanged() {
        if (changed) {
            changed = false;
            return true;
        }
        return false;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        if (!playerIn.getEntityWorld().isRemote) {
            for (int i = 0; i < tile.getSizeInventory(); ++i) {
                ItemStack itemstack = this.tile.removeStackFromSlot(i);

                if (!itemstack.isEmpty()) {
                    playerIn.dropItem(itemstack, false);
                }
            }
        }
    }

    @Override
    public void onInventoryChanged() {
        changed = true;
    }

    /**
     * Called via input packet, when the player clicks the levelup button.
     */
    public void onLevelupClicked() {
        if (canLevelup()) {
            int old = FactionPlayerHandler.get(player).getCurrentLevel(VReference.HUNTER_FACTION);
            FactionPlayerHandler.get(player).setFactionLevel(VReference.HUNTER_FACTION, old + 1);
            int[] req = HunterLevelingConf.instance().getItemRequirementsForTrainer(old + 1);
            InventoryHelper.removeItems(tile, new int[]{req[0], req[1], 1});
            player.addPotionEffect(new EffectInstance(ModPotions.saturation, 400, 2));
        }
    }

    /**
     * Simple inventory for the Hunter Trainer
     */
    public static class HunterTrainerInventory extends SimpleInventory {
        public HunterTrainerInventory() {
            super(new InventorySlot[]{new InventorySlot(Items.IRON_INGOT, 27, 26), new InventorySlot(Items.GOLD_INGOT, 57, 26), new InventorySlot(ModItems.hunter_intel_0, 86, 26)});
        }

        @Override
        public ITextComponent getName() {
            return new StringTextComponent("entity.vampirism." + ModEntities.hunter_trainer.getName() + ".name");
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Nullable
        @Override
        public ITextComponent getCustomName() {
            return null;//TODO not null?
        }
    }
}
