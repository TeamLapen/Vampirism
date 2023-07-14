package de.teamlapen.vampirism.api.entity.convertible;

import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for entities that were bitten and then converted to a vampire.
 * When converted the old creature is removed and a new {@link IConvertedCreature} is spawned
 * Must only be implemented on subclasses of {@link PathfinderMob}
 */
public interface IConvertedCreature<T extends PathfinderMob> extends IVampireMob {

    class Data<T> {

        @Nullable
        public ResourceLocation texture;
    }

    Data<T> data();
}
