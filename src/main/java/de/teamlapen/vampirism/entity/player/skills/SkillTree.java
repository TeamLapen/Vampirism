package de.teamlapen.vampirism.entity.player.skills;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record SkillTree(@NotNull IPlayableFaction<?> faction, @NotNull EntityPredicate unlockPredicate, @NotNull ItemStack display, @NotNull Component name) implements ISkillTree {

    public static final Codec<ISkillTree> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(inst ->
            inst.group(
                    IPlayableFaction.CODEC.fieldOf("faction").forGetter(ISkillTree::faction),
                    EntityPredicate.CODEC.fieldOf("unlock_predicate").forGetter(ISkillTree::unlockPredicate),
                    ItemStack.CODEC.fieldOf("display").forGetter(ISkillTree::display),
                    ComponentSerialization.CODEC.fieldOf("name").forGetter(ISkillTree::name)
            ).apply(inst, SkillTree::new)
    ));

}
