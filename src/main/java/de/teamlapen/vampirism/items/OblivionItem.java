package de.teamlapen.vampirism.items;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.item.Item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OblivionItem extends Item {

    public static void applyEffect(IFactionPlayer<?> factionPlayer) {
        PlayerEntity player = factionPlayer.getRepresentingPlayer();
        FactionPlayerHandler.getOpt(player).ifPresent(fph -> {
            ISkillHandler<?> skillHandler = factionPlayer.getSkillHandler();
            if (((SkillHandler<?>) skillHandler).getRootNodes().stream().flatMap(a -> a.getChildren().stream()).flatMap(a -> Arrays.stream(a.getElements())).noneMatch(skillHandler::isSkillEnabled))
                return;
            boolean test = VampirismMod.inDev || VampirismMod.instance.getVersionInfo().getCurrentVersion().isTestVersion();
            player.addEffect(new EffectInstance(ModEffects.OBLIVION.get(), Integer.MAX_VALUE, test ? 100 : 4));
            if (factionPlayer instanceof ISyncable.ISyncableEntityCapabilityInst) {
                HelperLib.sync((ISyncable.ISyncableEntityCapabilityInst) factionPlayer, factionPlayer.getRepresentingPlayer(), false);
            }
        });

    }

    public OblivionItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent("text.vampirism.oblivion_potion.resets_skills").withStyle(TextFormatting.GRAY));
    }

    @Nonnull
    @Override
    public ItemStack finishUsingItem(ItemStack stack, @Nonnull World worldIn, LivingEntity entityLiving) {
        stack.shrink(1);
        if (entityLiving instanceof PlayerEntity) {
            FactionPlayerHandler.getOpt(((PlayerEntity) entityLiving)).map(FactionPlayerHandler::getCurrentFactionPlayer).orElse(Optional.empty()).ifPresent(OblivionItem::applyEffect);
        }
        if (entityLiving instanceof MinionEntity) {
            ((MinionEntity<?>) entityLiving).getMinionData().ifPresent(d -> d.upgradeStat(-1, (MinionEntity<?>) entityLiving));
        }
        return stack;
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack stack) {
        return 32;
    }

    @Nonnull
    @Override
    public UseAction getUseAnimation(@Nonnull ItemStack stack) {
        return UseAction.DRINK;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(@Nonnull World worldIn, @Nonnull PlayerEntity playerIn, @Nonnull Hand handIn) {
        return DrinkHelper.useDrink(worldIn, playerIn, handIn);
    }
}
