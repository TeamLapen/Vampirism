package de.teamlapen.vampirism.client.gui.screens.skills;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.ISkillNode;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.data.ClientSkillTreeData;
import de.teamlapen.vampirism.entity.player.skills.ActionSkill;
import de.teamlapen.vampirism.entity.player.skills.SkillHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillTreeConfiguration;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.*;
import java.util.stream.Collectors;

@NonnullDefault
public class SkillNodeScreen {
    private static final ResourceLocation SKILL_BACKGROUND_SPRITE = VResourceLocation.mod("skills_screen/node");
    private static final ResourceLocation START_SKILL_BACKGROUND_SPRITE = VResourceLocation.mod("skills_screen/start_node");
    private static final ResourceLocation TITLE_RED_SPRITE = VResourceLocation.mod("skills_screen/title_red");
    private static final ResourceLocation TITLE_BLUE_SPRITE = VResourceLocation.mod("skills_screen/title_blue");
    private static final ResourceLocation TITLE_GREEN_SPRITE = VResourceLocation.mod("skills_screen/title_green");
    private static final ResourceLocation DESCRIPTION_SPRITE = VResourceLocation.mod("skills_screen/description");

    private static final int[] TEST_SPLIT_OFFSETS = new int[] {0, 10, -10, 25, -25};
    private final Minecraft minecraft;
    private final SkillsTabScreen tab;
    private final SkillsScreen screen;
    private final SkillTreeConfiguration.SkillTreeNodeConfiguration skillNode;
    private final ClientSkillTreeData treeData;
    private final SkillHandler<?> skillHandler;
    private final List<SkillNodeScreen> children = new ArrayList<>();
    @Nullable
    private final SkillNodeScreen parent;
    private final int x;
    private final int y;
    private final FormattedCharSequence[] titles;
    private final List<FormattedCharSequence>[] descriptions;
    private final int[] width;

    public SkillNodeScreen(@NotNull Minecraft minecraft, @NotNull SkillsScreen screen, @NotNull SkillsTabScreen tab, @NotNull SkillTreeConfiguration.SkillTreeNodeConfiguration skillNode, ClientSkillTreeData treeData, @NotNull SkillHandler<?> skillHandler) {
        this(minecraft, screen, tab, skillNode, treeData, skillHandler, null, 0, 0);
    }

    public SkillNodeScreen(@NotNull Minecraft minecraft, @NotNull SkillsScreen screen, @NotNull SkillsTabScreen tab, @NotNull SkillTreeConfiguration.SkillTreeNodeConfiguration skillNode, ClientSkillTreeData treeData, @NotNull SkillHandler<?> skillHandler, @Nullable SkillNodeScreen parent, int x, int y) {
        this.minecraft = minecraft;
        this.tab = tab;
        this.screen = screen;
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
                this.children.add(new SkillNodeScreen(minecraft, screen, tab, current, this.treeData, skillHandler, this, pos, y + 60));
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

    private static float getMaxWidth(@NotNull StringSplitter p_238693_0_, @NotNull List<FormattedText> p_238693_1_) {
        return (float) p_238693_1_.stream().mapToDouble(p_238693_0_::stringWidth).max().orElse(0.0D);
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

    private List<Holder<ISkill<?>>> getLockingSkills(SkillTreeConfiguration.SkillTreeNodeConfiguration node) {
        Registry<ISkillNode> nodes = minecraft.level.registryAccess().registryOrThrow(VampirismRegistries.Keys.SKILL_NODE);
        return node.node().value().lockingNodes().stream().flatMap(x -> nodes.getOptional(x).stream()).flatMap(x -> x.skills().stream()).collect(Collectors.toList());
    }

    public List<SkillNodeScreen> getChildren() {
        return children;
    }

    public SkillTreeConfiguration.SkillTreeNodeConfiguration getSkillNode() {
        return skillNode;
    }

    public void draw(@NotNull GuiGraphics graphics, int i, int j) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        SkillNodeState state = getState();
        if (state == SkillNodeState.HIDDEN) return;
        int width = getNodeWidth();

        // center and in front of the connectors
        pose.translate(-width / 2f, 0, 50);

        int x = i + getNodeStart();
        //draw skill background
        if (this.skillNode.elementCount() > 1) {
            graphics.blitSprite(SKILL_BACKGROUND_SPRITE, x, this.y + j, width, 26);
        }

        //draw skills
        for (int i1 = 0; i1 < this.skillNode.elementCount(); i1++) {
            if (state == SkillNodeState.LOCKED || !skillHandler.isSkillEnabled(this.skillNode.elements().get(i1))) {
                graphics.setColor(0.5f, 0.5f, 0.5f, 1);
            } else {
                graphics.setColor(1, 1, 1, 1);
            }
            graphics.blitSprite(skillNode.isRoot() ? START_SKILL_BACKGROUND_SPRITE : SKILL_BACKGROUND_SPRITE, x, this.y + j, 26, 26);


            graphics.setColor(1, 1, 1, 1);
            RenderSystem.enableBlend();
            graphics.blit(getSkillIconLocation(this.skillNode.elements().get(i1).value()), x + 5, this.y + j + 5, 0, 0, 16, 16, 16, 16);

            x += 26 + 10;
        }

        pose.popPose();

        for (SkillNodeScreen child : this.children) {
            child.draw(graphics, i, j);
        }
    }

    public void drawConnectivity(@NotNull GuiGraphics graphics, int startX, int startY, boolean outerLine) {
        SkillNodeState state = getState();
        if (state == SkillNodeState.HIDDEN) return;
        if (this.parent != null) {
            int color = state.pathColor(outerLine);
            PoseStack pose = graphics.pose();

            pose.pushPose();
            if (state == SkillNodeState.UNLOCKED) {
                pose.translate(0, 0, 10);
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
                graphics.hLine(i, i1, j - 1, color);
                graphics.hLine(i, i1, j + 1, color);
                graphics.vLine(i1 - 1, j2, j3, color);
                graphics.vLine(i3 + 1, j2, j3, color);
                graphics.vLine(i - 1, j4, j5 + 1, color);
                graphics.vLine(i2 + 1, j4, j5 + 1, color);
            } else {
                graphics.hLine(i, i1, j, color);
                graphics.vLine(i1, j2, j3, color);
                graphics.vLine(i, j4, j5 + 1, color);
            }
            pose.popPose();

        }

        for (SkillNodeScreen child : this.children) {
            child.drawConnectivity(graphics, startX, startY, outerLine);
        }
    }

    private int getNodeWidth() {
        return 26 * this.skillNode.elementCount() + (this.skillNode.elementCount() - 1) * 10;
    }

    public void drawHover(@NotNull GuiGraphics graphics, double mouseX, double mouseY, float fade, int scrollX, int scrollY) {
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

            Collection<Holder<ISkill<?>>> lockingSkills = this.getLockingSkills(this.skillNode);
            //draw blocked
            if (state == SkillNodeState.LOCKED || state == SkillNodeState.VISIBLE) {
                List<Component> text = new ArrayList<>();
                if (state == SkillNodeState.VISIBLE) {
                    MutableComponent t1 = Component.translatable("text.vampirism.skill.unlock_parent_first").withStyle(ChatFormatting.DARK_RED);
                    text.add(t1);
                } else {
                    text.add(Component.translatable("text.vampirism.skill.locked"));
                    lockingSkills.stream().map(a -> a.value().getName().copy().withStyle(ChatFormatting.DARK_RED)).forEach(text::add);
                }
                int width = text.stream().mapToInt(this.minecraft.font::width).max().getAsInt();
                graphics.blitSprite(DESCRIPTION_SPRITE, scrollX + x - 3, scrollY + this.y - 3 - text.size() * 9, width + 8, 10 + text.size() * 10);
                int fontY = scrollY + this.y + 1 - text.size() * 9;
                for (int i = 0; i < text.size(); i++) {
                    graphics.drawString(this.minecraft.font, text.get(i), scrollX + x + 2, fontY + i * 9, -1, true);
                }
            }

            List<FormattedCharSequence> description = this.descriptions[hoveredSkillIndex];

            if (!lockingSkills.isEmpty()) {
                List<Component> text = new ArrayList<>();
                text.add(Component.translatable("text.vampirism.skill.excluding"));
                lockingSkills.stream().map(a -> a.value().getName().copy().withStyle(ChatFormatting.YELLOW)).forEach(text::add);
                int width = Math.min(this.width[hoveredSkillIndex], text.stream().mapToInt(this.minecraft.font::width).max().getAsInt());

                int yOffset = description.isEmpty() ? 15 : 24;
                graphics.blitSprite(DESCRIPTION_SPRITE, scrollX + x - 3, scrollY + this.y + 3 + 7 + description.size() * 9, width + 8, 10 + text.size() * 10 + yOffset);
                int fontY = scrollY + this.y + 3 + yOffset + 8 + description.size() * 9;
                for (int i = 0; i < text.size(); i++) {
                    graphics.drawString(this.minecraft.font, text.get(i), scrollX + x + 2, fontY + i * 9, -1, true);
                }
            }

            //draw description
            if (!description.isEmpty()) {
                graphics.blitSprite(DESCRIPTION_SPRITE, scrollX + x - 5, scrollY + this.y + 3, this.width[hoveredSkillIndex], 30 + description.size() * 9);
                for (int i = 0; i < description.size(); i++) {
                    graphics.drawString(this.minecraft.font, description.get(i), scrollX + x + 2, scrollY + this.y + 3 + 24 + i * 9, -1, true);
                }
            }

            //draw title
            ResourceLocation texture = state.sprite;
            if (state == SkillNodeState.UNLOCKED && !this.skillHandler.isSkillEnabled(hoveredSkill)) {
                texture = SkillNodeState.LOCKED.sprite;
            }
            graphics.blitSprite(texture, scrollX + x - 5, scrollY + this.y + 3, this.width[hoveredSkillIndex], 20);
            graphics.drawString(this.minecraft.font, this.titles[hoveredSkillIndex], scrollX + x + 40, scrollY + this.y + 9, -1, true);

            //draw skill point cost
            if (!this.skillNode.isRoot()) {
                int cost = hoveredSkill.value().getSkillPointCost();
                int costWidth = this.minecraft.font.width(String.valueOf(cost));
                int costHeight = this.minecraft.font.lineHeight;
                graphics.blitSprite(DESCRIPTION_SPRITE, scrollX + x + 24, scrollY + this.y + ((26 - costHeight) / 2) - 1, costWidth + 5, costHeight + 4);
                graphics.drawString(this.minecraft.font, Component.literal(String.valueOf(cost)), scrollX + x + 27, (int) (scrollY + this.y + ((26 - costHeight) / 2f) + 1), -1, true);
            }

            //draw skill
            graphics.setColor(1f, 1f, 1f, 1);
            graphics.blitSprite(skillNode.isRoot() ? START_SKILL_BACKGROUND_SPRITE : SKILL_BACKGROUND_SPRITE, scrollX + x, scrollY + this.y, 26, 26);
            RenderSystem.enableBlend();
            graphics.blit(getSkillIconLocation(hoveredSkill.value()), x + scrollX + 5, this.y + scrollY + 5, 0, 0, 16, 16, 16, 16);
        }
    }

    private ResourceLocation getSkillIconLocation(@NotNull ISkill skill) {
        if (skill instanceof ActionSkill) {
            return VResourceLocation.loc(((ActionSkill<?>) skill).getActionID().getNamespace(), "textures/actions/" + ((ActionSkill<?>) skill).getActionID().getPath() + ".png");
        } else {
            ResourceLocation id = RegUtil.id(skill);
            return VResourceLocation.loc(id.getNamespace(), "textures/skills/" + id.getPath() + ".png");
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
    public Holder<ISkill<?>> getSelectedSkill(double mouseX, double mouseY, int scrollX, int scrollY) {
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
        public final ResourceLocation sprite;
        /**
         * connectivity inner color
         */
        public final int innerColor;
        /**
         * connectivity outer color
         */
        public final int outerColor;

        SkillNodeState(ResourceLocation sprite, int innerColor, int outerColor) {
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
