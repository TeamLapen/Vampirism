package de.teamlapen.vampirism;

import de.teamlapen.vampirism.entity.player.VampirePlayer;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;

import java.util.ArrayList;
import java.util.List;

/**
 * Main vampirism command
 */
public class VampirismCommand implements ICommand {

	@Override public String getCommandName() {
		return "vampirism";
	}

	@Override public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/vampirism <command>";
	}

	@Override public List getCommandAliases() {
		if (VampirismMod.inDev) {
			List l = new ArrayList();
			l.add("v");
			return l;
		}
		return null;
	}

	@Override public void processCommand(ICommandSender sender, String[] param) {
		if (sender instanceof EntityPlayer) {
			EntityPlayer p = (EntityPlayer) sender;
			VampirePlayer vampire = VampirePlayer.get(p);
			// -----------------
			if (param.length > 0) {
				if ("resetVampireLords".equals(param[0])) {
					VampireLordData.get(p.worldObj).reset();
					return;
				}
			}
			p.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.unknown_command"));
		}
	}

	@Override public boolean canCommandSenderUseCommand(ICommandSender sender) {
		if (VampirismMod.inDev)
			return true;
		return sender.canCommandSenderUseCommand(2, this.getCommandName());
	}

	@Override public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
		return null;
	}

	@Override public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		return false;
	}

	@Override public int compareTo(Object o) {
		return 0;
	}
}
