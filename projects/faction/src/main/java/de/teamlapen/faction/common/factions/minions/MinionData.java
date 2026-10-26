package de.teamlapen.faction.common.factions.minions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.factions.tasks.Task;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.entities.minion.*;
import de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTask;
import de.teamlapen.faction.common.core.*;
import de.teamlapen.faction.common.factions.minions.management.MinionTask;
import de.teamlapen.faction.common.world.entities.EntityProperties;
import de.teamlapen.faction.common.world.entities.minion.MinionStats;
import de.teamlapen.faction.common.world.inventory.InventoryHelper;
import de.teamlapen.sync.SimpleMutableDataComponentMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MinionData extends SimpleMutableDataComponentMap implements IMinionData, DataComponentMap {

    public static final int MAX_NAME_LENGTH = 15;
    protected static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier STATS = FIdentifier.mod("minion_stats");
    /**
     * Maps the legacy stat ids to the current stats
     */
    private static final Map<String, MinionStat> LEGACY_STATS = Map.of(
            "inventory", MinionStats.INVENTORY_STATS,
            "health", MinionStats.HEALTH_STATS,
            "strength", MinionStats.ATTACK_DAMAGE_STATS,
            "speed", MinionStats.SPEED_STATS,
            "resources", MinionStats.RESOURCES_STATS
    );

    private final @NotNull MinionInventory inventory;
    private float health;
    private @NotNull CompoundTag entityCaps = new CompoundTag();
    private final IMinionEntry<?> minionEntry;

    @NotNull
    private ActiveTask<?> activeTaskDesc;
    private boolean taskLocked;

    public MinionData(IMinionEntry<?> entry) {
        this.minionEntry = entry;
        this.inventory = new MinionInventory();
        this.activeTaskDesc = ActiveTask.DEFAULT;
        this.health = getMaxHealth();
    }

    @Override
    public int getLevel() {
        return getOrDefault(FactionDataComponents.MINION_LEVEL,0);
    }

    public boolean setLevel(int level) {
        int oldLevel = getLevel();
        level = Math.clamp(level, 0, getMaxLevel());
        set(FactionDataComponents.MINION_LEVEL, level);
        return level > oldLevel;
    }

    public int getMaxLevel() {
        return this.minionEntry.maxLevel();
    }

    @Override
    @NotNull
    public ActiveTask<?> getActiveTask() {
        return activeTaskDesc;
    }

    public void setIncreasedStats(boolean hasIncreasedStats) {
        set(FactionDataComponents.MINION_HAS_INCREASED_STATS, hasIncreasedStats);
    }

    public boolean hasIncreasedStats() {
        return has(FactionDataComponents.MINION_HAS_INCREASED_STATS);
    }

    @Override
    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    @Override
    public @NotNull MinionInventory getInventory() {
        return inventory;
    }

    @Override
    public int getMaxHealth() {
        return (int) EntityProperties.MINION_MAX_HEALTH;
    }

    @Override
    public @NotNull String getName() {
        return getOrDefault(FactionDataComponents.MINION_NAME, "Minion");
    }

    public void setName(String name) {
        this.set(FactionDataComponents.MINION_NAME, name);
    }

    public IMinionEntry<?> getMinionEntry() {
        return minionEntry;
    }

    @Override
    public boolean hasUsedSkillPoints() {
        return getUsedStatPoints() > 0;
    }

    private int getUsedStatPoints() {
        return this.minionEntry.minionStats().stream().mapToInt(x -> getOrDefault(x.getIdentifier(), 0)).sum();
    }

    public boolean isTaskLocked() {
        return taskLocked;
    }

    public @NotNull CompoundTag getEntityCaps() {
        return entityCaps;
    }

    public void updateEntityCaps(CompoundTag caps) {
        this.entityCaps = caps;
    }

    public void resetStats(@NotNull MinionEntity entity) {
        entity.getInventory().ifPresent(inv -> {
            if (!InventoryHelper.removeItemFromInventory(inv, new ItemStack(FactionItems.OBLIVION_POTION.get()))) {
                entity.getLordOpt().ifPresent(lord -> InventoryHelper.removeItemFromInventory(lord.asEntity().getInventory(), new ItemStack(FactionItems.OBLIVION_POTION.get())));
            }
        });
        this.minionEntry.minionStats().forEach(x -> {
            x.apply(0, entity, this); // let the stat revert its side effects (e.g. inventory slots)
            remove(x.getIdentifier().get());
        });
        shrinkInventory(entity);
        entity.updateAttributes();
    }

    @MustBeInvokedByOverriders
    @Override
    public void serialize(@NotNull ValueOutput output) {
        super.serialize(output);
        inventory.write(output.list("inv", ItemStackWithSlot.CODEC));
        output.store("caps", CompoundTag.CODEC, this.entityCaps);
    }

    @MustBeInvokedByOverriders
    @Override
    public void deserialize(@NotNull ValueInput input) {
        super.deserialize(input);
        this.inventory.read(input.listOrEmpty("inv", ItemStackWithSlot.CODEC));
        this.entityCaps = input.read("caps", CompoundTag.CODEC).orElse(new CompoundTag());
    }


    @Override
    protected void registerProperties() {
        super.registerProperties();
        this.registerProperty(FIdentifier.mod("health")).simple(10, () -> this.health, h -> this.health = h);
        this.registerProperty(FIdentifier.mod("task_locked")).simple(false, () -> this.taskLocked, l -> this.taskLocked = l);
        this.registerProperty(FIdentifier.mod("active_task")).simple(ActiveTask.CODEC).defaultValue(() -> ActiveTask.DEFAULT).provider(() -> this.activeTaskDesc).clientLoader(x -> {
            var old = this.activeTaskDesc;
            this.activeTaskDesc = x;
            return old.equals(x);
        }).register();
    }

    public boolean setTaskLocked(boolean locked) {
        return this.taskLocked = locked;
    }

    protected void applyBaseStats(MinionEntity entity) {
        for (MinionStat minionStat : this.minionEntry.minionStats()) {
            if (minionStat instanceof MinionStat.AttributeBased attributeBased) {
                AttributeInstance attribute = entity.getAttribute(attributeBased.getAttribute());
                if (attribute != null) {
                    attribute.addOrReplacePermanentModifier(new AttributeModifier(STATS, getStatBonus(attributeBased), AttributeModifier.Operation.ADD_VALUE));
                }
            }
        }
    }

    private double getStatBonus(MinionStat.AttributeBased stat) {
        return stat.perLevelValue() * stat.currentLevel(this) * (hasIncreasedStats() ? 1.2 : 1);
    }

    /**
     * Calculates the value of an attribute without requiring the minion entity, e.g. for tasks that are ticked while the minion is not loaded.
     * Assumes that the minion entity uses the default value of the attribute as base value.
     *
     * @return The default value of the attribute plus the bonus of all stats of this minion that modify the attribute
     */
    public double getAttributeValue(Holder<Attribute> attribute) {
        double value = attribute.value().getDefaultValue();
        for (MinionStat minionStat : this.minionEntry.minionStats()) {
            if (minionStat instanceof MinionStat.AttributeBased attributeBased && attributeBased.getAttribute().value() == attribute.value()) {
                value += getStatBonus(attributeBased);
            }
        }
        return attribute.value().sanitizeValue(value);
    }

    /**
     * Moves items from main inventory slots that are no longer available into free slots. Items that do not fit are given to the lord or dropped.
     */
    public void shrinkInventory(@NotNull MinionEntity entity) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
        for (ItemStack stack : this.inventory.removeUnavailableItems()) {
            this.inventory.addItemStack(stack);
            if (!stack.isEmpty()) {
                Optional<Player> lord = entity.getLordOpt().map(ILordPlayer::asEntity);
                if (lord.isEmpty() || !lord.get().addItem(stack)) {
                    entity.spawnAtLocation(serverLevel, stack);
                }
            }
        }
    }

    public boolean switchTask(@Nullable Player player, @Nullable MinionEntity minion, @NotNull Holder<? extends IMinionTask<?>> task) {
        var result = task.value().activateTask(player, minion, this);
        if (result.successful()) {
            ((IMinionTask)this.activeTaskDesc.task().value()).deactivateTask(this.activeTaskDesc.data());
            this.activeTaskDesc = new ActiveTask(task, result.data());
            if (player instanceof ServerPlayer) {
                FactionAdvancements.TRIGGER_MINION_ACTION.get().trigger(((ServerPlayer) player), task.value());
            }
            return true;
        } else if (player != null) {
            player.sendOverlayMessage(Component.translatable("message.factionapi.minion_task.could_not_activate"));
        }
        return false;
    }

    public int getRemainingStatPoints() {
        return Math.max(0, getLevel() - getUsedStatPoints());
    }

    public boolean upgradeStat(@NotNull Holder<DataComponentType<?>> statId, @NotNull MinionEntity entity) {
        MinionStat minionStat = this.minionEntry.minionStat(statId);
        if (minionStat == null || getRemainingStatPoints() <= 0) {
            return false;
        }
        int currentStat = getOrDefault(minionStat.getIdentifier(), 0);
        if (currentStat >= minionStat.getMaxLevel()) {
            return false;
        }
        minionStat.apply(currentStat + 1, entity, this);
        entity.updateAttributes();
        return true;
    }

    public record ActiveTask<TState extends IMinionTask.IMinionTaskState>(Holder<? extends IMinionTask<TState>> task, TState data) implements IActiveTask<TState> {

        public static final ActiveTask<?> DEFAULT = new ActiveTask(FactionMinionTasks.NOTHING, IMinionTask.EmptyState.INSTANCE);
        public static final Codec<ActiveTask<?>> CODEC = ModRegistries.MINION_TASKS.holderByNameCodec().dispatch("task", ActiveTask::holder, ActiveTask::stateCodec);

        @SuppressWarnings("unchecked")
        private Holder<IMinionTask<?>> holder() {
            return (Holder<IMinionTask<?>>) (Object) this.task;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private static MapCodec<ActiveTask<?>> stateCodec(Holder<IMinionTask<?>> holder) {
            Holder<IMinionTask<IMinionTask.IMinionTaskState>> task = (Holder) holder;
            Codec<IMinionTask.IMinionTaskState> stateCodec = task.value().stateCodec();
            if (stateCodec == null) {
                return MapCodec.unit(() -> new ActiveTask<>(task, null));
            }
            return stateCodec.fieldOf("state").xmap(state -> new ActiveTask<>(task, state), ActiveTask::data);
        }
    }

    public static Optional<MinionData> fromCompound(ValueInput input) {
        Optional<IMinionEntry<?>> entry = input.read("entry", ModRegistries.MINIONS.byNameCodec());
        if (entry.isEmpty()) {
            return fromLegacyCompound(input);
        }
        return entry.map(x -> {
            var data = new MinionData(x);
            data.deserialize(input);
            return data;
        });
    }

    /**
     * Reads minion data that was saved before minion values were stored as data components.
     * Back then the entry was stored as "data_type" and the minion specific values were plain properties, namespaced by the mod that registered the minion entry.
     * The active task can not be converted and is reset.
     */
    private static Optional<MinionData> fromLegacyCompound(ValueInput input) {
        return input.read("data_type", ModRegistries.MINIONS.byNameCodec()).map(entry -> {
            var data = new MinionData(entry);
            data.deserialize(input);

            String prefix = Objects.requireNonNull(ModRegistries.MINIONS.getKey(entry)).getNamespace() + ":";
            input.read(FIdentifier.mod("name").toString(), Codec.STRING).ifPresent(data::setName);
            input.read(prefix + "level", Codec.INT).ifPresent(data::setLevel);
            input.read(prefix + "type", Codec.INT).ifPresent(type -> data.set(FactionDataComponents.MINION_SKIN, Math.max(0, type)));
            input.read(prefix + "use_lord_skin", Codec.BOOL).ifPresent(x -> data.set(FactionDataComponents.MINION_USE_LORD_SKIN, x));
            input.read(prefix + "minion_skin", Codec.BOOL).ifPresent(x -> data.set(FactionDataComponents.MINION_USE_MINION_SKIN, x));
            input.read(prefix + "has_increased_stats", Codec.BOOL).ifPresent(data::setIncreasedStats);

            String statsKey = FIdentifier.mod("stats").toString();
            input.childOrEmpty(statsKey).read(statsKey, Codec.unboundedMap(Identifier.CODEC, Codec.INT)).orElse(Map.of()).forEach((statId, level) -> {
                MinionStat stat = LEGACY_STATS.get(statId.getPath());
                if (stat != null && entry.minionStats().contains(stat)) {
                    stat.apply(Math.clamp(level, 0, stat.getMaxLevel()), null, data);
                }
            });
            LOGGER.info("Converted legacy minion data of {}", data.getName());
            return data;
        });
    }


    @Override
    protected void onPropertyChanged() {
        super.onPropertyChanged();
        // inventory size is derived from the data components, so refresh it after load (server) and sync (client)
        this.inventory.updateFromData(this);
    }

    public static void toCompound(MinionData data, ValueOutput output) {
        output.store("entry", ModRegistries.MINIONS.byNameCodec(), data.minionEntry);
        data.serialize(output);
    }
}
