package de.teamlapen.vampirism.player;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.EnumStrength;
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
import de.teamlapen.vampirism.effects.VampirismPoisonEffect;
import de.teamlapen.vampirism.effects.VampirismPotion;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.items.BloodBottleFluidHandler;
import de.teamlapen.vampirism.items.GarlicBreadItem;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.actions.BatVampireAction;
import de.teamlapen.vampirism.tileentity.TotemHelper;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
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
    public void blockDestroyed(BlockEvent.BreakEvent event) {
        if (!(event.getWorld() instanceof World)) return;
        //don't allow player to destroy blocks with PointOfInterests that are owned by a totem with different faction as the player
        if (event.getPlayer().isCreative()) return;
        Set<BlockPos> positions = new HashSet<>();
        BlockPos totemPos = TotemHelper.getTotemPosition(((World) event.getWorld()).dimension(), event.getPos());
        Block block = event.getState().getBlock();
        //if the blockstate does not have a POI, but another blockstate of the specific block eg. the bed, search for the blockstate in a 3x3x3 radius
        //or the other way around
        ImmutableList<BlockState> validStates = block.getStateDefinition().getPossibleStates();
        if (validStates.size() > 1 && PointOfInterestType.ALL_STATES.stream().anyMatch(validStates::contains)) {
            for (int x = event.getPos().getX() - 1; x <= event.getPos().getX() + 1; ++x) {
                for (int z = event.getPos().getZ() - 1; z <= event.getPos().getZ() + 1; ++z) {
                    for (double y = event.getPos().getY() - 1; y <= event.getPos().getY() + 1; ++y) {
                        BlockPos pos1 = new BlockPos(x, y, z);
                        if (event.getWorld().getChunkSource().isEntityTickingChunk(new ChunkPos(pos1)) && event.getWorld().getBlockState(pos1).getBlock() == block) {
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
        if (totemPos != null && event.getWorld().hasChunkAt(totemPos)) {
            TileEntity totem = (event.getWorld().getBlockEntity(totemPos));
            if (totem instanceof TotemTileEntity && ((TotemTileEntity) totem).getControllingFaction() != null && VampirismPlayerAttributes.get(event.getPlayer()).faction != ((TotemTileEntity) totem).getControllingFaction()) {
                event.setCanceled(true);
                event.getPlayer().displayClientMessage(new TranslationTextComponent("text.vampirism.village.totem_destroy.fail_totem_faction"), true);
                if (!positions.isEmpty() && event.getPlayer() instanceof ServerPlayerEntity) {
                    ServerPlayerEntity playerMP = (ServerPlayerEntity) event.getPlayer();
                    positions.forEach(pos -> {
                        playerMP.connection.send(new SChangeBlockPacket(event.getWorld(), pos));
                        TileEntity tileentity = event.getWorld().getBlockEntity(pos);
                        if (tileentity != null) {
                            IPacket<?> pkt = tileentity.getUpdatePacket();
                            if (pkt != null) {
                                playerMP.connection.send(pkt);
                            }
                        }
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public void eyeHeight(EntityEvent.Size event) {
        if (event.getEntity() instanceof PlayerEntity && ((PlayerEntity) event.getEntity()).inventory != null /*make sure we are not in the player's contructor*/) {
            if (event.getEntity().isAlive() && event.getEntity().position().lengthSqr() != 0 && event.getEntity().getVehicle() == null) { //Do not attempt to get capability while entity is being initialized
                if (VampirismPlayerAttributes.get((PlayerEntity) event.getEntity()).getVampSpecial().bat) {
                    event.setNewSize(BatVampireAction.BAT_SIZE);
                    event.setNewEyeHeight(BatVampireAction.BAT_EYE_HEIGHT);

                } else if (VampirismPlayerAttributes.get((PlayerEntity) event.getEntity()).getVampSpecial().isDBNO) {
                    event.setNewSize(EntitySize.fixed(0.6f, 0.95f));
                    event.setNewEyeHeight(0.725f);
                }
            }
        }
    }

    @SubscribeEvent
    public void onTryMount(EntityMountEvent event){
        if (event.getEntity() instanceof PlayerEntity && VampirismPlayerAttributes.get((PlayerEntity) event.getEntity()).getVampSpecial().isCannotInteract()) {
            event.setCanceled(true);
        }
    }

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
            if (VampirismPlayerAttributes.get(player).getVampSpecial().bat) {
                event.setCanceled(true);
            }
            HunterPlayer.getOpt(player).ifPresent(HunterPlayer::breakDisguise);
            if (!checkItemUsePerm(player.getMainHandItem(), player)) {
                event.setCanceled(true);
            }
        }

    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() != null) {
            HunterPlayer.getOpt(event.getPlayer()).ifPresent(HunterPlayer::breakDisguise);
        }
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof PlayerEntity) || !event.getEntity().isAlive()) return;
        if(event.getPlacedBlock().isAir(event.getWorld(), event.getPos())) return; //If for some reason, cough Create cough, a block is removed (so air is placed) we don't want to prevent that.
        try {
            if (VampirismPlayerAttributes.get((PlayerEntity) event.getEntity()).getVampSpecial().isCannotInteract()) {
                event.setCanceled(true);

                //Workaround for https://github.com/MinecraftForge/MinecraftForge/issues/7609 or https://github.com/TeamLapen/Vampirism/issues/1021
                //Chest drops content when restoring snapshot
                if(event.getPlacedBlock().hasTileEntity()){
                    TileEntity t =  event.getWorld().getBlockEntity(event.getPos());
                    if(t instanceof IInventory){
                        ((IInventory) t).clearContent();
                    }
                }

                if(event.getEntity() instanceof ServerPlayerEntity){ //For some reason this event is only run serverside. Therefore, we have to make sure the client is notified about the not-placed block.
                    MinecraftServer server = event.getEntity().level.getServer();
                    if(server!=null){
                        server.getPlayerList().sendAllPlayerInfo((ServerPlayerEntity) event.getEntity()); //Would probably suffice to just sent a SHeldItemChangePacket
                    }
                }
            }
            HunterPlayer.getOpt((PlayerEntity) event.getEntity()).ifPresent(HunterPlayer::breakDisguise);
        } catch (Exception e) {
            // Added try catch to prevent any exception in case some other mod uses auto placers or so
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (VampirismPlayerAttributes.get((PlayerEntity) event.getEntity()).getVampSpecial().isCannotInteract()) {
            event.setCanceled(true);
        } else if ((ModBlocks.GARLIC_BEACON_NORMAL.get().equals(event.getState().getBlock()) || ModBlocks.GARLIC_BEACON_WEAK.get().equals(event.getState().getBlock()) || ModBlocks.GARLIC_BEACON_IMPROVED.get().equals(event.getState().getBlock())) && VampirismPlayerAttributes.get(event.getPlayer()).vampireLevel > 0) {
            event.setNewSpeed(event.getOriginalSpeed() * 0.1F);
        }
    }

    @SubscribeEvent
    public void onItemPickupPre(EntityItemPickupEvent event) {
        if (VampirismPlayerAttributes.get((PlayerEntity) event.getEntity()).getVampSpecial().isDBNO) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        if (!checkItemUsePerm(event.getItemStack(), event.getPlayer())) {
            event.setCanceled(true);
        }

        if ((event.getItemStack().getItem() instanceof ThrowablePotionItem || event.getItemStack().getItem() instanceof CrossbowItem)) {
            if (VampirismPlayerAttributes.get((PlayerEntity) event.getEntity()).getVampSpecial().isCannotInteract()) {
                event.setCancellationResult(ActionResultType.sidedSuccess(event.getWorld().isClientSide()));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemUse(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            if (VampirismPlayerAttributes.get((PlayerEntity) event.getEntity()).getVampSpecial().isCannotInteract()) {
                event.setCanceled(true);
            }
            if (!checkItemUsePerm(event.getItem(), player)) {
                event.setCanceled(true);
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemUse(LivingEntityUseItemEvent.Finish event) {
        if (Helper.isVampire(event.getEntity())) {
            if (event.getItem().getItem() instanceof GarlicBreadItem) {
                if (!event.getEntity().getCommandSenderWorld().isClientSide) {
                    if (event.getEntity() instanceof IVampire) {
                        DamageHandler.affectVampireGarlicDirect((IVampire) event.getEntity(), EnumStrength.MEDIUM);
                    } else if (event.getEntity() instanceof PlayerEntity) {
                        VampirePlayer.getOpt((PlayerEntity) event.getEntity()).ifPresent(vampire -> {
                            DamageHandler.affectVampireGarlicDirect(vampire, EnumStrength.MEDIUM);
                        });
                    }
                }
            }
        }
        if (!Helper.isHunter(event.getEntity())) {
            ItemStack stack = event.getItem();
            if (stack.getItem() == Items.POTION) {
                Potion p = PotionUtils.getPotion(stack);
                if (p instanceof VampirismPotion.HunterPotion && p.getEffects().stream().map(EffectInstance::getEffect).anyMatch(Effect::isBeneficial)) {
                    event.getEntityLiving().addEffect(new EffectInstance(ModEffects.POISON.get(), Integer.MAX_VALUE, VampirismPoisonEffect.DEADLY_AMPLIFIER));
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
        if (event.getSource().getEntity() instanceof PlayerEntity) {
            HunterPlayer.getOpt((PlayerEntity) event.getSource().getEntity()).ifPresent(HunterPlayer::breakDisguise);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDeathFirst(LivingDeathEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            if (VampirePlayer.getOpt((PlayerEntity) event.getEntity()).map(v -> v.onDeadlyHit(event.getSource())).orElse(false))
                event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            event.setDistance(event.getDistance() - VampirismPlayerAttributes.get((PlayerEntity) event.getEntity()).getVampSpecial().getJumpBoost());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingHurt(LivingHurtEvent event) {
        DamageSource d = event.getSource();
        if (!d.isBypassMagic() && !d.isBypassArmor() && event.getEntityLiving() instanceof PlayerEntity) {
            if (VampirismPlayerAttributes.get((PlayerEntity) event.getEntity()).getVampSpecial().bat) {
                event.setAmount(event.getAmount() * 2);
            }
        }
    }

    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            event.getEntity().setDeltaMovement(event.getEntity().getDeltaMovement().add(0.0D, (float) VampirismPlayerAttributes.get((PlayerEntity) event.getEntity()).getVampSpecial().getJumpBoost() * 0.1F, 0.0D));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.getPlayer().getCommandSenderWorld().isClientSide) {
            FactionPlayerHandler.get(event.getPlayer()).copyFrom(event.getOriginal());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {

        //To replace glas bottles with blood bottles if interacting with a blood container
        //Also used to force block interaction with blood container if sneaking
        //DoesSneakByPassUse on the blood bottle is not enough, since the item in the offhand can still block
        if (event.getWorld().getWorldBorder().isWithinBounds(event.getPos())) {
            ItemStack heldStack = event.getItemStack();
            if (!heldStack.isEmpty() && heldStack.getCount() == 1) {
                boolean glasBottle = Items.GLASS_BOTTLE.equals(heldStack.getItem());
                boolean bloodBottle = ModItems.BLOOD_BOTTLE.get().equals(heldStack.getItem());
                if (bloodBottle || (glasBottle && VampirismConfig.COMMON.autoConvertGlassBottles.get())) {
                    Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
                    BlockState state = event.getWorld().getBlockState(event.getPos());
                    boolean convert = false;
                    if (glasBottle && state.hasTileEntity()) {
                        TileEntity entity = event.getWorld().getBlockEntity(event.getPos());
                        if (entity != null) {
                            convert = entity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, event.getFace()).map(fluidHandler -> {
                                boolean flag = false;
                                FluidStack drain = fluidHandler.drain(new FluidStack(ModFluids.BLOOD.get(), 1000), IFluidHandler.FluidAction.SIMULATE);
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
                        Hand hand = heldStack.equals(event.getPlayer().getMainHandItem()) ? Hand.MAIN_HAND : (heldStack.equals(event.getPlayer().getOffhandItem()) ? Hand.OFF_HAND : Hand.MAIN_HAND);
                        heldStack = new ItemStack(ModItems.BLOOD_BOTTLE.get());
                        event.getPlayer().setItemInHand(hand, heldStack);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget().getType() == EntityType.ZOMBIE)) return;
        ItemStack stack = event.getPlayer().getItemInHand(event.getHand());
        if (stack.getItem() != ModItems.INJECTION_EMPTY.get()) return;
        event.getPlayer().setItemInHand(event.getHand(), new ItemStack(ModItems.INJECTION_ZOMBIE_BLOOD.get()));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onPlayerLeftClickedBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getFace() == null) return;
        BlockPos pos = event.getPos().relative(event.getFace());
        World world = event.getWorld();
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() == ModBlocks.ALCHEMICAL_FIRE.get()) {
            world.levelEvent(null, 1009, pos, 0);
            world.removeBlock(pos, false);
            event.setCanceled(true);
        } else if ((ModBlocks.GARLIC_BEACON_NORMAL.get().equals(state.getBlock()) || ModBlocks.GARLIC_BEACON_WEAK.get().equals(state.getBlock()) || ModBlocks.GARLIC_BEACON_IMPROVED.get().equals(state.getBlock())) && Helper.isVampire(event.getPlayer())) {
            event.getPlayer().addEffect(new EffectInstance(ModEffects.GARLIC.get()));
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
                            displayName = new StringTextComponent("[").append(fph.getLordTitle()).append("] ").append(event.getDisplayname());
                        } else {
                            displayName = event.getDisplayname().copy();
                        }
                        event.setDisplayname(displayName.withStyle(f.getChatColor()));
                    }
                });
            });
        }
    }

    @SubscribeEvent
    public void sleepTimeCheck(SleepingTimeCheckEvent event) {
        if (Helper.isVampire(event.getPlayer())) {
            event.getSleepingLocation().ifPresent((blockPos -> event.setResult(event.getPlayer().level.getBlockState(blockPos).getBlock() instanceof CoffinBlock ? event.getPlayer().level.isDay() ? Event.Result.ALLOW : Event.Result.DENY : event.getResult())));
        }
        if (Helper.isHunter(event.getPlayer())) {
            event.getSleepingLocation().ifPresent((blockPos -> event.setResult(event.getPlayer().getCommandSenderWorld().getBlockState(blockPos).getBlock() instanceof TentBlock ? !event.getPlayer().getCommandSenderWorld().isDay() ? Event.Result.ALLOW : Event.Result.DENY : event.getResult())));
        }
    }

    @SubscribeEvent
    public void sleepTimeFinish(SleepFinishedTimeEvent event) {
        if (event.getWorld() instanceof ServerWorld && ((ServerWorld) event.getWorld()).isDay()) {
            boolean sleepingInCoffin = event.getWorld().players().stream().anyMatch(player -> {
                Optional<BlockPos> pos = player.getSleepingPos();
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
        boolean message = !player.getCommandSenderWorld().isClientSide;
        if (!stack.isEmpty() && stack.getItem() instanceof IFactionLevelItem) {
            if (!player.isAlive()) return false;
            IFactionLevelItem<?> item = (IFactionLevelItem<?>) stack.getItem();
            LazyOptional<FactionPlayerHandler> handler = FactionPlayerHandler.getOpt(player);
            IPlayableFaction<? extends IFactionPlayer<?>> usingFaction = item.getUsingFaction(stack);
            ISkill requiredSkill = item.getRequiredSkill(stack);
            if (usingFaction != null && !handler.map(h->h.isInFaction(usingFaction)).orElse(false)) {
                if (message)
                    player.displayClientMessage(new TranslationTextComponent("text.vampirism.can_only_be_used_by", usingFaction.getNamePlural()), true);
                return false;
            } else if (handler.map(FactionPlayerHandler::getCurrentLevel).orElse(0) < item.getMinLevel(stack)) {
                if (message)
                    player.displayClientMessage(new TranslationTextComponent("text.vampirism.can_only_be_used_by_level", usingFaction == null ? new TranslationTextComponent("text.vampirism.all") : usingFaction.getNamePlural(), item.getMinLevel(stack)), true);
                return false;
            } else if (requiredSkill != null) {
                IFactionPlayer<?> factionPlayer = handler.resolve().flatMap(FactionPlayerHandler::getCurrentFactionPlayer).orElse(null);
                if (factionPlayer == null || !factionPlayer.getSkillHandler().isSkillEnabled(requiredSkill)) {
                    if (message)
                        player.displayClientMessage(new TranslationTextComponent("text.vampirism.can_only_be_used_with_skill", requiredSkill.getName()), true);
                    return false;
                }
            }

        }
        return true;
    }
}
