package de.teamlapen.vampirism.entity.player.runnable;

import de.teamlapen.vampirism.api.entity.player.runnable.ISaveablePlayerRunnable;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DispatchedDash implements ISaveablePlayerRunnable<IVampirePlayer> {

    private static final ResourceLocation ID = new ResourceLocation("vampirism", "dispatched_dash");
    private final Vec3 targetPosition;
    private Vec3 sourcePosition;
    private double distance;
    private int steps;

    public DispatchedDash(Vec3 target) {
        this.targetPosition = target;
    }

    private DispatchedDash(CompoundTag nbt) {
        this.targetPosition = new Vec3(nbt.getDouble("targetX"), nbt.getDouble("targetY"), nbt.getDouble("targetZ"));
        this.sourcePosition = new Vec3(nbt.getDouble("sourceX"), nbt.getDouble("sourceY"), nbt.getDouble("sourceZ"));
        this.distance = nbt.getDouble("distance");
        this.steps = nbt.getInt("steps");
    }

    @Override
    public void setUp(@NotNull IVampirePlayer factionPlayer) {
        Player player = factionPlayer.getRepresentingPlayer();
        this.sourcePosition = player.position();
        double distance = this.targetPosition.distanceTo(this.sourcePosition);
        this.steps = Math.max(1, Math.min((int) (distance / 6), 6));
        this.distance = distance / this.steps;
        player.level.playSound(null, this.sourcePosition.x, this.sourcePosition.y, this.sourcePosition.z, ModSounds.TELEPORT_AWAY.get(), SoundSource.PLAYERS, 1f, 1f);
    }

    @Override
    public void shutdown(@NotNull IVampirePlayer factionPlayer) {
        if (!factionPlayer.getRepresentingPlayer().level.getBlockState(factionPlayer.getRepresentingEntity().blockPosition()).isAir()) {
            factionPlayer.getRepresentingPlayer().teleportRelative(0, 1, 0);
        }
    }

    @Override
    public boolean run(@NotNull IVampirePlayer factionPlayer) {
        if (this.steps-- > 0) {
            Player player = factionPlayer.getRepresentingPlayer();
            Vec3 scale = this.targetPosition.subtract(player.position()).normalize().scale(this.distance);
            player.teleportRelative(scale.x, scale.y, scale.z);
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.TELEPORT_HERE.get(), SoundSource.PLAYERS, 1f, 1f);
            return this.steps <= 0;
        }
        return true;
    }

    @Override
    public CompoundTag writeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("targetX", this.targetPosition.x);
        tag.putDouble("targetY", this.targetPosition.y);
        tag.putDouble("targetZ", this.targetPosition.z);
        tag.putDouble("sourceX", this.sourcePosition.x);
        tag.putDouble("sourceY", this.sourcePosition.y);
        tag.putDouble("sourceZ", this.sourcePosition.z);
        tag.putDouble("distance", this.distance);
        tag.putInt("steps", this.steps);
        return tag;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    public static void register() {
        CONSTRUCTORS.put(ID, DispatchedDash::new);
    }
}
