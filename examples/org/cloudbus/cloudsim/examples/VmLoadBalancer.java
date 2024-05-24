package org.cloudbus.cloudsim.examples;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;

public class VmLoadBalancer {

    private Datacenter datacenter;
    private List<Vm> vmList;
    private List<Host> hostList;
    private int currentIndex;

    public VmLoadBalancer(Datacenter datacenter, List<Vm> vmList, List<Host> hostList) {
        this.datacenter = datacenter;
        this.vmList = vmList;
        this.hostList = hostList;
        this.currentIndex = 0;
    }

    public void balanceLoad(List<Cloudlet> cloudletList) {
    	Log.printLine("Initial Total Hosts: " + hostList.size());
    	Log.printLine("Initial Total VMs: " + vmList.size());
        
        for (Cloudlet cloudlet : cloudletList) {
            Vm vm = getNextVm();
            cloudlet.setVmId(vm.getId());
            System.out.println("Cloudlet " + cloudlet.getCloudletId() + " assigned to VM " + vm.getId());
        }
    }

    private Vm getNextVm() {
        Vm vm = vmList.get(currentIndex);
        currentIndex = (currentIndex + 1) % vmList.size();
        System.out.println("Next VM selected: " + vm.getId());
        return vm;
    }

    public Datacenter getDatacenter() {
        return datacenter;
    }

    public void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
    }

    public List<Vm> getVmList() {
        return vmList;
    }

    public void setVmList(List<Vm> vmList) {
        this.vmList = vmList;
    }
}

