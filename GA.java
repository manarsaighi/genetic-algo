import java.util.*;
import java.io.*;

public class GA {
    private static final int POPULATION_SIZE = 100;
    private static final int NUM_GENERATIONS = 100;
    private static final String SEQUENCE = "HPHHPPPH";
    private static final String[] DIRECTIONS = {"U", "D", "L", "R"};

    // Store fitness data of generations
    private static List<Double> bestFit = new ArrayList<>();
    private static List<Double> averageFit = new ArrayList<>();
    private static Folding overallBestCandidate = null; // Track the overall best candidate

    public static void main(String[] args) {
        // Init population
        List<Folding> population = generateCandidates(POPULATION_SIZE);

        // Create or open log file
        try (PrintWriter logWriter = new PrintWriter(new FileWriter("fitness_log.csv"))) {
            // Write header to log file
            logWriter.println("Generation,Average Fitness,Best Fitness,Overall Best Fitness,H/H Bonds,Overlaps");

            // Evolution
            for (int generation = 0; generation < NUM_GENERATIONS; generation++) {
                // Fitness
                double totalFitness = 0.0;
                Folding bestCandidateGen = null; // Track the best candidate in the current generation
                double bestFitnessGen = 0.0;

                for (Folding candidate : population) {
                    double fitness = candidate.calculateFitness();
                    totalFitness += fitness;

                    // Update best candidate of the current generation
                    if (fitness > bestFitnessGen) {
                        bestFitnessGen = fitness;
                        bestCandidateGen = candidate;
                    }
                }

                // Update overall best candidate
                if (overallBestCandidate == null || bestFitnessGen > overallBestCandidate.calculateFitness()) {
                    overallBestCandidate = bestCandidateGen;
                }

                // Store best and average fitness
                double averageFitness = totalFitness / population.size();
                bestFit.add(bestFitnessGen);
                averageFit.add(averageFitness);

                // Log data for the current generation
                int hhBonds = (overallBestCandidate != null) ? overallBestCandidate.calculateHHBonds() : 0;
                int overlaps = (overallBestCandidate != null) ? overallBestCandidate.calculateOverlaps() : 0;
                logWriter.printf("%d,%.2f,%.2f,%.2f,%d,%d%n",
                        generation + 1, averageFitness, bestFitnessGen,
                        overallBestCandidate.calculateFitness(), hhBonds, overlaps);

                // Print each generation
                System.out.printf("\nGeneration %d \nBest Fitness: %.2f, \nAverage Fitness: %.2f%n",
                        generation + 1, bestFitnessGen, averageFitness);
                System.out.printf("Overall Best Fitness: %.2f, H/H Bonds: %d, Overlaps: %d%n",
                        overallBestCandidate != null ? overallBestCandidate.calculateFitness() : 0,
                        hhBonds, overlaps);

                // Select candidates for the next generation
                List<Folding> selectedCandidates = fitnessProportionateSelection(population);

                // Replace the previous population with the new population
                population = selectedCandidates;
            }

            // Save the fitness data
            saveFitnessData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Folding> generateCandidates(int POPULATION_SIZE) {
        List<Folding> candidates = new ArrayList<>();
        Random random = new Random();

        // Loop to generate the 100 candidates
        for (int i = 0; i < POPULATION_SIZE; i++) {
            List<String> directions = new ArrayList<>();
            for (int j = 0; j < SEQUENCE.length() - 1; j++) {
                directions.add(DIRECTIONS[random.nextInt(DIRECTIONS.length)]);
            }
            Folding folding = new Folding(SEQUENCE, directions);
            // Add to candidate list
            candidates.add(folding);
        }

        return candidates;
    }

    public static List<Folding> fitnessProportionateSelection(List<Folding> candidates) {
        // Fitness of all candidates
        double totalFitness = 0.0;
        // Calculate fitness of all candidates and add together for total fitness
        for (Folding candidate : candidates) {
            totalFitness += candidate.calculateFitness();
        }

        // Probability of being chosen has to be proportionate to fitness
        List<Double> selectionProbabilities = new ArrayList<>();
        for (Folding candidate : candidates) {
            // Probability is candidate fitness divided by the total fitness
            double probability = candidate.calculateFitness() / totalFitness;
            selectionProbabilities.add(probability);
        }

        List<Folding> selectedCandidates = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            // Number from 0.0 to 1.0
            double r = random.nextDouble();
            double probability = 0.0;
            for (int j = 0; j < candidates.size(); j++) {
                probability += selectionProbabilities.get(j);
                if (r <= probability) {
                    selectedCandidates.add(candidates.get(j));
                    break;
                }
            }
        }

        return selectedCandidates;
    }

    public static double getBestFitness(List<Folding> population) {
        double bestFitness = 0.0;
        for (Folding candidate : population) {
            double fitness = candidate.calculateFitness();
            if (fitness > bestFitness) {
                bestFitness = fitness;
            }
        }
        return bestFitness;
    }

    public static double getAverageFitness(List<Folding> population) {
        double totalFitness = 0.0;
        for (Folding candidate : population) {
            totalFitness += candidate.calculateFitness();
        }
        return totalFitness / population.size();
    }

    public static void saveFitnessData() {
        try (PrintWriter writer = new PrintWriter(new File("fitness_data.csv"))) {
            writer.println("Generation,Best Fitness,Average Fitness");
            for (int i = 0; i < NUM_GENERATIONS; i++) {
                writer.printf("%d,%.2f,%.2f%n", i + 1, bestFit.get(i), averageFit.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
