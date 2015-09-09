package de.teamlapen.vampirism;

import de.teamlapen.vampirism.generation.WorldGenVampirism;
import de.teamlapen.vampirism.util.BasicCommand;
import de.teamlapen.vampirism.util.VampireLordData;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;

/**
 * Main vampirism command
 */
public class VampirismCommand extends BasicCommand {


	public VampirismCommand() {
		if (VampirismMod.inDev) {
			aliases.add("v");
		}
		addSub(new SubCommand() {
			@Override
			public boolean canCommandSenderUseCommand(ICommandSender var1) {
				return isSenderCreative(var1);
			}

			@Override
			public String getCommandName() {
				return "resetVampireLords";
			}

			@Override
			public void processCommand(ICommandSender var1, String[] var2) {
				if (var1 instanceof EntityPlayer) {
					VampireLordData.get(((EntityPlayer) var1).worldObj).reset();
				}

			}

			@Override
			public String getCommandUsage(ICommandSender var1) {
				return getCommandName();
			}
		});
		addSub(new SubCommand() {
			@Override
			public boolean canCommandSenderUseCommand(ICommandSender var1) {
				return true;
			}

			@Override
			public String getCommandName() {
				return "checkForVampireBiome";
			}

			@Override
			public void processCommand(ICommandSender var1, String[] var2) {
				if (!(var1 instanceof EntityPlayer)) return;
				EntityPlayer p = (EntityPlayer) var1;
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
			}

			@Override
			public String getCommandUsage(ICommandSender var1) {
				return getCommandName();
			}
		});
	}
	@Override public String getName() {
		return "vampirism";
	}

	public boolean isSenderCreative(ICommandSender sender) {
		if (VampirismMod.inDev)
			return true;
		return sender.canUseCommand(2, this.getName());
	}
}
