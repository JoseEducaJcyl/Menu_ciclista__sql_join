// Importes necesarios para el programa
import java.sql.*;
import java.util.Scanner;

// Clase Main para la ejecucion del programa
public class Main {
    public static void main(String[] args) {
        // Creacion de un Scanner para leer opciones del usuario
        Scanner sc = new Scanner(System.in);
        
        // Datos para conectarse a la base de datos (constantes con final)
        final String URL = "jdbc:oracle:thin:@//localhost:1521/xe"; // Cambia según tu BD
        final String USUARIO = "RIBERA";
        final String CONTRASENIA = "ribera";
        
        // Variable de control para el bucle
        boolean terminado = false;
        
        // Bucle do-while que muestra el menú principal
        do {
            System.out.println("----MENU----");
            System.out.println("1--- CLASIFICACION GENERAL");
            System.out.println("2--- CLASIFICACION POR EQUIPOS");
            System.out.println("3--- RANKING DE ETAPA MAS LARGA");
            System.out.println("4---SALIR");
            System.out.println("-------------");
            System.out.println("Ingrese la opcion: ");
            int opcion = sc.nextInt();
            sc.nextLine();
            
            switch (opcion) {
                case 1:
                    clasificacionGeneral(URL, USUARIO, CONTRASENIA);
                    break;
                case 2:
                    clasificacionEquipos(URL, USUARIO, CONTRASENIA);
                    break;
                case 3:
                    rankingEtapasMasLargas(URL, USUARIO, CONTRASENIA);
                    break;
                case 4:
                    System.out.println("Saliendo...");
                    terminado = true;
                    break;
                default:
                    System.out.println("Opcion invalida");
                    break;
            }
        } while (!terminado);
        
        sc.close();
    }
    
    // ==================== CLASIFICACION GENERAL (TOP 10 CICLISTAS) ====================
    public static void clasificacionGeneral(String url, String usuario, String contraseña) {
        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña);
             Statement stmt = conn.createStatement()) {
            
            // Consulta que suma los puntos de cada ciclista y muestra el TOP 10
            String sql = "SELECT C.NOMBRE AS NOMBRE_CICLISTA, E.NOMBRE AS NOMBRE_EQUIPO, " +
                    "SUM(P.PUNTOS) AS PUNTOS_TOTALES FROM CICLISTA C JOIN PARTICIPACION P " +
                    "ON C.ID_CICLISTA = P.ID_CICLISTA JOIN EQUIPO E ON C.ID_EQUIPO = E.ID_EQUIPO " +
                    "GROUP BY C.NOMBRE, E.NOMBRE " +
                    "ORDER BY SUM(P.PUNTOS) DESC " +
                    "FETCH FIRST 10 ROWS ONLY";
            
            ResultSet rs = stmt.executeQuery(sql);
            
            System.out.println("\n=== CLASIFICACION GENERAL (TOP 10) ===");
            int puesto = 1;
            while (rs.next()) {
                String ciclista = rs.getString("nombre_ciclista");
                String equipo = rs.getString("nombre_equipo");
                int puntos = rs.getInt("puntos_totales");
                System.out.println(puesto++ + ". " + ciclista + " - " + equipo + " - " + puntos + " puntos");
            }
            
        } catch (SQLException e) {
            System.out.println("Error al mostrar la clasificacion general: " + e.getMessage());
        }
    }
    
    // ==================== CLASIFICACION POR EQUIPOS ====================
    public static void clasificacionEquipos(String url, String usuario, String contraseña) {
        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña);
             Statement stmt = conn.createStatement()) {
            
            // Consulta que suma los puntos de todos los ciclistas de cada equipo
            String sql = "SELECT E.NOMBRE AS nombre_equipo, E.PAIS AS pais, SUM(P.PUNTOS) AS puntos_totales " +
                    "FROM EQUIPO E JOIN CICLISTA C ON E.ID_EQUIPO = C.ID_EQUIPO " +
                    "JOIN PARTICIPACION P ON C.ID_CICLISTA = P.ID_CICLISTA " +
                    "GROUP BY E.NOMBRE, E.PAIS " +
                    "ORDER BY SUM(P.PUNTOS) DESC";
            
            ResultSet rs = stmt.executeQuery(sql);
            
            System.out.println("\n=== CLASIFICACION POR EQUIPOS ===");
            int puesto = 1;
            while (rs.next()) {
                String equipo = rs.getString("nombre_equipo");
                String pais = rs.getString("pais");
                int puntos = rs.getInt("puntos_totales");
                System.out.println(puesto++ + ". " + equipo + " (" + pais + ") - " + puntos + " puntos");
            }
            
        } catch (SQLException e) {
            System.out.println("Error al mostrar la clasificacion por equipos: " + e.getMessage());
        }
    }
    
    // ==================== RANKING ETAPAS MAS LARGAS (TOP 3 SUPERAN PROMEDIO) ====================
    public static void rankingEtapasMasLargas(String URL, String USUARIO, String CONTRASENA) {
        try (Connection conn = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
             Statement stmt = conn.createStatement()) {
            
            // Consulta que muestra las 3 etapas más largas que superan el promedio de distancia
            String sql = "SELECT DISTANCIA_KM AS distancia_total, NUMERO AS numero, " +
                    "ORIGEN AS origen, DESTINO AS destino, FECHA AS fecha FROM ETAPA " +
                    "WHERE DISTANCIA_KM > (SELECT AVG(DISTANCIA_KM) FROM ETAPA) " +
                    "ORDER BY DISTANCIA_KM DESC " +
                    "FETCH FIRST 3 ROWS ONLY";
            
            ResultSet rs = stmt.executeQuery(sql);
            
            System.out.println("\n=== TOP 3 ETAPAS MAS LARGAS (superan el promedio) ===");
            int puesto = 1;
            while (rs.next()) {
                int distancia = rs.getInt("distancia_total");
                int numero = rs.getInt("numero");
                String origen = rs.getString("origen");
                String destino = rs.getString("destino");
                String fecha = rs.getString("fecha");
                System.out.println(puesto++ + ". Etapa " + numero + " - " + origen + " → " + destino);
                System.out.println("   Distancia: " + distancia + " km - Fecha: " + fecha);
            }
            
        } catch (SQLException e) {
            System.out.println("Error al mostrar el ranking de etapas: " + e.getMessage());
        }
    }
}
