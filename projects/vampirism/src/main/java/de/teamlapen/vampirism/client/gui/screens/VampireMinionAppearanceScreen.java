package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.faction.api.world.entities.minion.IMinionData;
import de.teamlapen.faction.api.world.entities.minion.MinionAppearance;
import de.teamlapen.faction.common.network.packets.server.ServerboundCustomizationPacket;
import de.teamlapen.faction.common.world.entities.customization.CustomizationData;
import de.teamlapen.gui.components.DropdownWidget;
import de.teamlapen.faction.client.gui.screens.AppearanceScreen;
import de.teamlapen.faction.client.gui.screens.ILastScreenProvider;
import de.teamlapen.faction.common.factions.minions.MinionData;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.renderer.entities.VampireMinionRenderer;
import de.teamlapen.vampirism.common.world.entity.minion.VampireMinionEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

import java.util.stream.IntStream;

public class VampireMinionAppearanceScreen extends AppearanceScreen<VampireMinionEntity> {
    private static final Component NAME = Component.translatable("gui.vampirism.minion_appearance");

    private int skinType;
    private boolean useLordSkin;
    private boolean isMinionSpecificSkin;
    private int normalSkinCount;
    private int minionSkinCount;
    private String minionName = "";
    private final IMinionData data;

    public VampireMinionAppearanceScreen(VampireMinionEntity minion, ILastScreenProvider backScreen) {
        this.data = minion.getData().orElseThrow();
        super(NAME, minion, backScreen);

    }

    @Override
    public void removed() {
        String name = minionName;
        if (name.isEmpty()) {
            name = Component.translatable("gui.vampirism.minion_appearance.minion").getString() + entity.getMinionId().orElse(0);
        }
        VampirismMod.proxy.sendToServer(new ServerboundCustomizationPacket(this.entity.getId(), CustomizationData
                .with(MinionAppearance.NAME_TYPE, name)
                .with(MinionAppearance.SKIN_TYPE, this.skinType)
                .with(MinionAppearance.LORD_SKIN, this.useLordSkin)
                .with(MinionAppearance.MINION_SKIN, this.isMinionSpecificSkin)));
        super.removed();
    }

    @Override
    protected void init() {
        this.normalSkinCount = ((VampireMinionRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this.entity)).getVampireTextureCount();
        this.minionSkinCount = ((VampireMinionRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this.entity)).getMinionSpecificTextureCount(); //can be 0

        this.minionName = MinionAppearance.NAME_TYPE.currentValue(this.entity, data);
        this.skinType = MinionAppearance.SKIN_TYPE.currentValue(this.entity, data);
        this.isMinionSpecificSkin = MinionAppearance.MINION_SKIN.currentValue(this.entity, data);
        this.useLordSkin = MinionAppearance.LORD_SKIN.currentValue(this.entity, data);

        if (this.isMinionSpecificSkin && this.minionSkinCount > 0) {
            this.skinType = this.skinType % this.minionSkinCount;
        } else {
            this.skinType = this.skinType % this.normalSkinCount;
            this.isMinionSpecificSkin = false; //If this.isMinionSpecificSkin && this.minionSkinCount==0
        }
        super.init();
    }

    @Override
    protected LayoutElement createLayout() {
        LinearLayout vertical = LinearLayout.vertical()
                .spacing(4);

        var name = new EditBox(this.font,  98, 12, Component.translatable("gui.vampirism.minion_appearance.name"));
        name.setTextColorUneditable(-1);
        name.setTextColor(-1);
        name.insertText(this.minionName);
        name.setMaxLength(MinionData.MAX_NAME_LENGTH);
        name.setResponder(this::onNameChanged);
        vertical.addChild(name);

        vertical.addChild(DropdownWidget.simple(0,0)
                .width(99)
                .itemHeight(20)
                .maxVisibleItems(5)
                .initialSelection(this.skinType)
                .onSelect(this::skin)
                .onHover(this::previewSkin)
                .simpleItems(IntStream.range(0, this.normalSkinCount + this.minionSkinCount)
                        .mapToObj(type -> (Component) Component.translatable("gui.vampirism.minion_appearance.skin").append(" " + (type + 1)))
                        .toList())
                .build());

        vertical.addChild(Checkbox.builder(Component.translatable("gui.vampirism.minion_appearance.use_lord_skin"), this.font).selected(useLordSkin).onValueChange((checkBox, selected) -> {
            useLordSkin = selected;
            MinionAppearance.LORD_SKIN.setValue(entity, data, selected);
        }).build());

        return vertical;
    }

    private void onNameChanged(String newName) {
        this.minionName = newName;
        MinionAppearance.NAME_TYPE.setValue(this.entity, this.data, newName);
    }

    private void previewSkin(int type, boolean hovered) {
        boolean minionSpecific = type >= normalSkinCount;
        if (hovered) {
            MinionAppearance.SKIN_TYPE.setValue(this.entity, this.data, type);
            MinionAppearance.MINION_SKIN.setValue(this.entity, this.data, minionSpecific);
        } else {
            if (MinionAppearance.SKIN_TYPE.currentValue(this.entity, this.data) == type && MinionAppearance.MINION_SKIN.currentValue(this.entity, this.data) == minionSpecific) {
                MinionAppearance.SKIN_TYPE.setValue(this.entity, this.data, this.skinType);
                MinionAppearance.MINION_SKIN.setValue(this.entity, this.data, this.isMinionSpecificSkin);
            }
        }
    }

    private void skin(int type) {
        boolean minionSpecific = type >= normalSkinCount;
        MinionAppearance.SKIN_TYPE.setValue(this.entity, this.data, this.skinType = type);
        MinionAppearance.MINION_SKIN.setValue(this.entity, this.data, this.isMinionSpecificSkin = minionSpecific);
    }
}