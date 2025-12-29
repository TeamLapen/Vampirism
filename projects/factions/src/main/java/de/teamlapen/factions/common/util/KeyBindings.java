package de.teamlapen.factions.common.util;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.InputEvent;

import java.util.ArrayList;
import java.util.List;

public record KeyBindings(List<KeyConfig> bindings) {

    public KeyBindings(List<KeyConfig> bindings) {
        this.bindings = ImmutableList.copyOf(bindings);
    }

    public void handleInputEvent(InputEvent event, int action) {
        if (action == InputConstants.PRESS) {
            for (KeyConfig config : this.bindings) {
                if (config.isDown()) {
                    config.run();
                    break;
                }
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public record KeyConfig(KeyMapping mapping, Runnable action, boolean consume) {

        public boolean isDown() {
            return this.consume ? this.mapping.consumeClick() : this.mapping.isDown();
        }

        public void run() {
            this.action.run();
        }
    }

    public static class Builder {
        private final List<KeyConfig> bindings = new ArrayList<>();

        public Builder addBinding(KeyMapping mapping, Runnable action, boolean consume) {
            this.bindings.add(new KeyConfig(mapping, action, consume));
            return this;
        }

        public Builder addBinding(KeyMapping mapping, Runnable action) {
            return this.addBinding(mapping, action, true);
        }

        public KeyBindings build() {
            return new KeyBindings(this.bindings);
        }
    }
}
