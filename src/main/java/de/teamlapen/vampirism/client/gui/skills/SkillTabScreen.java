package de.teamlapen.vampirism.client.gui.skills;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.vampirism.player.skills.SkillNode;
import de.teamlapen.vampirism.player.skills.SkillTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.advancements.AdvancementTabType;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillTabScreen extends AbstractGui {

    private final Minecraft minecraft;
    private final SkillsScreen screen;
    private final ISkillHandler<?> skillHandler;
    private final ItemStack icon;
    private final Map<SkillNode, SkillNodeScreen> nodes = new HashMap<>();
    private final AdvancementTabType position;
    private boolean centered;
    private double scrollX;
    private double scrollY;
    private int minX = Integer.MIN_VALUE;
    private int minY = Integer.MIN_VALUE;
    private int maxX = Integer.MAX_VALUE;
    private int maxY = Integer.MAX_VALUE;
    private float zoom = 1;
    private int index;
    private final SkillNodeScreen root;
    private final int treeWidth;
    private final ResourceLocation background;
    private float fade;


    public SkillTabScreen(Minecraft minecraft, SkillsScreen screen, int index, ItemStack icon, SkillNode rootNode, ISkillHandler<?> skillHandler) {
        this.minecraft = minecraft;
        this.screen = screen;
        this.skillHandler = skillHandler;
        this.index = index;
        this.icon = icon;
        this.position = AdvancementTabType.LEFT;
        this.root = new SkillNodeScreen(minecraft, screen, this, rootNode, ((SkillHandler<?>) skillHandler));
        this.treeWidth = SkillTree.getTreeWidth(rootNode);
        this.background = new ResourceLocation(REFERENCE.MODID, "textures/gui/skills/backgrounds/level.png");
        addNode(this.root);
        this.maxY = 20;
        this.minY = -SkillTree.getTreeHeight(rootNode);
        this.minX = -(SkillsScreen.SCREEN_WIDTH - 18)/2;
        this.maxX = treeWidth - (SkillsScreen.SCREEN_WIDTH - 18)/2;
        this.scrollX = (SkillsScreen.SCREEN_WIDTH - 18)/2 - 13;
        this.scrollY = 20;
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

    public void drawTab(MatrixStack stack, int x, int y, boolean selected) {
        this.position.draw(stack, this, x, y, selected, this.index);
    }

    public void drawIcon(int x, int y, ItemRenderer itemRenderer) {
        this.position.drawIcon(x, y, this.index, itemRenderer, this.icon);
    }

    public boolean isMouseOver(int guiLeft, int guiTop, double mouseX, double mouseY) {
        return this.position.isMouseOver(guiLeft, guiTop, this.index, mouseX, mouseY);
    }

    public void drawContents(MatrixStack stack) {
        if (!this.centered) {
            this.scrollX = MathHelper.clamp(this.scrollX, this.minX, this.maxX);
            this.scrollY = MathHelper.clamp(this.scrollY, this.minY, this.maxY);
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
        fill(stack, SkillsScreen.SCREEN_WIDTH - 18, SkillsScreen.SCREEN_HEIGHT - 27, 0, 0, -16777216);
        RenderSystem.depthFunc(515);

        this.minecraft.getTextureManager().bind(this.background);

        int i = MathHelper.floor(this.scrollX);
        int j = MathHelper.floor(this.scrollY);
        int k = i % 16;
        int l = j % 16;

        for(int i1 = -1; i1 <= 15; ++i1) {
            for(int j1 = -1; j1 <= 11; ++j1) {
                blit(stack, k + 16 * i1, l + 16 * j1, 0.0F, 0.0F, 16, 16, 16, 16);
            }
        }

        stack.scale(this.zoom, this.zoom,1);

        this.root.drawConnectivity(stack, i, j, true);
        this.root.drawConnectivity(stack, i, j, false);
        this.root.draw(stack, i, j);
        RenderSystem.depthFunc(518);
        RenderSystem.translatef(0.0F, 0.0F, -950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(stack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0F, 0.0F, 950.0F);
        RenderSystem.depthFunc(515);
        stack.popPose();

        if (this.minecraft.player.getEffect(ModEffects.oblivion) != null) {
            stack.pushPose();
            RenderSystem.enableDepthTest();
            stack.translate(0.0F, 0.0F, 200.0F);
            fill(stack, 0, 0, 234, 172, MathHelper.floor(0.5 * 255.0F) << 24);
            RenderSystem.disableDepthTest();
            stack.popPose();
            stack.pushPose();
            stack.translate(0,0,200);
            this.drawDisableText(stack);
            stack.popPose();
        }

    }

    public void drawTooltips(MatrixStack stack, int mouseX, int mouseY, int guiLeft, int guiTop){
        RenderSystem.pushMatrix();
        stack.translate(0.0F, 0.0F, 200.0F);
        fill(stack, 0, 0, 234, 172, MathHelper.floor(this.fade * 255.0F) << 24);
        boolean flag = false;
        int i = MathHelper.floor(this.scrollX);
        int j = MathHelper.floor(this.scrollY);
        mouseX -= guiLeft;
        mouseY -= guiTop;
        if (mouseX > 8 && mouseX < 243 && mouseY > 18 && mouseY < 173 + 18) {
            for(SkillNodeScreen nodeScreen : this.nodes.values()) {
                if (nodeScreen.isMouseOver(mouseX, mouseY,i,j+18, this.zoom)) {
                    flag = true;
                    stack.pushPose();
                    stack.scale(this.zoom, this.zoom,1);
                    nodeScreen.drawHover(stack, mouseX, mouseY, this.fade, i,j, 0, 0, this.zoom);
                    stack.popPose();
                    break;
                }
            }
        }

        RenderSystem.popMatrix();
        if (flag) {
            this.fade = MathHelper.clamp(this.fade + 0.02F, 0.0F, 0.3F);
        } else {
            this.fade = MathHelper.clamp(this.fade - 0.04F, 0.0F, 1.0F);
        }
    }

    public void mouseDragged(double mouseX, double mouseY, int mouseButton, double xDragged, double yDragged) {
        this.scrollX += xDragged;
        this.scrollY += yDragged;
        this.centered = false;
    }

    public List<? extends IReorderingProcessor> getTitle() {
        return Collections.emptyList();
    }

    public ISkill getSelected(double mouseX, double mouseY, int guiLeft, int guiTop) {
        int i = MathHelper.floor(this.scrollX);
        int j = MathHelper.floor(this.scrollY);
        for (SkillNodeScreen screen : this.nodes.values()) {
            ISkill selected = screen.getSelectedSkill(mouseX, mouseY, guiLeft+i, guiTop+j);
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
        this.zoom = (float) MathHelper.clamp(this.zoom + (amount/50), 0.2, 1);
        this.zoom = 1f; //TODO fix zooming tooltip rendering
        return true;
    }

    public void drawDisableText(MatrixStack mStack) {
        if (this.minecraft.player.getEffect(ModEffects.oblivion) == null) return;

        ITextComponent f = new TranslationTextComponent("text.vampirism.skill.unlock_unavailable").withStyle(TextFormatting.WHITE);
        IReorderingProcessor s = LanguageMap.getInstance().getVisualOrder(f);

        int tooltipTextWidth = 219;
        int tooltipX = 7;//(this.width - this.display_width) / 2 + 19 + 3;
        int tooltipY = 19;//(this.height - this.display_height) / 2 + 4 + 19;
        int tooltipHeight = this.minecraft.font.lineHeight * 2;
        int backgroundColor = 0xF09b0404;//0xF0550404;;
        int borderColorStart = 0x505f0c0c;
        int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
        int zLevel = this.getBlitOffset();

        mStack.pushPose();
        Matrix4f mat = mStack.last().pose();
        GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
        GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
        GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        GuiUtils.drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
        GuiUtils.drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
        GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
        GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

        IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
        mStack.translate(0.0D, 0.0D, zLevel);

        this.minecraft.font.drawInBatch(s, (float) tooltipX + (tooltipTextWidth / 2) - this.minecraft.font.width(f) / 2, (float) tooltipY + (tooltipHeight / 2) - 3, -1, true, mat, renderType, false, 0, 15728880);

        renderType.endBatch();
        mStack.popPose();
    }
}
