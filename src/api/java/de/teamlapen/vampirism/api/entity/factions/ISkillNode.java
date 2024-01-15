package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A node for the skill tree.<br>
 * Can contain multiple skills.<br>
 */
public interface ISkillNode {

    /**
     * @return The skills contained in this node
     */
    @NotNull
    List<Holder<ISkill<?>>> skills();

    /**
     * Nodes that are mutually exclusive to this node. Each node must define this.
     */
    @NotNull
    List<ResourceKey<ISkillNode>> lockingNodes();

    /**
     * checks if a skill is contained in this node
     */
    boolean containsSkill(ISkill<?> skill);
}
