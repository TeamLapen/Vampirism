package de.teamlapen.faction.client.gui.screens.skills;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.client.gui.GuiRenderer;
import de.teamlapen.faction.common.factions.skills.SkillHandler;
import de.teamlapen.faction.common.factions.skills.SkillTreeGraph;
import de.teamlapen.faction.common.util.RegUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SkillSegmentComponent {
    
    private static final Identifier SKILL_BACKGROUND_SPRITE = FIdentifier.mod("skills_screen/node");
    private static final Identifier START_SKILL_BACKGROUND_SPRITE = FIdentifier.mod("skills_screen/start_node");
    private static final Identifier SKILL_RESET_BACKGROUND_SPRITE = FIdentifier.mod("skills_screen/node_reset");
    private static final Identifier START_SKILL_RESET_BACKGROUND_SPRITE = FIdentifier.mod("skills_screen/start_node_reset");
    private static final Identifier TITLE_RED_SPRITE = FIdentifier.mod("skills_screen/title_red");
    private static final Identifier TITLE_BLUE_SPRITE = FIdentifier.mod("skills_screen/title_blue");
    private static final Identifier TITLE_GREEN_SPRITE = FIdentifier.mod("skills_screen/title_green");
    private static final Identifier DESCRIPTION_SPRITE = FIdentifier.mod("skills_screen/description");

    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};

    private final Minecraft minecraft;
    private final SkillTreeGraph.Entry entry;
    private final SkillTreeLayout.Placement placement;
    private final SkillHandler<?> skillHandler;
    private final List<SkillSegmentComponent> parents = new ArrayList<>();
    private final List<Holder<? extends ISkill<?>>> lockingSkills;
    private final FormattedCharSequence[] titles;
    private final List<FormattedCharSequence>[] descriptions;
    private final int[] width;

    public SkillSegmentComponent(Minecraft minecraft, SkillTreeLayout.Placement placement, SkillTreeGraph graph, SkillHandler<?> skillHandler) {
        this.minecraft = minecraft;
        this.entry = placement.entry();
        this.placement = placement;
        this.skillHandler = skillHandler;
        this.lockingSkills = graph.lockingSegments(this.entry).stream().flatMap(x -> x.skills().stream()).toList();

        List<Holder<? extends ISkill<?>>> skills = this.entry.skills();
        this.titles = new FormattedCharSequence[skills.size()];
        //noinspection unchecked
        this.descriptions = new List[skills.size()];
        this.width = new int[skills.size()];
        int length = 29 + 27;
        for (int i = 0; i < skills.size(); i++) {
            this.titles[i] = Language.getInstance().getVisualOrder(skills.get(i).value().getName());
            int size = Math.max(length + minecraft.font.width(titles[i]), 120);
            this.descriptions[i] = Language.getInstance().getVisualOrder(this.findOptimalLines(ComponentUtils.mergeStyles(Optional.ofNullable(skills.get(i).value().getDescription()).orElse(Component.empty()).copy(), Style.EMPTY.withColor(ChatFormatting.GRAY)), size - 30));
            this.width[i] = size;
        }
    }

    public void bindParents(List<SkillSegmentComponent> parents) {
        this.parents.clear();
        this.parents.addAll(parents);
    }

    public SkillTreeGraph.Entry getEntry() {
        return this.entry;
    }

    public List<SkillSegmentComponent> getParents() {
        return this.parents;
    }

    public SkillTreeLayout.Placement getPlacement() {
        return this.placement;
    }

    private static float getMaxWidth(StringSplitter splitter, List<FormattedText> texts) {
        return (float) texts.stream().mapToDouble(splitter::stringWidth).max().orElse(0.0D);
    }

    SkillSegmentState getState() {
        if (this.entry.skills().stream().anyMatch(this.skillHandler::isSkillEnabled)) {
            return SkillSegmentState.UNLOCKED;
        } else if (this.lockingSkills.stream().anyMatch(this.skillHandler::isSkillEnabled)) {
            return SkillSegmentState.LOCKED;
        } else if (this.entry.isRoot() || this.entry.parents().stream().flatMap(x -> x.skills().stream()).anyMatch(this.skillHandler::isSkillEnabled)) {
            return SkillSegmentState.AVAILABLE;
        } else {
            return SkillSegmentState.VISIBLE;
        }
    }

    public void draw(GuiGraphicsExtractor graphics, int offsetX, int offsetY) {
        SkillSegmentState state = getState();
        if (state == SkillSegmentState.HIDDEN) return;

        var pose = graphics.pose();
        pose.pushMatrix();
        int width = this.placement.width();

        // center and in front of the connectors
        pose.translate(-width / 2f, 0);

        int x = offsetX + this.placement.x();
        //draw skill background
        if (this.entry.skills().size() > 1) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SKILL_BACKGROUND_SPRITE, x, this.placement.y() + offsetY, width, SkillTreeLayout.SKILL_SIZE);
        }

        //draw skills
        for (Holder<? extends ISkill<?>> skill : this.entry.skills()) {
            int color = -1;
            if (state == SkillSegmentState.LOCKED || !this.skillHandler.isSkillEnabled(skill)) {
                color = ARGB.colorFromFloat(1, 0.5f, 0.5f, 0.5f);
            }
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.entry.isRoot() ? START_SKILL_BACKGROUND_SPRITE : SKILL_BACKGROUND_SPRITE, x, this.placement.y() + offsetY, SkillTreeLayout.SKILL_SIZE, SkillTreeLayout.SKILL_SIZE, color);
            GuiRenderer.blit(graphics, getSkillIconLocation(skill.value()), x + 5, this.placement.y() + offsetY + 5, 16, 16, 16, 16);

            x += SkillTreeLayout.SKILL_SIZE + SkillTreeLayout.SKILL_GAP;
        }

        pose.popMatrix();
    }

    public void drawHover(GuiGraphicsExtractor graphics, double mouseX, double mouseY, float fade, int scrollX, int scrollY, @Nullable Holder<? extends ISkill<?>> heldSkill, float holdingProgress) {
        SkillSegmentState state = getState();
        if (state == SkillSegmentState.HIDDEN) return;
        List<Holder<? extends ISkill<?>>> skills = this.entry.skills();
        scrollX -= (int) (this.placement.width() / 2f);

        //check if a node is hovered
        int hoveredSkillIndex = -1;
        for (int i = 0; i < skills.size(); i++) {
            if (this.isMouseOverSkill(i, mouseX, mouseY, scrollX, scrollY)) {
                hoveredSkillIndex = i;
                break;
            }
        }

        if (hoveredSkillIndex != -1) {
            Holder<? extends ISkill<?>> hoveredSkill = skills.get(hoveredSkillIndex);
            int x = this.placement.x() + (SkillTreeLayout.SKILL_SIZE + SkillTreeLayout.SKILL_GAP) * hoveredSkillIndex;

            //draw blocked
            if (state == SkillSegmentState.LOCKED || state == SkillSegmentState.VISIBLE) {
                List<Component> text = new ArrayList<>();
                if (state == SkillSegmentState.VISIBLE) {
                    MutableComponent unlockText = Component.translatable("gui.factionapi.skills.unlock_parent_first").withStyle(ChatFormatting.DARK_RED);
                    text.add(unlockText);
                } else {
                    text.add(Component.translatable("gui.factionapi.skills.locked"));
                    this.lockingSkills.stream().map(a -> a.value().getName().copy().withStyle(ChatFormatting.DARK_RED)).forEach(text::add);
                }
                int width = text.stream().mapToInt(this.minecraft.font::width).max().getAsInt();
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, DESCRIPTION_SPRITE, scrollX + x - 3, scrollY + this.placement.y() - 3 - text.size() * 9, width + 8, 10 + text.size() * 10);
                int fontY = scrollY + this.placement.y() + 1 - text.size() * 9;
                for (int i = 0; i < text.size(); i++) {
                    graphics.text(this.minecraft.font, text.get(i), scrollX + x + 2, fontY + i * 9, -1, true);
                }
            }

            List<FormattedCharSequence> description = this.descriptions[hoveredSkillIndex];

            if (!this.lockingSkills.isEmpty()) {
                List<Component> text = new ArrayList<>();
                text.add(Component.translatable("gui.factionapi.skills.excluding"));
                this.lockingSkills.stream().map(a -> a.value().getName().copy().withStyle(ChatFormatting.YELLOW)).forEach(text::add);
                int width = Math.min(this.width[hoveredSkillIndex], text.stream().mapToInt(this.minecraft.font::width).max().getAsInt());

                int yOffset = description.isEmpty() ? 15 : 24;
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, DESCRIPTION_SPRITE, scrollX + x - 3, scrollY + this.placement.y() + 3 + 7 + description.size() * 9, width + 8, 10 + text.size() * 10 + yOffset);
                int fontY = scrollY + this.placement.y() + 3 + yOffset + 8 + description.size() * 9;
                for (int i = 0; i < text.size(); i++) {
                    graphics.text(this.minecraft.font, text.get(i), scrollX + x + 2, fontY + i * 9, -1, true);
                }
            }

            //draw description
            if (!description.isEmpty()) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, DESCRIPTION_SPRITE, scrollX + x - 5, scrollY + this.placement.y() + 3, this.width[hoveredSkillIndex], 30 + description.size() * 9);
                for (int i = 0; i < description.size(); i++) {
                    graphics.text(this.minecraft.font, description.get(i), scrollX + x + 2, scrollY + this.placement.y() + 3 + 24 + i * 9, -1, true);
                }
            }

            //draw title
            Identifier texture = state.sprite;
            if (state == SkillSegmentState.UNLOCKED && !this.skillHandler.isSkillEnabled(hoveredSkill)) {
                texture = SkillSegmentState.LOCKED.sprite;
            }
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, scrollX + x - 5, scrollY + this.placement.y() + 3, this.width[hoveredSkillIndex], 20);
            graphics.text(this.minecraft.font, this.titles[hoveredSkillIndex], scrollX + x + 40, scrollY + this.placement.y() + 9, -1, true);

            //draw skill point cost
            if (!this.entry.isRoot()) {
                int cost = hoveredSkill.value().getSkillPointCost();
                int costWidth = this.minecraft.font.width(String.valueOf(cost));
                int costHeight = this.minecraft.font.lineHeight;
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, DESCRIPTION_SPRITE, scrollX + x + 24, scrollY + this.placement.y() + ((SkillTreeLayout.SKILL_SIZE - costHeight) / 2) - 1, costWidth + 5, costHeight + 4);
                graphics.text(this.minecraft.font, Component.literal(String.valueOf(cost)), scrollX + x + 27, (int) (scrollY + this.placement.y() + ((SkillTreeLayout.SKILL_SIZE - costHeight) / 2f) + 1), -1, true);
            }

            //draw skill
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.entry.isRoot() ? START_SKILL_BACKGROUND_SPRITE : SKILL_BACKGROUND_SPRITE, scrollX + x, scrollY + this.placement.y(), SkillTreeLayout.SKILL_SIZE, SkillTreeLayout.SKILL_SIZE);
            GuiRenderer.blit(graphics, getSkillIconLocation(hoveredSkill.value()), x + scrollX + 5, this.placement.y() + scrollY + 5, 16, 16, 16, 16);

            if (heldSkill != null && heldSkill.equals(hoveredSkill)) {
                int fill = Mth.ceil(SkillTreeLayout.SKILL_SIZE * holdingProgress);
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.entry.isRoot() ? START_SKILL_RESET_BACKGROUND_SPRITE : SKILL_RESET_BACKGROUND_SPRITE, SkillTreeLayout.SKILL_SIZE, SkillTreeLayout.SKILL_SIZE, 0, 0, scrollX + x, scrollY + this.placement.y(), fill, SkillTreeLayout.SKILL_SIZE);
            }
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
        double width = this.placement.width();
        return mouseX >= this.placement.x() + scrollX - width / 2 && mouseX < this.placement.x() + scrollX + width / 2 - 1 && mouseY > scrollY + this.placement.y() && mouseY < scrollY + this.placement.y() + SkillTreeLayout.SKILL_SIZE;
    }

    public boolean isMouseOverSkill(int index, double mouseX, double mouseY, int scrollX, int scrollY) {
        int x = this.placement.x() + (SkillTreeLayout.SKILL_SIZE + SkillTreeLayout.SKILL_GAP) * index;
        return mouseX > x + scrollX && mouseX < x + scrollX + SkillTreeLayout.SKILL_SIZE && mouseY > scrollY + this.placement.y() && mouseY < scrollY + this.placement.y() + SkillTreeLayout.SKILL_SIZE;
    }

    @Nullable
    public Holder<? extends ISkill<?>> getSelectedSkill(double mouseX, double mouseY, int scrollX, int scrollY) {
        if (!isMouseOver(mouseX, mouseY, scrollX, scrollY)) return null;
        for (int i = 0; i < this.entry.skills().size(); i++) {
            if (isMouseOverSkill(i, mouseX, mouseY, (int) (scrollX - this.placement.width() / 2f), scrollY)) {
                return this.entry.skills().get(i);
            }
        }
        return null;
    }

    /**
     * from net.minecraft.client.gui.advancements.AdvancementEntryGui#findOptimalLines(ITextComponent, int)
     */
    private List<FormattedText> findOptimalLines(Component description, int maxWidth) {
        StringSplitter splitter = this.minecraft.font.getSplitter();
        List<FormattedText> best = Collections.emptyList();
        float bestDelta = Float.MAX_VALUE;

        for (int offset : TEST_SPLIT_OFFSETS) {
            List<FormattedText> lines = splitter.splitLines(description, maxWidth - offset, Style.EMPTY);
            float delta = Math.abs(getMaxWidth(splitter, lines) - (float) maxWidth);
            if (delta <= 10.0F) {
                return lines;
            }

            if (delta < bestDelta) {
                bestDelta = delta;
                best = lines;
            }
        }

        return best;
    }

    enum SkillSegmentState {
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
        public final @Nullable Identifier sprite;
        /**
         * connectivity inner color
         */
        public final int innerColor;
        /**
         * connectivity outer color
         */
        public final int outerColor;

        SkillSegmentState(@Nullable Identifier sprite, int innerColor, int outerColor) {
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
