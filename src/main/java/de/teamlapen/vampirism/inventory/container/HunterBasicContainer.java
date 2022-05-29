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
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

/**
 * Container for interacting with basic hunters to level up as a hunter
 */
public class HunterBasicContainer extends InventoryContainer {
    private static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[]{new SelectorInfo(ModItems.VAMPIRE_BLOOD_BOTTLE.get(), 27, 32)};
    private final IHunterPlayer player;
    @Nullable
    private final BasicHunterEntity entity;

    @Deprecated
    public HunterBasicContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, null);
    }

    public HunterBasicContainer(int id, PlayerInventory playerInventory, @Nullable BasicHunterEntity hunter) {
        super(ModContainer.HUNTER_BASIC.get(), id, playerInventory, hunter == null ? IWorldPosCallable.NULL : IWorldPosCallable.create(hunter.level, hunter.blockPosition()), new Inventory(SELECTOR_INFOS.length), SELECTOR_INFOS);
        player = HunterPlayer.get(playerInventory.player);
        this.addPlayerSlots(playerInventory);
        this.entity = hunter;
    }

    /**
     * @return The number of missing vampire blood bottles to level up. -1 if wrong level
     */
    public int getMissingCount() {
        int targetLevel = player.getLevel() + 1;
        ItemStack blood = inventory.getItem(0);

        HunterLevelingConf conf = HunterLevelingConf.instance();
        if (!conf.isLevelValidForBasicHunter(targetLevel)) return -1;
        int required = conf.getVampireBloodCountForBasicHunter(targetLevel);
        return (blood.isEmpty() || !blood.getItem().equals(ModItems.VAMPIRE_BLOOD_BOTTLE.get())) ? required : Math.max(0, required - blood.getCount());
    }

    public boolean canLevelUp() {
        return getMissingCount() == 0;
    }

    public void onLevelUpClicked() {
        if (!canLevelUp()) return;
        int target = player.getLevel() + 1;
        inventory.removeItem(0, HunterLevelingConf.instance().getVampireBloodCountForBasicHunter(target));
        FactionPlayerHandler.getOpt(player.getRepresentingPlayer()).ifPresent(h -> h.setFactionLevel(VReference.HUNTER_FACTION, target));
        player.getRepresentingPlayer().displayClientMessage(new TranslationTextComponent("container.vampirism.basic_hunter.levelup"), false);
        player.getRepresentingPlayer().closeContainer();

    }

    @Override
    public void removed(PlayerEntity playerIn) {
        super.removed(playerIn);
        if (!playerIn.getCommandSenderWorld().isClientSide) {
            this.clearContainer(playerIn, playerIn.getCommandSenderWorld(), inventory);
        }
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        if (entity == null) return false;
        return new Vector3d(playerIn.getX(), playerIn.getY(), playerIn.getZ()).distanceTo(new Vector3d(entity.getX(), entity.getY(), entity.getZ())) < 5;
    }

}
