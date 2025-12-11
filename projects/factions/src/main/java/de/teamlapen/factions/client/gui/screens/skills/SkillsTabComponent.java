package de.teamlapen.factions.client.gui.screens.skills;

import de.teamlapen.factions.api.skills.ISkill;
import de.teamlapen.factions.api.skills.ISkillHandler;
import de.teamlapen.factions.api.skills.ISkillTree;
import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.client.gui.GuiRenderer;
import de.teamlapen.factions.common.core.FactionEffects;
import de.teamlapen.factions.common.skills.ClientSkillTreeData;
import de.teamlapen.factions.common.skills.SkillHandler;
import de.teamlapen.factions.common.skills.SkillTreeConfiguration;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTabType;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.core.Holder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.HashMap;
import java.util.Map;

@NonnullDefault
public class SkillsTabComponent {

    private static final ResourceLocation RED_TOOLTIP = FResourceLocation.mod("red");
    public static final int SCREEN_WIDTH = SkillsScreen.SCREEN_WIDTH - 18;
    public static final int SCREEN_HEIGHT = SkillsScreen.SCREEN_HEIGHT - 46;
    private final Minecraft minecraft;
    private final SkillsScreen screen;
    private final Holder<ISkillTree> skillTree;
    private final ISkillHandler<?> skillHandler;
    private final ItemStack icon;
    private final Component title;
    private final Map<SkillTreeConfiguration.SkillTreeNodeConfiguration, SkillNodeComponent> nodes = new HashMap<>();
    private final AdvancementTabType position;
    private final SkillNodeComponent root;
    private final int treeWidth;
    private final int treeHeight;
    private final ResourceLocation background;
    private double minX = Double.MIN_VALUE;
    private double minY = Double.MAX_VALUE;
    private double maxX = Double.MAX_VALUE;
    private double maxY = Double.MIN_VALUE;
    private double zoom = 1;
    private final int index;
    private float fade;
    private final ClientSkillTreeData treeData;
    private double centerX;
    private double centerY;
    private final double maxZoom = 2;
    private final double minZoom = 0.25;


    public SkillsTabComponent(Minecraft minecraft, SkillsScreen screen, int index, Holder<ISkillTree> skillTree, ISkillHandler<?> skillHandler, ClientSkillTreeData skillTreeData) {
        this.minecraft = minecraft;
        this.screen = screen;
        this.skillTree = skillTree;
        this.skillHandler = skillHandler;
        ISkillTree tree = skillTree.value();
        this.index = index;
        this.icon = tree.display();
        this.title = tree.name();
        this.position = AdvancementTabType.LEFT;
        this.treeData = skillTreeData;
        this.treeWidth = this.treeData.getTreeWidth(skillTree);
        this.treeHeight = this.treeData.getTreeHeight(skillTree);
        this.root = new SkillNodeComponent(minecraft, screen, this, this.treeData.root(skillTree), this.treeData, ((SkillHandler<?>) skillHandler));
        this.background = tree.background().map(x -> x.withPath(path -> "textures/" + path + ".png")).orElse(FResourceLocation.mod("textures/gui/skills/backgrounds/level.png"));
        addNode(this.root);

        recalculateBorders();
    }

    private void recalculateBorders() {
        this.minY = -(this.treeHeight + 16);
        this.maxY = 20;

        this.minX = -SCREEN_WIDTH / 2d;
        this.maxX = this.treeWidth - SCREEN_WIDTH / 2d;
        this.centerX = 0;
        this.centerY = 0;
    }

    private void addNode(SkillNodeComponent screen) {
        this.nodes.put(screen.getSkillNode(), screen);
        for (SkillNodeComponent child : screen.getChildren()) {
            addNode(child);
        }
    }

    public int getIndex() {
        return index;
    }

    public void drawTab(GuiGraphics graphics, int x, int y, boolean selected) {
        this.position.draw(graphics, x, y, selected, this.index);
    }

    public void drawIcon(GuiGraphics graphics, int x, int y) {
        this.position.drawIcon(graphics, x, y, this.index, this.icon);
    }

    public boolean isMouseOverTabItem(int guiLeft, int guiTop, double mouseX, double mouseY) {
        return this.position.isMouseOver(guiLeft, guiTop, this.index, mouseX, mouseY);
    }

    public void drawContents(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
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

        this.root.drawConnectivity(graphics, 0, 0, true);
        this.root.drawConnectivity(graphics, 0, 0, false);
        this.root.draw(graphics, 0, 0);
        pose.popMatrix();

        if (this.minecraft.player.getEffect(FactionEffects.OBLIVION) != null) {
            pose.pushMatrix();
            graphics.fill(x, y, x + SCREEN_WIDTH, y + SCREEN_HEIGHT, Mth.floor(0.5 * 255.0F) << 24);
            this.drawDisableText(graphics, x, y);
            pose.popMatrix();
        }

        graphics.disableScissor();
    }

    public void drawTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        var pose = graphics.pose();
        pose.pushMatrix();
        graphics.fill(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, Mth.floor(this.fade * 255.0F) << 24);
        boolean flag = false;
        if (mouseX >= 0 && mouseX < SCREEN_WIDTH && mouseY >= 0 && mouseY < SCREEN_HEIGHT) {
            double scaledMouseX = getScaledMouseX(mouseX);
            double scaledMouseY = getScaledMouseY(mouseY);
            for (SkillNodeComponent nodeScreen : this.nodes.values()) {
                if (nodeScreen.isMouseOver(scaledMouseX, scaledMouseY, 0, 0)) {
                    flag = true;
                    pose.pushMatrix();
                    pose.translate((float) (SCREEN_WIDTH / 2d + centerX), (float) (20 + centerY));
                    pose.scale((float) this.zoom, (float) this.zoom);
                    nodeScreen.drawHover(graphics, scaledMouseX, scaledMouseY, this.fade, 0, 0);
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
    public Holder<ISkill<?>> getSelected(int mouseX, int mouseY) {
        for (SkillNodeComponent screen : this.nodes.values()) {
            Holder<ISkill<?>> selected = screen.getSelectedSkill(getScaledMouseX(mouseX), getScaledMouseY(mouseY), 0, 0);
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
        int remainingPoints1 = this.getRemainingPoints();
        Component remainingPoints;
        var skillPointId = this.skillTree.value().skillPointTag().location();
        if (remainingPoints1 == Integer.MAX_VALUE) {
            remainingPoints = Component.translatable(skillPointId.toLanguageKey("skill_tree", "skill_points"), "∞");
        } else {
            remainingPoints = Component.translatable(skillPointId.toLanguageKey("skill_tree", remainingPoints1 == 1 ? "skill_point" : "skill_points"), remainingPoints1);
        }
        return remainingPoints;
    }

    public Holder<ISkillTree> getSkillTree() {
        return this.skillTree;
    }

    public void drawDisableText(GuiGraphics graphics, int x, int y) {
        Component f = Component.translatable("gui.factions.skill.unlock_unavailable").withStyle(ChatFormatting.WHITE);
        FormattedCharSequence s = Language.getInstance().getVisualOrder(f);

        int tooltipTextWidth = 219;
        int tooltipX = 7 + x;
        int tooltipY = 17 + y;
        int tooltipHeight = this.minecraft.font.lineHeight * 2;

        TooltipRenderUtil.renderTooltipBackground(graphics, tooltipX, tooltipY, tooltipTextWidth, tooltipHeight, RED_TOOLTIP);

        graphics.drawCenteredString(this.minecraft.font, f, tooltipX + tooltipTextWidth / 2, tooltipY + tooltipHeight / 2 - this.minecraft.font.lineHeight / 2, 0xff000000);
    }

}
