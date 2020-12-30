package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.lib.lib.client.gui.ScrollableListButton;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.AppearancePacket;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class VampirePlayerAppearanceScreen extends AppearanceScreen<PlayerEntity> {

    private static final ITextComponent NAME = new TranslationTextComponent("gui.vampirism.appearance");

    private float[] color;

    private int fangType;
    private int eyeType;
    private boolean glowingEyes;
    private ScrollableListButton eyeList;
    private ScrollableListButton fangList;
    private ExtendedButton eyeButton;
    private ExtendedButton fangButton;
    private CheckboxButton glowingEyesButton;


    public VampirePlayerAppearanceScreen(@Nullable  Screen backScreen) {
        super(NAME, Minecraft.getInstance().player, backScreen);
    }

    @Override
    public void onClose() {
        VampirismMod.dispatcher.sendToServer(new AppearancePacket(this.entity.getEntityId(), "", fangType, eyeType, glowingEyes ? 1 : 0));
        super.onClose();
    }

    @Override
    protected void init() {
        super.init();
        this.color = FactionPlayerHandler.getOpt(Minecraft.getInstance().player).resolve().flatMap(fhp -> Optional.ofNullable(fhp.getCurrentFaction())).map(IPlayableFaction::getColor).orElse(Color.gray).getRGBColorComponents(null);


        this.fangType = VampirePlayer.getOpt(this.minecraft.player).map(VampirePlayer::getFangType).orElse(0);
        this.eyeType = VampirePlayer.getOpt(this.minecraft.player).map(VampirePlayer::getEyeType).orElse(0);
        this.glowingEyes = VampirePlayer.getOpt(this.minecraft.player).map(VampirePlayer::getGlowingEyes).orElse(false);

        this.eyeList = this.addButton(new ScrollableListButton(this.guiLeft + 20, this.guiTop + 30 + 19, 99, 5, REFERENCE.EYE_TYPE_COUNT, null, new TranslationTextComponent("gui.vampirism.appearance.eye"), this::eye, false));
        this.fangList = this.addButton(new ScrollableListButton(this.guiLeft + 20, this.guiTop + 50 + 19, 99, 4, REFERENCE.FANG_TYPE_COUNT, null, new TranslationTextComponent("gui.vampirism.appearance.fang"), this::fang, false));
        this.eyeButton = this.addButton(new ExtendedButton(eyeList.x, eyeList.y - 20, eyeList.getWidth() + 1, 20, new StringTextComponent(""), (b) -> {
            this.setEyeListVisibility(!eyeList.visible);
        }));
        this.fangButton = this.addButton(new ExtendedButton(fangList.x, fangList.y - 20, fangList.getWidth() + 1, 20, new StringTextComponent(""), (b) -> {
            this.setFangListVisibility(!fangList.visible);
        }));
        this.glowingEyesButton = this.addButton(new CheckboxButton(this.guiLeft + 20, this.guiTop + 70, 99, 20, new TranslationTextComponent("gui.vampirism.appearance.glowing_eye"), glowingEyes) {
            @Override
            public void onPress() {
                super.onPress();
                glowingEyes = isChecked();
                VampirePlayer.getOpt(entity).ifPresent(p -> p.setGlowingEyes(glowingEyes));
            }
        });
        this.setEyeListVisibility(false);
        this.setFangListVisibility(false);
    }

    @Override
    protected void renderGuiBackground(MatrixStack mStack) {
        GlStateManager.color4f(color[0], color[1], color[2], 1f);
        super.renderGuiBackground(mStack);
        GlStateManager.color4f(1, 1, 1, 1);
    }

    private void eye(int eyeType) {
        VampirePlayer.getOpt(this.minecraft.player).ifPresent(vampire -> {
            vampire.setEyeType(this.eyeType = eyeType);
            setEyeListVisibility(false);
        });
    }

    private void fang(int fangType) {
        VampirePlayer.getOpt(this.minecraft.player).ifPresent(vampire -> {
            vampire.setFangType(this.fangType = fangType);
            setFangListVisibility(false);
        });
    }

    private void setEyeListVisibility(boolean show) {
        eyeButton.setMessage(eyeList.getMessage().deepCopy().appendString(" " + (eyeType + 1)));
        this.eyeList.visible = show;
        this.fangButton.visible = !show;
        this.glowingEyesButton.visible = !show;
        if (show) this.fangList.visible = false;
    }

    private void setFangListVisibility(boolean show) {
        fangButton.setMessage(fangList.getMessage().deepCopy().appendString(" " + (fangType + 1)));
        this.fangList.visible = show;
        this.glowingEyesButton.visible = !show;
        if (show) this.eyeList.visible = false;
    }
}