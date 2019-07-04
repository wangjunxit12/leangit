
package com.meibanlu.driver;

/**
 * Listen for application to being closing.
 *
 * @author alexander.ivanov
 */
public interface OnCloseListener extends BaseManagerInterface {

    /**
     * Called after service have been stoped.
     * <p/>
     * This function will be call from UI thread.
     */
    void onClose();
}
