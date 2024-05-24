package org.cloudbus.cloudsim.loadbalancing;

import java.util.List;
import org.cloudbus.cloudsim.Vm;

public interface LoadBalancingPolicy {
    Vm begin(List<Vm> vmList);
    String getLoadBalancingPolicy();
}
