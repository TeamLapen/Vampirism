package de.teamlapen.lib.lib.client.gui.screens.radialmenu;

public enum SecondaryIconPosition {
    NORTH, EAST, SOUTH, WEST;

    public static SecondaryIconPosition getNextPositon(SecondaryIconPosition secondaryIconPosition) {
        return switch (secondaryIconPosition) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
        };
    }
}
