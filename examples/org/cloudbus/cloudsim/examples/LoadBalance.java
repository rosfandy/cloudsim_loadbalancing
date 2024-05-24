package org.cloudbus.cloudsim.examples;

import org.cloudbus.cloudsim.core.CloudSim;
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
import org.cloudbus.cloudsim.lists.VmList;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LoadBalance {

    private static List<Vm> vmList;
    private static List<Cloudlet> cloudletList;

    public static void main(String[] args) {
        Log.printLine("Starting LoadBalancerExample...");

        try {
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            boolean traceFlag = false;

            CloudSim.init(numUsers, calendar, traceFlag);

            Datacenter datacenter0 = createDatacenter("Datacenter_0");

            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();

            vmList = createVM(brokerId, 5);  // Create 5 VMs
            cloudletList = createCloudlet(brokerId, 10); // Create 10 cloudlets

            broker.submitVmList(vmList);
            broker.submitCloudletList(cloudletList);

            CloudSim.startSimulation();

            List<Cloudlet> newList = broker.getCloudletReceivedList();
            printCloudletList(newList);

            CloudSim.stopSimulation();

            Log.printLine("LoadBalancerExample finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }

    private static Datacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList<Host>();
        int mips = 1000;
        List<Pe> peList = new ArrayList<Pe>();
        peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS rating

        int hostId = 0;
        int ram = 2048; // host memory (MB)
        long storage = 1000000; // host storage
        int bw = 10000;

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
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
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
        }
        return broker;
    }

    private static List<Vm> createVM(int userId, int vms) {
        List<Vm> vmsList = new ArrayList<Vm>();

        long size = 10000;
        int ram = 512;
        int mips = 1000;
        long bw = 1000;
        int pesNumber = 1;
        String vmm = "Xen";

        Vm[] vm = new Vm[vms];

        for (int i = 0; i < vms; i++) {
            vm[i] = new Vm(i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            vmsList.add(vm[i]);
        }

        return vmsList;
    }

    private static List<Cloudlet> createCloudlet(int userId, int cloudlets) {
        List<Cloudlet> cloudletList = new ArrayList<Cloudlet>();

        long length = 40000;
        long fileSize = 300;
        long outputSize = 300;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        Cloudlet[] cloudlet = new Cloudlet[cloudlets];

        for (int i = 0; i < cloudlets; i++) {
            cloudlet[i] = new Cloudlet(i, length, 1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet[i].setUserId(userId);
            cloudlet[i].setVmId(i % 5);
            cloudletList.add(cloudlet[i]);
        }

        return cloudletList;
    }

    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
                "Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
                        indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime()) +
                        indent + indent + dft.format(cloudlet.getFinishTime()));
            }
        }
    }
}
