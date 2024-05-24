package org.cloudbus.cloudsim.loadbalancing;

import java.util.List;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.Log;

public class RoundRobinLoadBalancer implements LoadBalancingPolicy {
    private int currentIndex = 0;

    @Override
    public Vm begin(List<Vm> vmList) {
        Vm vm = vmList.get(currentIndex);
        currentIndex = (currentIndex + 1) % vmList.size();
        Log.printLine("Next VM: " + vm.getId());
        return vm;
    }
    public String getLoadBalancingPolicy() {
        return "RoundRobin";
    }
}
