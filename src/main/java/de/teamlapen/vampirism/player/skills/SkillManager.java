package de.teamlapen.vampirism.player.skills;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillManager;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillType;
import de.teamlapen.vampirism.api.entity.player.skills.SkillType;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.command.CommandSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 1.12
 *
 * @author maxanier
 */
public class SkillManager implements ISkillManager {

    private final static Logger LOGGER = LogManager.getLogger();
    private final Map<ResourceLocation,ISkillType> skillTypes = new HashMap<>();

    @Deprecated
    @Nonnull ISkill getRootSkill(IPlayableFaction faction) {
        return getRootSkill(faction, SkillType.LEVEL);
    }
    /**
     * Get the root skill of the faction (Registered with the same key as the faction itself.
     * If none ist found, prints a warning and returns a dummy one
     *
     * @throws java.lang.ClassCastException if the faction id is not for a {@link de.teamlapen.vampirism.api.entity.factions.IPlayableFaction}
     */
    @Deprecated // for removal
    public @Nonnull
    ISkill getRootSkill(ResourceLocation factionId, ISkillType type) {
        return getRootSkill(((IPlayableFaction) VampirismAPI.factionRegistry().getFactionByID(factionId)), type);
    }

    /**
     * Gets the root skill of the faction for the given skill type. The skill must be registered with an id that matches {@link de.teamlapen.vampirism.api.entity.player.skills.ISkillType#createIdForFaction(net.minecraft.util.ResourceLocation)}
     *
     * @param faction the faction for which the skill should be returned
     * @param type the type of skill that is searched for
     * @return th root skill for the parameters
     * @throws java.lang.IllegalStateException when the faction can not have a skill for the given skill type or when there is no skill registered conforming to the skill type's naming scheme
     */
    @Nonnull
    public ISkill getRootSkill(IPlayableFaction faction, ISkillType type) {
        if (!type.isForFaction(faction)) throw new IllegalStateException("The skilltype " + type + " is not applicable for the faction " + faction.getID());
        ISkill skill = ModRegistries.SKILLS.getValue(type.createIdForFaction(faction.getID()));
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

    @Override
    public Collection<ISkillType> getSkillTypes() {
        return this.skillTypes.values();
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
            sender.sendSuccess(new StringTextComponent("ID: " + ModRegistries.SKILLS.getKey(s) + " Skill: ").append(s.getName()), true);
        }
    }

    @Nullable
    @Override
    public ISkillType getSkillType(@Nonnull ResourceLocation id) {
        return this.skillTypes.get(id);
    }

    @Nonnull
    @Override
    public ISkillType registerSkillType(@Nonnull ISkillType type) {
        if (this.skillTypes.containsKey(type.getRegistryName())) {
            throw new IllegalStateException("A skill type with the id " + type.getRegistryName() + " has already been registered");
        }
        this.skillTypes.put(type.getRegistryName(), type);
        return type;
    }
}
