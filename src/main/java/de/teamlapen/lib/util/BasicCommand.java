package de.teamlapen.lib.util;


import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic command which manages subcommands
 */
public abstract class BasicCommand extends CommandBase {

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

            @Override
            public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
                return null;
            }
        };
    }

    public static void sendMessage(ICommandSender target, String message) {
        String[] lines = message.split("\\n");
        for (String line : lines) {
            target.addChatMessage(new ChatComponentText(line));
        }

    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return String.format("/%s <subcommand> <params> | Use /%s help to get all available subcommands", this.getCommandName(), this.getCommandName());
    }

    @Override
    public void processCommand(ICommandSender sender, String[] param) throws CommandException {
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
    public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return (args.length==1)?getListOfStringsMatchingLastWord(args,getSubNames()):getSubcommandTabCompletion(sender, args, pos);
    }

    private List getSubcommandTabCompletion(ICommandSender sender,String[] args,BlockPos pos){
        if(args.length<2)return null;
        return getSub(args[0]).addTabCompletionOptions(sender,ArrayUtils.subarray(args,1,args.length),pos);
    }

    @Override
    public List getCommandAliases() {
        return aliases;
    }


    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    /**
     * Returns the subcommand matching the given name.
     * If no command is found, returns a default "unknown" command.
     * @param name
     * @return
     */
    private SubCommand getSub(String name) {
        for (SubCommand s : subCommands) {
            if (s.getCommandName().equals(name)) return s;
        }
        return unknown;
    }

    protected void addSub(SubCommand s) {
        subCommands.add(s);
    }

    private String[] getSubNames(){
        String[] names=new String[subCommands.size()];
        for(int i=0;i<names.length;i++){
            names[i]=subCommands.get(i).getCommandName();
        }
        return names;
    }

    public interface SubCommand {
        boolean canCommandSenderUseCommand(ICommandSender var1);

        String getCommandName();

        void processCommand(ICommandSender var1, String[] var2);

        String getCommandUsage(ICommandSender var1);

        List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos);
    }

 }