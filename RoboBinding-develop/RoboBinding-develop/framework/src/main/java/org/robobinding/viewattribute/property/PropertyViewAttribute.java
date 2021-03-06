package org.robobinding.viewattribute.property;

/**
 * 
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
public interface PropertyViewAttribute<ViewType, PropertyType> {
	void updateView(ViewType view, PropertyType newValue);
}
