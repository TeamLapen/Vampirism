package de.teamlapen.vampirism.entity.player;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.IFactionLevelItem;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Event handler for player related events
 */
public class ModPlayerEventHandler {


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onAttackEntity(AttackEntityEvent event) {
        if (VampirePlayer.get(event.entityPlayer).getActionHandler().isActionActive(VampireActions.batAction)) {
            event.setCanceled(true);
        }
        checkItemUsePerm(event);
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.PlaceEvent event) {
        try {
            if (VampirePlayer.get(event.player).getActionHandler().isActionActive(VampireActions.batAction)) {
                event.setCanceled(true);
            }
        } catch (Exception e) {
            // Added try catch to prevent any exception in case some other mod uses auto placers or so
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (VampirePlayer.get(event.entityPlayer).getActionHandler().isActionActive(VampireActions.batAction)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer) {
            /*
            Register ExtendedProperties.
            Could be done via factions, but that might be a little bit overkill for 2-5 factions and might cause trouble with addon mods.
             */
            if (FactionPlayerHandler.get((EntityPlayer) event.entity) == null) {
                FactionPlayerHandler.register((EntityPlayer) event.entity);
            }
            if (VampirePlayer.get((EntityPlayer) event.entity) == null) {
                VampirePlayer.register((EntityPlayer) event.entity);
            }
            if (HunterPlayer.get((EntityPlayer) event.entity) == null) {
                HunterPlayer.register((EntityPlayer) event.entity);
            }

        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemUse(PlayerUseItemEvent.Start event) {
        if (VampirePlayer.get(event.entityPlayer).getActionHandler().isActionActive(VampireActions.batAction)) {
            event.setCanceled(true);
        }
        checkItemUsePerm(event);
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        if (event.entity instanceof EntityPlayer) {
            event.distance -= VampirePlayer.get((EntityPlayer) event.entity).getSpecialAttributes().getJumpBoost();
        }
    }

    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.entity instanceof EntityPlayer) {
            event.entity.motionY += (double) ((float) (VampirePlayer.get((EntityPlayer) event.entity).getSpecialAttributes().getJumpBoost()) * 0.1F);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingUpdateLast(LivingEvent.LivingUpdateEvent event) {
        if (event.entity instanceof EntityPlayer) {
            VampirePlayer.get((EntityPlayer) event.entity).onUpdateBloodStats();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.entityPlayer.worldObj.isRemote) {
            FactionPlayerHandler.get(event.entityPlayer).copyFrom(event.original);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        //Replace glas bottle by empty blood bottle, if interacting with a fluid container that contains blood
        if (Configs.autoConvertGlasBottles && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (event.world.getWorldBorder().contains(event.pos))
                if (event.entityPlayer.getHeldItem() != null &&
                        event.entityPlayer.getHeldItem().getItem() != null &&
                        event.entityPlayer.getHeldItem().getItem().equals(Items.glass_bottle) && event.entityPlayer.getHeldItem().stackSize == 1) {
                    Block block = event.world.getBlockState(event.pos).getBlock();
                    boolean flag = false;
                    if (block instanceof IFluidTank) {
                        //Probably never happens
                        VampirismMod.log.d("Fluid", "Found block that is instanceof IFluidTank %s", block.getClass());
                        if (ModFluids.blood.equals(((IFluidTank) block).getFluid())) {
                            flag = true;
                        }
                    } else if (block instanceof ITileEntityProvider) {
                        TileEntity entity = event.world.getTileEntity(event.pos);
                        if (entity instanceof IFluidHandler) {
                            FluidTankInfo[] infos = ((IFluidHandler) entity).getTankInfo(event.face);
                            if (infos != null) {
                                for (FluidTankInfo info : infos) {
                                    if (info.fluid != null && ModFluids.blood.equals(info.fluid.getFluid())) {
                                        flag = true;
                                        break;
                                    }
                                }
                            }

                        }
                    }
                    if (flag) {
                        event.entityPlayer.getHeldItem().setItem(ModItems.bloodBottle);
                        event.entityPlayer.getHeldItem().setItemDamage(0);
                    }
                }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerName(PlayerEvent.NameFormat event) {
        if (event.entityPlayer != null && !Configs.disable_factionDisplayChat) {
            IFactionPlayer f = FactionPlayerHandler.get(event.entityPlayer).getCurrentFactionPlayer();
            if (f != null && !f.isDisguised()) {
                event.displayname = f.getFaction().getChatColor() + event.displayname;
                if (f instanceof IVampirePlayer && ((IVampirePlayer) f).isVampireLord()) {
                    event.displayname = EnumChatFormatting.RED + "[" + StatCollector.translateToLocal("text.vampirism.lord") + "] " + EnumChatFormatting.RESET + event.displayname;
                }
            }

        }
    }

    /**
     * Checks if the player is allowed to use that item ({@link IFactionLevelItem}) and cancels the event if not.
     *
     * @param event
     */
    private void checkItemUsePerm(PlayerEvent event) {
        ItemStack stack = event.entityPlayer.getCurrentEquippedItem();

        if (stack != null && stack.getItem() instanceof IFactionLevelItem) {
            IFactionLevelItem item = (IFactionLevelItem) stack.getItem();
            FactionPlayerHandler handler = FactionPlayerHandler.get(event.entityPlayer);
            if (!handler.isInFaction(item.getUsingFaction())) {
                event.setCanceled(true);
                event.entityPlayer.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.can_only_be_used_by", new ChatComponentTranslation(item.getUsingFaction().getUnlocalizedNamePlural())));
            } else {
                if (handler.getCurrentLevel() < item.getMinLevel() || !item.canUse(handler.getCurrentFactionPlayer(), stack)) {
                    event.setCanceled(true);
                    event.entityPlayer.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.can_only_be_used_by_level", new ChatComponentTranslation(item.getUsingFaction().getUnlocalizedNamePlural()), item.getMinLevel()));
                }
            }

        }
    }


}
