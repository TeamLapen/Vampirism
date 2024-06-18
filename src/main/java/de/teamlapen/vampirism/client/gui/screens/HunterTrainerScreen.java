package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.inventory.HunterTrainerMenu;
import de.teamlapen.vampirism.network.ServerboundSimpleInputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Gui for the Hunter Trainer interaction
 */
public class HunterTrainerScreen extends ItemCombinerScreen<HunterTrainerMenu> {
    private static final ResourceLocation BACKGROUND = VResourceLocation.mod("textures/gui/container/hunter_trainer.png");
    private static final ResourceLocation EMPTY_INGOT = VResourceLocation.mc("item/empty_slot_ingot");
    private static final ResourceLocation EMPTY_INTEL = VResourceLocation.mod("item/empty_hunter_intel");

    private Button buttonLevelup;

    private final CyclingSlotBackground ironIcon = new CyclingSlotBackground(0);
    private final CyclingSlotBackground goldIcon = new CyclingSlotBackground(1);
    private final CyclingSlotBackground hunterIntel = new CyclingSlotBackground(2);

    private final ItemStack iron;
    private final ItemStack gold;


    public HunterTrainerScreen(@NotNull HunterTrainerMenu inventorySlotsIn, @NotNull Inventory playerInventory, @NotNull Component name) {
        super(inventorySlotsIn, playerInventory, name, BACKGROUND);
        this.iron = Items.IRON_INGOT.getDefaultInstance();
        this.gold = Items.GOLD_INGOT.getDefaultInstance();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        var requirement = this.menu.getRequirement();
        this.ironIcon.tick(requirement.filter(s -> s.ironQuantity() > 0).map(s -> List.of(EMPTY_INGOT)).orElse(List.of()));
        this.goldIcon.tick(requirement.filter(s -> s.goldQuantity() > 0).map(s -> List.of(EMPTY_INGOT)).orElse(List.of()));
        this.hunterIntel.tick(requirement.map(s -> List.of(EMPTY_INTEL)).orElse(List.of()));
    }

    @Override
    public void slotChanged(@NotNull AbstractContainerMenu pContainerToSend, int pSlotInd, @NotNull ItemStack pStack) {
        this.buttonLevelup.active = this.menu.canLevelup();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        this.renderOnBoardingTooltips(graphics, pMouseX, pMouseY);
    }

    private void renderOnBoardingTooltips(GuiGraphics graphics, int pMouseX, int pMouseY) {
        if (this.hoveredSlot != null && this.hoveredSlot.index < 3) {
            Optional<Component> optional = Optional.empty();
            var req = this.menu.getRequirement();
            ItemStack stack = this.hoveredSlot.getItem();
            var missing = req.map(s -> switch (this.hoveredSlot.index) {
                case 0 -> s.ironQuantity() - stack.getCount();
                case 1 -> s.goldQuantity() - stack.getCount();
                case 2 -> 1 - stack.getCount();
                default -> 0;
            }).orElse(0);
            if (missing > 0) {
                optional = Optional.of(Component.translatable("text.vampirism.hunter_trainer.ritual_missing_items", missing, (switch (this.hoveredSlot.index) {
                    case 0 -> this.iron.getHoverName();
                    case 1 -> this.gold.getHoverName();
                    case 2 -> req.map(s -> s.tableRequirement().resultIntelItem().get().getCustomName()).orElseGet(Component::empty);
                    default -> throw new IllegalStateException("Unexpected value: " + this.hoveredSlot.index);
                }).getString()));
            }
            optional.ifPresent((p_274684_) -> {
                graphics.renderTooltip(this.font, this.font.split(p_274684_, 115), pMouseX, pMouseY);
            });
        }
    }

    @Override
    protected void subInit() {
        Component name = Component.translatable("text.vampirism.level_up");
        int buttonWidth = this.font.width(name) + 10;
        this.addRenderableWidget(this.buttonLevelup = new ExtendedButton(this.leftPos + imageWidth - buttonWidth - 6, this.topPos + 45, buttonWidth, 20, name, (context) -> {
            VampirismMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.TRAINER_LEVELUP));
            Player player = Minecraft.getInstance().player;
            UtilLib.spawnParticles(player.getCommandSenderWorld(), ParticleTypes.ENCHANT, player.getX(), player.getY(), player.getZ(), 1, 1, 1, 100, 1);
            player.playSound(SoundEvents.NOTE_BLOCK_HARP.value(), 4.0F, (1.0F + (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F) * 0.7F);
            this.onClose();
        }));
        this.buttonLevelup.active = false;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float var1, int var2, int var3) {
        super.renderBg(graphics, var1, var2, var3);
        this.ironIcon.render(this.menu, graphics, var1, this.leftPos, this.topPos);
        this.goldIcon.render(this.menu, graphics, var1, this.leftPos, this.topPos);
        this.hunterIntel.render(this.menu, graphics, var1, this.leftPos, this.topPos);
    }

    @Override
    protected void renderErrorIcon(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {

    }
}
