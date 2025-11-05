import javax.swing.*;
import servicios.TemperaturaServicio;
import ui.FrmTemperaturas;

public class App {
    public static void main(String[] args) throws Exception {
        TemperaturaServicio servicio = TemperaturaServicio.cargar("src/datos/Temperaturas.csv");
        SwingUtilities.invokeLater(() -> new FrmTemperaturas(servicio).setVisible(true));
    }
}
