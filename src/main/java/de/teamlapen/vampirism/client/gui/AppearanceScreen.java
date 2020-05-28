package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.lib.client.gui.ScrollableListButton;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.AppearancePacket;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
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
        for (int i = 0; i < descEye.length; i++) {
            descEye[i] = "Type " + (i + 1);
        }
        for (int i = 0; i < descFang.length; i++) {
            descFang[i] = "Type " + (i + 1);
        }
    }

    protected final int xSize = 256;
    protected final int ySize = 177;
    protected int guiLeft;
    protected int guiTop;
    private float[] color;

    private Button eyes;
    private Button fangs;
    private int fangType;
    private int eyeType;


    public AppearanceScreen() {
        super(NAME);
    }

    @Override
    protected void init() {
        this.color = FactionPlayerHandler.getOpt(Minecraft.getInstance().player).map(FactionPlayerHandler::getCurrentFaction).map(IPlayableFaction::getColor).orElse(Color.gray).getRGBColorComponents(null);

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.eyes = this.addButton(new ScrollableListButton(this.guiLeft + 20, this.guiTop + 30 + 19, 99, 100, REFERENCE.EYE_TYPE_COUNT, descEye, UtilLib.translate("gui.vampirism.appearance.eyestyle"), this::eye));
        this.fangs = this.addButton(new ScrollableListButton(this.guiLeft + 20, this.guiTop + 50 + 19, 99, 80, REFERENCE.FANG_TYPE_COUNT, descFang, UtilLib.translate("gui.vampirism.appearance.fangstyle"), this::fang));

        this.addButton(new GuiButtonExt(this.guiLeft + 20, this.guiTop + 30, 100, 20, UtilLib.translate("gui.vampirism.appearance.eyes"), (button -> {
            this.eyes.visible = !this.eyes.visible;
            this.fangs.visible = false;
        })));
        this.addButton(new GuiButtonExt(this.guiLeft + 20, this.guiTop + 50, 100, 20, UtilLib.translate("gui.vampirism.appearance.fangs"), (button -> {
            this.fangs.visible = !this.fangs.visible;
            this.eyes.visible = false;
        })));

        this.addButton(new Button(this.guiLeft + 5, this.guiTop + 152, 80, 20, UtilLib.translate("gui.done"), (context) -> {
            this.onClose();
        }));

        this.eyes.visible = false;
        this.fangs.visible = false;

        this.fangType = VampirePlayer.getOpt(this.minecraft.player).map(VampirePlayer::getFangType).orElse(0);
        this.eyeType = VampirePlayer.getOpt(this.minecraft.player).map(VampirePlayer::getEyeType).orElse(0);
    }


    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {

        this.renderBackground();

        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        GlStateManager.color4f(color[0], color[1], color[2], 1f);
        blit(this.guiLeft, this.guiTop, this.blitOffset, 0, 0, this.xSize, this.ySize, 256, 300);
        GlStateManager.color4f(1, 1, 1, 1);

        this.drawTitle();
        InventoryScreen.drawEntityOnScreen(this.guiLeft + 200, this.guiTop + 145, 60, (float) (this.guiLeft + 200) - mouseX, (float) (this.guiTop + 45) - mouseY, this.minecraft.player);

        super.render(mouseX, mouseY, partialTicks);

        this.eyes.render(mouseX, mouseY, partialTicks);
        this.fangs.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int activeButton, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        if (this.fangs.visible && this.fangs.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, activeButton, p_mouseDragged_6_, p_mouseDragged_8_) ) {
            return true;
        }
        if (this.eyes.visible && this.eyes.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, activeButton, p_mouseDragged_6_, p_mouseDragged_8_) ) {
            return true;
        }
        return false;
    }

    protected void drawTitle() {
        String title = NAME.getFormattedText();
        this.font.drawStringWithShadow(title, this.guiLeft + 15, this.guiTop + 5, 0xFFFFFFFF);
    }

    private void fang(int fangType) {
        VampirePlayer.getOpt(this.minecraft.player).ifPresent(vampire -> {
            vampire.setFangType(this.fangType = fangType);
        });
    }

    private void eye(int eyeType) {
        VampirePlayer.getOpt(this.minecraft.player).ifPresent(vampire -> {
            vampire.setEyeType(this.eyeType = eyeType);
        });
    }

    @Override
    public void onClose() {
        VampirismMod.dispatcher.sendToServer(new AppearancePacket(fangType, eyeType));
        super.onClose();
    }
}
