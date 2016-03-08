package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraftforge.common.MinecraftForge;

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
        if (parent == null) {
            //Do not allow modify if this is the root node
            this.elements = elements;
        } else {
            SkillEvent.AddSkills event = new SkillEvent.AddSkills(faction, Arrays.asList(elements));
            MinecraftForge.EVENT_BUS.post(event);
            this.elements = event.getSkills().toArray(new ISkill[event.getSkills().size()]);
            if (elements.length == 0) {
                throw new IllegalArgumentException("Cannot remove all skills from a skill node");
            }
        }
        VampirismAPI.skillRegistry().registerNode(this);
    }

    /**
     * Creates a root node for the given faction
     * DO NOTE CREATE  BEFORE VAMPIRISM'S PRE-INIT
     *
     * @param faction
     * @param element
     */
    public SkillNode(IPlayableFaction faction, ISkill element) {
        this(faction, null, 0, element);

    }

    /**
     * Creates a child nodes with one or multiple xor skills.
     * DO NOTE CREATE  BEFORE VAMPIRISM'S PRE-INIT
     *
     * @param parent
     * @param elements
     */
    public SkillNode(SkillNode parent, ISkill... elements) {
        this(parent.getFaction(), parent, parent.depth + 1, elements);
        parent.children.add(this);
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
}
