package de.teamlapen.vampirism.player;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.api.items.IFactionLevelItem;
import de.teamlapen.vampirism.blockentity.TotemBlockEntity;
import de.teamlapen.vampirism.blockentity.TotemHelper;
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
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
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
        if (!(event.getLevel() instanceof Level)) return;
        //don't allow player to destroy blocks with PointOfInterests that are owned by a totem with different faction as the player
        if (event.getPlayer().isCreative()) return;
        Set<BlockPos> positions = new HashSet<>();
        BlockPos totemPos = TotemHelper.getTotemPosition(((Level) event.getLevel()).dimension(), event.getPos());
        Block block = event.getState().getBlock();
        //if the blockstate does not have a POI, but another blockstate of the specific block e.g. the bed, search for the blockstate in a 3x3x3 radius
        //or the other way around
        ImmutableList<BlockState> validStates = block.getStateDefinition().getPossibleStates();
        if (validStates.size() > 1 && RegUtil.values(ForgeRegistries.POI_TYPES).stream().flatMap(poiType -> poiType.matchingStates().stream()).anyMatch(validStates::contains)) {
            for (int x = event.getPos().getX() - 1; x <= event.getPos().getX() + 1; ++x) {
                for (int z = event.getPos().getZ() - 1; z <= event.getPos().getZ() + 1; ++z) {
                    for (double y = event.getPos().getY() - 1; y <= event.getPos().getY() + 1; ++y) {
                        BlockPos pos1 = new BlockPos(x, y, z);
                        if (((Level) event.getLevel()).isLoaded(pos1) && event.getLevel().getBlockState(pos1).getBlock() == block) {
                            BlockPos totemPos1 = TotemHelper.getTotemPosition(((Level) event.getLevel()).dimension(), pos1);
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
        if (totemPos != null && event.getLevel().hasChunkAt(totemPos)) {
            BlockEntity totem = (event.getLevel().getBlockEntity(totemPos));
            if (totem instanceof TotemBlockEntity && ((TotemBlockEntity) totem).getControllingFaction() != null && VampirismPlayerAttributes.get(event.getPlayer()).faction != ((TotemBlockEntity) totem).getControllingFaction()) {
                event.setCanceled(true);
                event.getPlayer().displayClientMessage(Component.translatable("text.vampirism.village.totem_destroy.fail_totem_faction"), true);
                if (!positions.isEmpty() && event.getPlayer() instanceof ServerPlayer player) {
                    positions.forEach(pos -> {
                        player.connection.send(new ClientboundBlockUpdatePacket(event.getLevel(), pos));
                        BlockEntity tileentity = event.getLevel().getBlockEntity(pos);
                        if (tileentity != null) {
                            Packet<?> pkt = tileentity.getUpdatePacket();
                            if (pkt != null) {
                                player.connection.send(pkt);
                            }
                        }
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public void eyeHeight(EntityEvent.Size event) {
        if (event.getEntity() instanceof Player && ((Player) event.getEntity()).getInventory() != null /*make sure we are not in the player's contructor*/) {
            if (event.getEntity().isAlive() && event.getEntity().position().lengthSqr() != 0 && event.getEntity().getVehicle() == null) { //Do not attempt to get capability while entity is being initialized
                if (VampirismPlayerAttributes.get((Player) event.getEntity()).getVampSpecial().bat) {
                    event.setNewSize(BatVampireAction.BAT_SIZE);
                    event.setNewEyeHeight(BatVampireAction.BAT_EYE_HEIGHT);
                } else if (VampirismPlayerAttributes.get((Player) event.getEntity()).getVampSpecial().isDBNO) {
                    event.setNewSize(EntityDimensions.fixed(0.6f, 0.95f));
                    event.setNewEyeHeight(0.725f);
                }
            }
        }
    }

    @SubscribeEvent
    public void onTryMount(EntityMountEvent event){
        if (event.getEntity() instanceof Player && VampirismPlayerAttributes.get((Player) event.getEntity()).getVampSpecial().isCannotInteract()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            try {
                event.addCapability(REFERENCE.FACTION_PLAYER_HANDLER_KEY, FactionPlayerHandler.createNewCapability((Player) event.getObject()));
                event.addCapability(REFERENCE.VAMPIRE_PLAYER_KEY, VampirePlayer.createNewCapability((Player) event.getObject()));
                event.addCapability(REFERENCE.HUNTER_PLAYER_KEY, HunterPlayer.createNewCapability((Player) event.getObject()));
            } catch (Exception e) {
                LOGGER.error("Failed to attach capabilities to player. Player: {}", event.getObject());
                Throwables.propagate(e);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
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
        if (!(event.getEntity() instanceof Player) || !event.getEntity().isAlive()) return;
        if(event.getPlacedBlock().isAir()) return; //If for some reason, cough Create cough, a block is removed (so air is placed) we don't want to prevent that.
        try {
            if (VampirismPlayerAttributes.get((Player) event.getEntity()).getVampSpecial().isCannotInteract()) {
                event.setCanceled(true);

                //Workaround for https://github.com/MinecraftForge/MinecraftForge/issues/7609 or https://github.com/TeamLapen/Vampirism/issues/1021
                //Chest drops content when restoring snapshot
                if(event.getPlacedBlock().hasBlockEntity()){
                    BlockEntity t =  event.getLevel().getBlockEntity(event.getPos());
                    if(t instanceof Container){
                        ((Container) t).clearContent();
                    }
                }

                if(event.getEntity() instanceof ServerPlayer){ //For some reason this event is only run serverside. Therefore, we have to make sure the client is notified about the not-placed block.
                    MinecraftServer server = event.getEntity().level.getServer();
                    if(server!=null){
                        server.getPlayerList().sendAllPlayerInfo((ServerPlayer) event.getEntity()); //Would probably suffice to just sent a SHeldItemChangePacket
                    }
                }
            }
            HunterPlayer.getOpt((Player) event.getEntity()).ifPresent(HunterPlayer::breakDisguise);
        } catch (Exception e) {
            // Added try catch to prevent any exception in case some other mod uses auto placers or so
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (VampirismPlayerAttributes.get((Player) event.getEntity()).getVampSpecial().isCannotInteract()) {
            event.setCanceled(true);
        } else if ((ModBlocks.GARLIC_DIFFUSER_NORMAL.get() == event.getState().getBlock() || ModBlocks.GARLIC_DIFFUSER_WEAK.get() == event.getState().getBlock() || ModBlocks.GARLIC_DIFFUSER_IMPROVED.get() == event.getState().getBlock()) && VampirismPlayerAttributes.get(event.getEntity()).vampireLevel > 0) {
            event.setNewSpeed(event.getOriginalSpeed() * 0.1F);
        }
    }

    @SubscribeEvent
    public void onItemPickupPre(EntityItemPickupEvent event) {
        if (VampirismPlayerAttributes.get((Player) event.getEntity()).getVampSpecial().isDBNO) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        if (!checkItemUsePerm(event.getItemStack(), event.getEntity())) {
            event.setCanceled(true);
        }

        if ((event.getItemStack().getItem() instanceof ThrowablePotionItem || event.getItemStack().getItem() instanceof CrossbowItem)) {
            if (VampirismPlayerAttributes.get((Player) event.getEntity()).getVampSpecial().isCannotInteract()) {
                event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemUse(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof Player player) {
            if (VampirismPlayerAttributes.get((Player) event.getEntity()).getVampSpecial().isCannotInteract()) {
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
                    } else if (event.getEntity() instanceof Player) {
                        VampirePlayer.getOpt((Player) event.getEntity()).ifPresent(vampire -> {
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
                if (p instanceof VampirismPotion.HunterPotion && p.getEffects().stream().map(MobEffectInstance::getEffect).anyMatch(MobEffect::isBeneficial)) {
                    event.getEntity().addEffect(new MobEffectInstance(ModEffects.POISON.get(), Integer.MAX_VALUE, VampirismPoisonEffect.DEADLY_AMPLIFIER));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().isAlive() && !FactionPlayerHandler.getOpt((Player) event.getEntity()).map(h -> h.onEntityAttacked(event.getSource(), event.getAmount())).orElse(false)) {
                event.setCanceled(true);
            }
        }
        if (event.getSource().getEntity() instanceof Player) {
            HunterPlayer.getOpt((Player) event.getSource().getEntity()).ifPresent(HunterPlayer::breakDisguise);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDeathFirst(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            if (VampirePlayer.getOpt((Player) event.getEntity()).map(v -> v.onDeadlyHit(event.getSource())).orElse(false))
                event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setDistance(event.getDistance() - VampirismPlayerAttributes.get((Player) event.getEntity()).getVampSpecial().getJumpBoost());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingHurt(LivingHurtEvent event) {
        DamageSource d = event.getSource();
        if (!d.isBypassMagic() && !d.isBypassArmor() && event.getEntity() instanceof Player) {
            if (VampirismPlayerAttributes.get((Player) event.getEntity()).getVampSpecial().bat) {
                event.setAmount(event.getAmount() * 2);
            }
        }
    }

    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player) {
            event.getEntity().setDeltaMovement(event.getEntity().getDeltaMovement().add(0.0D, (float) VampirismPlayerAttributes.get((Player) event.getEntity()).getVampSpecial().getJumpBoost() * 0.1F, 0.0D));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.getEntity().getCommandSenderWorld().isClientSide) {
            event.getOriginal().reviveCaps();
            FactionPlayerHandler.get(event.getEntity()).copyFrom(event.getOriginal());
            event.getOriginal().invalidateCaps();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {

        //To replace glas bottles with blood bottles if interacting with a blood container
        //Also used to force block interaction with blood container if sneaking
        //DoesSneakByPassUse on the blood bottle is not enough, since the item in the offhand can still block
        if (event.getLevel().getWorldBorder().isWithinBounds(event.getPos())) {
            ItemStack heldStack = event.getItemStack();
            if (!heldStack.isEmpty() && heldStack.getCount() == 1) {
                boolean glassBottle = Items.GLASS_BOTTLE.equals(heldStack.getItem());
                boolean bloodBottle = ModItems.BLOOD_BOTTLE.get() == heldStack.getItem();
                if (bloodBottle || (glassBottle && VampirismConfig.COMMON.autoConvertGlassBottles.get())) {
                    Block block = event.getLevel().getBlockState(event.getPos()).getBlock();
                    BlockState state = event.getLevel().getBlockState(event.getPos());
                    boolean convert = false;
                    if (glassBottle && state.hasBlockEntity()) {
                        BlockEntity entity = event.getLevel().getBlockEntity(event.getPos());
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
                        InteractionHand hand = heldStack.equals(event.getEntity().getMainHandItem()) ? InteractionHand.MAIN_HAND : (heldStack.equals(event.getEntity().getOffhandItem()) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
                        heldStack = new ItemStack(ModItems.BLOOD_BOTTLE.get());
                        event.getEntity().setItemInHand(hand, heldStack);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget().getType() == EntityType.ZOMBIE)) return;
        ItemStack stack = event.getEntity().getItemInHand(event.getHand());
        if (stack.getItem() != ModItems.INJECTION_EMPTY.get()) return;
        event.getEntity().setItemInHand(event.getHand(), new ItemStack(ModItems.INJECTION_ZOMBIE_BLOOD.get()));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onPlayerLeftClickedBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getFace() == null) return;
        BlockPos pos = event.getPos().relative(event.getFace());
        Level world = event.getLevel();
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() == ModBlocks.ALCHEMICAL_FIRE.get()) {
            world.levelEvent(null, 1009, pos, 0);
            world.removeBlock(pos, false);
            event.setCanceled(true);
        } else if ((ModBlocks.GARLIC_DIFFUSER_NORMAL.get() == state.getBlock() || ModBlocks.GARLIC_DIFFUSER_WEAK.get() == state.getBlock() || ModBlocks.GARLIC_DIFFUSER_IMPROVED.get() == state.getBlock()) && Helper.isVampire(event.getEntity())) {
            event.getEntity().addEffect(new MobEffectInstance(ModEffects.GARLIC.get()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerName(PlayerEvent.NameFormat event) {
        if (event.getEntity() != null && VampirismConfig.SERVER.factionColorInChat.get()) {
            FactionPlayerHandler.getOpt(event.getEntity()).ifPresent(fph -> {
                fph.getCurrentFactionPlayer().ifPresent(fp -> {
                    IFaction<?> f = fp.getDisguisedAs();
                    if (f != null) {
                        MutableComponent displayName;
                        if (fph.getLordLevel() > 0 && VampirismConfig.SERVER.lordPrefixInChat.get()) {
                            displayName = Component.literal("[").append(fph.getLordTitle()).append("] ").append(event.getDisplayname());
                        } else {
                            displayName = event.getDisplayname().copy();
                        }
                        event.setDisplayname(displayName.withStyle(style -> style.withColor((f.getChatColor()))));
                    }
                });
            });
        }
    }

    @SubscribeEvent
    public void sleepTimeCheck(SleepingTimeCheckEvent event) {
        if (Helper.isVampire(event.getEntity())) {
            event.getSleepingLocation().ifPresent((blockPos -> event.setResult(event.getEntity().level.getBlockState(blockPos).getBlock() instanceof CoffinBlock ? event.getEntity().level.isDay() ? Event.Result.ALLOW : Event.Result.DENY : event.getResult())));
        }
        if (Helper.isHunter(event.getEntity())) {
            event.getSleepingLocation().ifPresent((blockPos -> event.setResult(event.getEntity().getCommandSenderWorld().getBlockState(blockPos).getBlock() instanceof TentBlock ? !event.getEntity().getCommandSenderWorld().isDay() ? Event.Result.ALLOW : Event.Result.DENY : event.getResult())));
        }
    }

    @SubscribeEvent
    public void sleepTimeFinish(SleepFinishedTimeEvent event) {
        if (event.getLevel() instanceof ServerLevel && ((ServerLevel) event.getLevel()).isDay()) {
            boolean sleepingInCoffin = event.getLevel().players().stream().anyMatch(player -> {
                Optional<BlockPos> pos = player.getSleepingPos();
                return pos.isPresent() && event.getLevel().getBlockState(pos.get()).getBlock() instanceof CoffinBlock;
            });
            if (sleepingInCoffin) {
                long dist = ((ServerLevel) event.getLevel()).getDayTime() % 24000L > 12000L ? 13000 : -11000; //Make sure we don't go backwards in time (in special case sleeping at 23500)
                event.setTimeAddition(event.getNewTime() + dist);

            }
        }
    }

    /**
     * Checks if the player is allowed to use that item ({@link IFactionLevelItem}) and cancels the event if not.
     *
     * @return If it is allowed to use the item
     */
    private boolean checkItemUsePerm(ItemStack stack, Player player) {

        boolean message = !player.getCommandSenderWorld().isClientSide;
        if (!stack.isEmpty() && stack.getItem() instanceof IFactionLevelItem item) {
            if (!player.isAlive()) return false;
            LazyOptional<FactionPlayerHandler> handler = FactionPlayerHandler.getOpt(player);
            IFaction<?> usingFaction = item.getExclusiveFaction(stack);
            ISkill<?> requiredSkill = item.getRequiredSkill(stack);
            if (usingFaction != null && !handler.map(h->h.isInFaction(usingFaction)).orElse(false)) {
                if (message)
                    player.displayClientMessage(Component.translatable("text.vampirism.can_only_be_used_by", usingFaction.getNamePlural()), true);
                return false;
            } else if (handler.map(FactionPlayerHandler::getCurrentLevel).orElse(0) < item.getMinLevel(stack)) {
                if (message)
                    player.displayClientMessage(Component.translatable("text.vampirism.can_only_be_used_by_level", usingFaction == null ? Component.translatable("text.vampirism.all") : usingFaction.getNamePlural(), item.getMinLevel(stack)), true);
                return false;
            } else if (requiredSkill != null) {
                IFactionPlayer<?> factionPlayer = handler.resolve().flatMap(FactionPlayerHandler::getCurrentFactionPlayer).orElse(null);
                if (factionPlayer == null || !factionPlayer.getSkillHandler().isSkillEnabled(requiredSkill)) {
                    if (message)
                        player.displayClientMessage(Component.translatable("text.vampirism.can_only_be_used_with_skill", requiredSkill.getName()), true);
                    return false;
                }
            }

        }
        return true;
    }
}
