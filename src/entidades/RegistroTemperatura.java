package entidades;

import java.time.LocalDate;

public record RegistroTemperatura(String ciudad, LocalDate fecha, double temperatura) {}
