package de.teamlapen.vampirism.entity;

import com.google.common.base.Predicate;
import de.teamlapen.lib.lib.util.ItemStackUtil;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.minions.IMinionLordWithSaveable;
import de.teamlapen.vampirism.api.items.IFactionSlayerItem;
import de.teamlapen.vampirism.blocks.BlockCastleBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.inventory.BloodPotionTableContainer;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.potion.FakeNightVisionPotion;
import de.teamlapen.vampirism.util.DifficultyCalculator;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

/**
 * Event handler for all entity related events
 */
public class ModEntityEventHandler {

    private boolean skipAttackDamageOnce = false;

    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent.Entity event) {
        if (event.getEntity() instanceof EntityCreature) {
            event.addCapability(REFERENCE.EXTENDED_CREATURE_KEY, ExtendedCreature.createNewCapability((EntityCreature) event.getEntity()));
        }
    }

    @SubscribeEvent
    public void onEntityAttacked(LivingAttackEvent event) {
        //Probably not a very "clean" solution, but the only one I found
        if (!skipAttackDamageOnce && "player".equals(event.getSource().getDamageType()) && event.getSource().getEntity() instanceof EntityPlayer) {
            ItemStack stack = ((EntityPlayer) event.getSource().getEntity()).getHeldItemMainhand();
            if (!ItemStackUtil.isEmpty(stack) && stack.getItem() instanceof IFactionSlayerItem) {
                IFactionSlayerItem item = (IFactionSlayerItem) stack.getItem();
                IFaction faction = VampirismAPI.factionRegistry().getFaction(event.getEntity());

                if (faction != null && faction.equals(item.getSlayedFaction())) {
                    float amt = event.getAmount() * item.getDamageMultiplierForFaction(stack);
                    skipAttackDamageOnce = true;
                    boolean result = net.minecraftforge.common.ForgeHooks.onLivingAttack(event.getEntityLiving(), event.getSource(), amt);
                    skipAttackDamageOnce = false;
                    event.setCanceled(!result);
                }
            }
        }


    }

    @SubscribeEvent
    public void onEntityCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        IBlockState blockState = event.getWorld().getBlockState(new BlockPos(event.getX() - 0.4F, event.getY(), event.getZ() - 0.4F).down());
        if (blockState.getBlock().equals(ModBlocks.castleBlock)) {
            if (BlockCastleBlock.EnumType.DARK_STONE.equals(blockState.getValue(BlockCastleBlock.VARIANT)) || !event.getEntity().isCreatureType(VReference.VAMPIRE_CREATURE_TYPE, false)) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote && (event.getEntity() instanceof IAdjustableLevel)) {
            IAdjustableLevel entity = (IAdjustableLevel) event.getEntity();
            if (entity.getLevel() == -1) {
                Difficulty d = DifficultyCalculator.findDifficultyForPos(event.getWorld(), event.getEntity().getPosition(), 30);
                int l = entity.suggestLevel(d);
                if (l > entity.getMaxLevel()) {
                    l = entity.getMaxLevel();
                } else if (l < 0) {
                    event.setCanceled(true);
                }
                entity.setLevel(l);
                if (entity instanceof EntityCreature) {
                    ((EntityCreature) entity).setHealth(((EntityCreature) entity).getMaxHealth());
                }
            }
        }
        if (event.getEntity() instanceof EntityCreeper) {
            ((EntityCreeper) event.getEntity()).tasks.addTask(3, new EntityAIAvoidEntity<>((EntityCreeper) event.getEntity(), EntityPlayer.class, new Predicate<EntityPlayer>() {
                @Override
                public boolean apply(@Nullable EntityPlayer input) {
                    return VampirePlayer.get(input).getSpecialAttributes().avoided_by_creepers;
                }
            }, 15, 1.1, 1.3));
        }
        if (event.getEntity() instanceof IMinionLordWithSaveable) {
            ((IMinionLordWithSaveable) event.getEntity()).getSaveableMinionHandler().addLoadedMinions();
        }
    }

    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() instanceof EntityCreature) {
            event.getEntity().getEntityWorld().theProfiler.startSection("vampirism_extended_creature");
            ExtendedCreature.get((EntityCreature) event.getEntity()).onUpdate();
            event.getEntity().getEntityWorld().theProfiler.endSection();

        } else if (!event.getEntity().getEntityWorld().isRemote && event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            if (player.openContainer instanceof BloodPotionTableContainer) {
                ((BloodPotionTableContainer) player.openContainer).tick();
            }
        }
        PotionEffect vanillaNightVision = null;
        if (FakeNightVisionPotion.vanillaInstance != null && (vanillaNightVision = event.getEntityLiving().getActivePotionEffect(FakeNightVisionPotion.vanillaInstance)) != null) {
            event.getEntityLiving().removePotionEffect(FakeNightVisionPotion.vanillaInstance);
            event.getEntityLiving().addPotionEffect(new PotionEffect(ModPotions.fakeNightVisionPotion, vanillaNightVision.getDuration(), vanillaNightVision.getAmplifier(), vanillaNightVision.getIsAmbient(), vanillaNightVision.doesShowParticles()));
            VampirismMod.log.d("EntityEventHandler", "Replacing vanilla night vision potion effect by modified potion effect");
        }


    }
}
