package de.teamlapen.vampirism.player;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.items.IFactionLevelItem;
import de.teamlapen.vampirism.blocks.BlockAltarInspiration;
import de.teamlapen.vampirism.blocks.BlockBloodContainer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.items.BloodBottleFluidHandler;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
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
        if (VampirePlayer.get(event.getEntityPlayer()).getSpecialAttributes().bat || HunterPlayer.get(event.getEntityPlayer()).getSpecialAttributes().isDisguised()) {
            event.setCanceled(true);
        }
        if (!checkItemUsePerm(event.getEntityPlayer().getHeldItemMainhand(), event.getEntityPlayer())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.PlaceEvent event) {
        try {
            if (VampirePlayer.get(event.getPlayer()).getSpecialAttributes().bat || HunterPlayer.get(event.getPlayer()).getSpecialAttributes().isDisguised()) {
                event.setCanceled(true);
            }
        } catch (Exception e) {
            // Added try catch to prevent any exception in case some other mod uses auto placers or so
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        VampirePlayer vampire = VampirePlayer.get(event.getEntityPlayer());
        HunterPlayer hunter = HunterPlayer.get(event.getEntityPlayer());
        if (vampire.getSpecialAttributes().bat || hunter.getSpecialAttributes().isDisguised()) {
            event.setCanceled(true);
        } else if (ModBlocks.garlicBeacon.equals(event.getState().getBlock()) && vampire.getLevel() > 0) {
            event.setNewSpeed(event.getOriginalSpeed() * 0.1F);
        }
    }

    @SubscribeEvent
    public void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        if (!checkItemUsePerm(event.getItemStack(), event.getEntityPlayer())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemUse(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof EntityPlayer && (VampirePlayer.get((EntityPlayer) event.getEntityLiving()).getSpecialAttributes().bat || HunterPlayer.get((EntityPlayer) event.getEntityLiving()).getSpecialAttributes().isDisguised())) {
            event.setCanceled(true);
        }
        if (event.getEntity() instanceof EntityPlayer && !checkItemUsePerm(event.getItem(), (EntityPlayer) event.getEntityLiving())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if (!FactionPlayerHandler.get((EntityPlayer) event.getEntity()).onEntityAttacked(event.getSource(), event.getAmount())) {
                event.setCanceled(true);
            }
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
                        event.getItemStack().getItem().equals(Items.GLASS_BOTTLE) && event.getItemStack().stackSize == 1) {
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
                        if (entity != null && entity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, event.getFace())) {
                            net.minecraftforge.fluids.capability.IFluidHandler fluidHandler = entity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, event.getFace());
                            FluidStack drain = fluidHandler.drain(new FluidStack(ModFluids.blood, 1000), false);
                            if (drain != null && drain.amount >= BloodBottleFluidHandler.MULTIPLIER) {
                                flag = true;
                            }
                        }
                        if (flag && block instanceof BlockAltarInspiration) {
                            flag = false;
                        }
                        if (flag && block instanceof BlockBloodContainer) {
                            flag = event.getEntityPlayer().isSneaking();
                        }
                    }
                    if (flag) {
                        event.getItemStack().deserializeNBT(new ItemStack(ModItems.bloodBottle).serializeNBT());
                    }
                }
        }
    }

    @SubscribeEvent
    public void onPlayerLeftLickedBlock(PlayerInteractEvent.LeftClickBlock event) {
        assert event.getFace() != null;
        BlockPos pos = event.getPos().offset(event.getFace());
        World world = event.getWorld();
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() == ModBlocks.alchemicalFire) {
            world.playEvent(null, 1009, pos, 0);
            world.setBlockToAir(pos);
            event.setCanceled(true);
        } else if (ModBlocks.garlicBeacon.equals(state.getBlock()) && Helper.isVampire(event.getEntityPlayer())) {
            event.getEntityPlayer().addPotionEffect(new PotionEffect(ModPotions.garlic));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerName(PlayerEvent.NameFormat event) {
        if (event.getEntityPlayer() != null && !Configs.disable_factionDisplayChat) {
            IFactionPlayer fp = FactionPlayerHandler.get(event.getEntityPlayer()).getCurrentFactionPlayer();
            IFaction f = fp == null ? null : fp.getDisguisedAs();
            if (f != null) {
                event.setDisplayname(f.getChatColor() + event.getDisplayname() + TextFormatting.RESET);
                if (fp instanceof IVampirePlayer && !fp.isDisguised() && ((IVampirePlayer) fp).isVampireLord()) {
                    event.setDisplayname(TextFormatting.RED + "[" + UtilLib.translate("text.vampirism.lord") + "] " + TextFormatting.RESET + event.getDisplayname());
                }
            }

        }
    }

    @SubscribeEvent
    public void onPlayerVisibilityCheck(PlayerEvent.Visibility event) {
        if (HunterPlayer.get(event.getEntityPlayer()).getSpecialAttributes().isDisguised()) {
            event.modifyVisibility(Balance.hpa.DISGUISE_VISIBILITY_MOD);
        }
    }

    /**
     * Checks if the player is allowed to use that item ({@link IFactionLevelItem}) and cancels the event if not.
     *
     *@return If it is allowed to use the item
     */
    private boolean checkItemUsePerm(ItemStack stack, EntityPlayer player) {

        boolean message = !player.worldObj.isRemote;
        if (stack != null && stack.getItem() instanceof IFactionLevelItem) {
            IFactionLevelItem item = (IFactionLevelItem) stack.getItem();
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            IPlayableFaction usingFaction = item.getUsingFaction(stack);
            ISkill requiredSkill = item.getRequiredSkill(stack);
            if (usingFaction != null && !handler.isInFaction(usingFaction)) {

                if (message)
                    player.addChatComponentMessage(new TextComponentTranslation("text.vampirism.can_only_be_used_by", new TextComponentTranslation(usingFaction.getUnlocalizedNamePlural())));
                return false;
            } else if (handler.getCurrentLevel() < item.getMinLevel(stack)) {
                if (message)
                    player.addChatComponentMessage(new TextComponentTranslation("text.vampirism.can_only_be_used_by_level", new TextComponentTranslation(usingFaction == null ? "text.vampirism.all" : usingFaction.getUnlocalizedNamePlural()), item.getMinLevel(stack)));
                return false;
            } else if (requiredSkill != null) {
                IFactionPlayer factionPlayer = handler.getCurrentFactionPlayer();
                if (factionPlayer == null || !factionPlayer.getSkillHandler().isSkillEnabled(requiredSkill)) {
                    if (message)
                        player.addChatComponentMessage(new TextComponentTranslation("text.vampirism.can_only_be_used_with_skill", new TextComponentTranslation(requiredSkill.getUnlocalizedName())));
                    return false;
                }
            }

        }
        return true;
    }


}
