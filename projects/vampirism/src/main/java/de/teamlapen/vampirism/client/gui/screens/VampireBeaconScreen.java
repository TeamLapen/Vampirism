package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.faction.client.gui.GuiRenderer;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.network.packets.server.ServerboundSetVampireBeaconPacket;
import de.teamlapen.vampirism.common.world.blockentity.VampireBeaconBlockEntity;
import de.teamlapen.vampirism.common.world.inventory.VampireBeaconMenu;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VampireBeaconScreen extends AbstractContainerScreen<VampireBeaconMenu> {
    private static final Identifier BEACON_LOCATION = VIdentifier.mod("textures/gui/container/vampire_beacon.png");
    static final Identifier BUTTON_DISABLED_SPRITE = VIdentifier.mc("container/beacon/button_disabled");
    static final Identifier BUTTON_SELECTED_SPRITE = VIdentifier.mc("container/beacon/button_selected");
    static final Identifier BUTTON_HIGHLIGHTED_SPRITE = VIdentifier.mc("container/beacon/button_highlighted");
    static final Identifier BUTTON_SPRITE = VIdentifier.mc("container/beacon/button");
    static final Identifier CONFIRM_SPRITE = VIdentifier.mc("container/beacon/confirm");
    static final Identifier CANCEL_SPRITE = VIdentifier.mc("container/beacon/cancel");
    private static final Component EFFECT_LABEL = Component.translatable("gui.vampirism.vampire_beacon.power");
    private final List<BeaconButton> beaconButtons = new ArrayList<>();
    @Nullable
    private Holder<MobEffect> primary;
    private int amplifier;
    private boolean isUpgraded;

    public VampireBeaconScreen(VampireBeaconMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, 230, 219);
        pMenu.addSlotListener(new ContainerListener() {
            @Override
            public void slotChanged(AbstractContainerMenu pContainerToSend, int pDataSlotIndex, ItemStack pStack) {
            }

            @Override
            public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {
                VampireBeaconScreen.this.primary = menu.getPrimaryEffect();
                VampireBeaconScreen.this.amplifier = menu.getAmplifier();
                VampireBeaconScreen.this.isUpgraded = menu.isUpgraded();
                VampireBeaconScreen.this.children().forEach(pWidget -> {
                    if (pWidget instanceof BeaconPowerButton button) {
                        button.updateTooltip();
                    }
                });
            }
        });
    }

    private <T extends AbstractWidget & BeaconButton> void addBeaconButton(T button) {
        this.addRenderableWidget(button);
        this.beaconButtons.add(button);
    }

    @Override
    protected void init() {
        super.init();
        this.beaconButtons.clear();
        this.addBeaconButton(new BeaconConfirmButton(this.leftPos + 164, this.topPos + 107));
        this.addBeaconButton(new BeaconCancelButton(this.leftPos + 190, this.topPos + 107));

        for (int i = 0; i <= 2; ++i) {
            int j = VampireBeaconBlockEntity.BEACON_EFFECTS[i].length;
            int k = j * 22 + (j - 1) * 2;

            for (int l = 0; l < j; ++l) {
                Holder<MobEffect> mobeffect = VampireBeaconBlockEntity.BEACON_EFFECTS[i][l];
                int amplifier = VampireBeaconBlockEntity.BEACON_EFFECTS_AMPLIFIER[i][l];
                BeaconPowerButton beaconscreen$beaconpowerbutton = new BeaconPowerButton(this.leftPos + 76 + 62 + l * 24 - k / 2, this.topPos + 22 + i * 25, mobeffect, amplifier, i);
                beaconscreen$beaconpowerbutton.active = false;
                this.addBeaconButton(beaconscreen$beaconpowerbutton);
            }
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.updateButtons();
    }

    void updateButtons() {
        int i = this.menu.getLevels();
        this.beaconButtons.forEach((p_169615_) -> {
            p_169615_.updateStatus(i);
        });
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        graphics.centeredText(this.font, EFFECT_LABEL, 62 + 55, 10, 14737632);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        GuiRenderer.blit(graphics, BEACON_LOCATION, i, j, this.imageWidth, this.imageHeight);
        graphics.pose().pushMatrix();
        graphics.pose().translate(0.0F, 0.0F/*, 100.0F*/);
        graphics.item(new ItemStack(ModItems.PURE_BLOOD_0.get()), i + 41, j + 109);
        graphics.item(new ItemStack(ModItems.SOUL_ORB_VAMPIRE.get()), i + 41 + 22, j + 109);
        graphics.item(new ItemStack(ModItems.HUMAN_HEART.get()), i + 42 + 44, j + 109);
        graphics.item(new ItemStack(ModItems.WEAK_HUMAN_HEART.get()), i + 42 + 66, j + 109);
        graphics.pose().popMatrix();
    }

    interface BeaconButton {
        void updateStatus(int beaconTier);
    }

    class BeaconCancelButton extends BeaconSpriteScreenButton {
        public BeaconCancelButton(int pX, int pY) {
            super(pX, pY, CANCEL_SPRITE, CommonComponents.GUI_CANCEL);
        }


        @Override
        public void onPress(InputWithModifiers input) {
            VampireBeaconScreen.this.minecraft.player.closeContainer();
        }

        public void updateStatus(int pBeaconTier) {
        }
    }

    class BeaconConfirmButton extends BeaconSpriteScreenButton {
        public BeaconConfirmButton(int pX, int pY) {
            super(pX, pY, CONFIRM_SPRITE, CommonComponents.GUI_DONE);
        }

        @Override
        public void onPress(InputWithModifiers input) {
            VampirismMod.proxy.sendToServer(new ServerboundSetVampireBeaconPacket(Optional.ofNullable(VampireBeaconScreen.this.primary), Optional.of(VampireBeaconScreen.this.amplifier)));
            VampireBeaconScreen.this.minecraft.player.closeContainer();
        }

        public void updateStatus(int pBeaconTier) {
            this.active = VampireBeaconScreen.this.menu.hasPayment() && VampireBeaconScreen.this.primary != null;
        }
    }

    class BeaconPowerButton extends BeaconScreenButton {
        protected final int tier;
        private Holder<MobEffect> effect;
        private int effectAmplifier;
        private Identifier sprite;

        public BeaconPowerButton(int pX, int pY, Holder<MobEffect> pEffect, int effectAmplifier, int pTier) {
            super(pX, pY);
            this.effectAmplifier = effectAmplifier;
            this.tier = pTier;
            this.setEffect(pEffect, effectAmplifier);
        }

        protected void setEffect(Holder<MobEffect> pEffect, int effectAmplifier) {
            this.effect = pEffect;
            this.effectAmplifier = effectAmplifier;
            this.sprite = Gui.getMobEffectSprite(pEffect);
            this.updateTooltip();
        }

        public void updateTooltip() {
            this.setTooltip(Tooltip.create(this.createEffectDescription(this.effect, this.effectAmplifier), null));
        }

        protected MutableComponent createEffectDescription(Holder<MobEffect> pEffect, int amplifier) {
            MutableComponent component = Component.translatable(pEffect.value().getDescriptionId());
            if (!VampireBeaconBlockEntity.NO_AMPLIFIER_EFFECTS.contains(pEffect)) {
                amplifier += VampireBeaconScreen.this.menu.isUpgraded() ? 1 : 0;
            }
            if (amplifier <= 0) {
                return component;
            }
            return Component.translatable("potion.withAmplifier", component, Component.translatable("potion.potency." + amplifier));
        }

        @Override
        public void onPress(InputWithModifiers input) {
            if (!this.isSelected()) {
                VampireBeaconScreen.this.primary = this.effect;
                VampireBeaconScreen.this.amplifier = this.effectAmplifier;

                VampireBeaconScreen.this.updateButtons();
            }
        }

        protected void renderIcon(GuiGraphicsExtractor pGuiGraphicsExtractor) {
            pGuiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, this.getX() + 2, this.getY() + 2, 18, 18);
        }

        public void updateStatus(int pBeaconTier) {
            this.active = this.tier < pBeaconTier;
            this.setSelected(this.effect == VampireBeaconScreen.this.primary);
        }

        protected MutableComponent createNarrationMessage() {
            return this.createEffectDescription(this.effect, this.effectAmplifier);
        }
    }

    abstract static class BeaconScreenButton extends AbstractButton implements BeaconButton {
        private boolean selected;

        protected BeaconScreenButton(int pX, int pY) {
            super(pX, pY, 22, 22, CommonComponents.EMPTY);
        }

        protected BeaconScreenButton(int pX, int pY, Component pMessage) {
            super(pX, pY, 22, 22, pMessage);
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {

            Identifier resourcelocation;
            if (!this.active) {
                resourcelocation = BUTTON_DISABLED_SPRITE;
            } else if (this.selected) {
                resourcelocation = BUTTON_SELECTED_SPRITE;
            } else if (this.isHoveredOrFocused()) {
                resourcelocation = BUTTON_HIGHLIGHTED_SPRITE;
            } else {
                resourcelocation = BUTTON_SPRITE;
            }

            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, resourcelocation, this.getX(), this.getY(), this.width, this.height);
            this.renderIcon(graphics);
        }

        protected abstract void renderIcon(GuiGraphicsExtractor pGuiGraphicsExtractor);

        public boolean isSelected() {
            return this.selected;
        }

        public void setSelected(boolean pSelected) {
            this.selected = pSelected;
        }

        public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
            this.defaultButtonNarrationText(pNarrationElementOutput);
        }
    }

    abstract static class BeaconSpriteScreenButton extends BeaconScreenButton {
        private final Identifier sprite;

        protected BeaconSpriteScreenButton(int pX, int pY, Identifier pSprite, Component pMessage) {
            super(pX, pY, pMessage);
            this.sprite = pSprite;

        }

        protected void renderIcon(GuiGraphicsExtractor pGuiGraphicsExtractor) {
            pGuiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, this.getX() + 2, this.getY() + 2, 18, 18);
        }
    }

}
