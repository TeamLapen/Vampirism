package de.teamlapen.vampirism.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.lib.client.gui.components.HoverList;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayerSpecialAttributes;
import de.teamlapen.vampirism.network.ServerboundAppearancePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

@OnlyIn(Dist.CLIENT)
public class VampirePlayerAppearanceScreen extends AppearanceScreen<Player> {

    private static final Component NAME = Component.translatable("gui.vampirism.appearance");

    private float[] color;

    private int fangType;
    private int eyeType;
    private boolean glowingEyes;
    private HoverList<?> eyeList;
    private HoverList<?> fangList;
    private ExtendedButton eyeButton;
    private ExtendedButton fangButton;
    private Checkbox glowingEyesButton;


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
        VampirismMod.dispatcher.sendToServer(new ServerboundAppearancePacket(this.entity.getId(), "", fangType, eyeType, glowingEyes ? 1 : 0));
        super.removed();
    }

    @Override
    protected void init() {
        super.init();
        IFaction<?> f = VampirismPlayerAttributes.get(Minecraft.getInstance().player).faction;
        this.color = f == null ? Color.GRAY.getRGBColorComponents() : new Color(f.getColor()).getRGBColorComponents();
        VampirePlayerSpecialAttributes vampAtt = VampirismPlayerAttributes.get(this.minecraft.player).getVampSpecial();
        this.fangType = vampAtt.fangType;
        this.eyeType = vampAtt.eyeType;
        this.glowingEyes = vampAtt.glowingEyes;

        this.glowingEyesButton = this.addRenderableWidget(new Checkbox(this.guiLeft + 20, this.guiTop + 70, 99, 20, Component.translatable("gui.vampirism.appearance.glowing_eye"), glowingEyes) {
            @Override
            public void onPress() {
                super.onPress();
                glowingEyes = selected();
                VampirePlayer.getOpt(entity).ifPresent(p -> p.setGlowingEyes(glowingEyes));
            }
        });

        this.fangList = this.addRenderableWidget(HoverList.builder(this.guiLeft + 20, this.guiTop + 50 + 19, 99, 80).componentsWithClickAndHover(IntStream.range(0, REFERENCE.FANG_TYPE_COUNT).mapToObj(type -> Component.translatable("gui.vampirism.appearance.fang").append(" " + (type + 1))).toList(), this::fang, this::hoverFang).build());
        this.fangButton = this.addRenderableWidget(new ExtendedButton(fangList.getLeft(), fangList.getTop() - 20, fangList.getWidth(), 20, Component.literal(""), (b) -> this.setFangListVisibility(!this.fangList.isVisible)));

        this.eyeList = this.addRenderableWidget(HoverList.builder(this.guiLeft + 20, this.guiTop + 30 + 19, 99, 100).componentsWithClickAndHover(IntStream.range(0, REFERENCE.EYE_TYPE_COUNT).mapToObj(type -> Component.translatable("gui.vampirism.appearance.eye").append(" " + (type + 1))).toList(), this::eye, this::hoverEye).build());
        this.eyeButton = this.addRenderableWidget(new ExtendedButton(eyeList.getLeft(), eyeList.getTop() - 20, eyeList.getWidth(), 20, Component.literal(""), (b) -> this.setEyeListVisibility(!this.eyeList.isVisible)));

        this.setEyeListVisibility(false);
        this.setFangListVisibility(false);
    }

    @Override
    protected void renderGuiBackground(@NotNull GuiGraphics graphics) {
        graphics.setColor(color[0], color[1], color[2], 1f);
        super.renderGuiBackground(graphics);
        graphics.setColor(1, 1, 1, 1);
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
        eyeButton.setMessage(Component.translatable("gui.vampirism.appearance.eye").append(" " + (eyeType + 1)));
        this.eyeList.isVisible = show;
        this.fangButton.visible = !show;
        this.glowingEyesButton.visible = !show;
        if (show) this.fangList.isVisible = false;
    }

    private void setFangListVisibility(boolean show) {
        fangButton.setMessage(Component.translatable("gui.vampirism.appearance.fang").append(" " + (fangType + 1)));
        this.fangList.isVisible = show;
        this.glowingEyesButton.visible = !show;
        if (show) this.eyeList.isVisible = false;
    }
}