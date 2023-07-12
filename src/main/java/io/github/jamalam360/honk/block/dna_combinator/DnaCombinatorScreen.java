/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Jamalam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.github.jamalam360.honk.block.dna_combinator;

import io.github.jamalam360.honk.HonkInit;
import io.wispforest.owo.ui.base.BaseUIModelHandledScreen;
import io.wispforest.owo.ui.base.BaseUIModelScreen.DataSource;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.PositionedRectangle;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class DnaCombinatorScreen extends BaseUIModelHandledScreen<FlowLayout, DnaCombinatorScreenHandler> {

    private TextureComponent fuelIndicator;
    private TextureComponent progressIndicator;

    public DnaCombinatorScreen(DnaCombinatorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, FlowLayout.class, DataSource.asset(HonkInit.idOf("dna_combinator")));
        this.backgroundWidth = 176;
        this.backgroundHeight = 165;
    }

    @Override
    protected void build(FlowLayout layout) {
        this.fuelIndicator = layout.childById(TextureComponent.class, "burning-indicator");
        this.progressIndicator = layout.childById(TextureComponent.class, "progress-indicator");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        float burnProgress = this.handler.propertyDelegate.get(DnaCombinatorBlockEntity.BURN_TIME_PROPERTY) / (float) this.handler.propertyDelegate.get(DnaCombinatorBlockEntity.MAX_BURN_TIME_PROPERTY);

        this.fuelIndicator.visibleArea(PositionedRectangle.of(
              0,
              13 - Math.round(burnProgress * 13),
              this.fuelIndicator.fullSize()
        ));

        float recipeProgress = 1 - this.handler.propertyDelegate.get(DnaCombinatorBlockEntity.RECIPE_PROGRESS_PROPERTY) / (float) DnaCombinatorBlockEntity.getDnaCombinatorProcessingTime();

        if (recipeProgress == 1) {
            recipeProgress = 0;
        }

        this.progressIndicator.visibleArea(PositionedRectangle.of(
              0,
              0,
              Math.round(recipeProgress * 39),
              this.progressIndicator.height()
        ));
    }
}
