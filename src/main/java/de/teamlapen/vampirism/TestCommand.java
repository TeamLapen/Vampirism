package de.teamlapen.vampirism;

import java.util.List;

import de.teamlapen.vampirism.playervampire.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

/**
 * Basic command on which all other commands should depend on
 * @author Max
 *
 */
public class TestCommand implements ICommand {
	public static void sendMessage(ICommandSender target, String message) {
		String[] lines = message.split("\\n");
		for (String line : lines) {
			target.addChatMessage(new ChatComponentText(line));
		}

	}

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		return "test";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/test";
	}

	@Override
	public List getCommandAliases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] p_71515_2_) {
		if(sender instanceof EntityPlayer){
			EntityPlayer p = (EntityPlayer)sender;
			VampirePlayer.get(p).levelUp();
			sendMessage(sender,"You are a vampire now");
		}
		
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		// TODO Auto-generated method stub
		return false;
	}

}
