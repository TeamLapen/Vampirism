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
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class VampirePlayerAppearanceScreen extends AppearanceScreen<PlayerEntity> {

    private static final ITextComponent NAME = new TranslationTextComponent("gui.vampirism.appearance");

    private float[] color;

    private int fangType;
    private int eyeType;
    private ScrollableListButton eyeList;
    private ScrollableListButton fangList;
    private Button eyeButton;
    private Button fangButton;


    public VampirePlayerAppearanceScreen(Screen backScreen) {
        super(NAME, Minecraft.getInstance().player, backScreen);
    }

    @Override
    public void onClose() {
        VampirismMod.dispatcher.sendToServer(new AppearancePacket(this.entity.getEntityId(), "", fangType, eyeType));
        super.onClose();
    }

    @Override
    protected void init() {
        super.init();
        this.color = FactionPlayerHandler.getOpt(Minecraft.getInstance().player).map(FactionPlayerHandler::getCurrentFaction).map(IPlayableFaction::getColor).orElse(Color.gray).getRGBColorComponents(null);

        this.fangType = VampirePlayer.getOpt(this.minecraft.player).map(VampirePlayer::getFangType).orElse(0);
        this.eyeType = VampirePlayer.getOpt(this.minecraft.player).map(VampirePlayer::getEyeType).orElse(0);

        this.eyeList = this.addButton(new ScrollableListButton(this.guiLeft + 20, this.guiTop + 30 + 19, 99, 5, REFERENCE.EYE_TYPE_COUNT, null, UtilLib.translate("gui.vampirism.appearance.eye"), this::eye, false));
        this.fangList = this.addButton(new ScrollableListButton(this.guiLeft + 20, this.guiTop + 50 + 19, 99, 4, REFERENCE.FANG_TYPE_COUNT, null, UtilLib.translate("gui.vampirism.appearance.fang"), this::fang, false));
        this.eyeButton = this.addButton(new GuiButtonExt(eyeList.x, eyeList.y - 20, eyeList.getWidth() + 1, 20, "", (b) -> {
            this.setEyeListVisibility(!eyeList.visible);
        }));
        this.fangButton = this.addButton(new GuiButtonExt(fangList.x, fangList.y - 20, fangList.getWidth() + 1, 20, "", (b) -> {
            this.setFangListVisibility(!fangList.visible);
        }));
        this.setEyeListVisibility(false);
        this.setFangListVisibility(false);

    }

    @Override
    protected void renderGuiBackground() {
        GlStateManager.color4f(color[0], color[1], color[2], 1f);
        super.renderGuiBackground();
        GlStateManager.color4f(1, 1, 1, 1);
    }

    private void eye(int eyeType) {
        VampirePlayer.getOpt(this.minecraft.player).ifPresent(vampire -> {
            vampire.setEyeType(this.eyeType = eyeType);
        });
        setEyeListVisibility(false);
    }

    private void fang(int fangType) {
        VampirePlayer.getOpt(this.minecraft.player).ifPresent(vampire -> {
            vampire.setFangType(this.fangType = fangType);
        });
        setFangListVisibility(false);
    }

    private void setEyeListVisibility(boolean show) {
        eyeButton.setMessage(eyeList.getMessage() + " " + (eyeType + 1));
        this.eyeList.visible = show;
        this.fangButton.visible = !show;
        if (show) this.fangList.visible = false;
    }

    private void setFangListVisibility(boolean show) {
        fangButton.setMessage(fangList.getMessage() + " " + (fangType + 1));
        this.fangList.visible = show;
        if (show) this.eyeList.visible = false;
    }
}
