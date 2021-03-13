package de.teamlapen.vampirism.items;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class OblivionItem extends VampirismItem {

    public static void applyEffect(Optional<? extends IFactionPlayer> factionPlayerOpt, @Nullable ItemStack stack) {
        factionPlayerOpt.ifPresent(factionPlayer -> {
            PlayerEntity player = factionPlayer.getRepresentingPlayer();
            FactionPlayerHandler.getOpt(player).ifPresent(fph -> {
                if (stack != null) {
                    stack.shrink(1);
                }else {
                    InventoryHelper.removeItemFromInventory(player.inventory, new ItemStack(ModItems.oblivion_potion));
                }
                ISkillHandler<?> skillHandler = factionPlayer.getSkillHandler();
                player.addPotionEffect(new EffectInstance(ModEffects.oblivion, Integer.MAX_VALUE, 5));
                if (!VampirismMod.inDev && !VampirismMod.instance.getVersionInfo().getCurrentVersion().isTestVersion()) {
                    int l = factionPlayer.getLevel();
                    int lordLevel = fph.getLordLevel();
                    if (lordLevel > 0) {
                        fph.setLordLevel(lordLevel - 1);
                    } else if (l > 1) {
                        fph.setFactionLevel(factionPlayer.getFaction(), l - 1);
                    }
                }
                if (factionPlayer instanceof ISyncable.ISyncableEntityCapabilityInst && skillHandler instanceof SkillHandler) {
                    HelperLib.sync((ISyncable.ISyncableEntityCapabilityInst) factionPlayer, factionPlayer.getRepresentingPlayer(), false);
                }
                player.sendStatusMessage(new TranslationTextComponent("text.vampirism.skill.skills_reset"), true);
            });

        });
    }

    public OblivionItem(String regName, Properties properties) {
        super(regName, properties.maxStackSize(1));
    }

    @Nonnull
    @Override
    public UseAction getUseAction(@Nonnull ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack stack) {
        return 32;
    }

    @Nonnull
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, @Nonnull World worldIn, LivingEntity entityLiving) {
        if (entityLiving instanceof PlayerEntity) {
            applyEffect(FactionPlayerHandler.getOpt(((PlayerEntity) entityLiving)).map(FactionPlayerHandler::getCurrentFactionPlayer).orElse(Optional.empty()), stack);
        }
        return stack;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, @Nonnull PlayerEntity playerIn, @Nonnull Hand handIn) {
        return DrinkHelper.startDrinking(worldIn, playerIn, handIn);
    }
}
