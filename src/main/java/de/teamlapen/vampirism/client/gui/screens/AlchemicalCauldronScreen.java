package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.lib.lib.util.MultilineTooltip;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.inventory.AlchemicalCauldronMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.BelowOrAboveWidgetTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class AlchemicalCauldronScreen extends AbstractContainerScreen<AlchemicalCauldronMenu> {
    public static final ResourceLocation BACKGROUND = VResourceLocation.mod("textures/gui/container/alchemical_cauldron.png");
    public static final ResourceLocation LIT_PROGRESS_SPRITE = VResourceLocation.mod("container/alchemical_cauldron/lit_progress");
    public static final ResourceLocation BURN_PROGRESS_SPRITE = VResourceLocation.mod("container/alchemical_cauldron/burn_progress");
    public static final ResourceLocation BUBBLES_PROGRESS_SPRITE = VResourceLocation.mod("container/alchemical_cauldron/bubbles_progress");
    private static final ResourceLocation ERROR_SPRITE = ResourceLocation.withDefaultNamespace("container/anvil/error");

    public AlchemicalCauldronScreen(@NotNull AlchemicalCauldronMenu inventorySlotsIn, @NotNull Inventory inventoryPlayer, @NotNull Component name) {
        super(inventorySlotsIn, inventoryPlayer, name);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.setColor(1, 1, 1, 1);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        graphics.blit(BACKGROUND, i, j, 0, 0, this.imageWidth, this.imageHeight);

        if (this.menu.isLit()) {
            int l = Mth.ceil(this.menu.getLitProgress() * 13) + 1;
            graphics.blitSprite(LIT_PROGRESS_SPRITE, 14, 14, 0, 14-l, i + 56, j + 36 + 14 - l, 14, l);
        }

        int j1 = Mth.ceil(this.menu.getBurnProgress() * 24.0F);
        graphics.blitSprite(BURN_PROGRESS_SPRITE, 24, 16, 0, 0, i + 79, j + 35, j1, 16);
        int l = Mth.ceil(menu.getBurnProgress() * 29F);
        graphics.blitSprite(BUBBLES_PROGRESS_SPRITE, 12,29,0, 29-l, i + 142, j + 28 + 30 - l, 12, l);

        this.menu.checkRecipeNoSkills().ifPresent(holder -> {
            boolean allSkills = HunterPlayer.get(this.minecraft.player).getSkillHandler().areSkillsEnabled(holder.value().getRequiredSkills());
            if (!allSkills) {
                graphics.blitSprite(ERROR_SPRITE, i + 77, j + 32,28,21);
            }
        });
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderTooltip(pGuiGraphics, pX, pY);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        if (pX > i + 77 && pX < i + 77 + 28 && pY > j + 32 && pY < j + 32 + 21) {
            this.menu.checkRecipeNoSkills().ifPresent(holder -> {
                List<ISkill<?>> missingSkills = holder.value().getRequiredSkills().stream().filter(s -> !HunterPlayer.get(this.minecraft.player).getSkillHandler().isSkillEnabled(s)).toList();
                if (!missingSkills.isEmpty()) {
                    List<Component> components = Stream.concat(Stream.of(Component.translatable("gui.vampirism.alchemical_cauldron.missing_skills").withStyle(ChatFormatting.RED)), missingSkills.stream().map(skill -> Component.literal("p- ").append(skill.getName()).withStyle(ChatFormatting.RED))).collect(Collectors.toUnmodifiableList());
                    setTooltipForNextRenderPass(new MultilineTooltip(components),new BelowOrAboveWidgetTooltipPositioner(new ScreenRectangle(i +77, j +32, 28, 21)), false);
                }
            });
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        Component name = Component.translatable("tile.vampirism.alchemical_cauldron.display", minecraft.player.getDisplayName().copy().withStyle(ChatFormatting.DARK_BLUE), ModBlocks.ALCHEMICAL_CAULDRON.get().getName());
        graphics.drawString(this.font, name, 5, 6, 0x404040, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

}
