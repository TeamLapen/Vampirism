package de.teamlapen.vampirism;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Stores vampire lord informations for the map.
 */
public class VampireLordData extends WorldSavedData {
	private final static String IDENTIFIER ="vampirism_lord_data";
	private final static String TAG="VampLordData";

	public boolean shouldRegenerateCastleDim() {
		return shouldRegenerateCastleDim;
	}

	public void setRegenerateCastleDim(boolean shouldRegenerateCastleDim) {
		this.shouldRegenerateCastleDim = shouldRegenerateCastleDim;
	}

	private boolean shouldRegenerateCastleDim;

	public boolean isPortalEnabled() {
		return portalEnabled;
	}

	private boolean portalEnabled;
	final private List<UUID> lords;
	/**
	 * List of lords which lost their lord state while being absent
	 */
	final private List<UUID> disabledLord;

	public static VampireLordData get(World world){
		VampireLordData data= (VampireLordData) world.mapStorage.loadData(VampireLordData.class,IDENTIFIER);
		if(data==null){
			data=new VampireLordData(IDENTIFIER);
			world.mapStorage.setData(IDENTIFIER,data);
		}
		return data;
	}
	public VampireLordData(String identifier) {
		super(identifier);
		lords=new ArrayList<UUID>();
		disabledLord=new ArrayList<UUID>();
		shouldRegenerateCastleDim=false;
		portalEnabled=true;
	}

	/**
	 * Checks if the given player is a vampire lord
	 * @param player
	 * @return
	 */
	public boolean isLord(EntityPlayer player){
		return lords.contains(player.getUniqueID());
	}

	/**
	 * Makes the given player a vampire lord.
	 * Notifies the VP object, removes and notifies prior lords if only one lord is allowed and sends a message to all players
	 * @param player
	 * @return
	 */
	public boolean makeLord(EntityPlayer player){
		if(VampirePlayer.get(player).setVampireLord(true)){
			if(!lords.contains(player.getUniqueID())){
				if(!Configs.mulitple_lords&&!lords.isEmpty()){
					for(UUID uuid:lords){
						EntityPlayer p=getPlayerFormUUID(uuid);
						if(p!=null){
							VampirePlayer.get(p).setVampireLord(false);
							p.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.lord.vampire_replace"));
						}
					}
					lords.clear();
				}
				lords.add(player.getUniqueID());
					player.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.become_lord"));
					MinecraftServer.getServer().getConfigurationManager().sendChatMsg(
							new ChatComponentText(player.getDisplayName() + " ").appendSibling(new ChatComponentTranslation("text.vampirism.other_player_become_lord")));
			}
			markDirty();
			return true;
		}
		return false;
	}

	/**
	 * Transfers the vampire lord state from the oldPlayer to the newPlayer and notifies all
	 * @param oldPlayer
	 * @param newPlayer
	 * @return
	 */
	public boolean replaceLord(EntityPlayer oldPlayer,EntityPlayer newPlayer){
		if(VampirePlayer.get(newPlayer).setVampireLord(true)){
			lords.remove(oldPlayer.getUniqueID());
			oldPlayer.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.lord.vampire_kill_replace"));
			if(!lords.contains(newPlayer)){
				lords.add(newPlayer.getUniqueID());
				newPlayer.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.become_lord"));
				MinecraftServer.getServer().getConfigurationManager().sendChatMsg(
						new ChatComponentText(newPlayer.getDisplayName() + " ").appendSibling(new ChatComponentTranslation("text.vampirism.other_player_become_lord")));
			}
			markDirty();
			return true;

		}
		return false;
	}

	/**
	 * Makes the given player to a normal vampire again
	 * Notifies the VP object, sends a message to all players and checks if the castle dim has to be regenerated
	 * @param player
	 * @return
	 */
	public void makeNoLord(EntityPlayer player,String reason){
		lords.remove(player.getUniqueID());
		if(!Configs.mulitple_lords){
			this.openAndRegenDim(true);
		}
		if(reason!=null){
			player.addChatComponentMessage(new ChatComponentTranslation(reason));
		}
		VampirePlayer.get(player).setVampireLord(false);
		this.markDirty();
	}

	/**
	 * Checks if the dimension has to be regenerated or if the castle portal should be disabled
	 */
	public void onDraculaDied(){
		if(Configs.mulitple_lords){
			this.openAndRegenDim(false);
		}
		else{
			portalEnabled=false;
			this.markDirty();
		}
	}

	/**
	 * Returns all lords as String
	 * @return
	 */
	public String getLordNamesAsString(){
		String s="";
		for(UUID uuid:lords){
			EntityPlayer p=this.getPlayerFormUUID(uuid);
			if(p!=null){
				s+=p.getDisplayName()+" \n";
			}
			else{
				s+=uuid.toString()+" \n";
			}
		}
		return s;
	}

	private void checkLords(){
		if(!Configs.mulitple_lords&&lords.size()>0){
			if(shouldLooseLord(lords.get(0))){
				disabledLord.add(lords.remove(0));
				openAndRegenDim(true);
				this.markDirty();
			}
		}
	}

	private void openAndRegenDim(boolean message){
		shouldRegenerateCastleDim=true;
		portalEnabled=true;
		if(message){
			MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("text.vampirism.new_lord_in_castle"));
		}
		this.markDirty();
	}

	private boolean shouldLooseLord(UUID uuid){
		return false;
	}

	private EntityPlayer getPlayerFormUUID(UUID uuid){
		List l=MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		Iterator it=l.iterator();
		while (it.hasNext()){
			EntityPlayer e= (EntityPlayer) it.next();
			if(e.getUniqueID().equals(uuid))return e;
		}
		return null;
	}
	private void checkDisabledLords(){
		Iterator<UUID> it=disabledLord.iterator();
		while (it.hasNext()){
			UUID uuid=it.next();
			EntityPlayer player=getPlayerFormUUID(uuid);
			if(player!=null){
				player.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.lost_lord_absence"));
				it.remove();
			}
			this.markDirty();
		}
	}

	public void tick(TickEvent.ServerTickEvent event){
		if(event.getPhase().equals(TickEvent.Phase.END)&& MinecraftServer.getSystemTimeMillis()%5000==0){
			checkLords();
			checkDisabledLords();
		}
	}

	/**
	 * Resets all vampire lords as well as the vampire dimension
	 */
	public void reset(){
		Iterator<UUID> it=lords.iterator();
		while(it.hasNext()){
			EntityPlayer player=getPlayerFormUUID(it.next());
			if(player!=null){
				VampirePlayer.get(player).setVampireLord(false);
			}
			it.remove();
		}
		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText("Vampire Lords were reset"));
		this.openAndRegenDim(true);
	}


	@Override public void readFromNBT(NBTTagCompound nbt) {
		shouldRegenerateCastleDim=nbt.getBoolean("regenerate");
		portalEnabled=nbt.getBoolean("portal");
		try {
			int i=0;
			while(nbt.hasKey("lord_ls_"+i)){
				long l=nbt.getLong("lord_ls_"+i);
				long m=nbt.getLong("lord_ms_"+i);
				lords.add(new UUID(m,l));
				i++;
			}
			i=0;
			while(nbt.hasKey("dis_lord_ls_"+i)){
				long l=nbt.getLong("dis_lord_ls_"+i);
				long m=nbt.getLong("dis_lord_ms_"+i);
				disabledLord.add(new UUID(m,l));
				i++;
			}
		} catch (Exception e) {
			Logger.e(TAG,e,"Failed to read lord uuids");
		}
		Logger.t("Loaded %d %d lords",lords.size(),disabledLord.size());
		if(!Configs.mulitple_lords&&lords.size()>1){
			MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText("The configuration was changed so that only one player can be a vampire lord at a time"));
			MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText("Therefore all players loose their lord status"));
			this.openAndRegenDim(false);
		}
	}

	@Override public void writeToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("regenerate",shouldRegenerateCastleDim);
		nbt.setBoolean("portal",portalEnabled);
		for(int i=0;i<lords.size();i++){
			nbt.setLong("lord_ls_"+i,lords.get(i).getLeastSignificantBits());
			nbt.setLong("lord_ms_"+i,lords.get(i).getMostSignificantBits());
		}
		for(int i=0;i<disabledLord.size();i++){
			nbt.setLong("dis_lord_ls_"+i,disabledLord.get(i).getLeastSignificantBits());
			nbt.setLong("dis_lord_ms_"+i,disabledLord.get(i).getMostSignificantBits());
		}
	}
}
