package de.teamlapen.vampirism;

import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.generation.WorldGenVampirism;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;

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
				if ("checkForVampireBiome".equals(param[0])) {
					if (Configs.disable_vampire_biome) {
						p.addChatComponentMessage(new ChatComponentText("The Vampire Viome is disabled in the config file"));
					} else {
						p.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.biome.looking_for_biome"));
						ChunkCoordIntPair pos = WorldGenVampirism.castleGenerator.findNearVampireBiome(p.worldObj, MathHelper.floor_double(p.posX), MathHelper.floor_double(p.posZ), 1000);
						if (pos == null) {
							p.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.biome.not_found"));
						} else if (p.capabilities.isCreativeMode) {
							p.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.biome.found").appendSibling(new ChatComponentText("[" + (pos.chunkXPos << 4) + "," + (pos.chunkZPos << 4) + "]")));
						} else {
							p.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.biome.found"));
						}
					}
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
