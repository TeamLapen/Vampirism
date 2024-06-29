package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.inventory.InventoryContainerMenu;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.core.ModFactions;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModMenus;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import de.teamlapen.vampirism.entity.player.hunter.HunterLeveling;
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
    private static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[] {new SelectorInfo(ModItems.VAMPIRE_BLOOD_BOTTLE.get(), 27, 32)};
    private final @NotNull IHunterPlayer player;
    @Nullable
    private final BasicHunterEntity entity;

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public HunterBasicMenu(int id, @NotNull Inventory playerInventory) {
        this(id, playerInventory, null);
    }

    public HunterBasicMenu(int id, @NotNull Inventory playerInventory, @Nullable BasicHunterEntity hunter) {
        super(ModMenus.HUNTER_BASIC.get(), id, playerInventory, hunter == null ? ContainerLevelAccess.NULL : ContainerLevelAccess.create(hunter.level(), hunter.blockPosition()), new SimpleContainer(SELECTOR_INFOS.length), SELECTOR_INFOS);
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

        return HunterLeveling.getBasicHunterRequirement(targetLevel).map(req -> {
            int required = req.vampireBloodAmount();
            return (blood.isEmpty() || !blood.getItem().equals(ModItems.VAMPIRE_BLOOD_BOTTLE.get())) ? required : Math.max(0, required - blood.getCount());
        }).orElse(-1);
    }

    public boolean canLevelUp() {
        return getMissingCount() == 0;
    }

    public void onLevelUpClicked() {
        if (!canLevelUp()) return;
        int target = player.getLevel() + 1;
        HunterLeveling.getBasicHunterRequirement(target).ifPresent(req -> {
            inventory.removeItem(0, req.vampireBloodAmount());
            Player player1 = player.asEntity();
            FactionPlayerHandler.get(player1).setFactionLevel(ModFactions.HUNTER, target);
            player1.displayClientMessage(Component.translatable("container.vampirism.basic_hunter.levelup"), false);
            player1.closeContainer();
        });


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
