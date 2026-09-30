package de.teamlapen.faction.common.factions.minions;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import de.teamlapen.faction.api.FactionDataComponents;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.util.SafeCast;
import de.teamlapen.faction.api.world.entities.minion.IMinionData;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntry;
import de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTask;
import de.teamlapen.faction.common.core.FactionItems;
import de.teamlapen.faction.common.core.FactionMinionTasks;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.faction.common.factions.minions.stats.MinionStat;
import de.teamlapen.faction.common.world.entities.EntityProperties;
import de.teamlapen.faction.common.world.entities.appearance.AppearanceKey;
import de.teamlapen.faction.common.world.entities.appearance.AppearancePacket;
import de.teamlapen.faction.common.world.entities.appearance.IAppearanceHolder;
import de.teamlapen.faction.common.world.inventory.InventoryHelper;
import de.teamlapen.sync.PropertySync;
import de.teamlapen.sync.SimpleMutableDataComponentMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ItemStackWithSlot;
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

public class MinionData extends SimpleMutableDataComponentMap implements IMinionData, IAppearanceHolder, DataComponentMap {

    public static final AppearanceKey<Integer> AppearanceType = AppearancePacket.register(FIdentifier.mod("type"), ByteBufCodecs.VAR_INT);
    public static final AppearanceKey<Integer> SkinType = AppearancePacket.register(FIdentifier.mod("skin"), ByteBufCodecs.VAR_INT);
    public static final AppearanceKey<String> NameType = AppearancePacket.register(FIdentifier.mod("name"), ByteBufCodecs.STRING_UTF8);

    public static final int MAX_NAME_LENGTH = 15;
    protected static final Logger LOGGER = LogManager.getLogger();

    @Nullable
    public static <T extends MinionData> T fromNBT(ValueInput input) {
        return input.read("data_type", Identifier.CODEC).map(ModRegistries.MINIONS::getValue).map(IMinionEntry::data).map(IMinionEntry.IMinionCreator::create).map(x -> {
            try {
                @SuppressWarnings("unchecked")
                T t = (T) x;
                t.deserialize(input);
                return t;
            } catch (ClassCastException ex) {
                return null;
            }
        }).orElse(null);
    }

    private final @NotNull MinionInventory inventory;
    private float health;
    private @NotNull CompoundTag entityCaps = new CompoundTag();
    private MinionStat.StatCollection statCollection;


    @NotNull
    private ActiveTask<?> activeTaskDesc;
    private boolean taskLocked;

    protected MinionData(String name, int invSize) {
        this.health = getMaxHealth();
        this.inventory = new MinionInventory(invSize);
        this.activeTaskDesc = ActiveTask.DEFAULT;
        this.collectStats();
    }

    protected MinionData() {
        this.inventory = new MinionInventory();
        this.activeTaskDesc = ActiveTask.DEFAULT;
        this.collectStats();
    }

    private void collectStats() {
        List<MinionStat<?>> stats = new ArrayList<>();
        registerStats(stats::add);
        this.statCollection = new MinionStat.StatCollection(this, stats);
    }

    public int getLevel() {
        return getOrDefault(FactionDataComponents.MINION_LEVEL,0);
    }


    protected int getMaxStatLevel() {
        return 0;
    }

    protected void registerStats(Consumer<MinionStat<?>> consumer) {

    }

    public int getStatLevel(Identifier identifier) {
        return this.statCollection.getStatLevel(identifier);
    }

    @Override
    @NotNull
    public ActiveTask<?> getActiveTask() {
        return activeTaskDesc;
    }

    public int getDefaultInventorySize() {
        return 9;
    }

    public abstract void setIncreasedStats(boolean hasIncreasedStats);

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

    public int getInventorySize() {
        return getDefaultInventorySize();
    }

    @Override
    public int getMaxHealth() {
        return (int) EntityProperties.MINION_MAX_HEALTH;
    }

    @Override
    public @NotNull Component getName() {
        return getOrDefault(FactionDataComponents.MINION_NAME, Component.literal("Minion"));
    }

    public void setName(Component name) {
        this.set(FactionDataComponents.MINION_NAME, name);
    }

    public <T> void setAppearanceData(@NonNull AppearanceKey<T> id, @NonNull T data) {
        if (id.equals(NameType)) {
            setName((String) data);
        }
    }

    public boolean hasUsedSkillPoints() {
        return this.statCollection.getLevels() > 0;
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

    public void resetStats(@NotNull MinionEntity<?> entity) {
        entity.getInventory().ifPresent(inv -> {
            if (!InventoryHelper.removeItemFromInventory(inv, new ItemStack(FactionItems.OBLIVION_POTION.get()))) {
                entity.getLordOpt().ifPresent(lord -> InventoryHelper.removeItemFromInventory(lord.asEntity().getInventory(), new ItemStack(FactionItems.OBLIVION_POTION.get())));
            }
        });
        this.statCollection.reset(entity, this);
    }

    @MustBeInvokedByOverriders
    @Override
    public void serialize(@NotNull ValueOutput output) {
        super.serialize(output);
        inventory.write(output.list("inv", ItemStackWithSlot.CODEC));
        output.store("data_type", Identifier.CODEC, getDataType());
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
        //noinspection Convert2MethodRef
        this.registerProperty(FIdentifier.mod("inventory_size")).simple(getDefaultInventorySize(), () -> this.inventory.getAvailableSize(), x -> this.inventory.setAvailableSize(x));
        this.registerProperty(FIdentifier.mod("task_locked")).simple(false, () -> this.taskLocked, l -> this.taskLocked = l);
        this.registerProperty(FIdentifier.mod("active_task")).simple(ActiveTask.CODEC).defaultValue(() -> ActiveTask.DEFAULT).provider(() -> this.activeTaskDesc).clientLoader(x -> {
            var old = this.activeTaskDesc;
            this.activeTaskDesc = x;
            return old.equals(x);
        }).register();
        this.registerProperty(FIdentifier.mod("stats")).subProperty(() -> this.statCollection).register();
    }

    public boolean setTaskLocked(boolean locked) {
        return this.taskLocked = locked;
    }

    public void shrinkInventory(@NotNull MinionEntity<?> entity) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
        Optional<MinionInventory> invOpt = entity.getMinionData().map(MinionData::getInventory);
        if (invOpt.isPresent()) {
            MinionInventory inv = invOpt.get();
            List<ItemStack> stacks = new ArrayList<>();
            for (int i = 6 + getDefaultInventorySize(); i < inv.getContainerSize(); ++i) {
                ItemStack stack = inv.removeItemNoUpdate(i);
                if (!stack.isEmpty()) {
                    stacks.add(stack);
                }
            }
            for (ItemStack stack : stacks) {
                if (!stack.isEmpty()) {
                    inv.addItemStack(stack);
                    if (!stack.isEmpty()) {
                        entity.getLordOpt().ifPresent(lord -> {
                            if (!lord.asEntity().addItem(stack)) {
                                entity.spawnAtLocation(serverLevel, stack);
                            }
                        });
                    }
                }
            }
        }
    }

    public boolean switchTask(@Nullable Player player, @Nullable MinionEntity<?> minion, @NotNull Holder<IMinionTask<?, ?>> task) {
        var result = task.value().activateTask(player, minion, this);
        if (result.successful()) {
            this.activeTaskDesc.task().value().deactivateTask(this.activeTaskDesc.data());
            this.activeTaskDesc = new ActiveTask<>(task, result.data());
            return true;
        } else if (player != null) {
            player.sendOverlayMessage(Component.translatable("message.factionapi.minion_task.could_not_activate"));
        }
        return false;
    }

    public int getRemainingStatPoints() {
        return Math.max(0, getLevel() - this.statCollection.getLevels());
    }

    public boolean upgradeStat(@NotNull Identifier statId, @NotNull MinionEntity<?> entity) {
        if (this.statCollection.getLevels() >= getLevel()) {
            return false;
        }
        return this.statCollection.upgrade(statId, entity, this);
    }

    protected abstract Identifier getDataType();

    public record ActiveTask<TState extends IMinionTask.IMinionTaskState>(Holder<? extends IMinionTask<?, TState>> task, TState data) implements IActiveTask<TState> {

        public static final ActiveTask<?> DEFAULT = new ActiveTask<IMinionTask.IMinionTaskState>(FactionMinionTasks.NOTHING, IMinionTask.EmptyState.INSTANCE);
        public static final Codec<ActiveTask<?>> CODEC = ModRegistries.MINION_TASKS.holderByNameCodec().dispatch("task", ActiveTask::holder, ActiveTask::stateCodec);

        @SuppressWarnings("unchecked")
        private Holder<IMinionTask<?, ?>> holder() {
            return (Holder<IMinionTask<?, ?>>) this.task;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private static MapCodec<ActiveTask<?>> stateCodec(Holder<IMinionTask<?, ?>> holder) {
            Holder<IMinionTask<MinionData, IMinionTask.IMinionTaskState>> task = (Holder) holder;
            Codec<IMinionTask.IMinionTaskState> stateCodec = task.value().stateCodec();
            if (stateCodec == null) {
                return MapCodec.unit(() -> new ActiveTask<>(task, null));
            }
            return stateCodec.fieldOf("state").xmap(state -> new ActiveTask<>(task, state), ActiveTask::data);
        }
    }
}
