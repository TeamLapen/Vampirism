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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OblivionItem extends Item {

    public static void applyEffect(IFactionPlayer<?> factionPlayer) {
        Player player = factionPlayer.getRepresentingPlayer();
        FactionPlayerHandler.getOpt(player).ifPresent(fph -> {
            ISkillHandler<?> skillHandler = factionPlayer.getSkillHandler();
            if (((SkillHandler<?>) skillHandler).getRootNode().getChildren().stream().flatMap(a -> Arrays.stream(a.getElements())).noneMatch(skillHandler::isSkillEnabled))
                return;
            boolean test = VampirismMod.inDev || VampirismMod.instance.getVersionInfo().getCurrentVersion().isTestVersion();
            player.addEffect(new MobEffectInstance(ModEffects.OBLIVION.get(), Integer.MAX_VALUE, test ? 100 : 4));
            if (factionPlayer instanceof ISyncable.ISyncableEntityCapabilityInst) {
                HelperLib.sync((ISyncable.ISyncableEntityCapabilityInst) factionPlayer, factionPlayer.getRepresentingPlayer(), false);
            }
        });

    }

    public OblivionItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("text.vampirism.oblivion_potion.resets_skills").withStyle(ChatFormatting.GRAY));
    }

    @Nonnull
    @Override
    public ItemStack finishUsingItem(ItemStack stack, @Nonnull Level worldIn, @Nonnull LivingEntity entityLiving) {
        stack.shrink(1);
        if (entityLiving instanceof Player) {
            FactionPlayerHandler.getOpt(((Player) entityLiving)).map(FactionPlayerHandler::getCurrentFactionPlayer).orElseGet(Optional::empty).ifPresent(OblivionItem::applyEffect);
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
    public UseAnim getUseAnimation(@Nonnull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level worldIn, @Nonnull Player playerIn, @Nonnull InteractionHand handIn) {
        return ItemUtils.startUsingInstantly(worldIn, playerIn, handIn);
    }
}
