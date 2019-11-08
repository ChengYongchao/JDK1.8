
package java.util;

import java.io.InvalidObjectException;
import sun.misc.SharedSecrets;

public class HashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, java.io.Serializable
{
    static final long serialVersionUID = -5024744406713321676L;

    private transient HashMap<E, Object> map;

    // Dummy value to associate with an Object in the backing Map
    private static final Object PRESENT = new Object();

    /**
     * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has
     * default initial capacity (16) and load factor (0.75).
     */
    public HashSet()
    {
        map = new HashMap<>();
    }

    /**
     * 构造一个新集合，该集合包含指定集合中的元素。HashMap是使用默认的负载因子(0.75)和足够容纳指定集合中的元素的初始容量创建的。
     */
    public HashSet(Collection<? extends E> c)
    {
        map = new HashMap<>(Math.max((int)(c.size() / .75f) + 1, 16)); // 判断容量，实际元素数占全部容量的0.75（负载因子）
        addAll(c);
    }

    /**
     * 构造一个新的空集;支持的HashMap实例具有指定的初始容量和指定的负载因子。
     * 
     * @param initialCapacity
     * @param loadFactor
     */
    public HashSet(int initialCapacity, float loadFactor)
    {
        map = new HashMap<>(initialCapacity, loadFactor);
    }

    /**
     * 构造一个新的空集;支持的HashMap实例具有指定的初始容量
     * 
     * @param initialCapacity
     */
    public HashSet(int initialCapacity)
    {
        map = new HashMap<>(initialCapacity);
    }

    /**
     * 构造一个新的空的HashSet(这是一个包私有构造函数只被LinkedHashSet使用)。支持的HashMap实例是一个LinkedHashMap，它具有指定的初始容量和指定的负载因子。
     * 
     * @param initialCapacity
     * @param loadFactor
     * @param dummy
     */
    HashSet(int initialCapacity, float loadFactor, boolean dummy)
    {
        map = new LinkedHashMap<>(initialCapacity, loadFactor);
    }

    /**
     * 返回一个迭代器。元素没有特定的顺序。
     */
    public Iterator<E> iterator()
    {
        return map.keySet().iterator();
    }

    /**
     * 返回此集合中的元素数(其基数)。
     */
    public int size()
    {
        return map.size();
    }

    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    public boolean contains(Object o)
    {
        return map.containsKey(o);
    }

    /**
     * 如果指定的元素尚未出现，则将其添加到此集合。
     */
    public boolean add(E e)
    {
        return map.put(e, PRESENT) == null; // HashSet的本质是用hashMap做存储，这里将value值存为指定的PRESENT
        // 这里非常有意思的一点，为什么要专门创建一个Object对象存进去呢？
        // 优点1：由于每次存入都是存的引用，所以Object只占一份内存，我们的惯性思维都是存"" null，但是就算是个null
        // 他也是string的，每次 存入都会不同，随着元素增加，占用会越来越大
        // 优点2：做判断，标志位，标志改元素的存在
    }

    /**
     * 如果指定的元素存在，则从该集合中移除它
     */
    public boolean remove(Object o)
    {
        return map.remove(o) == PRESENT;
    }

    public void clear()
    {
        map.clear();
    }

    @SuppressWarnings ("unchecked")
    public Object clone()
    {
        try
        {
            HashSet<E> newSet = (HashSet<E>)super.clone();
            newSet.map = (HashMap<E, Object>)map.clone();
            return newSet;
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError(e);
        }
    }

    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException
    {
        // Write out any hidden serialization magic
        s.defaultWriteObject();

        // Write out HashMap capacity and load factor
        s.writeInt(map.capacity());
        s.writeFloat(map.loadFactor());

        // Write out size
        s.writeInt(map.size());

        // Write out all elements in the proper order.
        for (E e : map.keySet())
            s.writeObject(e);
    }

    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException
    {
        // Read in any hidden serialization magic
        s.defaultReadObject();

        // Read capacity and verify non-negative.
        int capacity = s.readInt();
        if (capacity < 0)
        {
            throw new InvalidObjectException("Illegal capacity: " + capacity);
        }

        // Read load factor and verify positive and non NaN.
        float loadFactor = s.readFloat();
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
        {
            throw new InvalidObjectException("Illegal load factor: " + loadFactor);
        }

        // Read size and verify non-negative.
        int size = s.readInt();
        if (size < 0)
        {
            throw new InvalidObjectException("Illegal size: " + size);
        }
        // Set the capacity according to the size and load factor ensuring that
        // the HashMap is at least 25% full but clamping to maximum capacity.
        capacity = (int)Math.min(size * Math.min(1 / loadFactor, 4.0f), HashMap.MAXIMUM_CAPACITY);

        // Constructing the backing map will lazily create an array when the
        // first element is
        // added, so check it before construction. Call HashMap.tableSizeFor to
        // compute the
        // actual allocation size. Check Map.Entry[].class since it's the
        // nearest public type to
        // what is actually created.

        SharedSecrets.getJavaOISAccess().checkArray(s, Map.Entry[].class, HashMap.tableSizeFor(capacity));

        // Create backing HashMap
        map = (((HashSet<?>)this) instanceof LinkedHashSet ? new LinkedHashMap<E, Object>(capacity, loadFactor)
                : new HashMap<E, Object>(capacity, loadFactor));

        // Read in all elements in the proper order.
        for (int i = 0; i < size; i++)
        {
            @SuppressWarnings ("unchecked")
            E e = (E)s.readObject();
            map.put(e, PRESENT);
        }
    }

    /**
     * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
     * and <em>fail-fast</em> {@link Spliterator} over the elements in this set.
     * <p>
     * The {@code Spliterator} reports {@link Spliterator#SIZED} and
     * {@link Spliterator#DISTINCT}. Overriding implementations should document
     * the reporting of additional characteristic values.
     *
     * @return a {@code Spliterator} over the elements in this set
     * @since 1.8
     */
    public Spliterator<E> spliterator()
    {
        return new HashMap.KeySpliterator<E, Object>(map, 0, -1, 0, 0);
    }
}
