package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.hunter.HunterLevelingConf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

/**
 * Container which handles hunter levelup at an hunter trainer
 */
public class HunterTrainerContainer extends InventoryContainer {
    private final static Item[] items = new Item[]{Items.iron_ingot, Items.gold_ingot, ModItems.hunterIntel};
    private final EntityPlayer player;
    private boolean changed = false;
    private ItemStack missing;

    public HunterTrainerContainer(EntityPlayer player) {
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
        missing = InventoryHelper.checkItems(tile, items, new int[]{req[0], req[1], 1}, new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, levelingConf.getHunterIntelMetaForLevel(targetLevel) == 0 ? Integer.MIN_VALUE : -levelingConf.getHunterIntelMetaForLevel(targetLevel)});
        return missing == null;
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
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (!playerIn.worldObj.isRemote) {
            for (int i = 0; i < 3; ++i) {
                ItemStack itemstack = this.tile.removeStackFromSlot(i);

                if (itemstack != null) {
                    playerIn.dropPlayerItemWithRandomChoice(itemstack, false);
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
            player.addPotionEffect(new PotionEffect(MobEffects.saturation, 400, 2));
        }
    }
}
