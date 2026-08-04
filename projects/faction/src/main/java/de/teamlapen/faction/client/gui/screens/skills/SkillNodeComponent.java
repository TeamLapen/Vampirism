package de.teamlapen.faction.client.gui.screens.skills;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillNode;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.client.gui.GuiRenderer;
import de.teamlapen.faction.common.factions.skills.ClientSkillTreeData;
import de.teamlapen.faction.common.factions.skills.SkillHandler;
import de.teamlapen.faction.common.factions.skills.SkillTreeConfiguration;
import de.teamlapen.faction.common.util.RegUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.stream.Collectors;

@NullMarked
public class SkillNodeComponent {
    private static final Identifier SKILL_BACKGROUND_SPRITE = FIdentifier.mod("skills_screen/node");
    private static final Identifier START_SKILL_BACKGROUND_SPRITE = FIdentifier.mod("skills_screen/start_node");
    private static final Identifier TITLE_RED_SPRITE = FIdentifier.mod("skills_screen/title_red");
    private static final Identifier TITLE_BLUE_SPRITE = FIdentifier.mod("skills_screen/title_blue");
    private static final Identifier TITLE_GREEN_SPRITE = FIdentifier.mod("skills_screen/title_green");
    private static final Identifier DESCRIPTION_SPRITE = FIdentifier.mod("skills_screen/description");

    private static final int[] TEST_SPLIT_OFFSETS = new int[] {0, 10, -10, 25, -25};
    private final Minecraft minecraft;
    private final SkillTreeConfiguration.SkillTreeNodeConfiguration skillNode;
    private final ClientSkillTreeData treeData;
    private final SkillHandler<?> skillHandler;
    private final List<SkillNodeComponent> children = new ArrayList<>();
    @Nullable
    private final SkillNodeComponent parent;
    private final int x;
    private final int y;
    private final FormattedCharSequence[] titles;
    private final List<FormattedCharSequence>[] descriptions;
    private final int[] width;

    public SkillNodeComponent(Minecraft minecraft, SkillsScreen screen, SkillsTabComponent tab, SkillTreeConfiguration.SkillTreeNodeConfiguration skillNode, ClientSkillTreeData treeData, SkillHandler<?> skillHandler) {
        this(minecraft, screen, tab, skillNode, treeData, skillHandler, null, 0, 0);
    }

    public SkillNodeComponent(Minecraft minecraft, SkillsScreen screen, SkillsTabComponent tab, SkillTreeConfiguration.SkillTreeNodeConfiguration skillNode, ClientSkillTreeData treeData, SkillHandler<?> skillHandler, @Nullable SkillNodeComponent parent, int x, int y) {
        this.minecraft = minecraft;
        this.skillNode = skillNode;
        this.treeData = treeData;
        this.skillHandler = skillHandler;
        this.parent = parent;
        this.x = x;
        this.y = y;
        int w = this.treeData.getNodeWidth(skillNode);
        int childrenCount = this.skillNode.children().size();
        if (childrenCount > 0) {

            int pos = x;
            childrenCount -= 1;
            if (childrenCount > 0) {
                pos -= w / 2 - 13;
            }

            for (int i = 0; i < skillNode.childrenCount(); i++) {
                SkillTreeConfiguration.SkillTreeNodeConfiguration current = skillNode.children().get(i);
                this.children.add(new SkillNodeComponent(minecraft, screen, tab, current, this.treeData, skillHandler, this, pos, y + 60));
                pos += this.treeData.getNodeWidth(current) / 2 + 30;
                if (skillNode.children().size() >= i + 2) {
                    SkillTreeConfiguration.SkillTreeNodeConfiguration next = skillNode.children().get(i + 1);
                    pos += this.treeData.getNodeWidth(next) / 2;
                }

            }
        }

        this.titles = new FormattedCharSequence[skillNode.elementCount()];
        //noinspection unchecked
        this.descriptions = new List[skillNode.elementCount()];
        this.width = new int[skillNode.elementCount()];
        int l = 29 + 27;
        for (int i = 0; i < skillNode.elementCount(); i++) {
            this.titles[i] = Language.getInstance().getVisualOrder(minecraft.font.substrByWidth(skillNode.elements().get(i).value().getName(), 163));
            int size = Math.max(l + minecraft.font.width(titles[i]), 120);
            this.descriptions[i] = Language.getInstance().getVisualOrder(this.findOptimalLines(ComponentUtils.mergeStyles(Optional.ofNullable(skillNode.elements().get(i).value().getDescription()).orElse(Component.empty()).copy(), Style.EMPTY.withColor(ChatFormatting.GRAY)), size - 30));
            this.width[i] = size;
        }
    }

    private static float getMaxWidth(StringSplitter splitter, List<FormattedText> texts) {
        return (float) texts.stream().mapToDouble(splitter::stringWidth).max().orElse(0.0D);
    }

    private SkillNodeState getState() {
        if (this.skillNode.elements().stream().anyMatch(this.skillHandler::isSkillEnabled)) {
            return SkillNodeState.UNLOCKED;
        } else if (this.skillHandler.isSkillNodeLocked(this.skillNode.node().value())) {
            return SkillNodeState.LOCKED;
        } else if (this.skillNode.isRoot() || this.treeData.getParent(this.skillNode).stream().flatMap(s -> s.value().skills().stream()).anyMatch(this.skillHandler::isSkillEnabled)) {
            return SkillNodeState.AVAILABLE;
        } else {
            return SkillNodeState.VISIBLE;
        }
    }

    private List<Holder<? extends ISkill<?>>> getLockingSkills(SkillTreeConfiguration.SkillTreeNodeConfiguration node) {
        Registry<ISkillNode> nodes = minecraft.level.registryAccess().lookupOrThrow(FactionRegistries.Keys.SKILL_NODE);
        return node.node().value().lockingNodes().stream().flatMap(x -> nodes.getOptional(x).stream()).flatMap(x -> x.skills().stream()).collect(Collectors.toList());
    }

    public List<SkillNodeComponent> getChildren() {
        return children;
    }

    public SkillTreeConfiguration.SkillTreeNodeConfiguration getSkillNode() {
        return skillNode;
    }

    public void draw(GuiGraphicsExtractor graphics, int i, int j) {
        var pose = graphics.pose();
        pose.pushMatrix();
        SkillNodeState state = getState();
        if (state == SkillNodeState.HIDDEN) return;
        int width = getNodeWidth();

        // center and in front of the connectors
        pose.translate(-width / 2f, 0);

        int x = i + getNodeStart();
        //draw skill background
        if (this.skillNode.elementCount() > 1) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SKILL_BACKGROUND_SPRITE, x, this.y + j, width, 26);
        }

        //draw skills
        for (int i1 = 0; i1 < this.skillNode.elementCount(); i1++) {
            int color = -1;
            if (state == SkillNodeState.LOCKED || !skillHandler.isSkillEnabled(this.skillNode.elements().get(i1))) {
                color = ARGB.colorFromFloat(1, 0.5f, 0.5f, 0.5f);
            }
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, skillNode.isRoot() ? START_SKILL_BACKGROUND_SPRITE : SKILL_BACKGROUND_SPRITE, x, this.y + j, 26, 26, color);


            GuiRenderer.blit(graphics, getSkillIconLocation(this.skillNode.elements().get(i1).value()), x+5, this.y + j + 5, 16 ,16, 16, 16);

            x += 26 + 10;
        }

        pose.popMatrix();

        for (SkillNodeComponent child : this.children) {
            child.draw(graphics, i, j);
        }
    }

    public void drawConnectivity(GuiGraphicsExtractor graphics, int startX, int startY, boolean outerLine) {
        SkillNodeState state = getState();
        if (state == SkillNodeState.HIDDEN) return;
        if (this.parent != null) {
            int color = state.pathColor(outerLine);
            var pose = graphics.pose();

            pose.pushMatrix();
            if (state == SkillNodeState.UNLOCKED) {
                //           pose.translate(0, 0, 10);
            }
            int i = startX + x;
            int i1 = startX + this.parent.x;
            int j = startY + this.y - 30;
            int j2 = startY + this.parent.y + 13;
            int j3 = startY + this.y - 29;
            int j4 = startY + this.parent.y + 13 + 16;
            int i2 = startX + x;
            int j5 = startY + this.y;
            int i3 = startX + this.parent.x;
            if (outerLine) {
                graphics.horizontalLine(i, i1, j - 1, color);
                graphics.horizontalLine(i, i1, j + 1, color);
                graphics.verticalLine(i1 - 1, j2, j3, color);
                graphics.verticalLine(i3 + 1, j2, j3, color);
                graphics.verticalLine(i - 1, j4, j5 + 1, color);
                graphics.verticalLine(i2 + 1, j4, j5 + 1, color);
            } else {
                graphics.horizontalLine(i, i1, j, color);
                graphics.verticalLine(i1, j2, j3, color);
                graphics.verticalLine(i, j4, j5 + 1, color);
            }
            pose.popMatrix();

        }

        for (SkillNodeComponent child : this.children) {
            child.drawConnectivity(graphics, startX, startY, outerLine);
        }
    }

    private int getNodeWidth() {
        return 26 * this.skillNode.elementCount() + (this.skillNode.elementCount() - 1) * 10;
    }

    public void drawHover(GuiGraphicsExtractor graphics, double mouseX, double mouseY, float fade, int scrollX, int scrollY) {
        SkillNodeState state = getState();
        if (state == SkillNodeState.HIDDEN) return;
        Holder<ISkill<?>>[] elements = this.skillNode.elements().toArray(Holder[]::new);
        scrollX -= (int) (getNodeWidth() / 2f);

        //check if a node is hovered
        int hoveredSkillIndex = -1;
        for (int i = 0; i < elements.length; i++) {
            if (this.isMouseOverSkill(i, mouseX, mouseY, scrollX, scrollY)) {
                hoveredSkillIndex = i;
                break;
            }
        }

        if (hoveredSkillIndex != -1) {
            Holder<ISkill<?>> hoveredSkill = elements[hoveredSkillIndex];
            int x = getNodeStart() + (26 + 10) * hoveredSkillIndex;

            Collection<Holder<? extends ISkill<?>>> lockingSkills = this.getLockingSkills(this.skillNode);
            //draw blocked
            if (state == SkillNodeState.LOCKED || state == SkillNodeState.VISIBLE) {
                List<Component> text = new ArrayList<>();
                if (state == SkillNodeState.VISIBLE) {
                    MutableComponent t1 = Component.translatable("gui.factionapi.skills.unlock_parent_first").withStyle(ChatFormatting.DARK_RED);
                    text.add(t1);
                } else {
                    text.add(Component.translatable("gui.factionapi.skills.locked"));
                    lockingSkills.stream().map(a -> a.value().getName().copy().withStyle(ChatFormatting.DARK_RED)).forEach(text::add);
                }
                int width = text.stream().mapToInt(this.minecraft.font::width).max().getAsInt();
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, DESCRIPTION_SPRITE, scrollX + x - 3, scrollY + this.y - 3 - text.size() * 9, width + 8, 10 + text.size() * 10);
                int fontY = scrollY + this.y + 1 - text.size() * 9;
                for (int i = 0; i < text.size(); i++) {
                    graphics.text(this.minecraft.font, text.get(i), scrollX + x + 2, fontY + i * 9, -1, true);
                }
            }

            List<FormattedCharSequence> description = this.descriptions[hoveredSkillIndex];

            if (!lockingSkills.isEmpty()) {
                List<Component> text = new ArrayList<>();
                text.add(Component.translatable("gui.factionapi.skills.excluding"));
                lockingSkills.stream().map(a -> a.value().getName().copy().withStyle(ChatFormatting.YELLOW)).forEach(text::add);
                int width = Math.min(this.width[hoveredSkillIndex], text.stream().mapToInt(this.minecraft.font::width).max().getAsInt());

                int yOffset = description.isEmpty() ? 15 : 24;
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, DESCRIPTION_SPRITE, scrollX + x - 3, scrollY + this.y + 3 + 7 + description.size() * 9, width + 8, 10 + text.size() * 10 + yOffset);
                int fontY = scrollY + this.y + 3 + yOffset + 8 + description.size() * 9;
                for (int i = 0; i < text.size(); i++) {
                    graphics.text(this.minecraft.font, text.get(i), scrollX + x + 2, fontY + i * 9, -1, true);
                }
            }

            //draw description
            if (!description.isEmpty()) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, DESCRIPTION_SPRITE, scrollX + x - 5, scrollY + this.y + 3, this.width[hoveredSkillIndex], 30 + description.size() * 9);
                for (int i = 0; i < description.size(); i++) {
                    graphics.text(this.minecraft.font, description.get(i), scrollX + x + 2, scrollY + this.y + 3 + 24 + i * 9, -1, true);
                }
            }

            //draw title
            Identifier texture = state.sprite;
            if (state == SkillNodeState.UNLOCKED && !this.skillHandler.isSkillEnabled(hoveredSkill)) {
                texture = SkillNodeState.LOCKED.sprite;
            }
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, scrollX + x - 5, scrollY + this.y + 3, this.width[hoveredSkillIndex], 20);
            graphics.text(this.minecraft.font, this.titles[hoveredSkillIndex], scrollX + x + 40, scrollY + this.y + 9, -1, true);

            //draw skill point cost
            if (!this.skillNode.isRoot()) {
                int cost = hoveredSkill.value().getSkillPointCost();
                int costWidth = this.minecraft.font.width(String.valueOf(cost));
                int costHeight = this.minecraft.font.lineHeight;
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, DESCRIPTION_SPRITE, scrollX + x + 24, scrollY + this.y + ((26 - costHeight) / 2) - 1, costWidth + 5, costHeight + 4);
                graphics.text(this.minecraft.font, Component.literal(String.valueOf(cost)), scrollX + x + 27, (int) (scrollY + this.y + ((26 - costHeight) / 2f) + 1), -1, true);
            }

            //draw skill
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, skillNode.isRoot() ? START_SKILL_BACKGROUND_SPRITE : SKILL_BACKGROUND_SPRITE, scrollX + x, scrollY + this.y, 26, 26);
            GuiRenderer.blit(graphics, getSkillIconLocation(hoveredSkill.value()), x + scrollX + 5, this.y + scrollY + 5, 16, 16, 16, 16);
        }
    }

    private Identifier getSkillIconLocation(ISkill<?> skill) {
        var action = skill.getAction();
        if (action != null) {
            Identifier location = action.unwrapKey().orElseThrow().identifier();
            return location.withPath(x -> "textures/actions/" + x + ".png");
        } else {
            Identifier id = RegUtil.id(skill);
            return id.withPath(x -> "textures/skills/" + x + ".png");
        }
    }

    public boolean isMouseOver(double mouseX, double mouseY, int scrollX, int scrollY) {
        double width = getNodeWidth();
        return mouseX >= this.x + scrollX - width / 2 && mouseX < this.x + scrollX + width / 2 - 1 && mouseY > scrollY + this.y && mouseY < scrollY + this.y + 26;
    }

    public boolean isMouseOverSkill(int index, double mouseX, double mouseY, int guiLeft, int guiTop) {
        int x = this.x + (26 + 10) * index;
        return mouseX > x + guiLeft && mouseX < x + guiLeft + 26 && mouseY > guiTop + this.y && mouseY < guiTop + this.y + 26;
    }

    @Nullable
    public Holder<? extends ISkill<?>> getSelectedSkill(double mouseX, double mouseY, int scrollX, int scrollY) {
        if (!isMouseOver(mouseX, mouseY, scrollX, scrollY)) return null;
        int nodeWidth = getNodeWidth();
        for (int i = 0; i < this.skillNode.elementCount(); i++) {
            if (isMouseOverSkill(i, mouseX, mouseY, (int) (scrollX - nodeWidth / 2f), scrollY)) {
                return this.skillNode.elements().get(i);
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
        return x;
    }

    /**
     * from net.minecraft.client.gui.advancements.AdvancementEntryGui#findOptimalLines(ITextComponent, int)
     */
    private List<FormattedText> findOptimalLines(@NotNull Component p_238694_1_, int p_238694_2_) {
        StringSplitter charactermanager = this.minecraft.font.getSplitter();
        List<FormattedText> list = Collections.emptyList();
        float f = Float.MAX_VALUE;

        for (int i : TEST_SPLIT_OFFSETS) {
            List<FormattedText> list1 = charactermanager.splitLines(p_238694_1_, p_238694_2_ - i, Style.EMPTY);
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
        AVAILABLE(TITLE_BLUE_SPRITE, 0xffa7a7a7, -1),
        /**
         * Not rendered
         */
        HIDDEN(null, 0, 0),
        /**
         * Rendered but not able to unlock (blocked)
         */
        LOCKED(TITLE_RED_SPRITE, 0xffcf0000, 0xff6a0000),
        /**
         * Rendered and unlocked
         */
        UNLOCKED(TITLE_GREEN_SPRITE, 0xff008711, 0xff005304),
        /**
         * Rendered but not unlockable
         */
        VISIBLE(TITLE_BLUE_SPRITE, 0xff222222, 0xff3f3f3f);

        /**
         * texture y offset for the tooltip title back rendering
         */
        public final Identifier sprite;
        /**
         * connectivity inner color
         */
        public final int innerColor;
        /**
         * connectivity outer color
         */
        public final int outerColor;

        SkillNodeState(Identifier sprite, int innerColor, int outerColor) {
            this.sprite = sprite;
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
