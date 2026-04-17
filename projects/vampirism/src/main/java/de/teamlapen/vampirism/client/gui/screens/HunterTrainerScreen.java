package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.network.packets.server.ServerboundSimpleInputEvent;
import de.teamlapen.vampirism.common.util.UtilLib;
import de.teamlapen.vampirism.common.world.inventory.HunterTrainerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

import java.util.List;

public class HunterTrainerScreen extends ItemCombinerScreen<HunterTrainerMenu> {

    private static final Identifier EMPTY_SLOT_INGOT = VIdentifier.mc("container/slot/ingot");
    private static final Identifier EMPTY_SLOT_HUNTER_INTEL = VIdentifier.mod("container/slot/hunter_intel");
    private static final Identifier BACKGROUND_LOCATION = VIdentifier.mod("textures/gui/container/hunter_trainer.png");

    private Button buttonLevelUp;

    private final CyclingSlotBackground ironIcon = new CyclingSlotBackground(0);
    private final CyclingSlotBackground goldIcon = new CyclingSlotBackground(1);
    private final CyclingSlotBackground hunterIntelIcon = new CyclingSlotBackground(2);

    public HunterTrainerScreen(HunterTrainerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, BACKGROUND_LOCATION);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        var requirement = this.menu.getRequirement();
        this.ironIcon.tick(requirement.filter(r -> r.ironQuantity() > 0).map(r -> List.of(EMPTY_SLOT_INGOT)).orElse(List.of()));
        this.goldIcon.tick(requirement.filter(r -> r.goldQuantity() > 0).map(r -> List.of(EMPTY_SLOT_INGOT)).orElse(List.of()));
        this.hunterIntelIcon.tick(requirement.map(r -> List.of(EMPTY_SLOT_HUNTER_INTEL)).orElse(List.of()));
    }

    @Override
    public void slotChanged(AbstractContainerMenu container, int slotIndex, ItemStack stack) {
        this.buttonLevelUp.active = this.menu.canLevelup();
    }


    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (this.hoveredSlot != null && this.hoveredSlot.index >= 0 && this.hoveredSlot.index < 3) {
            var requirementOpt = this.menu.getRequirement();
            if (requirementOpt.isPresent()) {
                var requirement = requirementOpt.get();
                int slot = this.hoveredSlot.index;
                ItemStack stack = this.hoveredSlot.getItem();
                Component tooltip = null;

                switch (slot) {
                    case 0 -> {
                        int missing = requirement.ironQuantity() - stack.getCount();
                        if (missing > 0) {
                            tooltip = Component.translatable("gui.vampirism.hunter_trainer.ritual_missing_iron", missing);
                        }
                    }
                    case 1 -> {
                        int missing = requirement.goldQuantity() - stack.getCount();
                        if (missing > 0) {
                            tooltip = Component.translatable("gui.vampirism.hunter_trainer.ritual_missing_gold", missing);
                        }
                    }
                    case 2 -> {
                        int requiredLevel = requirement.targetLevel();
                        if (stack.isEmpty()) {
                            tooltip = Component.translatable("gui.vampirism.hunter_trainer.ritual_missing_hunter_intel", requiredLevel);
                        }
                    }
                }

                if (tooltip != null) {
                    ClientTooltipComponent clientTooltip = ClientTooltipComponent.create(tooltip.getVisualOrderText());
                    graphics.tooltip(this.font, List.of(clientTooltip), mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, stack.get(DataComponents.TOOLTIP_STYLE));
                    return;
                }
            }
        }

        super.extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void subInit() {
        Component name = Component.translatable("gui.vampirism.level_up");
        int buttonWidth = this.font.width(name) + 10;
        this.addRenderableWidget(this.buttonLevelUp = new ExtendedButton(
                this.leftPos + imageWidth - buttonWidth - 6, this.topPos + 45,
                buttonWidth, 20, name,
                button -> {
                    VampirismMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.TRAINER_LEVELUP));
                    Player player = Minecraft.getInstance().player;
                    UtilLib.spawnParticles(player.level(), ParticleTypes.ENCHANT, player.getX(), player.getY(), player.getZ(), 1, 1, 1, 100, 1);
                    player.playSound(SoundEvents.NOTE_BLOCK_HARP.value(), 4.0F, (1.0F + (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F) * 0.7F);
                    this.onClose();
                }
        ));
        this.buttonLevelUp.active = false;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        this.ironIcon.extractRenderState(this.menu, graphics, a, this.leftPos, this.topPos);
        this.goldIcon.extractRenderState(this.menu, graphics, a, this.leftPos, this.topPos);
        this.hunterIntelIcon.extractRenderState(this.menu, graphics, a, this.leftPos, this.topPos);
    }

    @Override
    protected void extractErrorIcon(GuiGraphicsExtractor graphics, int xo, int yo) {
    }
}
