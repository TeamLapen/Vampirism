package de.teamlapen.factions.common.minions;

import de.teamlapen.factions.api.entities.minion.IMinionData;
import de.teamlapen.factions.api.entities.minion.IMinionEntry;
import de.teamlapen.factions.api.entities.minion.IMinionTask;
import de.teamlapen.factions.common.core.FactionItems;
import de.teamlapen.factions.common.core.FactionMinionTasks;
import de.teamlapen.factions.common.core.ModRegistries;
import de.teamlapen.factions.common.entities.EntityProperties;
import de.teamlapen.factions.common.inventory.InventoryHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class MinionData implements ValueIOSerializable, IMinionData {


    public final static int MAX_NAME_LENGTH = 15;
    protected final static Logger LOGGER = LogManager.getLogger();

    @Nullable
    public static <T extends MinionData> T fromNBT(ValueInput input) {
        return input.read("data_type", ResourceLocation.CODEC).map(ModRegistries.MINIONS::getValue).map(IMinionEntry::data).map(Supplier::get).map(x -> {
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
    @NotNull
    public IMinionTask.IMinionTaskDesc<MinionData> getCurrentTaskDesc() {
        return activeTaskDesc;
    }

    public int getDefaultInventorySize() {
        return 9;
    }

    @Override
    public MutableComponent getFormattedName() {
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
    public MinionInventory getInventory() {
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
    public String getName() {
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
                entity.getLordOpt().ifPresent(lord -> InventoryHelper.removeItemFromInventory(lord.getPlayer().getInventory(), new ItemStack(FactionItems.OBLIVION_POTION.get())));
            }
        });
    }

    @MustBeInvokedByOverriders
    @Override
    public void serialize(ValueOutput output) {
        output.putInt("inv_size", inventory.getAvailableSize());
        inventory.write(output.list("inv", ItemStackWithSlot.CODEC));
        output.putFloat("health", health);
        output.putString("name", name);
        output.store("data_type", ResourceLocation.CODEC, getDataType());
        output.putBoolean("locked", taskLocked);
        var task = output.child("task");
        task.store("task", ModRegistries.MINION_TASKS.byNameCodec(), activeTaskDesc.getTask());
        this.activeTaskDesc.serialize(task.child("desc"));
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

        input.child("task").ifPresent(tasksum -> {
            tasksum.read("task", ModRegistries.MINION_TASKS.byNameCodec()).ifPresent(task -> {
                this.activeTaskDesc = tasksum.child("desc").map(x -> (IMinionTask.IMinionTaskDesc<MinionData>) task.load(x)).orElseGet(() -> new IMinionTask.NoDesc<>(FactionMinionTasks.NOTHING.get()));
            });
        });
        this.entityCaps = input.read("caps", CompoundTag.CODEC).orElse(new CompoundTag());
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
                            if (!lord.getPlayer().addItem(stack)) {
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

    protected ResourceLocation getDataType() {
        return ResourceLocation.withDefaultNamespace("");
    }
}
