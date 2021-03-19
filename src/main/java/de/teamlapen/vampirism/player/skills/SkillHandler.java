package de.teamlapen.vampirism.player.skills;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles skills for Vampirism's IFactionPlayers
 */
public class SkillHandler<T extends IFactionPlayer<?>> implements ISkillHandler<T> {
    private final static Logger LOGGER = LogManager.getLogger(SkillHandler.class);
    /**
     * All currently activated skills
     */
    private final ArrayList<ISkill> enabledSkills = new ArrayList<>();
    private final T player;
    private final IPlayableFaction<T> faction;
    private boolean dirty = false;

    public SkillHandler(T player, IPlayableFaction<T> faction) {
        this.player = player;
        this.faction = faction;
    }

    @Override
    public Result canSkillBeEnabled(ISkill skill) {
        if (player.getRepresentingPlayer().getActivePotionEffect(ModEffects.oblivion) != null) {
            return Result.LOCKED_BY_PLAYER_STATE;
        }
        if (isSkillEnabled(skill)) {
            return Result.ALREADY_ENABLED;
        }
        SkillNode node = findSkillNode(getRootNode(), skill);
        if (node != null) {
            if (isSkillNodeLocked(node)) {
                return Result.LOCKED_BY_OTHER_NODE;
            }
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
            LOGGER.warn("Node for skill {} could not be found", skill);
            return Result.NOT_FOUND;
        }
    }

    public boolean isSkillNodeLocked(SkillNode nodeIn) {
        return Arrays.stream(nodeIn.getLockingNodes()).map(id -> SkillTreeManager.getInstance().getSkillTree().getNodeFromId(id)).filter(Objects::nonNull).flatMap(node -> Arrays.stream(node.getElements())).anyMatch(this::isSkillEnabled);
    }

    public List<ISkill> getLockingSkills(SkillNode nodeIn) {
        return Arrays.stream(nodeIn.getLockingNodes()).map(id -> SkillTreeManager.getInstance().getSkillTree().getNodeFromId(id)).filter(Objects::nonNull).flatMap(node -> Arrays.stream(node.getElements())).collect(Collectors.toList());
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
        enableSkill(getRootNode().getElements()[0]);
    }

    @Override
    public void enableSkill(ISkill skill) {
        if (!enabledSkills.contains(skill)) {
            skill.onEnable(player);
            enabledSkills.add(skill);
            dirty = true;
            if(this.player.getRepresentingPlayer() instanceof ServerPlayerEntity) {
                ModAdvancements.TRIGGER_SKILL_UNLOCKED.trigger((ServerPlayerEntity)player.getRepresentingPlayer(), skill);
            }
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
        int totalSkillPoints = level * VampirismConfig.BALANCE.skillPointsPerLevel.get();
        int remainingSkillPoints = totalSkillPoints - enabledSkills.size();
        if (VampirismConfig.SERVER.unlockAllSkills.get() && level == player.getMaxLevel()) {
            return Math.max(remainingSkillPoints, 1);
        }
        return remainingSkillPoints;
    }

    @Override
    public ISkill[] getParentSkills(ISkill skill) {
        SkillNode node = findSkillNode(getRootNode(), skill);
        if (node == null)
            return null;
        else
            return node.getParent().getElements();
    }

    public T getPlayer() {
        return player;
    }

    /**
     * @return If an update should be send to the client
     */
    public boolean isDirty() {
        return dirty;
    }

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

    public void loadFromNbt(CompoundNBT nbt) {
        if (!nbt.contains("skills")) return;
        for (String id : nbt.getCompound("skills").keySet()) {
            ISkill skill = ModRegistries.SKILLS.getValue(new ResourceLocation(id));
            if (skill == null) {
                LOGGER.warn("Skill {} does not exist anymore", id);
                continue;
            }
            enableSkill(skill);

        }

    }

    public void readUpdateFromServer(CompoundNBT nbt) {
        if (!nbt.contains("skills")) return;
        //noinspection unchecked
        List<ISkill> old = (List<ISkill>) enabledSkills.clone();
        for (String id : nbt.getCompound("skills").keySet()) {
            ISkill skill = ModRegistries.SKILLS.getValue(new ResourceLocation(id));
            if (skill == null) {
                LOGGER.error("Skill {} does not exist on client!!!", id);
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
        VampirismMod.proxy.resetSkillScreenCache();
    }

    public void resetSkills() {
        disableAllSkills();
        enableRootSkill();
    }

    public void saveToNbt(CompoundNBT nbt) {
        CompoundNBT skills = new CompoundNBT();
        for (ISkill skill : enabledSkills) {
            skills.putBoolean(skill.getRegistryName().toString(), true);
        }
        nbt.put("skills", skills);

    }

    public void writeUpdateForClient(CompoundNBT nbt) {
        CompoundNBT skills = new CompoundNBT();
        for (ISkill skill : enabledSkills) {
            skills.putBoolean(skill.getRegistryName().toString(), true);
        }
        nbt.put("skills", skills);
        dirty = false;
    }

    public SkillNode getRootNode() {
        return VampirismMod.proxy.getSkillTree(player.isRemote()).getRootNodeForFaction(faction.getID());
    }

    public Optional<SkillNode> anyLastNode() {
        SkillNode rootNode = getRootNode();
        Queue<SkillNode> queue = new ArrayDeque<>();
        queue.add(rootNode);

        for (SkillNode skillNode = queue.poll(); skillNode != null; skillNode = queue.poll()) {
            List<SkillNode> child = skillNode.getChildren().stream().filter(this::isNodeEnabled).collect(Collectors.toList());
            if (child.isEmpty()) {
                if (skillNode == rootNode) {
                    skillNode = null;
                }
                return Optional.ofNullable(skillNode);
            } else {
                queue.addAll(child);
            }
        }
        return Optional.empty();
    }
}
