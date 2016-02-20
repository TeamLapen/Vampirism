package de.teamlapen.vampirism;

import de.teamlapen.vampirism.generation.WorldGenVampirism;
import de.teamlapen.vampirism.util.BasicCommand;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.VampireLordData;
import de.teamlapen.vampirism.util.VersionChecker;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import net.minecraft.world.ChunkCoordIntPair;

import java.util.List;

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
					p.addChatComponentMessage(new ChatComponentText("The Vampire Biome is disabled in the config file"));
				} else {
					int maxDist = 5000;
					if (var2.length > 0) {
						try {
							maxDist = Integer.parseInt(var2[0]);
						} catch (NumberFormatException e) {
							Logger.w("CheckVampireBiome", "Failed to parse max dist %s", var2[0]);
						}
						if (maxDist > 10000) {
							if (var2.length > 1 && "yes".equals(var2[1])) {

							} else {
								p.addChatMessage(new ChatComponentText("This will take a looong time. Please use '/" + getCommandUsage(var1) + " yes', if you are sure"));
								return;
							}
						}
					}
					p.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.biome.looking_for_biome"));
					ChunkCoordIntPair pos = WorldGenVampirism.castleGenerator.findNearVampireBiome(p.worldObj, MathHelper.floor_double(p.posX), MathHelper.floor_double(p.posZ), maxDist, var1);
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
				return getCommandName() + " <maxRadius>";
			}
		});
		addSub(new SubCommand() {
			@Override
			public boolean canCommandSenderUseCommand(ICommandSender var1) {
				return true;
			}

			@Override
			public String getCommandName() {
				return "changelog";
			}

			@Override
			public void processCommand(ICommandSender var1, String[] var2) {
				if (VersionChecker.newVersion == null) {
					var1.addChatMessage(new ChatComponentText("There is no new version available"));
					return;
				}
				List<String> changes = VersionChecker.newVersion.getChangeLog();
				var1.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Vampirism " + VersionChecker.newVersion.getModVersion() + "(" + VersionChecker.newVersion.getMcVersion() + ")"));
				for (String c : changes) {
					var1.addChatMessage(new ChatComponentText("-" + c));
				}
				var1.addChatMessage(new ChatComponentText(""));
				IChatComponent component = IChatComponent.Serializer.func_150699_a(VersionChecker.addVersionInfo(StatCollector.translateToLocal("text.vampirism.update_message")));
				var1.addChatMessage(component);
			}

			@Override
			public String getCommandUsage(ICommandSender var1) {
				return getCommandName();
			}
		});
		addSub(new SubCommand() {
			@Override
			public boolean canCommandSenderUseCommand(ICommandSender var1) {
				return isSenderCreative(var1);
			}

			@Override
			public String getCommandName() {
				return "lord_offline_time";
			}

			@Override
			public void processCommand(ICommandSender var1, String[] var2) {
				var1.addChatMessage(new ChatComponentText("Current lord has been offline for " + VampireLordData.get(var1.getEntityWorld()).getCurrentLordOfflineTime() + " ticks"));
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
				return "currentDimension";
			}

			@Override
			public void processCommand(ICommandSender var1, String[] var2) {
				if (var1 instanceof EntityPlayer) {
					EntityPlayer p = (EntityPlayer) var1;
					if (p.worldObj != null) {
						var1.addChatMessage(new ChatComponentText("Dimension ID: " + p.worldObj.provider.dimensionId));
					}
				}
			}

			@Override
			public String getCommandUsage(ICommandSender var1) {
				return getCommandName();
			}
		});
	}
	@Override public String getCommandName() {
		return "vampirism";
	}

	public boolean isSenderCreative(ICommandSender sender) {
		if (VampirismMod.inDev)
			return true;
		return sender.canCommandSenderUseCommand(2, this.getCommandName());
	}
}
