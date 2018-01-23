package de.teamlapen.vampirism.items;

import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.network.ModGuiHandler;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemHeartSeeker extends VampirismVampireSword implements IItemWithTierNBTImpl {

    public static final String regName = "heart_seeker";
    private final static float[] DAMAGE_TIER = {5.0F, 6.0F, 8.0F};
    private final static float[] SPEED_TIER = {0.4f, 0.5f, 0.6f};

    public ItemHeartSeeker() {
        super(regName, ToolMaterial.IRON, 0.0f, 0.0f);
    }

    @Override
    protected float getBaseAttackDamage(ItemStack stack) {
        return DAMAGE_TIER[getTier(stack).ordinal()];
    }

    @Override
    protected float getBaseAttackSpeed(ItemStack stack) {
        return SPEED_TIER[getTier(stack).ordinal()];
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        for (TIER t : TIER.values()) {
            subItems.add(setTier(new ItemStack(itemIn), t));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void addInformation(ItemStack stack, @Nullable EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        addTierInformation(stack, tooltip);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (target.getHealth() <= 0.0f && Helper.isVampire(attacker)) {
            float trained = getTrained(stack, attacker);
            int exp = target instanceof EntityPlayer ? 10 : (attacker instanceof EntityPlayer ? (Helper.getExperiencePoints(target, (EntityPlayer) attacker)) : 5);
            trained += exp / 5f * (1.0f - trained) / 15f;
            setTrained(stack, attacker, trained);


        }
        float charged = getCharged(stack);
        charged -= 0.01f;
        setCharged(stack, charged);
        attacker.setHeldItem(EnumHand.MAIN_HAND, stack);
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (isSelected && worldIn.isRemote && entityIn.ticksExisted % 16 == 7 && getCharged(stack) > 0.95f && entityIn instanceof EntityLivingBase) {
            spawnChargedParticle((EntityLivingBase) entityIn);
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnChargedParticle(EntityLivingBase player) {
        boolean firstPerson = player instanceof EntityPlayer && ((EntityPlayer) player).isUser() && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0;
        if (player instanceof EntityPlayer && player.getSwingProgress(1f) > 0.0) return;
        for (int j = 0; j < 3; ++j) {
            Vec3d dir = firstPerson ? player.getForward() : Vec3d.fromPitchYawVector(new Vec2f(player.rotationPitch, player.renderYawOffset));
            dir = dir.rotateYaw((float) (-Math.PI / 5f)).scale(0.75f).addVector((player.getRNG().nextFloat() - 0.5f) * 0.1f, (player.getRNG().nextFloat() - 0.3f) * 0.9f, (player.getRNG().nextFloat() - 0.5f) * 0.1f);
            Vec3d pos = dir.addVector(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            VampLib.proxy.getParticleHandler().spawnParticle(player.getEntityWorld(), ModParticles.FLYING_BLOOD, pos.x, pos.y, pos.z, pos.x + (player.getRNG().nextFloat() - 0.5D) * 0.2D, pos.y + (player.getRNG().nextFloat() - 0.5D) * 0.2D, pos.z + (player.getRNG().nextFloat() - 0.5D) * 0.2D, (int) (4.0F / (player.getRNG().nextFloat() * 0.9F + 0.1F)), 177);
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnChargingParticle(EntityLivingBase player) {
        boolean firstPerson = player instanceof EntityPlayer && ((EntityPlayer) player).isUser() && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0;
        if (player instanceof EntityPlayer && player.getSwingProgress(1f) > 0.0) return;
        Vec3d dir = firstPerson ? player.getForward() : Vec3d.fromPitchYawVector(new Vec2f(player.rotationPitch, player.renderYawOffset));
        dir = dir.rotateYaw((float) (-Math.PI / 5f)).scale(0.75f).addVector((player.getRNG().nextFloat() - 0.5f) * 0.1f, (player.getRNG().nextFloat() - 0.3f) * 0.9f, (player.getRNG().nextFloat() - 0.5f) * 0.1f);
        Vec3d playerPos = new Vec3d((player).posX, (player).posY + player.getEyeHeight() - 0.2f, (player).posZ);
        Vec3d pos = dir.addVector(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        VampLib.proxy.getParticleHandler().spawnParticle(player.getEntityWorld(), ModParticles.FLYING_BLOOD, playerPos.x, playerPos.y, playerPos.z, pos.x, pos.y, pos.z, (int) (4.0F / (player.getRNG().nextFloat() * 0.6F + 0.1F)));

    }


    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 80;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        super.onUsingTick(stack, player, count);
        if (player.getEntityWorld().isRemote) {

            if (count % 3 == 0) {
                spawnChargingParticle(player);
            }
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        if (!(entityLiving instanceof EntityPlayer)) return stack;
        IVampirePlayer vampire = VReference.VAMPIRE_FACTION.getPlayerCapability((EntityPlayer) entityLiving);
        if (vampire.getBloodStats().consumeBlood(5)) {
            setCharged(stack, getCharged(stack) + 0.34f);

        }
        if (!stack.hasDisplayName() && getCharged(stack) == 1) {
            ((EntityPlayer) entityLiving).openGui(VampirismMod.instance, ModGuiHandler.ID_NAME_SWORD, worldIn, (int) entityLiving.posX, (int) entityLiving.posY, (int) entityLiving.posZ);
            ((EntityPlayer) entityLiving).world.playSound(((EntityPlayer) entityLiving).posX, ((EntityPlayer) entityLiving).posY, ((EntityPlayer) entityLiving).posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1f, 1f, false);
        }
        return stack;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        VampirePlayer vampire = VampirePlayer.get(playerIn);
        if (vampire.getLevel() == 0) return new ActionResult<>(EnumActionResult.PASS, stack);


        if (getCharged(stack) < 1 && vampire.getBloodLevel() >= 5) {
            playerIn.setActiveHand(handIn);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
