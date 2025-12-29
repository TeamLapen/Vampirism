package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.factions.client.gui.components.DropdownWidget;
import de.teamlapen.factions.client.gui.components.IRenderLast;
import de.teamlapen.factions.client.gui.screens.AppearanceScreen;
import de.teamlapen.factions.common.factions.minions.MinionData;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.renderer.entities.HunterMinionRenderer;
import de.teamlapen.vampirism.common.network.packets.server.ServerboundAppearancePacket;
import de.teamlapen.vampirism.common.world.entity.minion.HunterMinionEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

public class HunterMinionAppearanceScreen extends AppearanceScreen<HunterMinionEntity> {

    private static final Component NAME = Component.translatable("gui.vampirism.minion_appearance");

    private int skinType;
    private int hatType;
    private boolean useLordSkin;
    private boolean isMinionSpecificSkin;
    private int normalSkinCount;
    @SuppressWarnings("FieldCanBeLocal")
    private int minionSkinCount;
    private String minionName;

    public HunterMinionAppearanceScreen(HunterMinionEntity minion, Screen backScreen) {
        super(NAME, minion, backScreen);
    }

    @Override
    public void removed() {
        String name = minionName;
        if (name.isEmpty()) {
            name = Component.translatable("text.vampirism.minion").toString() + entity.getMinionId().orElse(0);
        }
        VampirismMod.proxy.sendToServer(new ServerboundAppearancePacket(this.entity.getId(), name, this.skinType, this.hatType, (this.isMinionSpecificSkin ? 0b10 : 0b0) | (this.useLordSkin ? 0b1 : 0b0)));
        super.removed();
    }

    @Override
    protected void init() {
        this.minionName = this.entity.getMinionData().map(MinionData::getName).orElse("");
        this.normalSkinCount = ((HunterMinionRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this.entity)).getHunterTextureCount();
        this.minionSkinCount = ((HunterMinionRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this.entity)).getMinionSpecificTextureCount(); //Can be 0
        this.isMinionSpecificSkin = this.entity.hasMinionSpecificSkin();
        this.skinType = this.entity.getHunterType();
        if (this.isMinionSpecificSkin && this.minionSkinCount > 0) {
            this.skinType = this.skinType % this.minionSkinCount;
        } else {
            this.skinType = this.skinType % this.normalSkinCount;
            this.isMinionSpecificSkin = false; //If this.isMinionSpecificSkin && this.minionSkinCount==0
        }
        this.hatType = this.entity.getHatType();
        this.useLordSkin = this.entity.shouldRenderLordSkin();

        super.init();
    }


    @Override
    protected @NotNull LayoutElement createLayout() {
        LinearLayout vertical = LinearLayout.vertical();
        vertical.spacing(4);

        var name = new EditBox(this.font,  98, 12, Component.translatable("gui.vampirism.minion_appearance.name"));
        name.setTextColorUneditable(-1);
        name.setTextColor(-1);
        name.insertText(this.minionName);
        name.setMaxLength(MinionData.MAX_NAME_LENGTH);
        name.setResponder(this::onNameChanged);
        vertical.addChild(name);

        vertical.addChild(DropdownWidget.builder(0,0)
                .width(99)
                        .itemHeight(20)
                        .maxVisibleItems(5)
                        .initialSelection(this.hatType)
                        .onSelect(this::hat)
                        .onHover(this::previewHat)
                        .items(IntStream.range(0, 3)
                                .mapToObj(type -> (Component) Component.translatable("gui.vampirism.minion_appearance.hat").append(" " + (type + 1)))
                                .toList())
                .build());
        vertical.addChild(DropdownWidget.builder(0,0)
                .width(99)
                        .itemHeight(20)
                        .maxVisibleItems(5)
                        .initialSelection(this.skinType)
                        .onSelect(this::skin)
                        .onHover(this::previewSkin)
                        .items(IntStream.range(0, this.normalSkinCount + this.minionSkinCount)
                                .mapToObj(type -> (Component) Component.translatable("gui.vampirism.minion_appearance.skin").append(" " + (type + 1)))
                                .toList())
                .build());

        vertical.addChild(Checkbox.builder(Component.translatable("gui.vampirism.minion_appearance.use_lord_skin"), this.font).selected(useLordSkin).onValueChange((checkBox, selected) -> {
            useLordSkin = selected;
            entity.setUseLordSkin(selected);
        }).build());

        return vertical;
    }

    private void hat(int type) {
        this.entity.setHatType(this.hatType = type);
    }

    private void onNameChanged(String newName) {
        this.minionName = newName;
        this.entity.changeMinionName(newName);
    }

    private void previewHat(int type, boolean hovered) {
        if (hovered) {
            this.entity.setHatType(type);
        } else {
            if (this.entity.getHatType() == type) {
                this.entity.setHatType(this.hatType);
            }
        }
    }

    private void previewSkin(int type, boolean hovered) {
        boolean minionSpecific = type >= normalSkinCount;
        if (hovered) {
            this.entity.setHunterType(type, minionSpecific);
        } else {
            if (this.entity.getHunterType() == type && this.entity.hasMinionSpecificSkin() == minionSpecific) {
                this.entity.setHunterType(this.skinType, this.isMinionSpecificSkin);
            }
        }
    }
    private void skin(int type) {
        boolean minionSpecific = type >= normalSkinCount;
        this.entity.setHunterType(this.skinType = type, this.isMinionSpecificSkin = minionSpecific);
    }
}
