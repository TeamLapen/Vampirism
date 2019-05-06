package de.teamlapen.lib.lib.util;


import com.mojang.brigadier.builder.ArgumentBuilder;

import net.minecraft.command.CommandSource;

/**
 * Basic command not really necessary since CommandTreeBase exists, but keeping it for future additions
 */
public abstract class BasicCommand {

    protected final static int PERMISSION_LEVEL_ALL = 0;
    protected final static int PERMISSION_LEVEL_CHEAT = 2;
    protected final static int PERMISSION_LEVEL_ADMIN = 3;
    protected final static int PERMISSION_LEVEL_FULL = 4;

    public static ArgumentBuilder<CommandSource, ?> register() {
        return null;
    };
}