package de.teamlapen.faction.common.factions.minions.management;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.world.entities.minion.IMinionData;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntity;
import de.teamlapen.faction.api.world.entities.minion.MinionStat;
import de.teamlapen.faction.api.world.entities.minion.tasks.MinionTaskProperties;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.core.FactionLootContextParamSets;
import de.teamlapen.faction.common.core.FactionLootContextParams;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Sends the minion away to collect resources. Every cooldown the given loot table is rolled and the result is added to the minion inventory.
 * <p>
 * The loot table is rolled with the {@link FactionLootContextParamSets#MINION_COLLECT} parameter set. The item in the minion's main hand is passed as {@link LootContextParams#TOOL},
 * so pools can require better tools via {@link net.minecraft.world.level.storage.loot.predicates.MatchTool}. If the lord is online, it is passed as {@link FactionLootContextParams#LORD_PLAYER}.
 */
public class CollectResourcesTask extends MinionTask<CollectResourcesTask.State> {

    private final ResourceKey<LootTable> lootTable;
    private final RandomSource rng = RandomSource.create();

    public CollectResourcesTask(MinionTaskProperties properties, ResourceKey<LootTable> lootTable) {
        super(properties, State.CODEC);
        this.lootTable = lootTable;
    }

    public ResourceKey<LootTable> getLootTable() {
        return this.lootTable;
    }

    @Override
    public @NonNull ActivateResult<State> activateTask(@Nullable Player lord, @Nullable IMinionEntity minion, @NonNull IMinionData data) {
        this.triggerAdvancements(lord);
        if (minion != null) {
            minion.recallMinion();
        }
        if (lord != null) {
            lord.sendOverlayMessage(Component.translatable(getNameId() + ".start"));
        }
        return new ActivateResult<>(new State(getCooldown(data), lord != null ? lord.getUUID() : null));
    }

    @Override
    public void tickBackground(@NonNull State state, @NonNull IMinionData data) {
        if (--state.cooldown > 0) {
            return;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        ServerPlayer lord = state.lordId != null ? server.getPlayerList().getPlayer(state.lordId) : null;
        state.cooldown = lord != null ? getCooldown(data) : (int) (getCooldown(data) * FactionConfig.server().minionOfflineResourceCooldownMultiplier.get());

        ServerLevel level = lord != null ? lord.level() : server.overworld();
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.TOOL, data.getInventory().getInventoryHands().getFirst())
                .withOptionalParameter(FactionLootContextParams.LORD_PLAYER, lord)
                .create(FactionLootContextParamSets.MINION_COLLECT);
        boolean inventoryFull = false;
        for (ItemStack stack : server.reloadableRegistries().getLootTable(this.lootTable).getRandomItems(params, this.rng)) {
            data.getInventory().addItemStack(stack);
            inventoryFull |= !stack.isEmpty();
        }
        state.failedCollections = inventoryFull ? state.failedCollections + 1 : 0;

        repairRandomEquipment(data);
    }

    /**
     * The task is finished once the collected items could not be added to the minion inventory several times in a row
     */
    @Override
    public boolean isFinished(@NonNull State state) {
        return state.failedCollections >= FactionConfig.server().minionCollectFailuresUntilFinished.get();
    }

    @Override
    public @NonNull Component getFinishedMessage(@NonNull State state, @NonNull IMinionData data) {
        return Component.translatable("dialogue.factionapi.minion.finished_collecting", data.getName());
    }

    /**
     * The base cooldown is reduced by up to 40% depending on the minion's resource efficiency level
     */
    protected int getCooldown(IMinionData data) {
        int level = MinionStat.RESOURCES_STATS.currentLevel(data);
        return (int) (FactionConfig.server().minionResourceCooldown.get() * (1f - level / (float) MinionStat.RESOURCES_STATS.getMaxLevel() * 0.4f));
    }

    private void repairRandomEquipment(IMinionData data) {
        List<ItemStack> stacks = Stream.of(data.getInventory().getInventoryArmor(), data.getInventory().getInventoryHands()).flatMap(Collection::stream).filter(stack -> !stack.isEmpty()).toList();
        if (!stacks.isEmpty()) {
            ItemStack stack = stacks.get(this.rng.nextInt(stacks.size()));
            if (stack.get(DataComponents.REPAIRABLE) != null && stack.getDamageValue() > 0) {
                stack.setDamageValue(Math.max(0, stack.getDamageValue() - FactionConfig.server().minionEquipmentRepairAmount.get()));
            }
        }
    }

    public static class State implements IMinionTaskState {

        public static final Codec<State> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.INT.fieldOf("cooldown").forGetter(s -> s.cooldown),
                UUIDUtil.CODEC.optionalFieldOf("lord").forGetter(s -> Optional.ofNullable(s.lordId)),
                Codec.INT.optionalFieldOf("failed_collections", 0).forGetter(s -> s.failedCollections)
        ).apply(inst, (cooldown, lord, failedCollections) -> new State(cooldown, lord.orElse(null), failedCollections)));

        private int cooldown;
        private final @Nullable UUID lordId;
        /**
         * Number of consecutive collections in which not all items fit into the minion inventory
         */
        private int failedCollections;

        public State(int cooldown, @Nullable UUID lordId) {
            this(cooldown, lordId, 0);
        }

        private State(int cooldown, @Nullable UUID lordId, int failedCollections) {
            this.cooldown = cooldown;
            this.lordId = lordId;
            this.failedCollections = failedCollections;
        }
    }
}
