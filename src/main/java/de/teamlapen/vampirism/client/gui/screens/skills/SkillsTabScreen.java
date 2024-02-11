package de.teamlapen.vampirism.client.gui.screens.skills;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
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
    public static final int SCREEN_HEIGHT = SkillsScreen.SCREEN_HEIGHT - 47;
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
    private double scrollX;
    private double scrollY;
    private int minX = Integer.MIN_VALUE;
    private int minY = Integer.MIN_VALUE;
    private int maxX = Integer.MAX_VALUE;
    private int maxY = Integer.MAX_VALUE;
    private float zoom = 1;
    private final int index;
    private float fade;
    private final ClientSkillTreeData treeData;


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
        this.background = new ResourceLocation(REFERENCE.MODID, "textures/gui/skills/backgrounds/level.png");
        addNode(this.root);

        recalculateBorders();
        this.scrollX = 0;
        this.scrollY = 20;
    }

    private void recalculateBorders() {
        this.minY = (int) -((this.treeHeight) * this.zoom);
        this.maxY = (int) (20 * this.zoom);

        this.minX = (int) ((-this.treeWidth/2) * this.zoom);
        this.maxX = (int) ((this.treeWidth/2) * this.zoom);

        this.center();
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

    public void drawContents(@NotNull GuiGraphics graphics, int x, int y) {
        PoseStack pose = graphics.pose();

        graphics.enableScissor(x, y, x + SCREEN_WIDTH, y + SCREEN_HEIGHT);
        pose.pushPose();
        pose.translate(x, y, 0);


        int i = getX();
        int j = getY();
        int k = i % 16;
        int l = j % 16;
        pose.scale(this.zoom, this.zoom, 1);

        for (int i1 = -1; i1 <= 15 / this.zoom; ++i1) {
            for (int j1 = -1; j1 <= 12 / this.zoom; ++j1) {
                graphics.blit(this.background, k + 16 * i1, l + 16 * j1, 0.0F, 0.0F, 16, 16, 16, 16);
            }
        }

        this.root.drawConnectivity(graphics, i, j, true);
        this.root.drawConnectivity(graphics, i, j, false);
        this.root.draw(graphics, i, j);
        pose.popPose();

        if (this.minecraft.player.getEffect(ModEffects.OBLIVION.get()) != null) {
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
        int scrollX = getX();
        int scrollY = getY();
        if (mouseX >= 0 && mouseX < 235 && mouseY >= 0 && mouseY < 173) {
            for (SkillNodeScreen nodeScreen : this.nodes.values()) {
                if (nodeScreen.isMouseOver(mouseX / this.zoom, mouseY / this.zoom, scrollX, scrollY)) {
                    flag = true;
                    pose.pushPose();
                    pose.scale(this.zoom, this.zoom, 1);
                    nodeScreen.drawHover(graphics, mouseX / this.zoom, mouseY / this.zoom, this.fade, scrollX, scrollY);
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

    public void mouseDragged(double mouseX, double mouseY, int mouseButton, double xDragged, double yDragged) {
        this.scrollX += xDragged;
        this.scrollY += yDragged;
        center();
    }

    private void center() {
        this.scrollX = Mth.clamp(this.scrollX, this.minX, this.maxX);
        this.scrollY = Mth.clamp(this.scrollY, this.minY, this.maxY);
    }

    public Component getTitle() {
        return this.title;
    }

    private int getX(){
        int centerX = (SCREEN_WIDTH / 2);
        centerX += (int) scrollX;
        centerX /= (int) this.zoom;
        return centerX;
    }

    private int getY() {
        int centerY = 20;
        centerY += (int) scrollY;
        centerY /= (int) this.zoom;
        return centerY;
    }

    @Nullable
    public ISkill<?> getSelected(int mouseX, int mouseY) {
        int i = getX();
        int j = getY();
        for (SkillNodeScreen screen : this.nodes.values()) {
            ISkill<?> selected = screen.getSelectedSkill(mouseX / this.zoom, mouseY / this.zoom, i, j);
            if (selected != null) {
                return selected;
            }
        }
        return null;
    }

    public int getRemainingPoints() {
        return this.skillHandler.getLeftSkillPoints();
    }

    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        double scrollXP = this.scrollX * this.zoom;
        double scrollYP = this.scrollY * this.zoom;
        this.zoom = (float) (this.zoom + ((pScrollX + pScrollY) / 25));
        float heightZoom = this.zoom;
        float widthZoom = this.zoom;
        if (this.zoom * (this.treeHeight) < (SCREEN_HEIGHT)) {
            heightZoom = Math.max(this.zoom, (float) (SCREEN_HEIGHT) / (this.treeHeight));
        }
        if (this.zoom * this.treeWidth < (SCREEN_WIDTH - 20)) {
            widthZoom = Math.max(this.zoom, (float) (SCREEN_WIDTH - 20) / (Math.max(60, this.treeWidth)));
        }

        this.zoom = Math.min(heightZoom, widthZoom);
        this.zoom = Math.min(1, this.zoom);

        this.scrollX = scrollXP / this.zoom;
        this.scrollY = scrollYP / this.zoom;

        recalculateBorders();
        return true;
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
        graphics.drawCenteredString(this.minecraft.font, f, tooltipX + tooltipTextWidth/2, tooltipY + tooltipHeight/2 - this.minecraft.font.lineHeight/2, 15728880);
        graphics.pose().popPose();
    }
}
