package org.opennaas.extensions.router.capability.chassis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.ManagedSystemElement.OperationalStatus;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.utils.ModelHelper;

public class ChassisCapability extends AbstractCapability implements IChassisCapability {

	public static final String	CAPABILITY_TYPE	= "chassis";

	Log							log				= LogFactory.getLog(ChassisCapability.class);

	private String				resourceId		= "";

	public ChassisCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Chassis Capability");
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	// TODO MOVE TO A PARENT
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			// TODO do not use Activator for this, use injection instead
			return Activator.getChassisActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		getQueueManager(resourceId).queueAction(action);
	}

	/**
	 * 
	 * @return QueuemanagerService this capability is associated to.
	 * @throws CapabilityException
	 *             if desired queueManagerService could not be retrieved.
	 */
	private IQueueManagerCapability getQueueManager(String resourceId) throws CapabilityException {
		try {
			return Activator.getQueueManagerService(resourceId);
		} catch (ActivatorException e) {
			throw new CapabilityException("Failed to get QueueManagerService for resource " + resourceId, e);
		}
	}

	/*
	 * IChassisService Implementation
	 */

	@Override
	public void upPhysicalInterface(LogicalPort iface) throws CapabilityException {
		log.info("Start of upPhysicalInterface call");
		iface.setOperationalStatus(OperationalStatus.OK);

		IAction action = createActionAndCheckParams(ChassisActionSet.CONFIGURESTATUS, iface);
		queueAction(action);
		log.info("End of upPhysicalInterface call");

	}

	@Override
	public void downPhysicalInterface(LogicalPort iface) throws CapabilityException {

		log.info("Start of downPhysicalInterface call");
		iface.setOperationalStatus(OperationalStatus.STOPPED);

		IAction action = createActionAndCheckParams(ChassisActionSet.CONFIGURESTATUS, iface);
		queueAction(action);
		log.info("End of downPhysicalInterface call");
	}

	@Override
	public void createSubInterface(NetworkPort iface) throws CapabilityException {

		log.info("Start of createSubInterface call");
		IAction action = createActionAndCheckParams(ChassisActionSet.CONFIGURESUBINTERFACE, iface);
		queueAction(action);
		log.info("End of createSubInterface call");
	}

	@Override
	public void deleteSubInterface(NetworkPort iface) throws CapabilityException {

		log.info("Start of deleteSubInterface call");
		IAction action = createActionAndCheckParams(ChassisActionSet.DELETESUBINTERFACE, iface);
		queueAction(action);
		log.info("End of deleteSubInterface call");
	}

	@Override
	public void setEncapsulation(LogicalPort iface, ProtocolIFType encapsulationType) throws CapabilityException {

		log.info("Start of setEncapsulation call");
		if (!encapsulationType.equals(ProtocolIFType.UNKNOWN)) {
			ProtocolEndpoint encapsulationEndpoint = new ProtocolEndpoint();
			encapsulationEndpoint.setProtocolIFType(encapsulationType);
			iface.addProtocolEndpoint(encapsulationEndpoint);
		}

		removeCurrentEncapsulation(iface);
		setDesiredEncapsulation(iface);
		log.info("End of setEncapsulation call");
	}

	@Override
	public void setEncapsulationLabel(LogicalPort iface, String encapsulationLabel) throws CapabilityException {

		log.info("Start of setEncapsulationLabel call");
		// specify label in iface
		// we use the name of the endpoint to store the encapsulation label
		// and mark protocolType as unknown (it will be discovered by opennaas)
		ProtocolEndpoint protocolEndpoint = new ProtocolEndpoint();
		protocolEndpoint.setName(encapsulationLabel);
		protocolEndpoint.setProtocolIFType(ProtocolIFType.UNKNOWN);
		iface.addProtocolEndpoint(protocolEndpoint);

		// FIXME it assumes there is only TAGGED_ETHERNET and NO encapsulation
		IAction action = createActionAndCheckParams(ChassisActionSet.SET_VLANID, iface);
		queueAction(action);
		log.info("End of setEncapsulationLabel call");
	}

	@Override
	public void createLogicalRouter(ComputerSystem logicalRouter) throws CapabilityException {

		log.info("Start of createLogicalRouter call");
		IAction action = createActionAndCheckParams(ChassisActionSet.CREATELOGICALROUTER, logicalRouter);
		queueAction(action);

		List<LogicalPort> interfaces = new ArrayList<LogicalPort>();
		interfaces.addAll(ModelHelper.getInterfaces(logicalRouter));
		addInterfacesToLogicalRouter(logicalRouter, interfaces);
		log.info("End of createLogicalRouter call");
	}

	@Override
	public void deleteLogicalRouter(ComputerSystem logicalRouter) throws CapabilityException {

		log.info("Start of deleteLogicalRouter call");
		// By default, interfaces are not transfered back to the physical router.
		// Instead, their configuration is lost. If the user wants to maintain them,
		// it can be done launching removeInterfacesFromLogicalRouter(desiredInterfaces) before deleting it.

		// List<LogicalPort> interfaces = new ArrayList<LogicalPort>();
		// interfaces.addAll(ModelHelper.getInterfaces(logicalRouter));
		// removeInterfacesFromLogicalRouter(logicalRouter, interfaces);

		IAction action = createActionAndCheckParams(ChassisActionSet.DELETELOGICALROUTER, logicalRouter);
		queueAction(action);
		log.info("E of deleteLogicalRouter call");
	}

	@Override
	public void addInterfacesToLogicalRouter(ComputerSystem logicalRouter, List<LogicalPort> interfaces) throws CapabilityException {

		log.info("Start of addInterfacesToLogicalRouter call");
		IAction action;
		ComputerSystem logicalRouterStub;
		for (LogicalPort interfaceToAdd : interfaces) {

			logicalRouterStub = new ComputerSystem();
			logicalRouterStub.setName(logicalRouter.getName());
			logicalRouterStub.setElementName(logicalRouter.getElementName());
			logicalRouterStub.addLogicalDevice(interfaceToAdd);

			action = createActionAndCheckParams(ChassisActionSet.ADDINTERFACETOLOGICALROUTER, logicalRouterStub);
			queueAction(action);
		}
		log.info("End of addInterfacesToLogicalRouter call");

	}

	@Override
	public void removeInterfacesFromLogicalRouter(ComputerSystem logicalRouter, List<LogicalPort> interfaces) throws CapabilityException {

		log.info("Start of removeInterfacesFromLogicalRouter call");
		IAction action;
		ComputerSystem logicalRouterStub;
		for (LogicalPort interfaceToAdd : interfaces) {

			logicalRouterStub = new ComputerSystem();
			logicalRouterStub.setName(logicalRouter.getName());
			logicalRouterStub.setElementName(logicalRouter.getElementName());
			logicalRouterStub.addLogicalDevice(interfaceToAdd);

			action = createActionAndCheckParams(ChassisActionSet.REMOVEINTERFACEFROMLOGICALROUTER, logicalRouterStub);
			queueAction(action);
		}
		log.info("End of removeInterfacesFromLogicalRouter call");

	}

	private boolean requiresTaggedEthernetEncapsulation(LogicalPort port) {
		boolean hasTaggedEthernetEndpoint = false;
		for (ProtocolEndpoint endpoint : port.getProtocolEndpoint()) {
			if (endpoint.getProtocolIFType().equals(ProtocolIFType.LAYER_2_VLAN_USING_802_1Q)) {
				hasTaggedEthernetEndpoint = true;
				break;
			}
		}
		return hasTaggedEthernetEndpoint;
	}

	private boolean requiresNoEncapsulation(LogicalPort port) {
		return port.getProtocolEndpoint().isEmpty();
	}

	private void removeCurrentEncapsulation(LogicalPort port) throws CapabilityException {
		// FIXME it assumes there is only TAGGED_ETHERNET and NO encapsulation

		if (requiresTaggedEthernetEncapsulation(port)) {
			// nothing to remove, as current can only be no encapsulation or required one
		} else {
			// it is save to remove TAGGED_ETHERNET, as current can only be tagged or none
			// and removing when it does not exists does not fail
			IAction action = createActionAndCheckParams(ChassisActionSet.REMOVE_TAGGEDETHERNET_ENCAPSULATION, port);
			queueAction(action);
		}
	}

	private void setDesiredEncapsulation(LogicalPort port) throws CapabilityException {

		if (requiresNoEncapsulation(port)) {
			// nothing to set
		} else {
			if (requiresTaggedEthernetEncapsulation(port)) {
				IAction action = createActionAndCheckParams(ChassisActionSet.SET_TAGGEDETHERNET_ENCAPSULATION, port);
				queueAction(action);
			} else {
				throw new CapabilityException("Unsupported encapsulation type");
			}
		}
	}

}
