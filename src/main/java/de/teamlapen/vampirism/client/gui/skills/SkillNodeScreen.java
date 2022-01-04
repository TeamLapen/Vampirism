package de.teamlapen.vampirism.client.gui.skills;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.player.skills.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.lwjgl.system.NonnullDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

@NonnullDefault
@ParametersAreNonnullByDefault
public class SkillNodeScreen extends AbstractGui {
    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(REFERENCE.MODID, "textures/gui/skills/widgets.png");
    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};
    private final Minecraft minecraft;
    private final SkillsTabScreen tab;
    private final SkillsScreen screen;
    private final SkillNode skillNode;
    private final SkillHandler<?> skillHandler;
    private final List<SkillNodeScreen> children = new ArrayList<>();
    @Nullable
    private final SkillNodeScreen parent;
    private final int x;
    private final int y;
    private final IReorderingProcessor[] titles;
    private final List<IReorderingProcessor>[] descriptions;
    private final int[] width;

    public SkillNodeScreen(Minecraft minecraft, SkillsScreen screen, SkillsTabScreen tab, SkillNode skillNode, SkillHandler<?> skillHandler) {
        this(minecraft, screen, tab, skillNode, skillHandler, null, 0, 0);
    }

    public SkillNodeScreen(Minecraft minecraft, SkillsScreen screen, SkillsTabScreen tab, SkillNode skillNode, SkillHandler<?> skillHandler, @Nullable SkillNodeScreen parent, int x, int y) {
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
            childrenCount -= 1;
            if (childrenCount > 0) {
                pos -= w / 2 - 13;
            }

            for (int i = 0; i < skillNode.getChildren().size(); i++) {
                SkillNode current = skillNode.getChildren().get(i);
                this.children.add(new SkillNodeScreen(minecraft, screen, tab, current, skillHandler, this, pos, y + 60));
                pos += SkillTree.getTreeWidth(current) / 2 + 30;
                if (skillNode.getChildren().size() >= i + 2) {
                    SkillNode next = skillNode.getChildren().get(i + 1);
                    pos += SkillTree.getTreeWidth(next) / 2;
                }

            }
        }

        this.titles = new IReorderingProcessor[skillNode.getElements().length];
        //noinspection unchecked
        this.descriptions = new List[skillNode.getElements().length];
        this.width = new int[skillNode.getElements().length];
        int l = 29 + 27;
        for (int i = 0; i < skillNode.getElements().length; i++) {
            this.titles[i] = LanguageMap.getInstance().getVisualOrder(minecraft.font.substrByWidth(skillNode.getElements()[i].getName(), 163));
            int size = Math.max(l + minecraft.font.width(titles[i]), 120);
            this.descriptions[i] = LanguageMap.getInstance().getVisualOrder(this.findOptimalLines(TextComponentUtils.mergeStyles(Optional.ofNullable(skillNode.getElements()[i].getDescription()).orElse(new StringTextComponent("")).copy(), Style.EMPTY.withColor(TextFormatting.GRAY)), size - 30));
            this.width[i] = size;
        }
    }

    private static float getMaxWidth(CharacterManager p_238693_0_, List<ITextProperties> p_238693_1_) {
        return (float) p_238693_1_.stream().mapToDouble(p_238693_0_::stringWidth).max().orElse(0.0D);
    }

    private SkillNodeState getState() {
        if (Arrays.stream(this.skillNode.getElements()).anyMatch(this.skillHandler::isSkillEnabled)) {
            return SkillNodeState.UNLOCKED;
        } else if (this.skillHandler.isSkillNodeLocked(this.skillNode)) {
            return SkillNodeState.LOCKED;
        } else if (Arrays.stream(this.skillNode.getParent().getElements()).anyMatch(this.skillHandler::isSkillEnabled)) {
            return SkillNodeState.AVAILABLE;
        } else {
            return this.skillNode.isHidden() ? SkillNodeState.HIDDEN :SkillNodeState.VISIBLE;
        }
    }

    private Collection<ISkill> getLockingSkills(SkillNode node) {
        return Arrays.stream(node.getLockingNodes()).map(id -> SkillTreeManager.getInstance().getSkillTree().getNodeFromId(id)).filter(Objects::nonNull).flatMap(node2 -> Arrays.stream(node2.getElements())).filter(skillHandler::isSkillEnabled).collect(Collectors.toList());
    }

    public List<SkillNodeScreen> getChildren() {
        return children;
    }

    public SkillNode getSkillNode() {
        return skillNode;
    }

    public void draw(MatrixStack stack, int i, int j) {
        SkillNodeState state = getState();
        if (state == SkillNodeState.HIDDEN) return;
        int width = 26 * this.skillNode.getElements().length + (this.skillNode.getElements().length - 1) * 10;
        this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);

        int x = getNodeStart();
        //draw skill background
        if (this.skillNode.getElements().length > 1) {
            GuiUtils.drawContinuousTexturedBox(stack, i + x, j + this.y, 200, 0, width, 26, 26, 26, 3, this.getBlitOffset());
        }

        //draw skills
        for (int i1 = 0; i1 < this.skillNode.getElements().length; i1++) {
            if (state == SkillNodeState.LOCKED || !skillHandler.isSkillEnabled(this.skillNode.getElements()[i1])) {
                RenderSystem.color4f(0.5f, 0.5f, 0.5f, 1);
            } else {
                RenderSystem.color4f(1, 1, 1, 1);
            }
            this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
            this.blit(stack, i + x, j + this.y,  skillNode.isRoot() ?226:200, 0, 26, 26);

            this.minecraft.textureManager.bind(getSkillIconLocation(this.skillNode.getElements()[i1]));
            RenderSystem.color4f(1, 1, 1, 1);
            RenderSystem.disableLighting();
            RenderSystem.enableBlend();
            UtilLib.drawTexturedModalRect(stack.last().pose(), this.getBlitOffset(), x + i + 5, this.y + j + 5, 0, 0, 16, 16, 16, 16);
            RenderSystem.disableLighting();

            x += 26 + 10;
        }

        for (SkillNodeScreen child : this.children) {
            child.draw(stack, i, j);
        }
    }

    public void drawConnectivity(MatrixStack stack, int startX, int startY, boolean outerLine) {
        SkillNodeState state = getState();
        if (state == SkillNodeState.HIDDEN) return;
        if (this.parent != null) {
            int color = state.pathColor(outerLine);

            int i = startX + x + 13;
            int i1 = startX + this.parent.x + 13;
            int j = startY + this.y - 30;
            int j2 = startY + this.parent.y + 13;
            int j3 = startY + this.y - 29;
            int j4 = startY + this.parent.y + 13 + 16;
            int i2 = startX + x + 13;
            int j5 = startY + this.y;
            int i3 = startX + this.parent.x + 13;
            if (outerLine) {
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
            child.drawConnectivity(stack, startX, startY, outerLine);
        }
    }

    public void drawHover(MatrixStack stack, double mouseX, double mouseY, float fade, int scrollX, int scrollY) {
        SkillNodeState state = getState();
        if (state == SkillNodeState.HIDDEN) return;
        this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
        ISkill[] elements = this.skillNode.getElements();

        //check if a node is hovered
        int hoveredSkill = -1;
        for (int i = 0; i < elements.length; i++) {
            if (this.isMouseOverSkill(i, mouseX, mouseY, scrollX, scrollY)) {
                hoveredSkill = i;
                break;
            }
        }

        if (hoveredSkill != -1) {
            int x = getNodeStart() + (26 + 10) * hoveredSkill;

            //draw blocked
            if (state == SkillNodeState.LOCKED || state == SkillNodeState.VISIBLE) {
                List<ITextComponent> text = new ArrayList<>();
                if (state == SkillNodeState.VISIBLE) {
                    ITextComponent t1 = new TranslationTextComponent("text.vampirism.skill.unlock_parent_first").withStyle(TextFormatting.DARK_RED);
                    text.add(t1);
                } else {
                    text.add(new TranslationTextComponent("text.vampirism.skill.locked"));
                    this.getLockingSkills(this.skillNode).stream().map(a -> a.getName().copy().withStyle(TextFormatting.DARK_RED)).forEach(text::add);
                }
                int width = text.stream().mapToInt(this.minecraft.font::width).max().getAsInt();
                this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
                GuiUtils.drawContinuousTexturedBox(stack, scrollX + x - 5 + this.width[hoveredSkill] - width -8, scrollY + this.y -3 - text.size() * 9, 0, 81, width+5, 6 + text.size() * 10, 200, 20, 3, this.getBlitOffset());
                int fontY = scrollY + this.y +1 - text.size() * 9;
                for (int i = 0; i < text.size(); i++) {
                    this.minecraft.font.drawShadow(stack, text.get(i),  scrollX + x - 8 -2+ this.width[hoveredSkill] - width ,  fontY+ i * 9, -1);
                }
            }

            //draw description
            if (elements[hoveredSkill].getDescription() != null) {
                List<IReorderingProcessor> description = getSkillDescription(hoveredSkill);
                this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
                GuiUtils.drawContinuousTexturedBox(stack, scrollX + x - 5, scrollY + this.y + 3, 0, 81, this.width[hoveredSkill], 30 + description.size() * 9, 200, 20, 3, this.getBlitOffset());
                for (int i = 0; i < description.size(); i++) {
                    this.minecraft.font.drawShadow(stack, description.get(i), scrollX + x + 2, scrollY + this.y + 3 + 24 + i * 9, -1);
                }
            }

            //draw title
            this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
            int wid = this.width[hoveredSkill] / 2;
            int titleTextureY = state.titleTextureY;
            if (state == SkillNodeState.UNLOCKED && !this.skillHandler.isSkillEnabled(elements[hoveredSkill])) {
                titleTextureY = SkillNodeState.LOCKED.titleTextureY;
            }
            blit(stack, scrollX + x - 5, scrollY + this.y + 3, 0, titleTextureY, wid, 22);
            blit(stack, scrollX + x - 5 + wid, scrollY + this.y + 3, 200 - wid, titleTextureY, wid, 22);
            this.minecraft.font.drawShadow(stack, this.titles[hoveredSkill], scrollX + x + 40, scrollY + this.y + 9, -1);

            //draw skill
            RenderSystem.color4f(1f, 1f, 1f, 1);
            this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
            this.blit(stack, scrollX + x, scrollY + this.y, skillNode.isRoot() ?226:200, 0, 26, 26);
            this.minecraft.textureManager.bind(getSkillIconLocation(elements[hoveredSkill]));
            RenderSystem.color4f(1, 1, 1, 1);
            RenderSystem.disableLighting();
            RenderSystem.enableBlend();
            UtilLib.drawTexturedModalRect(stack.last().pose(), this.getBlitOffset(), x + scrollX + 5, this.y + scrollY + 5, 0, 0, 16, 16, 16, 16);
            RenderSystem.disableLighting();
        }
    }

    /**
     * Adds additional elements to the skill description
     *
     * @param skill index of the skill
     * @return skill description
     */
    private List<IReorderingProcessor> getSkillDescription(int skill) {
        List<IReorderingProcessor> description = this.descriptions[skill];
        ISkillHandler.Result result = skillHandler.canSkillBeEnabled(this.skillNode.getElements()[skill]);

        List<ISkill> lockingSkills = null;
        TextFormatting lockingColor = TextFormatting.BLACK;
        if (this.skillNode.getLockingNodes().length != 0) {
            lockingSkills = skillHandler.getLockingSkills(this.skillNode);
            lockingColor = result == ISkillHandler.Result.ALREADY_ENABLED ? TextFormatting.DARK_GRAY : lockingSkills.stream().anyMatch(skillHandler::isSkillEnabled) ? TextFormatting.DARK_RED : TextFormatting.YELLOW;
        }
        if (lockingSkills != null) {
            int l = 29 + 27;
            int size = Math.max(l + minecraft.font.width(titles[skill]), 120) - 20;
            description = new ArrayList<>(description);
            description.addAll(LanguageMap.getInstance().getVisualOrder(this.findOptimalLines(new TranslationTextComponent("text.vampirism.skill.excluding").withStyle(lockingColor), size)));
            for (ISkill lockingSkill : lockingSkills) {
                description.addAll(LanguageMap.getInstance().getVisualOrder(this.findOptimalLines(new StringTextComponent("  ").append(lockingSkill.getName().copy().withStyle(lockingColor)), size)));
            }
        }
        return description;
    }

    private ResourceLocation getSkillIconLocation(ISkill skill) {
        if (skill instanceof ActionSkill) {
            return new ResourceLocation(((ActionSkill<?>) skill).getActionID().getNamespace(), "textures/actions/" + ((ActionSkill<?>) skill).getActionID().getPath() + ".png");
        } else {
            return new ResourceLocation(skill.getRegistryName().getNamespace(), "textures/skills/" + skill.getRegistryName().getPath() + ".png");
        }
    }

    public boolean isMouseOver(double mouseX, double mouseY, int scrollX, int scrollY) {
        double width = 26 * this.skillNode.getElements().length + (this.skillNode.getElements().length - 1) * 10;
        return mouseX >= this.x + scrollX - width / 2 + 13 && mouseX < this.x + scrollX + 13 + width / 2 - 1 && mouseY > scrollY + this.y && mouseY < scrollY + this.y + 26;
    }

    public boolean isMouseOverSkill(int index, double mouseX, double mouseY, int guiLeft, int guiTop) {
        int x = getNodeStart() + (26 + 10) * index;
        return mouseX > x + guiLeft && mouseX < x + guiLeft + 26 && mouseY > guiTop + this.y && mouseY < guiTop + this.y + 26;
    }

    @Nullable
    public ISkill getSelectedSkill(double mouseX, double mouseY, int scrollX, int scrollY) {
        if (!isMouseOver(mouseX, mouseY, scrollX, scrollY)) return null;
        for (int i = 0; i < this.skillNode.getElements().length; i++) {
            if (isMouseOverSkill(i, mouseX, mouseY, scrollX, scrollY)) {
                return this.skillNode.getElements()[i];
            }
        }
        return null;
    }

    /**
     * calculates the x position of the first skill
     *
     * @return x pos of first skill
     */
    private int getNodeStart() {
        int x = this.x;
        if (this.skillNode.getElements().length > 1) {
            if (this.skillNode.getElements().length % 2 == 0) {
                x -= ((this.skillNode.getElements().length) / 2 * 26);
                x -= (this.skillNode.getElements().length - 1) / 2 * 8;
                x -= 2 * ((this.skillNode.getElements().length) / 2);
                x += 10;
            } else {
                x -= (this.skillNode.getElements().length - 1) / 2 * 26;
                x -= (this.skillNode.getElements().length - 1) / 2 * 10;
            }
        }
        return x;
    }

    /**
     * from net.minecraft.client.gui.advancements.AdvancementEntryGui#findOptimalLines(ITextComponent, int)
     */
    private List<ITextProperties> findOptimalLines(ITextComponent p_238694_1_, int p_238694_2_) {
        CharacterManager charactermanager = this.minecraft.font.getSplitter();
        List<ITextProperties> list = Collections.emptyList();
        float f = Float.MAX_VALUE;

        for (int i : TEST_SPLIT_OFFSETS) {
            List<ITextProperties> list1 = charactermanager.splitLines(p_238694_1_, p_238694_2_ - i, Style.EMPTY);
            float f1 = Math.abs(getMaxWidth(charactermanager, list1) - (float) p_238694_2_);
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

    enum SkillNodeState {
        /**
         * Rendered and unlockable
         */
        AVAILABLE(3, 0xffa7a7a7, -1),
        /**
         * Not rendered
         */
        HIDDEN(0, 0, 0),
        /**
         * Rendered but not able to unlock (blocked)
         */
        LOCKED(55, 0xffcf0000, 0xff6a0000),
        /**
         * Rendered and unlocked
         */
        UNLOCKED(29, 0xff008711, 0xff005304),
        /**
         * Rendered but not unlockable
         */
        VISIBLE(3, 0xff222222, 0xff3f3f3f);

        /**
         * texture y offset for the tooltip title back rendering
         */
        public final int titleTextureY;
        /**
         * connectivity inner color
         */
        public final int innerColor;
        /**
         * connectivity outer color
         */
        public final int outerColor;

        SkillNodeState(int titleTextureY, int innerColor, int outerColor) {
            this.titleTextureY = titleTextureY;
            this.outerColor = outerColor;
            this.innerColor = innerColor;
        }

        /**
         * Get connectivity color
         *
         * @param outer inner or outer color
         * @return hex color as integer
         */
        int pathColor(boolean outer) {
            return outer ? this.outerColor : this.innerColor;
        }
    }
}
