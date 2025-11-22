package de.teamlapen.vampirism.common.entity.player;

import com.google.common.collect.ImmutableList;
import de.teamlapen.factions.FactionsMod;
import de.teamlapen.factions.common.core.FactionDataComponents;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.items.components.IBottleBlood;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.actions.IActionHandler;
import de.teamlapen.factions.common.blockentity.TotemBlockEntity;
import de.teamlapen.vampirism.common.blocks.BloodContainerBlock;
import de.teamlapen.vampirism.common.blocks.CoffinBlock;
import de.teamlapen.vampirism.common.blocks.mother.MotherBlock;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.*;
import de.teamlapen.vampirism.common.effects.VampirismPoisonMobEffect;
import de.teamlapen.vampirism.common.entity.SundamageRegistry;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.common.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.entity.player.vampire.actions.BatVampireAction;
import de.teamlapen.factions.common.components.FactionRestriction;
import de.teamlapen.factions.common.components.FactionSlayer;
import de.teamlapen.vampirism.common.items.crossbow.HunterCrossbowItem;
import de.teamlapen.vampirism.common.potions.BasePotion;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.util.RegUtil;
import de.teamlapen.factions.common.util.TotemHelper;
import de.teamlapen.vampirism.common.world.attachments.LevelFog;
import de.teamlapen.vampirism.common.world.attachments.LevelGarlic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.TriState;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

/**
 * Event handler for player related events
 */
public class ModPlayerEventHandler {

    @SubscribeEvent
    public void eyeHeight(EntityEvent.@NotNull Size event) {
        if (event.getEntity() instanceof Player && ((Player) event.getEntity()).getInventory() != null /*make sure we are not in the player's contructor*/) {
            if (event.getEntity().isAlive() && event.getEntity().position().lengthSqr() != 0 && event.getEntity().getVehicle() == null) { //Do not attempt to get capability while entity is being initialized
                if (VampirePlayer.get((Player) event.getEntity()).getSpecialAttributes().bat) {
                    event.setNewSize(BatVampireAction.BAT_SIZE);
                } else if (VampirePlayer.get((Player) event.getEntity()).getSpecialAttributes().isDBNO) {
                    event.setNewSize(EntityDimensions.fixed(0.6f, 0.95f).withEyeHeight(0.725f));
                }
            }
        }
    }

    @SubscribeEvent
    public void onTryMount(@NotNull EntityMountEvent event) {
        if (event.getEntity() instanceof Player && VampirismPlayerAttributes.get((Player) event.getEntity()).getVampSpecial().isCannotInteract()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onAttackEntity(@NotNull AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.isAlive()) {
            if (VampirePlayer.get(player).getSpecialAttributes().bat) {
                event.setCanceled(true);
            }
            HunterPlayer.get(player).breakDisguise();
            if (!FactionRestriction.canUse(player, player.getMainHandItem(), true)) {
                event.setCanceled(true);
            }
        }

    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.@NotNull BreakEvent event) {
        HunterPlayer.get(event.getPlayer()).breakDisguise();
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.@NotNull EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Player) || !event.getEntity().isAlive()) return;
        if (event.getPlacedBlock().isAir()) return; //If for some reason, cough Create cough, a block is removed (so air is placed) we don't want to prevent that.
        try {
            if (VampirismPlayerAttributes.get((Player) event.getEntity()).getVampSpecial().isCannotInteract()) {
                event.setCanceled(true);

                //Workaround for https://github.com/MinecraftForge/MinecraftForge/issues/7609 or https://github.com/TeamLapen/Vampirism/issues/1021
                //Chest drops content when restoring snapshot
                if (event.getPlacedBlock().hasBlockEntity()) {
                    BlockEntity t = event.getLevel().getBlockEntity(event.getPos());
                    if (t instanceof Container) {
                        ((Container) t).clearContent();
                    }
                }

                if (event.getEntity() instanceof ServerPlayer) { //For some reason this event is only run serverside. Therefore, we have to make sure the client is notified about the not-placed block.
                    MinecraftServer server = event.getEntity().level().getServer();
                    if (server != null) {
                        server.getPlayerList().sendAllPlayerInfo((ServerPlayer) event.getEntity()); //Would probably suffice to just sent a SHeldItemChangePacket
                    }
                }
            }
            HunterPlayer.get((Player) event.getEntity()).breakDisguise();
        } catch (Exception e) {
            // Added try catch to prevent any exception in case some other mod uses auto placers or so
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.@NotNull BreakSpeed event) {
        if (VampirismPlayerAttributes.get(event.getEntity()).getVampSpecial().isCannotInteract()) {
            event.setCanceled(true);
        } else if ((ModBlocks.GARLIC_DIFFUSER_NORMAL.get() == event.getState().getBlock() || ModBlocks.GARLIC_DIFFUSER_WEAK.get() == event.getState().getBlock() || ModBlocks.GARLIC_DIFFUSER_IMPROVED.get() == event.getState().getBlock()) && VampirismPlayerAttributes.get(event.getEntity()).vampireLevel > 0) {
            event.setNewSpeed(event.getOriginalSpeed() * 0.1F);
        }
    }

    @SubscribeEvent
    public void onItemPickupPre(@NotNull ItemEntityPickupEvent.Pre event) {
        if (VampirismPlayerAttributes.get(event.getPlayer()).getVampSpecial().isDBNO) {
            event.setCanPickup(TriState.FALSE);
        }
    }

    @SubscribeEvent
    public void onItemRightClick(PlayerInteractEvent.@NotNull RightClickItem event) {
        if (VampirismPlayerAttributes.get(event.getEntity()).getVampSpecial().isCannotInteract()) {
            event.setCancellationResult(InteractionResult.SUCCESS_SERVER);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemUse(LivingEntityUseItemEvent.@NotNull Start event) {
        if (event.getEntity() instanceof Player player) {
            if (VampirismPlayerAttributes.get(player).getVampSpecial().isCannotInteract()) {
                event.setCanceled(true);
            }
            if (event.getItem().getItem() instanceof HunterCrossbowItem && HunterPlayer.get(player).getSkillHandler().isSkillEnabled(HunterSkills.CROSSBOW_TECHNIQUE)) {
                event.setDuration((int) (event.getDuration() * 0.5f));
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockRightClicked(PlayerInteractEvent.RightClickBlock event) {
        if (Helper.isVampire(event.getEntity()) && VampirismPlayerAttributes.get(event.getEntity()).getVampSpecial().isCannotInteract()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemUse(LivingEntityUseItemEvent.@NotNull Finish event) {
        if (!Helper.isHunter(event.getEntity())) {
            ItemStack stack = event.getItem();
            if (stack.getItem() == Items.POTION) {
                PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                if (contents.potion().map(s -> s.value() instanceof BasePotion.HunterPotion).orElse(false) && StreamSupport.stream(contents.getAllEffects().spliterator(), false).map(MobEffectInstance::getEffect).map(Holder::value).anyMatch(MobEffect::isBeneficial)) {
                    event.getEntity().addEffect(new MobEffectInstance(ModEffects.POISON, Integer.MAX_VALUE, VampirismPoisonMobEffect.DEADLY_AMPLIFIER));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingAttack(@NotNull LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().isAlive() && !FactionPlayerHandler.get((Player) event.getEntity()).onEntityAttacked(event.getSource(), event.getAmount())) {
                event.setCanceled(true);
            }
        }
        if (event.getSource().getEntity() instanceof Player) {
            HunterPlayer.get((Player) event.getSource().getEntity()).breakDisguise();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDeathFirst(@NotNull LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (VampirePlayer.get(player).onDeadlyHit(event.getSource())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onLivingFall(@NotNull LivingFallEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setDistance(event.getDistance() - VampirismPlayerAttributes.get((Player) event.getEntity()).getVampSpecial().getJumpBoost());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingHurt(@NotNull LivingDamageEvent.Pre event) {
        DamageContainer d = event.getContainer();
        if (!d.getSource().is(DamageTypeTags.WITCH_RESISTANT_TO) && !d.getSource().is(DamageTypeTags.BYPASSES_ARMOR) && event.getEntity() instanceof Player) {
            if (VampirePlayer.get((Player) event.getEntity()).getSpecialAttributes().bat) {
                d.setNewDamage(event.getContainer().getNewDamage() * 2);
            }
        }

        // reduce damage dor vampires
        if (event.getEntity() instanceof Player player && Helper.isVampire(player)) {
            VampirePlayer vampire = VampirePlayer.get(player);
            float mod = (float) (0.2 * (float)vampire.getLevel()/ (float)vampire.getMaxLevel());
            d.setNewDamage(d.getNewDamage() * (1 - mod));
        }
    }

    @SubscribeEvent
    public void onLivingJump(LivingEvent.@NotNull LivingJumpEvent event) {
        if (event.getEntity() instanceof Player) {
            event.getEntity().setDeltaMovement(event.getEntity().getDeltaMovement().add(0.0D, (float) VampirismPlayerAttributes.get((Player) event.getEntity()).getVampSpecial().getJumpBoost() * 0.1F, 0.0D));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent.@NotNull RightClickBlock event) {

        //To replace glas bottles with blood bottles if interacting with a blood container
        //Also used to force block interaction with blood container if sneaking
        //DoesSneakByPassUse on the blood bottle is not enough, since the item in the offhand can still block
        if (event.getLevel().getWorldBorder().isWithinBounds(event.getPos())) {
            ItemStack heldStack = event.getItemStack();
            if (!heldStack.isEmpty() && heldStack.getCount() == 1) {
                boolean glassBottle = Items.GLASS_BOTTLE.equals(heldStack.getItem());
                boolean bloodBottle = ModItems.BLOOD_BOTTLE.get() == heldStack.getItem();
                if (bloodBottle || (glassBottle && ModConfig.COMMON.autoConvertGlassBottles.get())) {
                    Block block = event.getLevel().getBlockState(event.getPos()).getBlock();
                    BlockState state = event.getLevel().getBlockState(event.getPos());
                    boolean convert = false;
                    if (glassBottle && state.hasBlockEntity()) {
                        BlockEntity entity = event.getLevel().getBlockEntity(event.getPos());
                        if (entity != null) {
                            convert = Optional.ofNullable(event.getLevel().getCapability(Capabilities.Fluid.BLOCK, event.getPos(), state, entity, event.getFace())).map(fluidHandler -> {
                                boolean flag = false;
                                try (Transaction transaction = Transaction.openRoot()) {
                                    int drain = fluidHandler.extract(FluidResource.of(ModFluids.BLOOD.get()), 1000, transaction);
                                    if (drain >= IBottleBlood.MULTIPLIER) {
                                        flag = true;
                                        transaction.commit();
                                    }
                                }
                                return flag;
                            }).orElse(false);

                        }
                    }
                    if ((bloodBottle || convert) && block instanceof BloodContainerBlock) {
                        event.setUseBlock(TriState.TRUE);
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
    public void onPlayerLeftClickedBlock(PlayerInteractEvent.@NotNull LeftClickBlock event) {
        if (event.getFace() == null) return;
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() == ModBlocks.ALCHEMICAL_FIRE.get()) {
            BlockPos pos1 = event.getPos().relative(event.getFace());
            BlockState state1 = world.getBlockState(pos);
            world.levelEvent(null, 1009, pos, 0);
            world.removeBlock(pos, false);
            event.setCanceled(true);
        } else if ((ModBlocks.GARLIC_DIFFUSER_NORMAL.get() == state.getBlock() || ModBlocks.GARLIC_DIFFUSER_WEAK.get() == state.getBlock() || ModBlocks.GARLIC_DIFFUSER_IMPROVED.get() == state.getBlock()) && Helper.isVampire(event.getEntity())) {
            event.getEntity().addEffect(new MobEffectInstance(ModEffects.GARLIC));
        } else if (state.getBlock() instanceof MotherBlock) {
            //BlockEntity blockEntity = event.getEntity().level().getBlockEntity(pos);
            //if (blockEntity instanceof MotherBlockEntity mother && !mother.isCanBeBroken()) {
            //    event.setUseItem(Event.Result.DENY);
            //}
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        Level level = event.getEntity().level();

        if (!level.isClientSide() && event.getEntity() instanceof ServerPlayer serverPlayer) {
            ModAdvancements.TRIGGER_MAP_FOUND.get().trigger(serverPlayer, serverPlayer.getInventory());
        }
    }

    @SubscribeEvent
    public void sleepTimeCheck(@NotNull CanPlayerSleepEvent event) {
        if (Helper.isVampire(event.getEntity()) && event.getState().getBlock() instanceof CoffinBlock) {
            //This complete overwrites sleep check logic from net.minecraft.server.level.ServerPlayer.startSleepInBed
            //Vanilla checks for several things in order. We only want to overwrite the last two things: NOT_POSSIBLE_NOW and NOT_SAFE.
            boolean day = Helper.isDay(event.getLevel());
            if (!day && event.getProblem() == null) {
                //If everything is fine, but it is night, we change it to NOT POSSIBLE NOW
                event.setProblem(Player.BedSleepingProblem.NOT_POSSIBLE_NOW);
                return;
            }
            if (day && (event.getProblem() == Player.BedSleepingProblem.NOT_POSSIBLE_NOW || event.getProblem() == Player.BedSleepingProblem.NOT_SAFE)) {
                //If vanilla comes up with NOT_POSSIBLE_NOW or NOT_SAFE, it means all other conditions are met.
                //Respawn position is already set by vanilla at this point in code
                if (!event.getEntity().isCreative()) {
                    //Perform vanilla style check for monsters nearby, but ignore monsters that don't attack vampires
                    Vec3 vec3 = Vec3.atBottomCenterOf(event.getPos());
                    List<Monster> list = event.getLevel().getEntitiesOfClass(Monster.class, new AABB(vec3.x() - 8.0, vec3.y() - 5.0, vec3.z() - 8.0, vec3.x() + 8.0, vec3.y() + 5.0, vec3.z() + 8.0));
                    boolean zombie = ModConfig.BALANCE.zombieIgnoreVampire.get();
                    boolean skeleton = ModConfig.BALANCE.skeletonIgnoreVampire.get();
                    boolean creeper = ModConfig.BALANCE.creeperIgnoreVampire.get();
                    if (list.stream().anyMatch(monster ->
                            {
                                if (zombie && monster instanceof Zombie) return false;
                                if (skeleton && (monster instanceof Skeleton || monster instanceof Stray)) return false;
                                if (creeper && (monster instanceof Creeper)) return false;
                                return monster.isPreventingPlayerRest(event.getEntity().level(), event.getEntity());
                            }
                    )) {
                        event.setProblem(Player.BedSleepingProblem.NOT_SAFE);
                        return;
                    }
                }

                event.setProblem(null);

            }
        }
    }

    @SubscribeEvent
    public void canContinueToSleep(CanContinueSleepingEvent event) {
        if (Helper.isVampire(event.getEntity()) && event.getEntity().getSleepingPos().map(s -> event.getEntity().level().getBlockState(s)).map(s -> s.getBlock() instanceof CoffinBlock).orElse(false)) {
            boolean day = Helper.isDay(event.getEntity().level());
            if (day && event.getProblem() == Player.BedSleepingProblem.NOT_POSSIBLE_NOW) {
                event.setContinueSleeping(true);
            } else if (!day) {
                event.setContinueSleeping(false);
            }
        }
    }

    @SubscribeEvent
    public void sleepTimeFinish(@NotNull SleepFinishedTimeEvent event) {
        if (event.getLevel() instanceof ServerLevel && Helper.isDay(event.getLevel())) {
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

    private boolean checkExceptions(@NotNull Player player, Holder<? extends IPlayableFaction<?>> currentFaction, @NotNull ItemStack stack) { // stupid implementation. Otherwise we would need a better IFactionExclusiveItem#getExclusiveFaction method.
        return !stack.is(ModItems.GARLIC_BREAD) || currentFaction != null;
    }


    @SubscribeEvent
    public void onPlayerAttackCritical(@NotNull CriticalHitEvent event) {
        ItemStack stack = event.getEntity().getMainHandItem();
        if (!stack.isEmpty()) {
            FactionSlayer factionSlayer = stack.get(FactionDataComponents.FACTION_SLAYER);
            Holder<? extends IFaction<?>> faction = VampirismAPI.factionRegistry().getFaction(event.getTarget());
            if (factionSlayer != null && IFaction.contains(factionSlayer.slayedFactions(), faction)) {
                event.setDamageMultiplier(event.getDamageMultiplier() + (event.getVanillaMultiplier() * (factionSlayer.multiplier() - 1)));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerGameMode(PlayerEvent.PlayerChangeGameModeEvent event) {
        if (event.getNewGameMode() == GameType.SPECTATOR) {
            FactionPlayerHandler.get(event.getEntity()).getActionHandler().ifPresent(IActionHandler::deactivateAllActions);
        }
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        FactionPlayerHandler.get(event.getEntity()).checkSkillTreeLocks();
    }

    @SubscribeEvent
    public void joinLevelEvent(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            LevelGarlic.get(event.getLevel()).updatePlayer(player);
            LevelFog.get(event.getLevel()).updatePlayer(player);
        }
    }

    @SubscribeEvent
    private void onDataPackSyncEvent(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) {
            ((SundamageRegistry) VampirismAPI.sundamageRegistry()).updateClient(event.getPlayer());
        } else {
            event.getPlayerList().getPlayers().forEach(player -> ((SundamageRegistry) VampirismAPI.sundamageRegistry()).updateClient(player));
        }
    }

}
