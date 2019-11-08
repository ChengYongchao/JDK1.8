package java.util;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import sun.misc.SharedSecrets;

public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{
    private static final long serialVersionUID = 8683452581122892189L;

    // 默认初始容量。
    private static final int DEFAULT_CAPACITY = 10;

    // 用于空实例的共享空数组实例。
    private static final Object[] EMPTY_ELEMENTDATA = {};

    // 用于默认大小的空实例的共享空数组实例。我们将其与EMPTY_ELEMENTDATA区分开来，以了解添加第一个元素时的膨胀程度。
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    /*
     * ==> 官方说明：存储ArrayList元素的数组缓冲区。ArrayList的容量是这个数组缓冲区的长度。
     * ==>当添加第一个元素时，任何带有elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
     * ==>的空ArrayList将被扩展为 DEFAULT_CAPACITY。
     * ==>个人理解：当使用无参构造函数创建ArrayList时，此时的elementData会等于
     * DEFAULTCAPACITY_EMPTY_ELEMENTDATA
     * ==>当添加元素时，通过ensureCapacityInternal()—>calculateCapacity()会初始化数组，
     * ==>数组大小为DEFAULT_CAPACITY
     */
    transient Object[] elementData; // 非私有以简化嵌套类访问

    // The size of the ArrayList (the number of elements it contains)
    private int size;

    // 构造具有指定初始容量的空list
    public ArrayList(int initialCapacity)
    {
        if (initialCapacity > 0)
        {
            this.elementData = new Object[initialCapacity];
        }
        else if (initialCapacity == 0)
        {
            this.elementData = EMPTY_ELEMENTDATA;
        }
        else
        {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
    }

    // 构造一个初始容量为10的空列表(其实初始还是个nullList，一旦开始添加元素就会初始化为默认长度的数组)
    public ArrayList()
    {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    /*
     * 构造一个包含指定集合的元素的列表，按集合的迭代器返回元素的顺序排列。
     */
    public ArrayList(Collection<? extends E> c)
    {
        elementData = c.toArray();
        if ((size = elementData.length) != 0)
        {
            // c.toArray might (incorrectly) not return Object[] (see 6260652)
            if (elementData.getClass() != Object[].class)
                elementData = Arrays.copyOf(elementData, size, Object[].class);
        }
        else
        {
            // replace with empty array.
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }

    // 将ArrayList实例的容量调整为列表的当前大小。应用程序可以使用此操作最小化ArrayList实例的存储。
    public void trimToSize()
    {
        modCount++;
        if (size < elementData.length)
        {
            elementData = (size == 0) ? EMPTY_ELEMENTDATA : Arrays.copyOf(elementData, size);
        }
    }

    /*
     * 如果需要，增加这个ArrayList实例的容量，以确保它至少可以容纳由minCapacity参数指定的元素数量。 思路：
     * 先确认elementData是否指向DEFAULTCAPACITY_EMPTY_ELEMENTDATA，如果是minExpand = 0，否则
     * minExpand = DEFAULT_CAPACITY。若minCapacity > minExpand， 调用扩容方法。
     */
    public void ensureCapacity(int minCapacity)
    {
        int minExpand = (elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA)
                // any size if not default element table
                ? 0
                // larger than default for default empty table. It's already
                // supposed to be at default size.
                : DEFAULT_CAPACITY;

        if (minCapacity > minExpand)
        {
            ensureExplicitCapacity(minCapacity);
        }
    }

    /*
     * 确定elementData是否是初始的DEFAULTCAPACITY_EMPTY_ELEMENTDATA，
     * 如果是minCapacity和初始容量DEFAULT_CAPACITY比较，如果不是，直接返回minCapacity
     */
    private static int calculateCapacity(Object[] elementData, int minCapacity)
    {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA)
        {
            return Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        return minCapacity;
    }

    /*
     * 确保能够扩容方法
     */
    private void ensureCapacityInternal(int minCapacity)
    {
        ensureExplicitCapacity(calculateCapacity(elementData, minCapacity));
    }

    /*
     * 扩容前处理，先modCount++，modCount指该ArrayList被修改的次数，再调用grow方法
     */
    private void ensureExplicitCapacity(int minCapacity)
    {
        modCount++;

        // overflow-conscious code
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }

    /**
     * The maximum size of array to allocate. Some VMs reserve some header words
     * in an array. Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /*
     * 增加容量，以确保它至少可以容纳由minCapacity参数指定的元素数量。 思路：
     * 先获取原elementData的长度，尝试1.5倍扩容，比较是否大于minCapacity，如果不大于就使用minCapacity值，
     * 在比较minCapacity是否大于MAX_ARRAY_SIZE，大于则使用大容量方法，最后复制数组扩容。
     */
    private void grow(int minCapacity)
    {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    /*
     * minCapacity < 0 ?
     * 数值太大越界导致成负数抛出异常，再判断minCapacity是否大于MAX_ARRAY_SIZE，大于赋值Integer.MAX_VALUE
     * ，不大于赋值MAX_ARRAY_SIZE
     */
    private static int hugeCapacity(int minCapacity)
    {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }

    public int size()
    {
        return size;
    }

    public boolean isEmpty()
    {
        return size == 0;
    }

    public boolean contains(Object o)
    {
        return indexOf(o) >= 0;
    }

    /**
     * Returns the index of the first occurrence of the specified element in
     * this list, or -1 if this list does not contain the element. More
     * formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     */
    /*
     * 返回该列表中指定元素第一次出现的索引，如果该列表不包含该元素，则返回-1。
     * 思路：判断o对象是否为空，为空则遍历elementData返回第一个为空的index
     * o对象不为null。遍历elementData，通过equals方法比较返回第一个相等的对象的index。 否则返回-1
     */
    public int indexOf(Object o)
    {
        if (o == null)
        {
            for (int i = 0; i < size; i++)
                if (elementData[i] == null)
                    return i;
        }
        else
        {
            for (int i = 0; i < size; i++)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }

    /*
     * 思路同indexOf方法，但是是逆序遍历
     */
    public int lastIndexOf(Object o)
    {
        if (o == null)
        {
            for (int i = size - 1; i >= 0; i--)
                if (elementData[i] == null)
                    return i;
        }
        else
        {
            for (int i = size - 1; i >= 0; i--)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }

    /*
     * 返回此ArrayList实例的浅拷贝
     */
    public Object clone()
    {
        try
        {
            ArrayList<?> v = (ArrayList<?>)super.clone();
            v.elementData = Arrays.copyOf(elementData, size);
            v.modCount = 0;
            return v;
        }
        catch (CloneNotSupportedException e)
        {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }

    /*
     * 转数组
     */
    public Object[] toArray()
    {
        return Arrays.copyOf(elementData, size);
    }

    @SuppressWarnings ("unchecked")
    public <T> T[] toArray(T[] a)
    {
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[])Arrays.copyOf(elementData, size, a.getClass());
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    // Positional Access Operations

    @SuppressWarnings ("unchecked")
    E elementData(int index)
    {
        return (E)elementData[index];
    }

    /*
     * 返回列表中指定位置的元素 思路：先进行范围检测
     */
    public E get(int index)
    {
        rangeCheck(index);

        return elementData(index);
    }

    /*
     * 设置列表中指定位置的元素
     */
    public E set(int index, E element)
    {
        rangeCheck(index);

        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }

    /*
     * 添加元素
     */
    public boolean add(E e)
    {
        ensureCapacityInternal(size + 1); // 先进行size检查、当size不够时会进行扩容
        elementData[size++] = e;
        return true;
    }

    /*
     * 将指定元素插入到列表中的指定位置。将当前位于该位置的元素(如果有)和任何后续元素向右移动。
     */
    public void add(int index, E element)
    {
        rangeCheckForAdd(index); // 越界检测

        ensureCapacityInternal(size + 1); // 容量检测
        System.arraycopy(elementData, index, elementData, index + 1, size - index);// 将elementDataindex后的所有元素复制一遍
        elementData[index] = element; // 赋值
        size++;
    }

    /*
     * 删除列表中指定位置的元素。将任何后续元素向左移动(从它们的索引中减去1)。
     */
    public E remove(int index)
    {
        rangeCheck(index);// 越界检测

        modCount++; // 修改次数增加
        E oldValue = elementData(index); // 获取值

        int numMoved = size - index - 1; // 获取需要前移的index
        if (numMoved > 0)
            System.arraycopy(elementData, index + 1, elementData, index, numMoved); // 将index之后的元素前移
        elementData[--size] = null; // 将最后一位置null

        return oldValue;
    }

    /*
     * 从该列表中删除指定元素的第一个匹配项(如果存在)。如果列表不包含该元素，它将保持不变。 思路：分为o是否为空走不同逻辑相同处理
     */
    public boolean remove(Object o)
    {
        if (o == null)
        {
            for (int index = 0; index < size; index++)
                if (elementData[index] == null)
                {
                    fastRemove(index);
                    return true;
                }
        }
        else
        {
            for (int index = 0; index < size; index++)
                if (o.equals(elementData[index]))
                {
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }

    /*
     * 私有移除方法，该方法跳过边界检查且不返回被移除的值。
     */
    private void fastRemove(int index)
    {
        modCount++; // 修改次数增加
        int numMoved = size - index - 1; // 获取需要前移的index
        if (numMoved > 0)
            System.arraycopy(elementData, index + 1, elementData, index, numMoved);// 将index之后的元素前移
        elementData[--size] = null; // 将最后一位置null
    }

    /**
     * 从列表中删除所有元素。该调用返回后，列表将为空。
     */
    public void clear()
    {
        modCount++;

        // clear to let GC do its work
        for (int i = 0; i < size; i++)
            elementData[i] = null;

        size = 0;
    }

    /*
     * 将指定集合中的所有元素按照指定集合的迭代器返回它们的顺序追加到此列表的末尾。如果在操作过程中修改了指定的集合，则此操作的行为未定义。(这意味着，
     * 如果指定的集合是这个列表，而这个列表不是空的，则此调用的行为是未定义的。)
     */
    public boolean addAll(int index, Collection<? extends E> c)
    {
        rangeCheckForAdd(index);

        Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacityInternal(size + numNew); // 扩容 size

        int numMoved = size - index;
        if (numMoved > 0)
            System.arraycopy(elementData, index, elementData, index + numNew, numMoved); // 扩容

        System.arraycopy(a, 0, elementData, index, numNew); // 复制
        size += numNew; // 新size
        return numNew != 0;
    }

    /*
     * 该列表中的所有元素的索引都在fromIndex(包含)和toIndex(排除)之间。将任何后续元素向左移动(减少其索引)
     */
    protected void removeRange(int fromIndex, int toIndex)
    {
        modCount++; // 增加修改次数
        int numMoved = size - toIndex; // 需要移动的个数
        System.arraycopy(elementData, toIndex, elementData, fromIndex, numMoved);// 移动

        // clear to let GC do its work
        int newSize = size - (toIndex - fromIndex);
        for (int i = newSize; i < size; i++)
        {
            elementData[i] = null;// 清空
        }
        size = newSize;
    }

    /*
     * 范围检测
     */
    private void rangeCheck(int index)
    {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * 由add和addAll使用的rangeCheck的一个版本。
     */
    private void rangeCheckForAdd(int index)
    {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /*
     * 构造IndexOutOfBoundsException详细信息。在错误处理代码的许多可能的重构中，这种“大纲”在服务器和客户端vm上执行得最好。
     */
    private String outOfBoundsMsg(int index)
    {
        return "Index: " + index + ", Size: " + size;
    }

    /*
     * 从此列表中移除指定集合中包含的所有元素。
     */
    public boolean removeAll(Collection<?> c)
    {
        Objects.requireNonNull(c);
        return batchRemove(c, false);
    }

    /*
     * 仅保留此列表中包含在指定集合中的元素。换句话说，从这个列表中删除指定集合中不包含的所有元素。
     */
    public boolean retainAll(Collection<?> c)
    {
        Objects.requireNonNull(c);
        return batchRemove(c, true);
    }

    /*
     * 思路;complement = fasle时，将所有在c中的元素移除，其他元素从头重新排列，complement = true时，相反
     */
    private boolean batchRemove(Collection<?> c, boolean complement)
    {
        final Object[] elementData = this.elementData;
        int r = 0, w = 0;
        boolean modified = false;
        try
        {
            for (; r < size; r++)
                if (c.contains(elementData[r]) == complement)
                    elementData[w++] = elementData[r];
        }
        finally
        {
            // Preserve behavioral compatibility with AbstractCollection,
            // even if c.contains() throws.
            if (r != size)
            {
                System.arraycopy(elementData, r, elementData, w, size - r);
                w += size - r;
            }
            if (w != size)
            {
                // clear to let GC do its work
                for (int i = w; i < size; i++)
                    elementData[i] = null;
                modCount += size - w;
                size = w;
                modified = true;
            }
        }
        return modified;
    }

    /*
     * 序列化ArrayList
     */
    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException
    {
        // Write out element count, and any hidden stuff
        int expectedModCount = modCount;
        s.defaultWriteObject();

        // Write out size as capacity for behavioural compatibility with clone()
        s.writeInt(size);

        // Write out all elements in the proper order.
        for (int i = 0; i < size; i++)
        {
            s.writeObject(elementData[i]);
        }

        if (modCount != expectedModCount)
        {
            throw new ConcurrentModificationException();
        }
    }

    /*
     * 反序列化ArrayLsit
     */
    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException
    {
        elementData = EMPTY_ELEMENTDATA;

        // Read in size, and any hidden stuff
        s.defaultReadObject();

        // Read in capacity
        s.readInt(); // ignored

        if (size > 0)
        {
            // be like clone(), allocate array based upon size not capacity
            int capacity = calculateCapacity(elementData, size);
            SharedSecrets.getJavaOISAccess().checkArray(s, Object[].class, capacity);
            ensureCapacityInternal(size);

            Object[] a = elementData;
            // Read in all elements in the proper order.
            for (int i = 0; i < size; i++)
            {
                a[i] = s.readObject();
            }
        }
    }

    /*
     * 返回此列表中元素的列表迭代器(按适当的顺序)，从列表中的指定位置开始。指定的索引指示将由对next的初始调用返回的第一个元素。
     * 对previous的初始调用将返回具有指定索引- 1的元素。
     */
    public ListIterator<E> listIterator(int index)
    {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: " + index);
        return new ListItr(index);
    }

    /*
     * 返回此列表中元素的列表迭代器(按适当的顺序)
     */
    public ListIterator<E> listIterator()
    {
        return new ListItr(0);
    }

    /*
     * 按适当的顺序对列表中的元素返回一个迭代器。
     */
    public Iterator<E> iterator()
    {
        return new Itr();
    }

    /**
     * An optimized version of AbstractList.Itr
     */
    private class Itr implements Iterator<E>
    {
        int cursor; // index of next element to return

        int lastRet = -1; // index of last element returned; -1 if no such

        int expectedModCount = modCount;

        Itr()
        {}

        public boolean hasNext()
        {
            return cursor != size;
        }

        @SuppressWarnings ("unchecked")
        public E next()
        {
            checkForComodification();
            int i = cursor;
            if (i >= size)
                throw new NoSuchElementException();
            Object[] elementData = ArrayList.this.elementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i + 1;
            return (E)elementData[lastRet = i];
        }

        public void remove()
        {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try
            {
                ArrayList.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                expectedModCount = modCount;
            }
            catch (IndexOutOfBoundsException ex)
            {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        @SuppressWarnings ("unchecked")
        public void forEachRemaining(Consumer<? super E> consumer)
        {
            Objects.requireNonNull(consumer);
            final int size = ArrayList.this.size;
            int i = cursor;
            if (i >= size)
            {
                return;
            }
            final Object[] elementData = ArrayList.this.elementData;
            if (i >= elementData.length)
            {
                throw new ConcurrentModificationException();
            }
            while (i != size && modCount == expectedModCount)
            {
                consumer.accept((E)elementData[i++]);
            }
            // update once at end of iteration to reduce heap write traffic
            cursor = i;
            lastRet = i - 1;
            checkForComodification();
        }

        final void checkForComodification()
        {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    /**
     * An optimized version of AbstractList.ListItr
     */
    private class ListItr extends Itr implements ListIterator<E>
    {
        ListItr(int index)
        {
            super();
            cursor = index;
        }

        public boolean hasPrevious()
        {
            return cursor != 0;
        }

        public int nextIndex()
        {
            return cursor;
        }

        public int previousIndex()
        {
            return cursor - 1;
        }

        @SuppressWarnings ("unchecked")
        public E previous()
        {
            checkForComodification();
            int i = cursor - 1;
            if (i < 0)
                throw new NoSuchElementException();
            Object[] elementData = ArrayList.this.elementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i;
            return (E)elementData[lastRet = i];
        }

        public void set(E e)
        {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try
            {
                ArrayList.this.set(lastRet, e);
            }
            catch (IndexOutOfBoundsException ex)
            {
                throw new ConcurrentModificationException();
            }
        }

        public void add(E e)
        {
            checkForComodification();

            try
            {
                int i = cursor;
                ArrayList.this.add(i, e);
                cursor = i + 1;
                lastRet = -1;
                expectedModCount = modCount;
            }
            catch (IndexOutOfBoundsException ex)
            {
                throw new ConcurrentModificationException();
            }
        }
    }

    /**
     * Returns a view of the portion of this list between the specified
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive. (If
     * {@code fromIndex} and {@code toIndex} are equal, the returned list is
     * empty.) The returned list is backed by this list, so non-structural
     * changes in the returned list are reflected in this list, and vice-versa.
     * The returned list supports all of the optional list operations.
     * <p>
     * This method eliminates the need for explicit range operations (of the
     * sort that commonly exist for arrays). Any operation that expects a list
     * can be used as a range operation by passing a subList view instead of a
     * whole list. For example, the following idiom removes a range of elements
     * from a list: <pre>
     *      list.subList(from, to).clear();
     * </pre> Similar idioms may be constructed for {@link #indexOf(Object)} and
     * {@link #lastIndexOf(Object)}, and all of the algorithms in the
     * {@link Collections} class can be applied to a subList.
     * <p>
     * The semantics of the list returned by this method become undefined if the
     * backing list (i.e., this list) is <i>structurally modified</i> in any way
     * other than via the returned list. (Structural modifications are those
     * that change the size of this list, or otherwise perturb it in such a
     * fashion that iterations in progress may yield incorrect results.)
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public List<E> subList(int fromIndex, int toIndex)
    {
        subListRangeCheck(fromIndex, toIndex, size);
        return new SubList(this, 0, fromIndex, toIndex);
    }

    static void subListRangeCheck(int fromIndex, int toIndex, int size)
    {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > size)
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
    }

    private class SubList extends AbstractList<E> implements RandomAccess
    {
        private final AbstractList<E> parent;

        private final int parentOffset;

        private final int offset;

        int size;

        SubList(AbstractList<E> parent, int offset, int fromIndex, int toIndex)
        {
            this.parent = parent;
            this.parentOffset = fromIndex;
            this.offset = offset + fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = ArrayList.this.modCount;
        }

        public E set(int index, E e)
        {
            rangeCheck(index);
            checkForComodification();
            E oldValue = ArrayList.this.elementData(offset + index);
            ArrayList.this.elementData[offset + index] = e;
            return oldValue;
        }

        public E get(int index)
        {
            rangeCheck(index);
            checkForComodification();
            return ArrayList.this.elementData(offset + index);
        }

        public int size()
        {
            checkForComodification();
            return this.size;
        }

        public void add(int index, E e)
        {
            rangeCheckForAdd(index);
            checkForComodification();
            parent.add(parentOffset + index, e);
            this.modCount = parent.modCount;
            this.size++;
        }

        public E remove(int index)
        {
            rangeCheck(index);
            checkForComodification();
            E result = parent.remove(parentOffset + index);
            this.modCount = parent.modCount;
            this.size--;
            return result;
        }

        protected void removeRange(int fromIndex, int toIndex)
        {
            checkForComodification();
            parent.removeRange(parentOffset + fromIndex, parentOffset + toIndex);
            this.modCount = parent.modCount;
            this.size -= toIndex - fromIndex;
        }

        public boolean addAll(Collection<? extends E> c)
        {
            return addAll(this.size, c);
        }

        public boolean addAll(int index, Collection<? extends E> c)
        {
            rangeCheckForAdd(index);
            int cSize = c.size();
            if (cSize == 0)
                return false;

            checkForComodification();
            parent.addAll(parentOffset + index, c);
            this.modCount = parent.modCount;
            this.size += cSize;
            return true;
        }

        public Iterator<E> iterator()
        {
            return listIterator();
        }

        public ListIterator<E> listIterator(final int index)
        {
            checkForComodification();
            rangeCheckForAdd(index);
            final int offset = this.offset;

            return new ListIterator<E>()
            {
                int cursor = index;

                int lastRet = -1;

                int expectedModCount = ArrayList.this.modCount;

                public boolean hasNext()
                {
                    return cursor != SubList.this.size;
                }

                @SuppressWarnings ("unchecked")
                public E next()
                {
                    checkForComodification();
                    int i = cursor;
                    if (i >= SubList.this.size)
                        throw new NoSuchElementException();
                    Object[] elementData = ArrayList.this.elementData;
                    if (offset + i >= elementData.length)
                        throw new ConcurrentModificationException();
                    cursor = i + 1;
                    return (E)elementData[offset + (lastRet = i)];
                }

                public boolean hasPrevious()
                {
                    return cursor != 0;
                }

                @SuppressWarnings ("unchecked")
                public E previous()
                {
                    checkForComodification();
                    int i = cursor - 1;
                    if (i < 0)
                        throw new NoSuchElementException();
                    Object[] elementData = ArrayList.this.elementData;
                    if (offset + i >= elementData.length)
                        throw new ConcurrentModificationException();
                    cursor = i;
                    return (E)elementData[offset + (lastRet = i)];
                }

                @SuppressWarnings ("unchecked")
                public void forEachRemaining(Consumer<? super E> consumer)
                {
                    Objects.requireNonNull(consumer);
                    final int size = SubList.this.size;
                    int i = cursor;
                    if (i >= size)
                    {
                        return;
                    }
                    final Object[] elementData = ArrayList.this.elementData;
                    if (offset + i >= elementData.length)
                    {
                        throw new ConcurrentModificationException();
                    }
                    while (i != size && modCount == expectedModCount)
                    {
                        consumer.accept((E)elementData[offset + (i++)]);
                    }
                    // update once at end of iteration to reduce heap write
                    // traffic
                    lastRet = cursor = i;
                    checkForComodification();
                }

                public int nextIndex()
                {
                    return cursor;
                }

                public int previousIndex()
                {
                    return cursor - 1;
                }

                public void remove()
                {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();

                    try
                    {
                        SubList.this.remove(lastRet);
                        cursor = lastRet;
                        lastRet = -1;
                        expectedModCount = ArrayList.this.modCount;
                    }
                    catch (IndexOutOfBoundsException ex)
                    {
                        throw new ConcurrentModificationException();
                    }
                }

                public void set(E e)
                {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();

                    try
                    {
                        ArrayList.this.set(offset + lastRet, e);
                    }
                    catch (IndexOutOfBoundsException ex)
                    {
                        throw new ConcurrentModificationException();
                    }
                }

                public void add(E e)
                {
                    checkForComodification();

                    try
                    {
                        int i = cursor;
                        SubList.this.add(i, e);
                        cursor = i + 1;
                        lastRet = -1;
                        expectedModCount = ArrayList.this.modCount;
                    }
                    catch (IndexOutOfBoundsException ex)
                    {
                        throw new ConcurrentModificationException();
                    }
                }

                final void checkForComodification()
                {
                    if (expectedModCount != ArrayList.this.modCount)
                        throw new ConcurrentModificationException();
                }
            };
        }

        public List<E> subList(int fromIndex, int toIndex)
        {
            subListRangeCheck(fromIndex, toIndex, size);
            return new SubList(this, offset, fromIndex, toIndex);
        }

        private void rangeCheck(int index)
        {
            if (index < 0 || index >= this.size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }

        private void rangeCheckForAdd(int index)
        {
            if (index < 0 || index > this.size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }

        private String outOfBoundsMsg(int index)
        {
            return "Index: " + index + ", Size: " + this.size;
        }

        private void checkForComodification()
        {
            if (ArrayList.this.modCount != this.modCount)
                throw new ConcurrentModificationException();
        }

        public Spliterator<E> spliterator()
        {
            checkForComodification();
            return new ArrayListSpliterator<E>(ArrayList.this, offset, offset + this.size, this.modCount);
        }
    }

    /*
     * 遍历elementData执行action的操作
     */
    @Override
    public void forEach(Consumer<? super E> action)
    {
        Objects.requireNonNull(action);
        final int expectedModCount = modCount; // modCount 安全检测
        @SuppressWarnings ("unchecked")
        final E[] elementData = (E[])this.elementData;
        final int size = this.size;
        for (int i = 0; modCount == expectedModCount && i < size; i++)
        {
            action.accept(elementData[i]);
        }
        if (modCount != expectedModCount)// modCount 安全检测

        {
            throw new ConcurrentModificationException();
        }
    }

    /*
     * 在此列表中的元素上创建late-binding和fail-fast的Spliterator。
     */
    @Override
    public Spliterator<E> spliterator()
    {
        return new ArrayListSpliterator<>(this, 0, -1, 0);
    }

    /** Index-based split-by-two, lazily initialized Spliterator */
    static final class ArrayListSpliterator<E> implements Spliterator<E>
    {

        /*
         * If ArrayLists were immutable, or structurally immutable (no adds,
         * removes, etc), we could implement their spliterators with
         * Arrays.spliterator. Instead we detect as much interference during
         * traversal as practical without sacrificing much performance. We rely
         * primarily on modCounts. These are not guaranteed to detect
         * concurrency violations, and are sometimes overly conservative about
         * within-thread interference, but detect enough problems to be
         * worthwhile in practice. To carry this out, we (1) lazily initialize
         * fence and expectedModCount until the latest point that we need to
         * commit to the state we are checking against; thus improving
         * precision. (This doesn't apply to SubLists, that create spliterators
         * with current non-lazy values). (2) We perform only a single
         * ConcurrentModificationException check at the end of forEach (the most
         * performance-sensitive method). When using forEach (as opposed to
         * iterators), we can normally only detect interference after actions,
         * not before. Further CME-triggering checks apply to all other possible
         * violations of assumptions for example null or too-small elementData
         * array given its size(), that could only have occurred due to
         * interference. This allows the inner loop of forEach to run without
         * any further checks, and simplifies lambda-resolution. While this does
         * entail a number of checks, note that in the common case of
         * list.stream().forEach(a), no checks or other computation occur
         * anywhere other than inside forEach itself. The other less-often-used
         * methods cannot take advantage of most of these streamlinings.
         */

        private final ArrayList<E> list;

        private int index; // current index, modified on advance/split

        private int fence; // -1 until used; then one past last index

        private int expectedModCount; // initialized when fence set

        /** Create new spliterator covering the given range */
        ArrayListSpliterator(ArrayList<E> list, int origin, int fence, int expectedModCount)
        {
            this.list = list; // OK if null unless traversed
            this.index = origin;
            this.fence = fence;
            this.expectedModCount = expectedModCount;
        }

        private int getFence()
        { // initialize fence to size on first use
            int hi; // (a specialized variant appears in method forEach)
            ArrayList<E> lst;
            if ((hi = fence) < 0)
            {
                if ((lst = list) == null)
                    hi = fence = 0;
                else
                {
                    expectedModCount = lst.modCount;
                    hi = fence = lst.size;
                }
            }
            return hi;
        }

        public ArrayListSpliterator<E> trySplit()
        {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid) ? null : // divide range in half unless too small
                    new ArrayListSpliterator<E>(list, lo, index = mid, expectedModCount);
        }

        public boolean tryAdvance(Consumer<? super E> action)
        {
            if (action == null)
                throw new NullPointerException();
            int hi = getFence(), i = index;
            if (i < hi)
            {
                index = i + 1;
                @SuppressWarnings ("unchecked")
                E e = (E)list.elementData[i];
                action.accept(e);
                if (list.modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                return true;
            }
            return false;
        }

        public void forEachRemaining(Consumer<? super E> action)
        {
            int i, hi, mc; // hoist accesses and checks from loop
            ArrayList<E> lst;
            Object[] a;
            if (action == null)
                throw new NullPointerException();
            if ((lst = list) != null && (a = lst.elementData) != null)
            {
                if ((hi = fence) < 0)
                {
                    mc = lst.modCount;
                    hi = lst.size;
                }
                else
                    mc = expectedModCount;
                if ((i = index) >= 0 && (index = hi) <= a.length)
                {
                    for (; i < hi; ++i)
                    {
                        @SuppressWarnings ("unchecked")
                        E e = (E)a[i];
                        action.accept(e);
                    }
                    if (lst.modCount == mc)
                        return;
                }
            }
            throw new ConcurrentModificationException();
        }

        public long estimateSize()
        {
            return (long)(getFence() - index);
        }

        public int characteristics()
        {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
    }

    /*
     * 删除此集合中满足给定谓词的所有元素。迭代期间或由谓词抛出的错误或运行时异常将传递给调用者。
     */
    @Override
    public boolean removeIf(Predicate<? super E> filter)
    {
        Objects.requireNonNull(filter);
        // 在此阶段，从筛选器谓词抛出的任何异常都将使集合保持不变
        int removeCount = 0;
        final BitSet removeSet = new BitSet(size);
        final int expectedModCount = modCount;
        final int size = this.size;
        for (int i = 0; modCount == expectedModCount && i < size; i++)
        {
            @SuppressWarnings ("unchecked")
            final E element = (E)elementData[i];
            if (filter.test(element))
            {
                removeSet.set(i);
                removeCount++;
            }
        }
        if (modCount != expectedModCount)
        {
            throw new ConcurrentModificationException();
        }

        // 将剩余的元素移动到被移除元素所留下的空间上
        final boolean anyToRemove = removeCount > 0;
        if (anyToRemove)
        {
            final int newSize = size - removeCount;
            for (int i = 0, j = 0; (i < size) && (j < newSize); i++, j++)
            {
                i = removeSet.nextClearBit(i);
                elementData[j] = elementData[i];
            }
            for (int k = newSize; k < size; k++)
            {
                elementData[k] = null; // Let gc do its work
            }
            this.size = newSize;
            if (modCount != expectedModCount)
            {
                throw new ConcurrentModificationException();
            }
            modCount++;
        }

        return anyToRemove;
    }

    /*
     * 将此ArrayList中的每个元素替换为将operator应用于该元素的结果。操作符抛出的错误或运行时异常将传递给调用者。
     */
    @Override
    @SuppressWarnings ("unchecked")
    public void replaceAll(UnaryOperator<E> operator)
    {
        Objects.requireNonNull(operator);
        final int expectedModCount = modCount;
        final int size = this.size;
        for (int i = 0; modCount == expectedModCount && i < size; i++)
        {
            elementData[i] = operator.apply((E)elementData[i]);
        }
        if (modCount != expectedModCount)
        {
            throw new ConcurrentModificationException();
        }
        modCount++;
    }

    /*
     * 根据指定比较器产生的顺序对这个列表进行排序。
     */
    @Override
    @SuppressWarnings ("unchecked")
    public void sort(Comparator<? super E> c)
    {
        final int expectedModCount = modCount;
        Arrays.sort((E[])elementData, 0, size, c);
        if (modCount != expectedModCount)
        {
            throw new ConcurrentModificationException();
        }
        modCount++;
    }
}
