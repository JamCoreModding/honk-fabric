package io.github.jamalam360.honk.registry;

import io.github.jamalam360.honk.HonkInit;
import io.github.jamalam360.honk.entity.honk.ai.HonkAttackablesSensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.util.registry.Registry;

public class HonkSensorTypes {

    public static final SensorType<HonkAttackablesSensor> HONK_ATTACKABLES = Registry.register(Registry.SENSOR_TYPE, HonkInit.id("honk_attackables"), new SensorType<>(HonkAttackablesSensor::new));

    public static void init() {}
}
