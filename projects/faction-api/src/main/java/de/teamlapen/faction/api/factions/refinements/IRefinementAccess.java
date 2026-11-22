package de.teamlapen.faction.api.factions.refinements;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;

public interface IRefinementAccess {

    boolean isRefinementEquipped(ResourceKey<IRefinement> refinement);

    default boolean isRefinementEquipped(Holder<IRefinement> refinement) {
        return refinement.unwrapKey().map(this::isRefinementEquipped).orElse(false);
    }

    Optional<IRefinementHandler> asHandler();

    IRefinementAccess EMPTY = new IRefinementAccess() {
        @Override
        public boolean isRefinementEquipped(ResourceKey<IRefinement> refinement) {
            return false;
        }

        @Override
        public Optional<IRefinementHandler> asHandler() {
            return Optional.empty();
        }
    };

    static IRefinementAccess from(IRefinementHandler handler) {
        return new IRefinementAccess() {
            @Override
            public boolean isRefinementEquipped(ResourceKey<IRefinement> refinement) {
                return handler.isRefinementEquipped(refinement);
            }

            @Override
            public Optional<IRefinementHandler> asHandler() {
                return Optional.of(handler);
            }
        };
    }
}
