package util;

import java.util.Vector;

/**
 * Stack implementation
 */
public class Stack {

    /**
     * Default constructor.
     */
    public Stack() {
        lifo = new Vector(10);
    }

    /**
     * Constructor. Creates a stack of limited size
     * @param size size of this stack
     */
    public Stack(int size) {
        lifo = new Vector(size);
    }
    private Vector lifo;

    /**
     * Generic push method. Add a object to this stack
     * @param object
     */
    public synchronized void push(Object object) {
        lifo.addElement(object);
    }

    /**
     * Generic pop method. Remove the top element trom stack
     * @return first object from array or null if empty
     */
    public synchronized Object pop() {
        Object object = lifo.elementAt(lifo.size() - 1);
        lifo.removeElementAt(lifo.size() - 1);
        return object;
    }

    /**
     * Check if stack is empty
     * @return true if stack is empty
     */
    public synchronized boolean isEmpty() {
        return lifo.isEmpty();
    }

    /**
     * Get the size of this stack
     * @return the size of the stack
     */
    public synchronized int size() {
        return lifo.size();
    }

    /**
     * Clear elements from stack
     */
    public synchronized void clear() {
        lifo.removeAllElements();
    }
}
