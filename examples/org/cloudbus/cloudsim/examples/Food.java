package org.cloudbus.cloudsim.examples;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class Food extends Vm {
    private double capacity;
    private double timeProcessing;
    private double fitness;
    private List<Cloudlet> assignedCloudlets;  // List to track cloudlets assigned to this VM

    // Constructor that clones a Vm object
    public Food(Vm vm) {
        super(vm.getId(), vm.getUserId(), vm.getMips(), vm.getNumberOfPes(), vm.getRam(), vm.getBw(), vm.getSize(), vm.getVmm(), vm.getCloudletScheduler());
        this.assignedCloudlets = new ArrayList<>();
    }
    
    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public void setTimeProcessing(double timeProcessing) {
        this.timeProcessing = timeProcessing;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
    
    public void setCloudlet(Cloudlet cloudlet) {
        this.assignedCloudlets.add(cloudlet);
    }
    
    public double getCapacity() {
        return this.capacity;
    }

    public double getFitness() {
        return this.fitness;
    }

    public double getTimeProcessing() {
        return this.timeProcessing;
    }

   
    public List<Cloudlet> getAssignedCloudlets() {
        return this.assignedCloudlets;
    }
    
    public Cloudlet getCloudlet() {
        if (!this.assignedCloudlets.isEmpty()) {
            return this.assignedCloudlets.get(0); // Returns the first cloudlet, modify as needed
        }
        return null;
    }

    
    public double getTotalTaskLength() {
        return assignedCloudlets.stream().mapToDouble(Cloudlet::getCloudletLength).sum();
    }

    public double getIncomingTaskLength() {
        // This should be defined based on how you track incoming tasks; 
        // Placeholder calculation as an example
        return assignedCloudlets.stream().filter(c -> !c.isFinished()).mapToDouble(Cloudlet::getCloudletLength).sum();
    }

    public static double getAvgProcessingTime(List<Food> foodList) {
        if (foodList == null || foodList.isEmpty()) {
            return 0.0;
        }

        double totalProcessingTime = 0.0;
        for (Food food : foodList) {
            totalProcessingTime += food.getTimeProcessing();
        }
        System.out.println("Average Processing Time: " + foodList.size());

        return totalProcessingTime / foodList.size();
    }

    public void updateVm(Vm vm) {
        this.setId(vm.getId());  // Asumsikan ada setId jika Food adalah subclass dari Vm
        this.setMips(vm.getMips());
        this.setNumberOfPes(vm.getNumberOfPes());
        this.setBw(vm.getBw());
        this.setRam(vm.getRam());
        this.setSize(vm.getSize());
        this.setVmm(vm.getVmm());
        this.setCloudletScheduler(vm.getCloudletScheduler());
    }

}
