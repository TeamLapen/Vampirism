package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.client.gui.widget.ScrollableArrayTextComponentList;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.render.entities.HunterMinionRenderer;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.network.CAppearancePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

@OnlyIn(Dist.CLIENT)
public class HunterMinionAppearanceScreen extends AppearanceScreen<HunterMinionEntity> {

    private static final ITextComponent NAME = new TranslationTextComponent("gui.vampirism.minion_appearance");

    private int skinType;
    private int hatType;
    private boolean useLordSkin;
    private boolean isMinionSpecificSkin;
    private ScrollableArrayTextComponentList skinList;
    private ScrollableArrayTextComponentList hatList;
    private ExtendedButton skinButton;
    private ExtendedButton hatButton;
    private CheckboxButton useLordSkinButton;
    private TextFieldWidget nameWidget;
    private int normalSkinCount;
    private int minionSkinCount;

    public HunterMinionAppearanceScreen(HunterMinionEntity minion, Screen backScreen) {
        super(NAME, minion, backScreen);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!this.hatList.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            if (!this.skinList.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
            }
        }
        return true;
    }

    @Override
    public void removed() {
        String name = nameWidget.getValue();
        if (name.isEmpty()) {
            name = new TranslationTextComponent("text.vampirism.minion").toString() + entity.getMinionId().orElse(0);
        }
        VampirismMod.dispatcher.sendToServer(new CAppearancePacket(this.entity.getId(), name, this.skinType, this.hatType, (this.isMinionSpecificSkin ? 0b10 : 0b0) | (this.useLordSkin ? 0b1 : 0b0)));
        super.removed();
    }

    @Override
    protected void init() {
        super.init();
        this.nameWidget = this.addButton(new TextFieldWidget(font, this.guiLeft + 21, this.guiTop + 29, 98, 12, new TranslationTextComponent("gui.vampirism.minion_appearance.name")));
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
        this.skinList = this.addButton(new ScrollableArrayTextComponentList(this.guiLeft + 20, this.guiTop + 43 + 19, 99, 80, 20, this.normalSkinCount + this.minionSkinCount, new TranslationTextComponent("gui.vampirism.minion_appearance.skin"), this::skin, this::previewSkin));
        this.hatList = this.addButton(new ScrollableArrayTextComponentList(this.guiLeft + 20, this.guiTop + 64 + 19, 99, 60, 20, 3, new TranslationTextComponent("gui.vampirism.minion_appearance.hat"), this::hat, this::previewHat));
        this.skinButton = this.addButton(new ExtendedButton(skinList.x, skinList.y - 20, skinList.getWidth() + 1, 20, new StringTextComponent(""), (b) -> {
            setSkinListVisibility(!skinList.visible);
        }));
        this.hatButton = this.addButton(new ExtendedButton(hatList.x, hatList.y - 20, hatList.getWidth() + 1, 20, new StringTextComponent(""), (b) -> {
            setHatListVisibility(!hatList.visible);
        }));

        this.useLordSkinButton = this.addButton(new CheckboxButton(this.guiLeft + 20, this.guiTop + 86, 99, 20, new TranslationTextComponent("gui.vampirism.minion_appearance.use_lord_skin"), useLordSkin) {
            @Override
            public void onPress() {
                super.onPress();
                useLordSkin = selected();
                entity.setUseLordSkin(useLordSkin);
            }
        });
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
        hatButton.setMessage(hatList.getMessage().copy().append(" " + (hatType + 1)));
        hatList.visible = show;
        if (show) skinList.visible = false;
        useLordSkinButton.visible = !show;
    }

    private void setSkinListVisibility(boolean show) {
        skinButton.setMessage(skinList.getMessage().copy().append(" " + (skinType + 1)));
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
