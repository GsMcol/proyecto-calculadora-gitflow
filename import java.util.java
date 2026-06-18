import java.util.ArrayList;
import java.util.List;

// 1. MANEJO DE EXCEPCIONES (Requisito de la rúbrica)
class HardwareCalculatorException extends Exception {
    public HardwareCalculatorException(String message) {
        super(message);
    }
}

// 2. CLASES BASE (Principios de Clean Code y Modularidad)
class Componente {
    String nombre;
    int puntuacion;

    public Componente(String nombre, int puntuacion) {
        this.nombre = nombre;
        this.puntuacion = puntuacion;
    }
}

class Cpu extends Componente {
    public Cpu(String nombre, int puntuacion) { super(nombre, puntuacion); }
}

class Gpu extends Componente {
    public Gpu(String nombre, int puntuacion) { super(nombre, puntuacion); }
}

// 3. CLASE PRINCIPAL
public class CalculadoraHardware {
    // Simulando nuestra base de datos
    private static List<Cpu> catalogoCpus = new ArrayList<>();
    private static List<Gpu> catalogoGpus = new ArrayList<>();

    // Inicializando los datos
    static {
        catalogoCpus.add(new Cpu("Ryzen 9 9950X3D", 98));
        catalogoCpus.add(new Cpu("Core i9-14900K", 96));
        catalogoCpus.add(new Cpu("Ryzen 5 7600", 72));

        catalogoGpus.add(new Gpu("RTX 5090", 100));
        catalogoGpus.add(new Gpu("RTX 4080", 85));
        catalogoGpus.add(new Gpu("RX 6600", 62));
    }

    // 4. FUNCIÓN MODULAR PRINCIPAL
    public static String calcularRendimiento(String nombreCpu, String nombreGpu) throws HardwareCalculatorException {
        // Validación inicial
        if (nombreCpu == null || nombreCpu.trim().isEmpty() || nombreGpu == null || nombreGpu.trim().isEmpty()) {
            throw new HardwareCalculatorException("Los datos están incompletos. CPU y GPU son obligatorios.");
        }

        Cpu cpu = buscarCpu(nombreCpu);
        Gpu gpu = buscarGpu(nombreGpu);

        // Validación de existencia
        if (cpu == null) {
            throw new HardwareCalculatorException("CPU no encontrada en el catálogo: " + nombreCpu);
        }
        if (gpu == null) {
            throw new HardwareCalculatorException("GPU no encontrada en el catálogo: " + nombreGpu);
        }

        // Lógica de cuello de botella
        String cuelloBotella = (cpu.puntuacion < gpu.puntuacion) ? "CPU (Procesador muy débil)" 
                             : (cpu.puntuacion > gpu.puntuacion) ? "GPU (Gráfica muy débil)" 
                             : "Ninguno evidente. Equipo equilibrado.";

        return String.format("Reporte -> CPU: %s | GPU: %s | Cuello de botella: %s", cpu.nombre, gpu.nombre, cuelloBotella);
    }

    // Funciones de búsqueda (Responsabilidad única)
    private static Cpu buscarCpu(String nombre) {
        for (Cpu c : catalogoCpus) { if (c.nombre.equalsIgnoreCase(nombre)) return c; }
        return null;
    }

    private static Gpu buscarGpu(String nombre) {
        for (Gpu g : catalogoGpus) { if (g.nombre.equalsIgnoreCase(nombre)) return g; }
        return null;
    }

    // 5. ZONA DE PRUEBAS EN CONSOLA
    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBAS DE LA CALCULADORA ===");

        try {
            System.out.println("\nPRUEBA 1: Componentes compatibles");
            System.out.println(calcularRendimiento("Ryzen 9 9950X3D", "RTX 5090"));

            System.out.println("\nPRUEBA 2: Forzando error por datos en blanco");
            System.out.println(calcularRendimiento("", "RTX 4080"));

        } catch (HardwareCalculatorException e) {
            System.err.println("¡ERROR CONTROLADO! " + e.getMessage());
        }

        try {
            System.out.println("\nPRUEBA 3: Forzando error por hardware inexistente");
            System.out.println(calcularRendimiento("Intel Pentium 4", "RX 6600"));
        } catch (HardwareCalculatorException e) {
            System.err.println("¡ERROR CONTROLADO! " + e.getMessage());
        }
    }
}
