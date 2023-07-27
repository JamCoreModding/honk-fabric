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

package io.github.jamalam360.honk.util;

import io.github.jamalam360.honk.data.DnaData;
import net.minecraft.util.random.RandomGenerator;

public class DnaCombinator {

	public static DnaData combine(RandomGenerator random, DnaData a, DnaData b) {
		if (!a.type().id().equals(b.type().id())) {
			return null;
		}

		int growth = combine(random, a.growth(), b.growth(), a.instability(), b.instability());
		int productivity = combine(random, a.productivity(), b.productivity(), a.instability(), b.instability());
		int reproductivity = combine(random, a.reproductivity(), b.reproductivity(), a.instability(), b.instability());
		int instability = combine(random, a.instability(), b.instability(), a.instability(), b.instability());
		return new DnaData(a.type(), growth, productivity, reproductivity, instability);
	}

	private static int combine(RandomGenerator random, int g1, int g2, int i1, int i2) {
		float combinedGene = combine(random, g1, g2);
		float mutatedGene = mutate(random, combinedGene, i1, i2);
		mutatedGene = Math.max(1, Math.min(10, mutatedGene)); // Ensure gene falls within 1-10 range
		return Math.round(mutatedGene);
	}

	private static float combine(RandomGenerator random, int g1, int g2) {
		float avg = (g1 + g2) / 2.0f; // Average of the two genes
		float diff = g2 - g1; // Difference between the two genes

		if (diff > 0) {
			float randomIncrement = random.nextFloat() * diff;
			return avg + randomIncrement;
		} else if (diff < 0) {
			float randomDecrement = random.nextFloat() * Math.abs(diff);
			return avg - randomDecrement;
		} else {
			float randomIncrement = random.nextFloat() * 0.5f + 0.3f;
			return avg + randomIncrement;
		}
	}

	public static float mutate(RandomGenerator random, float combinedGene, int i1, int i2) {
		float instabilityFactor = 0.5f; // Adjust this based on desired mutation intensity
		float maxInstability = instabilityFactor * Math.max(i1, i2);
		float mutationOffset = random.nextFloat() * maxInstability;
		return combinedGene + mutationOffset;
	}
}
