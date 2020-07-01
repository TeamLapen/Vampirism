package de.teamlapen.vampirism.inventory.container;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.TaskFinishedPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.NonNullFunction;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public class TaskMasterContainer extends Container {

    private @Nonnull Task.Variant variant;
    /**
     * all tasks that can be completed by the player
     */
    private @Nonnull Set<Task> possibleTasks = Sets.newHashSet();
    /**
     * all tasks that have been completed and should not be displayed
     */
    private @Nonnull Set<Task> completedTasks = Sets.newHashSet();
    /**
     * all tasks that should be displayed in the {@link de.teamlapen.vampirism.client.gui.TaskMasterScreen}
     */
    private @Nonnull List<Task> unlockedTasks = Lists.newArrayList();
    private @Nonnull PlayerEntity player;
    private @Nonnull TextFormatting factionColor;

    @Deprecated
    public TaskMasterContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, Task.Variant.REPEATABLE);
    }

    public TaskMasterContainer(int id, PlayerInventory playerInventory, @Nonnull Task.Variant variant) {
        super(ModContainer.task_master, id);
        this.variant = variant;
        this.player = playerInventory.player;
        this.factionColor = FactionPlayerHandler.getOpt(player).map(FactionPlayerHandler::getCurrentFaction).map(IFaction::getChatColor).orElse(TextFormatting.RESET);
    }

    /**
     * @param possibleTasks updated possibleTasks
     * @param completedTasks updated completedTasks
     * @param unlockedTasks updated unlockedTasks
     */
    public void init(@Nonnull Set<Task> possibleTasks,@Nonnull Set<Task> completedTasks, @Nonnull List<Task> unlockedTasks) {
        this.possibleTasks.clear();
        this.possibleTasks.addAll(possibleTasks);
        this.completedTasks.clear();
        this.completedTasks.addAll(completedTasks);
        this.unlockedTasks.addAll(unlockedTasks.stream().filter(task -> !this.unlockedTasks.contains(task)).sorted((task1, task2) -> (this.possibleTasks.contains(task1) && !this.possibleTasks.contains(task2)) || (!possibleTasks.contains(task1) && !this.completedTasks.contains(task1) && this.completedTasks.contains(task2)) ? -1 : 0).collect(Collectors.toList()));
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return FactionPlayerHandler.getOpt(playerIn).map(player -> player.getCurrentFaction() != null).orElse(false);
    }

    public boolean canCompleteTask(Task task) {
        return this.possibleTasks.contains(task);
    }

    public void completeTask(Task task) {
        if(this.canCompleteTask(task)) {
            VampirismMod.dispatcher.sendToServer(new TaskFinishedPacket(task));
            this.completedTasks.add(task);
            this.possibleTasks.remove(task);
        }
    }

    public boolean isCompleted(Task task) {
        return this.completedTasks.contains(task);
    }

    public int size() {
        return this.unlockedTasks.size();
    }

    @Nonnull
    public Set<Task> getPossibleTasks() {
        return possibleTasks;
    }

    @Nonnull
    public Set<Task> getCompletedTasks() {
        return completedTasks;
    }

    @Nonnull
    public List<Task> getUnlockedTasks() {
        return unlockedTasks;
    }

    @Nonnull
    public TextFormatting getFactionColor() {
        return factionColor;
    }
}
