package de.teamlapen.vampirism.common.world.entity.dracula;

import com.mojang.serialization.Codec;
import de.teamlapen.faction.api.factions.LevelingChange;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IDraculaPlayer;
import de.teamlapen.vampirism.common.core.ModAdvancements;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.core.ModDimensions;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.network.packets.client.ClientboundVelmorraCollapsePacket;
import de.teamlapen.vampirism.common.world.blocks.ChaliceBlock;
import de.teamlapen.vampirism.common.world.dimensions.DimensionManager;
import de.teamlapen.vampirism.common.world.dimensions.velmorra.VelmorraDimension;
import de.teamlapen.vampirism.common.world.portal.VelmorraPortalShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;

public class DraculaFightData implements ValueIOSerializable {

    public final static Identifier DRACULA_SPAWN_MARKER = VIdentifier.mod("dracula_spawn");
    public final static Identifier RETURN_PORTAL_MARKER = VIdentifier.mod("return_portal");
    /** Time between Dracula's death and the destruction of the dimension */
    public static final int COLLAPSE_DURATION = 10 * 60 * 20;
    private static final Warning[] COLLAPSE_WARNINGS = {
            new Warning(1, "message.vampirism.velmorra.crumblin"),
            new Warning(COLLAPSE_DURATION - 5 * 60 * 20, "message.vampirism.velmorra.collapse.5min"),
            new Warning(COLLAPSE_DURATION - 2 * 60 * 20, "message.vampirism.velmorra.collapse.2min"),
            new Warning(COLLAPSE_DURATION - 60 * 20, "message.vampirism.velmorra.crumblin2"),
            new Warning(COLLAPSE_DURATION - 30 * 20, "message.vampirism.velmorra.collapse.30s"),
    };
    private static final Logger LOGGER = LoggerFactory.getLogger(DraculaFightData.class);
    private final ServerDraculaEvent event = new ServerDraculaEvent(1, FightStage.NONE, false);
    private final ServerLevel level;
    private final Map<BlockPos, ChaliceReference> chaliceReferences = new HashMap<>();
    private final Set<GlobalPos> portalLocations = new HashSet<>();
    private final List<BlockPos> pendingChalices = new ArrayList<>();
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

    public void registerPortal(GlobalPos pos) {
        this.portalLocations.add(pos);
    }

    public void tick() {
        if (!this.pendingChalices.isEmpty()) {
            this.pendingChalices.forEach(this::registerChalice);
            this.pendingChalices.clear();
        }
        switch (dimensionState) {
            case INVALID -> {
                LOGGER.error("The velmorra dimension is invalid, the level will be destroyed");
                destroyDimension();
            }
            case DEAD -> {
                deathTicks++;
                if (deathTicks >= COLLAPSE_DURATION) {
                    destroyDimension();
                    return;
                }
                while (warningsSend < COLLAPSE_WARNINGS.length && deathTicks >= COLLAPSE_WARNINGS[warningsSend].tick()) {
                    sendMessage(Component.translatable(COLLAPSE_WARNINGS[warningsSend].translationKey()));
                    warningsSend++;
                }
                int remaining = COLLAPSE_DURATION - deathTicks;
                if (remaining <= 10 * 20 && remaining % 20 == 0) {
                    sendOverlayMessage(Component.translatable("message.vampirism.velmorra.collapse.countdown", remaining / 20));
                }
                // regular sync covers late joiners as well
                if (deathTicks % 20 == 1) {
                    var packet = new ClientboundVelmorraCollapsePacket(deathTicks / (float) COLLAPSE_DURATION);
                    this.level.players().forEach(player -> player.connection.send(packet));
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
                    level.players().forEach(event::addPlayer);
                    if (this.draculaId == null) {
                        LOGGER.error("Dracula id is null");
                        this.dimensionState = State.INVALID;
                    } else if (this.dracula == null || this.dracula.get() == null) {
                        Entity entity = this.level.getEntity(this.draculaId);
                        if (entity instanceof Dracula dra) {
                            this.dracula = new WeakReference<>(dra);
                        } else {
                            LOGGER.error("Dracula is null");
                            this.dimensionState = State.INVALID;
                        }
                    }
                }
            }
        }
    }

    private void destroyDimension() {

        new ArrayList<>(this.level.players()).forEach(player -> {
            GlobalPos data = player.getExistingData(ModAttachments.VELMORRA_PORTAL).orElseGet(() -> {
                ServerPlayer.RespawnConfig respawnConfig = player.getRespawnConfig();
                LevelData.RespawnData respawnData = respawnConfig == null ? this.level.getServer().getRespawnData() : respawnConfig.respawnData();
                return GlobalPos.of(respawnData.dimension() == null ? Level.OVERWORLD : respawnData.dimension(), respawnData.pos());
            });
            ServerLevel targetLevel = this.level.getServer().getLevel(data.dimension());
            if (targetLevel == null || targetLevel == this.level) {
                targetLevel = this.level.getServer().overworld();
                data = GlobalPos.of(Level.OVERWORLD, targetLevel.getRespawnData().pos());
            }
            player.teleport(new TeleportTransition(targetLevel, data.pos().getBottomCenter(), Vec3.ZERO, 0, 0, Set.of(), TeleportTransition.PLAY_PORTAL_SOUND.then(TeleportTransition.PLACE_PORTAL_TICKET)));
        });

        var positions= portalLocations.stream().collect(Collectors.groupingBy(GlobalPos::dimension));

        for (var pos : positions.entrySet()) {
            ServerLevel portalLevel = level.getServer().getLevel(pos.getKey());
            if (portalLevel == null) {
                continue;
            }
            for (GlobalPos globalPos : pos.getValue()) {
                VelmorraPortalShape.findActivePortalShape(portalLevel, globalPos.pos()).ifPresent(x -> x.deactivate(portalLevel));
            }
        }

        this.event.clear();

        DimensionManager manager = DimensionManager.INSTANCE;
        manager.markDimensionForUnregistration(level.getServer(), ModDimensions.VELMORRA_LEVEL, true);
    }

    public static List<Marker> findMarkers(ServerLevel level, Identifier markerType) {
        return new ArrayList<>(level.getEntities(EntityType.MARKER, x -> x.getData(ModAttachments.MARKER.get()).equals(markerType)));
    }

    private void startFight() {
        this.dimensionState = State.FIGHTING;
        List<Marker> marker = findMarkers(level, DRACULA_SPAWN_MARKER);
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
        dracula.setPersistenceRequired();
        dracula.startFight(this.level.players().size());
        this.event.setVisible(true);
        this.level.addFreshEntity(dracula);
        this.draculaId = dracula.getUUID();
        this.dracula = new WeakReference<>(dracula);
        this.chaliceReferences.forEach((pos, chalice) -> {
            this.level.setBlock(pos, this.level.getBlockState(pos).setValue(ChaliceBlock.FILLED, false), Block.UPDATE_ALL);
        });
        this.sendMessage(Component.translatable("message.vampirism.dracula.summoned"));
    }

    public Optional<Dracula> getDracula() {
        return Optional.ofNullable(this.dracula).map(WeakReference::get);
    }

    /** Debug command hook: starts the fight regardless of the chalice ritual. */
    public boolean debugStartFight() {
        if (this.dimensionState != State.NONE) {
            return false;
        }
        startFight();
        return this.dimensionState == State.FIGHTING;
    }

    /** Debug command hook: fast-forwards the collapse so that {@code remainingTicks} are left. */
    public boolean debugSetCollapse(int remainingTicks) {
        if (this.dimensionState != State.FIGHTING && this.dimensionState != State.DEAD) {
            return false;
        }
        this.dimensionState = State.DEAD;
        this.deathTicks = Math.max(0, COLLAPSE_DURATION - remainingTicks);
        this.warningsSend = 0;
        while (this.warningsSend < COLLAPSE_WARNINGS.length && this.deathTicks >= COLLAPSE_WARNINGS[this.warningsSend].tick()) {
            this.warningsSend++;
        }
        return true;
    }

    public void sendMessage(Component component) {
        level.players().forEach(x -> {
            x.sendSystemMessage(component);
            x.sendOverlayMessage(component);
        });
    }

    private void sendOverlayMessage(Component component) {
        level.players().forEach(x -> x.sendOverlayMessage(component));
    }

    public void draculaDied(Dracula dracula) {
        if (!dracula.getUUID().equals(this.draculaId)) {
            return;
        }
        this.dimensionState = State.DEAD;
        this.applyAward();
        this.event.clear();
        this.activatePortal();
        this.sendMessage(Component.translatable("message.vampirism.dracula.defeated"));
    }

    public void applyAward() {
        Set<ServerPlayer> players = this.event.getPlayers();

        for (ServerPlayer player : players) {
            ModAdvancements.TRIGGER_DRACULA_WIN.get().trigger(player);
            player.giveExperiencePoints(500);
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            handler.factionPlayer(ModFactions.VAMPIRE).ifPresent(vampire -> {
                if (vampire.getLordLevel() == vampire.getMaxLordLevel()) {
                    handler.setFaction(LevelingChange.builder().add(new IDraculaPlayer.DraculaChange()).build());
                }
            });
        }
    }

    /**
     * Builds and activates the victory exit portal at the {@link #RETURN_PORTAL_MARKER} structure marker inside the
     * castle (the marker rotation encodes the portal facing), falling back to the dimension spawn point.
     */
    public void activatePortal() {
        List<Marker> markers = findMarkers(this.level, RETURN_PORTAL_MARKER);
        BlockPos anchor;
        Direction facing;
        if (markers.isEmpty()) {
            anchor = VelmorraDimension.SPAWN_POINT;
            facing = Direction.NORTH;
        } else {
            Marker marker = markers.get(0);
            anchor = marker.blockPosition();
            facing = Direction.fromYRot(marker.getYRot());
        }
        BlockPos frameStart = anchor.below().relative(facing.getCounterClockWise(), 2);
        VelmorraPortalShape.buildFrame(this.level, frameStart, facing);
        VelmorraPortalShape.findEmptyPortalShape(this.level, anchor).ifPresentOrElse(shape -> {
            shape.activate(this.level);
            this.sendMessage(Component.translatable("message.vampirism.velmorra.portal_opened"));
        }, () -> LOGGER.error("Failed to activate the return portal at {}", anchor));
    }

    @Override
    public void serialize(ValueOutput output) {
        output.storeNullable("dracula", UUIDUtil.CODEC, this.draculaId);
        output.store("state", State.CODEC, this.dimensionState);
        output.putInt("death_ticks", this.deathTicks);
        output.putInt("warnings_send", this.warningsSend);
        output.store("portals", GlobalPos.CODEC.listOf(), List.copyOf(this.portalLocations));
        output.store("chalices", BlockPos.CODEC.listOf(), List.copyOf(this.chaliceReferences.keySet()));
    }

    @Override
    public void deserialize(ValueInput input) {
        this.draculaId = input.read("dracula", UUIDUtil.CODEC).orElse(null);
        this.dimensionState = input.read("state", State.CODEC).orElse(State.NONE);
        this.deathTicks = input.getIntOr("death_ticks", 0);
        this.warningsSend = input.getIntOr("warnings_send", 0);
        input.read("portals", GlobalPos.CODEC.listOf()).ifPresent(this.portalLocations::addAll);
        // block states cannot be read while the level is loading, re-validate on the first tick
        input.read("chalices", BlockPos.CODEC.listOf()).ifPresent(this.pendingChalices::addAll);
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

    private record Warning(int tick, String translationKey) { }

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
