package org.cloudbus.cloudsim.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

public class HoneyBeeLB {
    private static int numOfBees = 300;
    private static int employeeBee = 100;
    private static int nsp = 70;
    private static int nep = 30;

    private static List<Cloudlet> cloudletList = new ArrayList<>(); // List to hold cloudlets
    private static double cloudletInput = 0;
    private static double totalTask = 0;

    public static void SubmitTask(List<Cloudlet> list) {
        cloudletList = list;
        for (Cloudlet cl : list) {
            // additional handling if needed
        	totalTask += cl.getCloudletLength();
        	cloudletInput = cl.getCloudletFileSize();
        }        
    }

    public static int Start(List<Vm> vmList, int index) {
        List<Food> foodList = Initialization(vmList, numOfBees, index);
        boolean isBalanced;
        int vmDestId = -1; // Initialize with a default or error value.

//        do {
            foodList = EmployedBeePhase(foodList, employeeBee);
            vmDestId = OnlookerBeePhase(foodList, 3, nsp, nep);

            double avgProcessingTime = calculateAverageProcessingTime(foodList,index);
            double sd = calculateStandardDeviation(foodList, avgProcessingTime);

            isBalanced = sd <= avgProcessingTime;
            
            System.out.println("=============================================================");
            System.out.println("Sd: " + sd + " avgtime: " + avgProcessingTime);
            System.out.println("=============================================================");

            if (isBalanced) {
                System.out.println("System is in a balanced condition.");
//                break;  // Exit the loop if the system is balanced.
            } else {
                System.out.println("System is in a state of imbalance.");
                
//                if(totalTask > )
//                ScoutBeePhase(foodList, vmList);
            }
//        } while (!isBalanced);

        return vmDestId; // Return the vmDestId after loop ends or if balanced.
    }
    

    public static List<Food> Initialization(List<Vm> vmList, int numOfBees, int index) {
        Random random = new Random();
        List<Food> foodList = new ArrayList<>();
        Log.printLine("Total Task: " + totalTask);
        
       
        for (int i = 0; i < 3; i++) {
            int randomIndex = random.nextInt(vmList.size());
            Vm selectedVm = vmList.get(randomIndex);
            Food food = new Food(selectedVm);

            foodList.add(food);
        }

        for (int i=0; i < foodList.size(); i++) {
            Food selectedVm = foodList.get(i);
            double capacity = selectedVm.getNumberOfPes() * selectedVm.getMips() + selectedVm.getBw();

        	Cloudlet cloudlet = cloudletList.get(i);
            double timeVM = cloudlet.getCloudletLength() / capacity;
            
            selectedVm.setCapacity(capacity);
            selectedVm.setTimeProcessing(timeVM);
            selectedVm.setCloudlet(cloudlet);
            double assignTask = cloudletList.get(i).getCloudletLength();

            Log.printLine("Task: " + assignTask);
            Log.printLine("PE: " + selectedVm.getNumberOfPes() + " MIPS: " + selectedVm.getMips() + " BW: " + selectedVm.getBw());
            Log.printLine("X: " + selectedVm.getTimeProcessing());
            Log.printLine("Capacity: " + selectedVm.getCapacity());

        }
        
        return foodList;
    }

    public static List<Food> EmployedBeePhase(List<Food> foodList, int employeeBee) {
        Random random = new Random();
        for (int i = 0; i < employeeBee; i++) {
            int randomIndex = random.nextInt(foodList.size());
            Food selectedFood = foodList.get(randomIndex);

            double totalTaskLength = 0;
            for (Cloudlet cloudlet : cloudletList) {
                if (cloudlet.getVmId() == selectedFood.getId()) {
                    totalTaskLength += cloudlet.getCloudletLength();
                }
            }
            double newFitness = totalTaskLength / selectedFood.getCapacity();
            selectedFood.setFitness(newFitness);
        }
        return foodList;
    }

    public static int OnlookerBeePhase(List<Food> foodList, int m, int nsp, int nep) {
        // Sorting food sources based on their fitness in descending order to choose the top m
        foodList.sort((f1, f2) -> Double.compare(f2.getFitness(), f1.getFitness()));
        List<Food> topMFoods = new ArrayList<>(foodList.subList(0, m));
        Random random = new Random();

        // nsp number of employed bees are sent to the top m food sources
        for (int i = 0; i < nsp; i++) {
            Food food = topMFoods.get(random.nextInt(m));
            NeighborhoodSearch(food, true); // True to indicate high priority search
        }

        // nep number of employed bees are sent to all food sources
        for (int i = 0; i < nep; i++) {
            Food food = foodList.get(random.nextInt(foodList.size()));
            NeighborhoodSearch(food, false); // False to indicate normal priority search
        }

        // Recalculate the fitness of all food sources after the neighborhood search
        for (Food food : foodList) {
            double totalTaskLength = getTotalTaskLength(food); // Calculate total task length for the VM
            double inlength = food.getIncomingTaskLength(); // Length of the new task
            double vmCapacity = food.getCapacity(); // Get the current capacity of the VM
            double newFitness = (totalTaskLength + inlength) / vmCapacity;
            food.setFitness(newFitness);
        }

        // After updating the fitness, sort again and choose the best VM for the new task
        foodList.sort((f1, f2) -> Double.compare(f2.getFitness(), f1.getFitness()));
        Food bestFood = foodList.get(0); // The best VM has the highest fitness
//        System.out.println("Best Vm: " + bestFood.getId());

        return bestFood.getId();
    }

    // Helper function to calculate the total task length assigned to a VM
    private static double getTotalTaskLength(Food food) {
        double totalLength = 0;
        for (Cloudlet cloudlet : food.getAssignedCloudlets()) {
            totalLength += cloudlet.getCloudletLength();
        }
        return totalLength;
    }

    public static void ScoutBeePhase(List<Food> foodList, List<Vm> vmList) {
        Random random = new Random();
        for (Food food : foodList) {
            if (food.getCloudlet() == null) {
                int randomIndex = random.nextInt(vmList.size());
                Vm newVm = vmList.get(randomIndex);
                food.updateVm(newVm);

                double newFitness = calculateFitnessBasedOnCapacity(newVm);
                food.setFitness(newFitness);
            }
        }
    }

    public static double calculateFitnessBasedOnCapacity(Vm vm) {
        double capacity = vm.getNumberOfPes() * vm.getMips() + vm.getBw();
        return capacity;
    }

    public static double calculateAverageProcessingTime(List<Food> foodList, int index) {
    	double totalProcessingTime = 0;
    	for(Food food : foodList) {
             totalProcessingTime += food.getTimeProcessing();
    	}
        return totalProcessingTime / foodList.size();
    }

    public static double calculateStandardDeviation(List<Food> foodList, double avgProcessingTime) {
        double variance = foodList.stream()
                                  .mapToDouble(f -> Math.pow(f.getTimeProcessing() - avgProcessingTime, 2))
                                  .average().orElse(0.0);
        return Math.sqrt(variance);
    }
    
    private static void NeighborhoodSearch(Food food, boolean isHighPriority) {
        Random random = new Random();
        double originalCapacity = food.getCapacity();
        List<Cloudlet> originalCloudlets = new ArrayList<>(food.getAssignedCloudlets());

        // Simulate a change in VM capacity, either increasing or decreasing slightly based on priority
        double factor = isHighPriority ? 1.1 : 0.9;  // Boost or reduce capacity by 10%
        food.setCapacity(originalCapacity * factor);

        // Recalculate fitness after the change
        double totalTaskLength = getTotalTaskLength(food);
        double newFitness = totalTaskLength / food.getCapacity();

        // Determine if the new configuration is better
        if (newFitness <= food.getFitness()) {
            // If not better, revert to the original configuration
            food.setCapacity(originalCapacity);
            food.getAssignedCloudlets().clear();
            food.getAssignedCloudlets().addAll(originalCloudlets);
        } else {
            // If better, update the fitness to the new calculated value
            food.setFitness(newFitness);
        }
    }


}
