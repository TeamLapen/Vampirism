package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.client.gui.ScrollableListButton;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.AppearancePacket;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class VampireAppearanceScreen extends AppearanceScreen {

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

    private Button eyes;
    private Button fangs;
    private int fangType;
    private int eyeType;

    @Override
    protected void init() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.eyes = this.addButton(new ScrollableListButton(this.guiLeft + 20, this.guiTop + 30 + 20, 100, 100, REFERENCE.EYE_TYPE_COUNT, 5, descEye, UtilLib.translate("gui.vampirism.appearance.eyestyle"), this::eye));
        this.fangs = this.addButton(new ScrollableListButton(this.guiLeft + 20, this.guiTop + 50 + 20, 100, 80, REFERENCE.FANG_TYPE_COUNT, 4, descFang, UtilLib.translate("gui.vampirism.appearance.fangstyle"), this::fang));

        this.addButton(new GuiButtonExt(this.guiLeft + 20, this.guiTop + 30, 100, 20, UtilLib.translate("gui.vampirism.appearance.eyes"), (button -> {
            this.eyes.visible = !this.eyes.visible;
            this.fangs.visible = false;
        })));
        this.addButton(new GuiButtonExt(this.guiLeft + 20, this.guiTop + 50, 100, 20, UtilLib.translate("gui.vampirism.appearance.fangs"), (button -> {
            this.fangs.visible = !this.fangs.visible;
            this.eyes.visible = false;
        })));

        this.eyes.visible = false;
        this.fangs.visible = false;

        this.fangType = VampirePlayer.getOpt(this.minecraft.player).map(VampirePlayer::getFangType).orElse(0);
        this.eyeType = VampirePlayer.getOpt(this.minecraft.player).map(VampirePlayer::getEyeType).orElse(0);

        super.init();


    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if (this.fangs.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) || this.fangs.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
            return true;
        }
        return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        this.eyes.render(mouseX, mouseY, partialTicks);
        this.fangs.render(mouseX, mouseY, partialTicks);
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
