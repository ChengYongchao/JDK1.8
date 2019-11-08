
package java.util;

import java.util.function.Consumer;

public class LinkedList<E> extends AbstractSequentialList<E>
        implements List<E>, Deque<E>, Cloneable, java.io.Serializable
{
    transient int size = 0;

    /**
     * 指向第一个节点的指针 不变的: (first == null && last == null) || (first.prev == null &&
     * first.item != null)
     */
    transient Node<E> first;

    /**
     * 指向最后一个节点的指针. Invariant: (first == null && last == null) || (last.next ==
     * null && last.item != null)
     */
    transient Node<E> last;

    public LinkedList()
    {}

    /**
     * 构造一个包含指定集合的元素的列表，按集合的迭代器返回元素的顺序排列。
     */
    public LinkedList(Collection<? extends E> c)
    {
        this();
        addAll(c);
    }

    /**
     * 链接e作为第一个元素
     */
    private void linkFirst(E e)
    {
        final Node<E> f = first; // 获取第一个节点的引用
        final Node<E> newNode = new Node<>(null, e, f); // 初始化新的节点，前一个节点为空，元素为e，后一个节点为原第一个节点
        first = newNode; // first指针指向新节点
        if (f == null) // 判断原第一个节点是否为空，若是则根据链表规则，这是个空链表则新元素为唯一一个元素，将last指针指向这个新元素
            last = newNode;
        else // 不是则获取原第一个节点的前一个元素的下一个元素指向当前节点
            f.prev = newNode;
        size++; // size加1
        modCount++; // 修改次数加1
    }

    /**
     * 链接e作为最后一个元素.原理同linkFirst()
     */
    void linkLast(E e)
    {
        final Node<E> l = last;
        final Node<E> newNode = new Node<>(l, e, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;
        modCount++;
    }

    /**
     * 在非空节点succ之前插入元素e。
     */
    void linkBefore(E e, Node<E> succ)
    {
        // assert succ != null;
        final Node<E> pred = succ.prev;
        final Node<E> newNode = new Node<>(pred, e, succ);
        succ.prev = newNode; // succ的前一个节点指针指向新节点
        if (pred == null) // 如果succ的前一个节点为空，则说明succ为第一个节点，则将first指针指向该新节点e
            first = newNode;
        else // 否则前一个节点的下一个节点指针指向新节点e
            pred.next = newNode;
        size++;
        modCount++;
    }

    /**
     * 取消非空的第一个节点f的链接。
     */
    private E unlinkFirst(Node<E> f)
    {
        // assert f == first && f != null;
        final E element = f.item;
        final Node<E> next = f.next;
        f.item = null;
        f.next = null; // help GC
        first = next;
        if (next == null) // next为null说明原第一个节点后没有元素则将last也置为null
            last = null;
        else
            next.prev = null; // 原指向移除的第一个元素，现在将他置为null
        size--;
        modCount++;
        return element;
    }

    /**
     * 取消非空的最后一个节点f的链接。
     */
    private E unlinkLast(Node<E> l)
    {
        // assert l == last && l != null;
        final E element = l.item;
        final Node<E> prev = l.prev;
        l.item = null;
        l.prev = null; // help GC
        last = prev;
        if (prev == null)
            first = null;
        else
            prev.next = null;
        size--;
        modCount++;
        return element;
    }

    /**
     * 取消非空的节点x的链接。
     */
    E unlink(Node<E> x)
    {
        // assert x != null;
        final E element = x.item;
        final Node<E> next = x.next;
        final Node<E> prev = x.prev;

        if (prev == null) // x节点没有prev节点说明它是第一个节点
        {
            first = next;
        }
        else
        {
            prev.next = next; // x节点的前一个节点的下一个节点指向x节点的下一个节点
            x.prev = null;
        }

        if (next == null) // x节点的下一个节点为null说明x是最后一个节点
        {
            last = prev;
        }
        else
        {
            next.prev = prev; // x节点的下一个节点的前一个节点指向x节点的前一个节点
            x.next = null;
        }

        x.item = null;
        size--;
        modCount++;
        return element;
    }

    /**
     * 返回列表中的第一个元素。
     */
    public E getFirst()
    {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return f.item;
    }

    /**
     * 返回列表中的最后一个元素。
     */
    public E getLast()
    {
        final Node<E> l = last;
        if (l == null)
            throw new NoSuchElementException();
        return l.item;
    }

    /**
     * 从列表中删除并返回第一个元素。
     */
    public E removeFirst()
    {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return unlinkFirst(f);
    }

    /**
     * 从列表中移除并返回最后一个元素。
     */
    public E removeLast()
    {
        final Node<E> l = last;
        if (l == null)
            throw new NoSuchElementException();
        return unlinkLast(l);
    }

    /**
     * 将指定的元素插入此列表的开头。
     */
    public void addFirst(E e)
    {
        linkFirst(e);
    }

    /**
     * 将指定的元素插入此列表的结尾。 这个方法相当于add
     */
    public void addLast(E e)
    {
        linkLast(e);
    }

    /**
     * 如果此列表包含指定的元素，则返回true
     */
    public boolean contains(Object o)
    {
        return indexOf(o) != -1;
    }

    /**
     * 返回列表中元素的数目。
     */
    public int size()
    {
        return size;
    }

    /**
     * 将指定的元素追加到此列表的末尾。
     */
    public boolean add(E e)
    {
        linkLast(e);
        return true;
    }

    /**
     * 删除列表中指定位置的元素。将任何后续元素向左移动(从它们的索引中减去1)。返回从列表中删除的元素。
     */
    public boolean remove(Object o)
    {
        if (o == null)
        {
            for (Node<E> x = first; x != null; x = x.next)
            {
                if (x.item == null)
                {
                    unlink(x);
                    return true;
                }
            }
        }
        else
        {
            for (Node<E> x = first; x != null; x = x.next)
            {
                if (o.equals(x.item))
                {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将指定集合中的所有元素按照指定集合的迭代器返回它们的顺序追加到此列表的末尾。如果在操作过程中修改了指定的集合，则此操作的行为未定义。
     * (注意，如果指定的集合是这个列表，并且它不是空的，则会发生这种情况。)
     */
    public boolean addAll(Collection<? extends E> c)
    {
        return addAll(size, c);
    }

    /**
     * 从指定位置开始，将指定集合中的所有元素插入此列表。将当前位于该位置的元素
     * (如果有)和任何后续元素向右移动(增加它们的索引)。新元素将按照指定集合的迭代器返回它们的顺序出现在列表中。
     */
    public boolean addAll(int index, Collection<? extends E> c)
    {
        checkPositionIndex(index);

        Object[] a = c.toArray();
        int numNew = a.length;
        if (numNew == 0)
            return false;

        Node<E> pred, succ;
        if (index == size) // index = size 当前succ置为null，前一个为last
        {
            succ = null;
            pred = last;
        }
        else
        {
            succ = node(index);
            pred = succ.prev;
        }

        for (Object o : a) // 遍历，将所有元素按顺序添加到list后
        {
            @SuppressWarnings ("unchecked")
            E e = (E)o;
            Node<E> newNode = new Node<>(pred, e, null);
            if (pred == null)
                first = newNode;
            else
                pred.next = newNode;
            pred = newNode;
        }

        if (succ == null) // 如果succ为null，说明一开始就是从last之后开始加的，
                          // 而for循环最后会将pred指向list末尾，所以last=
        {
            last = pred;
        }
        else // 否则一开始是从list中间某个位置开始加的，则最后一个节点pred的下一个节点为succ，succ的上一个节点为pred
        {
            pred.next = succ;
            succ.prev = pred;
        }

        size += numNew;
        modCount++;
        return true;
    }

    /**
     * 从列表中删除所有元素。该调用返回后，列表将为空。
     */
    public void clear()
    {
        // Clearing all of the links between nodes is "unnecessary", but:
        // - helps a generational GC if the discarded nodes inhabit
        // more than one generation
        // - is sure to free memory even if there is a reachable Iterator
        for (Node<E> x = first; x != null;)
        {
            Node<E> next = x.next;
            x.item = null;
            x.next = null;
            x.prev = null;
            x = next;
        }
        first = last = null;
        size = 0;
        modCount++;
    }

    // Positional Access Operations

    /**
     * 返回列表中指定位置的元素。
     */
    public E get(int index)
    {
        checkElementIndex(index);
        return node(index).item;
    }

    /**
     * 用指定的元素替换列表中指定位置的元素。
     */
    public E set(int index, E element)
    {
        checkElementIndex(index);
        Node<E> x = node(index);
        E oldVal = x.item;
        x.item = element;
        return oldVal;
    }

    /**
     * 将指定元素插入到列表中的指定位置。将当前位于该位置的元素(如果有)和任何后续元素向右移动(将一个元素添加到它们的索引中)。
     */
    public void add(int index, E element)
    {
        checkPositionIndex(index);

        if (index == size)
            linkLast(element);
        else
            linkBefore(element, node(index));
    }

    /**
     * 删除列表中指定位置的元素。将任何后续元素向左移动(从它们的索引中减去1)。返回从列表中删除的元素。
     */
    public E remove(int index)
    {
        checkElementIndex(index);
        return unlink(node(index));
    }

    /**
     * 指示该参数是否为现有元素的索引。
     */
    private boolean isElementIndex(int index)
    {
        return index >= 0 && index < size;
    }

    /**
     * 说明该参数是迭代器或添加操作的有效位置的索引。
     */
    private boolean isPositionIndex(int index)
    {
        return index >= 0 && index <= size;
    }

    /**
     * 构造IndexOutOfBoundsException详细信息。在错误处理代码的许多可能的重构中，这种“大纲”在服务器和客户端vm上执行得最好。
     */
    private String outOfBoundsMsg(int index)
    {
        return "Index: " + index + ", Size: " + size;
    }

    private void checkElementIndex(int index)
    {
        if (!isElementIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private void checkPositionIndex(int index)
    {
        if (!isPositionIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * 返回指定元素索引处的(非空)节点。
     */
    Node<E> node(int index)
    {
        // assert isElementIndex(index);
        // 类似二分法，减少查找次数
        if (index < (size >> 1)) // 右移除2
        {
            Node<E> x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        }
        else
        {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }

    // Search Operations

    /**
     * 返回该列表中指定元素第一次出现的索引，如果该列表不包含该元素，则返回-1。
     */
    public int indexOf(Object o)
    {
        int index = 0;
        if (o == null) // 等于null 比较
        {
            for (Node<E> x = first; x != null; x = x.next)
            {
                if (x.item == null)
                    return index;
                index++;
            }
        }
        else
        {
            for (Node<E> x = first; x != null; x = x.next)
            {
                if (o.equals(x.item))
                    return index;
                index++;
            }
        }
        return -1;
    }

    /**
     * 回此列表中指定元素的最后一次出现的索引，如果此列表不包含该元素，则返回-1。
     */
    public int lastIndexOf(Object o)
    {
        int index = size;
        if (o == null)
        {
            for (Node<E> x = last; x != null; x = x.prev)
            {
                index--;
                if (x.item == null)
                    return index;
            }
        }
        else
        {
            for (Node<E> x = last; x != null; x = x.prev)
            {
                index--;
                if (o.equals(x.item))
                    return index;
            }
        }
        return -1;
    }

    // Queue operations.

    /**
     * 检索但不删除此列表的头(第一个元素)。
     */
    public E peek()
    {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
    }

    /**
     * 检索但不删除此列表的头(第一个元素)。
     */
    public E element()
    {
        return getFirst();
    }

    /**
     * 检索并删除此列表的头(第一个元素)。
     */
    public E poll()
    {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }

    /**
     * 检索并删除此列表的头(第一个元素)。
     */
    public E remove()
    {
        return removeFirst();
    }

    /**
     * 将指定的元素添加为此列表的末尾(最后一个元素)。
     */
    public boolean offer(E e)
    {
        return add(e);
    }

    // Deque operations
    /**
     * 将指定的元素插入此列表的前面
     */
    public boolean offerFirst(E e)
    {
        addFirst(e);
        return true;
    }

    /**
     * 在列表末尾插入指定的元素。
     */
    public boolean offerLast(E e)
    {
        addLast(e);
        return true;
    }

    /**
     * 检索但不删除此列表的第一个元素，或在此列表为空时返回null。
     */
    public E peekFirst()
    {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
    }

    /**
     * 检索但不删除此列表的最后一个元素，或在此列表为空时返回null。
     */
    public E peekLast()
    {
        final Node<E> l = last;
        return (l == null) ? null : l.item;
    }

    /**
     * 检索并删除此列表的第一个元素，如果该列表为空，则返回null。
     */
    public E pollFirst()
    {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }

    /**
     * 检索并删除此列表的最后一个元素，如果该列表为空，则返回null。
     */
    public E pollLast()
    {
        final Node<E> l = last;
        return (l == null) ? null : unlinkLast(l);
    }

    /**
     * 将元素推入此列表所表示的堆栈。换句话说，将元素插入到列表的前面。 这个方法相当于addFirst(E)。
     */
    public void push(E e)
    {
        addFirst(e);
    }

    /**
     * 从该列表表示的堆栈中弹出一个元素。换句话说，删除并返回这个列表的第一个元素。 这个方法相当于removeFirst()。
     */
    public E pop()
    {
        return removeFirst();
    }

    /**
     * 删除此列表中指定元素的第一个匹配项(在从头到尾遍历列表时)。如果列表不包含该元素，它将保持不变。
     */
    public boolean removeFirstOccurrence(Object o)
    {
        return remove(o);
    }

    /**
     * 删除此列表中指定元素的最后一次出现(从首尾遍历该列表时)。如果列表不包含该元素，它将保持不变。
     */
    public boolean removeLastOccurrence(Object o)
    {
        if (o == null)
        {
            for (Node<E> x = last; x != null; x = x.prev)
            {
                if (x.item == null)
                {
                    unlink(x);
                    return true;
                }
            }
        }
        else
        {
            for (Node<E> x = last; x != null; x = x.prev)
            {
                if (o.equals(x.item))
                {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 返回此列表中元素的列表迭代器(按适当的顺序)，从列表中的指定位置开始。遵守List.listIterator(int)的总契约。
     */
    public ListIterator<E> listIterator(int index)
    {
        checkPositionIndex(index);
        return new ListItr(index);
    }

    private class ListItr implements ListIterator<E>
    {
        private Node<E> lastReturned;

        private Node<E> next;

        private int nextIndex;

        private int expectedModCount = modCount;

        ListItr(int index)
        {
            // assert isPositionIndex(index);
            next = (index == size) ? null : node(index);
            nextIndex = index;
        }

        public boolean hasNext()
        {
            return nextIndex < size;
        }

        public E next()
        {
            checkForComodification();
            if (!hasNext())
                throw new NoSuchElementException();

            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.item;
        }

        public boolean hasPrevious()
        {
            return nextIndex > 0;
        }

        public E previous()
        {
            checkForComodification();
            if (!hasPrevious())
                throw new NoSuchElementException();

            lastReturned = next = (next == null) ? last : next.prev;
            nextIndex--;
            return lastReturned.item;
        }

        public int nextIndex()
        {
            return nextIndex;
        }

        public int previousIndex()
        {
            return nextIndex - 1;
        }

        public void remove()
        {
            checkForComodification();
            if (lastReturned == null)
                throw new IllegalStateException();

            Node<E> lastNext = lastReturned.next;
            unlink(lastReturned);
            if (next == lastReturned)
                next = lastNext;
            else
                nextIndex--;
            lastReturned = null;
            expectedModCount++;
        }

        public void set(E e)
        {
            if (lastReturned == null)
                throw new IllegalStateException();
            checkForComodification();
            lastReturned.item = e;
        }

        public void add(E e)
        {
            checkForComodification();
            lastReturned = null;
            if (next == null)
                linkLast(e);
            else
                linkBefore(e, next);
            nextIndex++;
            expectedModCount++;
        }

        public void forEachRemaining(Consumer<? super E> action)
        {
            Objects.requireNonNull(action);
            while (modCount == expectedModCount && nextIndex < size)
            {
                action.accept(next.item);
                lastReturned = next;
                next = next.next;
                nextIndex++;
            }
            checkForComodification();
        }

        final void checkForComodification()
        {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    private static class Node<E>
    {
        E item;

        Node<E> next;

        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next)
        {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    /**
     * @since 1.6
     */
    public Iterator<E> descendingIterator()
    {
        return new DescendingIterator();
    }

    /**
     * Adapter to provide descending iterators via ListItr.previous
     */
    private class DescendingIterator implements Iterator<E>
    {
        private final ListItr itr = new ListItr(size());

        public boolean hasNext()
        {
            return itr.hasPrevious();
        }

        public E next()
        {
            return itr.previous();
        }

        public void remove()
        {
            itr.remove();
        }
    }

    @SuppressWarnings ("unchecked")
    private LinkedList<E> superClone()
    {
        try
        {
            return (LinkedList<E>)super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError(e);
        }
    }

    /**
     * Returns a shallow copy of this {@code LinkedList}. (The elements
     * themselves are not cloned.)
     *
     * @return a shallow copy of this {@code LinkedList} instance
     */
    public Object clone()
    {
        LinkedList<E> clone = superClone();

        // Put clone into "virgin" state
        clone.first = clone.last = null;
        clone.size = 0;
        clone.modCount = 0;

        // Initialize clone with our elements
        for (Node<E> x = first; x != null; x = x.next)
            clone.add(x.item);

        return clone;
    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element).
     * <p>
     * The returned array will be "safe" in that no references to it are
     * maintained by this list. (In other words, this method must allocate a new
     * array). The caller is thus free to modify the returned array.
     * <p>
     * This method acts as bridge between array-based and collection-based APIs.
     *
     * @return an array containing all of the elements in this list in proper
     *         sequence
     */
    public Object[] toArray()
    {
        Object[] result = new Object[size];
        int i = 0;
        for (Node<E> x = first; x != null; x = x.next)
            result[i++] = x.item;
        return result;
    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element); the runtime type of the returned
     * array is that of the specified array. If the list fits in the specified
     * array, it is returned therein. Otherwise, a new array is allocated with
     * the runtime type of the specified array and the size of this list.
     * <p>
     * If the list fits in the specified array with room to spare (i.e., the
     * array has more elements than the list), the element in the array
     * immediately following the end of the list is set to {@code null}. (This
     * is useful in determining the length of the list <i>only</i> if the caller
     * knows that the list does not contain any null elements.)
     * <p>
     * Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs. Further, this method allows
     * precise control over the runtime type of the output array, and may, under
     * certain circumstances, be used to save allocation costs.
     * <p>
     * Suppose {@code x} is a list known to contain only strings. The following
     * code can be used to dump the list into a newly allocated array of
     * {@code String}: <pre>
     *     String[] y = x.toArray(new String[0]);</pre> Note that
     * {@code toArray(new Object[0])} is identical in function to
     * {@code toArray()}.
     *
     * @param a the array into which the elements of the list are to be stored,
     *            if it is big enough; otherwise, a new array of the same
     *            runtime type is allocated for this purpose.
     * @return an array containing the elements of the list
     * @throws ArrayStoreException if the runtime type of the specified array is
     *             not a supertype of the runtime type of every element in this
     *             list
     * @throws NullPointerException if the specified array is null
     */
    @SuppressWarnings ("unchecked")
    public <T> T[] toArray(T[] a)
    {
        if (a.length < size)
            a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        int i = 0;
        Object[] result = a;
        for (Node<E> x = first; x != null; x = x.next)
            result[i++] = x.item;

        if (a.length > size)
            a[size] = null;

        return a;
    }

    private static final long serialVersionUID = 876323262645176354L;

    /**
     * Saves the state of this {@code LinkedList} instance to a stream (that is,
     * serializes it).
     *
     * @serialData The size of the list (the number of elements it contains) is
     *             emitted (int), followed by all of its elements (each an
     *             Object) in the proper order.
     */
    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException
    {
        // Write out any hidden serialization magic
        s.defaultWriteObject();

        // Write out size
        s.writeInt(size);

        // Write out all elements in the proper order.
        for (Node<E> x = first; x != null; x = x.next)
            s.writeObject(x.item);
    }

    /**
     * Reconstitutes this {@code LinkedList} instance from a stream (that is,
     * deserializes it).
     */
    @SuppressWarnings ("unchecked")
    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException
    {
        // Read in any hidden serialization magic
        s.defaultReadObject();

        // Read in size
        int size = s.readInt();

        // Read in all elements in the proper order.
        for (int i = 0; i < size; i++)
            linkLast((E)s.readObject());
    }

    /**
     * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
     * and <em>fail-fast</em> {@link Spliterator} over the elements in this
     * list.
     * <p>
     * The {@code Spliterator} reports {@link Spliterator#SIZED} and
     * {@link Spliterator#ORDERED}. Overriding implementations should document
     * the reporting of additional characteristic values.
     *
     * @implNote The {@code Spliterator} additionally reports
     *           {@link Spliterator#SUBSIZED} and implements {@code trySplit} to
     *           permit limited parallelism..
     * @return a {@code Spliterator} over the elements in this list
     * @since 1.8
     */
    @Override
    public Spliterator<E> spliterator()
    {
        return new LLSpliterator<E>(this, -1, 0);
    }

    /** A customized variant of Spliterators.IteratorSpliterator */
    static final class LLSpliterator<E> implements Spliterator<E>
    {
        static final int BATCH_UNIT = 1 << 10; // batch array size increment

        static final int MAX_BATCH = 1 << 25; // max batch array size;

        final LinkedList<E> list; // null OK unless traversed

        Node<E> current; // current node; null until initialized

        int est; // size estimate; -1 until first needed

        int expectedModCount; // initialized when est set

        int batch; // batch size for splits

        LLSpliterator(LinkedList<E> list, int est, int expectedModCount)
        {
            this.list = list;
            this.est = est;
            this.expectedModCount = expectedModCount;
        }

        final int getEst()
        {
            int s; // force initialization
            final LinkedList<E> lst;
            if ((s = est) < 0)
            {
                if ((lst = list) == null)
                    s = est = 0;
                else
                {
                    expectedModCount = lst.modCount;
                    current = lst.first;
                    s = est = lst.size;
                }
            }
            return s;
        }

        public long estimateSize()
        {
            return (long)getEst();
        }

        public Spliterator<E> trySplit()
        {
            Node<E> p;
            int s = getEst();
            if (s > 1 && (p = current) != null)
            {
                int n = batch + BATCH_UNIT;
                if (n > s)
                    n = s;
                if (n > MAX_BATCH)
                    n = MAX_BATCH;
                Object[] a = new Object[n];
                int j = 0;
                do
                {
                    a[j++] = p.item;
                } while ((p = p.next) != null && j < n);
                current = p;
                batch = j;
                est = s - j;
                return Spliterators.spliterator(a, 0, j, Spliterator.ORDERED);
            }
            return null;
        }

        public void forEachRemaining(Consumer<? super E> action)
        {
            Node<E> p;
            int n;
            if (action == null)
                throw new NullPointerException();
            if ((n = getEst()) > 0 && (p = current) != null)
            {
                current = null;
                est = 0;
                do
                {
                    E e = p.item;
                    p = p.next;
                    action.accept(e);
                } while (p != null && --n > 0);
            }
            if (list.modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }

        public boolean tryAdvance(Consumer<? super E> action)
        {
            Node<E> p;
            if (action == null)
                throw new NullPointerException();
            if (getEst() > 0 && (p = current) != null)
            {
                --est;
                E e = p.item;
                current = p.next;
                action.accept(e);
                if (list.modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                return true;
            }
            return false;
        }

        public int characteristics()
        {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
    }

}
