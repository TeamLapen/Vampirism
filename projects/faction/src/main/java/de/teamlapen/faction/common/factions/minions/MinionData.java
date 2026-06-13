package de.teamlapen.faction.common.factions.minions;

import com.mojang.serialization.Codec;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.util.SafeCast;
import de.teamlapen.faction.api.world.entities.minion.IMinionData;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntry;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ItemStackWithSlot;
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

public abstract class MinionData extends PropertySync implements IMinionData, IAppearanceHolder {

    public static final AppearanceKey<Integer> AppearanceType = AppearancePacket.register(FIdentifier.mod("type"), ByteBufCodecs.VAR_INT);
    public static final AppearanceKey<Integer> SkinType = AppearancePacket.register(FIdentifier.mod("skin"), ByteBufCodecs.VAR_INT);
    public static final AppearanceKey<String> NameType = AppearancePacket.register(FIdentifier.mod("name"), ByteBufCodecs.STRING_UTF8);

    private static final Codec<IMinionTask.IMinionTaskDesc<MinionData>> MINION_TASK_CODEC = SafeCast.cast(IMinionTask.IMinionTaskDesc.TASK_CODEC);
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
    private String name;
    private @NotNull CompoundTag entityCaps = new CompoundTag();
    private MinionStat.StatCollection statCollection;


    @NotNull
    private IMinionTask.IMinionTaskDesc<MinionData> activeTaskDesc;
    private boolean taskLocked;

    protected MinionData(String name, int invSize) {
        this.health = getMaxHealth();
        this.name = name;
        this.inventory = new MinionInventory(invSize);
        this.activeTaskDesc = new IMinionTask.NoDesc<>(FactionMinionTasks.NOTHING.get());
        this.collectStats();
    }

    protected MinionData() {
        this.inventory = new MinionInventory();
        this.activeTaskDesc = new IMinionTask.NoDesc<>(FactionMinionTasks.NOTHING.get());
        this.collectStats();
    }

    private void collectStats() {
        List<MinionStat<?>> stats = new ArrayList<>();
        registerStats(stats::add);
        this.statCollection = new MinionStat.StatCollection(this, stats);
    }

    public abstract int getLevel();


    protected int getMaxStatLevel() {
        return 0;
    }

    protected void registerStats(Consumer<MinionStat<?>> consumer) {

    }

    public int getStatLevel(Identifier identifier) {
        return this.statCollection.getStatLevel(identifier);
    }

    @Override
    public void sync() {
        // this instance itself cannot sync.
    }

    @Override
    @NotNull
    public IMinionTask.IMinionTaskDesc<MinionData> getCurrentTaskDesc() {
        return activeTaskDesc;
    }

    public int getDefaultInventorySize() {
        return 9;
    }

    @Override
    public @NotNull MutableComponent getFormattedName() {
        return Component.literal(name);
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

    public int getInventorySize() {
        return getDefaultInventorySize();
    }

    @Override
    public int getMaxHealth() {
        return (int) EntityProperties.MINION_MAX_HEALTH;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        this.registerProperty(FIdentifier.mod("health")).simple(10, () -> this.health, h -> this.health = h);
        this.registerProperty(FIdentifier.mod("name")).simple("Minion", () -> this.name, n -> this.name = n);
        //noinspection Convert2MethodRef
        this.registerProperty(FIdentifier.mod("inventory_size")).simple(getDefaultInventorySize(), () -> this.inventory.getAvailableSize(), x -> this.inventory.setAvailableSize(x));
        this.registerProperty(FIdentifier.mod("task_locked")).simple(false, () -> this.taskLocked, l -> this.taskLocked = l);
        this.registerProperty(FIdentifier.mod("active_task")).simple(MINION_TASK_CODEC).defaultValue(() -> new IMinionTask.NoDesc<>(FactionMinionTasks.NOTHING.get())).provider(() -> this.activeTaskDesc).clientLoader(x -> {
            var old = this.activeTaskDesc;
            this.activeTaskDesc = x;
            return old.getTask().equals(x.getTask());
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

    @SuppressWarnings("unchecked")
    public <Q extends IMinionTask.IMinionTaskDesc<MinionData>, T extends IMinionTask<Q, ?>> void switchTask(@NotNull T oldTask, IMinionTask.IMinionTaskDesc<MinionData> oldDesc, IMinionTask.@NotNull IMinionTaskDesc<MinionData> newDesc) {
        oldTask.deactivateTask((Q) oldDesc);
        this.activeTaskDesc = newDesc;
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
}
