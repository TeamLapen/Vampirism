package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.vampirism.api.entity.minion.IMinionData;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;

public class MinionData implements INBTSerializable<CompoundTag>, IMinionData {


    public final static int MAX_NAME_LENGTH = 15;
    protected final static Logger LOGGER = LogManager.getLogger();
    private final static Map<ResourceLocation, Supplier<? extends MinionData>> constructors = new HashMap<>(); //TODO maybe API


    public static void registerDataType(ResourceLocation id, Supplier<? extends MinionData> supplier) {
        constructors.put(id, supplier);
    }

    @Nonnull
    public static MinionData fromNBT(CompoundTag nbt) {
        ResourceLocation dataType = new ResourceLocation(nbt.getString("data_type"));
        Supplier<? extends MinionData> c = constructors.get(dataType);
        if (c == null) {
            LOGGER.error("No data constructor available for {}", dataType);
            return new MinionData();
        }

        MinionData d = c.get();
        d.deserializeNBT(nbt);
        return d;
    }

    private final MinionInventory inventory;
    private float health;
    private String name;


    @Nonnull
    private IMinionTask.IMinionTaskDesc<MinionData> activeTaskDesc;
    private boolean taskLocked;

    protected MinionData(String name, int invSize) {
        this.health = getMaxHealth();
        this.name = name;
        this.inventory = new MinionInventory(invSize);
        this.activeTaskDesc = new IMinionTask.NoDesc<>(MinionTasks.nothing);
    }

    protected MinionData() {
        this.inventory = new MinionInventory();
        this.activeTaskDesc = new IMinionTask.NoDesc<>(MinionTasks.nothing);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        inventory.read(nbt.getList("inv", 10));
        inventory.setAvailableSize(nbt.getInt("inv_size"));
        health = nbt.getFloat("health");
        name = nbt.getString("name");
        taskLocked = nbt.getBoolean("locked");
        if (nbt.contains("task", 10)) {
            CompoundTag task = nbt.getCompound("task");
            ResourceLocation id = new ResourceLocation(task.getString("id"));
            IMinionTask<?, MinionData> activeTask = (IMinionTask<?, MinionData>) ModRegistries.MINION_TASKS.getValue(id);
            if (activeTask != null) {
                activeTaskDesc = activeTask.readFromNBT(task);
            } else {
                LOGGER.error("Saved minion task does not exist anymore {}", id);
            }
        }
    }

    @Override
    @Nonnull
    public IMinionTask.IMinionTaskDesc<MinionData> getCurrentTaskDesc() {
        return activeTaskDesc;
    }

    public int getDefaultInventorySize() {
        return 9;
    }

    @Override
    public MutableComponent getFormattedName() {
        return new TextComponent(name);
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
        return (int) BalanceMobProps.mobProps.MINION_MAX_HEALTH;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void handleMinionAppearanceConfig(String name, int... data) {
    }

    public boolean hasUsedSkillPoints() {
        return false;
    }

    public boolean isTaskLocked() {
        return taskLocked;
    }

    public void resetStats(MinionEntity<?> entity) {
        entity.getInventory().ifPresent(inv -> {
            if (!InventoryHelper.removeItemFromInventory(inv, new ItemStack(ModItems.oblivion_potion.get()))) {
                entity.getLordOpt().ifPresent(lord -> InventoryHelper.removeItemFromInventory(lord.getPlayer().getInventory(), new ItemStack(ModItems.oblivion_potion.get())));
            }
        });
        HelperLib.sync(entity);
    }

    @Override
    public final CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        serializeNBT(tag);
        return tag;
    }

    public void serializeNBT(CompoundTag tag) {
        tag.putInt("inv_size", inventory.getAvailableSize());
        tag.put("inv", inventory.write(new ListTag()));
        tag.putFloat("health", health);
        tag.putString("name", name);
        tag.putString("data_type", getDataType().toString());
        tag.putBoolean("locked", taskLocked);
        if (activeTaskDesc != null) {
            CompoundTag task = new CompoundTag();
            task.putString("id", activeTaskDesc.getTask().getRegistryName().toString());
            activeTaskDesc.writeToNBT(task);
            tag.put("task", task);
        }
    }

    public boolean setTaskLocked(boolean locked) {
        return this.taskLocked = locked;
    }

    public void shrinkInventory(MinionEntity<?> entity) {
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
                                entity.spawnAtLocation(stack);
                            }
                        });
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <Q extends IMinionTask.IMinionTaskDesc<MinionData>, T extends IMinionTask<Q, ?>> void switchTask(T oldTask, IMinionTask.IMinionTaskDesc<MinionData> oldDesc, IMinionTask.IMinionTaskDesc<MinionData> newDesc) {
        oldTask.deactivateTask((Q) oldDesc);
        this.activeTaskDesc = newDesc;
    }

    /**
     * Called on server side to upgrade a stat of the given id
     *
     * @param statId -1 if stats are to be reset
     * @return if attributes where changed and a sync is required
     */
    public boolean upgradeStat(int statId, MinionEntity<?> entity) {
        if (statId == -1) {
            resetStats(entity);
            return true;
        }
        return false;
    }

    protected ResourceLocation getDataType() {
        return new ResourceLocation("");
    }
}
