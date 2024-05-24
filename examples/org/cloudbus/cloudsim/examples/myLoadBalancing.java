package org.cloudbus.cloudsim.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.getRandomValue;
import org.cloudbus.cloudsim.helperClass;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class myLoadBalancing {

    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmlist;
    private static List<Host> hostList;

	private static int numofTestVM=3;
	private static int numofTestCloudlet=3;

    public static void main(String[] args) {
        Log.printLine("Starting CloudSimExample1...");

        try {
            int num_user = 1;
            int hostId = 0;
            
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;

            CloudSim.init(num_user, calendar, trace_flag);

            Datacenter datacenter0 = createDatacenter("Datacenter_0", hostId);
            hostId++;
            
            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();
            
            vmlist = new ArrayList<Vm>();
            hostList = datacenter0.getHostList();

            long size = 10000; // Image size (MB)
            int pesNumber = 1; // Number of CPUs
            String vmm = "Xen";

            for (int i = 0; i < numofTestVM; i++) {
            	int mips = 500 + (int) (Math.random() * 500); // Randomize MIPS between 1000 and 1500
                int ram = 512 + (int) (Math.random() * (2048 - 512)); // Randomize RAM between 512 and 2048
                long bw = 10000 + (int) (Math.random() * 500); // Randomize bandwidth between 1000 and 1500
              
                Vm vm = new Vm(i, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
                vmlist.add(vm);
                
            }
            	
            helperClass.readRandomFile();
            helperClass.setAlgorithmType(helperClass.HBLoadBalancing);

            broker.submitVmList(vmlist);
            
            cloudletList = new ArrayList<Cloudlet>();
            
            int id = 0;
            long length = 10;
            long fileSize = 300;	
            long outputSize = 300;
            
            UtilizationModel utilizationModel = new UtilizationModelFull();
            int datasetCloudlet = helperClass.getNumOfCloudlets();

            // for integer
            for(int i = 0; i < numofTestCloudlet; i++) {            	
//        		long randint = (long) getRandomValue.getValue();
        		int randint =  getRandomValue.getIntValue();

            	Cloudlet cloudlet = new Cloudlet(id, length*randint, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            	cloudlet.setUserId(brokerId);
            	
            	cloudletList.add(cloudlet);
            	id++;
            }
            
            broker.submitCloudletList(cloudletList);
            HoneyBeeLB.SubmitTask(cloudletList);
            
            int i = 0;
            for(Cloudlet cl : cloudletList) {
                Log.printLine("============== Iterasi ke-" + i + " ================");
                int vmDestId = HoneyBeeLB.Start(vmlist, i);
                Log.printLine("Selected VM : " + vmDestId);
                broker.bindCloudletToVm(cl.getCloudletId(), vmDestId);
                i++;
            }
            
            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            List<Cloudlet> newList = broker.getCloudletReceivedList();
            printCloudletList(newList);
            
            Log.printLine("Simulation finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }

    private static myDatacenter createDatacenter(String name, int hostId) {
        List<Host> hostList = new ArrayList<Host>();
        List<Pe> peList = new ArrayList<Pe>();
        
        int mips = 1000;
        int pe = 0;
        
        for(int vmid=0;vmid<2*numofTestVM;vmid++)
    	{
    		peList.add(new Pe(vmid, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
    		pe += 1;
    	}

        int ram = 65536; //host memory (MB)
        long storage = 15000000; //host storage
        int bw = 10000000;

        hostList.add(
            new Host(
                hostId,
                new RamProvisionerSimple(ram),
                new BwProvisionerSimple(bw),
                storage,
                peList,
                new VmSchedulerTimeShared(peList)
            )
        );
        hostId++;
        hostList.add(
                new Host(
                    hostId,
                    new RamProvisionerSimple(ram),
                    new BwProvisionerSimple(bw),
                    storage,
                    peList,
                    new VmSchedulerTimeShared(peList)
                )
            );
        hostId++;
        hostList.add(
                new Host(
                    hostId,
                    new RamProvisionerSimple(ram),
                    new BwProvisionerSimple(bw),
                    storage,
                    peList,
                    new VmSchedulerTimeShared(peList)
                )
            );
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;
        LinkedList<Storage> storageList = new LinkedList<Storage>();

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            arch, os, vmm, hostList, time_zone, cost, costPerMem,
            costPerStorage, costPerBw
        );

        myDatacenter datacenter = null;
        try {
            datacenter = new myDatacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }

    private static DatacenterBroker createBroker() {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }

    
    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;
        
        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
            + "Data center ID" + indent + "VM ID" + indent + "Time" + indent
            + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + cloudlet.getResourceId()
                    + indent + indent + indent + cloudlet.getVmId()
                    + indent + indent
                    + dft.format(cloudlet.getActualCPUTime()) + indent
                    + indent + dft.format(cloudlet.getExecStartTime())
                    + indent + indent
                    + dft.format(cloudlet.getFinishTime()));
            }
        }
    }
}

