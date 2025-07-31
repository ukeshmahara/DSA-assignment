import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class TicketBookingSystem extends JFrame {
    private static final int ROWS = 5;
    private static final int COLS = 10;

    private Map<String, Seat> seats = new ConcurrentHashMap<>();
    private Queue<BookingRequest> bookingQueue = new ConcurrentLinkedQueue<>();

    private JPanel seatPanel;
    private JTextArea queueArea;
    private JButton bookSeatBtn, processBookingsBtn;
    private JComboBox<String> concurrencyCombo;

    private ExecutorService executor = Executors.newFixedThreadPool(5);

    // Concurrency control mode: "Optimistic" or "Pessimistic"
    private volatile String concurrencyMode = "Optimistic";

    public TicketBookingSystem() {
        setTitle("Online Ticket Booking System - Concurrency Control");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 500);
        setLayout(new BorderLayout());

        initSeats();
        initGUI();
    }

    private void initSeats() {
        char rowChar = 'A';
        for (int i = 0; i < ROWS; i++) {
            for (int j = 1; j <= COLS; j++) {
                String seatId = "" + (char)(rowChar + i) + j;
                seats.put(seatId, new Seat(seatId));
            }
        }
    }

    private void initGUI() {
        seatPanel = new JPanel(new GridLayout(ROWS, COLS, 5, 5));
        updateSeatButtons();

        JPanel rightPanel = new JPanel(new BorderLayout());

        queueArea = new JTextArea(10, 20);
        queueArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(queueArea);

        bookSeatBtn = new JButton("Book Random Seat");
        bookSeatBtn.addActionListener(e -> addRandomBookingRequest());

        processBookingsBtn = new JButton("Process Bookings");
        processBookingsBtn.addActionListener(e -> processBookingRequests());

        concurrencyCombo = new JComboBox<>(new String[]{"Optimistic", "Pessimistic"});
        concurrencyCombo.addActionListener(e -> concurrencyMode = (String) concurrencyCombo.getSelectedItem());

        JPanel controls = new JPanel(new GridLayout(0, 1, 10, 10));
        controls.add(new JLabel("Select Concurrency Control:"));
        controls.add(concurrencyCombo);
        controls.add(bookSeatBtn);
        controls.add(processBookingsBtn);

        rightPanel.add(controls, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        add(seatPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    private void updateSeatButtons() {
        seatPanel.removeAll();
        for (int i = 0; i < ROWS; i++) {
            char rowChar = (char) ('A' + i);
            for (int j = 1; j <= COLS; j++) {
                String seatId = "" + rowChar + j;
                Seat seat = seats.get(seatId);
                JButton btn = new JButton(seatId);
                btn.setEnabled(false);
                btn.setBackground(seat.isBooked() ? Color.RED : Color.GREEN);
                seatPanel.add(btn);
            }
        }
        seatPanel.revalidate();
        seatPanel.repaint();
    }

    private void addRandomBookingRequest() {
        List<String> availableSeats = new ArrayList<>();
        for (Seat seat : seats.values()) {
            if (!seat.isBooked() && !isSeatInQueue(seat.seatId)) {
                availableSeats.add(seat.seatId);
            }
        }
        if (availableSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No more seats available to book!");
            return;
        }
        String seatToBook = availableSeats.get(new Random().nextInt(availableSeats.size()));
        BookingRequest request = new BookingRequest("User" + (bookingQueue.size() + 1), seatToBook);
        bookingQueue.offer(request);
        queueArea.append("Added booking request: " + request + "\n");
    }

    private boolean isSeatInQueue(String seatId) {
        for (BookingRequest req : bookingQueue) {
            if (req.seatId.equals(seatId)) return true;
        }
        return false;
    }

    private void processBookingRequests() {
        if (bookingQueue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No pending booking requests.");
            return;
        }

        processBookingsBtn.setEnabled(false);
        bookSeatBtn.setEnabled(false);

        int tasks = bookingQueue.size();
        CountDownLatch latch = new CountDownLatch(tasks);

        while (!bookingQueue.isEmpty()) {
            BookingRequest request = bookingQueue.poll();
            executor.submit(() -> {
                boolean success;
                if ("Optimistic".equals(concurrencyMode)) {
                    success = bookSeatOptimistic(request);
                } else {
                    success = bookSeatPessimistic(request);
                }
                SwingUtilities.invokeLater(() -> {
                    queueArea.append(request + (success ? " -> Booking CONFIRMED\n" : " -> Booking FAILED (Conflict)\n"));
                    updateSeatButtons();
                });
                latch.countDown();
            });
        }

        new Thread(() -> {
            try {
                latch.await();
            } catch (InterruptedException ignored) {}
            SwingUtilities.invokeLater(() -> {
                processBookingsBtn.setEnabled(true);
                bookSeatBtn.setEnabled(true);
            });
        }).start();
    }

    private boolean bookSeatOptimistic(BookingRequest req) {
        int retries = 3;
        while (retries-- > 0) {
            Seat seat = seats.get(req.seatId);
            if (seat.isBooked()) return false;
            synchronized (seat) {
                if (!seat.isBooked()) {
                    seat.setBooked(true);
                    return true;
                }
            }
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }
        return false;
    }

    private boolean bookSeatPessimistic(BookingRequest req) {
        Seat seat = seats.get(req.seatId);
        boolean acquired = false;
        try {
            acquired = seat.lock.tryLock(200, TimeUnit.MILLISECONDS);
            if (!acquired) return false;
            if (seat.isBooked()) return false;
            seat.setBooked(true);
            return true;
        } catch (InterruptedException e) {
            return false;
        } finally {
            if (acquired) seat.lock.unlock();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TicketBookingSystem frame = new TicketBookingSystem();
            frame.setVisible(true);
        });
    }

    static class Seat {
        final String seatId;
        private volatile boolean booked = false;
        final ReentrantLock lock = new ReentrantLock();

        Seat(String seatId) {
            this.seatId = seatId;
        }

        boolean isBooked() {
            return booked;
        }

        void setBooked(boolean b) {
            this.booked = b;
        }
    }

    static class BookingRequest {
        final String userName;
        final String seatId;

        BookingRequest(String userName, String seatId) {
            this.userName = userName;
            this.seatId = seatId;
        }

        @Override
        public String toString() {
            return userName + " booking seat " + seatId;
        }
    }
}
// 5.b answer