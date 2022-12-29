package io.github.jamalam360.honk.api;

public interface PowerProvider {

    boolean isPowered();

    default void onBeginProcessing() {
    }
}
