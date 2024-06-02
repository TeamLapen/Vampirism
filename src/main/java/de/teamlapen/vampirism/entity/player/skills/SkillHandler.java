package de.teamlapen.vampirism.entity.player.skills;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillType;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.refinements.Refinement;
import de.teamlapen.vampirism.entity.player.refinements.RefinementSet;
import de.teamlapen.vampirism.items.RefinementItem;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles skills for Vampirism's IFactionPlayers
 */
public class SkillHandler<T extends IFactionPlayer<T>> implements ISkillHandler<T> {
    private final static Logger LOGGER = LogManager.getLogger(SkillHandler.class);
    /**
     * All currently activated skills
     */
    private final ArrayList<ISkill<T>> enabledSkills = new ArrayList<>();
    private final T player;
    private final IPlayableFaction<T> faction;
    private final NonNullList<ItemStack> refinementItems = NonNullList.withSize(3, ItemStack.EMPTY);
    private final Set<IRefinement> activeRefinements = new HashSet<>();
    private final Map<IRefinement, AttributeModifier> refinementModifier = new HashMap<>();
    private int maxSkillpoints;
    private boolean dirty = false;

    public SkillHandler(T player, IPlayableFaction<T> faction) {
        this.player = player;
        this.faction = faction;
    }

    public @NotNull Optional<SkillNode> anyLastNode() {
        Queue<SkillNode> queue = new ArrayDeque<>();
        for (ISkillType skillType : VampirismAPI.skillManager().getSkillTypes()) {
            if (skillType.isForFaction(faction)) {
                queue.add(getRootNode(skillType));
            }
        }

        for (SkillNode skillNode = queue.poll(); skillNode != null; skillNode = queue.poll()) {
            List<SkillNode> child = skillNode.getChildren().stream().filter(this::isNodeEnabled).toList();
            if (!child.isEmpty()) {
                queue.addAll(child);
            } else if (skillNode.getParent() != null) {
                return Optional.of(skillNode);
            }
        }
        return Optional.empty();
    }

    @Override
    public @NotNull Result canSkillBeEnabled(@NotNull ISkill<T> skill) {
        if (player.getRepresentingPlayer().getEffect(ModEffects.OBLIVION.get()) != null) {
            return Result.LOCKED_BY_PLAYER_STATE;
        }
        if (isSkillEnabled(skill)) {
            return Result.ALREADY_ENABLED;
        }
        SkillNode node = findSkillNode(getRootNode(skill.getType()), skill);
        if (node != null) {
            if (isSkillNodeLocked(node)) {
                return Result.LOCKED_BY_OTHER_NODE;
            }
            if (node.isRoot() || isNodeEnabled(node.getParent())) {
                if (getLeftSkillPoints() >= skill.getSkillPointCost()) {
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
    public ItemStack @NotNull [] createRefinementItems() {
        return this.refinementItems.toArray(ItemStack[]::new);
    }

    @Override
    public NonNullList<ItemStack> getRefinementItems() {
        return this.refinementItems;
    }

    @Override
    public void damageRefinements() {
        this.refinementItems.stream().filter(s -> !s.isEmpty()).forEach(stack -> {
            IRefinementSet set = ((IRefinementItem) stack.getItem()).getRefinementSet(stack);
            int damage = 40 + (set.getRarity().weight - 1) * 10 + this.getPlayer().getRepresentingPlayer().getRandom().nextInt(60);
            Integer unbreakingLevel = EnchantmentHelper.getEnchantments(stack).get(Enchantments.UNBREAKING);
            if (unbreakingLevel != null) {
                damage = (int) (damage / (1f/(1.6f/(unbreakingLevel + 1f))));
            }
            stack.setDamageValue(stack.getDamageValue() + damage);
            if (stack.getDamageValue() >= stack.getMaxDamage()) {
                stack.setCount(0);
            }
        });
    }

    public void disableAllSkills() {
        for (ISkill<T> skill : enabledSkills) {
            skill.onDisable(player);
        }
        enabledSkills.clear();
        dirty = true;
    }

    @Override
    public void disableSkill(@NotNull ISkill<T> skill) {
        if (enabledSkills.remove(skill)) {
            skill.onDisable(player);
            dirty = true;
        }


    }

    @Override
    public void enableRootSkills() {
        FactionPlayerHandler.getOpt(this.player.getRepresentingPlayer()).ifPresent(handler -> {
            for (ISkillType skillType : VampirismAPI.skillManager().getSkillTypes()) {
                if (!skillType.isForFaction(this.faction)) continue;
                if (skillType.isUnlocked(handler)) {
                    enableRootSkill(skillType);
                }
            }
        });
    }

    @Override
    public void enableRootSkill(@NotNull ISkillType type) {
        enableSkill((ISkill<T>) getRootNode(type).getElements()[0]);
    }

    @Override
    public void enableSkill(@NotNull ISkill<T> skill) {
        if (!enabledSkills.contains(skill)) {
            skill.onEnable(player);
            enabledSkills.add(skill);
            dirty = true;
            //noinspection ConstantValue
            if (this.player.getRepresentingPlayer() instanceof ServerPlayer serverPlayer && serverPlayer.connection != null) {
                serverPlayer.awardStat(ModStats.skills_unlocked);
                ModAdvancements.TRIGGER_SKILL_UNLOCKED.trigger(serverPlayer, skill);
            }
        }

    }

    @Override
    public boolean equipRefinementItem(@NotNull ItemStack stack) {
        if (stack.getItem() instanceof IRefinementItem refinementItem) {
            if (this.faction.equals(refinementItem.getExclusiveFaction(stack))) {
                @Nullable IRefinementSet newSet = refinementItem.getRefinementSet(stack);
                IRefinementItem.AccessorySlotType setSlot = refinementItem.getSlotType();

                removeRefinementItem(setSlot);
                this.dirty = true;

                applyRefinementItem(stack, setSlot.getSlot());
                return true;
            }
        }

        return false;
    }

    @Override
    public void removeRefinementItem(IRefinementItem.@NotNull AccessorySlotType slot) {
        removeRefinementItem(slot.getSlot());
        this.dirty = true;
    }

    public @Nullable SkillNode findSkillNode(@NotNull SkillNode base, ISkill<T> skill) {
        for (ISkill<?> s : base.getElements()) {
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
        int remainingSkillPoints = this.maxSkillpoints - enabledSkills.stream().mapToInt(ISkill::getSkillPointCost).sum();
        if (VampirismConfig.SERVER.unlockAllSkills.get()) {
            if(FactionPlayerHandler.getOpt(this.player.getRepresentingPlayer()).filter(s -> s.getCurrentLevel() == s.getCurrentFaction().getHighestReachableLevel()).filter(s -> s.getCurrentFaction().getHighestLordLevel() == 0 || s.getLordLevel() == s.getCurrentFaction().getHighestLordLevel()).isPresent()){
                return Integer.MAX_VALUE;
            }
        }
        return remainingSkillPoints;
    }

    @Override
    public int getMaxSkillPoints() {
        return this.maxSkillpoints;
    }

    public void addSkillPoints(int points) {
        this.maxSkillpoints = Math.max(0, this.maxSkillpoints + points);
        this.dirty = true;
    }

    public void reset() {
        disableAllSkills();
        resetRefinements();
        this.maxSkillpoints = 0;
        this.dirty = true;
    }

    public @NotNull List<ISkill<T>> getLockingSkills(@NotNull SkillNode nodeIn) {
        //noinspection unchecked
        return (List<ISkill<T>>) (Object) Arrays.stream(nodeIn.getLockingNodes()).map(id -> SkillTreeManager.getInstance().getSkillTree().getNodeFromId(id)).filter(Objects::nonNull).flatMap(node -> Arrays.stream(node.getElements())).collect(Collectors.toList());
    }

    @Override
    public ISkill<T> @Nullable [] getParentSkills(@NotNull ISkill<T> skill) {
        SkillNode node = findSkillNode(getRootNode(skill.getType()), skill);
        if (node == null) {
            return null;
        } else {
            //noinspection unchecked
            return (ISkill<T>[]) node.getParent().getElements();
        }
    }

    public T getPlayer() {
        return player;
    }

    public @NotNull Collection<SkillNode> getRootNodes() {
        return VampirismAPI.skillManager().getSkillTypes().stream().map(this::getRootNode).collect(Collectors.toList());
    }

    public @NotNull SkillNode getRootNode(@NotNull ISkillType type) {
        return VampirismMod.proxy.getSkillTree(player.isRemote()).getRootNodeForFaction(type.createIdForFaction(faction.getID()));
    }


    /**
     * @return If an update should be sent to the client
     */
    public boolean isDirty() {
        return dirty;
    }

    public boolean isNodeEnabled(@NotNull SkillNode node) {
        for (ISkill<T> s : enabledSkills) {
            if (node.containsSkill(s)) return true;
        }
        return false;
    }

    @Override
    public boolean isRefinementEquipped(IRefinement refinement) {
        return this.activeRefinements.contains(refinement);
    }

    @Override
    public boolean isSkillEnabled(ISkill<?> skill) {
        return enabledSkills.contains(skill);
    }

    public boolean isSkillNodeLocked(@NotNull SkillNode nodeIn) {
        return Arrays.stream(nodeIn.getLockingNodes()).map(id -> SkillTreeManager.getInstance().getSkillTree().getNodeFromId(id)).filter(Objects::nonNull).flatMap(node -> Arrays.stream(node.getElements())).anyMatch(this::isSkillEnabled);
    }

    public void loadFromNbt(@NotNull CompoundTag nbt) {
        if (nbt.contains("skills")) {
            for (String id : nbt.getCompound("skills").getAllKeys()) {
                //noinspection unchecked
                ISkill<T> skill = (ISkill<T>) RegUtil.getSkill(new ResourceLocation(id));
                if (skill == null) {
                    LOGGER.warn("Skill {} does not exist anymore", id);
                    continue;
                }
                enableSkill(skill);

            }
        }

        if (nbt.contains("refinement_set")) {
            CompoundTag setsNBT = nbt.getCompound("refinement_set");
            for (String id : setsNBT.getAllKeys()) {
                int i = Integer.parseInt(id);
                CompoundTag setNBT = setsNBT.getCompound(id);
                String setName = setNBT.getString("id");
                int damage = setNBT.getInt("damage");
                if ("none".equals(setName)) continue;
                ResourceLocation setId = new ResourceLocation(setName);
                IRefinementSet set = RegUtil.getRefinementSet(setId);
                Item refinementItem = this.faction.getRefinementItem(IRefinementItem.AccessorySlotType.values()[i]);
                ItemStack itemStack = new ItemStack(refinementItem);
                itemStack.setDamageValue(damage);
                ((IRefinementItem) refinementItem).applyRefinementSet(itemStack, set);
                this.applyRefinementItem(itemStack, i);
            }
        }
        if (nbt.contains("refinement_items")) {
            ListTag refinements = nbt.getList("refinement_items", 10);
            for (int i = 0; i < refinements.size(); i++) {
                CompoundTag stackNbt = refinements.getCompound(i);
                int slot = stackNbt.getInt("slot");
                ItemStack stack = ItemStack.of(stackNbt);
                if (stack.getItem() instanceof IRefinementItem refinementItem) {
                    IFaction<?> exclusiveFaction = refinementItem.getExclusiveFaction(stack);
                    if (exclusiveFaction == null || this.faction.equals(exclusiveFaction)) {
                        applyRefinementItem(stack, slot);
                    }
                }
            }
        }
        if (nbt.contains("skill_points")) {
            this.maxSkillpoints = nbt.getInt("skill_points");
        }
    }

    public void readUpdateFromServer(@NotNull CompoundTag nbt) {
        if (nbt.contains("skills")) {

            //noinspection unchecked
            List<ISkill<T>> old = (List<ISkill<T>>) enabledSkills.clone();
            for (String id : nbt.getCompound("skills").getAllKeys()) {
                //noinspection unchecked
                ISkill<T> skill = (ISkill<T>) RegUtil.getSkill(new ResourceLocation(id));
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
            for (ISkill<T> skill : old) {
                disableSkill(skill);
            }
        }

        if (nbt.contains("refinement_items")) {
            ListTag refinements = nbt.getList("refinement_items", 10);
            for (int i = 0; i < refinements.size(); i++) {
                CompoundTag stackNbt = refinements.getCompound(i);
                int slot = stackNbt.getInt("slot");
                ItemStack stack = ItemStack.of(stackNbt);
                if (stack.getItem() instanceof IRefinementItem refinementItem) {
                    IFaction<?> exclusiveFaction = refinementItem.getExclusiveFaction(stack);
                    if (exclusiveFaction == null || this.faction.equals(exclusiveFaction)) {
                        applyRefinementItem(stack, slot);
                    }
                }
            }
        }
        if (nbt.contains("skill_points")) {
            this.maxSkillpoints = nbt.getInt("skill_points");
        }
    }

    @Override
    public void resetRefinements() {
        this.refinementItems.clear();
    }

    public void resetSkills() {
        disableAllSkills();
        enableRootSkills();
    }

    public void saveToNbt(@NotNull CompoundTag nbt) {
        CompoundTag skills = new CompoundTag();
        for (ISkill<T> skill : enabledSkills) {
            skills.putBoolean(RegUtil.id(skill).toString(), true);
        }
        nbt.put("skills", skills);
        ListTag refinements = new ListTag();
        for (int i = 0; i < this.refinementItems.size(); i++) {
            ItemStack stack = this.refinementItems.get(i);
            if (!stack.isEmpty()) {
                CompoundTag stackNbt = new CompoundTag();
                stackNbt.putInt("slot", i);
                stack.save(stackNbt);
                refinements.add(stackNbt);
            }
        }
        nbt.put("refinement_items", refinements);
        nbt.putInt("skill_points", this.maxSkillpoints);
    }

    public void writeUpdateForClient(@NotNull CompoundTag nbt) {
        CompoundTag skills = new CompoundTag();
        for (ISkill<T> skill : enabledSkills) {
            skills.putBoolean(RegUtil.id(skill).toString(), true);
        }
        nbt.put("skills", skills);
        ListTag refinementItems = new ListTag();
        for (int i = 0; i < this.refinementItems.size(); i++) {
            ItemStack stack = this.refinementItems.get(i);
            if (!stack.isEmpty()) {
                CompoundTag stackNbt = new CompoundTag();
                stackNbt.putInt("slot", i);
                stack.save(stackNbt);
                refinementItems.add(stackNbt);
            }
        }
        nbt.put("refinement_items", refinementItems);
        nbt.putInt("skill_points", this.maxSkillpoints);
        dirty = false;
    }

    private void applyRefinementItem(@NotNull ItemStack stack, int slot) {
        this.refinementItems.set(slot, stack);
        if (stack.getItem() instanceof IRefinementItem refinementItem) {
            IRefinementSet set = refinementItem.getRefinementSet(stack);
            if (set != null) {
                set.getRefinements().stream().map(RegistryObject::get).forEach(x -> {
                    this.activeRefinements.add(x);
                    if (!this.player.isRemote() && x.getAttribute() != null) {
                        AttributeInstance attributeInstance = this.player.getRepresentingPlayer().getAttribute(x.getAttribute());
                        double value = x.getModifierValue();
                        AttributeModifier t = attributeInstance.getModifier(x.getUUID());
                        if (t != null) {
                            attributeInstance.removeModifier(t);
                            value += t.getAmount();
                        }
                        t = x.createAttributeModifier(x.getUUID(), value);
                        this.refinementModifier.put(x, t);
                        attributeInstance.addTransientModifier(t);
                    }
                });
            }
        }
    }

    private void removeRefinementItem(int slot) {
        ItemStack stack = this.refinementItems.get(slot);
        if (!stack.isEmpty()) {
            this.refinementItems.set(slot, ItemStack.EMPTY);
            if (stack.getItem() instanceof IRefinementItem refinementItem) {
                IRefinementSet set = refinementItem.getRefinementSet(stack);
                if (set != null) {
                    set.getRefinements().stream().map(RegistryObject::get).forEach(x -> {
                        this.activeRefinements.remove(x);
                        if (!this.player.isRemote() && x.getAttribute() != null) {
                            AttributeInstance attributeInstance = this.player.getRepresentingPlayer().getAttribute(x.getAttribute());
                            AttributeModifier t = this.refinementModifier.remove(x);
                            attributeInstance.removeModifier(t);
                            double value = t.getAmount() - x.getModifierValue();
                            if (value != 0) {
                                attributeInstance.addTransientModifier(t = x.createAttributeModifier(t.getId(), value));
                                this.refinementModifier.put(x, t);
                                this.activeRefinements.add(x);
                            }
                        }
                    });
                }
            }
        }
    }
}
