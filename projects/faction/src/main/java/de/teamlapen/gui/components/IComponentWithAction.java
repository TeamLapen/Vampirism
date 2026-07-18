package de.teamlapen.gui.components;

import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public interface IComponentWithAction {

    Component component();

    Runnable action();

    Consumer<Boolean> onHover();

    record ComponentWithAction(Component component, Runnable action, Consumer<Boolean> onHover) implements IComponentWithAction {

    }

    static IComponentWithAction of(Component component) {
        return new ComponentWithAction(component, () -> {}, (_) -> {});
    }

    static IComponentWithAction of(Component component, Runnable action) {
        return new ComponentWithAction(component, action, (_) -> {});
    }

    static IComponentWithAction of(Component component, Runnable action, Consumer<Boolean> onHover) {
        return new ComponentWithAction(component, action, onHover);
    }
}
