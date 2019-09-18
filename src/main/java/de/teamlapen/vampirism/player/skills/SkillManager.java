package de.teamlapen.vampirism.player.skills;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillManager;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 1.12
 *
 * @author maxanier
 */
public class SkillManager implements ISkillManager {

    private final static Logger LOGGER = LogManager.getLogger(SkillManager.class);

    /**
     * Get the root skill of the faction (Registered with the same key as the faction itself.
     * If none ist found, prints a warning and returns a dummy one
     *
     * @param faction
     * @return
     */
    public @Nonnull
    ISkill getRootSkill(IPlayableFaction faction) {
        ISkill skill = ModRegistries.SKILLS.getValue(faction.getID());
        if (skill == null) {
            LOGGER.warn("No root skill exists for faction {}", faction.getID());
            throw new IllegalStateException("You need to register a root skill for your faction " + faction.getID());
        }
        return skill;
    }

    @Override
    public List<ISkill> getSkillsForFaction(IPlayableFaction faction) {
        List<ISkill> list = Lists.newArrayList(ModRegistries.SKILLS.getValues());
        list.removeIf(skill -> !faction.equals(skill.getFaction()));
        return list;
    }

    /**
     * For debug purpose only.
     * Prints the skills of the given faction to the given sender
     *
     * @param faction
     * @param sender
     */
    public void printSkills(IPlayableFaction faction, CommandSource sender) {
        for (ISkill s : getSkillsForFaction(faction)) {
            sender.sendFeedback(new StringTextComponent("ID: " + ModRegistries.SKILLS.getKey(s) + " Skill: ").appendSibling(new TranslationTextComponent(s.getTranslationKey())), true);
        }
    }

}
