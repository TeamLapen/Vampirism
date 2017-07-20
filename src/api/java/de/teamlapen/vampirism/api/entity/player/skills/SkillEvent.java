package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;

/**
 * Skill related events.
 * All fired via {@link MinecraftForge#EVENT_BUS}
 */
public class SkillEvent extends Event {
    public final IPlayableFaction faction;

    private SkillEvent(IPlayableFaction faction) {
        this.faction = faction;
    }

    /**
     * This event is fired when a new skill node is registered. A skill node contains one or multiple skills only one of which can be activated at the same time.
     * You can replace, add or remove skills, but at least one skill has to be in the list at the end.
     * Is not thrown for the root skill node.
     */
    public static class AddSkills extends SkillEvent {
        private final List<ISkill> skills;

        public AddSkills(IPlayableFaction faction, List<ISkill> skills) {
            super(faction);
            this.skills = skills;
        }

        public List<ISkill> getSkills() {
            return skills;
        }

    }

    /**
     * Fired when a new skill node was created.
     * Allows to add child nodes directly.
     */
    public static class CreatedNode extends SkillEvent {

        private final SkillNode node;

        public CreatedNode(IPlayableFaction faction, SkillNode node) {
            super(faction);
            this.node = node;
        }

        /**
         * Allows you to directly register a child node.
         * If you have access to the node outside of this event consider using {@link ISkillManager#createSkillNode(SkillNode, ISkill...)} to prevent long (stacktrace) chains.
         *
         * @param skills One or more xor skills
         */
        public void createChild(ISkill... skills) {
            VampirismAPI.skillManager().createSkillNode(node, skills);
        }

        public SkillNode getNode() {
            return node;
        }
    }
}
