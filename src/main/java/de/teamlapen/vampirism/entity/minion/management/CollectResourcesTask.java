package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.minion.IFactionMinionTask;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static de.teamlapen.vampirism.entity.minion.management.CollectResourcesTask.Desc;


public class CollectResourcesTask<Q extends MinionData> extends DefaultMinionTask<Desc<Q>, Q> implements IFactionMinionTask<Desc<Q>, Q> {

    @NotNull
    private final Function<Q, Integer> coolDownSupplier;
    @NotNull
    private final List<WeightedEntry.Wrapper<ItemStack>> resources;
    private final RandomSource rng = RandomSource.create();
    @Nullable
    private final IFaction<?> faction;


    /**
     * @param faction If given, only available to this faction
     */
    public CollectResourcesTask(@Nullable IFaction<?> faction, @NotNull Function<Q, Integer> coolDownSupplier, @NotNull List<WeightedEntry.Wrapper<ItemStack>> resources, @NotNull Supplier<? extends ISkill<?>> requiredSkill) {
        super(requiredSkill);
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
    public boolean isAvailable(@NotNull IPlayableFaction<?> faction, @Nullable ILordPlayer player) {
        return (this.faction == null || this.faction == faction) && isRequiredSkillUnlocked(faction, player);
    }


    @Override
    public @NotNull Desc<Q> readFromNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        return new Desc<>(this, nbt.getInt("cooldown"), nbt.contains("lordid") ? nbt.getUUID("lordid") : null);
    }

    @Override
    public void tickBackground(@NotNull Desc<Q> desc, @NotNull Q data) {
        if (--desc.coolDown <= 0) {
            boolean lordOnline = desc.lordEntityID != null && ServerLifecycleHooks.getCurrentServer() != null && ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(desc.lordEntityID) != null;
            desc.coolDown = lordOnline ? coolDownSupplier.apply(data) : (int) (coolDownSupplier.apply(data) * VampirismConfig.BALANCE.miResourceCooldownOfflineMult.get());
            WeightedRandom.getRandomItem(rng, resources).map(WeightedEntry.Wrapper::data).map(ItemStack::copy).ifPresent(s -> data.getInventory().addItemStack(s));
            List<ItemStack> stacks = Stream.of(data.getInventory().getInventoryArmor(), data.getInventory().getInventoryHands()).flatMap(Collection::stream).filter(stack -> !stack.isEmpty()).toList();
            if (!stacks.isEmpty()) {
                ItemStack stack = stacks.get(rng.nextInt(stacks.size()));
                if (stack.isRepairable() && stack.getDamageValue() > 0) {
                    stack.setDamageValue(Math.max(0, stack.getDamageValue() - VampirismConfig.BALANCE.miEquipmentRepairAmount.get()));
                }
            }
        }
    }

    @Override
    public @Nullable IFaction<?> getFaction() {
        return this.faction;
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
        public void writeToNBT(@NotNull CompoundTag nbt) {
            nbt.putInt("cooldown", coolDown);
            if (lordEntityID != null) {
                nbt.putUUID("lordid", lordEntityID);
            }
        }
    }
}
