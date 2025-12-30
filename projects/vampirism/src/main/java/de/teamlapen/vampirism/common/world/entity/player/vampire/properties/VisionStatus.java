package de.teamlapen.vampirism.common.world.entity.player.vampire.properties;

import de.teamlapen.sync.PropertyParentSync;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.common.core.ModRegistries;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class VisionStatus extends PropertyParentSync implements ValueIOSerializable {
    private final VampirePlayer vampirePlayer;
    private final Set<ResourceKey<IVampireVision>> unlockedVisions = new HashSet<>();
    private @Nullable Holder<IVampireVision> vision;

    public VisionStatus(VampirePlayer vampire) {
        super(vampire);
        this.vampirePlayer = vampire;
    }

    public @Nullable Holder<IVampireVision> vision() {
        return this.vision;
    }

    public void deactivate() {
        if (this.vision != null) {
            this.vision.value().onDeactivated(vampirePlayer);
            vision = null;
        }
    }

    public void deactivate(ResourceKey<IVampireVision> vision) {
        if (this.vision != null && this.vision.is(vision)) {
            deactivate();
        }
    }

    public void tick() {
        if (this.vision != null) {
            this.vision.value().tick(vampirePlayer);
            if (!this.vision.value().isEnabled()) {
                deactivate();
            }
        }
    }

    public void switchVision() {
        List<ResourceKey<IVampireVision>> visions = ModRegistries.VAMPIRE_VISION.listElements().filter(x -> unlockedVisions.contains(x.getKey())).filter(x -> x.value().isEnabled()).map(Holder.Reference::getKey).toList();
        int newIndex;
        if (this.vision != null) {
            newIndex = visions.indexOf(this.vision.getKey()) + 1;
        } else {
            newIndex = 0;
        }
        var newVision = newIndex >= visions.size() ? null : visions.get(newIndex);
        activate(newVision);
    }

    public void unlockVision(ResourceKey<IVampireVision> vision) {
        this.unlockedVisions.add(vision);
    }

    public void lockVision(ResourceKey<IVampireVision> vision) {
        this.deactivate(vision);
        this.unlockedVisions.remove(vision);
    }

    public void activate(@Nullable ResourceKey<IVampireVision> vision) {
        if (this.vision != null && (vision != null && this.vision.is(vision))) {
            return;
        }
        if (vision != null && !this.unlockedVisions.contains(vision)) {
            return;
        }
        if (this.vision != null) {
            deactivate();
        }
        var holder = Optional.ofNullable(vision).flatMap(ModRegistries.VAMPIRE_VISION::get).orElse(null);
        if (holder != null) {
            if (holder.value().isEnabled()) {
                this.vision = holder;
                this.vision.value().onActivated(vampirePlayer);
            }
        } else {
            this.vision = null;
        }
    }

    @Override
    protected void registerProperties() {
        this.registerProperty(VIdentifier.mod("vision")).nullable(IVampireVision.CODEC).provider(() -> this.vision).commonLoader(v -> {
            var old = this.vision;
            this.vision = v;
            if (old != this.vision) {
                if (old != null) {
                    old.value().onDeactivated(vampirePlayer);
                }
                if (this.vision != null) {
                    this.vision.value().onActivated(vampirePlayer);
                }
                return true;
            }
            return false;
        }).register();
    }
}
