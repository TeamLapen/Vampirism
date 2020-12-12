package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.minion.IMinionData;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MinionData implements INBTSerializable<CompoundNBT>, IMinionData {


    public final static int MAX_NAME_LENGTH = 15;
    protected final static Logger LOGGER = LogManager.getLogger();
    private final static Map<ResourceLocation, Supplier<? extends MinionData>> constructors = new HashMap<>(); //TODO maybe API


    public static void registerDataType(ResourceLocation id, Supplier<? extends MinionData> supplier) {
        constructors.put(id, supplier);
    }

    @Nonnull
    public static MinionData fromNBT(CompoundNBT nbt) {
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
    public void deserializeNBT(CompoundNBT nbt) {
        inventory.read(nbt.getList("inv", 10));
        inventory.setAvailableSize(nbt.getInt("inv_size"));
        health = nbt.getFloat("health");
        name = nbt.getString("name");
        taskLocked = nbt.getBoolean("locked");
        if (nbt.contains("task", 10)) {
            CompoundNBT task = nbt.getCompound("task");
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

    @Override
    public IFormattableTextComponent getFormattedName() {
        return new StringTextComponent(name);
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

    public <Q extends IMinionTask.IMinionTaskDesc<MinionData>, T extends IMinionTask<Q, ?>> void switchTask(T oldTask, IMinionTask.IMinionTaskDesc<MinionData> oldDesc, IMinionTask.IMinionTaskDesc<MinionData> newDesc) {
        oldTask.deactivateTask((Q) oldDesc);
        this.activeTaskDesc = newDesc;
    }

    public boolean isTaskLocked() {
        return taskLocked;
    }

    @Override
    public final CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        serializeNBT(tag);
        return tag;
    }

    public void serializeNBT(CompoundNBT tag) {
        tag.putInt("inv_size", inventory.getAvailableSize());
        tag.put("inv", inventory.write(new ListNBT()));
        tag.putFloat("health", health);
        tag.putString("name", name);
        tag.putString("data_type", getDataType().toString());
        tag.putBoolean("locked", taskLocked);
        if (activeTaskDesc != null) {
            CompoundNBT task = new CompoundNBT();
            task.putString("id", activeTaskDesc.getTask().getRegistryName().toString());
            activeTaskDesc.writeToNBT(task);
            tag.put("task", task);
        }
    }

    public boolean setTaskLocked(boolean locked) {
        return this.taskLocked = locked;
    }

    /**
     * Called on server side to upgrade a stat of the given id
     */
    public boolean upgradeStat(int statId, MinionEntity<?> entity) {
        return false;
    }

    protected ResourceLocation getDataType() {
        return new ResourceLocation("");
    }
}
