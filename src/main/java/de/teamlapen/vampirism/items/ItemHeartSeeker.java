package de.teamlapen.vampirism.items;

import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
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
            float trained = getAttackSpeedModifier(stack);
            trained += (1.0f - trained) / 15f;
            setAttackSpeedModifier(stack, trained);
        }
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (isSelected && worldIn.isRemote && entityIn.ticksExisted % 16 == 7 && getAttackDamageModifier(stack) > 0.95f && entityIn instanceof EntityLivingBase) {
            spawnParticle((EntityLivingBase) entityIn);
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnParticle(EntityLivingBase player) {
        boolean firstPerson = player instanceof EntityPlayer && ((EntityPlayer) player).isUser() && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0;
        if (player instanceof EntityPlayer && player.getSwingProgress(1f) > 0.0) return;
        for (int j = 0; j < 3; ++j) {
            Vec3d dir = firstPerson ? player.getForward() : Vec3d.fromPitchYawVector(new Vec2f(player.rotationPitch, player.renderYawOffset));
            dir = dir.rotateYaw((float) (-Math.PI / 5f)).scale(0.75f).addVector((player.getRNG().nextFloat() - 0.5f) * 0.1f, (player.getRNG().nextFloat() - 0.3f) * 0.9f, (player.getRNG().nextFloat() - 0.5f) * 0.1f);
            Vec3d pos = dir.addVector(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            VampLib.proxy.getParticleHandler().spawnParticle(player.getEntityWorld(), ModParticles.FLYING_BLOOD, pos.x, pos.y, pos.z, pos.x + (player.getRNG().nextFloat() - 0.5D) * 0.2D, pos.y + (player.getRNG().nextFloat() - 0.5D) * 0.2D, pos.z + (player.getRNG().nextFloat() - 0.5D) * 0.2D, (int) (4.0F / (player.getRNG().nextFloat() * 0.9F + 0.1F)));
        }
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.NONE;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 40;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        super.onUsingTick(stack, player, count);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        float damage = getAttackDamageModifier(stack);
        setAttackDamageModifier(stack, Math.min(1.0f, damage + 0.1f));
        return stack;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        VampirePlayer vampire = VampirePlayer.get(playerIn);
        if (vampire.getLevel() == 0) return new ActionResult<>(EnumActionResult.PASS, stack);


        if (true) {
            playerIn.setActiveHand(handIn);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<>(EnumActionResult.PASS, stack);
    }
}
