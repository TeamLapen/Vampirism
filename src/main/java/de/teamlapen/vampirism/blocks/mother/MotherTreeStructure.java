package de.teamlapen.vampirism.blocks.mother;

import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Utility class to build and temporarily store the physical structure of the mother roots/tree
 */
public class MotherTreeStructure {
    /**
     * @param start Position of the mother block
     * @return A newly created representation of the structure in the world
     */
    public static MotherTreeStructure getTreeView(Level level, BlockPos start) {
        Builder builder = new Builder();
        BlockState mother = level.getBlockState(start);
        assert mother.getBlock() == ModBlocks.MOTHER.get();
        builder.addBlock(start, (IRemainsBlock) mother.getBlock(), mother, 0);
        Queue<Pair<Integer, BlockPos>> queue = new LinkedList<>();
        queue.add(Pair.of(0, start));

        while (!queue.isEmpty()) {
            Pair<Integer, BlockPos> p = queue.poll();
            nextTreeStep(level, p.getRight(), p.getLeft() + 1, queue, builder);
        }
        return builder.build();
    }

    /**
     * Analyzes the adjacent blocks to the current level and stores and queues the respective blocks
     *
     * @param currentPos The current, already processed, position
     * @param nextDepth  The current depth + 1
     * @param queue      Queue to add next block positions to, if any
     * @param builder    Structure builder to add processed information to
     */
    private static void nextTreeStep(Level level, BlockPos currentPos, int nextDepth, Queue<Pair<Integer, BlockPos>> queue, Builder builder) {
        Direction.stream().forEach(dir -> {
            BlockPos next = currentPos.relative(dir);
            if (!builder.allBlocks.contains(next)) {
                BlockState state = level.getBlockState(next);
                if (state.getBlock() instanceof IRemainsBlock b) {
                    builder.addBlock(next, b, state, nextDepth);
                    queue.add(Pair.of(nextDepth, next));
                }
            }
        });

    }

    public static Optional<Pair<BlockPos, BlockState>> findMother(LevelAccessor level, BlockPos pos) {
        return innerFindMother(level, pos, new HashSet<>(), blockState -> blockState.getBlock() == ModBlocks.MOTHER.get());
    }

    private static Optional<Pair<BlockPos, BlockState>> innerFindMother(LevelAccessor level, BlockPos pos, Set<BlockPos> visited, Predicate<BlockState> predicate) {
        if (visited.contains(pos)) return Optional.empty();
        visited.add(pos);
        BlockState blockState = level.getBlockState(pos);
        if (!(blockState.getBlock() instanceof IRemainsBlock)) return Optional.empty();
        if (predicate.test(blockState)) {
            return Optional.of(Pair.of(pos, blockState));
        } else {
            return Direction.stream().map(pos::relative).map(newPos -> innerFindMother(level, newPos, visited, predicate)).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
        }
    }

    /**
     * Set of all blocks belonging to the structure (at initial scan, may contain invalid blocks later)
     */
    private final Set<BlockPos> allBlocks;
    /**
     * Set of all vulnerabilities belonging to the structure (at initial scan, may contain invalid blocks later)
     */
    private final Set<BlockPos> vulnerabilities;
    /**
     * All blocks sorted by depth (distance from mother core). Only valid up to {@link MotherTreeStructure#validHierarchy}  (and potentially invalid due to external changes)
     */
    private final Set<BlockPos>[] hierarchy;
    private int validHierarchy;

    public MotherTreeStructure(Set<BlockPos> allBlocks, Set<BlockPos> vulnerabilities, Set<BlockPos>[] hierarchy) {
        this.allBlocks = allBlocks;
        this.vulnerabilities = vulnerabilities;
        this.hierarchy = hierarchy;
        this.validHierarchy = hierarchy.length - 1;
    }

    /**
     * Set of all blocks belonging to the structure.
     * May contain now invalid positions
     */
    public Set<BlockPos> getCachedBlocks() {
        return allBlocks;
    }

    /**
     * Set of all vulnerabilities belonging to the structure.
     * May contain now invalid positions
     */
    public Set<BlockPos> getCachedVulnerabilities() {
        return vulnerabilities;
    }


    /**
     * @return Stream of details about all verified (they still exist in the level) vulnerabilities of the structure
     */
    public Stream<Triple<BlockPos, BlockState, IRemainsBlock>> getVerifiedVulnerabilities(Level level) {
        return getCachedVulnerabilities().stream().map(pos -> {
                    BlockState bs = level.getBlockState(pos);
                    if (bs.getBlock() instanceof IRemainsBlock b) {
                        if (b.isVulnerability(bs)) {
                            return Triple.of(pos, bs, b);
                        }
                    }
                    return null;
                }
        ).filter(Objects::nonNull);
    }


    /**
     * Removes highest depth blocks from hierarchy
     *
     * @return Set of all blocks of highest depth
     */
    public Optional<Set<BlockPos>> popHierarchy() {
        if (validHierarchy > 0) {
            Set<BlockPos> set = hierarchy[validHierarchy];
            allBlocks.removeAll(set); //Probably not needed
            vulnerabilities.removeAll(set); //Probably not needed
            validHierarchy--;
            return Optional.of(set);
        }
        return Optional.empty();
    }

    private static class Builder {
        public final Set<BlockPos> allBlocks = new HashSet<>();

        private final Set<BlockPos> vulnerabilities = new HashSet<>();

        private final ArrayList<Set<BlockPos>> hierarchy = new ArrayList<>();

        public void addBlock(BlockPos pos, IRemainsBlock block, BlockState state, int depth) {
            allBlocks.add(pos);
            if (block.isVulnerability(state)) {
                vulnerabilities.add(pos);
            }
            if (depth < hierarchy.size()) {
                hierarchy.get(depth).add(pos);

            } else if (depth == hierarchy.size()) {
                Set<BlockPos> p = new HashSet<>();
                p.add(pos);
                hierarchy.add(p);
            } else {
                throw new IllegalStateException("Max fucked up");
            }
        }

        public MotherTreeStructure build() {
            return new MotherTreeStructure(allBlocks, vulnerabilities, hierarchy.toArray(new Set[0]));
        }


    }
}
