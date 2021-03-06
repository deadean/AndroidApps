package org.robobinding.attribute;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

/**
 * 
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 * @author Cheng Wei
 */
public class StaticResourceAttribute extends AbstractPropertyAttribute {
	private static final Pattern RESOURCE_ATTRIBUTE_PATTERN = Pattern.compile("^@([\\w\\.]+:)?(\\w+)/(\\w+)$");

	String resourceName;
	String resourceType;
	String resourcePackage;

	StaticResourceAttribute(String name, String value) {
		super(name);
		determineResourceNameAndType(value);
	}

	private void determineResourceNameAndType(String value) {
		Matcher matcher = RESOURCE_ATTRIBUTE_PATTERN.matcher(value);

		matcher.find();
		if (!matcher.matches() || matcher.groupCount() < 2 || matcher.groupCount() > 3)
			throw new MalformedAttributeException(getName(), "Invalid resource syntax: " + value);

		resourcePackage = matcher.group(1);

		if (resourcePackage != null && resourcePackage.length() > 0)
			resourcePackage = resourcePackage.substring(0, resourcePackage.length() - 1);

		resourceType = matcher.group(2);
		resourceName = matcher.group(3);
	}

	public int getResourceId(Context context) {
		int resourceId = context.getResources().getIdentifier(resourceName, resourceType, resourcePackage == null ? context.getPackageName() : resourcePackage);

		if (!checkValid(resourceId))
			throw new RuntimeException("No such resource was found");

		return context.getResources().getIdentifier(resourceName, resourceType, resourcePackage == null ? context.getPackageName() : resourcePackage);
	}

	private boolean checkValid(int resourceId) {
		return resourceId != 0;
	}

	@Override
	public boolean isTwoWayBinding() {
		return false;
	}

	static boolean isStaticResourceAttribute(String value) {
		Matcher matcher = RESOURCE_ATTRIBUTE_PATTERN.matcher(value);

		return matcher.matches();
	}
}
