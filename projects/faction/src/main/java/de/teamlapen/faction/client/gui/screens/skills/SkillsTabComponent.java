package de.teamlapen.faction.client.gui.screens.skills;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillHandler;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.client.gui.GuiRenderer;
import de.teamlapen.faction.common.core.FactionEffects;
import de.teamlapen.faction.common.factions.skills.SkillHandler;
import de.teamlapen.faction.common.factions.skills.SkillTreeGraph;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.advancements.AdvancementTabType;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.core.Holder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SkillsTabComponent {

    private static final Identifier RED_TOOLTIP = FIdentifier.mod("red");
    public static final int SCREEN_WIDTH = SkillsScreen.SCREEN_WIDTH - 18;
    public static final int SCREEN_HEIGHT = SkillsScreen.SCREEN_HEIGHT - 46;
    private final Minecraft minecraft;
    private final Holder<ISkillTree> skillTree;
    private final ISkillHandler<?> skillHandler;
    private final ItemStack icon;
    private final Component title;
    private final Map<SkillTreeGraph.Entry, SkillSegmentComponent> segments = new LinkedHashMap<>();
    private List<SkillSegmentConnection> connections = List.of();
    private final AdvancementTabType position;
    private final SkillTreeLayout layout;
    private final int treeWidth;
    private final int treeHeight;
    private final Identifier background;
    private double minX = Double.MIN_VALUE;
    private double minY = Double.MAX_VALUE;
    private double maxX = Double.MAX_VALUE;
    private double maxY = Double.MIN_VALUE;
    private double zoom = 1;
    private final int index;
    private float fade;
    private double centerX;
    private double centerY;
    private static final double maxZoom = 2;
    private static final double minZoom = 0.25;


    public SkillsTabComponent(Minecraft minecraft, int index, Holder<ISkillTree> skillTree, ISkillHandler<?> skillHandler, SkillTreeGraph graph, SkillTreeGraph.Tree tree) {
        this.minecraft = minecraft;
        this.skillTree = skillTree;
        this.skillHandler = skillHandler;
        ISkillTree treeValue = skillTree.value();
        this.index = index;
        this.icon = treeValue.display().create();
        this.title = treeValue.name();
        this.position = AdvancementTabType.LEFT;
        this.layout = SkillTreeLayout.of(tree);
        this.treeWidth = this.layout.width();
        this.treeHeight = this.layout.height();
        this.background = treeValue.background().map(x -> x.withPath(path -> "textures/" + path + ".png")).orElse(FIdentifier.mod("textures/gui/skills/backgrounds/level.png"));

        for (SkillTreeLayout.Placement placement : this.layout.placements()) {
            this.segments.put(placement.entry(), new SkillSegmentComponent(minecraft, placement, graph, (SkillHandler<?>) skillHandler));
        }
        this.segments.forEach((entry, component) -> component.bindParents(entry.parents().stream().map(this.segments::get).filter(Objects::nonNull).toList()));
        this.connections = SkillSegmentConnection.of(this.segments.values());

        recalculateBorders();
    }

    private void recalculateBorders() {
        this.minY = -(this.treeHeight + 16);
        this.maxY = 20;

        this.minX = this.layout.minX() - SCREEN_WIDTH / 2d;
        this.maxX = this.layout.maxX() + SCREEN_WIDTH / 2d;
        this.centerX = 0;
        this.centerY = 0;
    }

    public int getIndex() {
        return index;
    }

    public void extractTab(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY, boolean selected) {
        int tabX = x + this.position.getX(this.index);
        int tabY = y + this.position.getY(this.index);
        this.position.extractRenderState(graphics, tabX, tabY, selected, this.index);
        if (!selected && mouseX > tabX && mouseY > tabY && mouseX < tabX + this.position.getWidth() && mouseY < tabY + this.position.getHeight()) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
        }
    }

    public void drawIcon(GuiGraphicsExtractor graphics, int x, int y) {
        this.position.extractIcon(graphics, x, y, this.index, this.icon);
    }

    public boolean isMouseOverTabItem(int guiLeft, int guiTop, double mouseX, double mouseY) {
        return this.position.isMouseOver(guiLeft, guiTop, this.index, mouseX, mouseY);
    }

    public void extractContents(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY) {
        var pose = graphics.pose();

        graphics.enableScissor(x, y, x + SCREEN_WIDTH, y + SCREEN_HEIGHT);
        pose.pushMatrix();
        pose.translate(x, y);
        pose.translate((float) (SCREEN_WIDTH / 2d + centerX), (float) (20f + centerY));

        pose.scale((float) this.zoom, (float) this.zoom);

        for (int i = -(int) (((SCREEN_WIDTH / 2f + centerX) / 16 / zoom)) - 1; i <= (int) (((SCREEN_WIDTH / 2f - centerX) / 16 / zoom)); ++i) {
            for (int j = -(int) ((20 + centerY) / 16 / zoom) - 1; j <= (int) ((SCREEN_HEIGHT - centerY) / 16 / zoom); ++j) {
                GuiRenderer.blit(graphics, this.background, 16* i, 16*j, 16, 16, 16, 16);
            }
        }

        for (SkillSegmentConnection connection : this.connections) {
            connection.draw(graphics, true);
        }
        for (SkillSegmentConnection connection : this.connections) {
            connection.draw(graphics, false);
        }
        for (SkillSegmentComponent segment : this.segments.values()) {
            segment.draw(graphics, 0, 0);
        }
        pose.popMatrix();

        if (this.minecraft.player.getEffect(FactionEffects.OBLIVION) != null) {
            pose.pushMatrix();
            graphics.fill(x, y, x + SCREEN_WIDTH, y + SCREEN_HEIGHT, Mth.floor(0.5 * 255.0F) << 24);
            this.drawDisableText(graphics, x, y);
            pose.popMatrix();
        }

        graphics.disableScissor();
    }

    public void drawTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        var pose = graphics.pose();
        pose.pushMatrix();
        graphics.fill(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, Mth.floor(this.fade * 255.0F) << 24);
        boolean flag = false;
        if (mouseX >= 0 && mouseX < SCREEN_WIDTH && mouseY >= 0 && mouseY < SCREEN_HEIGHT) {
            double scaledMouseX = getScaledMouseX(mouseX);
            double scaledMouseY = getScaledMouseY(mouseY);
            for (SkillSegmentComponent segment : this.segments.values()) {
                if (segment.isMouseOver(scaledMouseX, scaledMouseY, 0, 0)) {
                    flag = true;
                    pose.pushMatrix();
                    pose.translate((float) (SCREEN_WIDTH / 2d + centerX), (float) (20 + centerY));
                    pose.scale((float) this.zoom, (float) this.zoom);
                    segment.drawHover(graphics, scaledMouseX, scaledMouseY, this.fade, 0, 0);
                    pose.popMatrix();
                    break;
                }
            }
        }

        pose.popMatrix();
        if (flag) {
            this.fade = Mth.clamp(this.fade + 0.02F, 0.0F, 0.3F);
        } else {
            this.fade = Mth.clamp(this.fade - 0.04F, 0.0F, 1.0F);
        }
    }

    private double getScaledMouseX(double mouseX) {
        return (mouseX - SCREEN_WIDTH / 2d - centerX) / zoom;
    }

    private double getScaledMouseY(double mouseY) {
        return (mouseY - 20 - centerY) / zoom;
    }

    public void mouseDragged(double mouseX, double mouseY, int mouseButton, double xDragged, double yDragged) {
        center(this.centerX + xDragged, this.centerY + yDragged);
    }

    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        var mouseX = getScaledMouseX(pMouseX);
        var mouseY = getScaledMouseY(pMouseY);
        this.zoom = Mth.clamp(this.zoom + (float) pScrollX * 0.1f + (float) pScrollY * 0.1f, this.minZoom, this.maxZoom);

        center(this.centerX - (mouseX - getScaledMouseX(pMouseX)) * zoom, this.centerY - (mouseY - getScaledMouseY(pMouseY)) * zoom);
        return true;
    }

    public void center(double x, double y) {
        this.centerX = Mth.clamp(x, this.minX * zoom, this.maxX * zoom);
        this.centerY = Mth.clamp(y, -this.treeHeight * zoom, 20 * zoom);
    }

    public Component getTitle() {
        return this.title;
    }

    @Nullable
    public Holder<? extends ISkill<?>> getSelected(int mouseX, int mouseY) {
        for (SkillSegmentComponent segment : this.segments.values()) {
            Holder<? extends ISkill<?>> selected = segment.getSelectedSkill(getScaledMouseX(mouseX), getScaledMouseY(mouseY), 0, 0);
            if (selected != null) {
                return selected;
            }
        }
        return null;
    }

    public int getRemainingPoints() {
        return this.skillHandler.getLeftSkillPoints(this.skillTree);
    }

    public Component getRemainingPointsText() {
        int points = this.getRemainingPoints();
        Component remainingPoints;
        var skillPointId = this.skillTree.value().skillPointTag().location().withPath(x -> "skills.skill_points." + x).toLanguageKey("gui");
        if (points == Integer.MAX_VALUE) {
            remainingPoints = Component.translatable(skillPointId, "∞");
        } else {
            remainingPoints = Component.translatable( points == 1 ? skillPointId + ".single" : skillPointId, points);
        }
        return remainingPoints;
    }

    public Holder<ISkillTree> getSkillTree() {
        return this.skillTree;
    }

    public void drawDisableText(GuiGraphicsExtractor graphics, int x, int y) {
        Component f = Component.translatable("gui.factionapi.skills.unlock_unavailable").withStyle(ChatFormatting.WHITE);
        FormattedCharSequence s = Language.getInstance().getVisualOrder(f);

        int tooltipTextWidth = 219;
        int tooltipX = 7 + x;
        int tooltipY = 17 + y;
        int tooltipHeight = this.minecraft.font.lineHeight * 2;

        TooltipRenderUtil.extractTooltipBackground(graphics, tooltipX, tooltipY, tooltipTextWidth, tooltipHeight, RED_TOOLTIP);

        graphics.centeredText(this.minecraft.font, f, tooltipX + tooltipTextWidth / 2, tooltipY + tooltipHeight / 2 - this.minecraft.font.lineHeight / 2, 0xff000000);
    }

}
