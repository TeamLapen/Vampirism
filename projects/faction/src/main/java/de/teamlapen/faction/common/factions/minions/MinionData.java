package de.teamlapen.faction.common.factions.minions;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.entities.ICustomizationOption;
import de.teamlapen.faction.api.world.entities.minion.*;
import de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTask;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.core.FactionItems;
import de.teamlapen.faction.common.core.FactionMinionTasks;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.faction.common.world.entities.EntityProperties;
import de.teamlapen.faction.common.world.entities.appearance.AppearanceKey;
import de.teamlapen.faction.common.world.entities.appearance.AppearancePacket;
import de.teamlapen.faction.common.world.entities.appearance.IAppearanceHolder;
import de.teamlapen.faction.common.world.entities.customization.ICustomizable;
import de.teamlapen.faction.common.world.inventory.InventoryHelper;
import de.teamlapen.sync.SimpleMutableDataComponentMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MinionData extends SimpleMutableDataComponentMap implements IMinionData, DataComponentMap {

    public static final int MAX_NAME_LENGTH = 15;
    protected static final Logger LOGGER = LogManager.getLogger();

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
        return this.minionEntry.minionStats().stream().mapToInt(x -> getOrDefault(x.getIdentifier(), 0)).sum() < getLevel();
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
        this.minionEntry.minionStats().forEach(x -> remove(x.getIdentifier().get()));
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

//    public void shrinkInventory(@NotNull MinionEntity entity) {
//        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
//        Optional<MinionInventory> invOpt = entity.getMinionData().map(MinionData::getInventory);
//        if (invOpt.isPresent()) {
//            MinionInventory inv = invOpt.get();
//            List<ItemStack> stacks = new ArrayList<>();
//            for (int i = 6 + getDefaultInventorySize(); i < inv.getContainerSize(); ++i) {
//                ItemStack stack = inv.removeItemNoUpdate(i);
//                if (!stack.isEmpty()) {
//                    stacks.add(stack);
//                }
//            }
//            for (ItemStack stack : stacks) {
//                if (!stack.isEmpty()) {
//                    inv.addItemStack(stack);
//                    if (!stack.isEmpty()) {
//                        entity.getLordOpt().ifPresent(lord -> {
//                            if (!lord.asEntity().addItem(stack)) {
//                                entity.spawnAtLocation(serverLevel, stack);
//                            }
//                        });
//                    }
//                }
//            }
//        }
//    }

    public boolean switchTask(@Nullable Player player, @Nullable MinionEntity minion, @NotNull Holder<? extends IMinionTask<?>> task) {
        var result = task.value().activateTask(player, minion, this);
        if (result.successful()) {
            ((IMinionTask)this.activeTaskDesc.task().value()).deactivateTask(this.activeTaskDesc.data());
            this.activeTaskDesc = new ActiveTask(task, result.data());
            return true;
        } else if (player != null) {
            player.sendOverlayMessage(Component.translatable("message.factionapi.minion_task.could_not_activate"));
        }
        return false;
    }

    public int getRemainingStatPoints() {
        return Math.max(0, getLevel() - this.minionEntry.minionStats().stream().mapToInt(x -> getOrDefault(x.getIdentifier(), 0)).sum());
    }

    public boolean upgradeStat(@NotNull Holder<DataComponentType<?>> statId, @NotNull MinionEntity entity) {
        MinionStat minionStat = this.minionEntry.minionStat(statId);
        if (minionStat != null) {
            int currentStat = getOrDefault(minionStat.getIdentifier(), 0);
            int nextStat = Math.min(currentStat + 1, minionStat.getMaxLevel());
            if (nextStat <= minionStat.getMaxLevel()) {
                minionStat.apply(nextStat, entity, this);
                set(minionStat.getIdentifier(), nextStat);
                return true;
            }
        }
        return false;
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
        return entry.map(x -> {
            var data = new MinionData(x);
            data.deserialize(input);
            return data;
        });
    }

    @Override
    protected void onPropertyChanged() {
        super.onPropertyChanged();
    }

    public static void toCompound(MinionData data, ValueOutput output) {
        output.store("entry", ModRegistries.MINIONS.byNameCodec(), data.minionEntry);
        data.serialize(output);
    }
}
