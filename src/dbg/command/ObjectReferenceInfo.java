package dbg.command;

import com.sun.jdi.Location;
import com.sun.jdi.ObjectReference;

public class ObjectReferenceInfo {
    private ObjectReference objectReference;
    private Location location;

    public ObjectReference getObjectReference() {
        return objectReference;
    }

    public void setObjectReference(ObjectReference objectReference) {
        this.objectReference = objectReference;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
