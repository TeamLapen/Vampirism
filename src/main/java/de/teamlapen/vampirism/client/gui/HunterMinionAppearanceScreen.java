package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.client.gui.ScrollableListButton;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.render.entities.HunterMinionRenderer;
import de.teamlapen.vampirism.client.render.entities.VampireMinionRenderer;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.network.AppearancePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;
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
    private ScrollableListButton skinList;
    private ScrollableListButton hatList;
    private ExtendedButton skinButton;
    private ExtendedButton hatButton;
    private CheckboxButton useLordSkinButton;
    private TextFieldWidget nameWidget;

    public HunterMinionAppearanceScreen(HunterMinionEntity minion, Screen backScreen) {
        super(NAME, minion, backScreen);
    }

    @Override
    public void onClose() {
        String name = nameWidget.getText();
        if (name.isEmpty()) {
            name = new TranslationTextComponent("text.vampirism.minion").toString() + entity.getMinionId().orElse(0);
        }
        VampirismMod.dispatcher.sendToServer(new AppearancePacket(this.entity.getEntityId(), name, this.skinType, this.hatType, useLordSkin ? 1 : 0));
        super.onClose();
    }

    @Override
    protected void init() {
        super.init();
        this.nameWidget = this.addButton(new TextFieldWidget(font, this.guiLeft + 21, this.guiTop + 29, 98, 12, UtilLib.translate("gui.vampirism.minion_appearance.name")));
        this.nameWidget.setText(entity.getMinionData().map(MinionData::getName).orElse("Minion"));
        this.nameWidget.setDisabledTextColour(-1);
        this.nameWidget.setTextColor(-1);
        this.nameWidget.setMaxStringLength(MinionData.MAX_NAME_LENGTH);
        this.nameWidget.setResponder(this::onNameChanged);
        this.skinType = this.entity.getHunterType();
        this.hatType = this.entity.getHatType();
        this.useLordSkin = this.entity.shouldRenderLordSkin();
        this.skinList = this.addButton(new ScrollableListButton(this.guiLeft + 20, this.guiTop + 43 + 19, 99, 5, ((HunterMinionRenderer) Minecraft.getInstance().getRenderManager().getRenderer(this.entity)).getTextureLength(), null, UtilLib.translate("gui.vampirism.minion_appearance.skin"), this::skin, false));
        this.hatList = this.addButton(new ScrollableListButton(this.guiLeft + 20, this.guiTop + 64 + 19, 99, 3, 3, null, UtilLib.translate("gui.vampirism.minion_appearance.hat"), this::hat, false));
        this.skinButton = this.addButton(new ExtendedButton(skinList.x, skinList.y - 20, skinList.getWidth() + 1, 20, "", (b) -> {
            setSkinListVisibility(!skinList.visible);
        }));
        this.hatButton = this.addButton(new ExtendedButton(hatList.x, hatList.y - 20, hatList.getWidth() + 1, 20, "", (b) -> {
            setHatListVisibility(!hatList.visible);
        }));

        this.useLordSkinButton = this.addButton(new CheckboxButton(this.guiLeft + 20, this.guiTop + 86, 99, 20, UtilLib.translate("gui.vampirism.minion_appearance.use_lord_skin"), useLordSkin) {
            @Override
            public void onPress() {
                super.onPress();
                useLordSkin = isChecked();
                entity.setUseLordSkin(useLordSkin);
            }
        });
        setSkinListVisibility(false);
        setHatListVisibility(false);
    }

    private void hat(int type) {
        this.entity.setHatType(this.hatType = type);
    }

    private void onNameChanged(String newName) {
        this.entity.changeMinionName(newName);
    }

    private void setHatListVisibility(boolean show) {
        hatButton.setMessage(hatList.getMessage() + " " + (hatType + 1));
        hatList.visible = show;
        if (show) skinList.visible = false;
        useLordSkinButton.visible = !show;
    }

    private void setSkinListVisibility(boolean show) {
        skinButton.setMessage(skinList.getMessage() + " " + (skinType + 1));
        this.skinList.visible = show;
        this.hatButton.visible = !show;
        this.useLordSkinButton.visible = !show;
        if (show) hatList.visible = false;
    }

    private void skin(int type) {
        this.entity.setHunterType(this.skinType = type );
        setSkinListVisibility(false);
    }
}
