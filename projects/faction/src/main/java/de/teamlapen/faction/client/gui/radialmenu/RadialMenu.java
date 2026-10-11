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

package de.teamlapen.faction.client.gui.radialmenu;

import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.List;
import java.util.function.Consumer;

public class RadialMenu<T> {
    private final ISlotSelectionCallback<T> selectionCallback;
    private final List<IRadialMenuSlot<T>> radialMenuSlots;
    private final IEntryDrawable<T> drawCallback;

    /**
     * Radial menu configuration that handles the selection of primary slots and their secondary items.
     * Secondary items are displayed in an outer ring, attached to their primary slot.
     *
     * @param selectionCallback Called with the selected slot and the index of the selected secondary item, or {@code -1} if the primary item was selected
     * @param drawCallback      Provide a callback that handles the drawing of the radial menu Icons.
     *                          YOU are responsible to provide a method that handles the objects provided in your RadialMenuSlots
     */
    public RadialMenu(List<IRadialMenuSlot<T>> radialMenuSlots, IEntryDrawable<T> drawCallback, ISlotSelectionCallback<T> selectionCallback) {
        this.selectionCallback = selectionCallback;
        this.radialMenuSlots = radialMenuSlots;
        this.drawCallback = drawCallback;
    }

    public RadialMenu(List<IRadialMenuSlot<T>> radialMenuSlots, IEntryDrawable<T> drawCallback, Consumer<T> selectionCallback) {
        this(radialMenuSlots, drawCallback, (a, _) -> selectionCallback.accept(a));
    }

    public List<IRadialMenuSlot<T>> getRadialMenuSlots() {
        return this.radialMenuSlots;
    }

    /**
     * @param secondarySlot index into {@link IRadialMenuSlot#secondaryItems()} or {@code -1} for the primary item
     */
    protected void setCurrentSlot(int slot, int secondarySlot) {
        var slotItem = this.radialMenuSlots.get(slot);
        var secondarySlotItem = secondarySlot >= 0 ? slotItem.secondaryItems().get(secondarySlot).item() : null;

        var selectedItem = secondarySlotItem == null ? slotItem.item() : secondarySlotItem;

        this.selectionCallback.accept(selectedItem, selectedItem == slotItem.item() ? null : slotItem.item());
    }

    public void drawIcon(T objectToBeDrawn, GuiGraphicsExtractor graphics, int positionX, int positionY, int size) {
        this.drawCallback.accept(objectToBeDrawn, graphics, positionX, positionY, size, false);
    }

}
