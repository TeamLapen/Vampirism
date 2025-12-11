package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.factions.client.gui.components.DropdownWidget;
import de.teamlapen.factions.client.gui.components.IRenderLast;
import de.teamlapen.factions.client.gui.screens.AppearanceScreen;
import de.teamlapen.vampirism.client.gui.components.HoverList;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.common.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.entity.player.vampire.skills.VampirePlayerSkillProperties;
import de.teamlapen.vampirism.common.network.packets.server.ServerboundAppearancePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class VampirePlayerAppearanceScreen extends AppearanceScreen<Player> {

    private static final Component NAME = Component.translatable("gui.vampirism.appearance");

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

//    @Override
//    public boolean mouseDragged(@NotNull MouseButtonEvent mouseButtonEvent, double dragX, double dragY) {
//        if (!this.fangList.mouseDragged(mouseButtonEvent, dragX, dragY)) {
//            if (!this.eyeList.mouseDragged(mouseButtonEvent, dragX, dragY)) {
//                return super.mouseDragged(mouseButtonEvent, dragX, dragY);
//            }
//        }
//        return true;
//    }

    @Override
    public void removed() {
        VampirismMod.proxy.sendToServer(new ServerboundAppearancePacket(this.entity.getId(), "", fangType, eyeType, glowingEyes ? 1 : 0, titleGender ? 1 : 0));
        super.removed();
    }

    @Override
    protected void init() {
        VampirePlayerSkillProperties vampAtt = VampirePlayer.get(Minecraft.getInstance().player).getSkillProperties();
        VampirePlayer vampire = VampirePlayer.get(minecraft.player);
        var customization = vampire.getCustomization();
        this.fangType = customization.fangType();
        this.eyeType = customization.eyeType();
        this.glowingEyes = customization.glowingEyes();
        this.titleGender = vampire.titleGender() == IPlayableFaction.TitleGender.FEMALE;
        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof IRenderLast last) {
                last.renderLast(guiGraphics, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    protected @NotNull LayoutElement createLayout() {
        LinearLayout vertical = LinearLayout.vertical();
        vertical.spacing(4);

        vertical.addChild(DropdownWidget.builder(0,0)
                        .width(120)
                        .itemHeight(20)
                        .maxVisibleItems(5)
                        .initialSelection(this.eyeType)
                        .onSelect(this::eye)
                        .onHover(this::hoverEye)
                .items(IntStream.range(0, REFERENCE.EYE_TYPE_COUNT)
                        .mapToObj(type -> (Component) Component.translatable("gui.vampirism.appearance.eye").append(" " + (type + 1)))
                        .toList())
                        .build());

        vertical.addChild(DropdownWidget.builder(0,0)
                        .width(120)
                        .itemHeight(20)
                        .maxVisibleItems(5)
                        .initialSelection(this.fangType)
                        .onSelect(this::fang)
                        .onHover(this::hoverFang)
                .items(IntStream.range(0, REFERENCE.FANG_TYPE_COUNT)
                        .mapToObj(type -> (Component) Component.translatable("gui.vampirism.appearance.fang").append(" " + (type + 1)))
                        .toList())
                        .build());


        vertical.addChild(Checkbox.builder(Component.translatable("gui.vampirism.appearance.title_gender"), minecraft.font).selected(titleGender).onValueChange((button, selected) -> {
            titleGender = selected;
            FactionPlayerHandler.get(entity).setTitleGender(titleGender);
        }).build());

        vertical.addChild(Checkbox.builder(Component.translatable("gui.vampirism.appearance.glowing_eye"), minecraft.font).selected(glowingEyes).onValueChange((button, selected) -> {
            glowingEyes = selected;
            VampirePlayer.get(entity).setGlowingEyes(glowingEyes);
        }).build());


        return vertical;
    }

    protected void ini2t() {

//        this.fangList = this.addRenderableWidget(HoverList.builder(this.guiLeft + 20, this.guiTop + 50 + 19, 99, 80).componentsWithClickAndHover(IntStream.range(0, REFERENCE.FANG_TYPE_COUNT).mapToObj(type -> Component.translatable("gui.vampirism.appearance.fang").append(" " + (type + 1))).toList(), this::fang, this::hoverFang).build());
//        this.fangButton = this.addRenderableWidget(new ExtendedButton(fangList.getX(), fangList.getY() - 20, fangList.getWidth(), 20, Component.literal(""), (b) -> this.setFangListVisibility(!this.fangList.visible)));
////
//        this.eyeList = this.addRenderableWidget(HoverList.builder(this.guiLeft + 20, this.guiTop + 30 + 19, 99, 100).componentsWithClickAndHover(IntStream.range(0, REFERENCE.EYE_TYPE_COUNT).mapToObj(type -> Component.translatable("gui.vampirism.appearance.eye").append(" " + (type + 1))).toList(), this::eye, this::hoverEye).build());
//        this.eyeButton = this.addRenderableWidget(new ExtendedButton(eyeList.getX(), eyeList.getY() - 20, eyeList.getWidth(), 20, Component.literal(""), (b) -> this.setEyeListVisibility(!this.eyeList.visible)));
////
//        this.setEyeListVisibility(false);
//        this.setFangListVisibility(false);
    }

    private void eye(int eyeType) {
        VampirePlayer vampire = VampirePlayer.get(this.minecraft.player);
        vampire.setEyeType(this.eyeType = eyeType);
//        setEyeListVisibility(false);
    }

    private void fang(int fangType) {
        VampirePlayer vampire = VampirePlayer.get(this.minecraft.player);
        vampire.setFangType(this.fangType = fangType);
//        setFangListVisibility(false);
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

//    private void setEyeListVisibility(boolean show) {
//        eyeButton.setMessage(Component.translatable("gui.vampirism.appearance.eye").append(" " + (eyeType + 1)));
//        this.eyeList.visible = show;
//        this.fangButton.visible = !show;
//        this.glowingEyesButton.visible = !show;
//        this.titleGenderButton.visible = !show;
//        if (show) this.fangList.visible = false;
//    }
//
//    private void setFangListVisibility(boolean show) {
//        fangButton.setMessage(Component.translatable("gui.vampirism.appearance.fang").append(" " + (fangType + 1)));
//        this.fangList.visible = show;
//        this.glowingEyesButton.visible = !show;
//        this.titleGenderButton.visible = !show;
//        if (show) this.eyeList.visible = false;
//    }
}