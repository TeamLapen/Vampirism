package de.teamlapen.vampirism.entity.player;

import de.teamlapen.vampirism.api.entity.player.FractionRegistry;
import de.teamlapen.vampirism.api.entity.player.IPlayerEventListener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Event handler for player related events
 */
public class ModPlayerEventHandler {
    @SubscribeEvent(receiveCanceled = true)
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer && VampirePlayer.get((EntityPlayer) event.entity) == null) {
            VampirePlayer.register((EntityPlayer) event.entity);
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        VampirePlayer.get(event.entityPlayer).copyFrom(event.original);
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event){
        if(event.entity instanceof EntityPlayer){
            if(event.entity.worldObj.isRemote||event.entity instanceof EntityPlayerSP){
                //TODO request update
            }
            else{
                for(FractionRegistry.Fraction f:FractionRegistry.getFractions()){
                    if(f.implementsEventListener()){
                        ((IPlayerEventListener)f.getProp((EntityPlayer) event.entity)).onJoinWorld();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event){
        if(event.entity instanceof EntityPlayer){
            for(FractionRegistry.Fraction f:FractionRegistry.getFractions()){
                if(f.implementsEventListener()){
                    if(!((IPlayerEventListener)f.getProp((EntityPlayer) event.entity)).onEntityAttacked(event.source,event.ammount)){
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event){
        if(event.entity instanceof EntityPlayer){
            for(FractionRegistry.Fraction f:FractionRegistry.getFractions()){
                if(f.implementsEventListener()){
                    ((IPlayerEventListener)f.getProp((EntityPlayer) event.entity)).onDeath(event.source);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event){
        if(event.entity instanceof EntityPlayer){
            for(FractionRegistry.Fraction f:FractionRegistry.getFractions()){
                if(f.implementsEventListener()){
                    ((IPlayerEventListener)f.getProp((EntityPlayer) event.entity)).onUpdate();
                }
            }
        }
    }

    @SubscribeEvent
    public void onChangedDimension(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event){
        for(FractionRegistry.Fraction f:FractionRegistry.getFractions()){
            if(f.implementsEventListener()){
                ((IPlayerEventListener)f.getProp((EntityPlayer) event.player)).onChangedDimension(event.fromDim,event.toDim);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event){
        for(FractionRegistry.Fraction f:FractionRegistry.getFractions()){
            if(f.implementsEventListener()){
                ((IPlayerEventListener)f.getProp((EntityPlayer) event.player)).onPlayerLoggedIn();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent event){
        for(FractionRegistry.Fraction f:FractionRegistry.getFractions()){
            if(f.implementsEventListener()){
                ((IPlayerEventListener)f.getProp((EntityPlayer) event.player)).onPlayerLoggedOut();
            }
        }
    }
}
