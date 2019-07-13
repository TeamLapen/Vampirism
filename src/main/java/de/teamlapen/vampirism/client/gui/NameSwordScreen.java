package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import de.teamlapen.vampirism.network.InputEventPacket;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.item.ItemStack;
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
    private final String yes;
    private final String no;
    private final List<String> listLines = new ArrayList<>();
    private final String text1;
    private final String text2;
    private final ItemStack sword;
    private TextFieldWidget nameField;

    public NameSwordScreen(ItemStack sword) {
        super(new TranslationTextComponent("name_Sword"));//TODO 1.14 name
        this.yes = UtilLib.translate("gui.yes");
        this.no = UtilLib.translate("gui.no");
        this.text1 = UtilLib.translate("gui.vampirism.name_sword.title");
        this.text2 = UtilLib.translate("gui.vampirism.name_sword.text");
        this.sword = sword;
    }

    @Override
    public void renderBackground() {
        super.renderBackground();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        //this.drawDefaultBackground();TODO 1.14 needed?
        this.drawCenteredString(this.font, this.text1, this.width / 2, 70, 16777215);
        int i = 90;
        for (String s : this.listLines) {
            this.drawCenteredString(this.font, s, this.width / 2, i, 16777215);
            i += this.font.FONT_HEIGHT;
        }

        super.render(mouseX, mouseY, partialTicks);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
    }

    @Override
    public void init() {
        super.init();
        this.buttons.add(new OptionButton(this.width / 2 - 155, this.height / 6 + 96, 150, 20, AbstractOption.AO, this.yes, (context) -> {//TODO 1.14 size & Abstract option
            if (!StringUtils.isBlank(nameField.getText())) {
                NameSwordScreen.this.sword.setDisplayName(new StringTextComponent(nameField.getText()));
                VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.NAME_ITEM, nameField.getText()));
            }
            NameSwordScreen.this.onClose();
        }));
        this.buttons.add(new OptionButton(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, AbstractOption.AO, this.no, (context) -> {//TODO 1.14 size & Abstract option
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.NAME_ITEM, VampirismVampireSword.DO_NOT_NAME_STRING));
            NameSwordScreen.this.onClose();
        }));
        this.nameField = new TextFieldWidget(this.font, this.width / 2 - 155 + 77, this.height / 6 + 70, 155, 12, "namesword.screen");//TODO 1.14 name
        this.nameField.setTextColor(-1);
        this.nameField.setDisabledTextColour(-1);
        this.nameField.setEnableBackgroundDrawing(true);
        this.nameField.setMaxStringLength(35);
        this.listLines.clear();
        this.listLines.addAll(this.font.listFormattedStringToWidth(this.text2, this.width - 50));
        this.nameField.setText(sword_names[new Random().nextInt(sword_names.length)]);
        this.buttons.add(nameField);
    }

    @Override
    public boolean keyPressed(int key1, int key2, int key3) {
        boolean retur = this.nameField.keyPressed(key1, key2, key3);
        if (super.keyPressed(key1, key2, key3))
            return true;
        return retur;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean retur = super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.nameField.mouseClicked(mouseX, mouseY, mouseButton))
            return true;
        return retur;
    }
}
