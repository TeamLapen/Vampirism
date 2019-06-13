package de.teamlapen.vampirism.player.skills;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.player.skills.SkillNode;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.VampirismRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles skills for Vampirism's IFactionPlayers
 */
public class SkillHandler<T extends IFactionPlayer> implements ISkillHandler<T> {
    private final static Logger LOGGER = LogManager.getLogger(SkillHandler.class);
    /**
     * All currently activated skills
     */
    private final ArrayList<ISkill> enabledSkills = new ArrayList<>();
    private final T player;
    private boolean dirty = false;

    public SkillHandler(T player) {
        this.player = player;
    }

    @Override
    public Result canSkillBeEnabled(ISkill skill) {
        if (isSkillEnabled(skill)) {
            return Result.ALREADY_ENABLED;
        }
        SkillNode node = VampirismAPI.skillManager().getRootSkillNode(player.getFaction());
        node = findSkillNode(node, skill);
        if (node != null) {
            if (node.isRoot() || isNodeEnabled(node.getParent())) {
                if (getLeftSkillPoints() > 0) {
                    return isNodeEnabled(node) ? Result.OTHER_NODE_SKILL : Result.OK;//If another skill in that node is already enabled this one cannot be enabled
                } else {
                    return Result.NO_POINTS;
                }

            } else {
                return Result.PARENT_NOT_ENABLED;
            }
        } else {
            LOGGER.warn("Node for skill %s could not be found", skill);
            return Result.NOT_FOUND;
        }
    }

    public void disableAllSkills() {
        for (ISkill skill : enabledSkills) {
            skill.onDisable(player);
        }
        enabledSkills.clear();
        dirty = true;
    }

    @Override
    public void disableSkill(ISkill skill) {
        if (enabledSkills.remove(skill)) {
            skill.onDisable(player);
            dirty = true;
        }


    }

    public void enableRootSkill() {
        enableSkill(VampirismAPI.skillManager().getRootSkillNode(player.getFaction()).getElements()[0]);
    }

    @Override
    public void enableSkill(ISkill skill) {
        if (!enabledSkills.contains(skill)) {
            skill.onEnable(player);
            enabledSkills.add(skill);
            dirty = true;
        }

    }

    public SkillNode findSkillNode(SkillNode base, ISkill skill) {
        for (ISkill s : base.getElements()) {
            if (s.equals(skill)) {
                return base;
            }
        }
        SkillNode node;
        for (SkillNode child : base.getChildren()) {
            if ((node = findSkillNode(child, skill)) != null) {
                return node;
            }
        }
        return null;
    }

    @Override
    public int getLeftSkillPoints() {
        int level = player.getLevel();
        if (Configs.unlock_all_skills && level == player.getMaxLevel()) {
            return 1;
        }
        return player.getLevel() - enabledSkills.size();
    }

    public IFactionPlayer<T> getPlayer() {
        return player;
    }

    /**
     * @return The root node of the faction this handler belongs to
     */
    public SkillNode getRootNode() {
        return VampirismAPI.skillManager().getRootSkillNode(player.getFaction());
    }

    /**
     * @return If an update should be send to the client
     */
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public boolean isNodeEnabled(SkillNode node) {
        for (ISkill s : enabledSkills) {
            if (node.containsSkill(s)) return true;
        }
        return false;
    }

    @Override
    public boolean isSkillEnabled(ISkill skill) {
        return enabledSkills.contains(skill);
    }

    public void loadFromNbt(NBTTagCompound nbt) {
        if (!nbt.contains("skills")) return;
        for (String id : nbt.getCompound("skills").keySet()) {
            ISkill skill = VampirismRegistries.SKILLS.getValue(new ResourceLocation(id));
            if (skill == null) {
                LOGGER.warn("Skill %s does not exist anymore", id);
                continue;
            }
            enableSkill(skill);

        }

    }

    public void readUpdateFromServer(NBTTagCompound nbt) {
        if (!nbt.contains("skills")) return;
        List<ISkill> old = (List<ISkill>) enabledSkills.clone();
        for (String id : nbt.getCompound("skills").keySet()) {
            ISkill skill = VampirismRegistries.SKILLS.getValue(new ResourceLocation(id));
            if (skill == null) {
                LOGGER.error("Skill %s does not exist on client!!!", id);
                continue;
            }
            if (old.contains(skill)) {
                old.remove(skill);
            } else {
                enableSkill(skill);
            }


        }
        for (ISkill skill : old) {
            disableSkill(skill);
        }
    }

    public void resetSkills() {
        disableAllSkills();
        enableRootSkill();
        //TODO make this cost something
    }

    public void saveToNbt(NBTTagCompound nbt) {
        NBTTagCompound skills = new NBTTagCompound();
        for (ISkill skill : enabledSkills) {
            skills.putBoolean(skill.getRegistryName().toString(), true);
        }
        nbt.put("skills", skills);

    }

    public void writeUpdateForClient(NBTTagCompound nbt) {
        NBTTagCompound skills = new NBTTagCompound();
        for (ISkill skill : enabledSkills) {
            skills.putBoolean(skill.getRegistryName().toString(), true);
        }
        nbt.put("skills", skills);
        dirty = false;
    }
}
