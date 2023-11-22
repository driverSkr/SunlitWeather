package com.driverskr.lib.utils.permission;


import androidx.annotation.NonNull;

/**
 * <p>Permission request.</p>
 */
public interface IRequest {

    /**
     * One or more permissions.
     */
    @NonNull
    IRequest permission(String... permissions);

    /**
     * One or more permission groups.
     */
    @NonNull
    IRequest permission(String[]... groups);

    /**
     * Action to be taken when all permissions are granted.
     */
    @NonNull
    IRequest onGranted(Action granted);

    /**
     * Action to be taken when all permissions are denied.
     */
    @NonNull
    IRequest onDenied(Action denied);

    /**
     * IRequest permission.
     */
    void start();

}