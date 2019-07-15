package de.teamlapen.vampirism.world.gen.structure;

import de.teamlapen.vampirism.util.SRGNAMES;
import net.minecraft.block.ChestBlock;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class VampirismTemplate extends Template {

    private static final Logger LOGGER = LogManager.getLogger(VampirismTemplate.class);
    private ResourceLocation lootTable;

    @Override
    public boolean addBlocksToWorld(IWorld worldIn, BlockPos pos, PlacementSettings placementIn, int flags) {
        if (!super.addBlocksToWorld(worldIn, pos, placementIn, flags)) {
            return false;
        }
        if (lootTable != null) {
            boolean flag = false;
            List<BlockInfo> blocks = ObfuscationReflectionHelper.getPrivateValue(Template.class, this, SRGNAMES.Template_blocks);
            for (BlockInfo b : blocks) {
                if (b.state.getBlock() instanceof ChestBlock) {
                    TileEntity t = worldIn.getTileEntity(b.pos);
                    if (t instanceof ChestTileEntity) {
                        ((ChestTileEntity) t).setLootTable(lootTable, worldIn.getSeed());
                        flag = true;
                    }
                }
            }
            if (!flag) {
                LOGGER.warn("Loot Table ({}) specified but no chest found", lootTable);
            }
            return true;
        }
        return false;
    }

    public void setLootTable(ResourceLocation lootTable) {
        this.lootTable = lootTable;
    }
}
