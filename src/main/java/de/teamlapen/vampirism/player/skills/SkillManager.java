package de.teamlapen.vampirism.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillManager;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillType;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 1.12
 *
 * @author maxanier
 */
public class SkillManager implements ISkillManager {

    private final static Logger LOGGER = LogManager.getLogger(SkillManager.class);
    private final Map<ResourceLocation,ISkillType> skillTypes = new HashMap<>();

    /**
     * Gets the root skill of the faction for the given skill type. The skill must be registered with an id that matches {@link de.teamlapen.vampirism.api.entity.player.skills.ISkillType#createIdForFaction(net.minecraft.util.ResourceLocation)}
     *
     * @param faction the faction for which the skill should be returned
     * @param type the type of skill that is searched for
     * @return th root skill for the parameters
     * @throws java.lang.IllegalStateException when the faction can not have a skill for the given skill type or when there is no skill registered conforming to the skill type's naming scheme
     */
    @NotNull
    public <T extends IFactionPlayer<T>> ISkill<T> getRootSkill(IPlayableFaction<T> faction, ISkillType type) {
        if (!type.isForFaction(faction)) throw new IllegalStateException("The skilltype " + type + " is not applicable for the faction " + faction.getID());
        ISkill skill = RegUtil.getSkill(type.createIdForFaction(faction.getID()));
        if (skill == null) {
            LOGGER.warn("No root skill exists for faction {}", faction.getID());
            throw new IllegalStateException("You need to register a root skill for your faction " + faction.getID());
        }
        return skill;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IFactionPlayer<T>> List<ISkill<T>> getSkillsForFaction(IPlayableFaction<T> faction) {
        return RegUtil.values(ModRegistries.SKILLS).stream().filter(action -> action.getFaction().map(f -> f == faction).orElse(true)).map(action -> (ISkill<T>)action).collect(Collectors.toList());
    }

    @Override
    public Collection<ISkillType> getSkillTypes() {
        return this.skillTypes.values();
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

    @Nullable
    @Override
    public ISkillType getSkillType(@NotNull ResourceLocation id) {
        return this.skillTypes.get(id);
    }

    @NotNull
    @Override
    public ISkillType registerSkillType(@NotNull ISkillType type) {
        if (this.skillTypes.containsKey(type.getRegistryName())) {
            throw new IllegalStateException("A skill type with the id " + type.getRegistryName() + " has already been registered");
        }
        this.skillTypes.put(type.getRegistryName(), type);
        return type;
    }
}
