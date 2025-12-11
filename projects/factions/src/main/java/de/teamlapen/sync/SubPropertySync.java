package de.teamlapen.sync;

public abstract class SubPropertySync extends PropertySync {

    private final PropertySync parent;

    public SubPropertySync(PropertySync parent) {
        this.parent = parent;
    }

    @Override
    public void sync() {
        this.parent.sync();
    }
}
