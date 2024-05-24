package org.cloudbus.cloudsim.examples;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CustomDatacenterBroker extends DatacenterBroker {

    public CustomDatacenterBroker(String name) throws Exception {
        super(name);
    }

    @Override
    protected void processVmCreate(SimEvent ev) {
        super.processVmCreate(ev);
        if (getVmsCreatedList().size() == getVmList().size()) {
            distributeCloudlets();
        }
    }

    protected void distributeCloudlets() {
        List<Cloudlet> cloudletList = getCloudletList();
        List<Vm> vms = getVmsCreatedList();

        Log.printLine("Distributing " + cloudletList.size() + " cloudlets among " + vms.size() + " VMs.");

        for (Cloudlet cloudlet : cloudletList) {
            Vm leastLoadedVm = vms.stream()
                .min(Comparator.comparing(vm -> getCloudletSubmittedList().stream().filter(c -> c.getVmId() == vm.getId()).count()))
                .orElse(null);

            if (leastLoadedVm != null) {
                cloudlet.setVmId(leastLoadedVm.getId());
                Log.printLine("Assigning Cloudlet #" + cloudlet.getCloudletId() + " to VM #" + leastLoadedVm.getId());
            } else {
                Log.printLine("No VM available for Cloudlet #" + cloudlet.getCloudletId());
            }
        }

        super.submitCloudletList(cloudletList);
    }

}
