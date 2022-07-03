package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.lib.util.WeightedRandomItem;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static de.teamlapen.vampirism.entity.minion.management.CollectResourcesTask.Desc;


public class CollectResourcesTask<Q extends MinionData> extends DefaultMinionTask<Desc<Q>, Q> {

    @Nonnull
    private final Function<Q, Integer> coolDownSupplier;
    @Nonnull
    private final List<WeightedRandomItem<ItemStack>> resources;
    private final RandomSource rng = RandomSource.create();
    @Nullable
    private final IFaction<?> faction;


    /**
     * @param faction If given, only available to this faction
     */
    public CollectResourcesTask(@Nullable IFaction<?> faction, @Nonnull Function<Q, Integer> coolDownSupplier, @Nonnull List<WeightedRandomItem<ItemStack>> resources) {
        this.coolDownSupplier = coolDownSupplier;
        this.resources = resources;
        this.faction = faction;
    }

    @Override
    public Desc<Q> activateTask(@Nullable Player lord, @Nullable IMinionEntity minion, Q data) {
        this.triggerAdvancements(lord);
        if (minion != null) {
            minion.recallMinion();
        }
        if (lord != null) {
            lord.displayClientMessage(Component.translatable(Util.makeDescriptionId("minion_task", RegUtil.id(this)) + ".start"), true);
        }
        return new Desc<>(this, this.coolDownSupplier.apply(data), lord != null ? lord.getUUID() : null);
    }

    @Override
    public void deactivateTask(Desc<Q> desc) {

    }

    @Override
    public boolean isAvailable(IFaction<?> faction, @Nullable ILordPlayer player) {
        return this.faction == null || this.faction == faction;
    }

    @Override
    public Desc<Q> readFromNBT(CompoundTag nbt) {
        return new Desc<>(this, nbt.getInt("cooldown"), nbt.contains("lordid") ? nbt.getUUID("lordid") : null);
    }

    @Override
    public void tickBackground(Desc<Q> desc, @Nonnull Q data) {
        if (--desc.coolDown <= 0) {
            boolean lordOnline = desc.lordEntityID != null && ServerLifecycleHooks.getCurrentServer() != null && ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(desc.lordEntityID) != null;
            desc.coolDown = lordOnline ? coolDownSupplier.apply(data) : (int) (coolDownSupplier.apply(data) * VampirismConfig.BALANCE.miResourceCooldownOfflineMult.get());
            WeightedRandom.getRandomItem(rng, resources).map(WeightedRandomItem::getItem).map(ItemStack::copy).ifPresent(s -> data.getInventory().addItemStack(s));
        }
    }

    public static class Desc<Z extends MinionData> implements IMinionTask.IMinionTaskDesc<Z> {
        private final CollectResourcesTask<Z> task;
        @Nullable
        private final UUID lordEntityID;
        private int coolDown;

        public Desc(CollectResourcesTask<Z> task, int coolDown, @Nullable UUID lordEntityID) {
            this.task = task;
            this.coolDown = coolDown;
            this.lordEntityID = lordEntityID;
        }

        @Override
        public IMinionTask<?, Z> getTask() {
            return task;
        }

        @Override
        public void writeToNBT(CompoundTag nbt) {
            nbt.putInt("cooldown", coolDown);
            if (lordEntityID != null) {
                nbt.putUUID("lordid", lordEntityID);
            }
        }
    }
}
