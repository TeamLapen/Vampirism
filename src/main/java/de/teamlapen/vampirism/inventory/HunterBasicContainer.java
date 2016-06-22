package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.lib.lib.inventory.InventorySlot;
import de.teamlapen.lib.lib.inventory.SimpleInventory;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

/**
 * Container for interacting with basic hunters to level up as a hunter
 */
public class HunterBasicContainer extends InventoryContainer {

    private final IHunterPlayer player;

    public HunterBasicContainer(InventoryPlayer invPlayer) {
        super(invPlayer, new HunterBasicInventory());
        player = HunterPlayer.get(invPlayer.player);

    }

    public boolean canLevelUp() {
        return getMissingCount() == 0;
    }

    /**
     * @return The number of missing vampire blood bottles to level up. -1 if wrong level
     */
    public int getMissingCount() {
        int targetLevel = player.getLevel() + 1;
        ItemStack blood = this.tile.getStackInSlot(0);

        HunterLevelingConf conf = HunterLevelingConf.instance();
        if (!conf.isLevelValidForBasicHunter(targetLevel)) return -1;
        int required = conf.getVampireBloodCountForBasicHunter(targetLevel);
        return (blood == null || !blood.getItem().equals(ModItems.vampireBlood)) ? required : Math.max(0, required - blood.stackSize);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (!playerIn.worldObj.isRemote) {
            for (int i = 0; i < 1; ++i) {
                ItemStack itemstack = this.tile.removeStackFromSlot(i);

                if (itemstack != null) {
                    playerIn.dropItem(itemstack, false);
                }
            }
        }
    }

    public void onLevelUpClicked() {
        if (!canLevelUp()) return;
        int target = player.getLevel() + 1;
        this.tile.decrStackSize(0, HunterLevelingConf.instance().getVampireBloodCountForBasicHunter(target));
        FactionPlayerHandler.get(player.getRepresentingPlayer()).setFactionLevel(VReference.HUNTER_FACTION, target);

    }

    private static class HunterBasicInventory extends SimpleInventory {


        public HunterBasicInventory() {
            super(new InventorySlot[]{new InventorySlot(ModItems.vampireBlood, 27, 32)});
        }

        @Override
        public String getName() {
            return "entity." + ModEntities.BASIC_HUNTER_NAME + ".name";
        }
    }
}
