package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.lib.lib.client.gui.components.ScrollableArrayTextComponentList;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.renderer.entity.VampireMinionRenderer;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.network.ServerboundAppearancePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.widget.ExtendedButton;

@OnlyIn(Dist.CLIENT)
public class VampireMinionAppearanceScreen extends AppearanceScreen<VampireMinionEntity> {
    private static final Component NAME = Component.translatable("gui.vampirism.minion_appearance");

    private int skinType;
    private boolean useLordSkin;
    private boolean isMinionSpecificSkin;
    private ScrollableArrayTextComponentList typeList;
    private ExtendedButton typeButton;
    private Checkbox lordSkinButton;
    private EditBox nameWidget;
    private int normalSkinCount;
    @SuppressWarnings("FieldCanBeLocal")
    private int minionSkinCount;

    public VampireMinionAppearanceScreen(VampireMinionEntity minion, Screen backScreen) {
        super(NAME, minion, backScreen);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!this.typeList.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        return true;
    }

    @Override
    public void removed() {
        String name = nameWidget.getValue();
        if (name.isEmpty()) {
            name = Component.translatable("text.vampirism.minion").getString() + entity.getMinionId().orElse(0);
        }
        VampirismMod.dispatcher.sendToServer(new ServerboundAppearancePacket(this.entity.getId(), name, this.skinType, (isMinionSpecificSkin ? 0b10 : 0b0) | (useLordSkin ? 0b1 : 0b0)));
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
        this.normalSkinCount = ((VampireMinionRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this.entity)).getVampireTextureCount();
        this.minionSkinCount = ((VampireMinionRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this.entity)).getMinionSpecificTextureCount(); //can be 0
        this.skinType = this.entity.getVampireType();
        this.isMinionSpecificSkin = this.entity.hasMinionSpecificSkin();
        if (this.isMinionSpecificSkin && this.minionSkinCount > 0) {
            this.skinType = this.skinType % this.minionSkinCount;
        } else {
            this.skinType = this.skinType % this.normalSkinCount;
            this.isMinionSpecificSkin = false; //If this.isMinionSpecificSkin && this.minionSkinCount==0
        }
        this.useLordSkin = this.entity.shouldRenderLordSkin();
        this.typeList = this.addRenderableWidget(new ScrollableArrayTextComponentList(this.guiLeft + 20, this.guiTop + 43 + 19, 99, 80, 20, this.normalSkinCount + this.minionSkinCount, Component.translatable("gui.vampirism.minion_appearance.skin"), this::skin, this::previewSkin));
        this.typeButton = this.addRenderableWidget(new ExtendedButton(this.typeList.getX(), this.typeList.getY() - 20, this.typeList.getWidth() + 1, 20, Component.literal(""), (button1 -> setListVisibility(!typeList.visible))));

        this.lordSkinButton = this.addRenderableWidget(new Checkbox(this.guiLeft + 20, this.guiTop + 64, 99, 20, Component.translatable("gui.vampirism.minion_appearance.use_lord_skin"), useLordSkin) {
            @Override
            public void onPress() {
                super.onPress();
                useLordSkin = selected();
                entity.setUseLordSkin(useLordSkin);
            }
        });

        setListVisibility(false);
    }

    private void onNameChanged(String newName) {
        this.entity.changeMinionName(newName);
    }

    private void previewSkin(int type, boolean hovered) {
        boolean minionSpecific = type >= normalSkinCount;
        if (hovered) {
            this.entity.setVampireType(type, minionSpecific);
        } else {
            if (this.entity.getVampireType() == type && this.entity.hasMinionSpecificSkin() == minionSpecific) {
                this.entity.setVampireType(this.skinType, this.isMinionSpecificSkin);
            }
        }
    }

    private void setListVisibility(boolean show) {
        this.typeButton.setMessage(typeList.getMessage().copy().append(" " + (skinType + 1)));
        this.typeList.visible = show;
        this.lordSkinButton.visible = !show;
    }

    private void skin(int type) {
        boolean minionSpecific = type >= normalSkinCount;
        this.entity.setVampireType(this.skinType = type, this.isMinionSpecificSkin = minionSpecific);
        setListVisibility(false);
    }
}