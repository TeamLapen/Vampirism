package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.lib.lib.inventory.InventoryHelper;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MinionData implements INBTSerializable<CompoundNBT> {

    private final static Logger LOGGER = LogManager.getLogger();
    private final static Map<ResourceLocation, Supplier<? extends MinionData>> constructors = new HashMap<>(); //TODO maybe API


    public static void registerDataType(ResourceLocation id, Supplier<? extends MinionData> supplier) {
        constructors.put(id, supplier);
    }

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

    private final Inventory inventory = new Inventory(10);
    private float health;
    private int maxHealth;
    private ITextComponent name;

    @Nullable
    private MinionTask currentTask;

    protected MinionData(int maxHealth, ITextComponent name) {
        this.health = maxHealth;
        this.maxHealth = maxHealth;
        this.name = name;
        this.currentTask = new MinionTask(MinionTask.Type.FOLLOW);
    }

    protected MinionData() {

    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        InventoryHelper.readInventoryFromTag(nbt, inventory);
        health = nbt.getFloat("health");
        maxHealth = nbt.getInt("max_health");
        name = ITextComponent.Serializer.fromJson(nbt.getString("name"));
        currentTask = nbt.contains("task", 10) ? MinionTask.createFromNBT(nbt.getCompound("task")) : null;
    }

    @Nullable
    public MinionTask getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(@Nullable MinionTask currentTask) {
        this.currentTask = currentTask;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public Inventory getInventory() {
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
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        InventoryHelper.writeInventoryToTag(tag, inventory);
        tag.putFloat("health", health);
        tag.putFloat("max_health", maxHealth);
        tag.putString("name", ITextComponent.Serializer.toJson(name));
        tag.putString("data_type", getDataType().toString());
        if (currentTask != null) {
            tag.put("task", currentTask.serializeNBT());
        }
        return tag;
    }

    protected ResourceLocation getDataType() {
        return new ResourceLocation("");
    }
}
