package de.teamlapen.vampirism.world.gen.structure;

import de.teamlapen.vampirism.util.SRGNAMES;
import net.minecraft.block.BlockChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.template.ITemplateProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

public class VampirismTemplate extends Template {

    private static final Logger LOGGER = LogManager.getLogger(VampirismTemplate.class);
    private ResourceLocation lootTable;


    @Override
    public void addBlocksToWorld(World worldIn, BlockPos p_189960_2_, @Nullable ITemplateProcessor templateProcessor, PlacementSettings placementIn, int flags) {
        super.addBlocksToWorld(worldIn, p_189960_2_, templateProcessor, placementIn, flags);
        if (lootTable != null) {
            boolean flag = false;
            List<BlockInfo> blocks = ReflectionHelper.getPrivateValue(Template.class, this, "blocks", SRGNAMES.Template_blocks);
            for (BlockInfo b : blocks) {
                if (b.blockState.getBlock() instanceof BlockChest) {
                    TileEntity t = worldIn.getTileEntity(b.pos);
                    if (t instanceof TileEntityChest) {
                        ((TileEntityChest) t).setLootTable(lootTable, worldIn.getSeed());
                        flag = true;
                    }
                }
            }
            if (!flag) {
                LOGGER.warn("Loot Table (%s) specified but no chest found", lootTable);
            }
        }
    }

    public void setLootTable(ResourceLocation lootTable) {
        this.lootTable = lootTable;
    }
}
