package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class AppearanceScreen extends Screen {

    private static final ITextComponent NAME = new TranslationTextComponent("gui.vampirism.appearance");
    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/appearance.png");
    private static final String[] descEye;
    private static final String[] descFang;

    static {
        descEye = new String[REFERENCE.EYE_TYPE_COUNT];
        descFang = new String[REFERENCE.FANG_TYPE_COUNT];
        descEye[0] = "None";
        for (int i = 1; i < descEye.length; i++) {
            descEye[i] = "Type " + i;
        }
        descFang[0] = "None";
        for (int i = 1; i < descFang.length; i++) {
            descFang[i] = "Type " + i;
        }
    }

    protected final int xSize = 256;
    protected final int ySize = 177;
    protected int guiLeft;
    protected int guiTop;
    private float[] color;


    public AppearanceScreen() {
        super(NAME);
    }

    @Override
    protected void init() {
        this.color = FactionPlayerHandler.getOpt(Minecraft.getInstance().player).map(FactionPlayerHandler::getCurrentFaction).map(IPlayableFaction::getColor).orElse(Color.gray).getRGBColorComponents(null);

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.addButton(new GuiButtonExt(this.guiLeft + 100, this.guiTop + 152, 120, 20, UtilLib.translate("gui.vampirism.appearance.skill_order"), (context) -> {
            Color color = FactionPlayerHandler.getOpt(Minecraft.getInstance().player).map(FactionPlayerHandler::getCurrentFaction).map(IPlayableFaction::getColor).orElse(Color.gray);
            Minecraft.getInstance().displayGuiScreen(new SelectActionScreen(color, true));
        }));

        this.addButton(new Button(this.guiLeft + 5, this.guiTop + 152, 80, 20, UtilLib.translate("gui.done"), (context) -> {
            this.onClose();
        }));
    }


    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {

        this.renderBackground();

        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        GlStateManager.color4f(color[0], color[1], color[2], 1f);
        blit(this.guiLeft, this.guiTop, this.blitOffset, 0, 0, this.xSize, this.ySize, 256, 300);
        GlStateManager.color4f(1, 1, 1, 1);

        this.drawTitle();
        InventoryScreen.drawEntityOnScreen((int) (this.width * 0.65), (int) (this.height * 0.7), 60, (float) (this.guiLeft + 200) - mouseX, (float) (this.guiTop + 45) - mouseY, this.minecraft.player);

        super.render(mouseX, mouseY, partialTicks);
    }

    protected void drawTitle() {
        String title = NAME.getFormattedText();
        this.font.drawStringWithShadow(title, this.guiLeft + 15, this.guiTop + 5, 0xFFFFFFFF);
    }
}
