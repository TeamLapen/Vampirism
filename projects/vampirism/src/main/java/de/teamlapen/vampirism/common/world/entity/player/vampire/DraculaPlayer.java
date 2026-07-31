package de.teamlapen.vampirism.common.world.entity.player.vampire;

import de.teamlapen.faction.api.factions.IFactionExtension;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.level.FactionUpdate;
import de.teamlapen.faction.common.factions.FactionExtension;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.util.AttachmentSynchronization;
import de.teamlapen.faction.common.world.entities.IPlayerEventListener;
import de.teamlapen.sync.AttachmentSync;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IDraculaPlayer;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IWingsEntity;
import de.teamlapen.vampirism.common.core.ModAttachments;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentType;

public class DraculaPlayer extends FactionExtension implements IDraculaPlayer, IPlayerEventListener {

    private boolean isDracula;
    private final AnimationState flyAnimationState = new AnimationState();
    private final AnimationState growAnimationState = new AnimationState();
    private IWingsEntity.WingsState wingsState = IWingsEntity.WingsState.CLOSED;
    private int growTicks;
    private int skillPoints;

    public DraculaPlayer(Player player) {
        super(player);
    }

    @Override
    public AttachmentType<?> getType() {
        return ModAttachments.DRACULA_PLAYER.get();
    }

    @Override
    protected void registerProperties() {
        super.registerProperties();
        this.registerProperty(VIdentifier.mod("id_dracula")).simple(false, () -> this.isDracula, b -> this.isDracula = b);
        this.registerProperty(VIdentifier.mod("wings_state")).simple(IWingsEntity.WingsState.CLOSED, () -> this.wingsState, this::switchState);
        this.registerProperty(VIdentifier.mod("skill_points")).simple(0, () -> this.skillPoints, i -> this.skillPoints = Math.clamp(i, 0, 2));
    }

    @Override
    public boolean isDracula() {
        return this.isDracula;
    }

    @Override
    public AnimationState flyAnimationState() {
        return this.flyAnimationState;
    }

    @Override
    public AnimationState growAnimationState() {
        return this.growAnimationState;
    }

    @Override
    public IWingsEntity.WingsState getWingsState() {
        return this.wingsState;
    }

    @Override
    public int getSkillPoints() {
        return this.skillPoints;
    }

    public void awardSkillPoint() {
        this.skillPoints = Math.clamp(this.skillPoints + 1, 0, 2);
    }

    @Override
    public boolean openWings() {
        if (this.isDracula) {
            if (VampirePlayer.get(this.player).getSkillProperties().bat) {
                return false;
            }
            switchState(IWingsEntity.WingsState.OPENING);
            this.player.tryToStartFallFlying();
            return true;
        }
        return false;
    }

    @Override
    public void closeWings(boolean force) {
        switchState(force ? IWingsEntity.WingsState.CLOSED : IWingsEntity.WingsState.CLOSING);
    }

    @Override
    public void toggleWings() {
        if (this.player.getItemBySlot(EquipmentSlot.CHEST).has(DataComponents.GLIDER)) {
            return;
        }
        switch (this.wingsState) {
            case OPENING, OPEN, FLYING -> this.closeWings(false);
            case CLOSING, CLOSED -> this.openWings();
        }
    }

    @Override
    public void swingWings() {
        if (this.wingsState != WingsState.FLYING) {
            return;
        }
        Vec3 lookAngle = this.player.getLookAngle();
        Vec3 deltaMovement = this.player.getDeltaMovement();
        double speed = deltaMovement.lengthSqr();
        double speedMultiplier = Math.clamp(speed, 0, 1);

        var newDeltaMovement = deltaMovement
                .add(new Vec3(
                                lookAngle.x * 0.1 + (lookAngle.x * 1.5 - deltaMovement.x) * 0.5,
                                lookAngle.y * 0.1 + (lookAngle.y * 1.5 - deltaMovement.y) * 0.5,
                                lookAngle.z * 0.1 + (lookAngle.z * 1.5 - deltaMovement.z) * 0.5
                        ).scale(speedMultiplier)
                ).add(0, 0.1, 0);

        if (newDeltaMovement.lengthSqr() > 1) {
            newDeltaMovement = newDeltaMovement.normalize().scale(deltaMovement.length());
        }

        this.player.setDeltaMovement(newDeltaMovement);
        this.flyAnimationState.fastForward(5, 1);
    }

    private void switchState(IWingsEntity.WingsState state) {
        if (this.wingsState == state) return;
        if (state == IWingsEntity.WingsState.CLOSING && this.wingsState == IWingsEntity.WingsState.CLOSED) return;
        if (state == IWingsEntity.WingsState.OPENING && this.wingsState == IWingsEntity.WingsState.OPEN) return;
        this.wingsState = state;
        this.growTicks = 0;
        switch (state) {
            case OPEN -> {
                this.flyAnimationState.start(this.player.tickCount);
                this.growAnimationState.stop();
            }
            case CLOSED -> {
                this.flyAnimationState.stop();
                this.growAnimationState.stop();
            }
            case OPENING, CLOSING -> {
                int startTicks = this.player.tickCount;
                int currentTicks = this.growAnimationState.vampirism$startTick();
                if (currentTicks != Integer.MIN_VALUE) {
                    int div = startTicks - currentTicks;
                    if (div < IWingsEntity.GROW_TICKS) {
                        startTicks -= (int) IWingsEntity.GROW_TICKS - div;
                    }
                }
                this.growAnimationState.start(startTicks);
                this.flyAnimationState.stop();
            }
        }
    }

    private void updateAnimations() {
        switch (this.wingsState) {
            case OPENING, CLOSING -> this.growAnimationState.startIfStopped(this.player.tickCount);
            case OPEN, FLYING -> this.flyAnimationState.startIfStopped(this.player.tickCount);
            case CLOSED -> {
                this.flyAnimationState.stop();
                this.growAnimationState.stop();
            }
        }
    }

    private void makeDracula() {
        this.isDracula = true;
        this.awardSkillPoint();
    }

    private void removeDracula() {
        this.isDracula = false;
        this.skillPoints = 0;
        this.wingsState = IWingsEntity.WingsState.CLOSED;
    }

    @Override
    public void onLeaveFaction(Player player) {
        removeDracula();
        sync();
    }

    @Override
    public void setLevel(FactionUpdate change) {
        if (change.get(IDraculaPlayer.DraculaChange.KEY) != null) {
            makeDracula();
        } else if (this.isDracula) {
            boolean droppedLevel = change.getLevel() < getFaction().value().getHighestReachableLevel();
            boolean droppedLordLevel = change.hasLordLevelChange() && change.getLordLevel() < getFaction().value().getHighestLordLevel();
            if (droppedLevel || droppedLordLevel) {
                removeDracula();
            }
        }
        sync();
    }

    @Override
    public void onUpdate() {
        if (this.isDracula) {
            this.growTicks++;
            switch (this.wingsState) {
                case OPENING -> {
                    if (growTicks > IWingsEntity.GROW_TICKS) {
                        switchState(IWingsEntity.WingsState.OPEN);
                    }
                    if (this.player.isFallFlying()) {
                        switchState(IWingsEntity.WingsState.FLYING);
                    }
                }
                case CLOSING -> {
                    if (growTicks > IWingsEntity.GROW_TICKS) {
                        switchState(IWingsEntity.WingsState.CLOSED);
                    }
                }
                case OPEN -> {
                    if (this.player.isFallFlying()) {
                        switchState(IWingsEntity.WingsState.FLYING);
                    }
                }
                case FLYING -> {
                    if (!this.player.isFallFlying()) {
                        switchState(IWingsEntity.WingsState.OPEN);
                    }
                }
            }
            updateAnimations();
        }
    }
}
