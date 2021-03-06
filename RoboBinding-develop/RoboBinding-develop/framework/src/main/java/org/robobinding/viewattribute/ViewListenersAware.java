package org.robobinding.viewattribute;

/**
 * 
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
public interface ViewListenersAware<T extends ViewListeners> {
	void setViewListeners(T viewListeners);
}
