package de.teamlapen.lib.network;
//
//NOT used anymore
//import de.teamlapen.lib.HelperRegistry;
//import de.teamlapen.lib.VampLib;
//import de.teamlapen.lib.lib.network.AbstractMessageHandler;
//import de.teamlapen.lib.lib.network.ISyncable;
//import io.netty.buffer.ByteBuf;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityLiving;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraftforge.common.IExtendedEntityProperties;
//import net.minecraftforge.fml.common.network.ByteBufUtils;
//import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
//import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
//
//import java.util.ArrayList;
//import java.util.List;


//public class RequestEntityUpdatePacket implements IMessage {
//
//    public static class Handler extends AbstractMessageHandler<RequestEntityUpdatePacket>{
//
//        @Override
//        public IMessage handleClientMessage(EntityPlayer player, RequestEntityUpdatePacket message, MessageContext ctx) {
//            return null;
//        }
//
//        @Override
//        public IMessage handleServerMessage(EntityPlayer player, RequestEntityUpdatePacket message, MessageContext ctx) {
//
//            Entity entity = player.worldObj.getEntityByID(message.id);
//            VampLib.log.t("4 %s (%d) %s",entity,message.id,player);
//            if(entity!=null){
//                List<ISyncable.ISyncableExtendedProperties> props=null;
//                String[] keys=null;
//                if(entity instanceof EntityLiving){
//                    keys=HelperRegistry.getSyncableEntityProperties();
//                }
//                else if(entity instanceof EntityPlayer){
//                    keys=HelperRegistry.getSyncablePlayerProperties();
//
//                }
//                VampLib.log.t("5 %s",keys);
//                if(keys!=null&&keys.length>0){
//                    props=new ArrayList<>();
//                    for(String prop: keys){
//                        ISyncable.ISyncableExtendedProperties p= (ISyncable.ISyncableExtendedProperties) entity.getExtendedProperties(prop);
//                        VampLib.log.t("Got prop %s for key %s in entity %s",p,prop,entity);
//                        if(p!=null){
//                            props.add(p);
//                        }
//                    }
//                }
//                if(props!=null){
//                    VampLib.log.t("6");
//                    if(entity instanceof ISyncable){
//                        return UpdateEntityPacket.create((EntityLiving) entity,props.toArray(new ISyncable.ISyncableExtendedProperties[props.size()]));
//                    }
//                    else{
//                        VampLib.log.t("Creating update packet for player %s (%s)",entity,props);
//                        return UpdateEntityPacket.create(props.toArray(new ISyncable.ISyncableExtendedProperties[props.size()]));
//                    }
//                }
//                else if(entity instanceof ISyncable){
//                    return UpdateEntityPacket.create(entity);
//                }
//                else {
//                    VampLib.log.w("RequestUpdatePacket","There is nothing to update for entity %s",entity);
//                }
//
//
//            }
//            else{
//                VampLib.log.t("Did not find entity %s",message.id);
//            }
//            return null;
//        }
//    }
//
//    private int id;
//
//    /**
//     * Dont use!
//     */
//    public RequestEntityUpdatePacket() {
//
//    }
//
//    /**
//     * Request a entity update.
//     * Only has a result if the entity implements ISyncable or there is a {@link de.teamlapen.lib.lib.network.ISyncable.ISyncableExtendedProperties} registered in the HelperRegistry
//     *
//     * @param entity
//     */
//    public RequestEntityUpdatePacket(Entity entity) {
//        this.id = entity.getEntityId();
//    }
//
//    @Override
//    public void fromBytes(ByteBuf buf) {
//        id = ByteBufUtils.readTag(buf).getInteger("id");
//
//    }
//
//    @Override
//    public void toBytes(ByteBuf buf) {
//        NBTTagCompound tag = new NBTTagCompound();
//        tag.setInteger("id", id);
//        ByteBufUtils.writeTag(buf, tag);
//
//    }
//}
