package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.blockentity.VampireBeaconBlockEntity;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.inventory.VampireBeaconMenu;
import de.teamlapen.vampirism.network.ServerboundSetVampireBeaconPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VampireBeaconScreen extends AbstractContainerScreen<VampireBeaconMenu> {
    private static final ResourceLocation BEACON_LOCATION = VResourceLocation.mod("textures/gui/container/vampire_beacon.png");
    static final ResourceLocation BUTTON_DISABLED_SPRITE = VResourceLocation.mc("container/beacon/button_disabled");
    static final ResourceLocation BUTTON_SELECTED_SPRITE = VResourceLocation.mc("container/beacon/button_selected");
    static final ResourceLocation BUTTON_HIGHLIGHTED_SPRITE = VResourceLocation.mc("container/beacon/button_highlighted");
    static final ResourceLocation BUTTON_SPRITE = VResourceLocation.mc("container/beacon/button");
    static final ResourceLocation CONFIRM_SPRITE = VResourceLocation.mc("container/beacon/confirm");
    static final ResourceLocation CANCEL_SPRITE = VResourceLocation.mc("container/beacon/cancel");
    private static final Component EFFECT_LABEL = Component.translatable("container.vampirism.vampire_beacon.power");
    private final List<BeaconButton> beaconButtons = new ArrayList<>();
    @Nullable
    private Holder<MobEffect> primary;
    private int amplifier;
    private boolean isUpgraded;

    public VampireBeaconScreen(VampireBeaconMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 230;
        this.imageHeight = 219;
        pMenu.addSlotListener(new ContainerListener() {
            @Override
            public void slotChanged(@NotNull AbstractContainerMenu pContainerToSend, int pDataSlotIndex, @NotNull ItemStack pStack) {
            }

            @Override
            public void dataChanged(@NotNull AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {
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

    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawCenteredString(this.font, EFFECT_LABEL, 62 + 55, 10, 14737632);
    }

    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(BEACON_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
        pGuiGraphics.renderItem(new ItemStack(ModItems.PURE_BLOOD_0.get()), i + 41, j + 109);
        pGuiGraphics.renderItem(new ItemStack(ModItems.SOUL_ORB_VAMPIRE.get()), i + 41 + 22, j + 109);
        pGuiGraphics.renderItem(new ItemStack(ModItems.HUMAN_HEART.get()), i + 42 + 44, j + 109);
        pGuiGraphics.renderItem(new ItemStack(ModItems.WEAK_HUMAN_HEART.get()), i + 42 + 66, j + 109);
        pGuiGraphics.pose().popPose();
    }

    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    interface BeaconButton {
        void updateStatus(int beaconTier);
    }

    class BeaconCancelButton extends BeaconSpriteScreenButton {
        public BeaconCancelButton(int pX, int pY) {
            super(pX, pY, CANCEL_SPRITE, CommonComponents.GUI_CANCEL);
        }

        public void onPress() {
            VampireBeaconScreen.this.minecraft.player.closeContainer();
        }

        public void updateStatus(int pBeaconTier) {
        }
    }

    @OnlyIn(Dist.CLIENT)
    class BeaconConfirmButton extends BeaconSpriteScreenButton {
        public BeaconConfirmButton(int pX, int pY) {
            super(pX, pY, CONFIRM_SPRITE, CommonComponents.GUI_DONE);
        }

        public void onPress() {
            VampirismMod.proxy.sendToServer(new ServerboundSetVampireBeaconPacket(Optional.ofNullable(VampireBeaconScreen.this.primary), Optional.of(VampireBeaconScreen.this.amplifier)));
            VampireBeaconScreen.this.minecraft.player.closeContainer();
        }

        public void updateStatus(int pBeaconTier) {
            this.active = VampireBeaconScreen.this.menu.hasPayment() && VampireBeaconScreen.this.primary != null;
        }
    }

    @OnlyIn(Dist.CLIENT)
    class BeaconPowerButton extends BeaconScreenButton {
        protected final int tier;
        private Holder<MobEffect> effect;
        private int effectAmplifier;
        private TextureAtlasSprite sprite;

        public BeaconPowerButton(int pX, int pY, Holder<MobEffect> pEffect, int effectAmplifier, int pTier) {
            super(pX, pY);
            this.effectAmplifier = effectAmplifier;
            this.tier = pTier;
            this.setEffect(pEffect, effectAmplifier);
        }

        protected void setEffect(Holder<MobEffect> pEffect, int effectAmplifier) {
            this.effect = pEffect;
            this.effectAmplifier = effectAmplifier;
            this.sprite = Minecraft.getInstance().getMobEffectTextures().get(pEffect);
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

        public void onPress() {
            if (!this.isSelected()) {
                VampireBeaconScreen.this.primary = this.effect;
                VampireBeaconScreen.this.amplifier = this.effectAmplifier;

                VampireBeaconScreen.this.updateButtons();
            }
        }

        protected void renderIcon(GuiGraphics pGuiGraphics) {
            pGuiGraphics.blit(this.getX() + 2, this.getY() + 2, 0, 18, 18, this.sprite);
        }

        public void updateStatus(int pBeaconTier) {
            this.active = this.tier < pBeaconTier;
            this.setSelected(this.effect == VampireBeaconScreen.this.primary);
        }

        protected @NotNull MutableComponent createNarrationMessage() {
            return this.createEffectDescription(this.effect, this.effectAmplifier);
        }
    }

    @OnlyIn(Dist.CLIENT)
    abstract static class BeaconScreenButton extends AbstractButton implements BeaconButton {
        private boolean selected;

        protected BeaconScreenButton(int pX, int pY) {
            super(pX, pY, 22, 22, CommonComponents.EMPTY);
        }

        protected BeaconScreenButton(int pX, int pY, Component pMessage) {
            super(pX, pY, 22, 22, pMessage);
        }

        public void renderWidget(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            ResourceLocation resourcelocation;
            if (!this.active) {
                resourcelocation = BUTTON_DISABLED_SPRITE;
            } else if (this.selected) {
                resourcelocation = BUTTON_SELECTED_SPRITE;
            } else if (this.isHoveredOrFocused()) {
                resourcelocation = BUTTON_HIGHLIGHTED_SPRITE;
            } else {
                resourcelocation = BUTTON_SPRITE;
            }

            pGuiGraphics.blitSprite(resourcelocation, this.getX(), this.getY(), this.width, this.height);
            this.renderIcon(pGuiGraphics);
        }

        protected abstract void renderIcon(GuiGraphics pGuiGraphics);

        public boolean isSelected() {
            return this.selected;
        }

        public void setSelected(boolean pSelected) {
            this.selected = pSelected;
        }

        public void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {
            this.defaultButtonNarrationText(pNarrationElementOutput);
        }
    }

    @OnlyIn(Dist.CLIENT)
    abstract static class BeaconSpriteScreenButton extends BeaconScreenButton {
        private final ResourceLocation sprite;

        protected BeaconSpriteScreenButton(int pX, int pY, ResourceLocation pSprite, Component pMessage) {
            super(pX, pY, pMessage);
            this.sprite = pSprite;

        }

        protected void renderIcon(GuiGraphics pGuiGraphics) {
            pGuiGraphics.blitSprite(this.sprite, this.getX() + 2, this.getY() + 2, 18, 18);
        }
    }

}
