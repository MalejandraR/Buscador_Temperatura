package ui;

import com.toedter.calendar.JDateChooser;
import servicios.TemperaturaServicio;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

public class FrmTemperaturas extends JFrame {

    private final TemperaturaServicio servicio;

    // Controles
    private final JComboBox<String> cboCiudad = new JComboBox<>();
    private final JDateChooser dcDesde = new JDateChooser();
    private final JDateChooser dcHasta = new JDateChooser();
    private final JButton btnGraficar = new JButton("Graficar");

    private final JTabbedPane tabs = new JTabbedPane();
    private final JPanel pnlGrafica = new JPanel(new BorderLayout());

    // Estadísticas
    private final JDateChooser dcDia = new JDateChooser();
    private final JButton btnEstad = new JButton("Calcular");
    private final JLabel lblMas = new JLabel("—");
    private final JLabel lblMenos = new JLabel("—");

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public FrmTemperaturas(TemperaturaServicio servicio) {
        super("Buscador de Temperaturas");
        this.servicio = servicio;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        configurarTopBar();
        configurarTabs();
        setVisible(true);

        // Carga inicial
        onGraficar();
    }

    private void configurarTopBar() {
        JPanel top = new JPanel(new GridBagLayout());
        top.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.gridy = 0; c.anchor = GridBagConstraints.WEST;

        // Ciudad
        c.gridx = 0; top.add(new JLabel("Ciudad:"), c);
        servicio.ciudades().forEach(cboCiudad::addItem); // incluye "Todas"
        c.gridx = 1; top.add(cboCiudad, c);

        // Fechas
        c.gridx = 2; top.add(new JLabel("Desde:"), c);
        dcDesde.setDateFormatString("dd/MM/yyyy");
        dcHasta.setDateFormatString("dd/MM/yyyy");
        dcDia.setDateFormatString("dd/MM/yyyy");

        dcDesde.setDate(utilDate(LocalDate.of(2024,1,1)));
        dcHasta.setDate(utilDate(LocalDate.of(2024,1,31)));
        dcDia.setDate(utilDate(LocalDate.of(2024,1,1)));

        c.gridx = 3; top.add(dcDesde, c);
        c.gridx = 4; top.add(new JLabel("Hasta:"), c);
        c.gridx = 5; top.add(dcHasta, c);

        c.gridx = 6; top.add(btnGraficar, c);

        btnGraficar.addActionListener(e -> onGraficar());
        add(top, BorderLayout.NORTH);
    }

    private void configurarTabs() {
        tabs.addTab("Gráfica", pnlGrafica);

        JPanel est = new JPanel(new GridBagLayout());
        est.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0; est.add(new JLabel("Fecha:"), c);
        c.gridx = 1; est.add(dcDia, c);
        c.gridx = 2; est.add(btnEstad, c);

        c.gridx = 0; c.gridy = 1; est.add(new JLabel("Más calurosa:"), c);
        c.gridx = 1; c.gridwidth = 2; est.add(lblMas, c);

        c.gridx = 0; c.gridy = 2; c.gridwidth = 1; est.add(new JLabel("Menos calurosa:"), c);
        c.gridx = 1; c.gridwidth = 2; est.add(lblMenos, c);

        btnEstad.addActionListener(e -> onEstadisticas());

        tabs.addTab("Estadísticas", est);
        add(tabs, BorderLayout.CENTER);
    }

    private void onGraficar() {
        try {
            String ciudad = (String) cboCiudad.getSelectedItem();
            LocalDate desde = toLocal(dcDesde.getDate());
            LocalDate hasta = toLocal(dcHasta.getDate());
            if (hasta.isBefore(desde)) { LocalDate t = desde; desde = hasta; hasta = t; }

            Map<String, Double> proms = servicio.promedioPorCiudad(desde, hasta, ciudad);
            pnlGrafica.removeAll();

            if (proms.isEmpty()) {
                pnlGrafica.add(new JLabel("No hay datos en el rango seleccionado.", SwingConstants.CENTER), BorderLayout.CENTER);
            } else {
                pnlGrafica.add(
                    BarChartUtil.crearPanelBarras(
                        "Promedio (" + desde.format(DTF) + " - " + hasta.format(DTF) + ")", "°C", proms
                    ),
                    BorderLayout.CENTER
                );
            }
            pnlGrafica.revalidate();
            pnlGrafica.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al graficar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEstadisticas() {
        try {
            String ciudad = (String) cboCiudad.getSelectedItem();
            LocalDate dia = toLocal(dcDia.getDate());
            Optional<temperaturaDTO> dto = servicio
                    .masYMenosCalurosa(dia, ciudad)
                    .map(h -> new temperaturaDTO(h.ciudadMax(), h.tempMax(), h.ciudadMin(), h.tempMin()));

            if (dto.isPresent()) {
                lblMas.setText(dto.get().maxCiudad + " (" + String.format("%.1f °C", dto.get().maxVal) + ")");
                lblMenos.setText(dto.get().minCiudad + " (" + String.format("%.1f °C", dto.get().minVal) + ")");
            } else {
                lblMas.setText("—");
                lblMenos.setText("—");
                JOptionPane.showMessageDialog(this, "No hay datos para esa fecha/ciudad.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en estadísticas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static LocalDate toLocal(Date d) {
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    private static Date utilDate(LocalDate d) {
        return Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private record temperaturaDTO(String maxCiudad, double maxVal, String minCiudad, double minVal) {}
}
