package de.teamlapen.vampirism.player.skills;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.items.VampireRefinementItem;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
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
    private final IRefinementSet[] appliedRefinementSets = new IRefinementSet[3];
    private final int[] refinementSetDamage = new int[3];
    private final Set<IRefinement> activeRefinements = new HashSet<>();
    private final Map<IRefinement, AttributeModifier> refinementModifier = new HashMap<>();
    private boolean dirty = false;

    public SkillHandler(T player, IPlayableFaction<T> faction) {
        this.player = player;
        this.faction = faction;
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

    @Override
    public Result canSkillBeEnabled(ISkill skill) {
        if (player.getRepresentingPlayer().getEffect(ModEffects.OBLIVION.get()) != null) {
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

    @Override
    public ItemStack[] createRefinementItems() {
        ItemStack[] items = new ItemStack[this.appliedRefinementSets.length];
        for (int i = 0; i < this.appliedRefinementSets.length; i++) {
            if (this.appliedRefinementSets[i] != null) {
                items[i] = new ItemStack(this.faction.getRefinementItem(IRefinementItem.AccessorySlotType.values()[i]));
                this.faction.getRefinementItem(IRefinementItem.AccessorySlotType.values()[i]).applyRefinementSet(items[i], this.appliedRefinementSets[i]);
                items[i].setDamageValue(this.refinementSetDamage[i]);
            }
        }
        return items;
    }

    @Override
    public void damageRefinements() {
        for (int i = 0; i < this.refinementSetDamage.length; i++) {
            if (this.appliedRefinementSets[i] == null) continue;
            int damage = 40 + (this.appliedRefinementSets[i].getRarity().weight - 1) * 10 + this.getPlayer().getRepresentingPlayer().getRandom().nextInt(60);
            if ((this.refinementSetDamage[i] += damage) >= VampireRefinementItem.MAX_DAMAGE) {
                this.removeRefinementSet(i);
            }
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
        enableSkill(getRootNode().getElements()[0]);
    }

    @Override
    public void enableSkill(ISkill skill) {
        if (!enabledSkills.contains(skill)) {
            skill.onEnable(player);
            enabledSkills.add(skill);
            dirty = true;
            if (this.player.getRepresentingPlayer() instanceof ServerPlayerEntity) {
                ModAdvancements.TRIGGER_SKILL_UNLOCKED.trigger((ServerPlayerEntity) player.getRepresentingPlayer(), skill);
            }
        }

    }

    @Override
    public boolean equipRefinementItem(ItemStack stack) {
        if (stack.getItem() instanceof IRefinementItem) {
            IRefinementItem refinementItem = ((IRefinementItem) stack.getItem());
            if (refinementItem.getExclusiveFaction().equals(this.faction)) {
                @Nullable IRefinementSet newSet = refinementItem.getRefinementSet(stack);
                IRefinementItem.AccessorySlotType setSlot = refinementItem.getSlotType();

                removeRefinementItem(setSlot);
                this.dirty = true;

                if (newSet != null && newSet.getFaction() == faction) {
                    applyRefinementSet(newSet, setSlot.getSlot());
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public void removeRefinementItem(IRefinementItem.AccessorySlotType slot) {
        this.removeRefinementSet(slot.getSlot());
        this.dirty = true;
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
        int totalSkillPoints = (int) (level * VampirismConfig.BALANCE.skillPointsPerLevel.get());
        int remainingSkillPoints = totalSkillPoints - enabledSkills.size();
        if (VampirismConfig.SERVER.unlockAllSkills.get() && level == player.getMaxLevel()) {
            return Math.max(remainingSkillPoints, 1);
        }
        return remainingSkillPoints;
    }

    public List<ISkill> getLockingSkills(SkillNode nodeIn) {
        return Arrays.stream(nodeIn.getLockingNodes()).map(id -> SkillTreeManager.getInstance().getSkillTree().getNodeFromId(id)).filter(Objects::nonNull).flatMap(node -> Arrays.stream(node.getElements())).collect(Collectors.toList());
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

    public SkillNode getRootNode() {
        return VampirismMod.proxy.getSkillTree(player.isRemote()).getRootNodeForFaction(faction.getID());
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
    public boolean isRefinementEquipped(IRefinement refinement) {
        return this.activeRefinements.contains(refinement);
    }

    @Override
    public boolean isSkillEnabled(ISkill skill) {
        return enabledSkills.contains(skill);
    }

    public boolean isSkillNodeLocked(SkillNode nodeIn) {
        return Arrays.stream(nodeIn.getLockingNodes()).map(id -> SkillTreeManager.getInstance().getSkillTree().getNodeFromId(id)).filter(Objects::nonNull).flatMap(node -> Arrays.stream(node.getElements())).anyMatch(this::isSkillEnabled);
    }

    public void loadFromNbt(CompoundNBT nbt) {
        if (nbt.contains("skills")) {
            for (String id : nbt.getCompound("skills").getAllKeys()) {
                ISkill skill = ModRegistries.SKILLS.getValue(new ResourceLocation(id));
                if (skill == null) {
                    LOGGER.warn("Skill {} does not exist anymore", id);
                    continue;
                }
                enableSkill(skill);

            }
        }

        if (nbt.contains("refinement_set")) {
            CompoundNBT setsNBT = nbt.getCompound("refinement_set");
            for (String id : setsNBT.getAllKeys()) {
                int i = Integer.parseInt(id);
                CompoundNBT setNBT = setsNBT.getCompound(id);
                String setName = setNBT.getString("id");
                int damage = setNBT.getInt("damage");
                if ("none".equals(setName)) continue;
                ResourceLocation setId = new ResourceLocation(setName);
                IRefinementSet set = ModRegistries.REFINEMENT_SETS.getValue(setId);
                this.applyRefinementSet(set, i);
                this.refinementSetDamage[i] = damage;
            }
        }

    }

    public void readUpdateFromServer(CompoundNBT nbt) {
        if (nbt.contains("skills")) {

            //noinspection unchecked
            List<ISkill> old = (List<ISkill>) enabledSkills.clone();
            for (String id : nbt.getCompound("skills").getAllKeys()) {
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
        }

        if (nbt.contains("refinement_set")) {
            CompoundNBT setsNBT = nbt.getCompound("refinement_set");
            for (String id : setsNBT.getAllKeys()) {
                int i = Integer.parseInt(id);
                CompoundNBT setNBT = setsNBT.getCompound(id);
                String setName = setNBT.getString("id");
                int damage = setNBT.getInt("damage");
                IRefinementSet set = null;
                if (!"none".equals(setName)) {
                    set = ModRegistries.REFINEMENT_SETS.getValue(new ResourceLocation(setName));
                }
                IRefinementSet oldSet = this.appliedRefinementSets[i];
                if (oldSet != set) {
                    this.removeRefinementSet(i);
                    this.applyRefinementSet(set, i);
                }
                this.refinementSetDamage[i] = damage;
            }
        }
    }

    @Override
    public void resetRefinements() {
        for (int i = 0; i < this.appliedRefinementSets.length; i++) {
            this.removeRefinementSet(i);
        }
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
        CompoundNBT refinements = new CompoundNBT();
        for (int i = 0; i < this.appliedRefinementSets.length; ++i) {
            CompoundNBT setNbt = new CompoundNBT();
            IRefinementSet set = this.appliedRefinementSets[i];
            int damage = this.refinementSetDamage[i];
            setNbt.putString("id", set != null ? set.getRegistryName().toString() : "none");
            setNbt.putInt("damage", damage);
            refinements.put(String.valueOf(i), setNbt);
        }
        nbt.put("refinement_set", refinements);

    }

    public void writeUpdateForClient(CompoundNBT nbt) {
        CompoundNBT skills = new CompoundNBT();
        for (ISkill skill : enabledSkills) {
            skills.putBoolean(skill.getRegistryName().toString(), true);
        }
        nbt.put("skills", skills);
        CompoundNBT refinements = new CompoundNBT();
        for (int i = 0; i < this.appliedRefinementSets.length; ++i) {
            CompoundNBT setNbt = new CompoundNBT();
            IRefinementSet set = this.appliedRefinementSets[i];
            int damage = this.refinementSetDamage[i];
            setNbt.putString("id", set != null ? set.getRegistryName().toString() : "none");
            setNbt.putInt("damage", damage);
            refinements.put(String.valueOf(i), setNbt);
        }
        nbt.put("refinement_set", refinements);
        dirty = false;
    }

    private void applyRefinementSet(@Nullable IRefinementSet set, int slot) {
        this.appliedRefinementSets[slot] = set;
        this.refinementSetDamage[slot] = 0;
        if (set != null) {
            Collection<IRefinement> refinements = set.getRefinements();
            for (IRefinement refinement : refinements) {
                this.activeRefinements.add(refinement);
                if (!this.player.isRemote()) {
                    Attribute a = refinement.getAttribute();
                    if (a != null) {
                        ModifiableAttributeInstance attributeInstance = this.player.getRepresentingPlayer().getAttribute(a);
                        double value = refinement.getModifierValue();
                        AttributeModifier t = attributeInstance.getModifier(refinement.getUUID());
                        if (t != null) {
                            attributeInstance.removeModifier(t);
                            value += t.getAmount();
                        }
                        t = refinement.createAttributeModifier(refinement.getUUID(), value);
                        this.refinementModifier.put(refinement, t);
                        attributeInstance.addTransientModifier(t);
                    }
                }
            }
        }
    }

    private void removeRefinementSet(int slot) {
        IRefinementSet set = this.appliedRefinementSets[slot];
        this.appliedRefinementSets[slot] = null;
        if (set != null) {
            Collection<IRefinement> refinements = set.getRefinements();
            for (IRefinement refinement : refinements) {
                this.activeRefinements.remove(refinement);
                if (!this.player.isRemote()) {
                    Attribute a = refinement.getAttribute();
                    if (a != null) {
                        ModifiableAttributeInstance attributeInstance = this.player.getRepresentingPlayer().getAttribute(a);
                        AttributeModifier modifier = this.refinementModifier.get(refinement);
                        double value = modifier.getAmount() - refinement.getModifierValue();
                        this.refinementModifier.remove(refinement);
                        attributeInstance.removeModifier(modifier);
                        if (value != 0) {
                            attributeInstance.addTransientModifier(modifier = refinement.createAttributeModifier(modifier.getId(), value));
                            this.refinementModifier.put(refinement, modifier);
                            this.activeRefinements.add(refinement);
                        }
                    }
                }
            }
        }
    }
}
