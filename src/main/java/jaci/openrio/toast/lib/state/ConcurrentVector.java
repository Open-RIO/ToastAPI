package jaci.openrio.toast.lib.state;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

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

    public void addConcurrent(E element) {
        joinQueue.add(element);
        changed = true;
    }

    public void removeConcurrent(E element) {
        removeQueue.add(element);
        changed = true;
    }

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
