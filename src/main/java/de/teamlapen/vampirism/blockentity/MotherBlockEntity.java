package de.teamlapen.vampirism.blockentity;

import de.teamlapen.lib.lib.util.SpawnHelper;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.mother.IRemainsBlock;
import de.teamlapen.vampirism.blocks.mother.MotherTreeStructure;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.GhostEntity;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.ClientboundBossEventSoundPacket;
import de.teamlapen.vampirism.network.ClientboundPlayEventPacket;
import de.teamlapen.vampirism.particle.FlyingBloodParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MotherBlockEntity extends BlockEntity {

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, MotherBlockEntity e) {

        //Handle fight ---------------------------------
        if (e.isFrozen && e.freezeTimer-- <= 0) {
            e.unFreezeFight(level, blockPos, blockState);
        }
        if (!e.isFrozen && e.level != null && e.isIntact()) {
            if (e.level.getRandom().nextInt(50) == 0) {
                e.updateFightStatus();
                if (!e.activePlayers.isEmpty()) {
                    List<Triple<BlockPos, BlockState, IRemainsBlock>> vuls = e.getTreeStructure(false).getVerifiedVulnerabilities(level).filter(t -> t.getRight().isVulnerable(t.getMiddle())).toList();
                    if (!vuls.isEmpty()) {
                        for (ServerPlayer player : e.activePlayers) {
                            if (player.getAbilities().invulnerable) {
                                continue;
                            }
                            BlockPos p = vuls.get(e.level.getRandom().nextInt(vuls.size())).getLeft();
                            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 5 * 20, 2));
                            ModParticles.spawnParticlesServer(player.level(), new FlyingBloodParticleOptions(100, false, p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, 0.5f), player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ(), 5, 0.1f, 0.1f, 0.1f, 0);
                        }
                    }
                }
            }
            if (e.level.getRandom().nextFloat() < Math.max(0.02f, Math.min(0.1f, e.activePlayers.size() * 0.002f))) {
                var blocks = e.getTreeStructure(false).getCachedBlocks();
                if (e.level.getEntitiesOfClass(GhostEntity.class, e.getArea().inflate(10)).size() < Math.min(e.activePlayers.size() * 1.6, 10)) {
                    blocks.stream().skip(e.level.getRandom().nextInt(blocks.size())).findFirst().ifPresent(pos -> e.spawnGhost(level, pos));
                }
            }
        }
        //Handle destruction --------------------------------
        if (e.level != null && e.destructionTimer > 0) {
            if (e.destructionTimer++ % 3 == 0) {
                MotherTreeStructure structure = e.getTreeStructure(false);
                Optional<Set<BlockPos>> hierarchy = structure.popHierarchy();
                if (hierarchy.isPresent()) {
                    for (BlockPos p : hierarchy.get()) {
                        if (level.getBlockState(p).getBlock() instanceof IRemainsBlock) {
                            level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                            ModParticles.spawnParticlesServer(level, new DustParticleOptions(new Vector3f(0.7f, 0.7f, 0.7f), 1), p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5f, 20, 0.3, 0.3, 0.3, 0.01);
                            e.level.playSound(null, p, ModSounds.REMAINS_DEATH.get(), SoundSource.BLOCKS, 0.2f, 1f);
                        }
                    }
                } else {
                    //Destruction complete
                    e.destructionTimer = -1;
                    if (e.level != null) {
                        e.level.playSound(null, blockPos, ModSounds.MOTHER_DEATH.get(), SoundSource.BLOCKS, 2f, 0.8f);
                    }
                    e.concludeFight();
                }
            }
        }
        if (level.getGameTime() % 64 == 0) {
            AABB area = e.getArea();
            Stream.concat(e.activePlayers.stream(), e.bossEvent.getPlayers().stream()).distinct().filter(player -> area.distanceToSqr(player.position()) > 1600).toList().forEach(player -> {
                e.bossEvent.removePlayer(player);
                e.activePlayers.remove(player);
            });
            AABB inflate = area.inflate(5, 5, 5);
            AABB inflate2 = inflate.inflate(10, 10, 10);
            if (!e.activePlayers.isEmpty()) {
                inflate = inflate.inflate(10, 10, 10);
                inflate2 = inflate2.inflate(20, 10, 20);
            }
            if (e.isIntact()) {
                level.getEntitiesOfClass(ServerPlayer.class, inflate).forEach(e::addPlayer);
            }
            level.getEntitiesOfClass(ServerPlayer.class, inflate2).forEach(e::addPlayerToBossEvent);
        }
    }



    private final ServerBossEvent bossEvent = new ServerBossEvent(Component.translatable("block.vampirism.mother"), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
    private final Set<ServerPlayer> activePlayers = new HashSet<>();
    private final Set<UUID> involvedPlayers = new HashSet<>();
    /**
     * Cache structure. Should be mostly valid because blocks cannot be destroyed by survival player. Is (unreliably) invalidated when a block is destroyed nonetheless.
     */
    private MotherTreeStructure cachedStructure;
    private boolean isFrozen = false;
    private int freezeTimer = 0;
    @Nullable
    private AABB area;

    public MotherBlockEntity(BlockPos pos, BlockState state) {
        super(ModTiles.MOTHER.get(), pos, state);
        this.bossEvent.setProgress(1);
        this.bossEvent.setPlayBossMusic(true);
    }

    /**
     * Describes the destruction process
     * <p>== 0: Intact</p>
     * <p>> 0: Increasing, destruction in progress</p>
     * <p>== -1: Structure, destroyed, mother is vulnerable</p>
     */
    private int destructionTimer = 0;

    public boolean isCanBeBroken() {
        return this.destructionTimer == -1;
    }

    public boolean isIntact() {
        return this.destructionTimer == 0;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.destructionTimer = tag.getInt("destruction_timer");
        this.isFrozen = tag.getBoolean("is_frozen");
        this.freezeTimer = tag.getInt("freeze_timer");
        if (this.isFrozen) {
            this.bossEvent.setColor(BossEvent.BossBarColor.WHITE);
        }
        this.involvedPlayers.clear();
        if (tag.contains("involved_players", Tag.TAG_LIST)) {
            ListTag involvedPlayers = tag.getList("involved_players", 11);
            for (Tag involvedPlayer : involvedPlayers) {
                this.involvedPlayers.add(NbtUtils.loadUUID(involvedPlayer));
            }
        }
    }

    /**
     * Call this when a block that belongs to this structure is removed
     */
    public void onStructureBlockRemoved() {
        this.cachedStructure = null;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.bossEvent.removeAllPlayers();
        this.activePlayers.clear();
    }

    private void addPlayer(Player player) {
        if (player instanceof ServerPlayer serverPlayer && !this.activePlayers.contains(serverPlayer)) {
            updateFightStatus();
            addPlayerToBossEvent(serverPlayer);
            this.activePlayers.add(serverPlayer);
        }
    }

    private void addPlayerToBossEvent(ServerPlayer player) {
        this.bossEvent.addPlayer(player);
        VampirismMod.dispatcher.sendTo(new ClientboundBossEventSoundPacket(this.bossEvent.getId(), ModSounds.MOTHER_AMBIENT.getKey()), player);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void onVulnerabilityHit(LivingEntity entity, boolean destroyed) {
        if (entity instanceof ServerPlayer player){
            informAboutAttacker(player);
        }
        updateFightStatus();
        if (destroyed && isIntact()) {
            freezeFight();
        }
    }

    public void updateFightStatus() {
        List<Triple<BlockPos, BlockState, IRemainsBlock>> vuls = getTreeStructure(false).getVerifiedVulnerabilities(level).toList();
        List<Triple<BlockPos, BlockState, IRemainsBlock>> remaining = vuls.stream().filter(vul -> vul.getRight().isVulnerable(vul.getMiddle())).toList();
        if (!remaining.isEmpty()) {
            var remainingHealth = remaining.stream().mapToInt(s -> {
                var entity = level.getBlockEntity(s.getLeft());
                if (entity instanceof VulnerableRemainsBlockEntity vulnerable) {
                    return vulnerable.getHealth();
                }
                return VulnerableRemainsBlockEntity.MAX_HEALTH;
            }).sum();
            this.bossEvent.setProgress(remainingHealth / ((float) vuls.size() * VulnerableRemainsBlockEntity.MAX_HEALTH));
        } else {
            this.bossEvent.setProgress(0);
            this.endFight();
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("destruction_timer", this.destructionTimer);
        tag.putBoolean("is_frozen", this.isFrozen);
        tag.putInt("freeze_timer", this.freezeTimer);
        ListTag involvedPlayers = new ListTag();
        for (UUID involvedPlayer : this.involvedPlayers) {
            involvedPlayers.add(NbtUtils.createUUID(involvedPlayer));
        }
        tag.put("involved_players", involvedPlayers);
    }

    private void endFight() {
        this.activePlayers.forEach(p -> VampirismMod.dispatcher.sendTo(new ClientboundPlayEventPacket(2, getBlockPos(), 0), p));
        this.bossEvent.removeAllPlayers();
        this.bossEvent.setVisible(false);
        this.activePlayers.clear();
        this.initiateDestruction();
    }

    private void freezeFight() {
        spawnGhosts();
        this.isFrozen = true;
        this.freezeTimer = 20 * 20;
        getTreeStructure(false).getVerifiedVulnerabilities(this.level).forEach(vul -> vul.getRight().freeze(level, vul.getLeft(), vul.getMiddle()));
        this.bossEvent.setColor(BossEvent.BossBarColor.WHITE);
    }

    @NotNull
    private MotherTreeStructure getTreeStructure(boolean forceRefresh) {
        if (forceRefresh || this.cachedStructure == null) {
            this.cachedStructure = MotherTreeStructure.getTreeView(this.level, this.worldPosition);
        }
        return cachedStructure;
    }

    private void initiateDestruction() {
        this.getTreeStructure(true);
        this.destructionTimer = 1;
    }

    private void unFreezeFight(Level level, BlockPos blockPos, BlockState blockState) {
        this.isFrozen = false;
        getTreeStructure(false).getVerifiedVulnerabilities(this.level).forEach(vul -> vul.getRight().unFreeze(level, vul.getLeft(), vul.getMiddle()));
        this.bossEvent.setColor(BossEvent.BossBarColor.RED);
    }

    public void informAboutAttacker(ServerPlayer serverPlayer) {
        addPlayer(serverPlayer);
        this.involvedPlayers.add(serverPlayer.getUUID());
    }

    public Collection<ServerPlayer> involvedPlayers() {
        return this.activePlayers;
    }

    private void spawnGhost(Level level, BlockPos pos) {
        SpawnHelper.spawn(ModEntities.GHOST, level, ghost -> {
            ghost.setPos(Vec3.atCenterOf(pos));
            ghost.setHome(getArea().inflate(15));
        });
    }

    private AABB getArea() {
        if (this.area == null) {
            this.area = new AABB(this.worldPosition).inflate(9, 0,9).expandTowards(0, -10, 0).expandTowards(0, 4, 0);
        }
        return this.area;
    }

    private void spawnGhosts() {
        Set<BlockPos> vuls = this.getTreeStructure(false).getCachedBlocks();
        int size = this.level.getEntitiesOfClass(GhostEntity.class, this.getArea()).size();
        for(int i = size; i < Math.max(3, Math.min(this.activePlayers.size() * 1.6, 10)); i++) {
            vuls.stream().skip(level.getRandom().nextInt(vuls.size())).findFirst().ifPresent(pos -> this.spawnGhost(level, pos));
        }
    }

    public void concludeFight() {
        //noinspection DataFlowIssue
        Set<LivingEntity> involvedEntities = this.involvedPlayers.stream().map(((ServerLevel) this.level)::getEntity).filter(LivingEntity.class::isInstance).filter(s -> !s.isSpectator()).map(LivingEntity.class::cast).collect(Collectors.toSet());
        for (LivingEntity livingentity : involvedEntities) {
            if (livingentity instanceof ServerPlayer serverplayer) {
                ModAdvancements.TRIGGER_MOTHER_WIN.trigger(serverplayer);
                serverplayer.awardStat(ModStats.mother_defeated, 1);
                FactionPlayerHandler.getOpt(serverplayer).filter(s -> s.getCurrentFaction() != null && s.getCurrentLevel() < s.getCurrentFaction().getHighestReachableLevel()).ifPresent(handler -> {
                    handler.setFactionLevel(handler.getCurrentFaction(), handler.getCurrentLevel() + 1);
                });
            }
        }
    }
}
