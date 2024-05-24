package org.cloudbus.cloudsim.examples;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

public class CloudletLoadBalancer 
{
	static ArrayList<myVM> OVM =   new ArrayList<myVM>();
	static ArrayList<myVM> LVM =   new ArrayList<myVM>();
	static ArrayList<myVM> BVM =   new ArrayList<myVM>();
	
	
	static double  threshHold = 0.9;
	static int counterLoadBalancer = 0;
	
	public static void setLVM(int vmID, double CPUvalue)
	{
		
		LVM.add(new myVM(vmID, CPUvalue));
	}
	
	public static void printVMClassification() {
        System.out.println("Low-Loaded VMs: " + getVMIds(LVM));
    }
		
    private static List<Integer> getVMIds(ArrayList<myVM> vmList) {
        List<Integer> ids = new ArrayList<>();
        for (myVM vm : vmList) {
            ids.add(vm.vmid);
        }
        return ids;
    }
    
	public static int HBLoadBalancer(List<Vm> list, Cloudlet cl) {
		// Standard Deviation
		
		
		return 0;
	}


}

