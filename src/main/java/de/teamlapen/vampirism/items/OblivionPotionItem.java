package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.storage.Attachment;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.player.skills.SkillHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;

import java.util.List;

public class OblivionPotionItem extends Item {

    public OblivionPotionItem(Properties properties) {
        super(properties);
    }

    public static <T extends IFactionPlayer<T> & ISkillPlayer<T>> void applyEffect(T factionPlayer) {
        Player player = factionPlayer.asEntity();
        ISkillHandler<?> skillHandler = factionPlayer.getSkillHandler();
        if (((SkillHandler<?>) skillHandler).noSkillEnabled()) {
            return;
        }
        boolean test = VampirismMod.inDev || REFERENCE.VERSION.isTestVersion();
        player.addEffect(new MobEffectInstance(ModEffects.OBLIVION, Integer.MAX_VALUE, test ? 100 : 4));
        if (factionPlayer instanceof Attachment syncable) {
            syncable.sync();
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("text.vampirism.oblivion_potion.resets_skills").withStyle(ChatFormatting.GRAY));
    }
}
