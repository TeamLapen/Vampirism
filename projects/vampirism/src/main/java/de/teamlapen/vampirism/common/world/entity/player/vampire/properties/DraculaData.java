package de.teamlapen.vampirism.common.world.entity.player.vampire.properties;

import de.teamlapen.faction.api.world.entities.extensions.IPlayer;
import de.teamlapen.sync.PropertyParentSync;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IWingsEntity;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EquipmentSlot;

public class DraculaData extends PropertyParentSync {

    private boolean isDracula;
    private final AnimationState flyAnimationState = new AnimationState();
    private final AnimationState growAnimationState = new AnimationState();
    private IWingsEntity.WingsState wingsState = IWingsEntity.WingsState.CLOSED;
    private int growTicks;

    private final IPlayer player;

    public DraculaData(VampirePlayer player) {
        super(player);
        this.player = player;
    }

    @Override
    protected void registerProperties() {
        this.registerProperty(VIdentifier.mod("id_dracula")).simple(false, () -> this.isDracula, b -> this.isDracula = b);
        this.registerProperty(VIdentifier.mod("wings_state")).simple(IWingsEntity.WingsState.CLOSED, () -> this.wingsState, this::switchState);
    }

    public boolean isDracula() {
        return this.isDracula;
    }

    public AnimationState flyAnimationState() {
        return this.flyAnimationState;
    }

    public AnimationState growAnimationState() {
        return this.growAnimationState;
    }

    public IWingsEntity.WingsState getWingsState() {
        return this.wingsState;
    }

    public void tick() {
        if (this.isDracula) {
            this.growTicks++;
            switch (this.wingsState) {
                case OPENING -> {
                    if (growTicks > IWingsEntity.GROW_TICKS) {
                        switchState(IWingsEntity.WingsState.OPEN);
                    }
                    if (player.asEntity().isFallFlying()) {
                        switchState(IWingsEntity.WingsState.FLYING);
                    }
                }
                case CLOSING -> {
                    if (growTicks > IWingsEntity.GROW_TICKS) {
                        switchState(IWingsEntity.WingsState.CLOSED);
                    }
                }
                case OPEN -> {
                    if (player.asEntity().isFallFlying()) {
                        switchState(IWingsEntity.WingsState.FLYING);
                    }
                }
                case FLYING -> {
                    if (!player.asEntity().isFallFlying()) {
                        switchState(IWingsEntity.WingsState.OPEN);
                    }
                }
            }
            updateAnimations();
        }
    }

    private void updateAnimations() {
        switch (this.wingsState) {
            case OPENING, CLOSING -> this.growAnimationState.startIfStopped(player.asEntity().tickCount);
            case OPEN, FLYING -> this.flyAnimationState.startIfStopped(player.asEntity().tickCount);
            case CLOSED -> {
                this.flyAnimationState.stop();
                this.growAnimationState.stop();
            }
        }
    }

    public boolean openWings() {
        if (this.isDracula) {
            switchState(IWingsEntity.WingsState.OPENING);
            player.asEntity().tryToStartFallFlying();
            return true;
        }
        return false;
    }

    public void closeWings() {
        switchState(IWingsEntity.WingsState.CLOSING);
    }

    public void toggleWings() {
        if (player.asEntity().getItemBySlot(EquipmentSlot.CHEST).has(DataComponents.GLIDER)) {
            return;
        }
        switch (this.wingsState) {
            case OPENING, OPEN, FLYING -> this.closeWings();
            case CLOSING, CLOSED -> this.openWings();
        }
    }

    private void switchState(IWingsEntity.WingsState state) {
        this.wingsState = state;
        this.growTicks = 0;
        switch (state) {
            case OPEN ->  {
                this.flyAnimationState.start(player.asEntity().tickCount);
                this.growAnimationState.stop();
            }
            case CLOSED -> {
                this.flyAnimationState.stop();
                this.growAnimationState.stop();
            }
            case OPENING, CLOSING -> {
                int startTicks = player.asEntity().tickCount;
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

    public void makeDracula() {
        this.isDracula = true;
    }

    public void removeDracula() {
        this.isDracula = false;
        this.wingsState = IWingsEntity.WingsState.CLOSED;
    }

}
