package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.client.gui.ScrollableListButton;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.render.entities.HunterMinionRenderer;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.network.AppearancePacket;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HunterMinionAppearanceScreen extends AppearanceScreen<HunterMinionEntity> {

    private static final ITextComponent NAME = new TranslationTextComponent("gui.vampirism.minion_appearance");

    private int skinType;
    private int hatType;
    private boolean useLordSkin;

    public HunterMinionAppearanceScreen(HunterMinionEntity minion) {
        super(NAME, minion);
    }

    @Override
    public void onClose() {
        VampirismMod.dispatcher.sendToServer(new AppearancePacket(this.entity.getEntityId(), this.skinType, this.hatType, useLordSkin ? 1 : 0));
        super.onClose();
    }

    @Override
    protected void init() {
        super.init();
        this.skinType = this.entity.getHunterType();
        this.hatType = this.entity.getHatType();
        this.useLordSkin = this.entity.shouldRenderLordSkin();
        this.addList(new ScrollableListButton(this.guiLeft + 20, this.guiTop + 30 + 19, 99, 5, HunterMinionRenderer.TEXTURE_COUNT, null, UtilLib.translate("gui.vampirism.minion_appearance.skin"), this::skin, false));
        this.addList(new ScrollableListButton(this.guiLeft + 20, this.guiTop + 50 + 19, 99, 3, 3, null, UtilLib.translate("gui.vampirism.minion_appearance.hat"), this::hat, false));
        this.addButton(new CheckboxButton(this.guiLeft + 20, this.guiTop + 70, 99, 20, UtilLib.translate("gui.vampirism.minion_appearance.use_lord_skin"), useLordSkin) {
            @Override
            public void onPress() {
                super.onPress();
                useLordSkin = isChecked();
                entity.setUseLordSkin(useLordSkin);
            }
        });

    }

    private void hat(int type) {
        this.entity.setHatType(this.hatType = type);
    }

    private void skin(int type) {
        this.entity.setHunterType(this.skinType = type);
    }
}
