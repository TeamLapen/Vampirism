package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.lib.lib.client.gui.components.HoverList;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.renderer.entity.HunterMinionRenderer;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.network.ServerboundAppearancePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

@OnlyIn(Dist.CLIENT)
public class HunterMinionAppearanceScreen extends AppearanceScreen<HunterMinionEntity> {

    private static final Component NAME = Component.translatable("gui.vampirism.minion_appearance");

    private int skinType;
    private int hatType;
    private boolean useLordSkin;
    private boolean isMinionSpecificSkin;
    private HoverList<?> skinList;
    private HoverList<?> hatList;
    private ExtendedButton skinButton;
    private ExtendedButton hatButton;
    private Checkbox useLordSkinButton;
    private EditBox nameWidget;
    private int normalSkinCount;
    @SuppressWarnings("FieldCanBeLocal")
    private int minionSkinCount;

    public HunterMinionAppearanceScreen(HunterMinionEntity minion, Screen backScreen) {
        super(NAME, minion, backScreen);
    }

    @Override
    public void removed() {
        String name = nameWidget.getValue();
        if (name.isEmpty()) {
            name = Component.translatable("text.vampirism.minion").toString() + entity.getMinionId().orElse(0);
        }
        VampirismMod.proxy.sendToServer(new ServerboundAppearancePacket(this.entity.getId(), name, this.skinType, this.hatType, (this.isMinionSpecificSkin ? 0b10 : 0b0) | (this.useLordSkin ? 0b1 : 0b0)));
        super.removed();
    }

    @Override
    protected void init() {
        super.init();
        this.nameWidget = this.addRenderableWidget(new EditBox(font, this.guiLeft + 21, this.guiTop + 29, 98, 12, Component.translatable("gui.vampirism.minion_appearance.name")));
        this.nameWidget.setValue(entity.getMinionData().map(MinionData::getName).orElse("Minion"));
        this.nameWidget.setTextColorUneditable(-1);
        this.nameWidget.setTextColor(-1);
        this.nameWidget.setMaxLength(MinionData.MAX_NAME_LENGTH);
        this.nameWidget.setResponder(this::onNameChanged);
        this.normalSkinCount = ((HunterMinionRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this.entity)).getHunterTextureCount();
        this.minionSkinCount = ((HunterMinionRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this.entity)).getMinionSpecificTextureCount(); //Can be 0
        this.isMinionSpecificSkin = this.entity.hasMinionSpecificSkin();
        if (this.isMinionSpecificSkin && this.minionSkinCount > 0) {
            this.skinType = this.skinType % this.minionSkinCount;
        } else {
            this.skinType = this.skinType % this.normalSkinCount;
            this.isMinionSpecificSkin = false; //If this.isMinionSpecificSkin && this.minionSkinCount==0
        }
        this.hatType = this.entity.getHatType();
        this.useLordSkin = this.entity.shouldRenderLordSkin();

        this.useLordSkinButton = this.addRenderableWidget(Checkbox.builder(Component.translatable("gui.vampirism.minion_appearance.use_lord_skin"), this.font).pos(this.guiLeft + 20, this.guiTop + 86).selected(useLordSkin).onValueChange(new Checkbox.OnValueChange() {
            @Override
            public void onValueChange(@NotNull Checkbox checkBox, boolean selected) {
                useLordSkin = selected;
                entity.setUseLordSkin(selected);
            }
        }).build());

        this.hatList = this.addRenderableWidget(HoverList.builder(this.guiLeft + 20, this.guiTop + 64 + 19, 99, 60).componentsWithClickAndHover(IntStream.range(0, 3).mapToObj(id -> Component.translatable("gui.vampirism.minion_appearance.hat").append(" " + (id + 1))).toList(), this::hat, this::previewHat).build());
        this.hatButton = this.addRenderableWidget(new ExtendedButton(hatList.getX(), hatList.getY() - 20, hatList.getWidth(), 20, Component.literal(""), (b) -> setHatListVisibility(!this.hatList.visible)));
        this.skinList = this.addRenderableWidget(HoverList.builder(this.guiLeft + 20, this.guiTop + 43 + 19, 99, 80).componentsWithClickAndHover(IntStream.range(0, this.normalSkinCount + this.minionSkinCount).mapToObj(id -> Component.translatable("gui.vampirism.minion_appearance.skin").append(" " + (id + 1))).toList(), this::skin, this::previewSkin).build());
        this.skinButton = this.addRenderableWidget(new ExtendedButton(skinList.getX(), skinList.getY() - 20, skinList.getWidth(), 20, Component.literal(""), (b) -> setSkinListVisibility(!this.skinList.visible)));

        setSkinListVisibility(false);
        setHatListVisibility(false);
    }

    private void hat(int type) {
        this.entity.setHatType(this.hatType = type);
        setHatListVisibility(false);
    }

    private void onNameChanged(String newName) {
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

    private void setHatListVisibility(boolean show) {
        hatButton.setMessage(Component.translatable("gui.vampirism.minion_appearance.hat").append(" " + (hatType + 1)));
        hatList.visible = show;
        if (show) skinList.visible = false;
        useLordSkinButton.visible = !show;
    }

    private void setSkinListVisibility(boolean show) {
        skinButton.setMessage(Component.translatable("gui.vampirism.minion_appearance.skin").append(" " + (skinType + 1)));
        this.skinList.visible = show;
        this.hatButton.visible = !show;
        this.useLordSkinButton.visible = !show;
        if (show) hatList.visible = false;
    }

    private void skin(int type) {
        boolean minionSpecific = type >= normalSkinCount;
        this.entity.setHunterType(this.skinType = type, this.isMinionSpecificSkin = minionSpecific);
        setSkinListVisibility(false);
    }
}
