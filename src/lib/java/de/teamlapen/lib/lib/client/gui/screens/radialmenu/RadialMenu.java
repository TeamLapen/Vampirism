/*
 * Note: This code has been modified from David Quintana's solution.
 * Below is the required copyright notice.
 * Copyright (c) 2015, David Quintana <gigaherz@gmail.com>
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the author nor the
 *       names of the contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package de.teamlapen.lib.lib.client.gui.screens.radialmenu;

import com.mojang.blaze3d.vertex.PoseStack;

import java.util.List;
import java.util.function.IntConsumer;

public class RadialMenu<T> {
    private final IntConsumer setSelectedSlot;
    private final List<IRadialMenuSlot<T>> radialMenuSlots;
    private final boolean showMoreSecondaryItems;
    private final SecondaryIconPosition secondaryIconStartingPosition;
    private final DrawCallback<T> drawCallback;
    private final int offset;

    /**
     * Returns the basic SpellBook-Like Radial Menu configuration.
     * Only one secondary Icon is shown below the primary Icon.
     * Look at the Spellbook for an example on how to use the radial menu.
     *
     * @param setSelectedSlot Provide a callback that sets the selected Slot to the provided integer.
     *                        REMEMBER to also handle the Serverside tag-setting!
     * @param drawCallback    Provide a callback that handles the drawing of the radial menu Icons. Refer to the SpellBook for an example
     *                        GuiRadialMenuUtils provides methods to handle either drawing Items or drawing textures provided as ResourceLocations
     *                        YOU are responsible to provide a method that handles the objects provided in your RadialMenuSlots
     * @param offset          Additional offset amount for secondary icons. If your Icons don't above each other try around with this parameter
     */
    public RadialMenu(IntConsumer setSelectedSlot, List<IRadialMenuSlot<T>> radialMenuSlots, DrawCallback<T> drawCallback, int offset) {
        this.setSelectedSlot = setSelectedSlot;
        this.radialMenuSlots = radialMenuSlots;
        this.showMoreSecondaryItems = false;
        this.secondaryIconStartingPosition = SecondaryIconPosition.NORTH;
        this.drawCallback = drawCallback;
        this.offset = offset;
    }

    /**
     * Returns a Radial Menu configuration that displays up to 4 secondary Icons arranged around the primary Icon,
     * starting with the provided starting position and continuing counterclockwise
     * Look at the Spellbook for an example on how to use the radial menu.
     *
     * @param setSelectedSlot Provide a callback that sets the selected Slot to the provided integer. REMEMBER to also handle the Serverside tag-setting!
     * @param drawCallback    Provide a callback that handles the drawing of the radial menu Icons. Refer to the SpellBook for an example
     *                        GuiRadialMenuUtils provides methods to handle either drawing Items or drawing textures provided as ResourceLocations
     *                        YOU are responsible to provide a method that handles the objects provided in your RadialMenuSlots
     * @param offset          Additional offset amount for secondary icons. If your Icons don't above each other try around with this parameter
     */
    public RadialMenu(IntConsumer setSelectedSlot, List<IRadialMenuSlot<T>> radialMenuSlots, SecondaryIconPosition secondaryIconStartingPosition, DrawCallback<T> drawCallback, int offset) {
        this.setSelectedSlot = setSelectedSlot;
        this.radialMenuSlots = radialMenuSlots;
        this.showMoreSecondaryItems = true;
        this.secondaryIconStartingPosition = secondaryIconStartingPosition;
        this.drawCallback = drawCallback;
        this.offset = offset;
    }

    public List<IRadialMenuSlot<T>> getRadialMenuSlots() {
        return radialMenuSlots;
    }

    public void setCurrentSlot(int slot) {
        setSelectedSlot.accept(slot);
    }

    public boolean isShowMoreSecondaryItems() {
        return showMoreSecondaryItems;
    }

    public SecondaryIconPosition getSecondaryIconStartingPosition() {
        return this.secondaryIconStartingPosition;
    }

    public void drawIcon(T objectToBeDrawn, PoseStack poseStack, int positionX, int positionY, int size) {
        this.drawCallback.accept(objectToBeDrawn, poseStack, positionX, positionY, size, false);
    }

    public int getOffset() {
        return this.offset;
    }
}
