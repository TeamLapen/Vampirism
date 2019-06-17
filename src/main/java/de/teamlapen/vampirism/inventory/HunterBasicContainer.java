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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;

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
        return (blood.isEmpty() || !blood.getItem().equals(ModItems.vampire_blood_bottle)) ? required : Math.max(0, required - blood.getCount());
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (!playerIn.getEntityWorld().isRemote) {
            for (int i = 0; i < this.tile.getSizeInventory(); ++i) {
                ItemStack itemstack = this.tile.removeStackFromSlot(i);

                if (!itemstack.isEmpty()) {
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
        player.getRepresentingPlayer().sendMessage(new TextComponentTranslation("text.vampirism.basic_hunter.levelup"));
        player.getRepresentingPlayer().closeScreen();

    }

    private static class HunterBasicInventory extends SimpleInventory {


        public HunterBasicInventory() {
            super(new InventorySlot[]{new InventorySlot(ModItems.vampire_blood_bottle, 27, 32)});
        }

        @Override
        public ITextComponent getName() {
            return new TextComponentString("entity.vampirism." + ModEntities.BASIC_HUNTER_NAME + ".name");
        }

        @Nullable
        @Override
        public ITextComponent getCustomName() {
            return null;//TODO not null?
        }
    }
}
