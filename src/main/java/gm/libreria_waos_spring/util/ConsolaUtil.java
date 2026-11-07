package gm.libreria_waos_spring.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Scanner;
import java.math.BigDecimal;

public class ConsolaUtil {

    private static final Logger logger = LoggerFactory.getLogger(ConsolaUtil.class);
    private static final String NL = System.lineSeparator();
    private static final Scanner consola = new Scanner(System.in);

    // Constructor privado para que nadie pueda instanciar esta clase (es solo para métodos estáticos)
    private ConsolaUtil() {}

    public static void loguearIdMenorQue1() { logger.info("El ID debe ser igual o mayor que 1." + NL); }
    public static void loguearCopiasMenorQue1() { logger.info("La cantidad de copias debe ser igual o superior a 1." + NL); }
    public static void loguearPrecioMenorQue0() { logger.info("El Precio debe ser igual o mayor que 0." + NL); }
    public static void loguearExistenciasMenorQue0() { logger.info("Las Existencias deben ser iguales o mayores que 0." + NL); }


    public static String pedirString(String mensaje, boolean permitirVacio) {
        while (true) {
            String sufijo = permitirVacio ? " [Enter para mantener]: " : " [Enter para cancelar]: ";
            logger.info(NL + mensaje + sufijo);
            String input = consola.nextLine();

            if (input.isBlank()) {
                if (!permitirVacio) logger.info(NL + "Operación cancelada por el usuario." + NL);
                return null;
            }

            return input;
        }
    }

    public static Integer pedirInt(String mensaje, boolean permitirVacio) {
        while (true) {
            String sufijo = permitirVacio ? " [Enter para mantener]: " : " [Enter para cancelar]: ";
            logger.info(NL + mensaje + sufijo);
            String input = consola.nextLine();

            if (input.isBlank()) {
                if (!permitirVacio) logger.info(NL + "Operación cancelada por el usuario." + NL);
                return null;
            }

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                logger.info("Error: Debe ingresar solo números" + NL);
            }
        }
    }

    public static BigDecimal pedirBigDecimal(String mensaje, boolean permitirVacio) {
        while (true) {
            String sufijo = permitirVacio ? " [Enter para mantener]: " : " [Enter para cancelar]: ";
            logger.info(NL + mensaje + sufijo);
            String input = consola.nextLine();

            if (input.isBlank()) {
                if (!permitirVacio) logger.info(NL + "Operación cancelada por el usuario." + NL);
                return null;
            }

            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                logger.info("Error: Debe ingresar un número decimal válido" + NL);
            }
        }
    }

    public static boolean chequearSeguro() {
        String isSeguro;
        do {
            logger.info(NL + "¿Está Seguro/a? [S/N] ");
            isSeguro = consola.nextLine().toUpperCase();

            if (!isSeguro.equals("S") && !isSeguro.equals("N")) {
                logger.info("Opción inválida: " + isSeguro + NL);
            }
        } while (!isSeguro.equals("S") && !isSeguro.equals("N"));

        if (isSeguro.equals("N")) {
            logger.info(NL + "Operación cancelada por el Usuario. Vuelva pronto." + NL);
            return false;
        } else {
            logger.info(NL + "Acción confirmada." + NL);
            return true;
        }
    }
}