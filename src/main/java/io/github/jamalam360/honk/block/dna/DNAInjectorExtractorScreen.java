package io.github.jamalam360.honk.block.dna;

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.block.centrifuge.CentrifugeBlockEntity;
import io.github.jamalam360.honk.block.centrifuge.CentrifugeScreenHandler;
import io.wispforest.owo.ui.base.BaseUIModelHandledScreen;
import io.wispforest.owo.ui.base.BaseUIModelScreen.DataSource;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.PositionedRectangle;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class DNAInjectorExtractorScreen extends BaseUIModelHandledScreen<FlowLayout, DNAInjectorExtractorScreenHandler> {

    private TextureComponent fuelIndicator;
    private TextureComponent progressIndicator;

    public DNAInjectorExtractorScreen(DNAInjectorExtractorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, FlowLayout.class, DataSource.asset(HonkInit.id("dna_injector_extractor")));
        this.backgroundWidth = 176;
        this.backgroundHeight = 165;
    }

    @Override
    protected void build(FlowLayout layout) {
        this.fuelIndicator = layout.childById(TextureComponent.class, "burning-indicator");
        this.progressIndicator = layout.childById(TextureComponent.class, "progress-indicator");
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        float burnProgress = this.handler.propertyDelegate.get(CentrifugeBlockEntity.BURN_TIME_PROPERTY) / (float) this.handler.propertyDelegate.get(CentrifugeBlockEntity.MAX_BURN_TIME_PROPERTY);

        this.fuelIndicator.visibleArea(PositionedRectangle.of(
              0,
              13 - Math.round(burnProgress * 13),
              this.fuelIndicator.fullSize()
        ));

        float recipeProgress = 1 - this.handler.propertyDelegate.get(CentrifugeBlockEntity.RECIPE_PROGRESS_PROPERTY) / (float) CentrifugeBlockEntity.getCentrifugeProcessingTime();

        if (recipeProgress == 1) {
            recipeProgress = 0;
        }

        this.progressIndicator.visibleArea(PositionedRectangle.of(
              0,
              0,
              Math.round(recipeProgress * 23),
              this.progressIndicator.height()
        ));
    }
}
