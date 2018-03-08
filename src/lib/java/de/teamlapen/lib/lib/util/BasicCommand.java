package de.teamlapen.lib.lib.util;


import com.google.common.collect.Lists;
import net.minecraftforge.server.command.CommandTreeBase;

import java.util.List;

/**
 * Basic command not really necessary since CommandTreeBase exists, but keeping it for future additions
 */
public abstract class BasicCommand extends CommandTreeBase {

    protected final int PERMISSION_LEVEL_ALL = 0;
    protected final int PERMISSION_LEVEL_CHEAT = 2;
    protected final int PERMISSION_LEVEL_ADMIN = 3;
    protected final int PERMISSION_LEVEL_FULL = 4;

    protected final List<String> aliases = Lists.newArrayList();

    public BasicCommand() {
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }


    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}