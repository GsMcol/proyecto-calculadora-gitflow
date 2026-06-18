import java.util.Scanner;

// ============================================================
//  Excepcion personalizada (Requisito 1)
// ============================================================
class HardwareException extends Exception {
    public HardwareException(String mensaje) {
        super(mensaje);
    }
}

// ============================================================
//  Clase principal
// ============================================================
public class CalculadoraHardware {

    // --------------------------------------------------------
    //  Constantes de umbrales para la evaluacion
    // --------------------------------------------------------
    private static final int UMBRAL_CUELLO_CPU      = 50;
    private static final int UMBRAL_CUELLO_GPU      = 150;
    private static final double RATIO_CPU_DOMINANTE = 0.60;
    private static final double RATIO_GPU_DOMINANTE = 0.60;

    // --------------------------------------------------------
    //  Punto de entrada
    // --------------------------------------------------------
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        System.out.println("=============================================");
        System.out.println("   CALCULADORA DE CUELLO DE BOTELLA HW      ");
        System.out.println("=============================================");

        while (continuar) {
            mostrarMenu();
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    ejecutarEvaluacion(scanner);
                    break;
                case "2":
                    mostrarAyuda();
                    break;
                case "0":
                    continuar = false;
                    System.out.println("\n[OK] Hasta luego! Programa terminado.\n");
                    break;
                default:
                    System.out.println("[!] Opcion invalida. Elige 0, 1 o 2.\n");
            }
        }

        scanner.close();
    }

    // --------------------------------------------------------
    //  Menu principal
    // --------------------------------------------------------
    private static void mostrarMenu() {
        System.out.println("\n------------------------------------");
        System.out.println("           MENU PRINCIPAL           ");
        System.out.println("  1. Evaluar cuello de botella      ");
        System.out.println("  2. Ver ayuda / criterios          ");
        System.out.println("  0. Salir                          ");
        System.out.println("------------------------------------");
        System.out.print("Elige una opcion: ");
    }

    // --------------------------------------------------------
    //  Orquesta la recoleccion de datos y llama la evaluacion
    // --------------------------------------------------------
    private static void ejecutarEvaluacion(Scanner scanner) {
        try {
            // --- Datos de la CPU ---
            System.out.println("\n--- Datos de la CPU ---");
            String nombreCPU  = leerCampoObligatorio(scanner, "Nombre de la CPU (ej: Intel i5-12400): ");
            int nucleosCPU    = leerEnteroPositivo(scanner,   "Numero de nucleos: ");
            int frecuenciaCPU = leerEnteroPositivo(scanner,   "Frecuencia base (MHz): ");
            int tdpCPU        = leerEnteroPositivo(scanner,   "TDP / Consumo (W): ");

            // --- Datos de la GPU ---
            System.out.println("\n--- Datos de la GPU ---");
            String nombreGPU = leerCampoObligatorio(scanner, "Nombre de la GPU (ej: NVIDIA RTX 3060): ");
            int memoriaGPU   = leerEnteroPositivo(scanner,   "Memoria de video (GB): ");
            int velocidadGPU = leerEnteroPositivo(scanner,   "Velocidad de memoria (MHz): ");
            int tdpGPU       = leerEnteroPositivo(scanner,   "TDP / Consumo (W): ");

            // --- Evaluacion (SRP: logica extraida a metodo privado) ---
            String resultado = evaluarCuelloBotella(
                    nucleosCPU, frecuenciaCPU, tdpCPU,
                    memoriaGPU, velocidadGPU, tdpGPU
            );

            // --- Reporte final ---
            System.out.println("\n=============================================");
            System.out.println("         RESULTADO DEL ANALISIS             ");
            System.out.println("=============================================");
            System.out.println("  CPU : " + nombreCPU);
            System.out.println("  GPU : " + nombreGPU);
            System.out.println("---------------------------------------------");
            System.out.println(resultado);
            System.out.println("=============================================");

        } catch (HardwareException e) {
            System.out.println("\n[ERROR] Error de hardware: " + e.getMessage());
            System.out.println("        Vuelve al menu e intenta de nuevo.\n");
        }
    }

    // --------------------------------------------------------
    //  LOGICA DE EVALUACION - metodo privado independiente
    //  Principio de Responsabilidad Unica (SRP) (Requisito 2)
    // --------------------------------------------------------
    static String evaluarCuelloBotella(
            int nucleos, int frecuencia, int tdpCPU,
            int memoria, int velocidad, int tdpGPU)
            throws HardwareException {

        double puntajeCPU = (nucleos * frecuencia * 0.001) + tdpCPU;
        double puntajeGPU = (memoria * velocidad  * 0.01)  + tdpGPU;
        double total      = puntajeCPU + puntajeGPU;

        if (total == 0) {
            throw new HardwareException("Los puntajes son 0; revisa los datos ingresados.");
        }

        double ratioCPU = puntajeCPU / total;
        double ratioGPU = puntajeGPU / total;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  Puntaje CPU  : %.2f pts%n", puntajeCPU));
        sb.append(String.format("  Puntaje GPU  : %.2f pts%n", puntajeGPU));
        sb.append(String.format("  Ratio CPU/GPU: %.0f%% / %.0f%%%n",
                ratioCPU * 100, ratioGPU * 100));
        sb.append("\n");

        // --- Arbol de decision del cuello de botella ---
        if (tdpCPU < UMBRAL_CUELLO_CPU && tdpGPU >= UMBRAL_CUELLO_GPU) {
            sb.append("  [!] CUELLO DE BOTELLA: CPU\n");
            sb.append("  --> La CPU (TDP=" + tdpCPU + "W) limita a la GPU.\n");
            sb.append("  --> Mejora: CPU con mayor TDP y nucleos.");

        } else if (tdpGPU < UMBRAL_CUELLO_GPU && tdpCPU >= UMBRAL_CUELLO_CPU) {
            sb.append("  [!] CUELLO DE BOTELLA: GPU\n");
            sb.append("  --> La GPU (TDP=" + tdpGPU + "W) limita a la CPU.\n");
            sb.append("  --> Mejora: GPU con mayor VRAM y TDP.");

        } else if (ratioCPU >= RATIO_CPU_DOMINANTE) {
            sb.append("  [!] CUELLO DE BOTELLA: GPU\n");
            sb.append(String.format("  --> La CPU aporta el %.0f%% de la potencia.%n", ratioCPU * 100));
            sb.append("  --> La GPU no puede seguir el ritmo de la CPU.\n");
            sb.append("  --> Mejora: cambiar la GPU por un modelo mas potente.");

        } else if (ratioGPU >= RATIO_GPU_DOMINANTE) {
            sb.append("  [!] CUELLO DE BOTELLA: CPU\n");
            sb.append(String.format("  --> La GPU aporta el %.0f%% de la potencia.%n", ratioGPU * 100));
            sb.append("  --> La CPU no puede alimentar a la GPU correctamente.\n");
            sb.append("  --> Mejora: cambiar la CPU por un modelo mas potente.");

        } else {
            sb.append("  [OK] SIN CUELLO DE BOTELLA SIGNIFICATIVO\n");
            sb.append("  --> El balance CPU/GPU es adecuado para uso general.\n");
            sb.append("  --> Ningun componente limita drasticamente al otro.");
        }

        return sb.toString();
    }

    // --------------------------------------------------------
    //  Utilidades de entrada con validacion (Requisito 1)
    // --------------------------------------------------------
    private static String leerCampoObligatorio(Scanner scanner, String prompt)
            throws HardwareException {
        System.out.print(prompt);
        String valor = scanner.nextLine().trim();
        if (valor.isEmpty()) {
            throw new HardwareException(
                "El campo '" + prompt.replace(":", "").trim() + "' no puede estar vacio.");
        }
        return valor;
    }

    private static int leerEnteroPositivo(Scanner scanner, String prompt)
            throws HardwareException {
        System.out.print(prompt);
        String entrada = scanner.nextLine().trim();

        if (entrada.isEmpty()) {
            throw new HardwareException(
                "El campo '" + prompt.replace(":", "").trim() + "' no puede estar vacio.");
        }

        try {
            int valor = Integer.parseInt(entrada);
            if (valor <= 0) {
                throw new HardwareException(
                    "El campo '" + prompt.replace(":", "").trim()
                    + "' debe ser positivo. Se recibio: " + valor);
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new HardwareException(
                "El campo '" + prompt.replace(":", "").trim()
                + "' debe ser un numero entero valido. Se recibio: '" + entrada + "'.");
        }
    }

    // --------------------------------------------------------
    //  Ayuda / criterios
    // --------------------------------------------------------
    private static void mostrarAyuda() {
        System.out.println("\n=============================================");
        System.out.println("         CRITERIOS DE EVALUACION            ");
        System.out.println("=============================================");
        System.out.println("  Puntaje CPU = (nucleos x frec x 0.001) + TDP_CPU");
        System.out.println("  Puntaje GPU = (VRAM_GB x veloc x 0.01) + TDP_GPU");
        System.out.println("---------------------------------------------");
        System.out.println("  CUELLO EN CPU si:");
        System.out.println("    * TDP_CPU < 50W y TDP_GPU >= 150W, o");
        System.out.println("    * GPU aporta >= 60% del puntaje total");
        System.out.println();
        System.out.println("  CUELLO EN GPU si:");
        System.out.println("    * TDP_GPU < 150W y TDP_CPU >= 50W, o");
        System.out.println("    * CPU aporta >= 60% del puntaje total");
        System.out.println();
        System.out.println("  SIN CUELLO si los ratios estan equilibrados");
        System.out.println("=============================================");
    }
}
