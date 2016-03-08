package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.util.SRGNAMES;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Method;
import java.util.UUID;


public class BatVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    public static final float BAT_HEIGHT = 0.8F;
    public final static float BAT_EYE_HEIGHT = 0.85F * BAT_HEIGHT;
    public final UUID healthModifierUUID = UUID.fromString("4392fccb-4bfd-4290-b2e6-5cc91429053c");
    private final float BAT_WIDTH = 0.5F;
    private final float PLAYER_WIDTH = 0.6F;
    private final float PLAYER_HEIGHT = 1.8F;

    public BatVampireAction() {
        super(null);
    }

    @Override
    public boolean canBeUsedBy(IVampirePlayer vampire) {
        return !vampire.isGettingSundamage() && !vampire.getActionHandler().isActionActive(VampireActions.rageAction);
    }

    @Override
    public int getCooldown() {
        return 1;
    }

    @Override
    public int getDuration(int level) {
        return Integer.MAX_VALUE - 1;
    }

    @Override
    public int getMinLevel() {
        return Balance.vps.BAT_MIN_LEVEL;
    }

    @Override
    public int getMinU() {
        return 64;
    }

    @Override
    public int getMinV() {
        return 0;
    }

    @Override
    public String getUnlocalizedName() {
        return "skill.vampirism.bat_skill";
    }

    @Override
    public boolean onActivated(IVampirePlayer vampire) {
        EntityPlayer player = vampire.getRepresentingPlayer();
        float oldMax = player.getMaxHealth();
        float oldHealth = player.getHealth();
        setModifier(player, true);
        float newMax = player.getMaxHealth();
        float mult = newMax / oldMax;
        float newHealth = mult * oldHealth;
        if (newHealth < 1) newHealth = 1;
        player.setHealth(newHealth);
        setPlayerBat(player, true);
        return true;
    }

    @Override
    public void onActivatedClient(IVampirePlayer vampire) {
        setPlayerBat(vampire.getRepresentingPlayer(), true);
    }

    @Override
    public void onDeactivated(IVampirePlayer vampire) {
        EntityPlayer player = vampire.getRepresentingPlayer();
        float oldMax = player.getMaxHealth();
        float oldHealth = player.getHealth();
        setModifier(player, false);
        float newMax = player.getMaxHealth();
        float mult = newMax / oldMax;
        float newHealth = mult * oldHealth;
        player.setHealth(newHealth);
        if (player.onGround) {
            player.addPotionEffect(new PotionEffect(Potion.resistance.id, 20, 100));
        }
        player.addPotionEffect(new PotionEffect(Potion.resistance.id, 60, 100));
        setPlayerBat(player, false);
    }

    @Override
    public void onReActivated(IVampirePlayer vampire) {
        setModifier(vampire.getRepresentingPlayer(), true);
        setPlayerBat(vampire.getRepresentingPlayer(), true);
    }

    @Override
    public boolean onUpdate(IVampirePlayer vampire) {
        if (vampire.isGettingSundamage() && !vampire.isRemote()) {
            vampire.getRepresentingPlayer().addChatMessage(new ChatComponentTranslation("text.vampirism.cant_fly_day"));
            return true;
        }
        return false;
    }

    private void setModifier(EntityPlayer player, boolean enabled) {
        if (enabled) {

            IAttributeInstance health = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
            if (health.getModifier(healthModifierUUID) == null) {
                health.applyModifier(new AttributeModifier(healthModifierUUID, "Bat Health Reduction", -0.9, 2).setSaved(false));
            }

            player.capabilities.allowFlying = true;
            player.capabilities.isFlying = true;
            player.sendPlayerAbilities();
        } else {

            // Health modifier
            IAttributeInstance health = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
            AttributeModifier m = health.getModifier(healthModifierUUID);
            if (m != null) {
                health.removeModifier(m);
            }

            if (!player.capabilities.isCreativeMode) {
                player.capabilities.allowFlying = false;
            }
            player.capabilities.isFlying = false;
            player.sendPlayerAbilities();
        }

    }

    /**
     * Adjust the players size and eye height to fit to the bat model
     *
     * @param player
     * @param bat
     */
    private void setPlayerBat(EntityPlayer player, boolean bat) {
        float width = bat ? BAT_WIDTH : PLAYER_WIDTH;
        float height = bat ? BAT_HEIGHT : PLAYER_HEIGHT;
        try {
            Method mSetSize = ReflectionHelper.findMethod(Entity.class, player, new String[]{"setSize", SRGNAMES.Entity_setSize}, float.class, float.class);
            mSetSize.invoke(player, width, height);
        } catch (ReflectiveOperationException e) {
            VampirismMod.log.e("BatAction", e, "Could not change players size! ");
            return;
        }
        player.setPosition(player.posX, player.posY + (bat ? 1F : 1F) * (PLAYER_HEIGHT - BAT_HEIGHT), player.posZ);
        //VampirismMod.log.t( BAT_EYE_HEIGHT+": p "+player.getDefaultEyeHeight()+ ": y "+player.getYOffset()+" :e1 "+player.eyeHeight);
        player.eyeHeight = (bat ? BAT_EYE_HEIGHT - (float) player.getYOffset() : player.getDefaultEyeHeight());// Different from Client side
        //VampirismMod.log.t("2"+ BAT_EYE_HEIGHT+": p "+player.getDefaultEyeHeight()+ ": y "+player.getYOffset()+" :e1 "+player.eyeHeight);
    }

}
