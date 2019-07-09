package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.blocks.BlockWeaponTable;
import de.teamlapen.vampirism.inventory.HunterWeaponTableContainer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Gui for the weapon table. Only draws the background and the lava status
 */
@OnlyIn(Dist.CLIENT)
public class GuiHunterWeaponTable extends ContainerScreen {

    private static final ResourceLocation TABLE_GUI_TEXTURES = new ResourceLocation(REFERENCE.MODID, "textures/gui/weapon_table.png");
    private static final ResourceLocation TABLE_GUI_TEXTURES_LAVA = new ResourceLocation(REFERENCE.MODID, "textures/gui/weapon_table_lava.png");
    private static final ResourceLocation TABLE_GUI_TEXTURES_MISSING_LAVA = new ResourceLocation(REFERENCE.MODID, "textures/gui/weapon_table_missing_lava.png");
    private final BlockPos pos;
    private final World world;
    private int lava = 0;
    private boolean isMissingLava = false;

    public GuiHunterWeaponTable(PlayerInventory inventoryPlayer, World world, BlockPos pos) {
        super(new HunterWeaponTableContainer(inventoryPlayer, world, pos));
        this.xSize = 196;
        this.ySize = 191;
        this.pos = pos;
        this.world = world;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

    }

    @Override
    public void tick() {
        super.tick();
        BlockState blockState = this.world.getBlockState(pos);
        if (blockState.getBlock() instanceof BlockWeaponTable) {
            lava = blockState.get(BlockWeaponTable.LAVA);
            if (world.getGameTime() % 10 == 4) {
                isMissingLava = ((HunterWeaponTableContainer) this.inventorySlots).isMissingLava();
            }
        } else {
            this.mc.displayGuiScreen(null);
            this.mc.focusChanged(true);
        }

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.mc.getTextureManager().bindTexture(TABLE_GUI_TEXTURES);
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        if (lava > 0) {
            this.mc.getTextureManager().bindTexture(TABLE_GUI_TEXTURES_LAVA);
            this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        }
        if (isMissingLava) {
            this.mc.getTextureManager().bindTexture(TABLE_GUI_TEXTURES_MISSING_LAVA);
            this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        }
    }
}
