package de.teamlapen.vampirism;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import de.teamlapen.vampirism.block.BlockBloodAltar1;
import de.teamlapen.vampirism.block.BlockCoffin;
import de.teamlapen.vampirism.castleDim.TeleporterCastle;
import de.teamlapen.vampirism.entity.EntityDracula;
import de.teamlapen.vampirism.entity.minions.EntityRemoteVampireMinion;
import de.teamlapen.vampirism.entity.minions.EntitySaveableVampireMinion;
import de.teamlapen.vampirism.entity.minions.EntityVampireMinion;
import de.teamlapen.vampirism.entity.minions.IMinion;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.generation.WorldGenVampirism;
import de.teamlapen.vampirism.network.SpawnCustomParticlePacket;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar1;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.ChunkCoordIntPair;

import java.util.List;

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
		if (VampirismMod.inDev)
			return true;
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
		if (VampirismMod.inDev) {
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
			VampirePlayer vampire = VampirePlayer.get(p);
			// -----------------
			if (param.length > 0) {
				if ("lord".equals(param[0])) {
					vampire.setLevel(REFERENCE.HIGHEST_REACHABLE_LEVEL);
					VampireLordData.get(p.worldObj).makeLord(p);

					return;
				}
				if ("dim".equals(param[0]) && p instanceof EntityPlayerMP) {
					if (p.dimension != VampirismMod.castleDimensionId) {
						((EntityPlayerMP) p).mcServer.getConfigurationManager().transferPlayerToDimension(((EntityPlayerMP) p), VampirismMod.castleDimensionId, new TeleporterCastle(MinecraftServer.getServer().worldServerForDimension(VampirismMod.castleDimensionId)));
					}
				}
				if ("biome".equals(param[0])) {
					ChunkCoordIntPair pos = WorldGenVampirism.castleGenerator.findNearVampireBiome(p.worldObj, MathHelper.floor_double(p.posX), MathHelper.floor_double(p.posZ), 10000);
					sendMessage(sender, String.format("Found %s", pos));
					return;
				}
				if("lords".equals(param[0])){
					sendMessage(sender, VampireLordData.get(p.worldObj).getLordNamesAsString());
					sendMessage(sender, "Your UUID: " + ((EntityPlayer) sender).getUniqueID());
					return;
				}
				if("part".equals(param[0])){
					if(param.length<2)return;
					int id=Integer.parseInt(param[1]);
					int amount=10;
					if(param.length>2){
						amount=Integer.parseInt(param[2]);
					}
					NBTTagCompound nbt=new NBTTagCompound();
					nbt.setInteger("id", p.getEntityId());
					IMessage m=new SpawnCustomParticlePacket(id,p.posX,p.posY,p.posZ,amount,nbt);
					VampirismMod.modChannel.sendToAll(m);
					return;
				}
				if("entity".equals(param[0])){
					List l= p.worldObj.getEntitiesWithinAABBExcludingEntity(p, p.boundingBox.expand(3, 2, 3));
					for(Object o:l){
						if(o instanceof EntityCreature){
							sendMessage(sender,o.getClass().getName());
						}
						else{
							sendMessage(sender,"Not biteable "+o.getClass().getName());
						}
					}
					return;
				}
				if("dracula".equals(param[0])){
					List l= p.worldObj.getEntitiesWithinAABBExcludingEntity(p, p.boundingBox.expand(6, 6, 6));
					for(Object o:l){
						if(o instanceof EntityDracula){
							Logger.t("Dracula %s", o);
						}
					}
					return;
				}
				if("block".equals(param[0])){
					MovingObjectPosition pos = Helper.getPlayerLookingSpot(p, 0);
					if(pos!=null&&MovingObjectPosition.MovingObjectType.BLOCK.equals(pos.typeOfHit)){
						sendMessage(sender,"Block: "+p.worldObj.getBlock(pos.blockX,pos.blockY,pos.blockZ)+" Meta: "+p.worldObj.getBlockMetadata(pos.blockX,pos.blockY,pos.blockZ));
					}
					return;
				}
				if("infinite".equals(param[0])){
					MovingObjectPosition pos = Helper.getPlayerLookingSpot(p,0);
					if(pos!=null&&MovingObjectPosition.MovingObjectType.BLOCK.equals(pos.typeOfHit)) {
						Block b = p.worldObj.getBlock(pos.blockX, pos.blockY, pos.blockZ);
						if (b instanceof BlockBloodAltar1) {
							TileEntityBloodAltar1 altar1 = (TileEntityBloodAltar1) p.worldObj.getTileEntity(pos.blockX, pos.blockY, pos.blockZ);
							altar1.makeInfinite();
						}
					}
				}
				if("close".equals(param[0])){
					MovingObjectPosition pos = Helper.getPlayerLookingSpot(p,0);
					if(pos!=null&&MovingObjectPosition.MovingObjectType.BLOCK.equals(pos.typeOfHit)){
						Block b=p.worldObj.getBlock(pos.blockX,pos.blockY,pos.blockZ);
						if(b instanceof BlockCoffin){
							((BlockCoffin)b).setCoffinOccupied(p.worldObj,pos.blockX,pos.blockY,pos.blockZ,null,true);
							TileEntity t=p.worldObj.getTileEntity(pos.blockX,pos.blockY,pos.blockZ);
							t.markDirty();
						}
					}
					return;
				}
				if ("minions".equals(param[0])) {
					for (IMinion m : vampire.getMinionHandler().getMinionListForDebug()) {
						sendMessage(sender, m.getRepresentingEntity().toString());
					}
					sendMessage(sender, vampire.getMinionHandler().getMinionCount() + "/" + vampire.getMaxMinionCount());
					return;
				}
				if ("cminions".equals(param[0])) {
					List<IMinion> list = (List<IMinion>) vampire.getMinionHandler().getMinionListForDebug().clone();
					for (IMinion m : list) {
						if (m instanceof EntityVampireMinion) {
							if (m instanceof EntitySaveableVampireMinion) {
								((EntitySaveableVampireMinion) m).convertToRemote();
							} else if (m instanceof EntityRemoteVampireMinion) {
								((EntityRemoteVampireMinion) m).convertToSaveable();
							}
						}
					}
				}
				if ("target".equals(param[0])) {
					sendMessage(sender, Helper.entityToString(vampire.getMinionTarget()));

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
