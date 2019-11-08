
package java.util;

public class TreeSet<E> extends AbstractSet<E> implements NavigableSet<E>, Cloneable, java.io.Serializable
{
    /**
     * The backing map.
     */
    private transient NavigableMap<E, Object> m;

    // Dummy value to associate with an Object in the backing Map
    private static final Object PRESENT = new Object();

    /**
     * Constructs a set backed by the specified navigable map.
     */
    TreeSet(NavigableMap<E, Object> m)
    {
        this.m = m;
    }

    /**
     * 构造一个新的nullTreeMap，根据其元素的自然顺序排序。所有插入到集合中的元素必须实现可比接口。此外,所有这些元素都必须相互可比
     */
    public TreeSet()
    {
        this(new TreeMap<E, Object>());
    }

    /**
     * 构造一个新的nullTreeMap，根据指定的比较器排序。所有元素插入到集合必须由指定比较器相互可比
     * 
     * @param comparator
     */
    public TreeSet(Comparator<? super E> comparator)
    {
        this(new TreeMap<>(comparator));
    }

    /**
     * 构造一个新的nullTreeMap，根据其元素的自然顺序排序。所有插入到集合中的元素必须实现可比接口。此外,所有这些元素都必须相互可比
     * 
     * @param c
     */
    public TreeSet(Collection<? extends E> c)
    {
        this();
        addAll(c);
    }

    /**
     * 构造一个包含相同元素的TreeMap，并使用与指定的已排序集相同的顺序。
     * 
     * @param s
     */
    public TreeSet(SortedSet<E> s)
    {
        this(s.comparator());
        addAll(s);
    }

    /**
     * 以升序返回此集合中元素的迭代器.
     *
     * @return an iterator over the elements in this set in ascending order
     */
    public Iterator<E> iterator()
    {
        return m.navigableKeySet().iterator();
    }

    /**
     * 按降序返回该集合中元素的迭代器。
     *
     * @return an iterator over the elements in this set in descending order
     * @since 1.6
     */
    public Iterator<E> descendingIterator()
    {
        return m.descendingKeySet().iterator();
    }

    /**
     * @since 1.6
     */
    public NavigableSet<E> descendingSet()
    {
        return new TreeSet<>(m.descendingMap());
    }

    /**
     * Returns the number of elements in this set (its cardinality).
     *
     * @return the number of elements in this set (its cardinality)
     */
    public int size()
    {
        return m.size();
    }

    /**
     * Returns {@code true} if this set contains no elements.
     *
     * @return {@code true} if this set contains no elements
     */
    public boolean isEmpty()
    {
        return m.isEmpty();
    }

    public boolean contains(Object o)
    {
        return m.containsKey(o);
    }

    public boolean add(E e)
    {
        return m.put(e, PRESENT) == null;
    }

    public boolean remove(Object o)
    {
        return m.remove(o) == PRESENT;
    }

    public void clear()
    {
        m.clear();
    }

    public boolean addAll(Collection<? extends E> c)
    {
        // 如果符合，使用线性时间版本 具体介绍见TreeMap
        if (m.size() == 0 && c.size() > 0 && c instanceof SortedSet && m instanceof TreeMap)
        {
            SortedSet<? extends E> set = (SortedSet<? extends E>)c;
            TreeMap<E, Object> map = (TreeMap<E, Object>)m;
            Comparator<?> cc = set.comparator();
            Comparator<? super E> mc = map.comparator();
            if (cc == mc || (cc != null && cc.equals(mc)))
            {
                map.addAllForTreeSet(set, PRESENT);
                return true;
            }
        }
        return super.addAll(c);
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if {@code fromElement} or {@code toElement}
     *             is null and this set uses natural ordering, or its comparator
     *             does not permit null elements
     * @throws IllegalArgumentException {@inheritDoc}
     * @since 1.6
     */
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive)
    {
        return new TreeSet<>(m.subMap(fromElement, fromInclusive, toElement, toInclusive));
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if {@code toElement} is null and this set
     *             uses natural ordering, or its comparator does not permit null
     *             elements
     * @throws IllegalArgumentException {@inheritDoc}
     * @since 1.6
     */
    public NavigableSet<E> headSet(E toElement, boolean inclusive)
    {
        return new TreeSet<>(m.headMap(toElement, inclusive));
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if {@code fromElement} is null and this set
     *             uses natural ordering, or its comparator does not permit null
     *             elements
     * @throws IllegalArgumentException {@inheritDoc}
     * @since 1.6
     */
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive)
    {
        return new TreeSet<>(m.tailMap(fromElement, inclusive));
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if {@code fromElement} or {@code toElement}
     *             is null and this set uses natural ordering, or its comparator
     *             does not permit null elements
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public SortedSet<E> subSet(E fromElement, E toElement)
    {
        return subSet(fromElement, true, toElement, false);
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if {@code toElement} is null and this set
     *             uses natural ordering, or its comparator does not permit null
     *             elements
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public SortedSet<E> headSet(E toElement)
    {
        return headSet(toElement, false);
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if {@code fromElement} is null and this set
     *             uses natural ordering, or its comparator does not permit null
     *             elements
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public SortedSet<E> tailSet(E fromElement)
    {
        return tailSet(fromElement, true);
    }

    public Comparator<? super E> comparator()
    {
        return m.comparator();
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public E first()
    {
        return m.firstKey();
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public E last()
    {
        return m.lastKey();
    }

    // NavigableSet API methods

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified element is null and this
     *             set uses natural ordering, or its comparator does not permit
     *             null elements
     * @since 1.6
     */
    public E lower(E e)
    {
        return m.lowerKey(e);
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified element is null and this
     *             set uses natural ordering, or its comparator does not permit
     *             null elements
     * @since 1.6
     */
    public E floor(E e)
    {
        return m.floorKey(e);
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified element is null and this
     *             set uses natural ordering, or its comparator does not permit
     *             null elements
     * @since 1.6
     */
    public E ceiling(E e)
    {
        return m.ceilingKey(e);
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified element is null and this
     *             set uses natural ordering, or its comparator does not permit
     *             null elements
     * @since 1.6
     */
    public E higher(E e)
    {
        return m.higherKey(e);
    }

    /**
     * @since 1.6
     */
    public E pollFirst()
    {
        Map.Entry<E, ?> e = m.pollFirstEntry();
        return (e == null) ? null : e.getKey();
    }

    /**
     * @since 1.6
     */
    public E pollLast()
    {
        Map.Entry<E, ?> e = m.pollLastEntry();
        return (e == null) ? null : e.getKey();
    }

    /**
     * Returns a shallow copy of this {@code TreeSet} instance. (The elements
     * themselves are not cloned.)
     *
     * @return a shallow copy of this set
     */
    @SuppressWarnings ("unchecked")
    public Object clone()
    {
        TreeSet<E> clone;
        try
        {
            clone = (TreeSet<E>)super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError(e);
        }

        clone.m = new TreeMap<>(m);
        return clone;
    }

    /**
     * Save the state of the {@code TreeSet} instance to a stream (that is,
     * serialize it).
     *
     * @serialData Emits the comparator used to order this set, or {@code null}
     *             if it obeys its elements' natural ordering (Object), followed
     *             by the size of the set (the number of elements it contains)
     *             (int), followed by all of its elements (each an Object) in
     *             order (as determined by the set's Comparator, or by the
     *             elements' natural ordering if the set has no Comparator).
     */
    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException
    {
        // Write out any hidden stuff
        s.defaultWriteObject();

        // Write out Comparator
        s.writeObject(m.comparator());

        // Write out size
        s.writeInt(m.size());

        // Write out all elements in the proper order.
        for (E e : m.keySet())
            s.writeObject(e);
    }

    /**
     * Reconstitute the {@code TreeSet} instance from a stream (that is,
     * deserialize it).
     */
    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException
    {
        // Read in any hidden stuff
        s.defaultReadObject();

        // Read in Comparator
        @SuppressWarnings ("unchecked")
        Comparator<? super E> c = (Comparator<? super E>)s.readObject();

        // Create backing TreeMap
        TreeMap<E, Object> tm = new TreeMap<>(c);
        m = tm;

        // Read in size
        int size = s.readInt();

        tm.readTreeSet(size, s, PRESENT);
    }

    /**
     * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
     * and <em>fail-fast</em> {@link Spliterator} over the elements in this set.
     * <p>
     * The {@code Spliterator} reports {@link Spliterator#SIZED},
     * {@link Spliterator#DISTINCT}, {@link Spliterator#SORTED}, and
     * {@link Spliterator#ORDERED}. Overriding implementations should document
     * the reporting of additional characteristic values.
     * <p>
     * The spliterator's comparator (see
     * {@link java.util.Spliterator#getComparator()}) is {@code null} if the
     * tree set's comparator (see {@link #comparator()}) is {@code null}.
     * Otherwise, the spliterator's comparator is the same as or imposes the
     * same total ordering as the tree set's comparator.
     *
     * @return a {@code Spliterator} over the elements in this set
     * @since 1.8
     */
    public Spliterator<E> spliterator()
    {
        return TreeMap.keySpliteratorFor(m);
    }

    private static final long serialVersionUID = -2479143000061671589L;
}
