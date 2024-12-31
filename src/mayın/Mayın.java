package mayın;

public class Mayın {
	
	
	
	
	import javax.swing.*;
	import java.awt.*;
	import java.awt.event.*;
	import java.util.Random;
	import java.io.*; // En yüksek skoru saklamak için

	// Tahtadaki her hücreyi temsil eden sınıf
	class Hücre {
	    boolean mayınVar;
	    boolean açıldı;
	    int çevredekiMayınSayısı;

	    public Hücre() {
	        this.mayınVar = false;
	        this.açıldı = false;
	        this.çevredekiMayınSayısı = 0;
	    }
	}

	// OyunPenceresi sınıfı, JFrame sınıfından kalıtım alır
	class OyunPenceresi extends JFrame {
	    protected static int BOYUT; // Tahta boyutunu dinamik hale getiriyoruz
	    protected static int MAYINLAR; // Mayın sayısını dinamik hale getiriyoruz
	    protected JButton[][] butonlar; // Tahtadaki butonları tutar
	    protected Hücre[][] hücreler; // Tahtadaki hücreleri tutar
	    protected boolean oyunBitti = false; // Oyun bitti mi?

	    protected Timer timer; // Zamanlayıcı
	    protected int süre = 0; // Oyun süresi
	    protected JLabel skorLabel; // Skor etiketini tutar
	    protected int enYüksekSkor; // En yüksek skoru tutar
	    protected String skorDosyasi = "enYüksekSkor.txt"; // Skorun saklanacağı dosya

	    public OyunPenceresi(int boyut, int mayinlar) {
	        BOYUT = boyut; // Tahta boyutunu ayarla
	        MAYINLAR = mayinlar; // Mayın sayısını ayarla
	        butonlar = new JButton[BOYUT][BOYUT]; // Butonları oluştur
	        hücreler = new Hücre[BOYUT][BOYUT]; // Hücreleri oluştur

	        // Önceki en yüksek skoru oku
	        enYüksekSkoruOku();

	        setLayout(new BorderLayout()); // Tahtayı ve skoru farklı bölümlere yerleştiriyoruz
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setSize(600, 600);

	        // Skor gösterimi için etiket oluşturulur
	        skorLabel = new JLabel("Süre: 0 saniye - En Yüksek Skor: " + enYüksekSkor + " saniye", JLabel.CENTER);
	        add(skorLabel, BorderLayout.NORTH); // Skor etiketini üst kısma ekleriz

	        JPanel tahtaPaneli = new JPanel(new GridLayout(BOYUT, BOYUT)); // Tahta paneli için bir layout oluşturulur
	        add(tahtaPaneli, BorderLayout.CENTER);

	        // Hücreleri başlatır
	        for (int i = 0; i < BOYUT; i++) {
	            for (int j = 0; j < BOYUT; j++) {
	                hücreler[i][j] = new Hücre();
	                butonlar[i][j] = new JButton();
	                butonlar[i][j].addActionListener(new ButonDinleyicisi(i, j));
	                tahtaPaneli.add(butonlar[i][j]);
	            }
	        }

	        // Rastgele mayın yerleştirir
	        yerleştirMayınlar();

	        // Zamanlayıcıyı başlatır
	        timer = new Timer(1000, e -> extracted());
	                timer.start();
	        
	                // Bir başlangıç hücresini aç
	                aç((BOYUT / 2), (BOYUT / 2));
	        
	                setVisible(true);
	            }
	        
	            private void extracted() {
	                if (!oyunBitti) {
	                    süre++;
	                    skorLabel.setText("Süre: " + süre + " saniye - En Yüksek Skor: " + enYüksekSkor + " saniye"); // Skoru günceller
	                }
	            }

	    // Rastgele mayın yerleştiren metot
	    private void yerleştirMayınlar() {
	        Random rand = new Random();
	        int yerlestirilenMayinlar = 0;

	        while (yerlestirilenMayinlar < MAYINLAR) {
	            int x = rand.nextInt(BOYUT);
	            int y = rand.nextInt(BOYUT);
	            if (!hücreler[x][y].mayınVar) {
	                hücreler[x][y].mayınVar = true;
	                yerlestirilenMayinlar++;
	            }
	        }

	        // Çevredeki mayın sayısını hesaplar
	        for (int i = 0; i < BOYUT; i++) {
	            for (int j = 0; j < BOYUT; j++) {
	                if (!hücreler[i][j].mayınVar) {
	                    hücreler[i][j].çevredekiMayınSayısı = cevredekiMayinlarıSay(i, j);
	                }
	            }
	        }
	    }

	    // Çevredeki mayınları sayar
	    private int cevredekiMayinlarıSay(int x, int y) {
	        int sayi = 0;
	        for (int i = -1; i <= 1; i++) {
	            for (int j = -1; j <= 1; j++) {
	                int nx = x + i, ny = y + j;
	                if (nx >= 0 && ny >= 0 && nx < BOYUT && ny < BOYUT && hücreler[nx][ny].mayınVar) {
	                    sayi++;
	                }
	            }
	        }
	        return sayi;
	    }

	    // En yüksek skoru okuyan metot
	    private void enYüksekSkoruOku() {
	        try (BufferedReader reader = new BufferedReader(new FileReader(skorDosyasi))) {
	            enYüksekSkor = Integer.parseInt(reader.readLine());
	        } catch (IOException | NumberFormatException e) {
	            enYüksekSkor = 0; // Eğer dosya okunamazsa veya skor mevcut değilse, en yüksek skoru sıfır yap
	        }
	    }

	    // En yüksek skoru yazan metot
	    private void enYüksekSkoruYaz() {
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(skorDosyasi))) {
	            writer.write(String.valueOf(enYüksekSkor));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    // Buton tıklama dinleyicisi sınıfı
	    private class ButonDinleyicisi implements ActionListener {
	        private int x, y;

	        public ButonDinleyicisi(int x, int y) {
	            this.x = x;
	            this.y = y;
	        }

	        @Override
	        public void actionPerformed(ActionEvent e) {
	            if (!oyunBitti) {
	                aç(x, y);
	            }
	        }
	    }

	    // Hücreyi açar
	    protected void aç(int x, int y) {
	        if (hücreler[x][y].açıldı) {
	            return;
	        }

	        hücreler[x][y].açıldı = true;

	        if (hücreler[x][y].mayınVar) {
	            butonlar[x][y].setText("M");
	            oyunBitti = true;
	            timer.stop();

	            // Skoru kontrol et ve güncelle
	            if (süre < enYüksekSkor || enYüksekSkor == 0) {
	                enYüksekSkor = süre;
	                enYüksekSkoruYaz();
	                JOptionPane.showMessageDialog(this, "Oyun Bitti! Yeni En Yüksek Skor: " + enYüksekSkor + " saniye");
	            } else {
	                JOptionPane.showMessageDialog(this, "Oyun Bitti! Zaman: " + süre + " saniye");
	            }

	            skorLabel.setText("Oyun Bitti! Zaman: " + süre + " saniye - En Yüksek Skor: " + enYüksekSkor + " saniye");
	            return;
	        } else {
	            int cevredekiMayinlar = hücreler[x][y].çevredekiMayınSayısı;
	            butonlar[x][y].setText(cevredekiMayinlar == 0 ? "" : String.valueOf(cevredekiMayinlar));
	            butonlar[x][y].setEnabled(false);

	            if (cevredekiMayinlar == 0) {
	                for (int i = -1; i <= 1; i++) {
	                    for (int j = -1; j <= 1; j++) {
	                        int nx = x + i, ny = y + j;
	                        if (nx >= 0 && ny >= 0 && nx < BOYUT && ny < BOYUT && !hücreler[nx][ny].açıldı) {
	                            aç(nx, ny);
	                        }
	                    }
	                }
	            }
	        }
	    }
	}

	
	
	
}

