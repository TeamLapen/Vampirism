package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.client.gui.ScrollableListButton;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.render.entities.VampireMinionRenderer;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.network.AppearancePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

@OnlyIn(Dist.CLIENT)
public class VampireMinionAppearanceScreen extends AppearanceScreen<VampireMinionEntity> {
    private static final ITextComponent NAME = new TranslationTextComponent("gui.vampirism.minion_appearance");

    private int skinType;
    private boolean useLordSkin;
    private ScrollableListButton typeList;
    private ExtendedButton typeButton;
    private CheckboxButton lordSkinButton;
    private TextFieldWidget nameWidget;

    public VampireMinionAppearanceScreen(VampireMinionEntity minion) {
        super(NAME, minion);
    }

    @Override
    public void onClose() {
        String name = nameWidget.getText();
        if (name.isEmpty()) {
            name = new TranslationTextComponent("text.vampirism.minion").getString() + entity.getMinionId().orElse(0);
        }
        VampirismMod.dispatcher.sendToServer(new AppearancePacket(this.entity.getEntityId(), name, this.skinType, useLordSkin ? 1 : 0));        super.onClose();
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

        this.skinType = this.entity.getVampireType();
        this.useLordSkin = this.entity.shouldRenderLordSkin();
        typeList = this.addButton(new ScrollableListButton(this.guiLeft + 20, this.guiTop + 43 + 19, 99, 5, ((VampireMinionRenderer) Minecraft.getInstance().getRenderManager().getRenderer(this.entity)).getTextureLength(), null, UtilLib.translate("gui.vampirism.minion_appearance.skin"), this::skin, false));
        typeButton = this.addButton(new ExtendedButton(this.typeList.x, this.typeList.y - 20, this.typeList.getWidth() + 1, 20, "", (button1 -> {
            setListVisibility(!typeList.visible);
        })));

        this.lordSkinButton = this.addButton(new CheckboxButton(this.guiLeft + 20, this.guiTop + 64, 99, 20, UtilLib.translate("gui.vampirism.minion_appearance.use_lord_skin"), useLordSkin) {
            @Override
            public void onPress() {
                super.onPress();
                useLordSkin = isChecked();
                entity.setUseLordSkin(useLordSkin);
            }
        });

        setListVisibility(false);
    }

    private void onNameChanged(String newName) {
        this.entity.changeMinionName(newName);
    }

    private void setListVisibility(boolean show) {
        this.typeButton.setMessage(typeList.getMessage() + " " + (skinType + 1));
        this.typeList.visible = show;
        this.lordSkinButton.visible = !show;
    }

    private void skin(int type) {
        this.entity.setVampireType(this.skinType = type);
        setListVisibility(false);
    }
}