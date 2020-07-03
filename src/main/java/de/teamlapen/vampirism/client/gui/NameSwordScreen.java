package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import de.teamlapen.vampirism.network.InputEventPacket;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class NameSwordScreen extends Screen {
    /**
     * Suggested sword names.
     * Credits to http://www.fantasynamegenerators.com
     */
    private final static String[] sword_names = new String[]{"Felthorn", "Aetherius", "Agatha", "Alpha", "Amnesia", "Anduril", "Apocalypse", "Armageddon", "Arondite", "Ashrune", "Betrayal", "Betrayer", "Blackout", "Blazefury", "Blazeguard", "Blinkstrike", "Bloodquench", "Bloodweep", "Brutality", "Celeste", "Chaos", "Cometfell", "Convergence", "Darkheart", "Dawn", "Dawnbreaker", "Deathbringer", "Deathraze", "Decimation", "Desolation", "Destiny's Song", "Dirge", "Doomblade", "Doombringer", "Draughtbane", "Due Diligence", "Echo", "Eclipse", "Endbringer", "Epilogue", "Espada", "Extinction", "Faithkeeper", "Fate", "Fleshrender", "Florance", "Frenzy", "Fury", "Ghost Reaver", "Ghostwalker", "Gladius", "Glimmer", "Godslayer", "Grasscutter", "Gutrender", "Hatred's Bite", "Heartseeker", "Heartstriker", "Hell's Scream", "Hellfire", "Piece Maker", "Hellreaver", "Honor's Call", "Hope's End", "Infamy", "Interrogator", "Justifier", "Kinslayer", "Klinge", "Knightfall", "Lament", "Lazarus", "Lifedrinker", "Light's Bane", "Lightbane", "Lightbringer", "Lightning", "Limbo", "Loyalty", "Malice", "Mangler", "Massacre", "Mercy", "Misery", "Mournblade", "Narcoleptic", "Needle", "Nethersbane", "Night's Edge", "Night's Fall", "Nightbane", "Nightcrackle", "Nightfall", "Nirvana", "Oathbreaker", "Oathkeeper", "Oblivion", "Omega", "Orenmir", "Peacekeeper", "Persuasion", "Prick", "Purifier", "Rage", "Ragespike", "Ragnarok", "Reckoning", "Reign", "Remorse", "Requiem", "Retirement", "Rigormortis", "Savagery", "Scalpel", "Scar", "Seethe", "Severance", "Shadow Strike", "Shadowsteel", "Silence", "Silencer", "Silver Saber", "Silverlight", "Skullcrusher", "Slice of Life", "Soul Reaper", "Soulblade", "Soulrapier", "Spada", "Spike", "Spineripper", "Spiteblade", "Stalker", "Starshatterer", "Sting", "Stinger", "Storm", "Storm Breaker", "Stormbringer", "Stormcaller", "Story-Weaver", "Striker", "Sun Strike", "Suspension", "Swan Song", "The Ambassador", "The Black Blade", "The End", "The Facelifter", "The Light", "The Oculus", "The Stake", "The Untamed", "The Unyielding", "The Void", "Thorn", "Thunder", "Toothpick", "Tranquility", "Treachery", "Trinity", "Tyrhung", "Unending Tyranny", "Unholy Might", "Valkyrie", "Vanquisher", "Vengeance", "Venom", "Venomshank", "Warmonger", "Widow Maker", "Willbreaker", "Winterthorn", "Wit's End", "Witherbrand", "Wolf", "Worldbreaker", "Worldslayer"};
    private final ITextComponent yes;
    private final ITextComponent no;
    private final List<ITextProperties> listLines = new ArrayList<>();
    private final ITextComponent text1;
    private final ITextComponent text2;
    private final ItemStack sword;
    private TextFieldWidget nameField;

    public NameSwordScreen(ItemStack sword) {
        super(new TranslationTextComponent("gui.vampirism.name_sword.title"));
        this.yes = new TranslationTextComponent("gui.yes");
        this.no = new TranslationTextComponent("gui.no");
        this.text1 = new TranslationTextComponent("gui.vampirism.name_sword.title");
        this.text2 = new TranslationTextComponent("gui.vampirism.name_sword.text");
        this.sword = sword;
    }

    @Override
    public void func_230430_a_(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.func_230446_a_(stack);
        this.func_238472_a_(stack, this.field_230712_o_, this.text1, this.field_230708_k_ / 2, 70, 16777215);
        int i = 90;
        for (ITextProperties s : this.listLines) {
            this.func_238472_a_/*drawCenteredString*/(stack, this.field_230712_o_, s, this.field_230708_k_ / 2, i, 16777215);
            i += this.field_230712_o_.FONT_HEIGHT;
        }
        this.nameField.func_230430_a_(stack, mouseX, mouseY, partialTicks);


        super.func_230430_a_(stack, mouseX, mouseY, partialTicks);
        RenderSystem.disableLighting();
        RenderSystem.disableBlend();
    }

    @Override
    public void func_231023_e_() {
        nameField.tick();
    }

    @Override
    public void func_231152_a_(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) { //resize
        String text = nameField.getText();
        super.func_231152_a_(p_resize_1_, p_resize_2_, p_resize_3_); //Text gets deleted as this calls init again
        nameField.setText(text);
    }

    @Override
    public void func_231160_c_() {
        super.func_231160_c_();
        this.func_230480_a_(new OptionButton(this.field_230708_k_ / 2 - 155, this.field_230709_l_ / 6 + 96, 150, 20, AbstractOption.AO, this.yes, (context) -> {
            if (!StringUtils.isBlank(nameField.getText())) {
                NameSwordScreen.this.sword.setDisplayName(new StringTextComponent(nameField.getText()));
                VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.NAME_ITEM, nameField.getText()));
            }
            this.field_230706_i_.displayGuiScreen(null);
            this.field_230706_i_.setGameFocused(true);
        }));
        this.func_230480_a_(new OptionButton(this.field_230708_k_ / 2 - 155 + 160, this.field_230709_l_ / 6 + 96, 150, 20, AbstractOption.AO, this.no, (context) -> {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.NAME_ITEM, VampirismVampireSword.DO_NOT_NAME_STRING));
            this.field_230706_i_.displayGuiScreen(null);
            this.field_230706_i_.setGameFocused(true);
        }));

        this.listLines.clear();
        this.listLines.addAll(this.field_230712_o_.func_238425_b_(this.text2, this.field_230708_k_ - 50));

        this.nameField = new TextFieldWidget(this.field_230712_o_, this.field_230708_k_ / 2 - 155 + 77, this.field_230709_l_ / 6 + 70, 155, 20, new StringTextComponent("text_sword"));
        this.nameField.setTextColor(-1);
        this.nameField.setDisabledTextColour(-1);
        this.nameField.setEnableBackgroundDrawing(true);
        this.nameField.setMaxStringLength(35);
        this.nameField.setText(sword_names[new Random().nextInt(sword_names.length)]);
        this.field_230705_e_.add(nameField);
        this.func_231035_a_(nameField); //setFocused
    }
}
