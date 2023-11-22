package com.driverskr.lib.utils.permission;

/**
 */

public interface Action {

    /**
     * An action.
     *
     * @param permissions the current action under permissions.
     */
    void onAction(String[] permissions);

}