package de.teamlapen.faction.common.factions.skills;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillSegment;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.faction.api.factions.skills.SegmentPlacement;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Organizes individual skill segments into skill trees. A tree is a directed acyclic graph rather than a strict tree,
 * as a segment can have several parents. The build runs once per registry set, cached in {@link SkillTreeGraphs}.
 */
public class SkillTreeGraph {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Map<ResourceKey<ISkillSegment>, Entry> entriesByKey;
    private final Map<Holder<ISkillTree>, Tree> treesByKey;

    public SkillTreeGraph(Map<ResourceKey<ISkillSegment>, Entry> entriesByKey, Map<Holder<ISkillTree>, Tree> treesByKey) {
        this.entriesByKey = entriesByKey;
        this.treesByKey = treesByKey;
    }

    public static SkillTreeGraph build(HolderLookup.RegistryLookup<ISkillSegment> lookup) {
        Map<ResourceKey<ISkillSegment>, Entry> entriesByKey = new LinkedHashMap<>();

        for (Holder.Reference<ISkillSegment> reference : lookup.listElements().toList()) {
            ResourceKey<ISkillSegment> key = reference.getKey();
            if (key == null) continue;
            entriesByKey.put(key, new Entry(key, reference));
        }

        List<Entry> entries = prune(entriesByKey, linkEntries(entriesByKey));
        Map<Holder<ISkillTree>, List<Entry>> grouped = sortByDepth(entries).stream().collect(Collectors.groupingBy(entry -> entry.segment().value().tree(), LinkedHashMap::new, Collectors.toList()));

        Map<Holder<ISkillTree>, Tree> treesByKey = new LinkedHashMap<>();
        grouped.forEach((tree, treeEntries) -> {
            checkSkillConflicts(treeEntries);
            treesByKey.put(tree, new Tree(tree, treeEntries.stream().filter(entry -> entry.parents().isEmpty()).toList(), List.copyOf(treeEntries), treeEntries.getLast().depth()));
        });

        return new SkillTreeGraph(Map.copyOf(entriesByKey), Map.copyOf(treesByKey));
    }

    /**
     * Links parents and children. Returns the segments that declare parents that cannot be resolved properly.
     */
    private static Set<Entry> linkEntries(Map<ResourceKey<ISkillSegment>, Entry> entriesByKey) {
        Set<Entry> unresolvable = new HashSet<>();

        for (Entry entry : entriesByKey.values()) {
            List<ResourceKey<ISkillSegment>> declared = entry.segment().value().parents();
            List<Entry> parents = new ArrayList<>(declared.size());

            for (ResourceKey<ISkillSegment> key : declared) {
                Entry parent = entriesByKey.get(key);
                if (parent == null) {
                    LOGGER.error("Skill segment {} declares unknown parent {}", entry, key.identifier());
                } else if (parent == entry) {
                    LOGGER.error("Skill segment {} declares itself as a parent", entry);
                } else if (!parent.segment().value().tree().equals(entry.segment().value().tree())) {
                    LOGGER.error("Skill segment {} declares parent {} from a different skill tree", entry, parent);
                } else if (parents.contains(parent)) {
                    LOGGER.warn("Skill segment {} declares parent {} more than once", entry, parent);
                } else {
                    parents.add(parent);
                }
            }

            if (parents.isEmpty() && !declared.isEmpty()) {
                unresolvable.add(entry);
            }
            entry.setParents(parents);
            entry.setChildren(new ArrayList<>());
        }

        for (Entry entry : entriesByKey.values()) {
            entry.parents().forEach(parent -> parent.children().add(entry));
        }

        return unresolvable;
    }

    /**
     * Removes segments whose declared parents could not be resolved, and everything below them. Keeping children with no
     * parents would turn them into roots, so it's better to just drop them out and send an error into log.
     */
    private static List<Entry> prune(Map<ResourceKey<ISkillSegment>, Entry> entriesByKey, Set<Entry> unresolvable) {
        Deque<Entry> queue = new ArrayDeque<>(unresolvable);
        Set<Entry> removed = new HashSet<>(unresolvable);

        while (!queue.isEmpty()) {
            for (Entry child : queue.poll().children()) {
                if (removed.add(child)) {
                    queue.add(child);
                }
            }
        }

        if (!removed.isEmpty()) {
            LOGGER.error("Ignoring skill segments that have no reachable parent: {}", removed.stream().map(Entry::toString).collect(Collectors.joining(", ")));
            removed.forEach(entry -> entriesByKey.remove(entry.key()));
            for (Entry entry : entriesByKey.values()) {
                entry.parents().removeAll(removed);
                entry.children().removeAll(removed);
            }
        }

        return List.copyOf(entriesByKey.values());
    }

    private static void checkSkillConflicts(List<Entry> entries) {
        Map<Holder<? extends ISkill<?>>, Entry> owners = new HashMap<>();

        for (Entry entry : entries) {
            for (Holder<? extends ISkill<?>> skill : entry.skills()) {
                Entry previous = owners.put(skill, entry);
                if (previous != null) {
                    LOGGER.error("Skill {} is contained in both {} and {} of the same skill tree", skill.unwrapKey().map(key -> key.identifier().toString()).orElse("unknown"), previous, entry);
                }
            }
        }
    }

    /**
     * Assigns each segment the length of the longest path from a root, so it always ends up below all of its parents,
     * then orders by depth and key, before the placements of each row are applied. Segments forming a cycle are
     * specifically dropped out.
     */
    private static List<Entry> sortByDepth(Collection<Entry> entries) {
        Map<Entry, Integer> remainingParents = new HashMap<>();
        Deque<Entry> queue = new ArrayDeque<>();
        List<Entry> sorted = new ArrayList<>(entries.size());

        for (Entry entry : entries) {
            remainingParents.put(entry, entry.parents().size());
            if (entry.parents().isEmpty()) {
                queue.add(entry);
            }
        }

        while (!queue.isEmpty()) {
            Entry entry = queue.poll();
            sorted.add(entry);

            for (Entry child : entry.children()) {
                child.setDepth(Math.max(child.depth(), entry.depth() + 1));
                if (remainingParents.merge(child, -1, Integer::sum) == 0) {
                    queue.add(child);
                }
            }
        }

        if (sorted.size() != entries.size()) {
            Set<Entry> resolved = new HashSet<>(sorted);
            LOGGER.error("Skill segments form a cycle and are ignored: {}", entries.stream().filter(entry -> !resolved.contains(entry)).map(entry -> entry.key().identifier().toString()).collect(Collectors.joining(", ")));
        }

        sorted.sort(Comparator.comparingInt(Entry::depth).thenComparing(entry -> entry.key().identifier()));

        return sorted.stream().collect(Collectors.groupingBy(Entry::depth, TreeMap::new, Collectors.toList())).values().stream().map(SkillTreeGraph::orderByPlacement).flatMap(List::stream).toList();
    }

    public static List<Entry> orderByPlacement(List<Entry> row) {
        if (row.size() < 2) {
            return row;
        }

        Map<ResourceKey<ISkillSegment>, Entry> byKey = row.stream().collect(Collectors.toMap(Entry::key, Function.identity()));
        Map<Entry, Integer> indices = new HashMap<>();
        Map<Entry, List<Entry>> successors = new HashMap<>();
        Map<Entry, Integer> predecessors = new HashMap<>();

        for (Entry entry : row) {
            indices.put(entry, indices.size());
            predecessors.put(entry, 0);
        }

        for (Entry entry : row) {
            SegmentPlacement placement = entry.segment().value().placement().orElse(null);
            if (placement == null) {
                continue;
            }
            Entry relative = byKey.get(placement.segment());
            if (relative == null || relative == entry) {
                LOGGER.warn("Skill segment {} is placed {} a segment that is not part of its row", entry.key().identifier(), placement.type().getSerializedName());
                continue;
            }
            Entry first = switch (placement.type()) {
                case BEFORE -> entry;
                case AFTER -> relative;
            };
            Entry second = first == entry ? relative : entry;
            successors.computeIfAbsent(first, _ -> new ArrayList<>()).add(second);
            predecessors.merge(second, 1, Integer::sum);
        }

        if (successors.isEmpty()) {
            return row;
        }

        PriorityQueue<Entry> queue = new PriorityQueue<>(Comparator.comparingInt(indices::get));
        predecessors.forEach((entry, count) -> {
            if (count == 0) {
                queue.add(entry);
            }
        });

        List<Entry> ordered = new ArrayList<>(row.size());
        while (!queue.isEmpty()) {
            Entry entry = queue.poll();
            ordered.add(entry);
            for (Entry successor : successors.getOrDefault(entry, List.of())) {
                if (predecessors.merge(successor, -1, Integer::sum) == 0) {
                    queue.add(successor);
                }
            }
        }

        if (ordered.size() != row.size()) {
            LOGGER.error("Skill segment placements form a cycle and are ignored: {}", row.stream().filter(entry -> !ordered.contains(entry)).map(entry -> entry.key().identifier().toString()).collect(Collectors.joining(", ")));
            return row;
        }

        return ordered;
    }

    public Collection<Tree> trees() {
        return treesByKey.values();
    }

    public Optional<Tree> tree(Holder<ISkillTree> tree) {
        return Optional.ofNullable(treesByKey.get(tree));
    }

    public Optional<Entry> entry(ResourceKey<ISkillSegment> key) {
        return Optional.ofNullable(entriesByKey.get(key));
    }

    public Optional<Entry> entryForSkill(Collection<Holder<ISkillTree>> trees, Holder<? extends ISkill<?>> skill) {
        for (Holder<ISkillTree> tree : trees) {
            Optional<Entry> entry = entryForSkill(tree, skill);
            if (entry.isPresent()) {
                return entry;
            }
        }

        return Optional.empty();
    }

    public Optional<Entry> entryForSkill(Holder<ISkillTree> tree, Holder<? extends ISkill<?>> skill) {
        Optional<Tree> optTree = tree(tree);
        if (optTree.isPresent()) {
            List<Entry> entriesContaining = optTree.get().entries().stream().filter(e -> e.segment().value().skills().contains(skill)).toList();
            if (!entriesContaining.isEmpty()) {
                return Optional.of(entriesContaining.getFirst());
            }
        }

        return Optional.empty();
    }

    public List<Holder<? extends ISkill<?>>> forgetCascade(Holder<ISkillTree> treeHolder, Holder<? extends ISkill<?>> skill, Predicate<Holder<? extends ISkill<?>>> isEnabled) {
        Optional<Tree> optTree = tree(treeHolder);
        if (optTree.isEmpty() || !isEnabled.test(skill)) {
            return List.of();
        }

        Tree tree = optTree.get();
        Optional<Entry> optTarget = tree.entryForSkill(skill);
        if (optTarget.isEmpty() || optTarget.get().isRoot()) {
            return List.of();
        }
        Entry target = optTarget.get();

        Predicate<Entry> before = entry -> entry.skills().stream().anyMatch(isEnabled);
        Predicate<Entry> after = entry -> entry.skills().stream().anyMatch(s -> !s.equals(skill) && isEnabled.test(s));

        Set<Entry> orphaned = new HashSet<>(tree.orphanedBy(before, after));

        List<Entry> ordered = new ArrayList<>(orphaned);
        if (!orphaned.contains(target)) {
            ordered.add(target);
        }
        ordered.sort(Comparator.comparingInt(Entry::depth).reversed());

        List<Holder<? extends ISkill<?>>> cascade = new ArrayList<>();
        for (Entry entry : ordered) {
            boolean isOrphan = orphaned.contains(entry);
            for (Holder<? extends ISkill<?>> candidate : entry.skills()) {
                if (candidate.equals(skill) || isOrphan && isEnabled.test(candidate)) {
                    cascade.add(candidate);
                }
            }
        }

        return cascade;
    }

    public List<Entry> lockingSegments(Entry entry) {
        List<ResourceKey<ISkillSegment>> locked = entry.segment().value().lockingSegments();
        return entriesByKey.values().stream().filter(e -> locked.contains(e.key)).toList();
    }

    public static final class Entry {

        private final ResourceKey<ISkillSegment> key;
        private final Holder<ISkillSegment> segment;
        private List<Entry> parents;
        private List<Entry> children;
        private int depth;

        public Entry(ResourceKey<ISkillSegment> key, Holder<ISkillSegment> segment, List<Entry> parents, List<Entry> children, int depth) {
            this.key = key;
            this.segment = segment;
            this.parents = parents;
            this.children = children;
            this.depth = depth;
        }

        public Entry(ResourceKey<ISkillSegment> key, Holder<ISkillSegment> segment) {
            this(key, segment, List.of(), List.of(), 0);
        }

        public boolean isRoot() {
            return segment.value().isRoot();
        }

        public List<Holder<? extends ISkill<?>>> skills() {
            return segment.value().skills();
        }

        public ResourceKey<ISkillSegment> key() {
            return key;
        }

        public Holder<ISkillSegment> segment() {
            return segment;
        }

        public void setParents(List<Entry> parents) {
            this.parents = parents;
        }

        public List<Entry> parents() {
            return parents;
        }

        public void setChildren(List<Entry> children) {
            this.children = children;
        }

        public List<Entry> children() {
            return children;
        }

        public void setDepth(int depth) {
            this.depth = depth;
        }

        public int depth() {
            return depth;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            return obj instanceof Entry that && this.key.equals(that.key);
        }

        @Override
        public int hashCode() {
            return this.key.hashCode();
        }

        @Override
        public String toString() {
            return this.key.identifier().toString();
        }
    }

    public record Tree(Holder<ISkillTree> tree, List<Entry> roots, List<Entry> entries, int maxDepth) {

        public Optional<Entry> entryForSkill(Holder<? extends ISkill<?>> skill) {
            return entries.stream().filter(entry -> entry.skills().contains(skill)).findFirst();
        }

        public List<Entry> orphanedBy(Predicate<Entry> enabledBefore, Predicate<Entry> enabledAfter) {
            Set<Entry> reachable = new HashSet<>();
            Deque<Entry> queue = new ArrayDeque<>();
            roots.stream().filter(enabledAfter).forEach(queue::add);

            while (!queue.isEmpty()) {
                Entry entry = queue.poll();
                if (!reachable.add(entry)) {
                    continue;
                }
                entry.children().stream().filter(enabledAfter).filter(child -> !reachable.contains(child)).forEach(queue::add);
            }

            return entries.stream().filter(enabledBefore).filter(entry -> !reachable.contains(entry)).toList();
        }

        /**
         * Finds any enabled segment that has no enabled children.
         */
        public Optional<Entry> anyLastSegment(Predicate<Entry> isEnabled) {
            if (roots.stream().noneMatch(isEnabled)) {
                return Optional.empty();
            }

            for (Entry entry : entries) {
                if (isEnabled.test(entry) && entry.children().stream().noneMatch(isEnabled) && !entry.isRoot()) {
                    return Optional.of(entry);
                }
            }

            return Optional.empty();
        }
    }
}
