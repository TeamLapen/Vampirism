package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.client.gui.ScrollableListButton;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.render.entities.VampireMinionRenderer;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.network.AppearancePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.config.GuiButtonExt;

@OnlyIn(Dist.CLIENT)
public class VampireMinionAppearanceScreen extends AppearanceScreen<VampireMinionEntity> {
    private static final ITextComponent NAME = new TranslationTextComponent("gui.vampirism.minion_appearance");

    private int skinType;
    private boolean useLordSkin;
    private ScrollableListButton typeList;
    private GuiButtonExt typeButton;
    private CheckboxButton lordSkinButton;

    public VampireMinionAppearanceScreen(VampireMinionEntity minion) {
        super(NAME, minion);
    }

    @Override
    public void onClose() {
        VampirismMod.dispatcher.sendToServer(new AppearancePacket(this.entity.getEntityId(), this.skinType, useLordSkin ? 1 : 0));
        super.onClose();
    }

    @Override
    protected void init() {
        super.init();

        this.skinType = this.entity.getVampireType();
        this.useLordSkin = this.entity.shouldRenderLordSkin();
        typeList = this.addButton(new ScrollableListButton(this.guiLeft + 20, this.guiTop + 30 + 19, 99, 5, ((VampireMinionRenderer) Minecraft.getInstance().getRenderManager().getRenderer(this.entity)).getTextureLength(), null, UtilLib.translate("gui.vampirism.minion_appearance.skin"), this::skin, false));
        typeButton = this.addButton(new GuiButtonExt(this.guiLeft + 20, this.guiTop + 30, 100, 20, typeList.getMessage() + " " + (skinType + 1), (button1 -> {
            this.typeList.visible = !this.typeList.visible;
            this.lordSkinButton.visible = !this.typeList.visible;
        })));
        typeList.visible = false;
        this.lordSkinButton = this.addButton(new CheckboxButton(this.guiLeft + 20, this.guiTop + 50, 99, 20, UtilLib.translate("gui.vampirism.minion_appearance.use_lord_skin"), useLordSkin) {
            @Override
            public void onPress() {
                super.onPress();
                useLordSkin = isChecked();
                entity.setUseLordSkin(useLordSkin);
            }
        });

    }

    private void skin(int type) {
        this.entity.setVampireType(this.skinType = type);
        this.typeList.visible = false;
        this.lordSkinButton.visible = true;
        this.typeButton.setMessage(typeList.getMessage() + " " + (type + 1)); //TODO 1.15 TODO 1.16 make more elegant
    }
}
