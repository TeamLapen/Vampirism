package de.teamlapen.vampirism.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.teamlapen.vampirism.GuiHandler;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.minions.IMinion;
import de.teamlapen.vampirism.entity.minions.MinionHelper;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;

public class InputEventPacket implements IMessage {
	public static class Handler implements IMessageHandler<InputEventPacket, IMessage> {

		@Override
		public IMessage onMessage(InputEventPacket message, MessageContext ctx) {
			if(message.action==null)return null;
			if (message.action.equals(SUCKBLOOD)) {
				int id = 0;
				try {
					id = Integer.parseInt(message.param);
				} catch (NumberFormatException e) {
					Logger.e(TAG, "Receiving invalid param", e);
				}
				if (id != 0) {
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					VampirePlayer.get(player).suckBlood(id);
				}
			} else if (message.action.equals(TOGGLEAUTOFILLBLOOD)) {
				EntityPlayer player = ctx.getServerHandler().playerEntity;
				VampirePlayer.get(player).onToggleAutoFillBlood();
			} else if (message.action.equals(REVERTBACK)) {
				EntityPlayer player = ctx.getServerHandler().playerEntity;
				VampirePlayer.get(player).setLevel(0);
				player.attackEntityFrom(DamageSource.magic, 1000);
			} else if(message.action.equals(SWITCHVISION)){
				EntityPlayer player = ctx.getServerHandler().playerEntity;
				VampirePlayer.get(player).onToggleVision();
			}
			else if (message.action.equals(TOGGLESKILL)) {
				int id =-1;
				try {
					id = Integer.parseInt(message.param);
				} catch (NumberFormatException e) {
					Logger.e(TAG, "Receiving invalid param", e);
				}
				if (id >= 0) {
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					VampirePlayer.get(player).onSkillToggled(id);
				}
				else{
					Logger.w(TAG, "Skill with id "+id+" does not exist");
				}
			}
			else if(message.action.equals(LEAVE_COFFIN)){
				VampirePlayer.get(ctx.getServerHandler().playerEntity).wakeUpPlayer(true,false,true,true);
			}
			else if(message.action.equals(MINION_CONTROL)){
				
				try {
					if(message.param.contains(",")){
						String[] p=message.param.split(",");
						int cid = Integer.parseInt(p[0]);
						int eid = Integer.parseInt(p[1]);
						IMinion m=MinionHelper.getMinionFromEntity(ctx.getServerHandler().playerEntity.worldObj.getEntityByID(eid));
						if(m!=null){
							Logger.i(TAG, "Activated command %s", m.getCommand(cid));
							m.activateMinionCommand(m.getCommand(cid));
						}
						else{
							Logger.w(TAG, "Trying to activate command %s for enityid %s. But the entity cannot be found", cid,eid);
						}
					}
					else{
						int id=Integer.parseInt(message.param);
						VampirePlayer.get(ctx.getServerHandler().playerEntity).onCallActivated(id);
					}
				} catch (NumberFormatException e) {
					Logger.e(TAG, "Receiving invalid param", e);
				}
			}

			return null;
			
		}

	}

	public static String SUCKBLOOD = "sb";
	public static String TOGGLEAUTOFILLBLOOD = "ta";
	public static String REVERTBACK = "rb";
	public static String TOGGLESKILL = "ts";
	public static String LEAVE_COFFIN = "lc";
	public static String MINION_CONTROL = "mc";
	private final static String TAG = "InputEventPacket";
	public static final String SWITCHVISION = "sw";
	private String param;
	private String action;

	private final String SPLIT = "-";

	public InputEventPacket() {

	}

	public InputEventPacket(String action, String param) {
		this.action = action;
		this.param = param;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		String[] s = ByteBufUtils.readUTF8String(buf).split(SPLIT);
		action = s[0];
		if(s.length>1){
			param = s[1];
		}
		else{
			param="";
		}

	}

	@Override
	public void toBytes(ByteBuf buf) {

		ByteBufUtils.writeUTF8String(buf, action + SPLIT + param);

	}

}
