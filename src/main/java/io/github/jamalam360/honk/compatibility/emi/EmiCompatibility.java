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

package io.github.jamalam360.honk.compatibility.emi;

import com.google.common.collect.Lists;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.data.DnaData;
import io.github.jamalam360.honk.data.recipe.CentrifugeRecipe;
import io.github.jamalam360.honk.data.recipe.DnaCombinatorRecipe;
import io.github.jamalam360.honk.data.recipe.DnaInjectorExtractorRecipe;
import io.github.jamalam360.honk.data.type.HonkType;
import io.github.jamalam360.honk.registry.HonkBlocks;
import io.github.jamalam360.honk.registry.HonkItems;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;

public class EmiCompatibility implements EmiPlugin {

    public static final Identifier SPRITE_SHEET = HonkInit.idOf("textures/gui/emi_textures.png");
    public static final EmiStack CENTRIFUGE = EmiStack.of(HonkBlocks.CENTRIFUGE);
    public static final EmiStack DNA_INJECTOR_EXTRACTOR = EmiStack.of(HonkBlocks.DNA_INJECTOR_EXTRACTOR);
    public static final EmiStack DNA_COMBINATOR = EmiStack.of(HonkBlocks.DNA_COMBINATOR);
    public static final EmiRecipeCategory CENTRIFUGE_CATEGORY
          = new EmiRecipeCategory(HonkInit.idOf("centrifuge"), CENTRIFUGE, new EmiTexture(SPRITE_SHEET, 0, 0, 16, 16));
    public static final EmiRecipeCategory DNA_INJECTOR_EXTRACTOR_CATEGORY
          = new EmiRecipeCategory(HonkInit.idOf("dna_injector_extractor"), DNA_INJECTOR_EXTRACTOR, new EmiTexture(SPRITE_SHEET, 16, 0, 16, 16));
    public static final EmiRecipeCategory DNA_COMBINATOR_CATEGORY
          = new EmiRecipeCategory(HonkInit.idOf("dna_combinator"), DNA_COMBINATOR, new EmiTexture(SPRITE_SHEET, 0, 16, 16, 16));


    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(CENTRIFUGE_CATEGORY);
        registry.addWorkstation(CENTRIFUGE_CATEGORY, CENTRIFUGE);
        registry.addCategory(DNA_INJECTOR_EXTRACTOR_CATEGORY);
        registry.addWorkstation(DNA_INJECTOR_EXTRACTOR_CATEGORY, DNA_INJECTOR_EXTRACTOR);
        registry.addCategory(DNA_COMBINATOR_CATEGORY);
        registry.addWorkstation(DNA_COMBINATOR_CATEGORY, DNA_COMBINATOR);

        RecipeManager manager = registry.getRecipeManager();

        for (CentrifugeRecipe recipe : manager.listAllOfType(CentrifugeRecipe.TYPE)) {
            registry.addRecipe(new CentrifugeEmiRecipe(recipe));
        }

        for (DnaInjectorExtractorRecipe recipe : manager.listAllOfType(DnaInjectorExtractorRecipe.TYPE)) {
            registry.addRecipe(new DnaInjectorExtractorEmiRecipe(recipe));
        }

        for (DnaCombinatorRecipe recipe : manager.listAllOfType(DnaCombinatorRecipe.TYPE)) {
            if (recipe.getFirstInput().test(HonkItems.DNA.getDefaultStack()) && recipe.getSecondInput().test(HonkItems.DNA.getDefaultStack())) {
                for (HonkType potentialResult : HonkType.ENTRIES.values()) {
                    List<List<Identifier>> potentialParents = Lists.newArrayList(potentialResult.parents());
                    potentialParents.add(List.of(new Identifier(potentialResult.id()), new Identifier(potentialResult.id())));

                    for (List<Identifier> parents : potentialParents) {
                        Identifier finalId = HonkInit.idOf(new Identifier(potentialResult.id()).getPath() + "_from_" + parents.get(0).getPath() + "_and_" + parents.get(1).getPath());
                        ItemStack firstInput = new ItemStack(HonkItems.DNA);
                        new DnaData(HonkType.ENTRIES.get(parents.get(0).toString()), 1, 1, 1, 1).writeNbt(firstInput.getOrCreateNbt());
                        ItemStack secondInput = new ItemStack(HonkItems.DNA);
                        new DnaData(HonkType.ENTRIES.get(parents.get(1).toString()), 1, 1, 1, 1).writeNbt(secondInput.getOrCreateNbt());
                        ItemStack output = new ItemStack(HonkItems.DNA);
                        new DnaData(potentialResult, 1, 1, 1, 1).writeNbt(output.getOrCreateNbt());
                        registry.addRecipe(new DnaCombinatorEmiRecipe(finalId, firstInput, secondInput, output));
                    }
                }
            } else {
                registry.addRecipe(new DnaCombinatorEmiRecipe(recipe));
            }
        }
    }
}
