package de.teamlapen.faction.client.gui.screens.skills;

import de.teamlapen.faction.common.factions.skills.SkillTreeGraph;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Calculates and stores the layout of a skill tree. This is individual for each tree.
 */
public class SkillTreeLayout {

    public static final int SKILL_SIZE = 26;
    public static final int SKILL_GAP = 10;
    public static final int SEGMENT_GAP = 30;
    public static final int ROW_HEIGHT = 60;

    private static final int RELAX_PASSES = 3;
    private static final double CHAIN_WEIGHT = 4;
    private static final double PIN_WEIGHT = 1000;

    private final Map<SkillTreeGraph.Entry, Placement> placements;
    private final int minX;
    private final int maxX;
    private final int height;

    private SkillTreeLayout(Map<SkillTreeGraph.Entry, Placement> placements, int minX, int maxX, int height) {
        this.placements = placements;
        this.minX = minX;
        this.maxX = maxX;
        this.height = height;
    }

    public static SkillTreeLayout of(SkillTreeGraph.Tree tree) {
        Map<SkillTreeGraph.Entry, Placement> placements = new LinkedHashMap<>();
        SortedMap<Integer, List<SkillTreeGraph.Entry>> rows = tree.entries().stream().collect(Collectors.groupingBy(SkillTreeGraph.Entry::depth, TreeMap::new, Collectors.toList()));

        for (List<SkillTreeGraph.Entry> row : rows.values()) {
            row.sort(Comparator.comparingInt((SkillTreeGraph.Entry entry) -> primaryParentX(entry, placements)).thenComparingInt(entry -> entry.segment().value().priority()).thenComparing(entry -> entry.key().identifier()));

            int cursor = -rowWidth(row) / 2;

            for (SkillTreeGraph.Entry entry : row) {
                int width = segmentWidth(entry);
                placements.put(entry, new Placement(entry, cursor + width / 2, entry.depth() * ROW_HEIGHT, width));
                cursor += width + SEGMENT_GAP;
            }
        }

        List<List<SkillTreeGraph.Entry>> rowList = new ArrayList<>(rows.values());
        relax(rowList, placements, Map.of());

        Map<SkillTreeGraph.Entry, Integer> pinned = spreadFirstBranches(tree, placements);
        if (!pinned.isEmpty()) {
            pinned.forEach((entry, x) -> placements.put(entry, new Placement(entry, x, placements.get(entry).y(), placements.get(entry).width())));
            relax(rowList, placements, pinned);
        }

        int minX = placements.values().stream().mapToInt(placement -> placement.x() - placement.width() / 2).min().orElse(0);
        int maxX = placements.values().stream().mapToInt(placement -> placement.x() + placement.width() / 2).max().orElse(0);

        return new SkillTreeLayout(placements, minX, maxX, rows.isEmpty() ? 0 : rows.lastKey() * ROW_HEIGHT + SKILL_SIZE);
    }

    /**
     * The children of the first segment that branches in a tree get spread evenly to make the tree look more balanced.
     * The stem ends up being symmetric around it. The widest gap is reusing for each other gap, so other segments don't
     * overlay each other, though it might end up in unnaturally huge gaps if one branch is straight while the other one
     * splits into a few.
     */
    private static Map<SkillTreeGraph.Entry, Integer> spreadFirstBranches(SkillTreeGraph.Tree tree, Map<SkillTreeGraph.Entry, Placement> placements) {
        Map<SkillTreeGraph.Entry, Integer> pinned = new LinkedHashMap<>();

        for (SkillTreeGraph.Entry entry : tree.entries()) {
            if (entry.children().size() < 2 || !onStraightStem(entry)) continue;

            List<SkillTreeGraph.Entry> children = new ArrayList<>(entry.children());
            children.sort(Comparator.comparingInt(x -> placements.get(x).x()));

            int step = 0;
            for (int i = 1; i < children.size(); i++) {
                step = Math.max(step, placements.get(children.get(i)).x() - placements.get(children.get(i - 1)).x());
            }

            double first = placements.get(entry).x() - step * (children.size() - 1) / 2d;
            for (int i = 0; i < children.size(); i++) {
                pinned.put(children.get(i), (int) Math.round(first + (long) step * i));
            }
        }
        return pinned;
    }

    private static boolean onStraightStem(SkillTreeGraph.Entry entry) {
        SkillTreeGraph.Entry current = entry;
        while (!current.parents().isEmpty()) {
            if (current.parents().size() != 1 || current.parents().getFirst().children().size() != 1) {
                return false;
            }
            current = current.parents().getFirst();
        }
        return true;
    }

    private static void relax(List<List<SkillTreeGraph.Entry>> rows, Map<SkillTreeGraph.Entry, Placement> placements, Map<SkillTreeGraph.Entry, Integer> pinned) {
        for (int pass = 0; pass < RELAX_PASSES; pass++) {
            for (List<SkillTreeGraph.Entry> row : rows) {
                align(row, placements, SkillTreeGraph.Entry::parents, pinned);
            }
            for (int i = rows.size() - 1; i >= 0; i--) {
                align(rows.get(i), placements, SkillTreeGraph.Entry::children, pinned);
            }
        }
    }

    private static void align(List<SkillTreeGraph.Entry> row, Map<SkillTreeGraph.Entry, Placement> placements, Function<SkillTreeGraph.Entry, List<SkillTreeGraph.Entry>> neighbours, Map<SkillTreeGraph.Entry, Integer> pinned) {
        if (row.isEmpty()) return;

        Map<SkillTreeGraph.Entry, Integer> desired = new HashMap<>();
        Map<SkillTreeGraph.Entry, Double> weights = new HashMap<>();

        for (SkillTreeGraph.Entry entry : row) {
            if (pinned.containsKey(entry)) {
                desired.put(entry, pinned.get(entry));
                weights.put(entry, PIN_WEIGHT);
                continue;
            }
            List<SkillTreeGraph.Entry> related = neighbours.apply(entry);
            OptionalDouble average = related.stream().map(placements::get).filter(Objects::nonNull).mapToInt(Placement::x).average();
            desired.put(entry, average.isPresent() ? (int) Math.round(average.getAsDouble()) : placements.get(entry).x());
            weights.put(entry, related.size() == 1 ? CHAIN_WEIGHT : 1d);
        }

        row.sort(Comparator.comparingInt(desired::get));

        int[] offsets = new int[row.size()];
        for (int i = 1; i < row.size(); i++) {
            offsets[i] = offsets[i - 1] + spacing(row.get(i - 1), row.get(i));
        }

        List<Target> targets = new ArrayList<>(row.size());
        for (int i = 0; i < row.size(); i++) {
            targets.add(new Target(desired.get(row.get(i)) - offsets[i], weights.get(row.get(i))));
        }

        double[] resolved = isotonic(targets);

        for (int i = 0; i < row.size(); i++) {
            SkillTreeGraph.Entry entry = row.get(i);
            Placement placement = placements.get(entry);
            placements.put(entry, new Placement(entry, (int) Math.round(resolved[i]) + offsets[i], placement.y(), placement.width()));
        }
    }

    private static double[] isotonic(List<Target> targets) {
        List<List<Target>> blocks = new ArrayList<>();
        List<Double> medians = new ArrayList<>();

        for (Target target : targets) {
            List<Target> block = new ArrayList<>();
            block.add(target);
            double median = target.value();

            while (!medians.isEmpty() && medians.getLast() > median) {
                block.addAll(blocks.removeLast());
                medians.removeLast();
                median = median(block);
            }

            blocks.add(block);
            medians.add(median);
        }

        double[] resolved = new double[targets.size()];
        int index = 0;
        for (int i = 0; i < blocks.size(); i++) {
            for (int j = 0; j < blocks.get(i).size(); j++) {
                resolved[index++] = medians.get(i);
            }
        }
        return resolved;
    }

    private static double median(List<Target> values) {
        List<Target> sorted = new ArrayList<>(values);
        sorted.sort(Comparator.comparingDouble(Target::value));
        double total = sorted.stream().mapToDouble(Target::weight).sum();
        double accumulated = 0;

        for (int i = 0; i < sorted.size(); i++) {
            accumulated += sorted.get(i).weight();
            if (accumulated * 2 > total) {
                return sorted.get(i).value();
            }
            if (accumulated * 2 == total && i + 1 < sorted.size()) {
                return (sorted.get(i).value() + sorted.get(i + 1).value()) / 2;
            }
        }
        return sorted.getLast().value();
    }

    private static int primaryParentX(SkillTreeGraph.Entry entry, Map<SkillTreeGraph.Entry, Placement> placements) {
        return entry.parents().stream().min(Comparator.comparingInt(parent -> parent.segment().value().priority())).map(placements::get).map(Placement::x).orElse(0);
    }

    private static int spacing(SkillTreeGraph.Entry left, SkillTreeGraph.Entry right) {
        return segmentWidth(left) / 2 + segmentWidth(right) / 2 + SEGMENT_GAP;
    }

    private static int segmentWidth(SkillTreeGraph.Entry entry) {
        int count = entry.skills().size();
        return count * SKILL_SIZE + (count - 1) * SKILL_GAP;
    }

    private static int rowWidth(List<SkillTreeGraph.Entry> row) {
        return row.stream().mapToInt(SkillTreeLayout::segmentWidth).sum() + (row.size() - 1) * SEGMENT_GAP;
    }

    public Collection<Placement> placements() {
        return this.placements.values();
    }

    @Nullable
    public Placement placement(SkillTreeGraph.Entry entry) {
        return this.placements.get(entry);
    }

    public int minX() {
        return this.minX;
    }

    public int maxX() {
        return this.maxX;
    }

    public int width() {
        return this.maxX - this.minX;
    }

    public int height() {
        return this.height;
    }

    private record Target(double value, double weight) {}

    public record Placement(SkillTreeGraph.Entry entry, int x, int y, int width) {}
}
