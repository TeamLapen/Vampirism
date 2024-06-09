package de.teamlapen.vampirism.client.gui.screens;
import de.teamlapen.lib.lib.client.gui.components.HoverList;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayerSpecialAttributes;
import de.teamlapen.vampirism.network.ServerboundAppearancePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class VampirePlayerAppearanceScreen extends AppearanceScreen<Player> {

    private static final Component NAME = Component.translatable("gui.vampirism.appearance");

    private float[] color;

    private int fangType;
    private int eyeType;
    private boolean glowingEyes;
    private boolean titleGender;
    private HoverList<?> eyeList;
    private HoverList<?> fangList;
    private ExtendedButton eyeButton;
    private ExtendedButton fangButton;
    private Checkbox glowingEyesButton;
    private Checkbox titleGenderButton;


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
        VampirismMod.proxy.sendToServer(new ServerboundAppearancePacket(this.entity.getId(), "", fangType, eyeType, glowingEyes ? 1 : 0, titleGender ? 1 : 0));
        super.removed();
    }

    @Override
    protected void init() {
        super.init();
        VampirismPlayerAttributes attributes = VampirismPlayerAttributes.get(Minecraft.getInstance().player);
        Holder<? extends IPlayableFaction<?>> f = attributes.faction;
        this.color = f == null ? Color.GRAY.getRGBColorComponents() : new Color(f.value().getColor()).getRGBColorComponents();
        VampirePlayerSpecialAttributes vampAtt = attributes.getVampSpecial();
        this.fangType = vampAtt.fangType;
        this.eyeType = vampAtt.eyeType;
        this.glowingEyes = vampAtt.glowingEyes;
        this.titleGender = FactionPlayerHandler.get(Minecraft.getInstance().player).titleGender() == IPlayableFaction.TitleGender.FEMALE;

        this.titleGenderButton = this.addRenderableWidget(Checkbox.builder(Component.translatable("gui.vampirism.appearance.title_gender"), minecraft.font).pos(this.guiLeft + 20, this.guiTop + 91).selected(titleGender).onValueChange((button, selected) -> {
            titleGender = selected;
            FactionPlayerHandler.get(entity).setTitleGender(titleGender);
        }).build());
        this.glowingEyesButton = this.addRenderableWidget(Checkbox.builder(Component.translatable("gui.vampirism.appearance.glowing_eye"), minecraft.font).pos(this.guiLeft + 20, this.guiTop + 70).selected(glowingEyes).onValueChange((button, selected) -> {
            glowingEyes = selected;
            VampirePlayer.get(entity).setGlowingEyes(glowingEyes);
        }).build());

        this.fangList = this.addRenderableWidget(HoverList.builder(this.guiLeft + 20, this.guiTop + 50 + 19, 99, 80).componentsWithClickAndHover(IntStream.range(0, REFERENCE.FANG_TYPE_COUNT).mapToObj(type -> Component.translatable("gui.vampirism.appearance.fang").append(" " + (type + 1))).toList(), this::fang, this::hoverFang).build());
        this.fangButton = this.addRenderableWidget(new ExtendedButton(fangList.getX(), fangList.getY() - 20, fangList.getWidth(), 20, Component.literal(""), (b) -> this.setFangListVisibility(!this.fangList.visible)));
//
        this.eyeList = this.addRenderableWidget(HoverList.builder(this.guiLeft + 20, this.guiTop + 30 + 19, 99, 100).componentsWithClickAndHover(IntStream.range(0, REFERENCE.EYE_TYPE_COUNT).mapToObj(type -> Component.translatable("gui.vampirism.appearance.eye").append(" " + (type + 1))).toList(), this::eye, this::hoverEye).build());
        this.eyeButton = this.addRenderableWidget(new ExtendedButton(eyeList.getX(), eyeList.getY() - 20, eyeList.getWidth(), 20, Component.literal(""), (b) -> this.setEyeListVisibility(!this.eyeList.visible)));
//
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
        VampirePlayer vampire = VampirePlayer.get(this.minecraft.player);
        vampire.setEyeType(this.eyeType = eyeType);
        setEyeListVisibility(false);
    }

    private void fang(int fangType) {
        VampirePlayer vampire = VampirePlayer.get(this.minecraft.player);
        vampire.setFangType(this.fangType = fangType);
        setFangListVisibility(false);
    }

    private void hoverEye(int eyeType, boolean hovered) {
        VampirePlayer vampire = VampirePlayer.get(this.minecraft.player);
        if (hovered) {
            vampire.setEyeType(eyeType);
        } else {
            if (vampire.getEyeType() == eyeType) {
                vampire.setEyeType(this.eyeType);
            }
        }
    }

    private void hoverFang(int fangType, boolean hovered) {
        VampirePlayer vampire = VampirePlayer.get(this.minecraft.player);
        if (hovered) {
            vampire.setFangType(fangType);
        } else {
            if (vampire.getFangType() == fangType) {
                vampire.setFangType(this.fangType);
            }
        }
    }

    private void setEyeListVisibility(boolean show) {
        eyeButton.setMessage(Component.translatable("gui.vampirism.appearance.eye").append(" " + (eyeType + 1)));
        this.eyeList.visible = show;
        this.fangButton.visible = !show;
        this.glowingEyesButton.visible = !show;
        this.titleGenderButton.visible = !show;
        if (show) this.fangList.visible = false;
    }

    private void setFangListVisibility(boolean show) {
        fangButton.setMessage(Component.translatable("gui.vampirism.appearance.fang").append(" " + (fangType + 1)));
        this.fangList.visible = show;
        this.glowingEyesButton.visible = !show;
        this.titleGenderButton.visible = !show;
        if (show) this.eyeList.visible = false;
    }
}