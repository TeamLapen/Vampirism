package de.teamlapen.lib.lib.storage;

public record UpdateParams(boolean isForAllPlayer, boolean ignoreChanges) {

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

}
