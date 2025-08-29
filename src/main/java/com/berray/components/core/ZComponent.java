package com.berray.components.core;

import com.berray.GameObject;

/**
 * Registers the z index in the game object. Note that the z index is available in the {@link GameObject#getZ() GameObject}
 * and therefore the z index can be set and returned without this component. The component is useful to initially set
 * the z component and to use the z index for property binding.
 */
public class ZComponent extends Component{
    private final int initialZ;
    private boolean initialZset = false;

    public ZComponent(int z) {
        super("z");
        this.initialZ = z;
    }

    @Override
    public void add(GameObject gameObject) {
        super.add(gameObject);
        // only set the z component once, regardless how often the component is added to the game object
        if (!initialZset) {
            gameObject.setZ(initialZ);
            initialZset = true;
        }
        registerBoundProperty("z", gameObject::getZ, gameObject::setZ);
    }

    public static ZComponent z(int z) {
        return new ZComponent(z);
    }
}
