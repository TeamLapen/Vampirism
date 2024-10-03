package de.teamlapen.lib.lib.storage;

public final class UpdateParams {
    private boolean isForAllPlayer;
    private final boolean ignoreChanges;

    private UpdateParams(boolean isForAllPlayer, boolean ignoreChanges) {
        this.isForAllPlayer = isForAllPlayer;
        this.ignoreChanges = ignoreChanges;
    }

    public static UpdateParams defaults() {
        return new UpdateParams(false, false);
    }

    public static UpdateParams forAllPlayer() {
        return new UpdateParams(true, false);
    }

    public static UpdateParams ignoreChanged() {
        return new UpdateParams(false, true);
    }

    public static UpdateParams all() {
        return new UpdateParams(true, true);
    }

    public boolean isForAllPlayer() {
        return isForAllPlayer;
    }

    public boolean ignoreChanges() {
        return ignoreChanges;
    }

    public void markForAllPlayer() {
        this.isForAllPlayer = true;
    }

}
