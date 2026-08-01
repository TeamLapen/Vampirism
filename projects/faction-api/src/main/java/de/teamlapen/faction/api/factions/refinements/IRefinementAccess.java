package de.teamlapen.faction.api.factions.refinements;

import net.minecraft.core.Holder;

import java.util.Optional;

public interface IRefinementAccess {

    boolean isRefinementEquipped(Holder<IRefinement> refinement);

    Optional<IRefinementHandler> asHandler();

    IRefinementAccess EMPTY = new IRefinementAccess() {
        @Override
        public boolean isRefinementEquipped(Holder<IRefinement> refinement) {
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
            public boolean isRefinementEquipped(Holder<IRefinement> refinement) {
                return handler.isRefinementEquipped(refinement);
            }

            @Override
            public Optional<IRefinementHandler> asHandler() {
                return Optional.of(handler);
            }
        };
    }
}
