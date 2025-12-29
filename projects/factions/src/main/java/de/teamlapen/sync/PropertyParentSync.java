package de.teamlapen.sync;

public abstract class PropertyParentSync extends PropertySync {

    private final PropertySync parent;

    public PropertyParentSync(PropertySync parent) {
        this.parent = parent;
    }

    @Override
    public void sync() {
        this.parent.sync();
    }
}
