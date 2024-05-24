package org.cloudbus.cloudsim.examples;

import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.helperClass;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

public class myDatacenter extends Datacenter {
	int iteration = 1;
    public myDatacenter(String name, DatacenterCharacteristics characteristics,
                            VmAllocationPolicy vmPolicy, List<Storage> storageList, double schedulingInterval)
            throws Exception {
        super(name, characteristics, vmPolicy, storageList, schedulingInterval);
    }

    @Override
    protected void processCloudletSubmit(SimEvent ev, boolean ack) {
        super.updateCloudletProcessing();

        try {	
            Cloudlet cl = (Cloudlet) ev.getData();

            if (cl.isFinished()) {
                sendFinishedCloudletResponse(cl, ack);
                return;
            }

            cl.setResourceParameter(getId(), getCharacteristics().getCostPerSecond(), getCharacteristics().getCostPerBw());

            double fileTransferTime = predictFileTransferTime(cl.getRequiredFiles());
            
			int userId = cl.getUserId();
			int vmId = cl.getVmId();
			
			Host host = getVmAllocationPolicy().getHost(vmId, userId);
			Vm vm = host.getVm(vmId, userId);
            CloudletScheduler scheduler = vm.getCloudletScheduler();
            int datasetCloudlet = helperClass.getNumOfCloudlets();

            // Load Balancing Algorithm
//			implementLoadBalancing(host.getVmList(),cl,vm);
            
            double estimatedFinishTime = scheduler.cloudletSubmit(cl, fileTransferTime);
            if (estimatedFinishTime > 0.0 && !Double.isInfinite(estimatedFinishTime)) {
                estimatedFinishTime += fileTransferTime;
                send(getId(), estimatedFinishTime, CloudSimTags.VM_DATACENTER_EVENT);
            }

            sendAckIfNeeded(ack, cl);
        } catch (Exception e) {
            Log.printLine(getName() + ".processCloudletSubmit(): " + "Exception error.");
            e.printStackTrace();
        }

        checkCloudletCompletion();
    }

    private void implementLoadBalancing(List<Vm> list, Cloudlet cl, Vm vm) {
        if (helperClass.getAlgorithmType() == helperClass.HBLoadBalancing) {
            int vmDestId = HoneyBeeLB.Start(list);
        	
            int[] moveProperties = {cl.getCloudletId(), cl.getUserId(), vm.getId(), vmDestId, getId()};
            sendNow(getId(), CloudSimTags.CLOUDLET_MOVE, moveProperties);

        }
    }

    private void sendFinishedCloudletResponse(Cloudlet cl, boolean ack) {
        // Similar implementation as your original snippet
    }

    private void sendAckIfNeeded(boolean ack, Cloudlet cl) {
        // Similar implementation as your original snippet
    }
}
