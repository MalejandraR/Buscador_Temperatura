package servicios;

import entidades.RegistroTemperatura;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;

public class TemperaturaServicio {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final List<RegistroTemperatura> registros;

    private TemperaturaServicio(List<RegistroTemperatura> registros) {
        this.registros = registros;
    }

    public static TemperaturaServicio cargar(String rutaCsv) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(rutaCsv), StandardCharsets.UTF_8)) {
            List<RegistroTemperatura> data = lines
                    .skip(1)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(TemperaturaServicio::parsear)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            return new TemperaturaServicio(data);
        }
    }

    private static Optional<RegistroTemperatura> parsear(String line) {
        String[] p = line.split("[;,]", -1);  // admite ; o ,
        if (p.length < 3) return Optional.empty();
        try {
            String ciudad = p[0].trim();
            LocalDate fecha = LocalDate.parse(p[1].trim(), DTF);
            double temp = Double.parseDouble(p[2].trim());
            return Optional.of(new RegistroTemperatura(ciudad, fecha, temp));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Map<String, Double> promedioPorCiudad(LocalDate desde, LocalDate hasta, String ciudad) {
        Stream<RegistroTemperatura> base = registros.stream()
                .filter(r -> !r.fecha().isBefore(desde) && !r.fecha().isAfter(hasta));
        if (ciudad != null && !"Todas".equalsIgnoreCase(ciudad)) {
            base = base.filter(r -> r.ciudad().equalsIgnoreCase(ciudad));
        }
        return base.collect(Collectors.groupingBy(
                RegistroTemperatura::ciudad,
                Collectors.averagingDouble(RegistroTemperatura::temperatura)
        ));

    }

    public Optional<HotCold> masYMenosCalurosa(LocalDate fecha, String ciudad) {
        List<RegistroTemperatura> delDia = registros.stream()
                .filter(r -> r.fecha().equals(fecha))
                .filter(r -> ciudad == null || "Todas".equalsIgnoreCase(ciudad) || r.ciudad().equalsIgnoreCase(ciudad))
                .collect(Collectors.toList());

        if (delDia.isEmpty()) return Optional.empty();

        RegistroTemperatura max = delDia.stream()
                .max(Comparator.comparingDouble(RegistroTemperatura::temperatura)).get();
        RegistroTemperatura min = delDia.stream()
                .min(Comparator.comparingDouble(RegistroTemperatura::temperatura)).get();

        return Optional.of(new HotCold(max.ciudad(), max.temperatura(), min.ciudad(), min.temperatura()));
    }

    public List<String> ciudades() {
        List<String> cs = registros.stream()
                .map(RegistroTemperatura::ciudad)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        cs.add(0, "Todas");
        return cs;
    }

    public record HotCold(String ciudadMax, double tempMax, String ciudadMin, double tempMin) {}
}
