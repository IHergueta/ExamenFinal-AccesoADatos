/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examen;

import BeansExamen.ClienteDelMes;
import BeansExamen.ComprasDelMes;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQResultSequence;
import net.xqj.exist.ExistXQDataSource;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.xml.xquery.XQSequence;
import org.xmldb.api.base.Resource;
import org.xmldb.api.modules.CollectionManagementService;

/**
 *
 * @author David
 */
public class ExamenFinal {

    // También se podría haber hecho con un enum
    public static final String[] meses = {"enero", "febrero", "marzo", "abril", "mayo", "junio"};

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {

            // Ayuda sobre los métodos importantes de GregorianCalendar
            GregorianCalendar calendar = new GregorianCalendar();
            // Devuelve la fecha guardada en un GregorianCalendar con el formato de fechas utilizado en el XML
            System.out.println("Fecha actual: " + calendar.toZonedDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
            // Devuelve el mes del objeto GregorianCalendar empezando en 0
            System.out.println("Mes actual (siendo 0 = Enero): " + calendar.get(Calendar.MONTH));

            // Obtenemos colección y creamos el fichero en nuestra BBDD
            Collection col = obtenColeccion("/pruebas");
            crearFichero(col);

            // Creamos conexión con la BBDD para utilizar la API XQJ
            XQConnection conexion = obtenConexion();

            // Instanciamos objetos ComprasDelMes y ClienteDelMes con un DNI especifico
            // y configuramos el receptor de eventos
            String dni = "78901234X";
            ComprasDelMes comprasDelMes = new ComprasDelMes();
            comprasDelMes.setDni(dni);

            ClienteDelMes clienteDelMes = new ClienteDelMes(dni);

            comprasDelMes.addPropertyChangeListener(clienteDelMes);

            // Obtenemos las unidadesTotales del cliente 78901234X en el mes de enero
            int unidadesTotales = consultaPedidos(dni, "20210101", "20210131", conexion);
            comprasDelMes.setUnidades(unidadesTotales);

            // Comprobamos si cumple con la condición
            if (clienteDelMes.isEsClienteDelMes()) {
                insertarClienteMes(clienteDelMes, conexion, 0);
            } else {
                System.out.println("No cumple con la condición, por lo que no es cliente del mes");
            }

            System.out.println("Unidades totales: " + comprasDelMes.getUnidades());

        } catch (Exception ex) {
            Logger.getLogger(ExamenFinal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Collection obtenColeccion(String nomCol) throws Exception {
        Database dbDriver;
        Collection col;
        // Cargamos driver y obtenemos una instancia de la BBDD
        dbDriver = (Database) Class.forName("org.exist.xmldb.DatabaseImpl").newInstance();
        // Registramos el driver
        DatabaseManager.registerDatabase(dbDriver);
        // Obtenemos una colección determinada
        col = DatabaseManager.getCollection("xmldb:exist://localhost:8090/exist/xmlrpc/db" + nomCol,
                "admin", "admin");

        return col;
    }

    private static XQConnection obtenConexion() throws XQException {
        XQConnection con = null;
        XQDataSource db = new ExistXQDataSource();
        db.setProperty("serverName", "localhost");
        db.setProperty("port", "8090");
        db.setProperty("user", "admin");
        db.setProperty("password", "admin");

        con = db.getConnection();

        return con;
    }

    public static void crearFichero(Collection coleccion) {

        try {

            XMLResource res = (XMLResource) coleccion.getResource("clientes_del_mes.xml");

            if (res == null) {

                //obtengo el servicio para crear
                //CollectionManagementService mgtService = (CollectionManagementService) pruebas.getService("CollectionManagementService", "1.0");
                //la creo
                //mgtService.createCollection("copia_seguridad");
                Resource crear_archivo = coleccion.createResource("clientes_del_mes.xml", XMLResource.RESOURCE_TYPE);
                String file = "<anio_2021>\n"
                        + "<enero/>\n"
                        + "<febrero/>\n"
                        + "<marzo/>\n"
                        + "<abril/>\n"
                        + "<mayo/>\n"
                        + "<junio/>\n"
                        + "</anio_2021>";

                crear_archivo.setContent(file);

                coleccion.storeResource(crear_archivo);

                System.out.println("clientes_del_mes.xml creado");

            } else {

                System.out.println("El fichero ya existe en la BBDD");

            }

        } catch (Exception e) {

            System.out.println("Problema al crear el archivo");
        }
    }

// Formato fechaInicio y fechaFinal: YYYYMMDD (AñoMesDia)
    public static int consultaPedidos(String dni, String fechaInicio, String fechaFinal, XQConnection conexion) throws XQException {

        int cont = 0;
        XQExpression expr = conexion.createExpression();

        // where compare(fecha,fechaInicio) > 0 and compare()
        XQSequence muerte = expr.executeQuery("for $cp in doc('/db/pruebas/compras(1).xml')//cliente[@DNI='" + dni + "']\n"
                + "let $total := $cp/../unidades\n"
                + "where $cp[translate(fecha,'-','')< translate('" + fechaFinal + "','-','') and translate(fecha,'-','')< translate('" + fechaInicio + "','-','')]\n"
                + "\n"
                + "\n"
                + "return if (compare(translate('" + fechaFinal + "','-',''),translate('" + fechaInicio + "','-',''))=1)\n"
                + "then xs:int($total)\n"
                + "else()\n"
                + "   ");

        /*
         XQSequence muerte = expr.executeQuery( "for $cp in doc('/db/pruebas/compras(1).xml')//cliente[@DNI='" + dni + "']\n" +
         "let $total := $cp/../unidades\n" +
         "where compare($cp/../fecha, " + fechaFinal +") >0 and compare($cp/../fecha, " + fechaInicio +") <0\n" +
         "\n" +
         "return if (compare(translate('" + fechaFinal +"','-',''),translate('" + fechaFinal +"','-',''))=1)\n" +
         "then xs:int($total)\n" +
         "else()\n" +
         "   ");
         */
        while (muerte.next()) {
            cont = muerte.getInt() + cont;
        }

        return cont;

    }

    // mesActualizado -> entero entre 0 y 11 que indica un mes del año (0 ->enero, 1->febrero...)
    public static void insertarClienteMes(ClienteDelMes cliente, XQConnection conexion, int mesActualizado) throws XQException {

        XQExpression expr = conexion.createExpression();

        XQSequence existe = expr.executeQuery("for $cp in count(doc('/db/pruebas/clientes_del_mes.xml')/anio_2021/enero/cliente/DNI='78901234X')\n"
                + "return $cp\n"
                + "   ");

        if (existe.getInt() == 1) {

            System.out.println("Ya existe,actualizando datos");

            /*
             expr.executeCommand("update replace\n"
             + "doc('/db/pruebas/clientes_del_mes.xml')//DNI=" + cliente.getDni() + "\n"
             + "with\n"
             + "<cliente> \n"
             + "<DNI> " +cliente.getDni() + "</DNI>\n"
             + "<fechaLogro> "+ cliente.getFechaLogro()+"</fechaLogro>\n"
             + "<unidades_totales>"+cliente.getUnidadesTotales()+"</unidades_totales>\n"
             + "</cliente>");
             */
        } else {

            expr.executeCommand("update insert <cliente> \n"
                    + "<DNI>" + cliente.getDni() + "</DNI>\n"
                    + "<fechaLogro>" + cliente.getFechaLogro() + "</fechaLogro>\n"
                    + "<unidades_totales>" + cliente.getUnidadesTotales() + "</unidades_totales>\n"
                    + "</cliente>\n"
                    + "into\n"
                    + "doc('/db/pruebas/clientes_del_mes.xml')/anio_2021/" + meses[mesActualizado]);
        }

    }

}
