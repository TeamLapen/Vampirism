package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.lib.lib.inventory.InventorySlot;
import de.teamlapen.lib.lib.inventory.SimpleInventory;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Container for interacting with basic hunters to level up as a hunter
 */
public class HunterBasicContainer extends InventoryContainer {

    private final IHunterPlayer player;

    public HunterBasicContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, IWorldPosCallable.DUMMY);

    }

    public HunterBasicContainer(int id, PlayerInventory playerInventory, IWorldPosCallable worldPosIn) {
        super(id, playerInventory, ModContainer.hunter_basic, new HunterBasicInventory(), worldPosIn);
        player = HunterPlayer.get(playerInventory.player);

    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    public boolean canLevelUp() {
        return getMissingCount() == 0;
    }

    /**
     * @return The number of missing vampire blood bottles to level up. -1 if wrong level
     */
    public int getMissingCount() {
        int targetLevel = player.getLevel() + 1;
        ItemStack blood = this.inventory.getStackInSlot(0);

        HunterLevelingConf conf = HunterLevelingConf.instance();
        if (!conf.isLevelValidForBasicHunter(targetLevel)) return -1;
        int required = conf.getVampireBloodCountForBasicHunter(targetLevel);
        return (blood.isEmpty() || !blood.getItem().equals(ModItems.vampire_blood_bottle)) ? required : Math.max(0, required - blood.getCount());
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        if (!playerIn.getEntityWorld().isRemote) {
            for (int i = 0; i < this.inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = this.inventory.removeStackFromSlot(i);

                if (!itemstack.isEmpty()) {
                    playerIn.dropItem(itemstack, false);
                }
            }
        }
    }

    public void onLevelUpClicked() {
        if (!canLevelUp()) return;
        int target = player.getLevel() + 1;
        this.inventory.decrStackSize(0, HunterLevelingConf.instance().getVampireBloodCountForBasicHunter(target));
        FactionPlayerHandler.get(player.getRepresentingPlayer()).setFactionLevel(VReference.HUNTER_FACTION, target);
        player.getRepresentingPlayer().sendMessage(new TranslationTextComponent("text.vampirism.basic_hunter.levelup"));
        player.getRepresentingPlayer().closeScreen();

    }

    protected static class HunterBasicInventory extends SimpleInventory {
        protected HunterBasicInventory() {
            super(NonNullList.from(new InventorySlot(ModItems.vampire_blood_bottle, 27, 32)));
        }
    }
}
