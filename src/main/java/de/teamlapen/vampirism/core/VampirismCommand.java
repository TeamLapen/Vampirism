package de.teamlapen.vampirism.core;

import de.teamlapen.lib.util.BasicCommand;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.config.Balance;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Central command for this mod
 */
public class VampirismCommand extends BasicCommand {

    public VampirismCommand() {
        if(VampirismMod.inDev){
            aliases.add("v");
        }
        addSub(new SubCommand() {
            @Override
            public boolean canCommandSenderUseCommand(ICommandSender var1) {
                return var1.canUseCommand(3,getCommandName());
            }

            @Override
            public String getCommandName() {
                return "resetBalance";
            }

            @Override
            public void processCommand(ICommandSender var1, String[] var2) {
                String cat;
                if(var2==null||var2.length==0){
                    cat="all";
                }
                else{
                    cat=var2[0];
                }
                boolean p=Balance.reset(cat);
                if(p){
                    var1.addChatMessage(new ChatComponentText("Successfully reset "+cat+" balance category. Please restart MC."));
                }
                else{
                    var1.addChatMessage(new ChatComponentText("Did not find "+cat+" balance category."));
                }
            }

            @Override
            public String getCommandUsage(ICommandSender var1) {
                return getCommandName()+" <all/[category]>";
            }

            @Override
            public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
                return (args.length==1)?getListOfStringsMatchingLastWord(args,getCategories()):null;
            }

            private String[] getCategories(){
                Set<String> categories= Balance.getCategories().keySet();
                String[] result=categories.toArray(new String[categories.size()+1]);
                result[result.length-1]="all";
                return result;
            }
        });
    }

    @Override
    public String getName() {
        return "vampirism";
    }

    public boolean isSenderCreative(ICommandSender sender) {
        if (VampirismMod.inDev)
            return true;
        return sender.canUseCommand(2, this.getName());
    }
}
