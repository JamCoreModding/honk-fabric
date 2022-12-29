package io.github.jamalam360.honk.util;

import net.minecraft.screen.PropertyDelegate;

public interface ReadOnlyPropertyDelegate extends PropertyDelegate {

    @Override
    default void set(int index, int value) {
        throw new UnsupportedOperationException("Cannot modify ReadOnlyPropertyDelegate");
    }
}
