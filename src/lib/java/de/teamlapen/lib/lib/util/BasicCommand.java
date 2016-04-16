package de.teamlapen.lib.lib.util;


import de.teamlapen.lib.VampLib;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Basic command which manages subcommands
 * TODO maybe work with  {@link CommandException}
 */
public abstract class BasicCommand extends CommandBase {
    public static void sendMessage(ICommandSender target, String message) {
        String[] lines = message.split("\\n");
        for (String line : lines) {
            target.addChatMessage(new TextComponentString(line));
        }

    }

    protected final int PERMISSION_LEVEL_CHEAT = 2;
    protected final int PERMISSION_LEVEL_ADMIN = 3;
    protected final int PERMISSION_LEVEL_FULL = 4;
    protected List aliases;
    private List<SubCommand> subCommands;
    private SubCommand unknown;

    public BasicCommand() {
        aliases = new ArrayList();
        subCommands = new ArrayList<SubCommand>();
        unknown = new SubCommand() {
            @Override
            public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
                return null;
            }

            @Override
            public boolean canSenderUseCommand(ICommandSender var1) {
                return true;
            }

            @Override
            public String getCommandName() {
                return "unknown";
            }

            @Override
            public String getCommandUsage(ICommandSender var1) {
                return BasicCommand.this.getCommandUsage(var1);
            }

            @Override
            public void processCommand(ICommandSender var1, String[] var2) {
                sendMessage(var1, "Unknown command");
            }
        };
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] param) throws CommandException {
        if (param == null || param.length == 0) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
        if ("help".equals(param[0])) {
            if (param.length > 1) {
                sendMessage(sender, String.format("/%s %s", this.getCommandName(), getSub(param[1]).getCommandUsage(sender)));
            } else {
                String t = "Available subcommands: ";
                for (SubCommand s : subCommands) {
                    t += s.getCommandName() + ", ";
                }
                t += "Use /" + getCommandName() + " help <subcommand> to get more informations";
                sendMessage(sender, t);
            }
            return;

        }
        SubCommand cmd = getSub(param[0]);
        if (cmd.canSenderUseCommand(sender)) {

            try {
                cmd.processCommand(sender, ArrayUtils.subarray(param, 1, param.length));
            } catch (Exception e) {
                if (!(e instanceof CommandException)) {
                    VampLib.log.e("BasicCommand", e, "Failed to execute command %s with params %s", cmd, Arrays.toString(ArrayUtils.subarray(param, 1, param.length)));
                }
                throw e;
            }
            return;
        } else {
            sendMessage(sender, "You are not allowed to use this command");
        }
    }

    @Override
    public List getCommandAliases() {
        return aliases;
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return String.format("/%s <subcommand> <params> | Use /%s help to get all available subcommands", this.getCommandName(), this.getCommandName());
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        return (args.length == 1) ? getListOfStringsMatchingLastWord(args, getSubNames()) : getSubcommandTabCompletion(sender, args, pos);
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    protected void addSub(SubCommand s) {
        subCommands.add(s);
    }

    protected boolean canCommandSenderUseCheatCommand(ICommandSender sender) {
        return sender.canCommandSenderUseCommand(PERMISSION_LEVEL_CHEAT, this.getCommandName()) || (sender instanceof EntityPlayer) && ((EntityPlayer) sender).capabilities.isCreativeMode;
    }

    /**
     * Returns the subcommand matching the given name.
     * If no command is found, returns a default "unknown" command.
     *
     * @param name
     * @return
     */
    private SubCommand getSub(String name) {
        for (SubCommand s : subCommands) {
            if (s.getCommandName().equals(name)) return s;
        }
        return unknown;
    }

    private String[] getSubNames() {
        String[] names = new String[subCommands.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = subCommands.get(i).getCommandName();
        }
        return names;
    }

    private
    @Nonnull
    List getSubcommandTabCompletion(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length < 2) return Collections.EMPTY_LIST;
        List options = getSub(args[0]).addTabCompletionOptions(sender, ArrayUtils.subarray(args, 1, args.length), pos);
        return options == null ? Collections.emptyList() : options;
    }

    public interface SubCommand {
        List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos);

        boolean canSenderUseCommand(ICommandSender var1);

        String getCommandName();

        String getCommandUsage(ICommandSender var1);

        void processCommand(ICommandSender var1, String[] var2) throws CommandException;
    }

}