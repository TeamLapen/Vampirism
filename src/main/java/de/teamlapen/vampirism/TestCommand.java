package de.teamlapen.vampirism;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import de.teamlapen.vampirism.block.BlockBloodAltar1;
import de.teamlapen.vampirism.block.BlockCoffin;
import de.teamlapen.vampirism.castleDim.TeleporterCastle;
import de.teamlapen.vampirism.entity.EntityDracula;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.network.SpawnCustomParticlePacket;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar1;
import de.teamlapen.vampirism.util.BasicCommand;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.VampireLordData;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;

import java.util.List;

/**
 * Basic command on which all other commands should depend on
 * 
 * @author Max
 * 
 */
public class TestCommand extends BasicCommand {

	private abstract class TestSubCommand implements SubCommand {

		@Override
		public boolean canCommandSenderUseCommand(ICommandSender var1) {
			return true;
		}

		@Override
		public void processCommand(ICommandSender var1, String[] var2) {
			if (var1 instanceof EntityPlayer) {
				EntityPlayer p = (EntityPlayer) var1;
				VampirePlayer vampire = VampirePlayer.get(p);
				processCommand(var1, p, vampire, var2);
			}
		}

		@Override
		public String getCommandUsage(ICommandSender var1) {
			return this.getCommandName();
		}


		protected abstract void processCommand(ICommandSender sender, EntityPlayer player, VampirePlayer vampire, String[] param);
	}

	public TestCommand() {
		aliases.add("vtest");
		addSub(new TestSubCommand() {
			@Override
			protected void processCommand(ICommandSender sender, EntityPlayer player, VampirePlayer vampire, String[] param) {
				vampire.setLevel(REFERENCE.HIGHEST_REACHABLE_LEVEL);
				VampireLordData.get(player.worldObj).makeLord(player);
				MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText(sender.getCommandSenderName() + " made himself a Vampire Lord"));
			}

			@Override
			public String getCommandName() {
				return "lord";
			}
		});
		addSub(new TestSubCommand() {
			@Override
			protected void processCommand(ICommandSender sender, EntityPlayer player, VampirePlayer vampire, String[] param) {
				if (player instanceof EntityPlayerMP) {
					if (player.dimension != VampirismMod.castleDimensionId) {
						((EntityPlayerMP) player).mcServer.getConfigurationManager().transferPlayerToDimension(((EntityPlayerMP) player), VampirismMod.castleDimensionId, new TeleporterCastle(MinecraftServer.getServer().worldServerForDimension(VampirismMod.castleDimensionId)));
					}
					return;
				}
			}

			@Override
			public String getCommandName() {
				return "ddim";
			}
		});

		addSub(new TestSubCommand() {
			@Override
			protected void processCommand(ICommandSender sender, EntityPlayer player, VampirePlayer vampire, String[] param) {
				if (param.length < 1) return;
				int id = Integer.parseInt(param[0]);
					int amount=10;
				if (param.length > 1) {
					amount = Integer.parseInt(param[1]);
					}
					NBTTagCompound nbt=new NBTTagCompound();
				nbt.setInteger("id", player.getEntityId());
				IMessage m = new SpawnCustomParticlePacket(id, player.posX, player.posY, player.posZ, amount, nbt);
					VampirismMod.modChannel.sendToAll(m);
					return;
			}

			@Override
			public String getCommandUsage(ICommandSender var1) {
				return this.getCommandName() + " <id> (<amount>)";
			}

			@Override
			public String getCommandName() {
				return "part";
			}
		});

		addSub(new TestSubCommand() {
			@Override
			protected void processCommand(ICommandSender sender, EntityPlayer player, VampirePlayer vampire, String[] param) {
				List l = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.expand(3, 2, 3));
					for(Object o:l){
						if(o instanceof EntityCreature){
							sendMessage(sender,o.getClass().getName());
						}
						else{
							sendMessage(sender, "Not biteable " + o.getClass().getName());
						}
					}
			}

			@Override
			public String getCommandName() {
				return "entity";
			}
		});
		addSub(new TestSubCommand() {
			@Override
			protected void processCommand(ICommandSender sender, EntityPlayer player, VampirePlayer vampire, String[] param) {
				List l = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.expand(3, 2, 3));
				for (Object o : l) {
					if (o instanceof EntityLivingBase) {
						boolean flag = Helper.canReallySee((EntityLivingBase) o, player, false);
						sendMessage(sender, "Result " + flag);
						return;
					}
				}
			}

			@Override
			public String getCommandName() {
				return "look";
			}
		});
		addSub(new TestSubCommand() {
			@Override
			protected void processCommand(ICommandSender sender, EntityPlayer player, VampirePlayer vampire, String[] param) {
				List l = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.expand(50, 50, 50));
					for(Object o:l){
						if(o instanceof EntityDracula){
							sendMessage(sender, o.toString());
						}
					}

			}

			@Override
			public String getCommandName() {
				return "dracula";
			}
		});
		addSub(new TestSubCommand() {
			@Override
			protected void processCommand(ICommandSender sender, EntityPlayer player, VampirePlayer vampire, String[] param) {
				MovingObjectPosition pos = Helper.getPlayerLookingSpot(player, 0);
					if(pos!=null&&MovingObjectPosition.MovingObjectType.BLOCK.equals(pos.typeOfHit)){
						sendMessage(sender, "Block: " + player.worldObj.getBlock(pos.blockX, pos.blockY, pos.blockZ) + " Meta: " + player.worldObj.getBlockMetadata(pos.blockX, pos.blockY, pos.blockZ));
					}
			}

			@Override
			public String getCommandName() {
				return "block";
			}
		});
		addSub(new TestSubCommand() {
			@Override
			protected void processCommand(ICommandSender sender, EntityPlayer player, VampirePlayer vampire, String[] param) {
				MovingObjectPosition pos = Helper.getPlayerLookingSpot(player, 0);
				if (pos != null && MovingObjectPosition.MovingObjectType.BLOCK.equals(pos.typeOfHit)) {
					Block b = player.worldObj.getBlock(pos.blockX, pos.blockY, pos.blockZ);
					if (b instanceof BlockBloodAltar1) {
						TileEntityBloodAltar1 altar1 = (TileEntityBloodAltar1) player.worldObj.getTileEntity(pos.blockX, pos.blockY, pos.blockZ);
						altar1.makeInfinite();
					}
				}
			}

			@Override
			public String getCommandName() {
				return "infinite";
			}
		});
		addSub(new TestSubCommand() {
			@Override
			protected void processCommand(ICommandSender sender, EntityPlayer player, VampirePlayer vampire, String[] param) {
				MovingObjectPosition pos = Helper.getPlayerLookingSpot(player, 0);
				if (pos != null && MovingObjectPosition.MovingObjectType.BLOCK.equals(pos.typeOfHit)) {
					Block b = player.worldObj.getBlock(pos.blockX, pos.blockY, pos.blockZ);
					if (b instanceof BlockCoffin) {
						((BlockCoffin) b).setCoffinOccupied(player.worldObj, pos.blockX, pos.blockY, pos.blockZ, null, true);
						TileEntity t = player.worldObj.getTileEntity(pos.blockX, pos.blockY, pos.blockZ);
						t.markDirty();
					}
				}
			}

			@Override
			public String getCommandName() {
				return "close";
			}
		});
	}

	@Override
	public void processCommand(ICommandSender sender, String[] param) {
		if (param != null && param.length == 1 && sender instanceof EntityPlayer) {
			try {
				VampirePlayer.get((EntityPlayer) sender).setLevel(Integer.parseInt(param[0]));
				MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText(sender.getCommandSenderName() + " changed his vampire level to " + VampirePlayer.get((EntityPlayer) sender).getLevel()));
				return;
			} catch (NumberFormatException e) {

			}
		}
		super.processCommand(sender, param);
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		if (VampirismMod.inDev)
			return true;
		return sender.canCommandSenderUseCommand(2, this.getCommandName());
	}

	@Override
	public String getCommandName() {
		if (VampirismMod.inDev) {
			return "test";
		}
		return "vtest";
	}

	@Deprecated
	public void processCommandOld(ICommandSender sender, String[] param) {
		if (sender instanceof EntityPlayer) {
			EntityPlayer p = (EntityPlayer) sender;
			VampirePlayer vampire = VampirePlayer.get(p);
			// -----------------
			if (param.length > 0) {
				if ("infinite".equals(param[0])) {

				}
				if ("close".equals(param[0])) {

					return;
				}

//				if ("cminions".equals(param[0])) {
//					List<IMinion> list = (List<IMinion>) vampire.getMinionHandler().getMinionListForDebug().clone();
//					for (IMinion m : list) {
//						if (m instanceof EntityVampireMinion) {
//							if (m instanceof EntitySaveableVampireMinion) {
//								((EntitySaveableVampireMinion) m).convertToRemote();
//							} else if (m instanceof EntityRemoteVampireMinion) {
//								((EntityRemoteVampireMinion) m).convertToSaveable();
//							}
//						}
//					}
//				}
//				if ("target".equals(param[0])) {
//					sendMessage(sender, Helper.entityToString(vampire.getMinionTarget()));
//
//				}


			}
		}

	}

}
