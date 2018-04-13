package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A node for the skill tree. Can contain multiple skills which only can be activated exclusively to each other.
 */
public class SkillNode {
    private final SkillNode parent;
    private final List<SkillNode> children;
    private final ISkill[] elements;
    private final int depth;
    private final IPlayableFaction faction;


    private SkillNode(IPlayableFaction faction, SkillNode parent, int depth, ISkill... elements) {
        this.parent = parent;
        this.faction = faction;
        this.depth = depth;
        this.children = new ArrayList<>();
        this.elements = elements;
    }

    /**
     * DO NOT USE. Register your factions root skill with the same reg name as the faction and it will be created automatically
     * Creates a root node for the given faction
     */
    @Deprecated
    public SkillNode(IPlayableFaction faction, ISkill element) {
        this(faction, null, 0, element);

    }

    /**
     * DO NOT USE. Use {@link ISkillManager#createSkillNode(SkillNode, ISkill...)} instead
     * Creates a child nodes with one or multiple xor skills.
     *
     * @param elements One or more xor skills
     */
    @Deprecated
    public SkillNode(SkillNode parent, ISkill... elements) {
        this(parent.getFaction(), parent, parent.depth + 1, elements);
        parent.children.add(this);
    }

    /**
     * @param skill
     * @return If the given skill is an element of this node
     */
    public boolean containsSkill(ISkill skill) {
        return ArrayUtils.contains(elements, skill);
    }

    public List<SkillNode> getChildren() {
        return children;
    }

    public int getDepth() {
        return depth;
    }

    public ISkill[] getElements() {
        return elements;
    }

    public IPlayableFaction getFaction() {
        return faction;
    }

    public SkillNode getParent() {
        return parent;
    }

    public boolean isRoot() {
        return parent == null;
    }

    @Override
    public String toString() {
        return "SkillNode{" +
                "faction=" + faction +
                ", depth=" + depth +
                ", elements=" + Arrays.toString(elements) +
                '}';
    }
}
