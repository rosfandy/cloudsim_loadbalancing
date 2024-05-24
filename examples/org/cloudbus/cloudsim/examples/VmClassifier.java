package org.cloudbus.cloudsim.examples;

import org.cloudbus.cloudsim.Vm;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VmClassifier {
    private double lvmThreshold;  // Lower threshold for Low-loaded VMs
    private double ovmThreshold;  // Upper threshold for Overloaded VMs

    private Map<Integer, Vm> lowLoadedVMs;
    private Map<Integer, Vm> balancedVMs;
    private Map<Integer, Vm> overloadedVMs;

    public VmClassifier(double lvmThreshold, double ovmThreshold) {
        this.lvmThreshold = lvmThreshold;
        this.ovmThreshold = ovmThreshold;

        lowLoadedVMs = new HashMap<>();
        balancedVMs = new HashMap<>();
        overloadedVMs = new HashMap<>();
    }

    public void classifyVMs(List<Vm> vmList) {
        for (Vm vm : vmList) {
            double totalMips = vm.getMips() * vm.getNumberOfPes();
            if (totalMips < lvmThreshold) {
                lowLoadedVMs.put(vm.getId(), vm);
            } else if (totalMips > ovmThreshold) {
                overloadedVMs.put(vm.getId(), vm);
            } else {
                balancedVMs.put(vm.getId(), vm);
            }
        }
    }

    public Map<Integer, Vm> getLowLoadedVMs() {
        return lowLoadedVMs;
    }

    public Map<Integer, Vm> getBalancedVMs() {
        return balancedVMs;
    }

    public Map<Integer, Vm> getOverloadedVMs() {
        return overloadedVMs;
    }
}
