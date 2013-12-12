package util;

import java.util.Vector;

/**
 * Queue implementation
 * @author Florian Chis
 */
public class Queue {

    /**
     * Default constructor
     */
    public Queue() {
        fifo = new Vector(10);
    }

    /**
     * Constructor. Creates a queue of limited size
     * @param size size of this queue
     */
    public Queue(int size) {
        fifo = new Vector(size);
    }
    private Vector fifo;

    /**
     * Generic push method. Add a object to queue
     * @param object
     */
    public synchronized void push(Object object) {
        fifo.addElement(object);
    }

    /**
     * Generic pop method. Remove the bottom element from queue
     * @return last object from array or null if empty
     */
    public synchronized Object pop() {
        Object object = fifo.elementAt(0);
        fifo.removeElementAt(0);
        return object;
    }

    /**
     * Check if queue is empty
     * @return true if queue is empty
     */
    public synchronized boolean isEmpty() {
        return fifo.isEmpty();
    }

    /**
     * Get the size of this queue
     * @return size of the queue
     */
    public synchronized int size() {
        return fifo.size();
    }

    /**
     * Clear elements from queue
     */
    public synchronized void clear() {
        fifo.removeAllElements();
    }
}
