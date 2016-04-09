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
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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


    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent.Entity event) {
        if (event.getEntity() instanceof EntityPlayer) {
            event.addCapability(REFERENCE.FACTION_PLAYER_HANDLER_KEY, FactionPlayerHandler.createNewCapability((EntityPlayer) event.getEntity()));
            event.addCapability(REFERENCE.VAMPIRE_PLAYER_KEY, VampirePlayer.createNewCapability((EntityPlayer) event.getEntity()));
            event.addCapability(REFERENCE.HUNTER_PLAYER_KEY, HunterPlayer.createNewCapability((EntityPlayer) event.getEntity()));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onAttackEntity(AttackEntityEvent event) {
        if (VampirePlayer.get(event.getEntityPlayer()).getActionHandler().isActionActive(VampireActions.batAction)) {
            event.setCanceled(true);
        }
        if (!checkItemUsePerm(event.getEntityPlayer().getHeldItemMainhand(), event.getEntityPlayer())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.PlaceEvent event) {
        try {
            if (VampirePlayer.get(event.getPlayer()).getActionHandler().isActionActive(VampireActions.batAction)) {
                event.setCanceled(true);
            }
        } catch (Exception e) {
            // Added try catch to prevent any exception in case some other mod uses auto placers or so
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (VampirePlayer.get(event.getEntityPlayer()).getActionHandler().isActionActive(VampireActions.batAction)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemUse(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof EntityPlayer && VampirePlayer.get((EntityPlayer) event.getEntityLiving()).getActionHandler().isActionActive(VampireActions.batAction)) {
            event.setCanceled(true);
        }
        if (event.getEntity() instanceof EntityPlayer && !checkItemUsePerm(event.getItem(), (EntityPlayer) event.getEntityLiving())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            event.setDistance(event.getDistance() - VampirePlayer.get((EntityPlayer) event.getEntity()).getSpecialAttributes().getJumpBoost());
        }
    }

    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            event.getEntity().motionY += (double) ((float) (VampirePlayer.get((EntityPlayer) event.getEntity()).getSpecialAttributes().getJumpBoost()) * 0.1F);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingUpdateLast(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            VampirePlayer.get((EntityPlayer) event.getEntity()).onUpdateBloodStats();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.getEntityPlayer().worldObj.isRemote) {
            FactionPlayerHandler.get(event.getEntityPlayer()).copyFrom(event.getOriginal());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        //Replace glas bottle by empty blood bottle, if interacting with a fluid container that contains blood
        if (Configs.autoConvertGlasBottles) {
            if (event.getWorld().getWorldBorder().contains(event.getPos()))
                if (event.getItemStack() != null &&
                        event.getItemStack().getItem() != null &&
                        event.getItemStack().getItem().equals(Items.glass_bottle) && event.getItemStack().stackSize == 1) {
                    Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
                    boolean flag = false;
                    if (block instanceof IFluidTank) {
                        //Probably never happens
                        VampirismMod.log.d("Fluid", "Found block that is instanceof IFluidTank %s", block.getClass());
                        if (ModFluids.blood.equals(((IFluidTank) block).getFluid())) {
                            flag = true;
                        }
                    } else if (block instanceof ITileEntityProvider) {
                        TileEntity entity = event.getWorld().getTileEntity(event.getPos());
                        if (entity instanceof IFluidHandler) {
                            FluidTankInfo[] infos = ((IFluidHandler) entity).getTankInfo(event.getFace());
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
                        event.getItemStack().setItem(ModItems.bloodBottle);
                        event.getItemStack().setItemDamage(0);
                    }
                }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerName(PlayerEvent.NameFormat event) {
        if (event.getEntityPlayer() != null && !Configs.disable_factionDisplayChat) {
            IFactionPlayer f = FactionPlayerHandler.get(event.getEntityPlayer()).getCurrentFactionPlayer();
            if (f != null && !f.isDisguised()) {
                event.setDisplayname(f.getFaction().getChatColor() + event.getDisplayname());
                if (f instanceof IVampirePlayer && ((IVampirePlayer) f).isVampireLord()) {
                    event.setDisplayname(TextFormatting.RED + "[" + I18n.translateToLocal("text.vampirism.lord") + "] " + TextFormatting.RESET + event.getDisplayname());
                }
            }

        }
    }

    /**
     * Checks if the player is allowed to use that item ({@link IFactionLevelItem}) and cancels the event if not.
     *
     *@return If it is allowed to use the item
     */
    private boolean checkItemUsePerm(ItemStack stack, EntityPlayer player) {


        if (stack != null && stack.getItem() instanceof IFactionLevelItem) {
            IFactionLevelItem item = (IFactionLevelItem) stack.getItem();
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            if (!handler.isInFaction(item.getUsingFaction())) {

                player.addChatComponentMessage(new TextComponentTranslation("text.vampirism.can_only_be_used_by", new TextComponentTranslation(item.getUsingFaction().getUnlocalizedNamePlural())));
                return false;
            } else {
                if (handler.getCurrentLevel() < item.getMinLevel() || !item.canUse(handler.getCurrentFactionPlayer(), stack)) {

                    player.addChatComponentMessage(new TextComponentTranslation("text.vampirism.can_only_be_used_by_level", new TextComponentTranslation(item.getUsingFaction().getUnlocalizedNamePlural()), item.getMinLevel()));
                    return false;
                }
            }

        }
        return true;
    }


}
