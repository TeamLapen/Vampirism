package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.inventory.BloodPotionTableContainer;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.List;


public class GuiBloodPotionTable extends GuiContainer {

    private final ResourceLocation TABLE_GUI_TEXTURES = new ResourceLocation(REFERENCE.MODID, "textures/gui/blood_potion_table.png");
    private final BloodPotionTableContainer container;
    private GuiButton craftBtn;

    public GuiBloodPotionTable(InventoryPlayer playerInv, BlockPos pos, World world) {
        super(new BloodPotionTableContainer(playerInv, pos, world));
        this.container = (BloodPotionTableContainer) inventorySlots;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        List<String> hints = container.getLocalizedCraftingHint();
        if (hints != null) {
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            for (String hint : hints) {
                this.fontRendererObj.drawSplitString(hint, i + 5, j + 28, 92, java.awt.Color.WHITE.getRGB());
                j += this.fontRendererObj.splitStringWidth(hint, 92);
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(this.craftBtn = new GuiButton(0, this.width / 2 - 77, this.height / 2 - 78, 80, 20, UtilLib.translateToLocal("gui.vampirism.blood_potion_table.create")));
        craftBtn.enabled = false;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.craftBtn.enabled = container.canCurrentlyStartCrafting();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 0) {
                VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.CRAFT_BLOOD_POTION, ""));
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.mc.getTextureManager().bindTexture(TABLE_GUI_TEXTURES);
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);


        if (container.getCraftingPercentage() > 0) {
            int j1 = (int) (28.0F * container.getCraftingPercentage());

            if (j1 > 0) {
                this.drawTexturedModalRect(i + 145, j + 23, 176, 0, 9, j1);

            }

        }

    }
}
