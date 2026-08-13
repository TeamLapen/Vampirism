package de.teamlapen.faction.client.gui.screens.skills;

import de.teamlapen.faction.common.factions.skills.SkillTreeLayout;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.*;
import java.util.stream.Collectors;

public record SkillSegmentConnection(List<SkillSegmentComponent> parents, List<SkillSegmentComponent> children) {

    private static final int ROW_GAP = SkillTreeLayout.ROW_HEIGHT - SkillTreeLayout.SKILL_SIZE;
    private static final int UPPER_OFFSET = ROW_GAP - ROW_GAP / 3;
    private static final int LOWER_OFFSET = ROW_GAP / 3;

    public static List<SkillSegmentConnection> of(Collection<SkillSegmentComponent> components) {
        Map<Set<SkillSegmentComponent>, List<SkillSegmentComponent>> grouped = components.stream().filter(x -> !x.getParents().isEmpty()).collect(Collectors.groupingBy(x -> Set.copyOf(x.getParents()), LinkedHashMap::new, Collectors.toList()));
        return grouped.entrySet().stream().map(x -> new SkillSegmentConnection(List.copyOf(x.getKey()), x.getValue())).toList();
    }

    public void draw(GuiGraphicsExtractor graphics, boolean outerLine) {
        List<SkillSegmentComponent> visibleChildren = this.children.stream().filter(x -> x.getState() != SkillSegmentComponent.SkillSegmentState.HIDDEN).toList();
        if (visibleChildren.isEmpty()) return;

        int childY = visibleChildren.getFirst().getPlacement().y();
        int upperY = childY - UPPER_OFFSET;
        int lowerY = childY - LOWER_OFFSET;
        int trunkX = (int) Math.round(this.parents.stream().mapToInt(x -> x.getPlacement().x()).average().orElse(0));
        int sharedColor = sharedState(visibleChildren).pathColor(outerLine);

        drawStems(graphics, this.parents.stream().filter(x -> x.getState() != SkillSegmentComponent.SkillSegmentState.UNLOCKED).toList(), trunkX, upperY, SkillSegmentComponent.SkillSegmentState.VISIBLE.pathColor(outerLine), outerLine);
        drawStems(graphics, this.parents.stream().filter(x -> x.getState() == SkillSegmentComponent.SkillSegmentState.UNLOCKED).toList(), trunkX, upperY, sharedColor, outerLine);

        verticalLine(graphics, trunkX, upperY, lowerY, sharedColor, outerLine);

        for (SkillSegmentComponent child : visibleChildren) {
            int childX = child.getPlacement().x();
            int color = child.getState().pathColor(outerLine);
            if (childX != trunkX) {
                horizontalLine(graphics, Math.min(trunkX, childX), Math.max(trunkX, childX), lowerY, color, outerLine);
            }
            verticalLine(graphics, childX, lowerY, childY, color, outerLine);
        }
    }

    /**
     * Draws the stems of the given parents down to the junction bar. Only unlocked parents carry the shared color, so
     * the part of the bar leading to a parent that is not unlocked stays unlit.
     */
    private static void drawStems(GuiGraphicsExtractor graphics, List<SkillSegmentComponent> parents, int trunkX, int upperY, int color, boolean outerLine) {
        if (parents.isEmpty()) return;

        for (SkillSegmentComponent parent : parents) {
            verticalLine(graphics, parent.getPlacement().x(), parent.getPlacement().y() + SkillTreeLayout.SKILL_SIZE - 1, upperY, color, outerLine);
        }

        int barLeft = Math.min(trunkX, min(parents));
        int barRight = Math.max(trunkX, max(parents));
        if (barLeft != barRight) {
            horizontalLine(graphics, barLeft, barRight, upperY, color, outerLine);
        }
    }

    private SkillSegmentComponent.SkillSegmentState sharedState(List<SkillSegmentComponent> visibleChildren) {
        if (visibleChildren.stream().anyMatch(x -> x.getState() == SkillSegmentComponent.SkillSegmentState.UNLOCKED)) {
            return SkillSegmentComponent.SkillSegmentState.UNLOCKED;
        }
        return visibleChildren.getFirst().getState();
    }

    private static int min(List<SkillSegmentComponent> components) {
        return components.stream().mapToInt(x -> x.getPlacement().x()).min().orElse(0);
    }

    private static int max(List<SkillSegmentComponent> components) {
        return components.stream().mapToInt(x -> x.getPlacement().x()).max().orElse(0);
    }

    /**
     * This method covers both ends, {@code verticalLine} covers neither, so verticals are widened by one on each side.
     */
    private static void horizontalLine(GuiGraphicsExtractor graphics, int left, int right, int y, int color, boolean outerLine) {
        if (outerLine) {
            graphics.horizontalLine(left - 1, right + 1, y - 1, color);
            graphics.horizontalLine(left - 1, right + 1, y + 1, color);
        } else {
            graphics.horizontalLine(left, right, y, color);
        }
    }

    private static void verticalLine(GuiGraphicsExtractor graphics, int x, int top, int bottom, int color, boolean outerLine) {
        if (outerLine) {
            graphics.verticalLine(x - 1, top - 2, bottom + 2, color);
            graphics.verticalLine(x + 1, top - 2, bottom + 2, color);
        } else {
            graphics.verticalLine(x, top - 1, bottom + 1, color);
        }
    }
}
