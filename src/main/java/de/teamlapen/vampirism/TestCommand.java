package de.teamlapen.vampirism;

import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import de.teamlapen.vampirism.entity.minions.EntityRemoteVampireMinion;
import de.teamlapen.vampirism.entity.minions.EntitySaveableVampireMinion;
import de.teamlapen.vampirism.entity.minions.EntityVampireMinion;
import de.teamlapen.vampirism.entity.minions.IMinion;
import de.teamlapen.vampirism.entity.minions.SaveableMinionHandler;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

/**
 * Basic command on which all other commands should depend on
 * 
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
	public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
		return null;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		if(VampirismMod.inDev)return true;
		return sender.canCommandSenderUseCommand(2, this.getCommandName());
	}

	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public String getCommandName() {
		if(VampirismMod.inDev){
			return "test";
		}
		return "vtest";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/vtest";
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		return false;
	}

	
	@Override
	public void processCommand(ICommandSender sender, String[] param) {
		if (sender instanceof EntityPlayer) {
			EntityPlayer p = (EntityPlayer) sender;
			VampirePlayer vampire=VampirePlayer.get(p);
			// -----------------
			if (param.length > 0) {
				if("lord".equals(param[0])){
					vampire.setLevel(REFERENCE.HIGHEST_REACHABLE_LEVEL);
					if(vampire.setVampireLord(true)){
						sendMessage(sender,"You are now a vampire lord");
					}

					return;
				}
				if("info".equals(param[0])){
					//TODO add entity string output
				}
				if("minions".equals(param[0])){
					for(IMinion m:vampire.getMinionHandler().getMinionListForDebug()){
						sendMessage(sender,m.getRepresentingEntity().toString());
					}
					sendMessage(sender,vampire.getMinionHandler().getMinionCount()+"/"+vampire.getMaxMinionCount());
					return;
				}
				if("cminions".equals(param[0])){
					List<IMinion> list=(List<IMinion>) vampire.getMinionHandler().getMinionListForDebug().clone();
					for(IMinion m:list){
						if(m instanceof EntityVampireMinion){
							if(m instanceof EntitySaveableVampireMinion){
								((EntitySaveableVampireMinion)m).convertToRemote();
							}
							else if(m instanceof EntityRemoteVampireMinion){
								((EntityRemoteVampireMinion)m).convertToSaveable();
							}
						}
					}
				}
				if("target".equals(param[0])){
						sendMessage(sender,Helper.entityToString(vampire.getMinionTarget()));
					
				}
				try {
					VampirePlayer.get(p).setLevel(Integer.parseInt(param[0]));
				} catch (NumberFormatException e) {
					Logger.e("Testcommand", param[0] + " is no Integer");
				}
			} else {
				VampirePlayer.get(p).levelUp();
			}

			if (VampirePlayer.get(p).getLevel() == 1) {
				sendMessage(sender, "You are a vampire now");
			}
		}

	}

}
