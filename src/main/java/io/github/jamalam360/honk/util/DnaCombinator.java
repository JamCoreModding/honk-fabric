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

//    public static DnaData combine(RandomGenerator random, DnaData a, DnaData b) {
//        if (!a.type().id().equals(b.type().id())) {
//            return null;
//        }
//
//        int growth = combine(random, a.growth(), b.growth(), a.instability(), b.instability());
//        int productivity = combine(random, a.productivity(), b.productivity(), a.instability(), b.instability());
//        int reproductivity = combine(random, a.reproductivity(), b.reproductivity(), a.instability(), b.instability());
//        int instability = combine(random, a.instability(), b.instability(), a.instability(), b.instability());
//        return new DnaData(a.type(), growth, productivity, reproductivity, instability);
//    }
//
//    private static int combine(RandomGenerator random, int a, int b, int aInstab, int bInstab) {
//        float aMutation = random.nextFloat() * aInstab;
//        aMutation = random.nextBoolean() ? aMutation : -aMutation;
//        aMutation = aInstab >= 5 ? aMutation / 2 : aMutation;
//        float bMutation = (random.nextFloat() * bInstab) / 2.0F;
//        bMutation = random.nextBoolean() ? bMutation : -bMutation;
//        bMutation = bInstab >= 5 ? bMutation / 2 : bMutation;
//        float result = calculateBiasedAverage(a, b) + aMutation + bMutation;
//        return Math.max(1, Math.min(10, Math.round(result)));
//    }
//
//    private static float calculateBiasedAverage(float a, float b) {
//        float weightA, weightB;
//
//        if (a > b) {
//            weightA = 0.7F;
//            weightB = 0.3F;
//        } else {
//            weightA = 0.3F;
//            weightB = 0.7F;
//        }
//
//        return (weightA * a) + (weightB * b);
//    }
}