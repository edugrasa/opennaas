package org.opennaas.core.resources.tests.helpers.mocks.profile;

import java.util.Map;

import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.profile.IProfile;
import org.opennaas.core.resources.profile.ProfileDescriptor;

public class MockProfileFactory {

	public static IProfile newMockProfilefactory(ProfileDescriptor profileDescriptor, Map<String, IActionSet> actionSets) {
		IProfile profile = new MockProfile(profileDescriptor);

		for (String idCapability : actionSets.keySet()) {
			IActionSet actionSet = actionSets.get(idCapability);
			profile.addActionSetForCapability(actionSet, idCapability);
		}

		return profile;

	}

}