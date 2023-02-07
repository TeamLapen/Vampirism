package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.minion.IMinionData;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.FactionRegistry;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public class MinionData implements INBTSerializable<CompoundTag>, IMinionData {


    public final static int MAX_NAME_LENGTH = 15;
    protected final static Logger LOGGER = LogManager.getLogger();
    private final static Map<ResourceLocation, Supplier<? extends MinionData>> constructors = new HashMap<>(); //TODO maybe API

    /**
     * @deprecated use {@link de.teamlapen.vampirism.api.entity.factions.IMinionBuilder} from  {@link de.teamlapen.vampirism.api.entity.factions.ILordPlayerBuilder} instead
     */
    @Deprecated(forRemoval = true)
    public static void registerDataType(ResourceLocation id, Supplier<? extends MinionData> supplier) {
        ((FactionRegistry) VampirismAPI.factionRegistry()).addMinionData(id, supplier);
    }

    @NotNull
    public static MinionData fromNBT(@NotNull CompoundTag nbt) {
        ResourceLocation dataType = new ResourceLocation(nbt.getString("data_type"));
        return Optional.ofNullable(VampirismAPI.factionRegistry().getMinion(dataType)).map(Supplier::get).map(s-> {
            ((MinionData)s).deserializeNBT(nbt);
            return (MinionData)s;
        }).orElse(new MinionData());
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
        this.activeTaskDesc = new IMinionTask.NoDesc<>(MinionTasks.NOTHING.get());
    }

    protected MinionData() {
        this.inventory = new MinionInventory();
        this.activeTaskDesc = new IMinionTask.NoDesc<>(MinionTasks.NOTHING.get());
    }

    @Override
    public void deserializeNBT(@NotNull CompoundTag nbt) {
        inventory.read(nbt.getList("inv", 10));
        inventory.setAvailableSize(nbt.getInt("inv_size"));
        health = nbt.getFloat("health");
        name = nbt.getString("name");
        taskLocked = nbt.getBoolean("locked");
        if (nbt.contains("task", 10)) {
            CompoundTag task = nbt.getCompound("task");
            ResourceLocation id = new ResourceLocation(task.getString("id"));
            //noinspection unchecked
            IMinionTask<?, MinionData> activeTask = (IMinionTask<?, MinionData>) RegUtil.getMinionTask(id);
            if (activeTask != null) {
                activeTaskDesc = activeTask.readFromNBT(task);
            } else {
                LOGGER.error("Saved minion task does not exist anymore {}", id);
            }
        }
        entityCaps = nbt.getCompound("caps");
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

    public @NotNull CompoundTag getEntityCaps() {
        return entityCaps;
    }

    public void updateEntityCaps(CompoundTag caps) {
        this.entityCaps = caps;
    }

    public void resetStats(@NotNull MinionEntity<?> entity) {
        entity.getInventory().ifPresent(inv -> {
            if (!InventoryHelper.removeItemFromInventory(inv, new ItemStack(ModItems.OBLIVION_POTION.get()))) {
                entity.getLordOpt().ifPresent(lord -> InventoryHelper.removeItemFromInventory(lord.getPlayer().getInventory(), new ItemStack(ModItems.OBLIVION_POTION.get())));
            }
        });
        HelperLib.sync(entity);
    }

    @Override
    public final @NotNull CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        serializeNBT(tag);
        return tag;
    }

    public void serializeNBT(@NotNull CompoundTag tag) {
        tag.putInt("inv_size", inventory.getAvailableSize());
        tag.put("inv", inventory.write(new ListTag()));
        tag.putFloat("health", health);
        tag.putString("name", name);
        tag.putString("data_type", getDataType().toString());
        tag.putBoolean("locked", taskLocked);
        if (activeTaskDesc != null) {
            CompoundTag task = new CompoundTag();
            task.putString("id", RegUtil.id(activeTaskDesc.getTask()).toString());
            activeTaskDesc.writeToNBT(task);
            tag.put("task", task);
        }
        tag.put("caps", entityCaps);
    }

    public boolean setTaskLocked(boolean locked) {
        return this.taskLocked = locked;
    }

    public void shrinkInventory(@NotNull MinionEntity<?> entity) {
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
    public <Q extends IMinionTask.IMinionTaskDesc<MinionData>, T extends IMinionTask<Q, ?>> void switchTask(@NotNull T oldTask, IMinionTask.IMinionTaskDesc<MinionData> oldDesc, IMinionTask.@NotNull IMinionTaskDesc<MinionData> newDesc) {
        oldTask.deactivateTask((Q) oldDesc);
        this.activeTaskDesc = newDesc;
    }

    /**
     * Called on server side to upgrade a stat of the given id
     * <p>
     * @param  statId values:<br>
     * -1: reset all stats<br>
     * -2: update attributes<br>
     *
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
        return new ResourceLocation("");
    }
}
