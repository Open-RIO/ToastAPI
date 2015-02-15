package jaci.openrio.toast.core.loader.module;

import java.util.HashMap;

/**
 * Module Storage is a means by which Modules can store and access data. This data is stored per-module but is open for
 * other modules to manipulate or read from. This allows for 'hooks' into other modules without using a hard dependency.
 *
 * An example of this is an IO module. The module may save IO states in the Module Storage, and then other modules
 * can access it through the {@link ModuleManager} and read this data if it is available
 *
 * @author Jaci
 */
public class ModuleStorage {

    String id;
    HashMap<String, Object> objectStorage;

    public ModuleStorage(String id) {
        this.id = id;
        objectStorage = new HashMap<>();
    }

    public String getID() {
        return id;
    }

    /**
     * Insert an Object into the storage
     * @param identifier The identifier for the object. This is a key and should be unique per entry
     * @param storageObject The object to store
     */
    public void putValue(String identifier, Object storageObject) {
        objectStorage.put(identifier, storageObject);
    }

    /**
     * Retrieve an Object from the storage
     * @param identifier The identifier for the object. This is the key for the object
     * @return The object found, or null if not found
     */
    public Object getValue(String identifier) {
        return objectStorage.get(identifier);
    }

    /**
     * Returns the HashMap of the Module Storage for further manipulation
     */
    public HashMap<String, Object> getStorage() {
        return objectStorage;
    }

}
