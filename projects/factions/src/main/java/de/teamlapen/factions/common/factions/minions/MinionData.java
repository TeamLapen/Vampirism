package de.teamlapen.factions.common.factions.minions;

import com.mojang.serialization.Codec;
import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.api.util.SafeCast;
import de.teamlapen.factions.api.world.entities.minion.IMinionData;
import de.teamlapen.factions.api.world.entities.minion.IMinionEntry;
import de.teamlapen.factions.api.world.entities.minion.IMinionTask;
import de.teamlapen.factions.common.core.FactionItems;
import de.teamlapen.factions.common.core.FactionMinionTasks;
import de.teamlapen.factions.common.core.ModRegistries;
import de.teamlapen.factions.common.world.entities.EntityProperties;
import de.teamlapen.factions.common.world.inventory.InventoryHelper;
import de.teamlapen.sync.PropertySync;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class MinionData extends PropertySync implements IMinionData {

    private static final Codec<IMinionTask.IMinionTaskDesc<MinionData>> MINION_TASK_CODEC = SafeCast.cast(IMinionTask.IMinionTaskDesc.TASK_CODEC);
    public static final int MAX_NAME_LENGTH = 15;
    protected static final Logger LOGGER = LogManager.getLogger();

    @Nullable
    public static <T extends MinionData> T fromNBT(ValueInput input) {
        return input.read("data_type", Identifier.CODEC).map(ModRegistries.MINIONS::getValue).map(IMinionEntry::data).map(Supplier::get).map(x -> {
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


    @NotNull
    private IMinionTask.IMinionTaskDesc<MinionData> activeTaskDesc;
    private boolean taskLocked;

    protected MinionData(String name, int invSize) {
        this.health = getMaxHealth();
        this.name = name;
        this.inventory = new MinionInventory(invSize);
        this.activeTaskDesc = new IMinionTask.NoDesc<>(FactionMinionTasks.NOTHING.get());
    }

    protected MinionData() {
        this.inventory = new MinionInventory();
        this.activeTaskDesc = new IMinionTask.NoDesc<>(FactionMinionTasks.NOTHING.get());
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

    public void handleMinionAppearanceConfig(String name, List<Integer> data) {
    }

    public boolean hasUsedSkillPoints() {
        return false;
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
    }

    @MustBeInvokedByOverriders
    @Override
    public void serialize(@NotNull ValueOutput output) {
        output.putInt("inv_size", this.inventory.getAvailableSize());
        inventory.write(output.list("inv", ItemStackWithSlot.CODEC));
        output.putFloat("health", this.health);
        output.putString("name", this.name);
        output.store("data_type", Identifier.CODEC, getDataType());
        output.putBoolean("locked", this.taskLocked);
        output.store("task", MINION_TASK_CODEC, this.activeTaskDesc);
        output.store("caps", CompoundTag.CODEC, this.entityCaps);
    }

    @MustBeInvokedByOverriders
    @Override
    public void deserialize(@NotNull ValueInput input) {
        this.inventory.read(input.listOrEmpty("inv", ItemStackWithSlot.CODEC));
        input.getInt("inv_size").ifPresent(this.inventory::setAvailableSize);
        this.health = input.getFloatOr("health", 10);
        input.getString("name").ifPresent(x -> this.name = x);
        this.taskLocked = input.getBooleanOr("locked", false);

        this.activeTaskDesc = input.read("task", MINION_TASK_CODEC).orElseGet(() -> new IMinionTask.NoDesc<>(FactionMinionTasks.NOTHING.get()));
        this.entityCaps = input.read("caps", CompoundTag.CODEC).orElse(new CompoundTag());
    }


    @Override
    protected void registerProperties() {
        this.registerProperty(FResourceLocation.mod("health")).simple(10, () -> this.health, h -> this.health = h);
        this.registerProperty(FResourceLocation.mod("name")).simple("Minion", () -> this.name, n -> this.name = n);
        //noinspection Convert2MethodRef
        this.registerProperty(FResourceLocation.mod("inventory_size")).simple(getDefaultInventorySize(), () -> this.inventory.getAvailableSize(), x -> this.inventory.setAvailableSize(x));
        this.registerProperty(FResourceLocation.mod("task_locked")).simple(false, () -> this.taskLocked, l -> this.taskLocked = l);
        this.registerProperty(FResourceLocation.mod("active_task")).simple(MINION_TASK_CODEC).defaultValue(() -> new IMinionTask.NoDesc<>(FactionMinionTasks.NOTHING.get())).provider(() -> this.activeTaskDesc).clientLoader(x -> {
            var old = this.activeTaskDesc;
            this.activeTaskDesc = x;
            return old.getTask().equals(x.getTask());
        }).register();
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
            inv.setAvailableSize(getInventorySize());
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

    /**
     * Called on server side to upgrade a stat of the given id
     * <p>
     *
     * @param statId values:<br>
     *               -1: reset all stats<br>
     *               -2: update attributes<br>
     * @return if attributes where changed and a sync is required
     */
    public boolean upgradeStat(int statId, @NotNull MinionEntity<?> entity) {
        if (statId == -1) {
            resetStats(entity);
            return true;
        }
        return false;
    }

    protected abstract Identifier getDataType();
}
