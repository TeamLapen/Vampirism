package de.teamlapen.vampirism.client.gui.screens.skills;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.data.ClientSkillTreeData;
import de.teamlapen.vampirism.entity.player.skills.SkillHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillTreeConfiguration;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.HashMap;
import java.util.Map;

@NonnullDefault
public class SkillsTabScreen {

    public static final int SCREEN_WIDTH = SkillsScreen.SCREEN_WIDTH - 18;
    public static final int SCREEN_HEIGHT = SkillsScreen.SCREEN_HEIGHT - 46;
    private final Minecraft minecraft;
    private final SkillsScreen screen;
    private final Holder<ISkillTree> skillTree;
    private final ISkillHandler<?> skillHandler;
    private final ItemStack icon;
    private final Component title;
    private final Map<SkillTreeConfiguration.SkillTreeNodeConfiguration, SkillNodeScreen> nodes = new HashMap<>();
    private final AdvancementTabType position;
    private final SkillNodeScreen root;
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


    public SkillsTabScreen(@NotNull Minecraft minecraft, @NotNull SkillsScreen screen, int index, Holder<ISkillTree> skillTree, @NotNull ISkillHandler<?> skillHandler, ClientSkillTreeData skillTreeData) {
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
        this.root = new SkillNodeScreen(minecraft, screen, this, this.treeData.root(skillTree), this.treeData, ((SkillHandler<?>) skillHandler));
        this.background = VResourceLocation.mod("textures/gui/skills/backgrounds/level.png");
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

    private void addNode(@NotNull SkillNodeScreen screen) {
        this.nodes.put(screen.getSkillNode(), screen);
        for (SkillNodeScreen child : screen.getChildren()) {
            addNode(child);
        }
    }

    public int getIndex() {
        return index;
    }

    public void drawTab(@NotNull GuiGraphics graphics, int x, int y, boolean selected) {
        this.position.draw(graphics, x, y, selected, this.index);
    }

    public void drawIcon(GuiGraphics graphics, int x, int y) {
        this.position.drawIcon(graphics, x, y, this.index, this.icon);
    }

    public boolean isMouseOver(int guiLeft, int guiTop, double mouseX, double mouseY) {
        return this.position.isMouseOver(guiLeft, guiTop, this.index, mouseX, mouseY);
    }

    public void drawContents(@NotNull GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        PoseStack pose = graphics.pose();

        graphics.enableScissor(x, y, x + SCREEN_WIDTH, y + SCREEN_HEIGHT);
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.translate(SCREEN_WIDTH / 2d + centerX, 20 + centerY, 0);

        pose.scale((float) this.zoom, (float) this.zoom, 1);

        for (int i = -(int) (((SCREEN_WIDTH / 2 + centerX) / 16 / zoom)) - 1; i <= (int) (((SCREEN_WIDTH / 2 - centerX) / 16 / zoom)); ++i) {
            for (int j = -(int) ((20 + centerY) / 16 / zoom) - 1; j <= (int) ((SCREEN_HEIGHT - centerY) / 16 / zoom); ++j) {
                graphics.blit(this.background, 16 * i, 16 * j, 0.0F, 0.0F, 16, 16, 16, 16);
            }
        }

        this.root.drawConnectivity(graphics, 0, 0, true);
        this.root.drawConnectivity(graphics, 0, 0, false);
        this.root.draw(graphics, 0, 0);
        pose.popPose();

        if (this.minecraft.player.getEffect(ModEffects.OBLIVION) != null) {
            pose.pushPose();
            pose.translate(0.0F, 0.0F, 200.0F);
            graphics.fill(x, y, x + SCREEN_WIDTH, y + SCREEN_HEIGHT, Mth.floor(0.5 * 255.0F) << 24);
            pose.translate(0, 0, 200);
            this.drawDisableText(graphics, x, y);
            pose.popPose();
        }

        graphics.disableScissor();
    }

    public void drawTooltips(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(0.0F, 0.0F, -200.0F);
        graphics.fill(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, Mth.floor(this.fade * 255.0F) << 24);
        boolean flag = false;
        if (mouseX >= 0 && mouseX < SCREEN_WIDTH && mouseY >= 0 && mouseY < SCREEN_HEIGHT) {
            double scaledMouseX = getScaledMouseX(mouseX);
            double scaledMouseY = getScaledMouseY(mouseY);
            for (SkillNodeScreen nodeScreen : this.nodes.values()) {
                if (nodeScreen.isMouseOver(scaledMouseX, scaledMouseY, 0, 0)) {
                    flag = true;
                    pose.pushPose();
                    pose.translate(SCREEN_WIDTH / 2d + centerX, 20 + centerY, 0);
                    pose.scale((float) this.zoom, (float) this.zoom, 1);
                    nodeScreen.drawHover(graphics, scaledMouseX, scaledMouseY, this.fade, 0, 0);
                    pose.popPose();
                    break;
                }
            }
        }

        pose.popPose();
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
        for (SkillNodeScreen screen : this.nodes.values()) {
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

    public Holder<ISkillTree> getSkillTree() {
        return this.skillTree;
    }

    public void drawDisableText(@NotNull GuiGraphics graphics, int x, int y) {
        Component f = Component.translatable("text.vampirism.skill.unlock_unavailable").withStyle(ChatFormatting.WHITE);
        FormattedCharSequence s = Language.getInstance().getVisualOrder(f);

        int tooltipTextWidth = 219;
        int tooltipX = 7 + x;
        int tooltipY = 17 + y;
        int tooltipHeight = this.minecraft.font.lineHeight * 2;
        int backgroundColor = 0xF09b0404;//0xF0550404;;
        int borderColorStart = 0x505f0c0c;
        int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;

        TooltipRenderUtil.renderTooltipBackground(graphics, tooltipX, tooltipY, tooltipTextWidth, tooltipHeight, 400, backgroundColor, backgroundColor, borderColorStart, borderColorEnd);

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 400);
        graphics.drawCenteredString(this.minecraft.font, f, tooltipX + tooltipTextWidth / 2, tooltipY + tooltipHeight / 2 - this.minecraft.font.lineHeight / 2, 15728880);
        graphics.pose().popPose();
    }
}
