package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.inventory.InventoryContainerMenu;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.hunter.HunterTrainerEntity;
import de.teamlapen.vampirism.entity.player.hunter.HunterLeveling;
import de.teamlapen.vampirism.items.HunterIntelItem;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Container which handles hunter levelup at a hunter trainer
 */
public class HunterTrainerMenu extends InventoryContainerMenu implements ContainerListener {
    private static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[]{new SelectorInfo(Items.IRON_INGOT, 27, 26), new SelectorInfo(Items.GOLD_INGOT, 57, 26), new SelectorInfo(ModTags.Items.HUNTER_INTEL, 86, 26)};
    private final @NotNull Player player;
    @Nullable
    private final HunterTrainerEntity entity;
    private boolean changed = false;
    private @NotNull ItemStack missing = ItemStack.EMPTY;

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public HunterTrainerMenu(int id, @NotNull Inventory playerInventory) {
        this(id, playerInventory, null);
    }

    public HunterTrainerMenu(int id, @NotNull Inventory playerInventory, @Nullable HunterTrainerEntity trainer) {
        super(ModContainer.HUNTER_TRAINER.get(), id, playerInventory, trainer == null ? ContainerLevelAccess.NULL : ContainerLevelAccess.create(trainer.level, trainer.blockPosition()), new SimpleContainer(SELECTOR_INFOS.length), SELECTOR_INFOS);
        ((SimpleContainer) this.inventory).addListener(this);
        this.player = playerInventory.player;
        this.addPlayerSlots(playerInventory);
        this.entity = trainer;
    }

    @Override
    public void containerChanged(@NotNull Container iInventory) {
        changed = true;
    }

    /**
     * @return If the player can levelup with the given tileInventory
     */
    public boolean canLevelup() {
        int targetLevel = FactionPlayerHandler.getOpt(player).map(h -> h.getCurrentLevel(VReference.HUNTER_FACTION)).orElse(0) + 1;
        return HunterLeveling.getTrainerRequirement(targetLevel).map(requirement -> {
            missing = InventoryHelper.checkItems(inventory, new Item[]{Items.IRON_INGOT, Items.GOLD_INGOT, requirement.tableRequirement().intel().get()}, new int[]{requirement.iron(), requirement.gold(), 1}, (supplied, required) -> supplied.equals(required) || (supplied instanceof HunterIntelItem && required instanceof HunterIntelItem && ((HunterIntelItem) supplied).getLevel() >= ((HunterIntelItem) required).getLevel()));
            return missing.isEmpty();
        }).orElse(false);
    }

    /**
     * @return The missing Itemstack or null if nothing is missing
     */
    public ItemStack getMissingItems() {
        return this.missing;
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

    /**
     * Called via input packet, when the player clicks the levelup button.
     */
    public void onLevelupClicked() {
        if (canLevelup()) {
            int old = FactionPlayerHandler.get(player).getCurrentLevel(VReference.HUNTER_FACTION);
            FactionPlayerHandler.get(player).setFactionLevel(VReference.HUNTER_FACTION, old + 1);
            var req = HunterLeveling.getTrainerRequirement(old + 1).orElseThrow();
            InventoryHelper.removeItems(inventory, new int[]{req.iron(), req.gold(), 1});
            player.addEffect(new MobEffectInstance(ModEffects.SATURATION.get(), 400, 2));
            changed = true;
        }
    }

    @Override
    public void removed(@NotNull Player playerIn) {
        super.removed(playerIn);
        if (!playerIn.getCommandSenderWorld().isClientSide) {
            clearContainer(playerIn, inventory);
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (entity == null) return false;
        return new Vec3(player.getX(), player.getY(), player.getZ()).distanceTo(new Vec3(entity.getX(), entity.getY(), entity.getZ())) < 5;
    }

}
