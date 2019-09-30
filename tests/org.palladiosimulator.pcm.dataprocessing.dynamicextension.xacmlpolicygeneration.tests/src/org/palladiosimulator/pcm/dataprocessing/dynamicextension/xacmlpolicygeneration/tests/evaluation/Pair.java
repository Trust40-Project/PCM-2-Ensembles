package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests.evaluation;

import java.util.Objects;

public final class Pair<K, V> {
    private final K key;
    private final V value;
    
    public Pair(final K key, final V value) {
        this.key = Objects.requireNonNull(key);
        this.value = Objects.requireNonNull(value);
    }
    
    public K getKey() {
        return this.key;
    }
    
    public V getValue() {
        return this.value;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.value);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other != null && other.getClass().equals(getClass())) {
            final Pair<?,?> otherPair = (Pair<?,?>)other;
            return this.key.equals(otherPair.key) && this.value.equals(otherPair.value);
        }
        return false;
    }
    
    @Override 
    public String toString() {
        return this.key + " " + this.value;
    }
}
