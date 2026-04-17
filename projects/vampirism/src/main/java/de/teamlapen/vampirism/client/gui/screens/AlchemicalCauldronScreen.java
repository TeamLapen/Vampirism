package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.client.gui.GuiRenderer;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.inventory.AlchemicalCauldronMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class AlchemicalCauldronScreen extends AbstractContainerScreen<AlchemicalCauldronMenu> {
    public static final Identifier BACKGROUND = VIdentifier.mod("textures/gui/container/alchemical_cauldron.png");
    public static final Identifier LIT_PROGRESS_SPRITE = VIdentifier.mod("container/alchemical_cauldron/lit_progress");
    public static final Identifier BURN_PROGRESS_SPRITE = VIdentifier.mod("container/alchemical_cauldron/burn_progress");
    public static final Identifier BUBBLES_PROGRESS_SPRITE = VIdentifier.mod("container/alchemical_cauldron/bubbles_progress");
    private static final Identifier ERROR_SPRITE = VIdentifier.mod("container/anvil/error");

    public AlchemicalCauldronScreen(@NotNull AlchemicalCauldronMenu inventorySlotsIn, @NotNull Inventory inventoryPlayer, @NotNull Component name) {
        super(inventorySlotsIn, inventoryPlayer, name);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        GuiRenderer.blit(graphics, BACKGROUND, i, j, this.imageWidth, this.imageHeight);
        if (this.menu.isLit()) {
            int l = Mth.ceil(this.menu.getLitProgress() * 13) + 1;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LIT_PROGRESS_SPRITE, 14, 14, 0, 14 - l, i + 56, j + 36 + 14 - l, 14, l);
        }

        int j1 = Mth.ceil(this.menu.getBurnProgress() * 24.0F);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BURN_PROGRESS_SPRITE, 24, 16, 0, 0, i + 79, j + 35, j1, 16);
        int l = Mth.ceil(menu.getBurnProgress() * 29F);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BUBBLES_PROGRESS_SPRITE, 12, 29, 0, 29 - l, i + 142, j + 28 + 30 - l, 12, l);

        this.menu.checkRecipeNoSkills().ifPresent(holder -> {
            boolean allSkills = HunterPlayer.get(this.minecraft.player).getSkillHandler().areSkillsEnabled(holder.value().getRequiredSkills());
            if (!allSkills) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ERROR_SPRITE, i + 77, j + 32, 28, 21);
            }
        });
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        if (mouseX > i + 77 && mouseX < i + 77 + 28 && mouseY > j + 32 && mouseY < j + 32 + 21) {
            this.menu.checkRecipeNoSkills().ifPresent(holder -> {
                List<Holder<ISkill<?>>> missingSkills = holder.value().getRequiredSkills().stream().filter(s -> !HunterPlayer.get(this.minecraft.player).getSkillHandler().isSkillEnabled(s)).toList();
                if (!missingSkills.isEmpty()) {
                    List<Component> components = Stream.concat(Stream.of(Component.translatable("gui.vampirism.alchemical_cauldron.missing_skills").withStyle(ChatFormatting.RED)), missingSkills.stream().map(skill -> Component.literal("p- ").append(skill.value().getName()).withStyle(ChatFormatting.RED))).collect(Collectors.toUnmodifiableList());
                    graphics.setComponentTooltipForNextFrame(getFont(), components, i + 77, j + 23);
                }
            });
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        Component name = Component.translatable("container.vampirism.alchemical_cauldron.display", minecraft.player.getDisplayName().copy().withStyle(ChatFormatting.DARK_BLUE), ModBlocks.ALCHEMICAL_CAULDRON.get().getName());
        graphics.text(this.font, name, 5, 6, 0x404040, false);
        graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

}
