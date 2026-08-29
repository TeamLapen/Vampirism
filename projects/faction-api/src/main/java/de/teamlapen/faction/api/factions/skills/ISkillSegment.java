package de.teamlapen.faction.api.factions.skills;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

import java.util.List;
import java.util.Optional;

/**
 * Represents a segment of a skill tree (see {@link ISkillTree}). A segment contains the tree it belongs to, a skill or
 * a choice between a few skills, its parents (the segments it descends from), the segments that lock it, and its
 * placement.
 */
public interface ISkillSegment {

    Holder<ISkillTree> tree();

    /**
     * Skills that belong to this segment. They are self-exclusive, make two separate segments if you want the player to
     * be able to choose multiple.
     */
    List<Holder<? extends ISkill<?>>> skills();

    /**
     * The segments this one descends from. A segment can be unlocked as soon as any of them is unlocked.
     */
    List<ResourceKey<ISkillSegment>> parents();

    /**
     * The segments that lock this one: once a skill in any of them unlocked, this segment can no longer be unlocked.
     * The relation is stored one way only, so a mutual exclusion has to be declared on both segments.
     */
    List<ResourceKey<ISkillSegment>> lockingSegments();

    /**
     * Positions the segment on either side of another segment of the same row. Empty leaves the position up to the
     * layout.
     */
    Optional<SegmentPlacement> placement();

    /**
     * A skill segment is a root of a tree if it has no parents, which means that it will be positioned on the top.
     * <p>
     * Root skills are granted for free once the tree is unlocked and are kept when the skills are reset. A skill tree
     * can have multiple roots.
     */
    default boolean isRoot() {
        return parents().isEmpty();
    }
}
