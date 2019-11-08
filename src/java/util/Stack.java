
package java.util;

public class Stack<E> extends Vector<E>
{
    /**
     * Creates an empty Stack.
     */
    public Stack()
    {}

    /**
     * 将一个itempush到Stack的顶部
     */

    public E push(E item)
    {
        addElement(item);

        return item;
    }

    /**
     * 删除此Stack顶部的对象并将该对象作为此函数的值返回。
     */
    public synchronized E pop()
    {
        E obj;
        int len = size();

        obj = peek();
        removeElementAt(len - 1);

        return obj;
    }

    /**
     * 查看此Stack顶部的对象，但不将其从Stack中删除。
     */
    public synchronized E peek()
    {
        int len = size();

        if (len == 0)
            throw new EmptyStackException();
        return elementAt(len - 1);
    }

    /**
     * Tests if this stack is empty.
     */
    public boolean empty()
    {
        return size() == 0;
    }

    /**
     * 若stack种有这个元素，返回离stack顶部最近的元素距离顶部的距离，如果没有 返回-1
     */
    public synchronized int search(Object o)
    {
        int i = lastIndexOf(o);

        if (i >= 0)
        {
            return size() - i;
        }
        return -1;
    }

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 1224463164541339165L;
}
