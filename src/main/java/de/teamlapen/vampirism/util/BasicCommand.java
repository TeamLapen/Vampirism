package de.teamlapen.vampirism.util;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Max on 05.08.2015.
 */
public abstract class BasicCommand implements ICommand {

    protected List aliases;
    private List<SubCommand> subCommands;
    private SubCommand unknown;

    public BasicCommand() {
        aliases = new ArrayList();
        subCommands = new ArrayList<SubCommand>();
        unknown = new SubCommand() {
            @Override
            public boolean canCommandSenderUseCommand(ICommandSender var1) {
                return true;
            }

            @Override
            public String getCommandName() {
                return "unknown";
            }

            @Override
            public void processCommand(ICommandSender var1, String[] var2) {
                sendMessage(var1, "Unknown command");
            }

            @Override
            public String getCommandUsage(ICommandSender var1) {
                return BasicCommand.this.getCommandUsage(var1);
            }
        };
    }

    public interface SubCommand {
        boolean canCommandSenderUseCommand(ICommandSender var1);

        String getCommandName();

        void processCommand(ICommandSender var1, String[] var2);

        String getCommandUsage(ICommandSender var1);
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return String.format("/%s <subcommand> <params> | Use /%s help to get all available subcommands", this.getCommandName(), this.getCommandName());
    }

    @Override
    public void processCommand(ICommandSender sender, String[] param) {
        if (param == null || param.length == 0) {
            sendMessage(sender, getCommandUsage(sender));
            return;
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
        if (cmd.canCommandSenderUseCommand(sender)) {
            cmd.processCommand(sender, ArrayUtils.subarray(param, 1, param.length));
            return;
        } else {
            sendMessage(sender, "You are not allowed to use this command");
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public List getCommandAliases() {
        return aliases;
    }

    public static void sendMessage(ICommandSender target, String message) {
        String[] lines = message.split("\\n");
        for (String line : lines) {
            target.addChatMessage(new ChatComponentText(line));
        }

    }

    private SubCommand getSub(String name) {
        for (SubCommand s : subCommands) {
            if (s.getCommandName().equals(name)) return s;
        }
        return unknown;
    }

    protected void addSub(SubCommand s) {
        subCommands.add(s);
    }

}
