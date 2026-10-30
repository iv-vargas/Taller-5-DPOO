package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.excepciones.IngredienteRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.NoHayPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoFaltanteException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.YaHayUnPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;
import uniandes.dpoo.hamburguesas.mundo.Restaurante;

public class RestauranteTest
{
    private static final String CARPETA = "./data/pruebas/";

    private static final File INGREDIENTES = new File( CARPETA + "ingredientes.txt" );
    private static final File MENU = new File( CARPETA + "menu.txt" );
    private static final File COMBOS = new File( CARPETA + "combos.txt" );
    private static final File INGREDIENTES_REPETIDOS = new File( CARPETA + "ingredientes_repetidos.txt" );
    private static final File MENU_REPETIDO = new File( CARPETA + "menu_repetido.txt" );
    private static final File MENU_PRECIO_INVALIDO = new File( CARPETA + "menu_precio_invalido.txt" );
    private static final File COMBOS_REPETIDOS = new File( CARPETA + "combos_repetidos.txt" );
    private static final File COMBOS_PRODUCTO_FALTANTE = new File( CARPETA + "combos_producto_faltante.txt" );
    private static final File CARPETA_FACTURAS = new File( "./facturas/" );
    private Restaurante restaurante;
    private File facturaCreada;

    @BeforeEach
    void setUp( ) throws Exception
    {
        restaurante = new Restaurante( );
        CARPETA_FACTURAS.mkdirs( );   
        facturaCreada = null;
    }

    @AfterEach
    void tearDown( )
    {
        if( facturaCreada != null )
            facturaCreada.delete( );
    }

    @Test
    void testRestauranteVacio( )
    {
        assertTrue( restaurante.getPedidos( ).isEmpty( ), "No debe haber pedidos." );
        assertTrue( restaurante.getIngredientes( ).isEmpty( ), "No debe haber ingredientes." );
        assertTrue( restaurante.getMenuBase( ).isEmpty( ), "No debe haber productos en el menú." );
        assertTrue( restaurante.getMenuCombos( ).isEmpty( ), "No debe haber combos." );
        assertNull( restaurante.getPedidoEnCurso( ), "No debe haber pedido en curso." );
    }


    @Test
    void testIniciarPedido( ) throws Exception
    {
        restaurante.iniciarPedido( "Esteban", "Calle 123" );

        Pedido enCurso = restaurante.getPedidoEnCurso( );
        assertNotNull( enCurso, "Debe haber un pedido en curso." );
        assertEquals( "Esteban", enCurso.getNombreCliente( ), "El pedido no es del cliente esperado." );
    }

    @Test
    void testIniciarPedidoConPedidoEnCurso( ) throws Exception
    {
        restaurante.iniciarPedido( "Esteban", "Calle 123" );

        YaHayUnPedidoEnCursoException e = assertThrows( YaHayUnPedidoEnCursoException.class,
                ( ) -> restaurante.iniciarPedido( "Ana", "Carrera 7" ),
                "Debe lanzar excepción si ya hay un pedido en curso." );

        assertTrue( e.getMessage( ).contains( "Esteban" ) && e.getMessage( ).contains( "Ana" ),
                "El mensaje debe nombrar a los dos clientes." );
        assertEquals( "Esteban", restaurante.getPedidoEnCurso( ).getNombreCliente( ), "El pedido en curso no debe cambiar." );
    }


    @Test
    void testCerrarSinPedidoEnCurso( )
    {
        assertThrows( NoHayPedidoEnCursoException.class, ( ) -> restaurante.cerrarYGuardarPedido( ),
                "Debe lanzar excepción si no hay pedido en curso." );
    }

    @Test
    void testCerrarYGuardarPedido( ) throws Exception
    {
        restaurante.cargarInformacionRestaurante( INGREDIENTES, MENU, COMBOS );
        restaurante.iniciarPedido( "Esteban", "Calle 123" );

        Pedido pedido = restaurante.getPedidoEnCurso( );
        pedido.agregarProducto( restaurante.getMenuBase( ).get( 0 ) );
        String textoEsperado = pedido.generarTextoFactura( );

        facturaCreada = new File( CARPETA_FACTURAS, "factura_" + pedido.getIdPedido( ) + ".txt" );

        restaurante.cerrarYGuardarPedido( );
        assertNull( restaurante.getPedidoEnCurso( ), "Después de cerrar no debe haber pedido en curso." );

        assertTrue( facturaCreada.exists( ), "No se creó el archivo " + facturaCreada.getName( ) );
        assertEquals( textoEsperado, Files.readString( facturaCreada.toPath( ) ), "El archivo no tiene el texto de la factura." );

        assertEquals( 1, restaurante.getPedidos( ).size( ), "El pedido cerrado debe quedar en la lista de pedidos." );
        assertSame( pedido, restaurante.getPedidos( ).get( 0 ), "El pedido guardado no es el que se cerró." );
    }

    @Test
    void testIniciarPedidoDespuesDeCerrar( ) throws Exception
    {
        restaurante.iniciarPedido( "Esteban", "Calle 123" );
        facturaCreada = new File( CARPETA_FACTURAS, "factura_" + restaurante.getPedidoEnCurso( ).getIdPedido( ) + ".txt" );
        restaurante.cerrarYGuardarPedido( );

        assertDoesNotThrow( ( ) -> restaurante.iniciarPedido( "Ana", "Carrera 7" ),
                "Después de cerrar un pedido se debe poder iniciar otro." );
        assertEquals( "Ana", restaurante.getPedidoEnCurso( ).getNombreCliente( ) );
    }

  

    @Test
    void testCargarIngredientes( ) throws Exception
    {
        restaurante.cargarInformacionRestaurante( INGREDIENTES, MENU, COMBOS );

        assertEquals( 3, restaurante.getIngredientes( ).size( ), "No se cargaron los ingredientes esperados." );
        assertEquals( "lechuga", restaurante.getIngredientes( ).get( 0 ).getNombre( ) );
        assertEquals( 2500, restaurante.getIngredientes( ).get( 2 ).getCostoAdicional( ) );
    }

    @Test
    void testCargarMenu( ) throws Exception
    {
        restaurante.cargarInformacionRestaurante( INGREDIENTES, MENU, COMBOS );

        assertEquals( 3, restaurante.getMenuBase( ).size( ), "No se cargaron los productos esperados." );
        ProductoMenu corral = restaurante.getMenuBase( ).get( 0 );
        assertEquals( "corral", corral.getNombre( ) );
        assertEquals( 14000, corral.getPrecio( ) );
    }

    @Test
    void testCargarCombos( ) throws Exception
    {
        restaurante.cargarInformacionRestaurante( INGREDIENTES, MENU, COMBOS );

        assertEquals( 1, restaurante.getMenuCombos( ).size( ), "No se cargaron los combos esperados." );
        Combo combo = restaurante.getMenuCombos( ).get( 0 );
        assertEquals( "combo corral", combo.getNombre( ) );
        assertEquals( 22050, combo.getPrecio( ), "El precio del combo cargado no es el esperado." );
    }


    @Test
    void testIngredienteRepetido( )
    {
        assertThrows( IngredienteRepetidoException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( INGREDIENTES_REPETIDOS, MENU, COMBOS ),
                "Debe lanzar excepción si hay un ingrediente repetido." );
    }

    @Test
    void testProductoRepetido( )
    {
        assertThrows( ProductoRepetidoException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( INGREDIENTES, MENU_REPETIDO, COMBOS ),
                "Debe lanzar excepción si hay un producto repetido." );
    }

    @Test
    void testComboRepetido( )
    {
        assertThrows( ProductoRepetidoException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( INGREDIENTES, MENU, COMBOS_REPETIDOS ),
                "Debe lanzar excepción si hay un combo repetido." );
    }

    @Test
    void testComboConProductoFaltante( )
    {
        ProductoFaltanteException e = assertThrows( ProductoFaltanteException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( INGREDIENTES, MENU, COMBOS_PRODUCTO_FALTANTE ),
                "Debe lanzar excepción si un combo tiene un producto que no está en el menú." );

        assertTrue( e.getMessage( ).contains( "malteada" ), "El mensaje debe nombrar el producto faltante." );
    }

    @Test
    void testPrecioInvalido( )
    {
        assertThrows( NumberFormatException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( INGREDIENTES, MENU_PRECIO_INVALIDO, COMBOS ),
                "Debe lanzar NumberFormatException si un precio no es un número." );
    }

    @Test
    void testArchivoInexistente( )
    {
        File noExiste = new File( CARPETA + "no_existe.txt" );
        assertThrows( IOException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( noExiste, MENU, COMBOS ),
                "Debe lanzar IOException si el archivo no existe." );
    }
}
