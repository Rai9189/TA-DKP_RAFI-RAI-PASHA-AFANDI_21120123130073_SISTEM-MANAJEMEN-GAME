import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameStoreGUI::new);
    }
}

class Game {
    private String namaGame;
    private String konsol;
    private String genre;
    private String publisher;
    private String tahunRelease;
    private String harga;

    public Game(String namaGame, String konsol, String genre, String publisher, String tahunRelease, String harga) {
        this.namaGame = namaGame;
        this.konsol = konsol;
        this.genre = genre;
        this.publisher = publisher;
        this.tahunRelease = tahunRelease;
        this.harga = harga;
    }

    public String getNamaGame() {
        return namaGame;
    }

    public String getKonsol() {
        return konsol;
    }

    public String getGenre() {
        return genre;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getTahunRelease() {
        return tahunRelease;
    }

    public String getHarga() {
        return harga;
    }

    public String info() {
        return String.format("Nama Game: %s, Konsol: %s, Genre: %s, Publisher: %s, Tahun Release: %s, Harga: %s",
                namaGame, konsol, genre, publisher, tahunRelease, harga);
    }

    public void tambahHarga20Persen() {
        int hargaAsli = Integer.parseInt(harga.replace(".", ""));
        int hargaBaru = (int) (hargaAsli * 1.2);
        this.harga = String.format("%,d", hargaBaru).replace(",", ".");
    }
}

class GameStore {
    private List<Game> daftarGame;

    public GameStore() {
        daftarGame = new ArrayList<>();
    }

    public String gameCsv(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    String namaGame = parts[0].trim();
                    String konsol = parts[1].trim();
                    String genre = parts[2].trim();
                    String publisher = parts[3].trim();
                    String tahunRelease = parts[4].trim();
                    String harga = parts[5].trim();
                    tambahGame(new Game(namaGame, konsol, genre, publisher, tahunRelease, harga));
                }
            }
            return "Game berhasil dimuat dari file Game.csv.";
        } catch (IOException e) {
            return "File tidak ditemukan.";
        }
    }

    public void tambahGame(Game game) {
        daftarGame.add(game);
    }

    public List<Game> cariGame(String query) {
        List<Game> temukanGame = new ArrayList<>();
        for (Game game : daftarGame) {
            if (query.equalsIgnoreCase(game.getNamaGame()) ||
                    query.equalsIgnoreCase(game.getKonsol()) ||
                    query.equalsIgnoreCase(game.getGenre()) ||
                    query.equalsIgnoreCase(game.getPublisher()) ||
                    query.equalsIgnoreCase(game.getTahunRelease())) {
                temukanGame.add(game);
            }
        }
        return temukanGame;
    }

    public List<Game> daftarGameTersedia() {
        return daftarGame;
    }

    public boolean cekGameTersedia(String namaGame, String konsol) {
        for (Game game : daftarGame) {
            if (namaGame.equalsIgnoreCase(game.getNamaGame()) && konsol.equalsIgnoreCase(game.getKonsol())) {
                return true;
            }
        }
        return false;
    }

    public String beliGame(String namaGame, String konsol, String uang) {
        java.util.Iterator<Game> iterator = daftarGame.iterator();
        while (iterator.hasNext()) {
            Game game = iterator.next();
            if (namaGame.equalsIgnoreCase(game.getNamaGame()) && konsol.equalsIgnoreCase(game.getKonsol())) {
                int harga = Integer.parseInt(game.getHarga().replace(".", ""));
                int uangInt = Integer.parseInt(uang.replace(".", ""));
                if (uangInt < harga) {
                    return "Uang Anda tidak cukup untuk membeli game ini.";
                } else if (uangInt == harga) {
                    iterator.remove(); // Use iterator to remove the game
                    return String.format("Game '%s' untuk konsol '%s' telah dibeli. Uang Anda cukup untuk membeli game ini.", namaGame, konsol);
                } else {
                    int kembalian = uangInt - harga;
                    iterator.remove(); // Use iterator to remove the game
                    return String.format("Game '%s' untuk konsol '%s' telah dibeli. Uang Anda cukup untuk membeli game ini, berikut adalah kembalian Anda: %,d", namaGame, konsol, kembalian);
                }
            }
        }
        return String.format("Game '%s' tidak tersedia untuk konsol '%s'.", namaGame, konsol);
    }

    public String jualGame(Game game) {
        game.tambahHarga20Persen();
        tambahGame(game);
        return String.format("Game '%s' telah dijual ke toko dengan harga baru %s.", game.getNamaGame(), game.getHarga());
    }
}

class GameStoreGUI extends JFrame {
    private GameStore store;
    private JTextArea textArea;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public GameStoreGUI() {
        store = new GameStore();
        store.gameCsv("Assets/Game.csv");

        setTitle("Sistem Manajemen Toko Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel openingPanel = new JPanel(new BorderLayout());
        openingPanel.setBackground(Color.DARK_GRAY); // Set background color

        JLabel welcomeLabel = new JLabel("Selamat Datang di Toko Game", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 36)); // Set font type, style, and size
        welcomeLabel.setForeground(Color.WHITE); // Set font color

        JButton proceedButton = new JButton("Masuk");
        proceedButton.setFont(new Font("SansSerif", Font.PLAIN, 20)); // Set font type, style, and size
        proceedButton.setBackground(Color.LIGHT_GRAY);
        proceedButton.setForeground(Color.BLACK); // Set font color
        proceedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "menu");
            }
        });

        openingPanel.add(welcomeLabel, BorderLayout.CENTER);
        openingPanel.add(proceedButton, BorderLayout.SOUTH);

        // Add menu panel
        JPanel menuPanel = new JPanel(new GridLayout(5, 1));
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.add(scrollPane, BorderLayout.CENTER);
        gamePanel.add(menuPanel, BorderLayout.WEST);

        JButton listButton = new JButton("Daftar Game");
        listButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
                List<Game> daftarGame = store.daftarGameTersedia();
                if (!daftarGame.isEmpty()) {
                    textArea.append("Daftar game yang tersedia:\n\n");
                    for (Game game : daftarGame) {
                        textArea.append(game.info() + "\n\n"); // Adding newline for spacing
                    }
                } else {
                    textArea.append("Tidak ada game yang tersedia di toko.");
                }
            }
        });

        JButton searchButton = new JButton("Cari Game");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = JOptionPane.showInputDialog("Masukkan Judul, Konsol, Genre, Publisher, atau Tahun Release untuk mencari:");
                if (query != null && !query.trim().isEmpty()) {
                    List<Game> foundGame = store.cariGame(query.trim());
                    textArea.setText("");
                    if (!foundGame.isEmpty()) {
                        textArea.append("Hasil pencarian:\n\n");
                        for (Game game : foundGame) {
                            textArea.append(game.info() + "\n\n"); // Adding newline for spacing
                        }
                    } else {
                        textArea.append("Game tidak ditemukan.");
                    }
                }
            }
        });

        JButton buyButton = new JButton("Beli Game");
        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel buyPanel = new JPanel(new GridLayout(0, 2));
                JTextField namaField = new JTextField();
                JTextField konsolField = new JTextField();
                JTextField uangField = new JTextField();
                buyPanel.add(new JLabel("Nama Game:"));
                buyPanel.add(namaField);
                buyPanel.add(new JLabel("Konsol:"));
                buyPanel.add(konsolField);
                buyPanel.add(new JLabel("Uang Anda:"));
                buyPanel.add(uangField);

                int result = JOptionPane.showConfirmDialog(null, buyPanel, "Beli Game", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    String namaGame = namaField.getText();
                    String konsol = konsolField.getText();
                    String uang = uangField.getText();
                    textArea.setText(store.beliGame(namaGame, konsol, uang));
                }
            }
        });

        JButton sellButton = new JButton("Jual Game");
        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel sellPanel = new JPanel(new GridLayout(0, 2));
                JTextField namaField = new JTextField();
                JTextField konsolField = new JTextField();
                JTextField genreField = new JTextField();
                JTextField publisherField = new JTextField();
                JTextField tahunField = new JTextField();
                JTextField hargaField = new JTextField();
                sellPanel.add(new JLabel("Nama Game:"));
                sellPanel.add(namaField);
                sellPanel.add(new JLabel("Konsol:"));
                sellPanel.add(konsolField);
                sellPanel.add(new JLabel("Genre:"));
                sellPanel.add(genreField);
                sellPanel.add(new JLabel("Publisher:"));
                sellPanel.add(publisherField);
                sellPanel.add(new JLabel("Tahun Release:"));
                sellPanel.add(tahunField);
                sellPanel.add(new JLabel("Harga:"));
                sellPanel.add(hargaField);

                int result = JOptionPane.showConfirmDialog(null, sellPanel, "Jual Game", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    String namaGame = namaField.getText();
                    String konsol = konsolField.getText();
                    String genre = genreField.getText();
                    String publisher = publisherField.getText();
                    String tahunRelease = tahunField.getText();
                    String harga = hargaField.getText();
                    Game game = new Game(namaGame, konsol, genre, publisher, tahunRelease, harga);
                    int confirmResult = JOptionPane.showConfirmDialog(null, "Apakah Toko Game ingin membeli game ini?", "Konfirmasi Jual Game", JOptionPane.YES_NO_OPTION);
                    if (confirmResult == JOptionPane.YES_OPTION) {
                        textArea.setText(store.jualGame(game));
                    } else {
                        textArea.setText("Maaf, Toko Game menolak game Anda.");
                    }
                }
            }
        });

        JButton exitButton = new JButton("Keluar");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menuPanel.add(listButton);
        menuPanel.add(searchButton);
        menuPanel.add(buyButton);
        menuPanel.add(sellButton);
        menuPanel.add(exitButton);

        gamePanel.setBackground(Color.LIGHT_GRAY);
        Font font = new Font("Arial", Font.ITALIC, 14);
        textArea.setFont(font);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);

        mainPanel.add(openingPanel, "opening");
        mainPanel.add(gamePanel, "menu");

        add(mainPanel);
        setVisible(true);

        cardLayout.show(mainPanel, "opening");
    }
}
