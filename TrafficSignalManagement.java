import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;

public class TrafficSignalManagement extends JFrame {

    // Traffic light states
    enum SignalState { RED, GREEN, YELLOW }

    // Vehicle class
    static class Vehicle {
        String id;
        boolean emergency;

        Vehicle(String id, boolean emergency) {
            this.id = id;
            this.emergency = emergency;
        }

        @Override
        public String toString() {
            return (emergency ? "[EMERGENCY] " : "") + id;
        }
    }

    private static final int SIGNAL_CHANGE_INTERVAL_MS = 5000;
    private static final int VEHICLE_PROCESS_INTERVAL_MS = 1000;

    // Queues for vehicles
    private final Queue<Vehicle> vehicleQueue = new ConcurrentLinkedQueue<>();
    private final PriorityBlockingQueue<Vehicle> emergencyQueue = new PriorityBlockingQueue<>(10, Comparator.comparing(v -> !v.emergency));

    // GUI Components
    private final JTextArea queueArea = new JTextArea(10, 20);
    private final JLabel signalLabel = new JLabel("Signal: RED", SwingConstants.CENTER);
    private final JButton btnChangeSignal = new JButton("Change Signal");
    private final JButton btnAddVehicle = new JButton("Add Vehicle");
    private final JButton btnEmergencyMode = new JButton("Enable Emergency Mode");

    private volatile SignalState currentSignal = SignalState.RED;
    private volatile boolean emergencyMode = false;
    private volatile boolean running = true;

    // Thread Executors
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    // Vehicle counter
    private int vehicleCount = 0;

    public TrafficSignalManagement() {
        setTitle("Traffic Signal Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Setup GUI
        queueArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(queueArea);

        JPanel controlPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        controlPanel.add(btnChangeSignal);
        controlPanel.add(btnAddVehicle);
        controlPanel.add(btnEmergencyMode);

        signalLabel.setFont(new Font("Arial", Font.BOLD, 24));
        signalLabel.setOpaque(true);
        updateSignalColor();

        add(signalLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);

        // Button actions
        btnChangeSignal.addActionListener(e -> changeSignalManually());
        btnAddVehicle.addActionListener(e -> addVehicleContinuously());
        btnEmergencyMode.addActionListener(e -> toggleEmergencyMode());

        // Start threads
        startTrafficLightThread();
        startVehicleQueueThread();
    }

    private void updateSignalColor() {
        switch (currentSignal) {
            case RED -> signalLabel.setBackground(Color.RED);
            case GREEN -> signalLabel.setBackground(Color.GREEN);
            case YELLOW -> signalLabel.setBackground(Color.YELLOW);
        }
        signalLabel.setText("Signal: " + currentSignal);
    }

    // Manual signal change for testing
    private void changeSignalManually() {
        currentSignal = switch (currentSignal) {
            case RED -> SignalState.GREEN;
            case GREEN -> SignalState.YELLOW;
            case YELLOW -> SignalState.RED;
        };
        updateSignalColor();
    }

    // Continuously add vehicles in a separate thread
    private void addVehicleContinuously() {
        executor.submit(() -> {
            Random rand = new Random();
            while (running) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ignored) {}
                boolean emergency = rand.nextInt(10) == 0; // 10% chance emergency
                String id = "V" + (++vehicleCount);
                Vehicle vehicle = new Vehicle(id, emergency);
                if (emergencyMode && emergency) {
                    emergencyQueue.offer(vehicle);
                    appendQueueText("Added EMERGENCY vehicle: " + vehicle + "\n");
                } else {
                    vehicleQueue.offer(vehicle);
                    appendQueueText("Added vehicle: " + vehicle + "\n");
                }
            }
        });
    }

    private void toggleEmergencyMode() {
        emergencyMode = !emergencyMode;
        String msg = emergencyMode ? "Emergency Mode Enabled" : "Emergency Mode Disabled";
        JOptionPane.showMessageDialog(this, msg);
        btnEmergencyMode.setText(emergencyMode ? "Disable Emergency Mode" : "Enable Emergency Mode");
    }

    // Thread to change traffic light automatically every interval
    private void startTrafficLightThread() {
        executor.submit(() -> {
            while (running) {
                try {
                    Thread.sleep(SIGNAL_CHANGE_INTERVAL_MS);
                } catch (InterruptedException ignored) {}

                currentSignal = switch (currentSignal) {
                    case RED -> SignalState.GREEN;
                    case GREEN -> SignalState.YELLOW;
                    case YELLOW -> SignalState.RED;
                };

                SwingUtilities.invokeLater(this::updateSignalColor);
            }
        });
    }

    // Thread to process vehicle queues according to signal and priority
    private void startVehicleQueueThread() {
        executor.submit(() -> {
            while (running) {
                try {
                    Thread.sleep(VEHICLE_PROCESS_INTERVAL_MS);
                } catch (InterruptedException ignored) {}

                if (currentSignal != SignalState.GREEN) {
                    continue; // vehicles move only on green light
                }

                Vehicle vehicleToProcess = null;
                if (emergencyMode && !emergencyQueue.isEmpty()) {
                    vehicleToProcess = emergencyQueue.poll();
                } else if (!vehicleQueue.isEmpty()) {
                    vehicleToProcess = vehicleQueue.poll();
                }

                if (vehicleToProcess != null) {
                    String msg = "Vehicle passed: " + vehicleToProcess + "\n";
                    appendQueueText(msg);
                }
            }
        });
    }

    private void appendQueueText(String text) {
        SwingUtilities.invokeLater(() -> queueArea.append(text));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TrafficSignalManagement frame = new TrafficSignalManagement();
            frame.setVisible(true);
        });
    }
}
// 6.a answer
