package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.inventory.HunterBasicContainer;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;


@SideOnly(Side.CLIENT)
public class GuiHunterBasic extends GuiContainer {
    private static final ResourceLocation guiTexture = new ResourceLocation(REFERENCE.MODID, "textures/gui/hunter_basic.png");

    private GuiButton buttonLevelup;
    private HunterBasicContainer container;
    private int missing = 0;
    private int timer = 0;

    public GuiHunterBasic(EntityPlayer player) {
        super(new HunterBasicContainer(player.inventory));
        this.container = (HunterBasicContainer) inventorySlots;
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        String name = I18n.format("text.vampirism.level_up");
        buttonList.add(buttonLevelup = new GuiButton(1, i + 37, j + 55, 100, 20, name));
        buttonLevelup.enabled = false;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        timer = (timer + 1) % 10;
        if (timer == 0) {
            this.missing = container.getMissingCount();
            this.buttonLevelup.enabled = missing == 0;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.BASICHUNTERLEVELUP, ""));
        } else {
            super.actionPerformed(button);

        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(guiTexture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        String text = null;
        if (missing == 0) {
            text = UtilLib.translate("text.vampirism.basic_hunter.i_will_train_you");
        } else if (missing > 0) {
            text = UtilLib.translateFormatted("text.vampirism.basic_hunter.pay_n_vampire_blood_more", missing);
        }
        if (text != null) {
            this.fontRendererObj.drawSplitString(text, 50, 12, 120, 0);
        }
    }
}
