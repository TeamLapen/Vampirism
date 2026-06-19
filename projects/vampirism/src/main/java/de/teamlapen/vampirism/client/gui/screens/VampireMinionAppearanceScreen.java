package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.gui.components.DropdownWidget;
import de.teamlapen.faction.client.gui.screens.AppearanceScreen;
import de.teamlapen.faction.client.gui.screens.ILastScreenProvider;
import de.teamlapen.faction.common.factions.minions.MinionData;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.renderer.entities.VampireMinionRenderer;
import de.teamlapen.vampirism.common.network.packets.server.ServerboundAppearancePacket;
import de.teamlapen.vampirism.common.world.entity.minion.VampireMinionEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.IntStream;

public class VampireMinionAppearanceScreen extends AppearanceScreen<VampireMinionEntity> {
    private static final Component NAME = Component.translatable("gui.vampirism.minion_appearance");

    private int skinType;
    private boolean useLordSkin;
    private boolean isMinionSpecificSkin;
    private int normalSkinCount;
    private int minionSkinCount;
    private String minionName;

    public VampireMinionAppearanceScreen(VampireMinionEntity minion, ILastScreenProvider backScreen) {
        super(NAME, minion, backScreen);
    }

    @Override
    public void removed() {
        String name = minionName;
        if (name.isEmpty()) {
            name = Component.translatable("gui.vampirism.minion_appearance.minion").getString() + entity.getMinionId().orElse(0);
        }
        Map<de.teamlapen.faction.common.world.entities.appearance.AppearanceKey<?>, Object> map = new java.util.HashMap<>();
        map.put(MinionData.NameType, name);
        map.put(MinionData.SkinType, this.skinType);
        map.put(MinionData.AppearanceType, (isMinionSpecificSkin ? 0b10 : 0b0) | (useLordSkin ? 0b1 : 0b0));
        VampirismMod.proxy.sendToServer(new ServerboundAppearancePacket(this.entity.getId(), new de.teamlapen.faction.common.world.entities.appearance.AppearancePacket(map)));
        super.removed();
    }

    @Override
    protected void init() {
        this.minionName = this.entity.getMinionData().map(MinionData::getName).orElse("");
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
        super.init();
    }

    @Override
    protected @NotNull LayoutElement createLayout() {
        LinearLayout vertical = LinearLayout.vertical()
                .spacing(4);

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

    private void onNameChanged(String newName) {
        this.minionName = newName;
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

    private void skin(int type) {
        boolean minionSpecific = type >= normalSkinCount;
        this.entity.setVampireType(this.skinType = type, this.isMinionSpecificSkin = minionSpecific);
    }
}