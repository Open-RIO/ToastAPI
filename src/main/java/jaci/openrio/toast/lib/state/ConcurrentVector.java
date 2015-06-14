package jaci.openrio.toast.lib.state;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Why the extra Join and Remove Queues you ask? Well, if a module that is currently in a ticking or transition state wants
 * to add or remove a listener from the list, we run into some errors. Without 'synchronized', we run into a Concurrent
 * Modification, which causes big issues. With 'synchronized', the Thread will be forever blocked because it's trying to access
 * a synchronized lock from INSIDE the synchronized method. I hate iteration iteration iteration iteration iteration iteration
 * iteration iteration iteration iteration iteration iteration iteration....
 *
 * @author Jaci
 */
public class ConcurrentVector<E> extends ArrayList<E> {

    ArrayList<E> joinQueue = new ArrayList<E>();
    ArrayList<E> removeQueue = new ArrayList<E>();

    boolean changed = false;

    /**
     * Push an element into the Concurrent Vector. This element will be added to the
     * main vector/list when using the {@link #tick()} method
     */
    public void addConcurrent(E element) {
        joinQueue.add(element);
        changed = true;
    }

    /**
     * Pull and release an element from the Concurrent Vector. This element will be removed
     * from the main vector/list when using the {@link #tick()} method
     */
    public void removeConcurrent(E element) {
        removeQueue.add(element);
        changed = true;
    }

    /**
     * Tick the vector. When called, this method will take all the recently added/removed objects and
     * pass them into/out of the main list. This allows the Vector to be accessed by multiple objects when
     * the list is being iterated over. Call this before calling .get() or other, similar methods.
     */
    public void tick() {
        if (!changed) return;

        Iterator<E> joinIt = joinQueue.iterator();
        while (joinIt.hasNext()) {
            this.add(joinIt.next());
            joinIt.remove();
        }

        Iterator<E> removeIt = removeQueue.iterator();
        while (removeIt.hasNext()) {
            this.remove(removeIt.next());
            removeIt.remove();
        }
        changed = false;
        removeIt = null;
        joinIt = null;
    }

}
