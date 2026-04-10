import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        final String URL = "jdbc:oracle:thin:@//localhost:1521/xe"; // Cambia según tu BD
        final String USUARIO = "RIBERA";
        final String CONTRASENIA = "ribera";
        boolean terminado = false;

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
        }while(!terminado);


        sc.close();
    }
    public static void clasificacionGeneral(String url, String usuario, String contraseña){
        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña);
             Statement stmt = conn.createStatement()){

            String sql = "SELECT C.NOMBRE AS NOMBRE_CICLISTA, E.NOMBRE AS NOMBRE_EQUIPO, " +
            "SUM(P.PUNTOS) AS PUNTOS_TOTALES FROM CICLISTA C JOIN PARTICIPACION P " +
            "ON C.ID_CICLISTA=P.ID_CICLISTA JOIN EQUIPO E ON C.ID_EQUIPO=E.ID_EQUIPO " +
            "GROUP BY C.NOMBRE, E.NOMBRE " + "ORDER BY SUM(P.PUNTOS) DESC " + "FETCH FIRST 10 ROWS ONLY";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String ciclista = rs.getString("nombre_ciclista");
                String equipo = rs.getString("nombre_equipo");
                int puntos = rs.getInt("puntos_totales");
                System.out.println("Datos: ");
                System.out.println("Nombre del ciclista: " + ciclista);
                System.out.println("Nombre del equipo: " + equipo);
                System.out.println("Puntaje del ciclista: " + puntos);
            }
        } catch (SQLException e) {
            System.out.println("Error al mostrar la tabla: " + e.getMessage());
        }
    }

    public static void clasificacionEquipos(String url, String usuario, String contraseña) {
        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña);
             Statement stmt = conn.createStatement()){

            String sql = "SELECT E.NOMBRE AS nombre_equipo, E.PAIS AS pais, SUM(P.PUNTOS) AS puntos_totales " +
                    "FROM EQUIPO E JOIN CICLISTA C ON E.ID_EQUIPO=C.ID_EQUIPO JOIN PARTICIPACION P ON " +
                    "C.ID_CICLISTA=P.ID_CICLISTA " +
                    "GROUP BY E.NOMBRE, E.PAIS " +
                    "ORDER BY SUM(P.PUNTOS) DESC";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String equipo = rs.getString("nombre_equipo");
                String pais = rs.getString("pais");
                int puntos = rs.getInt("puntos_totales");
                System.out.println("Datos: ");
                System.out.println("Nombre del equipo: " + equipo);
                System.out.println("Nombre del pais: " + pais);
                System.out.println("Puntaje del equipo: " + puntos);
            }
        } catch (SQLException e) {
            System.out.println("Error al mostrar la tabla: " + e.getMessage());
        }
    }
    public static void rankingEtapasMasLargas(String URL, String USUARIO, String CONTRASENA) {
        try (Connection conn = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
             Statement stmt = conn.createStatement()){

            String sql = "SELECT MAX(DISTANCIA_KM) AS distancia_total, NUMERO AS numero, " +
                    "ORIGEN AS origen, DESTINO AS destino, FECHA AS fecha FROM ETAPA " +
                    "GROUP BY NUMERO, ORIGEN, DESTINO, FECHA " +
                    "HAVING MAX(DISTANCIA_KM) > (SELECT AVG(DISTANCIA_KM) FROM ETAPA) " +
                    "ORDER BY MAX(DISTANCIA_KM) DESC " +
                    "FETCH FIRST 3 ROWS ONLY";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int distancia = rs.getInt("distancia_total");
                int numero = rs.getInt("numero");
                String origen = rs.getString("origen");
                String destino = rs.getString("destino");
                String fecha = rs.getString("fecha");
                System.out.println("Datos: ");
                System.out.println("Distancia total: " + distancia);
                System.out.println("Numero de la etapa: " + numero);
                System.out.println("Origen de la etapa: " + origen);
                System.out.println("Destino de la etapa: " + destino);
                System.out.println("Fecha de l etapa: " + fecha);
            }
        } catch (SQLException e) {
            System.out.println("Error al mostrar la tabla: " + e.getMessage());
        }
    }
}