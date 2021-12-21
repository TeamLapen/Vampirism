package de.teamlapen.vampirism.client.gui.skills;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.player.skills.*;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.advancements.AdvancementState;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SkillNodeScreen extends AbstractGui {
    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/advancements/widgets.png");
    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};
    private final Minecraft minecraft;
    private final SkillTabScreen tab;
    private final SkillsScreen screen;
    private final SkillNode skillNode;
    private final SkillHandler<?> skillHandler;
    private final List<SkillNodeScreen> children = new ArrayList<>();
    private final SkillNodeScreen parent;
    private final int x;
    private final int y;
    private final IReorderingProcessor[] titles;
    private final List<IReorderingProcessor>[] descriptions;
    private final int[] width;

    public SkillNodeScreen(Minecraft minecraft, SkillsScreen screen, SkillTabScreen tab, SkillNode skillNode, SkillHandler<?> skillHandler) {
        this(minecraft, screen, tab, skillNode, skillHandler,null, 0,0);
    }

    public SkillNodeScreen(Minecraft minecraft, SkillsScreen screen, SkillTabScreen tab, SkillNode skillNode, SkillHandler<?> skillHandler, SkillNodeScreen parent, int x, int y) {
        this.minecraft = minecraft;
        this.tab = tab;
        this.screen = screen;
        this.skillNode = skillNode;
        this.skillHandler = skillHandler;
        this.parent = parent;
        this.x = x;
        this.y = y;
        int w = SkillTree.getTreeWidth(skillNode);
        int childrenCount = this.skillNode.getChildren().size();
        if (childrenCount > 0) {

            int pos = x;
            childrenCount -=1;
            if (childrenCount > 0) {
               pos -= w / 2;
            }

            for (int i = 0; i < skillNode.getChildren().size(); i++) {
                SkillNode current = skillNode.getChildren().get(i);
                this.children.add(new SkillNodeScreen(minecraft, screen, tab, current, skillHandler,this, pos , y + 60));
                pos += SkillTree.getTreeWidth(current)/2 + 60;
                if (skillNode.getChildren().size() >= i+2) {
                    SkillNode next = skillNode.getChildren().get(i+1);
                    pos += SkillTree.getTreeWidth(next)/2;
                }

            }
        }

        this.titles = new IReorderingProcessor[skillNode.getElements().length];
        this.descriptions = new List[skillNode.getElements().length];
        this.width = new int[skillNode.getElements().length];
        int l = 29 + 27;
        for (int i = 0; i < skillNode.getElements().length; i++) {
            this.titles[i] = LanguageMap.getInstance().getVisualOrder(minecraft.font.substrByWidth(skillNode.getElements()[i].getName(), 163));
            int size = Math.max(l + minecraft.font.width(titles[i]), 120);
            this.descriptions[i] = LanguageMap.getInstance().getVisualOrder(this.findOptimalLines(TextComponentUtils.mergeStyles(Optional.ofNullable(skillNode.getElements()[i].getDescription()).orElse(new StringTextComponent("")).copy(), Style.EMPTY.withColor(TextFormatting.GRAY)), size - 20));
            this.width[i] = size;
        }
    }

    private SkillNodeState getState()  {
        if (Arrays.stream(this.skillNode.getElements()).anyMatch(this.skillHandler::isSkillEnabled)) {
           return SkillNodeState.UNLOCKED;
        } else if (this.skillHandler.isSkillNodeLocked(this.skillNode)) {
            return SkillNodeState.LOCKED;
        } else if (Arrays.stream(this.skillNode.getParent().getElements()).anyMatch(this.skillHandler::isSkillEnabled)) {
            return SkillNodeState.AVAILABLE;
        } else {
            return SkillNodeState.HIDDEN;
        }
    }

    public List<SkillNodeScreen> getChildren() {
        return children;
    }

    public SkillNode getSkillNode() {
        return skillNode;
    }

    public void draw(MatrixStack stack, int i, int j) {
        int width = 26 * this.skillNode.getElements().length + (this.skillNode.getElements().length-1)*10;

        AdvancementState state = AdvancementState.UNOBTAINED;
        this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
        if (this.skillNode.getElements().length > 0) {
            GuiUtils.drawContinuousTexturedBox(stack, i + this.x + 3 - width/2 + 13,j + this.y, FrameType.TASK.getTexture(), 128 + state.getIndex() * 26,width, 26,26,26, 3, this.getBlitOffset());
        }
        int x = getNodeStart();
        for (int i1 = 0; i1 < this.skillNode.getElements().length; i1++) {
            if (getState() == SkillNodeState.LOCKED || !skillHandler.isSkillEnabled(this.skillNode.getElements()[i1])) {
                RenderSystem.color4f(0.5f, 0.5f, 0.5f, 1);
            } else {
                RenderSystem.color4f(1,1,1,1);
            }
            this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
            this.blit(stack, i + x + 3, j + this.y, FrameType.TASK.getTexture(), 128 + state.getIndex() * 26, 26, 26);

            this.minecraft.textureManager.bind(getIconLoc(this.skillNode.getElements()[i1]));
            RenderSystem.color4f(1,1,1,1);
            RenderSystem.disableLighting();
            RenderSystem.enableBlend();
            UtilLib.drawTexturedModalRect(stack.last().pose(), this.getBlitOffset(), x + i + 8,this.y+ j + 5, 0, 0, 16, 16, 16, 16);
            RenderSystem.disableLighting();

            x+= 26 + 10;
        }

        for (SkillNodeScreen child : this.children) {
            child.draw(stack, i, j);
        }
    }

    public void drawConnectivity(MatrixStack stack, int startX, int startY, boolean b) {
        if (this.parent != null) {
            int color;
            switch (getState()) {
                case AVAILABLE:
                    color = b ? -1:0xffa7a7a7;
                    break;
                case LOCKED:
                    color = b ?  0xff6a0000:0xffcf0000;
                    break;
                case UNLOCKED:
                    color = b ? 0xff005304 : 0xff008711;
                    break;
                default:
                    color = b?0xff3f3f3f:0xff222222;
                    break;
            }
            int i = startX + x + 15;
            int i1 = startX + this.parent.x + 15;
            int j = startY + this.y - 30;
            int j2 = startY + this.parent.y + 13;
            int j3 = startY + this.y - 29;
            int j4 = startY + this.parent.y + 13 + 16;
            int i2 = startX + x + 15;
            int j5 = startY + this.y;
            int i3 = startX + this.parent.x + 15;
            if (b) {
                this.hLine(stack, i, i1, j - 1, color);
                this.hLine(stack, i, i1, j + 1, color);
                this.vLine(stack, i1 - 1, j2, j3, color);
                this.vLine(stack, i3 + 1, j2, j3, color);
                this.vLine(stack, i - 1, j4, j5 + 1, color);
                this.vLine(stack, i2 + 1, j4, j5 + 1, color);
            } else {
                this.hLine(stack, i, i1, j, color);
                this.vLine(stack, i1, j2, j3, color);
                this.vLine(stack, i, j4, j5 + 1, color);
            }
        }

        for (SkillNodeScreen child : this.children) {
            child.drawConnectivity(stack, startX, startY, b);
        }
    }

    private ResourceLocation getIconLoc(ISkill skill) {
        if (skill instanceof ActionSkill) {
            return new ResourceLocation(((ActionSkill<?>) skill).getActionID().getNamespace(), "textures/actions/" + ((ActionSkill<?>) skill).getActionID().getPath() + ".png");
        } else {
            return new ResourceLocation(skill.getRegistryName().getNamespace(), "textures/skills/" + skill.getRegistryName().getPath() + ".png");
        }
    }

    public boolean isMouseOver(double mouseX, double mouseY, int guiLeft, int guiTop, float zoom) {
        int width = 26 * this.skillNode.getElements().length + (this.skillNode.getElements().length-1)*10;
        if (mouseX > (this.x + guiLeft - (width/2) +25)*zoom&& mouseX < (guiLeft + this.x + 26 + (width/2) - 2)*zoom && mouseY > (guiTop + this.y)*zoom && mouseY < (guiTop + this.y + 26)*zoom){
            return true;
        }
        return false;
    }

    public boolean isMouseOverSkill(int index, int xStart, double mouseX, double mouseY, int guiLeft, int guiTop) {
        if (mouseX > xStart + guiLeft + 8 + 4 && mouseX < xStart + guiLeft + 8 + 26 + 3 && mouseY > this.y + guiTop + 18 && mouseY < this.y + guiTop + 26 + 18) {
            return true;
        }
        return false;
    }

    public ISkill getSelectedSkill(double mouseX, double mouseY, int guiLeft, int guiTop) {
        int x = getNodeStart();
        for (int i = 0; i < this.skillNode.getElements().length; i++) {
            if (isMouseOverSkill(i, x, mouseX, mouseY, guiLeft, guiTop)){
                return this.skillNode.getElements()[i];
            }
            x+= 26 + 10;
        }
        return null;
    }

    public void drawHover(MatrixStack stack, int mouseX, int mouseY, float fade, int guiLeft, int guiTop, int left, int top, float zoom) {
        AdvancementState state = AdvancementState.UNOBTAINED;
        this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
        int x = getNodeStart();

        int selected = -1;
        for (int i = 0; i < this.skillNode.getElements().length; i++) {
            if (mouseX > (x + guiLeft + left + 8 + 4) * zoom && mouseX < (x + guiLeft + left + 8 + 26 + 3)*zoom&& mouseY > (this.y + guiTop + top + 18)*zoom && mouseY < (this.y + guiTop + top + 26 + 18)*zoom) {
                selected = i;
                break;
            }
            x+= 26 + 10;
        }

        if (selected != -1) {

            if (this.skillNode.getElements()[selected].getDescription() != null) {

                List<IReorderingProcessor> description = this.descriptions[selected];
                ISkillHandler.Result result = skillHandler.canSkillBeEnabled(this.skillNode.getElements()[selected]);

                List<ISkill> lockingSkills = null;
                TextFormatting lockingColor = TextFormatting.BLACK;
                if (this.skillNode.getLockingNodes().length != 0) {
                    lockingSkills = skillHandler.getLockingSkills(this.skillNode);
                    lockingColor = result == ISkillHandler.Result.ALREADY_ENABLED ? TextFormatting.DARK_GRAY : lockingSkills.stream().anyMatch(skillHandler::isSkillEnabled) ? TextFormatting.DARK_RED : TextFormatting.YELLOW;
                }
                if (lockingSkills != null) {
                    int l = 29 + 27;
                    int size = Math.max(l + minecraft.font.width(titles[selected]), 120) -20;
                    description = new ArrayList<>(description);
                    description.addAll(LanguageMap.getInstance().getVisualOrder(this.findOptimalLines(new TranslationTextComponent("text.vampirism.skill.excluding").withStyle(lockingColor),size )));
                    for (ISkill lockingSkill : lockingSkills) {
                        description.addAll(LanguageMap.getInstance().getVisualOrder(this.findOptimalLines(new StringTextComponent("  ").append(lockingSkill.getName().copy().withStyle(lockingColor)),size)));
                    }
                }

                GuiUtils.drawContinuousTexturedBox(stack, guiLeft + x - 3, guiTop + this.y +3, 0, 55, this.width[selected], 26 + description.size() * 9, 200, 20, 3, this.getBlitOffset());
                for (int i = 0; i < description.size(); i++) {
                    this.minecraft.font.draw(stack, description.get(i), guiLeft + x +2, guiTop + this.y +3 + 24 +i * 9,-1);
                }
            }

            this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
            int wid = this.width[selected] / 2;
            blit(stack, guiLeft + x - 3, guiTop + this.y + 3, 0, 29, wid, 22);
            blit(stack, guiLeft + x - 3 + wid, guiTop + this.y + 3, 200 - wid, 29, wid, 22);

            this.minecraft.font.drawShadow(stack, this.titles[selected], guiLeft + x+40, guiTop + this.y + 9, -1);
        }
        x = getNodeStart();
        for (int i1 = 0; i1 < this.skillNode.getElements().length; i1++) {
            if (selected == i1) {
                RenderSystem.color4f(1f, 1f, 1f, 1);
                this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
                this.blit(stack, guiLeft + x + 3, guiTop + this.y, FrameType.TASK.getTexture(), 128 + state.getIndex() * 26, 26, 26);

                this.minecraft.textureManager.bind(getIconLoc(this.skillNode.getElements()[i1]));
                RenderSystem.color4f(1, 1, 1, 1);
                RenderSystem.disableLighting();
                RenderSystem.enableBlend();
                UtilLib.drawTexturedModalRect(stack.last().pose(), this.getBlitOffset(), x + guiLeft + 8, this.y + guiTop + 5, 0, 0, 16, 16, 16, 16);
                RenderSystem.disableLighting();
            }
            x+= 26 + 10;
        }

    }

    private int getNodeStart() {
        int x = this.x;
        if (this.skillNode.getElements().length > 1) {
            if (this.skillNode.getElements().length % 2 == 0) {
                x -= ((this.skillNode.getElements().length)/2 * 26);
                x -= (this.skillNode.getElements().length - 1)/2  * 8;
                x-= 2 * ((this.skillNode.getElements().length)/2);
                x+= 10;
            } else {
                x -= (this.skillNode.getElements().length - 1) /2 * 26;
                x -= (this.skillNode.getElements().length - 1)/2 * 10;
            }
        }
        return x;
    }

    private List<ITextProperties> findOptimalLines(ITextComponent p_238694_1_, int p_238694_2_) {
        CharacterManager charactermanager = this.minecraft.font.getSplitter();
        List<ITextProperties> list = null;
        float f = Float.MAX_VALUE;

        for(int i : TEST_SPLIT_OFFSETS) {
            List<ITextProperties> list1 = charactermanager.splitLines(p_238694_1_, p_238694_2_ - i, Style.EMPTY);
            float f1 = Math.abs(getMaxWidth(charactermanager, list1) - (float)p_238694_2_);
            if (f1 <= 10.0F) {
                return list1;
            }

            if (f1 < f) {
                f = f1;
                list = list1;
            }
        }

        return list;
    }

    private static float getMaxWidth(CharacterManager p_238693_0_, List<ITextProperties> p_238693_1_) {
        return (float)p_238693_1_.stream().mapToDouble(p_238693_0_::stringWidth).max().orElse(0.0D);
    }

    enum SkillNodeState {
        LOCKED, AVAILABLE, UNLOCKED, HIDDEN;
    }
}
