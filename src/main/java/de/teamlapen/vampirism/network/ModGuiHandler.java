package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.blocks.BlockHunterTable;
import de.teamlapen.vampirism.client.gui.*;
import de.teamlapen.vampirism.inventory.HunterTrainerContainer;
import de.teamlapen.vampirism.tileentity.TileAltarInfusion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * Handle GUIs on server and client side
 */
public class ModGuiHandler implements IGuiHandler {
    public final static int ID_ACTION = 0;
    public final static int ID_SKILL = 1;
    public final static int ID_ALTAR_INFUSION = 2;
    public final static int ID_HUNTER_TABLE = 3;
    public final static int ID_HUNTER_TRAINER = 4;
    public final static int ID_REVERT_BACK = 5;

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

        switch (ID) {
            case ID_ACTION:
                return new GuiSelectAction();
            case ID_SKILL:
                return new GuiSkills();
            case ID_ALTAR_INFUSION:
                TileAltarInfusion tile = (TileAltarInfusion) world.getTileEntity(new BlockPos(x, y, z));

                return new GuiAltarInfusion(player.inventory, tile);
            case ID_HUNTER_TABLE:
                return new GuiHunterTable(BlockHunterTable.createInventoryContainer(player, new BlockPos(x, y, z)));
            case ID_HUNTER_TRAINER:
                return new GuiHunterTrainer(new HunterTrainerContainer(player));
            case ID_REVERT_BACK:
                return new GuiRevertBack();
        }
        return null;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == ID_ALTAR_INFUSION) {
            TileAltarInfusion tile = (TileAltarInfusion) world.getTileEntity(new BlockPos(x, y, z));
            return tile.getNewInventoryContainer(player.inventory);
        }
        if (ID == ID_HUNTER_TABLE) {
            return BlockHunterTable.createInventoryContainer(player, new BlockPos(x, y, z));
        }
        if (ID == ID_HUNTER_TRAINER) {
            return new HunterTrainerContainer(player);
        }
        return null;
    }
}
