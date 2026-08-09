package de.teamlapen.faction.common.factions.skills;

import com.mojang.serialization.Codec;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.*;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.util.SafeCast;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.core.FactionAdvancements;
import de.teamlapen.faction.common.core.FactionEffects;
import de.teamlapen.faction.common.core.FactionStats;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.faction.common.event.FactionEventFactory;
import de.teamlapen.faction.common.util.collections.CollectionUtil;
import de.teamlapen.faction.common.util.collections.LiveMap;
import de.teamlapen.sync.PropertySync;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class SkillHandler<T extends IFactionPlayer<T> & ISkillPlayer<T>> extends PropertySync implements ISkillHandler<T> {
    private final static Logger LOGGER = LogManager.getLogger();
    private final Map<Holder<ISkillTree>, List<Holder<? extends ISkill<T>>>> enabledSkills = new HashMap<>();
    private final LiveMap<Holder<? extends ISkill<T>>> skills = new LiveMap<>(enabledSkills);
    private final T player;
    private final SkillPoints skillPoints = new SkillPoints();
    private final Set<Holder<ISkillTree>> unlockedTrees = new LinkedHashSet<>();
    private final ISkillTreeData treeData;

    public SkillHandler(T player) {
        this.player = player;
        this.treeData = ISkillTreeData.getData(player.asEntity().level());
    }

    @Override
    public void sync() {
        this.player.sync();
    }

    public Optional<Pair<Holder<ISkillTree>, ISkillNode>> anyLastNode() {
        return unlockedTrees.stream().flatMap(s -> this.treeData.getAnyLastNode(s, l -> isNodeEnabled(s, l)).map(x -> Pair.of(s, x)).stream()).findAny();
    }

    public ISkillTreeData getTreeData() {
        return this.treeData;
    }

    @Override
    public Result canSkillBeEnabled(Holder<? extends ISkill<?>> skill, Holder<ISkillTree> skillTree) {
        var preResult = FactionEventFactory.fireSkillUnlockCheckEvent(this.player, skill);
        if (preResult != null) {
            return preResult;
        }

        if (player.asEntity().getEffect(FactionEffects.OBLIVION) != null) {
            return Result.LOCKED_BY_PLAYER_STATE;
        }
        if (isSkillEnabled(skill)) {
            return Result.ALREADY_ENABLED;
        }
        Optional<SkillTreeConfiguration.SkillTreeNodeConfiguration> node = unlockedTrees.stream().flatMap(x -> treeData.getNodeForSkill(skillTree, skill).stream()).findFirst();
        if (node.isPresent()) {
            if (isSkillNodeLocked(node.get().node().value())) {
                return Result.LOCKED_BY_OTHER_NODE;
            }
            if (this.treeData.isRoot(this.unlockedTrees, node.get()) || this.treeData.getParent(node.get()).stream().anyMatch(x -> isNodeEnabled(skillTree, x.value()))) {
                if (getLeftSkillPoints(skillTree) >= skill.value().getSkillPointCost()) {
                    return isNodeEnabled(skillTree, node.get().node().value()) ? Result.OTHER_NODE_SKILL : Result.OK;//If another skill in that node is already enabled this one cannot be enabled
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
    public Result canSkillBeEnabled(Holder<? extends ISkill<?>> skill) {
        return this.treeData.getNodeForSkill(unlockedTrees, skill).map(s -> canSkillBeEnabled(skill, s.getTreeConfig().skillTree())).orElse(Result.NOT_FOUND);
    }

    @Override
    public void disableSkill(Holder<? extends ISkill<T>> skill) {
        this.treeData.getNodeForSkill(unlockedTrees, SafeCast.cast(skill)).ifPresent(a  -> disableSkill(skill, a.getTreeConfig().skillTree()));
    }

    @Override
    public void enableSkill(Holder<? extends ISkill<T>> skill) {
        this.treeData.getNodeForSkill(unlockedTrees, SafeCast.cast(skill)).ifPresent(a  -> enableSkill(skill, a.getTreeConfig().skillTree()));
    }

    public void disableAllSkills() {
        for (Map.Entry<Holder<ISkillTree>, List<Holder<? extends ISkill<T>>>> entry : enabledSkills.entrySet()) {
            for (Holder<? extends ISkill<T>> skill : entry.getValue()) {
                FactionEventFactory.fireSkillDisabledEvent(player, skill);
                skill.value().onDisable(player);
            }
        }
        for (var entry : enabledSkills.entrySet()) {
            var root = this.treeData.root(entry.getKey());
            Set<Holder<? extends ISkill<?>>> rootSkills = new HashSet<>(root.elements());
            entry.getValue().removeIf(x -> !rootSkills.contains(x));
        }
    }

    @Override
    public void disableSkill(Holder<? extends ISkill<T>> skill, Holder<ISkillTree> tree) {
        if (enabledSkills.containsKey(tree) && enabledSkills.get(tree).remove(skill)) {
            FactionEventFactory.fireSkillDisabledEvent(player, skill);
            skill.value().onDisable(player);
            player.sync();
        }
    }

    @Override
    public void enableSkill(Holder<? extends ISkill<T>> skill, Holder<ISkillTree>  tree, boolean fromLoading) {
        List<Holder<? extends ISkill<T>>> skills = this.enabledSkills.computeIfAbsent(tree, x -> new ArrayList<>());
        if (!skills.contains(skill)) {
            FactionEventFactory.fireSkillEnableEvent(player, skill, tree, fromLoading);
            skill.value().onEnable(player);
            skills.add(skill);
            if (!fromLoading) {
                this.player.asEntity().awardStat(FactionStats.SKILL_UNLOCKED.get().get(skill.value()));
            }
            //noinspection ConstantValue
            if (this.player.asEntity() instanceof ServerPlayer serverPlayer && serverPlayer.connection != null) {
                FactionAdvancements.TRIGGER_SKILL_UNLOCKED.get().trigger(serverPlayer, skill.value());
            }
            player.sync();
        }

    }

    @Override
    public void updateUnlockedSkillTrees(Collection<Holder<ISkillTree>> skillTrees) {
        List<Holder<ISkillTree>> removedTrees = this.unlockedTrees.stream().filter(x -> !skillTrees.contains(x)).toList();
        removedTrees.forEach(this::lockSkillTree);
        skillTrees.stream().filter(x -> !this.unlockedTrees.contains(x)).forEach(this::unlockSkillTree);
    }

    private void unlockSkillTree(Holder<ISkillTree> tree) {
        this.unlockedTrees.add(tree);
        SkillTreeConfiguration.SkillTreeNodeConfiguration root = this.treeData.root(tree);
        root.elements().forEach(x -> enableSkill(SafeCast.cast(x), tree,true));
    }

    private void lockSkillTree(Holder<ISkillTree> tree) {
        var enabledSkills = new ArrayList<>(this.enabledSkills.getOrDefault(tree, Collections.emptyList()));
        for (Holder<? extends ISkill<T>> enabledSkill : enabledSkills) {
            if (enabledSkill.value().allowedSkillTrees().is(tree)) {
                this.disableSkill(enabledSkill, tree);
            }
        }
        this.unlockedTrees.remove(tree);
    }

    public Collection<Holder<ISkillTree>> unlockedSkillTrees() {
        return Collections.unmodifiableCollection(this.unlockedTrees);
    }

    @Override
    public int getLeftSkillPoints(Holder<ISkillTree> tree) {
        if (this.skillPoints.ignoreSkillPointLimit(this.player, tree)) {
            return Integer.MAX_VALUE;
        }
        return Math.max(0, this.skillPoints.getSkillPoints(this.player, tree) - this.enabledSkills.entrySet().stream().filter(x -> x.getKey().is(tree.value().skillPointTag())).flatMap(s -> s.getValue().stream()).map(Holder::value).mapToInt(ISkill::getSkillPointCost).sum());
    }

    public void reset() {
        disableAllSkills();
        this.unlockedTrees.clear();
    }

    public T getPlayer() {
        return player;
    }

    public boolean noSkillEnabled() {
        var defaultSkills = this.unlockedTrees.stream().collect(Collectors.toMap(x -> x, x -> this.treeData.root(x).elements()));
        //noinspection SuspiciousMethodCalls
        return this.enabledSkills.isEmpty() || this.enabledSkills.entrySet().stream().allMatch(x -> defaultSkills.containsKey(x.getKey()) && new HashSet<>(defaultSkills.get(x.getKey())).containsAll(x.getValue()));
    }

    @SuppressWarnings("unchecked")
    public boolean isNodeEnabled(Holder<ISkillTree> skillTree, ISkillNode node) {
        for (Holder<? extends ISkill<T>> s : enabledSkills.getOrDefault(skillTree, Collections.emptyList())) {
            if (node.containsSkill((Holder<ISkill<?>>) (Object) s)) return true;
        }
        return false;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public boolean areSkillsEnabled(Collection<Holder<? extends ISkill<?>>> skill) {
        return skills.containsAll(skill);
    }

    @Override
    public boolean isSkillEnabled(Holder<? extends ISkill<?>> skill) {
        return this.skills.contains(skill);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @SafeVarargs
    public final boolean isAnySkillEnabled(Holder<? extends ISkill<?>>... skill) {
        return Arrays.stream(skill).anyMatch(skills::contains);
    }

    public boolean isSkillNodeLocked(ISkillNode nodeIn) {
        Registry<ISkillNode> nodes = player.asEntity().level().registryAccess().lookupOrThrow(FactionRegistries.Keys.SKILL_NODE);
        return nodeIn.lockingNodes().stream().flatMap(s -> nodes.getOptional(s).stream()).flatMap(s -> s.skills().stream()).anyMatch(this::isSkillEnabled);
    }

    private static final Codec<Map<Holder<ISkillTree>, List<Holder<? extends ISkill<?>>>>> ENABLED_SKILLS_CODEC = Codec.simpleMap(ISkillTree.CODEC, ISkill.CODEC.listOf().xmap(x -> (List<Holder<? extends ISkill<?>>>)new ArrayList<>(x), x -> x), ModRegistries.SKILLS).codec();

    @Override
    protected void registerProperties() {
        this.registerProperty(FIdentifier.mod("unlocked_trees")).set(RegistryFixedCodec.create(FactionRegistries.Keys.SKILL_TREE)).provider(() -> this.unlockedTrees).commonLoader(x -> CollectionUtil.updateCollection(this.unlockedTrees, x)).register();
        this.registerProperty(FIdentifier.mod("skills")).simple(SafeCast.<Codec<Map<Holder<ISkillTree>, List<Holder<? extends ISkill<T>>>>>>cast(ENABLED_SKILLS_CODEC)).defaultValue(HashMap::new).provider(() -> this.enabledSkills).commonLoader(map -> {
            var old = new HashSet<>(this.skills);
            this.enabledSkills.clear();
            this.enabledSkills.putAll(map);
            return CollectionUtil.checkCollection(old, this.skills, x -> x.value().onDisable(this.player), x -> x.value().onEnable(this.player));
        }).register();
    }

    public void resetSkills() {
        disableAllSkills();
    }

    public static class SkillPoints {
        private final List<ISkillPointProvider> provider;

        public SkillPoints() {
            this.provider = FactionRegistries.SKILL_POINT_PROVIDER.get().stream().toList();
        }

        public int getSkillPoints(IFactionPlayer<?> factionPlayer, Holder<ISkillTree> tree) {
            return this.provider.stream().mapToInt(x -> Math.max(0, x.getSkillPoints(factionPlayer, tree))).sum();
        }

        public boolean ignoreSkillPointLimit(IFactionPlayer<?> factionPlayer, Holder<ISkillTree> tree) {
            return this.provider.stream().anyMatch(l -> l.ignoreSkillPointLimit(factionPlayer, tree));
        }
    }
}
