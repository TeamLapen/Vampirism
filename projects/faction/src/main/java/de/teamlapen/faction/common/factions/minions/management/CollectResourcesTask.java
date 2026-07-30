package de.teamlapen.faction.common.factions.minions.management;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.world.entities.minion.IFactionMinionTask;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntity;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.factions.minions.MinionData;
import de.teamlapen.faction.common.util.RegUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static de.teamlapen.faction.common.factions.minions.management.CollectResourcesTask.Desc;


public class CollectResourcesTask<Q extends MinionData> extends DefaultMinionTask<Desc<Q>, Q> implements IFactionMinionTask<Desc<Q>, Q> {

    @NotNull
    private final Function<Q, Integer> coolDownSupplier;
    @NotNull
    private final Supplier<List<Weighted<ItemStack>>> resources;
    private final RandomSource rng = RandomSource.create();
    @Nullable
    private final Holder<? extends IFaction<?>> faction;


    private final Codec<Desc<Q>> descriptionCodec;
    /**
     * @param faction If given, only available to this faction
     */
    public CollectResourcesTask(Supplier<CollectResourcesTask<Q>> taskSupplier, @Nullable Holder<? extends IFaction<?>> faction, @NotNull Function<Q, Integer> coolDownSupplier, @NotNull Supplier<List<Weighted<ItemStack>>> resources, @NotNull Holder<ISkill<?>> requiredSkill) {
        super(requiredSkill);
        this.descriptionCodec = Desc.createCodec(taskSupplier);
        this.coolDownSupplier = coolDownSupplier;
        this.resources = Suppliers.memoize(resources::get);
        this.faction = faction;
    }

    @Override
    public Desc<Q> activateTask(@Nullable Player lord, @Nullable IMinionEntity minion, @NonNull Q data) {
        this.triggerAdvancements(lord);
        if (minion != null) {
            minion.recallMinion();
        }
        if (lord != null) {
            lord.sendOverlayMessage(Component.translatable(Util.makeDescriptionId("minion_task", RegUtil.id(this)) + ".start"));
        }
        return new Desc<>(this, this.coolDownSupplier.apply(data), lord != null ? lord.getUUID() : null);
    }

    @Override
    public @NotNull Codec<Desc<Q>> descriptionCodec() {
        return this.descriptionCodec;
    }

    @Override
    public void deactivateTask(@NonNull Desc<Q> desc) {

    }

    @Override
    public boolean isAvailable( @NotNull ILordPlayer player) {
        return (this.faction == null || IFaction.is(this.faction, player.getFaction())) && isRequiredSkillUnlocked(FactionPlayerHandler.get(player.asEntity()).getCurrentSkillPlayer());
    }


    @Override
    public @NotNull Desc<Q> load(@NotNull ValueInput input) {
        return new Desc<>(this, input);
    }

    @Override
    public void tickBackground(@NotNull Desc<Q> desc, @NotNull Q data) {
        if (--desc.coolDown <= 0) {
            boolean lordOnline = desc.lordEntityID != null && ServerLifecycleHooks.getCurrentServer() != null && ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(desc.lordEntityID) != null;
            desc.coolDown = lordOnline ? coolDownSupplier.apply(data) : (int) (coolDownSupplier.apply(data) * FactionConfig.server().minionOfflineResourceCooldownMultiplier.get());
            WeightedList.of(resources.get()).getRandom(rng).ifPresent(s -> data.getInventory().addItemStack(s));
            List<ItemStack> stacks = Stream.of(data.getInventory().getInventoryArmor(), data.getInventory().getInventoryHands()).flatMap(Collection::stream).filter(stack -> !stack.isEmpty()).toList();
            if (!stacks.isEmpty()) {
                ItemStack stack = stacks.get(rng.nextInt(stacks.size()));
                if (stack.get(DataComponents.REPAIRABLE) != null && stack.getDamageValue() > 0) {
                    stack.setDamageValue(Math.max(0, stack.getDamageValue() - FactionConfig.server().minionEquipmentRepairAmount.get()));
                }
            }
        }
    }

    @Override
    public @Nullable Holder<? extends IFaction<?>> getFaction() {
        return this.faction;
    }

    public static class Desc<Z extends MinionData> implements IMinionTask.IMinionTaskDesc<Z> {
        private final CollectResourcesTask<Z> task;
        @Nullable
        private final UUID lordEntityID;
        private int coolDown;

        private static <Z extends MinionData> Codec<Desc<Z>> createCodec(Supplier<CollectResourcesTask<Z>> taskSupplier) {
            return RecordCodecBuilder.create(inst -> {
                return inst.group(
                        Codec.INT.fieldOf("cooldown").forGetter(x -> x.coolDown),
                        UUIDUtil.CODEC.fieldOf("lordid").forGetter(x -> x.lordEntityID)
                ).apply(inst, (i, u) -> new Desc<>(taskSupplier.get(), i, u));
            });
        }

        public Desc(CollectResourcesTask<Z> task, int coolDown, @Nullable UUID lordEntityID) {
            this.task = task;
            this.coolDown = coolDown;
            this.lordEntityID = lordEntityID;
        }

        private Desc(CollectResourcesTask<Z> task, ValueInput input) {
            this.task = task;
            this.coolDown = input.getIntOr("cooldown", 0);
            this.lordEntityID = input.read("lordid", UUIDUtil.CODEC).orElse(null);
        }

        @Override
        public @NotNull IMinionTask<?, Z> getTask() {
            return this.task;
        }

        @Override
        public void serialize(@NotNull ValueOutput output) {
            output.putInt("cooldown", this.coolDown);
            if (this.lordEntityID != null) {
                output.store("lordid", UUIDUtil.CODEC, this.lordEntityID);
            }
        }
    }
}
