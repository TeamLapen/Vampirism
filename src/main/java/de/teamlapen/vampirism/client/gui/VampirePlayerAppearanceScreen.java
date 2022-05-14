package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.lib.lib.client.gui.widget.ScrollableArrayTextComponentList;
import de.teamlapen.lib.lib.client.gui.widget.ScrollableListWidget;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.CAppearancePacket;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.VampirePlayerSpecialAttributes;
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
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class VampirePlayerAppearanceScreen extends AppearanceScreen<PlayerEntity> {

    private static final ITextComponent NAME = new TranslationTextComponent("gui.vampirism.appearance");

    private float[] color;

    private int fangType;
    private int eyeType;
    private boolean glowingEyes;
    private boolean lordGender;
    private ScrollableListWidget<Pair<Integer, ITextComponent>> eyeList;
    private ScrollableListWidget<Pair<Integer, ITextComponent>> fangList;
    private ExtendedButton eyeButton;
    private ExtendedButton fangButton;
    private CheckboxButton glowingEyesButton;
    private CheckboxButton lordGenderButton;


    public VampirePlayerAppearanceScreen(@Nullable Screen backScreen) {
        super(NAME, Minecraft.getInstance().player, backScreen);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!this.fangList.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            if (!this.eyeList.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
            }
        }
        return true;
    }

    @Override
    public void removed() {
        VampirismMod.dispatcher.sendToServer(new CAppearancePacket(this.entity.getId(), "", fangType, eyeType, glowingEyes ? 1 : 0, lordGender ? 1: 0));
        super.removed();
    }

    @Override
    protected void init() {
        super.init();
        IFaction<?> f = VampirismPlayerAttributes.get(Minecraft.getInstance().player).faction;
        this.color = f == null ? Color.gray.getRGBColorComponents(null) : f.getColor().getRGBColorComponents(null);
        VampirePlayerSpecialAttributes vampAtt = VampirismPlayerAttributes.get(this.minecraft.player).getVampSpecial();
        this.fangType = vampAtt.fangType;
        this.eyeType = vampAtt.eyeType;
        this.glowingEyes = vampAtt.glowingEyes;
        this.lordGender = FactionPlayerHandler.getOpt(entity).map(FactionPlayerHandler::getTitleGender).orElse(false);

        this.eyeList = this.addButton(new ScrollableArrayTextComponentList(this.guiLeft + 20, this.guiTop + 30 + 19, 99, 100, 20, REFERENCE.EYE_TYPE_COUNT, new TranslationTextComponent("gui.vampirism.appearance.eye"), this::eye, this::hoverEye).scrollSpeed(2));
        this.fangList = this.addButton(new ScrollableArrayTextComponentList(this.guiLeft + 20, this.guiTop + 50 + 19, 99, 80, 20, REFERENCE.FANG_TYPE_COUNT, new TranslationTextComponent("gui.vampirism.appearance.fang"), this::fang, this::hoverFang));
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
                glowingEyes = selected();
                VampirePlayer.getOpt(entity).ifPresent(p -> p.setGlowingEyes(glowingEyes));
            }
        });
        this.lordGenderButton = this.addButton(new CheckboxButton(this.guiLeft + 20, this.guiTop + 91, 99, 20, new TranslationTextComponent("gui.vampirism.appearance.lord_gender"), lordGender) {
            @Override
            public void onPress() {
                super.onPress();
                lordGender = selected();
                FactionPlayerHandler.getOpt(entity).ifPresent(p -> p.setTitleGender(lordGender));
            }
        });
        this.setEyeListVisibility(false);
        this.setFangListVisibility(false);
    }

    @Override
    protected void renderGuiBackground(MatrixStack mStack) {
        GlStateManager._color4f(color[0], color[1], color[2], 1f);
        super.renderGuiBackground(mStack);
        GlStateManager._color4f(1, 1, 1, 1);
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

    private void hoverEye(int eyeType, boolean hovered) {
        VampirePlayer.getOpt(this.minecraft.player).ifPresent(vampire -> {
            if (hovered) {
                vampire.setEyeType(eyeType);
            } else {
                if (vampire.getEyeType() == eyeType) {
                    vampire.setEyeType(this.eyeType);
                }
            }
        });
    }

    private void hoverFang(int fangType, boolean hovered) {
        VampirePlayer.getOpt(this.minecraft.player).ifPresent(vampire -> {
            if (hovered) {
                vampire.setFangType(fangType);
            } else {
                if (vampire.getFangType() == fangType) {
                    vampire.setFangType(this.fangType);
                }
            }
        });
    }

    private void setEyeListVisibility(boolean show) {
        eyeButton.setMessage(eyeList.getMessage().copy().append(" " + (eyeType + 1)));
        this.eyeList.visible = show;
        this.fangButton.visible = !show;
        this.glowingEyesButton.visible = !show;
        this.lordGenderButton.visible = !show;
        if (show) this.fangList.visible = false;
    }

    private void setFangListVisibility(boolean show) {
        fangButton.setMessage(fangList.getMessage().copy().append(" " + (fangType + 1)));
        this.fangList.visible = show;
        this.glowingEyesButton.visible = !show;
        this.lordGenderButton.visible = !show;
        if (show) this.eyeList.visible = false;
    }
}