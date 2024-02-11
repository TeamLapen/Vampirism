package de.teamlapen.vampirism.entity.player.skills;

import de.teamlapen.lib.lib.storage.ISyncableSaveData;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.factions.ISkillNode;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillPointProvider;
import de.teamlapen.vampirism.api.entity.player.skills.SkillPointProviders;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.data.ISkillTreeData;
import de.teamlapen.vampirism.mixin.accessor.AttributeInstanceAccessor;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SkillHandler<T extends IFactionPlayer<T>> implements ISkillHandler<T>, ISyncableSaveData {
    private static final String NBT_KEY = "skill_handler";
    private final static Logger LOGGER = LogManager.getLogger(SkillHandler.class);
    private final ArrayList<ISkill<T>> enabledSkills = new ArrayList<>();
    private final T player;
    private final IPlayableFaction<T> faction;
    private final NonNullList<ItemStack> refinementItems = NonNullList.withSize(3, ItemStack.EMPTY);
    private final Set<IRefinement> activeRefinements = new HashSet<>();
    private final Map<IRefinement, AttributeModifier> refinementModifier = new HashMap<>();
    private final ISkillPointProvider skillPoints = new SkillPoints();
    public LinkedHashSet<Holder<ISkillTree>> unlockedTrees = new LinkedHashSet<>();
    private boolean dirty = false;
    private final ISkillTreeData treeData;

    public SkillHandler(T player, IPlayableFaction<T> faction) {
        this.player = player;
        this.faction = faction;
        this.treeData = ISkillTreeData.getData(player.asEntity().level());
    }

    public @NotNull Optional<ISkillNode> anyLastNode() {
        return unlockedTrees.stream().flatMap(s -> this.treeData.getAnyLastNode(s, this::isNodeEnabled).stream()).findAny();
    }

    public ISkillTreeData getTreeData() {
        return this.treeData;
    }

    @Override
    public @NotNull Result canSkillBeEnabled(@NotNull ISkill<T> skill) {
        if (player.asEntity().getEffect(ModEffects.OBLIVION.get()) != null) {
            return Result.LOCKED_BY_PLAYER_STATE;
        }
        if (isSkillEnabled(skill)) {
            return Result.ALREADY_ENABLED;
        }
        Optional<SkillTreeConfiguration.SkillTreeNodeConfiguration> node = unlockedTrees.stream().flatMap(x -> treeData.getNodeForSkill(unlockedTrees, skill).stream()).findFirst();
        if (node.isPresent()) {
            if (isSkillNodeLocked(node.get().node().value())) {
                return Result.LOCKED_BY_OTHER_NODE;
            }
            if (this.treeData.isRoot(this.unlockedTrees, node.get()) || this.treeData.getParent(node.get()).stream().anyMatch(x -> isNodeEnabled(x.value()))) {
                if (getLeftSkillPoints() >= skill.getSkillPointCost()) {
                    return isNodeEnabled(node.get().node().value()) ? Result.OTHER_NODE_SKILL : Result.OK;//If another skill in that node is already enabled this one cannot be enabled
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
            int damage = 40 + (set.getRarity().weight - 1) * 10 + this.getPlayer().asEntity().getRandom().nextInt(60);
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
    public void enableSkill(@NotNull ISkill<T> skill, boolean fromLoading) {
        if (!enabledSkills.contains(skill)) {
            skill.onEnable(player);
            enabledSkills.add(skill);
            if (!fromLoading) {
                this.player.asEntity().awardStat(ModStats.SKILL_UNLOCKED.get().get(skill));
            }
            dirty = true;
            //noinspection ConstantValue
            if (this.player.asEntity() instanceof ServerPlayer serverPlayer && serverPlayer.connection != null) {
                ModAdvancements.TRIGGER_SKILL_UNLOCKED.get().trigger(serverPlayer, skill);
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

    @Override
    public void updateUnlockedSkillTrees(Collection<Holder<ISkillTree>> skillTrees) {
        List<Holder<ISkillTree>> removedTrees = this.unlockedTrees.stream().filter(x -> !skillTrees.contains(x)).toList();
        removedTrees.forEach(this::lockSkillTree);
        skillTrees.stream().filter(x -> !this.unlockedTrees.contains(x)).forEach(this::unlockSkillTree);
        this.dirty = true;
    }

    private void unlockSkillTree(Holder<ISkillTree> tree) {
        this.unlockedTrees.add(tree);
        SkillTreeConfiguration.SkillTreeNodeConfiguration root = this.treeData.root(tree);
        root.elements().forEach(x -> enableSkill((ISkill<T>) x.value(), true));
        this.dirty = true;
    }

    private void lockSkillTree(Holder<ISkillTree> tree) {
        for (ISkill<T> enabledSkill : this.enabledSkills) {
            if (enabledSkill.allowedSkillTrees().map(tree::is, tree::is)) {
                this.disableSkill(enabledSkill);
            }
        }
        this.unlockedTrees.remove(tree);
        this.dirty = true;
    }

    public @NotNull Collection<Holder<ISkillTree>> unlockedSkillTrees() {
        return Collections.unmodifiableCollection(this.unlockedTrees);
    }

    @Override
    public int getLeftSkillPoints() {
        if (this.skillPoints.ignoreSkillPointLimit(this.player)) {
            return Integer.MAX_VALUE;
        }
        return Math.max(0, this.skillPoints.getSkillPoints(this.player) - this.enabledSkills.stream().mapToInt(ISkill::getSkillPointCost).sum());
    }

    public void reset() {
        disableAllSkills();
        resetRefinements();
        this.unlockedTrees.clear();
        this.dirty = true;
    }

    @Override
    public ISkill<T> @Nullable [] getParentSkills(@NotNull ISkill<T> skill) {
        Optional<SkillTreeConfiguration.SkillTreeNodeConfiguration> nodeForSkill = this.treeData.getNodeForSkill(this.unlockedTrees, skill);
        return nodeForSkill.flatMap(this.treeData::getParent).stream().flatMap(x -> x.value().skills().stream()).map(Holder::value).toArray(ISkill[]::new);
    }

    public T getPlayer() {
        return player;
    }

    public boolean noSkillEnabled() {
        List<ISkill<?>> list = this.unlockedTrees.stream().map(this.treeData::root).flatMap(x -> x.elements().stream()).map(Holder::value).collect(Collectors.toList());
        return this.enabledSkills.isEmpty() || new HashSet<>(list).containsAll(this.enabledSkills);
    }

    /**
     * @return If an update should be sent to the client
     */
    public boolean isDirty() {
        return dirty;
    }

    public boolean isNodeEnabled(@NotNull ISkillNode node) {
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

    @Override
    public boolean isSkillEnabled(Holder<ISkill<?>> skill) {
        return enabledSkills.contains(skill.value());
    }

    public boolean isSkillNodeLocked(@NotNull ISkillNode nodeIn) {
        Registry<ISkillNode> nodes = player.asEntity().level().registryAccess().registryOrThrow(VampirismRegistries.SKILL_NODE_ID);
        return nodeIn.lockingNodes().stream().flatMap(s -> nodes.getOptional(s).stream()).flatMap(s -> s.skills().stream()).map(Holder::value).anyMatch(this::isSkillEnabled);
    }

    @Override
    public void deserializeNBT(@NotNull CompoundTag nbt) {
        if (nbt.contains("skills")) {
            for (String id : nbt.getCompound("skills").getAllKeys()) {
                //noinspection unchecked
                ISkill<T> skill = (ISkill<T>) RegUtil.getSkill(new ResourceLocation(id));
                if (skill == null) {
                    LOGGER.warn("Skill {} does not exist anymore", id);
                    continue;
                }
                enableSkill(skill, true);

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
        if (nbt.contains("unlocked_trees")) {
            ListTag unlockedTrees = nbt.getList("unlocked_trees", StringTag.TAG_STRING);
            this.unlockedTrees.clear();
            unlockedTrees.stream().map(StringTag.class::cast).forEach(tag -> {
                this.unlockedTrees.add(RegUtil.getSkillTree(getPlayer().asEntity().level(), tag.getAsString()));
            });
        }
    }

    @Override
    public void deserializeUpdateNBT(@NotNull CompoundTag nbt) {
        if (nbt.contains("skills", Tag.TAG_COMPOUND)) {

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
                    enableSkill(skill, true);
                }


            }
            for (ISkill<T> skill : old) {
                disableSkill(skill);
            }
        }

        if (nbt.contains("refinement_items", Tag.TAG_LIST)) {
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
        if (nbt.contains("unlocked_trees", Tag.TAG_LIST)) {
            ListTag unlockedTrees = nbt.getList("unlocked_trees", StringTag.TAG_STRING);
            this.unlockedTrees.clear();
            unlockedTrees.stream().map(StringTag.class::cast).forEach(tag -> {
                this.unlockedTrees.add(RegUtil.getSkillTree(getPlayer().asEntity().level(), tag.getAsString()));
            });
        }
    }

    @Override
    public void resetRefinements() {
        this.refinementItems.clear();
    }

    public void resetSkills() {
        disableAllSkills();
    }

    @Override
    public @NotNull CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
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
        ListTag unlockedTrees = new ListTag();
        for (Holder<ISkillTree> tree : this.unlockedTrees) {
            unlockedTrees.add(StringTag.valueOf(RegUtil.id(getPlayer().asEntity().level(), tree.value()).toString()));
        }
        nbt.put("unlocked_trees", unlockedTrees);
        return nbt;
    }

    @Override
    public @NotNull CompoundTag serializeUpdateNBT() {
        CompoundTag nbt = new CompoundTag();
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
        ListTag unlockedTrees = new ListTag();
        for (Holder<ISkillTree> tree : this.unlockedTrees) {
            unlockedTrees.add(StringTag.valueOf(RegUtil.id(getPlayer().asEntity().level(), tree.value()).toString()));
        }
        nbt.put("unlocked_trees", unlockedTrees);
        dirty = false;
        return nbt;
    }

    private void applyRefinementItem(@NotNull ItemStack stack, int slot) {
        this.refinementItems.set(slot, stack);
        if (stack.getItem() instanceof IRefinementItem refinementItem) {
            IRefinementSet set = refinementItem.getRefinementSet(stack);
            if (set != null) {
                set.getRefinements().stream().map(Supplier::get).forEach(x -> {
                    this.activeRefinements.add(x);
                    if (!this.player.isRemote() && x.getAttribute() != null) {
                        AttributeInstance attributeInstance = this.player.asEntity().getAttribute(x.getAttribute());
                        double value = x.getModifierValue();
                        AttributeModifier t = attributeInstance.getModifier(x.getUUID());
                        if (t != null) {
                            attributeInstance.removeModifier(x.getUUID());
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
                    set.getRefinements().stream().map(Supplier::get).forEach(x -> {
                        this.activeRefinements.remove(x);
                        if (!this.player.isRemote() && x.getAttribute() != null) {
                            AttributeInstance attributeInstance = this.player.asEntity().getAttribute(x.getAttribute());
                            AttributeModifier t = this.refinementModifier.remove(x);
                            ((AttributeInstanceAccessor) attributeInstance).invoke_removeModifier(t);
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

    @Override
    public String nbtKey() {
        return NBT_KEY;
    }

    public static class SkillPoints implements ISkillPointProvider {
        private final Map<ResourceLocation, ISkillPointProvider> provider;

        public SkillPoints() {
            this.provider = SkillPointProviders.MODIFIERS_VIEW;
        }

        @Override
        public int getSkillPoints(IFactionPlayer<?> factionPlayer) {
            return this.provider.values().stream().mapToInt(x -> Math.max(0, x.getSkillPoints(factionPlayer))).sum();
        }

        @Override
        public boolean ignoreSkillPointLimit(IFactionPlayer<?> factionPlayer) {
            return this.provider.values().stream().anyMatch(l -> l.ignoreSkillPointLimit(factionPlayer));
        }
    }
}
