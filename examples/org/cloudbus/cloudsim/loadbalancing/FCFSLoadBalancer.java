package org.cloudbus.cloudsim.loadbalancing;

import java.util.List;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.Log;

public class FCFSLoadBalancer implements LoadBalancingPolicy {
    private int currentIndex = 0;

    @Override
    public Vm begin(List<Vm> vmList) {
        if(vmList.isEmpty()) {
            Log.printLine("Tidak ada VM yang tersedia untuk penjadwalan.");
            return null;
        }
        if (currentIndex >= vmList.size()) {
            // Atur ulang currentIndex ke 0 jika melebihi batas, meniru perilaku FCFS
            // tanpa berputar seperti Round Robin
            currentIndex = 0; // Pertimbangkan untuk menunggu VM tersedia di skenario dunia nyata
        }
        Vm vm = vmList.get(currentIndex);
        currentIndex++; // Pindah ke VM berikutnya untuk cloudlet selanjutnya
        Log.printLine("VM yang ditugaskan: " + vm.getId() + " untuk Cloudlet berikutnya dengan cara FCFS.");
        return vm;
    }

    @Override
    public String getLoadBalancingPolicy() {
        return "FCFS";
    }
}
