package de.teamlapen.vampirism.blockentity.diffuser;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.datamaps.IFogDiffuserFuel;
import de.teamlapen.vampirism.core.ModDataMaps;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.inventory.diffuser.DiffuserMenu;
import de.teamlapen.vampirism.inventory.diffuser.FogDiffuserMenu;
import de.teamlapen.vampirism.world.fog.FogLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FogDiffuserBlockEntity extends DiffuserBlockEntity {

    public FogDiffuserBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModTiles.FOG_DIFFUSER.get(), pPos, pBlockState);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("block.vampirism.fog_diffuser");
    }

    @Override
    protected @NotNull DiffuserMenu createMenu(int pContainerId, @NotNull Inventory pInventory, @NotNull LockDataHolder lockData) {
        return new FogDiffuserMenu(pContainerId, pInventory, this, this.dataAccess, lockData);
    }

    @Override
    protected int getBurnDuration(ItemStack itemStack) {
        IFogDiffuserFuel data = itemStack.getItemHolder().getData(ModDataMaps.FOG_DIFFUSER_FUEL_MAP);
        return data != null ? data.burnDuration() : 0;
    }

    @Override
    protected void activateEffect(Level level, BlockPos blockPos, BlockState blockState) {
        var range = getRange();
        FogLevel.getOpt(level).ifPresent(l -> l.updateArtificialFogBoundingBox(blockPos, new AABB(Vec3.atLowerCornerOf(this.worldPosition.offset(-range, -range, -range)), Vec3.atLowerCornerWithOffset(this.worldPosition.offset(range, range, range), 1, 1, 1))));
    }

    @Override
    public boolean canOpen(@NotNull Player pPlayer) {
        return tryAccess(pPlayer, VReference.VAMPIRE_FACTION, getName())  && super.canOpen(pPlayer);
    }

    protected int getRange() {
        return (int) (2.5 * 16);
    }

    @Override
    public void deactivateEffect(Level level, BlockPos blockPos, BlockState blockState) {
        FogLevel.getOpt(level).ifPresent(l -> l.updateArtificialFogBoundingBox(blockPos, null));
    }
}
