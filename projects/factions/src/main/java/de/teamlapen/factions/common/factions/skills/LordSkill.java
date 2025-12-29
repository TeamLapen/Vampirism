package de.teamlapen.factions.common.factions.skills;

import com.mojang.datafixers.util.Either;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.skills.ISkillPlayer;
import de.teamlapen.factions.api.factions.skills.ISkillTree;
import de.teamlapen.factions.api.world.entities.player.IFactionPlayer;
import de.teamlapen.factions.common.tags.FactionSkillTreeTags;
import de.teamlapen.factions.common.tags.FactionTags;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * Use a better implementation when using custom lord skills
 * @param <T>
 */
@ApiStatus.Internal
public class LordSkill<T extends IFactionPlayer<T> & ISkillPlayer<T>> extends DefaultSkill<T> {

    private final Either<ResourceKey<ISkillTree>, TagKey<ISkillTree>> allowedSkillTrees = Either.right(FactionSkillTreeTags.LORD);

    public LordSkill(@Range(from = 0, to = 9) int skillPointCost) {
        super(skillPointCost);
    }

    @Override
    public @Nullable Component getDescription() {
        return Component.translatable(getDescriptionId() + ".desc");
    }

    @Override
    public TagKey<? extends IFaction<?>> factions() {
        return FactionTags.HAS_LORD_SKILLS;
    }

    @Override
    public Either<ResourceKey<ISkillTree>, TagKey<ISkillTree>> allowedSkillTrees() {
        return this.allowedSkillTrees;
    }
}
