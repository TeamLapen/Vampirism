package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.client.gui.ScrollableListButton;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.render.entities.VampireMinionRenderer;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.network.AppearancePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VampireMinionAppearanceScreen extends AppearanceScreen<VampireMinionEntity> {
    private static final ITextComponent NAME = new TranslationTextComponent("gui.vampirism.minion_appearance");

    private int skinType;

    public VampireMinionAppearanceScreen(VampireMinionEntity minion) {
        super(NAME, minion);
    }

    @Override
    public void onClose() {
        VampirismMod.dispatcher.sendToServer(new AppearancePacket(this.entity.getEntityId(), this.skinType));
        super.onClose();
    }

    @Override
    protected void init() {
        super.init();

        this.addList(new ScrollableListButton(this.guiLeft + 20, this.guiTop + 30 + 19, 99, 5, ((VampireMinionRenderer) Minecraft.getInstance().getRenderManager().getRenderer(this.entity)).getTextureLength() + 1, null, UtilLib.translate("gui.vampirism.minion_appearance.skin"), this::skin, false));

        this.skinType = this.entity.getVampireType();
    }

    private void skin(int type) {
        this.entity.setVampireType(this.skinType = type);
    }
}
