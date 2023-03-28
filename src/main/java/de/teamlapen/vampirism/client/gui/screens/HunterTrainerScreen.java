package de.teamlapen.vampirism.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.inventory.HunterTrainerMenu;
import de.teamlapen.vampirism.network.ServerboundSimpleInputEvent;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Gui for the Hunter Trainer interaction
 */
@OnlyIn(Dist.CLIENT)
public class HunterTrainerScreen extends ItemCombinerScreen<HunterTrainerMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/hunter_trainer.png");
    private static final ResourceLocation EMPTY_INGOT = new ResourceLocation("item/empty_slot_ingot");
    private static final ResourceLocation EMPTY_INTEL = new ResourceLocation(REFERENCE.MODID, "item/empty_hunter_intel");

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
        this.ironIcon.tick(requirement.filter(s -> s.iron() > 0).map(s -> List.of(EMPTY_INGOT)).orElse(List.of()));
        this.goldIcon.tick(requirement.filter(s -> s.gold() > 0).map(s -> List.of(EMPTY_INGOT)).orElse(List.of()));
        this.hunterIntel.tick(requirement.map(s -> List.of(EMPTY_INTEL)).orElse(List.of()));
    }

    @Override
    public void slotChanged(@NotNull AbstractContainerMenu pContainerToSend, int pSlotInd, @NotNull ItemStack pStack) {
        this.buttonLevelup.active = this.menu.canLevelup();
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderOnBoardingTooltips(pPoseStack, pMouseX, pMouseY);
    }

    private void renderOnBoardingTooltips(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        Optional<Component> optional = Optional.empty();

        if (this.hoveredSlot != null) {
            var req = this.menu.getRequirement();
            for (int i = 0; i < 3; i++) {
                if (this.hoveredSlot.index != i) continue;
                ItemStack stack = this.menu.getSlot(i).getItem();
                int finalI = i;
                var missing = req.map(s -> switch (finalI) {
                    case 0 -> s.iron();
                    case 1 -> s.gold();
                    default -> 1;
                } - stack.getCount()).orElse(0);
                if (missing > 0) {
                    optional = Optional.of(Component.translatable("text.vampirism.hunter_trainer.ritual_missing_items", missing, (switch (finalI) {
                        case 0 -> iron.getHoverName();
                        case 1 -> this.gold.getHoverName();
                        default -> req.map(s -> s.tableRequirement().intel().get()).map(item -> item.getName(item.getDefaultInstance())).orElseThrow();
                    }).getString()));
                    break;
                }
            }
            optional.ifPresent((p_274684_) -> {
                this.renderTooltip(pPoseStack, this.font.split(p_274684_, 115), pMouseX, pMouseY);
            });
        }
    }

    @Override
    protected void subInit() {
        Component name = Component.translatable("text.vampirism.level_up");
        int buttonWidth = this.font.width(name) + 10;
        this.addRenderableWidget(this.buttonLevelup = new ExtendedButton(this.leftPos + imageWidth - buttonWidth - 6, this.topPos + 45, buttonWidth, 20, name, (context) -> {
            VampirismMod.dispatcher.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Type.TRAINER_LEVELUP));
            Player player = Minecraft.getInstance().player;
            UtilLib.spawnParticles(player.getCommandSenderWorld(), ParticleTypes.ENCHANT, player.getX(), player.getY(), player.getZ(), 1, 1, 1, 100, 1);
            player.playSound(SoundEvents.NOTE_BLOCK_HARP.get(), 4.0F, (1.0F + (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F) * 0.7F);
            this.onClose();
        }));
        this.buttonLevelup.active = false;
    }

    @Override
    protected void renderBg(@NotNull PoseStack stack, float var1, int var2, int var3) {
        super.renderBg(stack, var1, var2, var3);
        this.ironIcon.render(this.menu, stack, var1, this.leftPos, this.topPos);
        this.goldIcon.render(this.menu, stack, var1, this.leftPos, this.topPos);
        this.hunterIntel.render(this.menu, stack, var1, this.leftPos, this.topPos);
    }

    @Override
    protected void renderErrorIcon(@NotNull PoseStack poseStack, int mouseX, int mouseY) {

    }
}
