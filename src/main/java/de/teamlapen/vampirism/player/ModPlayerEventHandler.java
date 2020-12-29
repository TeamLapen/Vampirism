package de.teamlapen.vampirism.player;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.api.items.IFactionLevelItem;
import de.teamlapen.vampirism.blocks.AltarInspirationBlock;
import de.teamlapen.vampirism.blocks.BloodContainerBlock;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.blocks.TentBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.items.BloodBottleFluidHandler;
import de.teamlapen.vampirism.items.GarlicBreadItem;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.HunterPlayerSpecialAttribute;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.VampirePlayerSpecialAttributes;
import de.teamlapen.vampirism.player.vampire.actions.BatVampireAction;
import de.teamlapen.vampirism.tileentity.TotemHelper;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Event handler for player related events
 */
public class ModPlayerEventHandler {

    private final static Logger LOGGER = LogManager.getLogger(ModPlayerEventHandler.class);

    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            try {
                event.addCapability(REFERENCE.FACTION_PLAYER_HANDLER_KEY, FactionPlayerHandler.createNewCapability((PlayerEntity) event.getObject()));
                event.addCapability(REFERENCE.VAMPIRE_PLAYER_KEY, VampirePlayer.createNewCapability((PlayerEntity) event.getObject()));
                event.addCapability(REFERENCE.HUNTER_PLAYER_KEY, HunterPlayer.createNewCapability((PlayerEntity) event.getObject()));
            } catch (Exception e) {
                LOGGER.error("Failed to attach capabilities to player. Player: {}", event.getObject());
                Throwables.propagate(e);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onAttackEntity(AttackEntityEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player.isAlive()) {
            if (VampirePlayer.get(player).getSpecialAttributes().bat || HunterPlayer.get(player).getSpecialAttributes().isDisguised()) {
                event.setCanceled(true);
            }
            if (!checkItemUsePerm(player.getHeldItemMainhand(), player)) {
                event.setCanceled(true);
            }
        }

    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof PlayerEntity) || !event.getEntity().isAlive()) return;
        try {
            if (VampirePlayer.get((PlayerEntity) event.getEntity()).getSpecialAttributes().bat || HunterPlayer.get((PlayerEntity) event.getEntity()).getSpecialAttributes().isDisguised()) {
                event.setCanceled(true);
            }
        } catch (Exception e) {
            // Added try catch to prevent any exception in case some other mod uses auto placers or so
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (VampirePlayer.getOpt(event.getPlayer()).map(VampirePlayer::getSpecialAttributes).map(s -> s.bat).orElse(false) || HunterPlayer.getOpt(event.getPlayer()).map(HunterPlayer::getSpecialAttributes).map(HunterPlayerSpecialAttribute::isDisguised).orElse(false)) {
            event.setCanceled(true);
        } else if ((ModBlocks.garlic_beacon_normal.equals(event.getState().getBlock()) || ModBlocks.garlic_beacon_weak.equals(event.getState().getBlock()) || ModBlocks.garlic_beacon_improved.equals(event.getState().getBlock())) && VampirePlayer.getOpt(event.getPlayer()).map(VampirismPlayer::getLevel).orElse(0) > 0) {
            event.setNewSpeed(event.getOriginalSpeed() * 0.1F);
        }
    }

    @SubscribeEvent
    public void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        if (!checkItemUsePerm(event.getItemStack(), event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemUse(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof PlayerEntity && (VampirePlayer.getOpt((PlayerEntity) event.getEntity()).map(VampirePlayer::getSpecialAttributes).map(s -> s.bat).orElse(false) || HunterPlayer.getOpt((PlayerEntity) event.getEntity()).map(HunterPlayer::getSpecialAttributes).map(HunterPlayerSpecialAttribute::isDisguised).orElse(false))) {
            event.setCanceled(true);
        }
        if (event.getEntity() instanceof PlayerEntity && !checkItemUsePerm(event.getItem(), (PlayerEntity) event.getEntityLiving())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemUse(LivingEntityUseItemEvent.Finish event) {
        if (Helper.isVampire(event.getEntity())) {
            if (event.getItem().getItem() instanceof GarlicBreadItem) {
                if (!event.getEntity().getEntityWorld().isRemote) {
                    DamageHandler.affectVampireGarlicDirect(event.getEntity() instanceof IVampire ? (IVampire) event.getEntity() : VampirePlayer.get((PlayerEntity) event.getEntity()), EnumStrength.MEDIUM);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            if (event.getEntity().isAlive() && !FactionPlayerHandler.getOpt((PlayerEntity) event.getEntity()).map(h -> h.onEntityAttacked(event.getSource(), event.getAmount())).orElse(false)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            event.setDistance(event.getDistance() - VampirePlayer.getOpt((PlayerEntity) event.getEntity()).map(VampirePlayer::getSpecialAttributes).map(VampirePlayerSpecialAttributes::getJumpBoost).orElse(0));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingHeal(LivingHealEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            if (VampirePlayer.getOpt((PlayerEntity) event.getEntityLiving()).map(VampirePlayer::getSpecialAttributes).map(s -> s.bat).orElse(false)) {
                event.setAmount(event.getAmount() * 0.1F);
            }
        }
    }

    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            event.getEntity().setMotion(event.getEntity().getMotion().add(0.0D, (float) (VampirePlayer.getOpt((PlayerEntity) event.getEntity()).map(VampirePlayer::getSpecialAttributes).map(VampirePlayerSpecialAttributes::getJumpBoost).orElse(0)) * 0.1F, 0.0D));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.getPlayer().getEntityWorld().isRemote) {
            FactionPlayerHandler.get(event.getPlayer()).copyFrom(event.getOriginal());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {

        //To replace glas bottles with blood bottles if interacting with a blood container
        //Also used to force block interaction with blood container if sneaking
        //DoesSneakByPassUse on the blood bottle is not enough, since the item in the offhand can still block
        if (event.getWorld().getWorldBorder().contains(event.getPos())) {
            ItemStack heldStack = event.getItemStack();
            if (!heldStack.isEmpty() && heldStack.getCount() == 1) {
                boolean glasBottle = Items.GLASS_BOTTLE.equals(heldStack.getItem());
                boolean bloodBottle = ModItems.blood_bottle.equals(heldStack.getItem());
                if (bloodBottle || (glasBottle && VampirismConfig.SERVER.autoConvertGlassBottles.get())) {
                    Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
                    BlockState state = event.getWorld().getBlockState(event.getPos());
                    boolean convert = false;
                    if (glasBottle && state.hasTileEntity()) {
                        TileEntity entity = event.getWorld().getTileEntity(event.getPos());
                        if (entity != null) {
                            convert = entity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, event.getFace()).map(fluidHandler -> {
                                boolean flag = false;
                                FluidStack drain = fluidHandler.drain(new FluidStack(ModFluids.blood, 1000), IFluidHandler.FluidAction.SIMULATE);
                                if (drain.getAmount() >= BloodBottleFluidHandler.MULTIPLIER) {
                                    flag = true;
                                }
                                if (flag && block instanceof AltarInspirationBlock) {
                                    flag = false;
                                }
                                if (flag && block instanceof BloodContainerBlock) {
                                    flag = false;
                                }
                                return flag;
                            }).orElse(false);

                        }
                    }
                    if ((bloodBottle || convert) && block instanceof BloodContainerBlock) {
                        event.setUseBlock(Event.Result.ALLOW);
                    }
                    if (convert) {
                        //Dangerous, but only solution I found so far
                        //Changes the held stack while {@link NetHandlerPlayServer#processRightClickBlock} is running which has a hard reference to the old stack
                        Hand hand = heldStack.equals(event.getPlayer().getHeldItemMainhand()) ? Hand.MAIN_HAND : (heldStack.equals(event.getPlayer().getHeldItemOffhand()) ? Hand.OFF_HAND : Hand.MAIN_HAND);
                        heldStack = new ItemStack(ModItems.blood_bottle);
                        event.getPlayer().setHeldItem(hand, heldStack);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLeftLickedBlock(PlayerInteractEvent.LeftClickBlock event) {
        assert event.getFace() != null;
        BlockPos pos = event.getPos().offset(event.getFace());
        World world = event.getWorld();
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() == ModBlocks.alchemical_fire) {
            world.playEvent(null, 1009, pos, 0);
            world.removeBlock(pos, false);
            event.setCanceled(true);
        } else if ((ModBlocks.garlic_beacon_normal.equals(state.getBlock()) || ModBlocks.garlic_beacon_weak.equals(state.getBlock()) || ModBlocks.garlic_beacon_improved.equals(state.getBlock())) && Helper.isVampire(event.getPlayer())) {
            event.getPlayer().addPotionEffect(new EffectInstance(ModEffects.garlic));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerName(PlayerEvent.NameFormat event) {
        if (event.getPlayer() != null && VampirismConfig.SERVER.factionColorInChat.get()) {
            FactionPlayerHandler.getOpt(event.getPlayer()).ifPresent(fph -> {
                fph.getCurrentFactionPlayer().ifPresent(fp -> {
                    IFaction<?> f = fp.getDisguisedAs();
                    if (f != null) {
                        IFormattableTextComponent displayName;
                        if (fph.getLordLevel() > 0 && VampirismConfig.SERVER.lordPrefixInChat.get()) {
                            displayName =new StringTextComponent("[").append(fph.getLordTitle()).appendString("] ").append(event.getDisplayname());
                        }
                        else{
                            displayName = event.getDisplayname().deepCopy();
                        }
                        event.setDisplayname(displayName.mergeStyle(f.getChatColor()));
                    }
                });
            });
        }
    }

    @SubscribeEvent
    public void onPlayerVisibilityCheck(PlayerEvent.Visibility event) {
        if (HunterPlayer.getOpt(event.getPlayer()).map(HunterPlayer::getSpecialAttributes).map(HunterPlayerSpecialAttribute::isDisguised).orElse(false)) {
            event.modifyVisibility(VampirismConfig.BALANCE.haDisguiseVisibilityMod.get());
        }
    }

    @SubscribeEvent
    public void sleepTimeCheck(SleepingTimeCheckEvent event) {
        if (Helper.isVampire(event.getPlayer())) {
            event.getSleepingLocation().ifPresent((blockPos -> event.setResult(event.getPlayer().world.getBlockState(blockPos).getBlock() instanceof CoffinBlock ? event.getPlayer().world.isDaytime() ? Event.Result.ALLOW : Event.Result.DENY : event.getResult())));
        }
        if (Helper.isHunter(event.getPlayer())) {
            event.getSleepingLocation().ifPresent((blockPos -> event.setResult(event.getPlayer().getEntityWorld().getBlockState(blockPos).getBlock() instanceof TentBlock ? !event.getPlayer().getEntityWorld().isDaytime() ? Event.Result.ALLOW : Event.Result.DENY : event.getResult())));
        }
    }

    @SubscribeEvent
    public void eyeHeight(EntityEvent.Size event) {
        if (event.getEntity() instanceof PlayerEntity && ((PlayerEntity) event.getEntity()).inventory != null /*make sure we are not in the player's contructor*/) {
            if (event.getEntity().isAlive() && event.getEntity().getPositionVec().lengthSquared() != 0) { //Do not attempt to get capability while entity is being initialized
                if (VampirePlayer.getOpt((PlayerEntity) event.getEntity()).map(vampire -> vampire.getSpecialAttributes().bat).orElse(false)) {
                    event.setNewSize(BatVampireAction.BAT_SIZE);
                    event.setNewEyeHeight(BatVampireAction.BAT_EYE_HEIGHT);
                }
            }
        }
    }

    @SubscribeEvent
    public void sleepTimeFinish(SleepFinishedTimeEvent event) {
        if (event.getWorld() instanceof ServerWorld && ((ServerWorld) event.getWorld()).isDaytime()) {
            boolean sleepingInCoffin = event.getWorld().getPlayers().stream().anyMatch(player -> {
                Optional<BlockPos> pos = player.getBedPosition();
                return pos.isPresent() && event.getWorld().getBlockState(pos.get()).getBlock() instanceof CoffinBlock;
            });
            if (sleepingInCoffin) {
                long dist = ((ServerWorld) event.getWorld()).getDayTime() % 24000L > 12000L ? 13000 : -11000; //Make sure we don't go backwards in time (in special case sleeping at 23500)
                event.setTimeAddition(event.getNewTime() + dist);

            }
        }
    }

    /**
     * Checks if the player is allowed to use that item ({@link IFactionLevelItem}) and cancels the event if not.
     *
     * @return If it is allowed to use the item
     */
    private boolean checkItemUsePerm(ItemStack stack, PlayerEntity player) {

        boolean message = !player.getEntityWorld().isRemote;
        if (!stack.isEmpty() && stack.getItem() instanceof IFactionLevelItem) {
            if (!player.isAlive()) return false;
            IFactionLevelItem item = (IFactionLevelItem) stack.getItem();
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            IPlayableFaction usingFaction = item.getUsingFaction(stack);
            ISkill requiredSkill = item.getRequiredSkill(stack);
            if (usingFaction != null && !handler.isInFaction(usingFaction)) {

                if (message)
                    player.sendStatusMessage(new TranslationTextComponent("text.vampirism.can_only_be_used_by", usingFaction.getNamePlural()), true);
                return false;
            } else if (handler.getCurrentLevel() < item.getMinLevel(stack)) {
                if (message)
                    player.sendStatusMessage(new TranslationTextComponent("text.vampirism.can_only_be_used_by_level", usingFaction == null ? new TranslationTextComponent("text.vampirism.all") : usingFaction.getNamePlural(), item.getMinLevel(stack)), true);
                return false;
            } else if (requiredSkill != null) {
                IFactionPlayer factionPlayer = handler.getCurrentFactionPlayer().orElse(null);
                if (factionPlayer == null || !factionPlayer.getSkillHandler().isSkillEnabled(requiredSkill)) {
                    if (message)
                        player.sendStatusMessage(new TranslationTextComponent("text.vampirism.can_only_be_used_with_skill", requiredSkill.getName()), true);
                    return false;
                }
            }

        }
        return true;
    }

    @SubscribeEvent
    public void blockDestroyed(BlockEvent.BreakEvent event) {
        //don't allow player to destroy blocks with PointOfInterests that are owned by a totem with different faction as the player
        if (event.getPlayer().isCreative()) return;
        Set<BlockPos> positions = new HashSet<>();
        BlockPos totemPos = TotemHelper.getTotemPosition(event.getPos());
        Block block = event.getState().getBlock();
        //if the blockstate does not have a POI, but another blockstate of the specific block eg. the bed, search for the blockstate in a 3x3x3 radius
        //or the other way around
        ImmutableList<BlockState> validStates = block.getStateContainer().getValidStates();
        if (validStates.size() > 1 && PointOfInterestType.BLOCKS_OF_INTEREST.stream().anyMatch(validStates::contains)) {
            for (int x = event.getPos().getX() - 1; x <= event.getPos().getX() + 1; ++x) {
                for (int z = event.getPos().getZ() - 1; z <= event.getPos().getZ() + 1; ++z) {
                    for (double y = event.getPos().getY() - 1; y <= event.getPos().getY() + 1; ++y) {
                        BlockPos pos1 = new BlockPos(x, y, z);
                        if (event.getWorld().getChunkProvider().isChunkLoaded(new ChunkPos(pos1)) && event.getWorld().getBlockState(pos1).getBlock() == block) {
                            BlockPos totemPos1 = TotemHelper.getTotemPosition(pos1);
                            if (totemPos1 != null && totemPos == null) {
                                totemPos = totemPos1;
                            }
                            positions.add(pos1);
                        }
                    }
                }
            }
        }
        //cancel the event and notify client about the failed block destroy.
        //also notify client about wrong destroyed neighbor blocks (bed)
        if (totemPos != null && event.getWorld().isBlockLoaded(totemPos)) {
            TileEntity totem = (event.getWorld().getTileEntity(totemPos));
            if (totem instanceof TotemTileEntity && ((TotemTileEntity) totem).getControllingFaction() != null && VampirismAPI.getFactionPlayerHandler(event.getPlayer()).map(player -> player.getCurrentFaction() != ((TotemTileEntity) totem).getControllingFaction()).orElse(true)) {
                event.setCanceled(true);
                event.getPlayer().sendStatusMessage(new TranslationTextComponent("text.vampirism.village.totem_destroy.fail_totem_faction"), true);
                if (!positions.isEmpty()) {
                    positions.forEach(pos -> {
                        ((ServerPlayerEntity) event.getPlayer()).connection.sendPacket(new SChangeBlockPacket(event.getWorld(), pos));
                        TileEntity tileentity = event.getWorld().getTileEntity(pos);
                        if (tileentity != null) {
                            IPacket<?> pkt = tileentity.getUpdatePacket();
                            if (pkt != null) {
                                ((ServerPlayerEntity) event.getPlayer()).connection.sendPacket(pkt);
                            }
                        }
                    });
                }
            }
        }
    }
}
