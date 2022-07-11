package de.teamlapen.vampirism.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillManager;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 1.12
 *
 * @author maxanier
 */
public class SkillManager implements ISkillManager {

    private final static Logger LOGGER = LogManager.getLogger(SkillManager.class);

    /**
     * Get the root skill of the faction (Registered with the same key as the faction itself).
     * If none ist found, prints a warning and returns a dummy one
     */
    @Nonnull
    public <T extends IFactionPlayer<T>> ISkill<T> getRootSkill(IPlayableFaction<T> faction) {
        //noinspection unchecked
        ISkill<T> skill = (ISkill<T>) RegUtil.get(ModRegistries.SKILLS, faction.getID());
        if (skill == null) {
            LOGGER.warn("No root skill exists for faction {}", faction.getID());
            throw new IllegalStateException("You need to register a root skill for your faction " + faction.getID());
        }
        return skill;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IFactionPlayer<T>> List<ISkill<T>> getSkillsForFaction(IPlayableFaction<T> faction) {
        return RegUtil.values(ModRegistries.SKILLS).stream().filter(action -> action.getFaction() == faction).map(action -> (ISkill<T>)action).collect(Collectors.toList());
    }

    /**
     * For debug purpose only.
     * Prints the skills of the given faction to the given sender
     */
    public void printSkills(IPlayableFaction<?> faction, CommandSourceStack sender) {
        for (ISkill<?> s : getSkillsForFaction(faction)) {
            sender.sendSuccess(Component.literal("ID: " + RegUtil.id(s) + " Skill: ").append(s.getName()), true);
        }
    }

}
