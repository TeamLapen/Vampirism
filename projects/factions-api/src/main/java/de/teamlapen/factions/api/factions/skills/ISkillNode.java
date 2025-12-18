package de.teamlapen.factions.api.factions.skills;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

import java.util.List;

/**
 * A node for the skill tree.<br>
 * Can contain multiple skills.<br>
 */
public interface ISkillNode {

    /**
     * @return The skills contained in this node
     */
    List<Holder<ISkill<?>>> skills();

    /**
     * Nodes that are mutually exclusive to this node. Each node must define this.
     */
    List<ResourceKey<ISkillNode>> lockingNodes();

    /**
     * checks if a skill is contained in this node
     */
    boolean containsSkill(Holder<ISkill<?>> skill);

}
