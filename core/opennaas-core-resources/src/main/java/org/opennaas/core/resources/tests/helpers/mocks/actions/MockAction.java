package org.opennaas.core.resources.tests.helpers.mocks.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;

public class MockAction extends Action {
	private Log				log				= LogFactory.getLog(MockAction.class);

	ActionResponse			actionResponse;
	private static String	actionIdMock	= "actionMock";

	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		log.info("----> Executing action: MOCK ACTION");
		actionResponse = new ActionResponse();
		actionResponse.setActionID(actionID);
		return actionResponse;
	}

	public boolean checkParams(Object params) throws ActionException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setActionResponse(ActionResponse actionResponse) {
		this.actionResponse = actionResponse;
	}

	public ActionResponse getActionResponse() {
		return actionResponse;
	}

}