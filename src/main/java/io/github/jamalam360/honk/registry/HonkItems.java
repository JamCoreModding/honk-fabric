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

package io.github.jamalam360.honk.registry;

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.item.*;
import io.github.jamalam360.jamlib.registry.JamLibContentRegistry;
import io.github.jamalam360.jamlib.registry.annotation.ContentRegistry;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryKey;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

@SuppressWarnings("unused")
@ContentRegistry(HonkInit.MOD_ID)
public class HonkItems implements JamLibContentRegistry {

	public static final Item AMBER = new Item(new QuiltItemSettings());
	public static final Item EGG = new EggItem();
	public static final Item FRIED_EGG = new Item(new QuiltItemSettings().food(new FoodComponent.Builder().hunger(6).saturationModifier(0.8F).meat().build()));
	public static final Item EMPTY_SYRINGE = new SyringeItem();
	public static final Item BLOOD_SYRINGE = new BloodSyringeItem();
	public static final Item DNA = new DnaItem();
	public static final Item MAGNIFYING_GLASS = new MagnifyingGlassItem();
	public static final Item SCREEN = new Item(new QuiltItemSettings());

	@Override
	public RegistryKey<ItemGroup> getItemGroup(Item item) {
		return HonkInit.MAIN_GROUP_KEY;
	}
}
