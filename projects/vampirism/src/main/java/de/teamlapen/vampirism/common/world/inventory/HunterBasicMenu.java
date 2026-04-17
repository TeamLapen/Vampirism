package de.teamlapen.vampirism.common.world.inventory;

import de.teamlapen.faction.api.factions.LevelingChange;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModMenus;
import de.teamlapen.vampirism.common.world.entity.hunter.BasicHunterEntity;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterLeveling;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Container for interacting with basic hunters to level up as a hunter
 */
public class HunterBasicMenu extends ItemCombinerMenu {
    private final IHunterPlayer player;
    @Nullable
    private final BasicHunterEntity entity;

    private LevelingState canLevelUp;
    private int requiredBloodBottles;

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public HunterBasicMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, null);
    }

    public HunterBasicMenu(int id, Inventory playerInventory, @Nullable BasicHunterEntity hunter) {
        super(ModMenus.HUNTER_BASIC.get(), id, playerInventory, hunter == null ? ContainerLevelAccess.NULL : ContainerLevelAccess.create(hunter.level(), hunter.blockPosition()), createInputSlotDefinitions(playerInventory.player));
        this.player = HunterPlayer.get(playerInventory.player);
        this.entity = hunter;
    }

    protected static ItemCombinerMenuSlotDefinition createInputSlotDefinitions(Player player) {
        return ModifiedItemCombinerMenuSlotDefinition.createWithoutResult()
                .withSlot(0, 27, 32, stack -> stack.is(ModItems.VAMPIRE_BLOOD_BOTTLE.get()))
                .build();
    }

    @Override
    protected boolean mayPickup(Player player, boolean hasStack) {
        return true;
    }

    @Override
    protected void onTake(Player player, ItemStack stack) {
        int targetLevel = this.player.getLevel() + 1;
        HunterLeveling.getBasicHunterRequirement(targetLevel).ifPresent(req -> {
            int required = req.vampireBloodAmount();
            getSlot(0).remove(required);
            FactionPlayerHandler.get(player).setFaction(LevelingChange.builder().faction(ModFactions.HUNTER).level(targetLevel));
            player.sendOverlayMessage(Component.translatable("gui.vampirism.hunter.level_up"));
            player.closeContainer();
        });
    }

    public void onLevelUpClicked(Player player) {
        this.onTake(player, ItemStack.EMPTY);
    }

    @Override
    protected boolean isValidBlock(BlockState state) {
        return true;
    }

    @Override
    public void createResult() {
        this.canLevelUp = HunterLeveling.getBasicHunterRequirement(this.player.getLevel() + 1)
                .map(HunterLeveling.BasicHunterRequirement::vampireBloodAmount)
                .map(x -> {
                    var state = getSlot(0).getItem().getCount() >= x ? LevelingState.CAN_LEVEL_UP : LevelingState.NEED_BLOOD;
                    this.requiredBloodBottles = x;
                    return state;
                })
                .orElse(LevelingState.WRONG_LEVEL);
    }

    public LevelingState canLevelUp() {
        return this.canLevelUp;
    }
    public int requiredBloodBottles() {
        return this.requiredBloodBottles;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        if (this.entity == null) return false;
        return new Vec3(playerIn.getX(), playerIn.getY(), playerIn.getZ()).distanceTo(this.entity.position()) < 5;
    }

    public enum LevelingState {
        NEED_BLOOD,
        CAN_LEVEL_UP,
        WRONG_LEVEL,
    }

}
