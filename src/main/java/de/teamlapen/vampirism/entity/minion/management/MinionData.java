package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MinionData implements INBTSerializable<CompoundNBT> {

    private final static Logger LOGGER = LogManager.getLogger();
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
    private int maxHealth;
    private ITextComponent name;


    @Nonnull
    private IMinionTask.IMinionTaskDesc activeTaskDesc;
    private boolean taskLocked;

    protected MinionData(int maxHealth, ITextComponent name, int invSize) {
        this.health = maxHealth;
        this.maxHealth = maxHealth;
        this.name = name;
        this.inventory = new MinionInventory(invSize);
        this.activeTaskDesc = new IMinionTask.NoDesc(MinionTasks.nothing);
    }

    protected MinionData() {
        this.inventory = new MinionInventory();
        this.activeTaskDesc = new IMinionTask.NoDesc(MinionTasks.nothing);
    }

    @Nonnull
    public IMinionTask.IMinionTaskDesc getCurrentTaskDesc() {
        return activeTaskDesc;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        inventory.read(nbt.getList("inv", 10));
        inventory.setAvailableSize(nbt.getInt("inv_size"));
        health = nbt.getFloat("health");
        maxHealth = nbt.getInt("max_health");
        name = ITextComponent.Serializer.fromJson(nbt.getString("name"));
        taskLocked = nbt.getBoolean("locked");
        if (nbt.contains("task", 10)) {
            CompoundNBT task = nbt.getCompound("task");
            ResourceLocation id = new ResourceLocation(task.getString("id"));
            IMinionTask<?> activeTask = ModRegistries.MINION_TASKS.getValue(id);
            if (activeTask != null) {
                activeTaskDesc = activeTask.readFromNBT(task);
            } else {
                LOGGER.error("Saved minion task does not exist anymore {}", id);
            }
        }
    }

    public boolean isTaskLocked() {
        return taskLocked;
    }

    public void serializeNBT(CompoundNBT tag) {
        tag.putInt("inv_size", inventory.getAvailableSize());
        tag.put("inv", inventory.write(new ListNBT()));
        tag.putFloat("health", health);
        tag.putFloat("max_health", maxHealth);
        tag.putString("name", ITextComponent.Serializer.toJson(name));
        tag.putString("data_type", getDataType().toString());
        tag.putBoolean("locked", taskLocked);
        if (activeTaskDesc != null) {
            CompoundNBT task = new CompoundNBT();
            task.putString("id", activeTaskDesc.getTask().getRegistryName().toString());
            activeTaskDesc.writeToNBT(task);
            tag.put("task", task);
        }
    }

    public <Q extends IMinionTask.IMinionTaskDesc, T extends IMinionTask<Q>> void switchTask(T oldTask, IMinionTask.IMinionTaskDesc oldDesc, IMinionTask.IMinionTaskDesc newDesc) {
        oldTask.deactivateTask((Q) oldDesc);
        this.activeTaskDesc = newDesc;
    }


    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public MinionInventory getInventory() {
        return inventory;
    }


    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public ITextComponent getName() {
        return name;
    }

    public void setName(ITextComponent name) {
        this.name = name;
    }

    @Override
    public final CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        serializeNBT(tag);
        return tag;
    }

    public boolean setTaskLocked(boolean locked) {
        return this.taskLocked = locked;
    }

    protected ResourceLocation getDataType() {
        return new ResourceLocation("");
    }
}
