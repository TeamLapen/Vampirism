package de.teamlapen.vampirism.client.gui.skills;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.vampirism.player.skills.SkillNode;
import de.teamlapen.vampirism.player.skills.SkillTree;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.advancements.AdvancementTabType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ScreenUtils;
import org.lwjgl.system.NonnullDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@NonnullDefault
@ParametersAreNonnullByDefault
public class SkillsTabScreen extends GuiComponent {

    public static final int SCREEN_WIDTH = SkillsScreen.SCREEN_WIDTH - 18;
    public static final int SCREEN_HEIGHT = SkillsScreen.SCREEN_HEIGHT - 47;
    private final Minecraft minecraft;
    private final SkillsScreen screen;
    private final ISkillHandler<?> skillHandler;
    private final ItemStack icon;
    private final Component title;
    private final Map<SkillNode, SkillNodeScreen> nodes = new HashMap<>();
    private final AdvancementTabType position;
    private final SkillNodeScreen root;
    private final int treeWidth;
    private final int treeHeight;
    private final ResourceLocation background;
    private boolean centered;
    private double scrollX;
    private double scrollY;
    private int minX = Integer.MIN_VALUE;
    private int minY = Integer.MIN_VALUE;
    private int maxX = Integer.MAX_VALUE;
    private int maxY = Integer.MAX_VALUE;
    private float zoom = 1;
    private final int index;
    private float fade;


    public SkillsTabScreen(Minecraft minecraft, SkillsScreen screen, int index, ItemStack icon, SkillNode rootNode, ISkillHandler<?> skillHandler, Component title) {
        this.minecraft = minecraft;
        this.screen = screen;
        this.skillHandler = skillHandler;
        this.index = index;
        this.icon = icon;
        this.title = title;
        this.position = AdvancementTabType.LEFT;
        this.root = new SkillNodeScreen(minecraft, screen, this, rootNode, ((SkillHandler<?>) skillHandler));
        this.treeWidth = SkillTree.getTreeWidth(rootNode);
        this.treeHeight = SkillTree.getTreeHeight(rootNode);
        this.background = new ResourceLocation(REFERENCE.MODID, "textures/gui/skills/backgrounds/level.png");
        addNode(this.root);

        recalculateBorders();
        this.scrollX = (SkillsScreen.SCREEN_WIDTH - 18) / (float)2 - 13;
        this.scrollY = 20;
    }

    private void recalculateBorders() {
        this.maxY = 20;
        this.minY = (int) (-(this.treeHeight-40) * this.zoom);

        this.minX = 0;
        this.maxX = this.treeWidth;

        this.centered = false;
    }

    private void addNode(SkillNodeScreen screen) {
        this.nodes.put(screen.getSkillNode(), screen);
        for (SkillNodeScreen child : screen.getChildren()) {
            addNode(child);
        }
    }

    public int getIndex() {
        return index;
    }

    public void drawTab(PoseStack stack, int x, int y, boolean selected) {
        this.position.draw(stack, this, x, y, selected, this.index);
    }

    public void drawIcon(int x, int y, ItemRenderer itemRenderer) {
        this.position.drawIcon(x, y, this.index, itemRenderer, this.icon);
    }

    public boolean isMouseOver(int guiLeft, int guiTop, double mouseX, double mouseY) {
        return this.position.isMouseOver(guiLeft, guiTop, this.index, mouseX, mouseY);
    }

    public void drawContents(PoseStack stack) {
        if (!this.centered) {
            this.scrollX = Mth.clamp(this.scrollX, this.minX, this.maxX);
            this.scrollY = Mth.clamp(this.scrollY, this.minY, this.maxY);
            this.centered = true;
        }

        stack.pushPose();
        RenderSystem.enableDepthTest();
        stack.translate(0.0F, 0.0F, 950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(stack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        stack.translate(0.0F, 0.0F, -950.0F);
        RenderSystem.depthFunc(518);
        fill(stack, SCREEN_WIDTH, SCREEN_HEIGHT, 0, 0, -16777216);
        RenderSystem.depthFunc(515);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.background);

        int i = Mth.floor(this.scrollX);
        int j = Mth.floor(this.scrollY);
        int k = i % 16;
        int l = j % 16;
        stack.scale(this.zoom, this.zoom, 1);

        for (int i1 = -1; i1 <= 15 / this.zoom; ++i1) {
            for (int j1 = -1; j1 <= 12 / this.zoom; ++j1) {
                blit(stack, k + 16 * i1, l + 16 * j1, 0.0F, 0.0F, 16, 16, 16, 16);
            }
        }


        this.root.drawConnectivity(stack, i, j, true);
        this.root.drawConnectivity(stack, i, j, false);
        this.root.draw(stack, i, j);
        RenderSystem.depthFunc(518);
        stack.translate(0.0F, 0.0F, -950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(stack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        stack.translate(0.0F, 0.0F, 950.0F);
        RenderSystem.depthFunc(515);
        stack.popPose();

        if (this.minecraft.player.getEffect(ModEffects.OBLIVION.get()) != null) {
            stack.pushPose();
            RenderSystem.enableDepthTest();
            stack.translate(0.0F, 0.0F, 200.0F);
            fill(stack, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, Mth.floor(0.5 * 255.0F) << 24);
            RenderSystem.disableDepthTest();
            stack.popPose();
            stack.pushPose();
            stack.translate(0, 0, 200);
            this.drawDisableText(stack);
            stack.popPose();
        }

    }

    public void drawTooltips(PoseStack stack, int mouseX, int mouseY) {
        stack.pushPose();
        stack.translate(0.0F, 0.0F, 200.0F);
        fill(stack, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, Mth.floor(this.fade * 255.0F) << 24);
        boolean flag = false;
        int scrollX = Mth.floor(this.scrollX);
        int scrollY = Mth.floor(this.scrollY);
        if (mouseX >= 0 && mouseX < 235 && mouseY >= 0 && mouseY < 173) {
            for (SkillNodeScreen nodeScreen : this.nodes.values()) {
                if (nodeScreen.isMouseOver(mouseX / this.zoom, mouseY / this.zoom, scrollX, scrollY)) {
                    flag = true;
                    stack.pushPose();
                    stack.scale(this.zoom, this.zoom, 1);
                    nodeScreen.drawHover(stack, mouseX / this.zoom, mouseY / this.zoom, this.fade, scrollX, scrollY);
                    stack.popPose();
                    break;
                }
            }
        }

        stack.popPose();
        if (flag) {
            this.fade = Mth.clamp(this.fade + 0.02F, 0.0F, 0.3F);
        } else {
            this.fade = Mth.clamp(this.fade - 0.04F, 0.0F, 1.0F);
        }
    }

    public void mouseDragged(double mouseX, double mouseY, int mouseButton, double xDragged, double yDragged) {
        this.scrollX += xDragged/this.zoom;
        this.scrollY += yDragged/this.zoom;
        this.centered = false;
    }

    public Component getTitle() {
        return this.title;
    }

    @Nullable
    public ISkill getSelected(double mouseX, double mouseY, int guiLeft, int guiTop) {
        int i = Mth.floor(this.scrollX);
        int j = Mth.floor(this.scrollY);
        for (SkillNodeScreen screen : this.nodes.values()) {
            ISkill selected = screen.getSelectedSkill((mouseX - guiLeft - 9) / this.zoom, (mouseY - guiTop - 18) / this.zoom, i, j);
            if (selected != null) {
                return selected;
            }
        }
        return null;
    }

    public int getRemainingPoints() {
        return this.skillHandler.getLeftSkillPoints();
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        double scrollXP = this.scrollX * this.zoom;
        double scrollYP = this.scrollY * this.zoom;
        this.zoom = (float) (this.zoom + (amount/25));
        float heightZoom = this.zoom;
        float widthZoom = this.zoom;
        if (this.zoom * (this.treeHeight) < (SCREEN_HEIGHT)) {
            heightZoom = Math.max(this.zoom, (float) (SCREEN_HEIGHT) / (this.treeHeight));
        }
        if (this.zoom * this.treeWidth < (SCREEN_WIDTH - 20)) {
            widthZoom = Math.max(this.zoom, (float) (SCREEN_WIDTH - 20) /(Math.max(60,this.treeWidth)));
        }

        this.zoom = Math.min(heightZoom, widthZoom);
        this.zoom = Math.min(1, this.zoom);

        this.scrollX = scrollXP / this.zoom;
        this.scrollY = scrollYP / this.zoom;

        recalculateBorders();
        return true;
    }

    public void drawDisableText(PoseStack mStack) {
        if (this.minecraft.player.getEffect(ModEffects.OBLIVION.get()) == null) return;

        Component f = Component.translatable("text.vampirism.skill.unlock_unavailable").withStyle(ChatFormatting.WHITE);
        FormattedCharSequence s = Language.getInstance().getVisualOrder(f);

        int tooltipTextWidth = 219;
        int tooltipX = 7;
        int tooltipY = 17;
        int tooltipHeight = this.minecraft.font.lineHeight * 2;
        int backgroundColor = 0xF09b0404;//0xF0550404;;
        int borderColorStart = 0x505f0c0c;
        int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
        int zLevel = this.getBlitOffset();

        mStack.pushPose();
        Matrix4f mat = mStack.last().pose();
        ScreenUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
        ScreenUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
        ScreenUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        ScreenUtils.drawGradientRect(mat, zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        ScreenUtils.drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        ScreenUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
        ScreenUtils.drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
        ScreenUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
        ScreenUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

        MultiBufferSource.BufferSource renderType = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        mStack.translate(0.0D, 0.0D, zLevel);

        this.minecraft.font.drawInBatch(s, (float) tooltipX + (tooltipTextWidth / 2f) - this.minecraft.font.width(f) / 2f, (float) tooltipY + (tooltipHeight / 2f) - 3, -1, true, mat, renderType, false, 0, 15728880);

        renderType.endBatch();
        mStack.popPose();
    }
}
