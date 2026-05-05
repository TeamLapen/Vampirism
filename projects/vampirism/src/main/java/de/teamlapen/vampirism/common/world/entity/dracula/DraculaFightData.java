package de.teamlapen.vampirism.common.world.entity.dracula;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.core.ModDimensions;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.world.blocks.ChaliceBlock;
import de.teamlapen.vampirism.common.world.dimensions.DimensionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Function;

public class DraculaFightData implements ValueIOSerializable {

    public final static Identifier DRACULA_SPAWN_MARKER = VIdentifier.mod("dracula_spawn");
    private static final Logger LOGGER = LoggerFactory.getLogger(DraculaFightData.class);
    private final ServerDraculaEvent event = new ServerDraculaEvent(1, FightStage.NONE, false);
    private final ServerLevel level;
    private final Map<BlockPos, ChaliceReference> chaliceReferences = new HashMap<>();
    private State dimensionState = State.NONE;
    @Nullable
    private UUID draculaId;
    @Nullable
    private WeakReference<Dracula> dracula;
    private int deathTicks = 0;
    private int warningsSend = 0;

    public static DraculaFightData get(ServerLevel level) {
        return level.getData(ModAttachments.DRACULA_FIGHT_DATA);
    }

    public static Optional<DraculaFightData> getOpt(LevelReader level) {
        return level instanceof ServerLevel serverLevel && serverLevel.dimensionTypeRegistration().is(ModDimensions.VELMORRA_DIMENSION_TYPE) ? Optional.of(serverLevel.getData(ModAttachments.DRACULA_FIGHT_DATA)) : Optional.empty();
    }

    public DraculaFightData(ServerLevel level) {
        this.level = level;
    }

    public ServerDraculaEvent getEvent() {
        return this.event;
    }

    public void registerChalice(BlockPos pos) {
        if (level.getBlockState(pos) instanceof BlockState state && state.getBlock() instanceof ChaliceBlock) {
            this.chaliceReferences.put(pos, new ChaliceReference(pos, state.getValue(ChaliceBlock.FILLED)));
        } else {
            this.chaliceReferences.remove(pos);
        }
    }

    public void tick() {
        switch (dimensionState) {
            case INVALID -> {
                LOGGER.error("The velmorra dimension is invalid, the level will be destroyed");
                destroyDimension();
            }
            case DEAD -> {
                deathTicks++;
                if (deathTicks >= 12000) {
                    // TODO check if players need to send back
                    destroyDimension();
                } else if (deathTicks >= 10000 && warningsSend == 1) {
                    level.players().forEach(x -> x.sendOverlayMessage(Component.translatable("message.vampirism.velmorra.crumblin2")));
                    warningsSend++;
                } else if (deathTicks >= 5000 && warningsSend == 0) {
                    level.players().forEach(x -> x.sendOverlayMessage(Component.translatable("message.vampirism.velmorra.crumblin")));
                    warningsSend++;
                }
            }
            case NONE -> {
                if (chaliceReferences.values().stream().filter(r -> r.active).count() >= 4) {
                    startFight();
                    return;
                }
            }
            case FIGHTING -> {
                if (level.getGameTime() % 100 == 0){
                    level.getPlayers(x -> true).forEach(event::addPlayer);
                }
            }
        }
    }

    private void destroyDimension() {
        DimensionManager manager = DimensionManager.INSTANCE;
        manager.markDimensionForUnregistration(level.getServer(), ModDimensions.VELMORRA_LEVEL, true);
    }

    private void startFight() {
        this.dimensionState = State.FIGHTING;
        List<Marker> marker = new ArrayList<>(level.getEntities(EntityType.MARKER, x -> x.getData(ModAttachments.MARKER.get()).equals(DRACULA_SPAWN_MARKER)));
        Marker spawnPoint = marker.isEmpty() ? null : marker.get(level.getRandom().nextInt(marker.size()));
        if (spawnPoint == null) {
            this.dimensionState = State.INVALID;
            return;
        }

        Dracula dracula = ModEntities.DRACULA.get().create(level, EntitySpawnReason.TRIGGERED);
        if (dracula == null) {
            this.dimensionState = State.INVALID;
            return;
        }
        dracula.setPos(spawnPoint.position());
        this.event.setVisible(true);
        this.level.addFreshEntity(dracula);
        this.draculaId = dracula.getUUID();
        this.dracula = new WeakReference<>(dracula);
        this.chaliceReferences.forEach((pos, chalice) -> {
            this.level.setBlock(pos, this.level.getBlockState(pos).setValue(ChaliceBlock.FILLED, false), Block.UPDATE_ALL);
        });
    }

    public void draculaDied(Dracula dracula) {
        if (!dracula.getUUID().equals(this.draculaId)) {
            return;
        }
        this.dimensionState = State.DEAD;
        applyAward();
        this.event.clear();
        activatePortal();
    }

    public void applyAward() {
        Set<ServerPlayer> players = this.event.getPlayers();
    }

    public void activatePortal() {

    }

    @Override
    public void serialize(ValueOutput output) {
        output.storeNullable("dracula", UUIDUtil.CODEC, this.draculaId);
        output.store("state", State.CODEC, this.dimensionState);
    }

    @Override
    public void deserialize(ValueInput input) {
        this.draculaId = input.read("dracula", UUIDUtil.CODEC).orElse(null);
        this.dimensionState = input.read("state", State.CODEC).orElse(State.NONE);
    }

    public static class Factory implements Function<IAttachmentHolder, DraculaFightData> {

        @Override
        public DraculaFightData apply(IAttachmentHolder holder) {
            if (holder instanceof ServerLevel level && level.dimensionTypeRegistration().is(ModDimensions.VELMORRA_DIMENSION_TYPE)) {
                return new DraculaFightData(level);
            }
            throw new IllegalArgumentException("Cannot create dracula fight data for holder " + holder.getClass() + ". Expected ServerLevel with Velmorra dimension type");
        }
    }

    public static class Serializer implements IAttachmentSerializer<DraculaFightData> {

        @Override
        public DraculaFightData read(IAttachmentHolder holder, ValueInput input) {
            if (holder instanceof ServerLevel level && level.dimensionTypeRegistration().is(ModDimensions.VELMORRA_DIMENSION_TYPE)) {
                var data = new DraculaFightData(level);
                data.deserialize(input);
                return data;
            }
            throw new IllegalArgumentException("Cannot create dracula fight data for holder " + holder.getClass() + ". Expected ServerLevel with Velmorra dimension type");
        }

        @Override
        public boolean write(DraculaFightData attachment, ValueOutput output) {
            attachment.serialize(output);
            return true;
        }
    }

    private record ChaliceReference(BlockPos pos, boolean active) { }

    private enum State {
        NONE,
        FIGHTING,
        /** Idle and destroy dimension after time */
        DEAD,
        /** Destroy the dimension */
        INVALID;

        public static Codec<State> CODEC = Codec.STRING.xmap(State::valueOf, State::name);
    }
}
