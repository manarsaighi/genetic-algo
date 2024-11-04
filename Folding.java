import java.util.*;

class Folding {
    private String sequence;
    private List<String> directions;

    public Folding(String sequence, List<String> directions) {
        this.sequence = sequence;
        this.directions = directions;
    }

    public List<int[]> calculatePositions() {
        List<int[]> positions = new ArrayList<>();
        int[] currentPosition = {0, 0};
        positions.add(currentPosition.clone());

        for (String direction : directions) {
            switch (direction) {
                case "U":
                    currentPosition[1] += 1;
                    break;
                case "D":
                    currentPosition[1] -= 1;
                    break;
                case "L":
                    currentPosition[0] -= 1;
                    break;
                case "R":
                    currentPosition[0] += 1;
                    break;
            }
            positions.add(currentPosition.clone());
        }

        return positions;
    }

    public float calculateFitness() {
        List<int[]> positions = calculatePositions();
        int hhBonds = 0;
        int overlaps = 0;
        Map<String, Integer> positionMap = new HashMap<>();

        for (int i = 0; i < sequence.length(); i++) {
            if (sequence.charAt(i) == 'H') {
                for (int j = i + 2; j < sequence.length(); j++) {
                    if (sequence.charAt(j) == 'H') {
                        int[] x = positions.get(i);
                        int[] y = positions.get(j);
                        double distance = Math.sqrt(Math.pow(x[0] - y[0], 2) + Math.pow(x[1] - y[1], 2));
                        if (Math.abs(distance - 1) < 0.01) {
                            hhBonds++;
                        }
                    }
                }
            }
        }

        for (int[] pos : positions) {
            int x = pos[0];
            int y = pos[1];
            String key = x + "," + y;
            positionMap.put(key, positionMap.getOrDefault(key, 0) + 1);
        }

        for (int count : positionMap.values()) {
            if (count > 1) {
                overlaps += count - 1;
            }
        }

        return (float) hhBonds / (overlaps + 1);
    }

    public int calculateHHBonds() {
        List<int[]> positions = calculatePositions();
        int hhBonds = 0;

        for (int i = 0; i < sequence.length(); i++) {
            if (sequence.charAt(i) == 'H') {
                for (int j = i + 2; j < sequence.length(); j++) {
                    if (sequence.charAt(j) == 'H') {
                        int[] x = positions.get(i);
                        int[] y = positions.get(j);
                        double distance = Math.sqrt(Math.pow(x[0] - y[0], 2) + Math.pow(x[1] - y[1], 2));
                        if (Math.abs(distance - 1) < 0.01) {
                            hhBonds++;
                        }
                    }
                }
            }
        }

        return hhBonds;
    }

    public int calculateOverlaps() {
        List<int[]> positions = calculatePositions();
        Map<String, Integer> positionMap = new HashMap<>();

        for (int[] pos : positions) {
            int x = pos[0];
            int y = pos[1];
            String key = x + "," + y;
            positionMap.put(key, positionMap.getOrDefault(key, 0) + 1);
        }

        int overlaps = 0;
        for (int count : positionMap.values()) {
            if (count > 1) {
                overlaps += count - 1;
            }
        }

        return overlaps;
    }

    public static void main(String[] args) {
        String sequence = "HPHHPPPH";
        List<String> directions = Arrays.asList("U", "L", "D", "L", "D", "R", "U");
        Folding folding = new Folding(sequence, directions);

        float fitness = folding.calculateFitness();
        System.out.println("Fitness: " + fitness);
        System.out.println("H/H Bonds: " + folding.calculateHHBonds());
        System.out.println("Overlaps: " + folding.calculateOverlaps());
    }
}
