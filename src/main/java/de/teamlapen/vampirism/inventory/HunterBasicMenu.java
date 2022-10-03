package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.inventory.InventoryContainerMenu;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import de.teamlapen.vampirism.entity.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Container for interacting with basic hunters to level up as a hunter
 */
public class HunterBasicMenu extends InventoryContainerMenu {
    private static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[]{new SelectorInfo(ModItems.VAMPIRE_BLOOD_BOTTLE.get(), 27, 32)};
    private final @NotNull IHunterPlayer player;
    @Nullable
    private final BasicHunterEntity entity;

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public HunterBasicMenu(int id, @NotNull Inventory playerInventory) {
        this(id, playerInventory, null);
    }

    public HunterBasicMenu(int id, @NotNull Inventory playerInventory, @Nullable BasicHunterEntity hunter) {
        super(ModContainer.HUNTER_BASIC.get(), id, playerInventory, hunter == null ? ContainerLevelAccess.NULL : ContainerLevelAccess.create(hunter.level, hunter.blockPosition()), new SimpleContainer(SELECTOR_INFOS.length), SELECTOR_INFOS);
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
        player.getRepresentingPlayer().displayClientMessage(Component.translatable("container.vampirism.basic_hunter.levelup"), false);
        player.getRepresentingPlayer().closeContainer();

    }

    @Override
    public void removed(@NotNull Player playerIn) {
        super.removed(playerIn);
        if (!playerIn.getCommandSenderWorld().isClientSide) {
            this.clearContainer(playerIn, inventory);
        }
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        if (entity == null) return false;
        return new Vec3(playerIn.getX(), playerIn.getY(), playerIn.getZ()).distanceTo(new Vec3(entity.getX(), entity.getY(), entity.getZ())) < 5;
    }

}
