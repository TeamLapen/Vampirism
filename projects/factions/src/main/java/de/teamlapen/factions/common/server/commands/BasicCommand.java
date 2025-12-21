package de.teamlapen.factions.common.server.commands;

import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.PermissionProviderCheck;
import net.minecraft.server.permissions.PermissionSetSupplier;

public abstract class BasicCommand {

    protected final static int PERMISSION_LEVEL_ALL = 0;
    protected final static int PERMISSION_LEVEL_CHEAT = 2;
    protected final static int PERMISSION_LEVEL_ADMIN = 3;
    protected final static int PERMISSION_LEVEL_FULL = 4;

}