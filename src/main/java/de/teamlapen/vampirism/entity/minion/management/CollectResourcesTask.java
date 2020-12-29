package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.lib.util.WeightedRandomItem;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.minion.DefaultMinionTask;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

import static de.teamlapen.vampirism.entity.minion.management.CollectResourcesTask.Desc;


public class CollectResourcesTask<Q extends MinionData> extends DefaultMinionTask<Desc<Q>, Q> {

    @Nonnull
    private final Function<Q, Integer> coolDownSupplier;
    @Nonnull
    private final List<WeightedRandomItem<ItemStack>> resources;
    private final Random rng = new Random();
    @Nullable
    private final IPlayableFaction<?> faction;


    /**
     * @param faction If given, only available to this faction
     */
    public CollectResourcesTask(@Nullable IPlayableFaction<?> faction, @Nonnull Function<Q, Integer> coolDownSupplier, @Nonnull List<WeightedRandomItem<ItemStack>> resources) {
        this.coolDownSupplier = coolDownSupplier;
        this.resources = resources;
        this.faction = faction;
    }

    @Override
    public Desc<Q> activateTask(@Nullable PlayerEntity lord, @Nullable IMinionEntity minion, Q data) {
        if (minion != null) {
            minion.recallMinion();
        }
        if (lord != null)
            lord.sendStatusMessage(new TranslationTextComponent(Util.makeTranslationKey("minion_task", getRegistryName()) + ".start"), true);
        return new Desc<>(this, this.coolDownSupplier.apply(data), lord != null ? lord.getUniqueID() : null);
    }

    @Override
    public void deactivateTask(Desc<Q> desc) {

    }

    @Override
    public boolean isAvailable(IPlayableFaction<?> faction, @Nullable ILordPlayer player) {
        return this.faction == null || this.faction == faction;
    }

    @Override
    public Desc<Q> readFromNBT(CompoundNBT nbt) {
        return new Desc<>(this, nbt.getInt("cooldown"), nbt.contains("lordid") ? nbt.getUniqueId("lordid") : null);
    }

    @Override
    public void tickBackground(Desc<Q> desc, @Nonnull Q data) {
        if (--desc.coolDown <= 0) {
            boolean lordOnline = desc.lordEntityID != null && ServerLifecycleHooks.getCurrentServer() != null && ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(desc.lordEntityID) != null;
            desc.coolDown = lordOnline ? coolDownSupplier.apply(data) : (int) (coolDownSupplier.apply(data) * VampirismConfig.BALANCE.miResourceCooldownOfflineMult.get());
            data.getInventory().addItemStack(WeightedRandom.getRandomItem(rng, resources).getItem().copy());
        }
    }

    public static class Desc<Z extends MinionData> implements IMinionTask.IMinionTaskDesc<Z> {
        private final CollectResourcesTask<Z> task;
        private int coolDown;
        @Nullable
        private final UUID lordEntityID;

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
        public void writeToNBT(CompoundNBT nbt) {
            nbt.putInt("cooldown", coolDown);
            if (lordEntityID != null) {
                nbt.putUniqueId("lordid", lordEntityID);
            }
        }
    }
}
