package de.teamlapen.vampirism;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.common.Optional;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.entity.minions.EntityVampireMinion;
import de.teamlapen.vampirism.entity.minions.IMinion;
import de.teamlapen.vampirism.entity.minions.IMinionCommand;
import de.teamlapen.vampirism.entity.minions.IMinionLord;
import de.teamlapen.vampirism.entity.minions.MinionHelper;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar1;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar2;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = "Waila")
public class WailaDataProvider implements IWailaDataProvider, IWailaEntityProvider{

	@Optional.Method(modid="Waila")
	public static void callbackRegister(IWailaRegistrar register){
		WailaDataProvider instance = new WailaDataProvider();
		register.addConfig(REFERENCE.MODID,"option.vampirism.showAltarInfo",true);
		register.addConfig(REFERENCE.MODID, "option.vampirism.showPlayerInfo",true);
		register.addConfig(REFERENCE.MODID, "option.vampirism.showEntityInfo",true);
		register.registerBodyProvider((IWailaDataProvider)instance, TileEntityBloodAltar1.class);
		register.registerBodyProvider((IWailaDataProvider)instance, TileEntityBloodAltar2.class);
		register.registerNBTProvider((IWailaDataProvider)instance, TileEntityBloodAltar1.class);
		register.registerBodyProvider((IWailaEntityProvider)instance, EntityPlayer.class);
		register.registerBodyProvider((IWailaEntityProvider)instance, EntityCreature.class);
	}
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if(config.getConfig("option.vampirism.showAltarInfo",true)){
			TileEntity tile=accessor.getTileEntity();
			if( tile instanceof TileEntityBloodAltar1){
				TileEntityBloodAltar1 altar1=(TileEntityBloodAltar1) tile;

				if(altar1.isOccupied()){
					int blood;
					if(accessor.getNBTData().hasKey("vampirism:bloodLeft")){
						blood=accessor.getNBTData().getInteger("vampirism:bloodLeft");
					}
					else{
						blood=altar1.getBloodLeft();
					}
						
					currenttip.add(String.format("%s%s: %d",SpecialChars.RED,StatCollector.translateToLocal("text.vampirism:blood_left"),blood));
				}
			}
			else if(tile instanceof TileEntityBloodAltar2){
				TileEntityBloodAltar2 altar2=(TileEntityBloodAltar2) tile;
				currenttip.add(String.format("%s%s: %d/%d", SpecialChars.RED,StatCollector.translateToLocal("text.vampirism:blood"),altar2.getBloodAmount(),altar2.getMaxBlood()));
			}
		}
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		if(te instanceof TileEntityBloodAltar1){
			tag.setInteger("vampirism:bloodLeft", ((TileEntityBloodAltar1)te).getBloodLeft());
		}
		return tag;
	}
	@Override
	public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		return null;
	}
	@Override
	public List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}
	@Override
	public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		if(config.getConfig("option.vampirism.showPlayerInfo", true)){
			if(entity instanceof EntityPlayer){
				VampirePlayer vampire=VampirePlayer.get((EntityPlayer) entity);
				if(vampire.getLevel()>0){
					currenttip.add(String.format("%s: %d", StatCollector.translateToLocal("text.vampirism:vampirelevel"), vampire.getLevel()));
					if(vampire.isVampireLord()){
						currenttip.add(SpecialChars.WHITE+StatCollector.translateToLocal("entity.vampirism.vampireLord.name"));
					}
				}
			}
		}
		if(config.getConfig("option.vampirism.showEntityInfo", true)){
			if(entity instanceof EntityCreature&&VampirePlayer.get(accessor.getPlayer()).getLevel()>0){
				VampireMob vampire=VampireMob.get((EntityCreature) entity);
				IMinion minion=MinionHelper.getMinionFromEntity(entity);
				
				int blood=vampire.getBlood();
				if(blood>0){
					currenttip.add(String.format("%s%s: %d", SpecialChars.RED,StatCollector.translateToLocal("text.vampirism:entitysblood"),blood));
				}
				if(minion!=null){
					currenttip.add(SpecialChars.GREEN+StatCollector.translateToLocal("text.vampirism:minion"));
					IMinionLord lord=minion.getLord();
					if(lord!=null){
						currenttip.add(String.format("%s%s: %s", SpecialChars.WHITE,StatCollector.translateToLocal("text.vampirism:lord"),lord.getRepresentingEntity().getCommandSenderName()));
						IMinionCommand c=minion.getCommand(minion.getActiveCommandId());
						if(c!=null){
							currenttip.add(String.format("%s%s: %s", SpecialChars.WHITE,StatCollector.translateToLocal("text.vampirism:curret_task"),StatCollector.translateToLocal(c.getUnlocalizedName())));
						}
						
					}
				}
			}
		}
		return currenttip;
	}
	@Override
	public List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}
	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, Entity ent, NBTTagCompound tag, World world) {
		return null;
	}

}
