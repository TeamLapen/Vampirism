package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.blocks.BlockWeaponTable;
import de.teamlapen.vampirism.inventory.WeaponTableContainer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Gui for the weapon table. Only draws the background and the lava status
 */
public class GuiWeaponTable extends GuiContainer {

    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(REFERENCE.MODID, "textures/gui/weapon_table.png");
    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES_LAVA = new ResourceLocation(REFERENCE.MODID, "textures/gui/weapon_table_lava.png");

    private final BlockPos pos;
    private final World world;
    private int lava = 0;

    public GuiWeaponTable(InventoryPlayer inventoryPlayer, World world, BlockPos pos) {
        super(new WeaponTableContainer(inventoryPlayer, world, pos));
        this.xSize = 196;
        this.ySize = 191;
        this.pos = pos;
        this.world = world;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        IBlockState blockState = this.world.getBlockState(pos);
        if (blockState.getBlock() instanceof BlockWeaponTable) {
            lava = blockState.getValue(BlockWeaponTable.LAVA);
        }

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(lava > 0 ? CRAFTING_TABLE_GUI_TEXTURES_LAVA : CRAFTING_TABLE_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }
}
