package de.teamlapen.factions.client.gui.components;

import de.teamlapen.factions.api.skills.IActionSkill;
import de.teamlapen.factions.api.skills.ISkill;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SkillDisplayWidget extends AbstractWidget {

    private final Minecraft minecraft;
    private final int xOffset;
    private final int yOffset;
    private final Holder<ISkill<?>> skill;
    private final boolean tooltip;

    public SkillDisplayWidget(Minecraft minecraft, int xOffset, int yOffset, int width, int height, Component message, Holder<ISkill<?>> skill, boolean tooltip) {
        super(0, 0, width, height, message);
        this.minecraft = minecraft;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.skill = skill;
        this.tooltip = tooltip;
    }

    @SuppressWarnings({"DataFlowIssue", "SwitchStatementWithTooFewBranches"})
    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation texture = switch (this.skill.value()) {
            case IActionSkill<?> actionSkill ->
                    this.skill.getKey().location().withPath(path -> "textures/actions/" + path + ".png");
            default -> this.skill.getKey().location().withPath(path -> "textures/skills/" + path + ".png");
        };
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.getX() + this.xOffset, this.getY() + this.yOffset, 0, 0, 16, 16, 16, 16);

        if (this.isFocused()) {
            guiGraphics.submitOutline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), -1);
        }

        if (this.tooltip && this.isHoveredOrFocused()) {
            this.renderTooltip(guiGraphics, mouseX, mouseY);
        }
    }

    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.setTooltipForNextFrame(this.minecraft.font, this.skill.value().getName(), mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, Component.translatable("narration.factions.skill", this.skill.value().getName()));
    }
}
