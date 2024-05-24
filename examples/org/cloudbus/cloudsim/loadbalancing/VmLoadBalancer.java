package org.cloudbus.cloudsim.loadbalancing;

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
    private LoadBalancingPolicy loadBalancingPolicy;

    public VmLoadBalancer(Datacenter datacenter, List<Vm> vmList, List<Host> hostList, LoadBalancingPolicy loadBalancingPolicy) {
        this.datacenter = datacenter;
        this.vmList = vmList;
        this.hostList = hostList;
        this.loadBalancingPolicy = loadBalancingPolicy;
    }

    public void balanceLoad(List<Cloudlet> cloudletList) {
        Log.printLine("Initial Total Hosts: " + hostList.size());
        Log.printLine("Initial Total VMs: " + vmList.size());
        Log.printLine("LB_Policy: " + loadBalancingPolicy.getLoadBalancingPolicy());
        
        if(loadBalancingPolicy.getLoadBalancingPolicy() == "RoundRobin") {        	
        	for (Cloudlet cloudlet : cloudletList) {
        		Vm vm = loadBalancingPolicy.begin(vmList);
        		cloudlet.setVmId(vm.getId());
        		System.out.println("Cloudlet " + cloudlet.getCloudletId() + " assigned to VM " + vm.getId());
        	}
        }
        if(loadBalancingPolicy.getLoadBalancingPolicy() == "FCFS") {        	
        	for (Cloudlet cloudlet : cloudletList) {
        		Vm vm = loadBalancingPolicy.begin(vmList);
        		cloudlet.setVmId(vm.getId());
        		System.out.println("Cloudlet " + cloudlet.getCloudletId() + " assigned to VM " + vm.getId());
        	}
        }
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

    public List<Host> getHostList() {
        return hostList;
    }

    public void setHostList(List<Host> hostList) {
        this.hostList = hostList;
    }

    public LoadBalancingPolicy getLoadBalancingPolicy() {
        return loadBalancingPolicy;
    }

    public void setLoadBalancingPolicy(LoadBalancingPolicy loadBalancingPolicy) {
        this.loadBalancingPolicy = loadBalancingPolicy;
    }
}
