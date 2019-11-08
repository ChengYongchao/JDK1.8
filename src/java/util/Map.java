package java.util;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.io.Serializable;

public interface Map<K, V>
{
    // Query Operations

    int size();

    boolean isEmpty();

    boolean containsKey(Object key);

    boolean containsValue(Object value);

    V get(Object key);

    // Modification Operations

    V put(K key, V value);

    V remove(Object key);

    // Bulk Operations

    void putAll(Map<? extends K, ? extends V> m);

    void clear();

    // Views

    Set<K> keySet();

    Collection<V> values();

    Set<Map.Entry<K, V>> entrySet();

    interface Entry<K, V>
    {

        K getKey();

        V getValue();

        V setValue(V value);

        boolean equals(Object o);

        int hashCode();

        public static <K extends Comparable<? super K>, V> Comparator<Map.Entry<K, V>> comparingByKey()
        {
            return (Comparator<Map.Entry<K, V>> & Serializable)(c1, c2) -> c1.getKey().compareTo(c2.getKey());
        }

        public static <K, V extends Comparable<? super V>> Comparator<Map.Entry<K, V>> comparingByValue()
        {
            return (Comparator<Map.Entry<K, V>> & Serializable)(c1, c2) -> c1.getValue().compareTo(c2.getValue());
        }

        public static <K, V> Comparator<Map.Entry<K, V>> comparingByKey(Comparator<? super K> cmp)
        {
            Objects.requireNonNull(cmp);
            return (Comparator<Map.Entry<K, V>> & Serializable)(c1, c2) -> cmp.compare(c1.getKey(), c2.getKey());
        }

        public static <K, V> Comparator<Map.Entry<K, V>> comparingByValue(Comparator<? super V> cmp)
        {
            Objects.requireNonNull(cmp);
            return (Comparator<Map.Entry<K, V>> & Serializable)(c1, c2) -> cmp.compare(c1.getValue(), c2.getValue());
        }
    }

    // Comparison and hashing

    boolean equals(Object o);

    int hashCode();

    // Defaultable methods

    default V getOrDefault(Object key, V defaultValue)
    {
        V v;
        return (((v = get(key)) != null) || containsKey(key)) ? v : defaultValue;
    }

    default void forEach(BiConsumer<? super K, ? super V> action)
    {
        Objects.requireNonNull(action);
        for (Map.Entry<K, V> entry : entrySet())
        {
            K k;
            V v;
            try
            {
                k = entry.getKey();
                v = entry.getValue();
            }
            catch (IllegalStateException ise)
            {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }
            action.accept(k, v);
        }
    }

    /**
     * 将每个条目的值替换为对该条目调用给定函数的结果，直到处理完所有条目或该函数抛出异常。函数抛出的异常将被转发给调用者。
     */
    default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function)
    {
        Objects.requireNonNull(function);
        for (Map.Entry<K, V> entry : entrySet())
        {
            K k;
            V v;
            try
            {
                k = entry.getKey();
                v = entry.getValue();
            }
            catch (IllegalStateException ise)
            {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }

            // ise thrown from function is not a cme.
            v = function.apply(k, v);

            try
            {
                entry.setValue(v);
            }
            catch (IllegalStateException ise)
            {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }
        }
    }

    /**
     * 如果指定的键尚未与值关联(或Map为null)，则将其与给定值关联并返回null，否则将返回当前值。
     */
    default V putIfAbsent(K key, V value)
    {
        V v = get(key);
        if (v == null)
        {
            v = put(key, value);
        }

        return v;
    }

    /**
     * 
     */
    default boolean remove(Object key, Object value)
    {
        Object curValue = get(key);
        // 当两值不相等，或者curValue为null且不包含key 才无法remove并返回false
        if (!Objects.equals(curValue, value) || (curValue == null && !containsKey(key)))
        {
            return false;
        }
        remove(key);
        return true;
    }

    default boolean replace(K key, V oldValue, V newValue)
    {
        Object curValue = get(key);
        if (!Objects.equals(curValue, oldValue) || (curValue == null && !containsKey(key)))
        {
            return false;
        }
        put(key, newValue);
        return true;
    }

    default V replace(K key, V value)
    {
        V curValue;
        // 当由key获取的value不为null或者包含key则做replace操作（不太理解，为啥不直接判断包含key，这么做的必要性是什么？）
        if (((curValue = get(key)) != null) || containsKey(key))
        {
            curValue = put(key, value);
        }
        return curValue;
    }

    /**
     * 如果指定的键尚未与值关联(或映射为null)，则尝试使用给定的映射函数计算其值并将其输入到此映射中，除非为null。
     */
    default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction)
    {
        Objects.requireNonNull(mappingFunction);
        V v;
        if ((v = get(key)) == null)
        {
            V newValue;
            if ((newValue = mappingFunction.apply(key)) != null)
            {
                put(key, newValue);
                return newValue;
            }
        }

        return v;
    }

    default V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
    {
        Objects.requireNonNull(remappingFunction);
        V oldValue;
        if ((oldValue = get(key)) != null)
        {
            V newValue = remappingFunction.apply(key, oldValue);
            if (newValue != null)
            {
                put(key, newValue);
                return newValue;
            }
            else
            {
                remove(key);
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * 根据指定key和function函数计算newValue，newValue为null，若old也为null返回null否则移除key对应的value。newValue不为null则put
     */
    default V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
    {
        Objects.requireNonNull(remappingFunction);
        V oldValue = get(key);

        V newValue = remappingFunction.apply(key, oldValue);
        if (newValue == null)
        {
            // delete mapping
            if (oldValue != null || containsKey(key))
            {
                // something to remove
                remove(key);
                return null;
            }
            else
            {
                // nothing to do. Leave things as they were.
                return null;
            }
        }
        else
        {
            // add or replace old mapping
            put(key, newValue);
            return newValue;
        }
    }

    /**
     * oldValue不为null 使用function计算oldValue和value，为null则直接等于value
     */
    default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)
    {
        Objects.requireNonNull(remappingFunction);
        Objects.requireNonNull(value);
        V oldValue = get(key);
        V newValue = (oldValue == null) ? value : remappingFunction.apply(oldValue, value);
        if (newValue == null)
        {
            remove(key);
        }
        else
        {
            put(key, newValue);
        }
        return newValue;
    }
}
