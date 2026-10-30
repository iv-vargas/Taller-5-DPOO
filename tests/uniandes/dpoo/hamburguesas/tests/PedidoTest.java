package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class PedidoTest
{
    private Pedido pedido;
    private Pedido pedidoVacio;
    private ProductoMenu papas;
    private ProductoMenu hamburguesa;

    @TempDir
    Path carpetaTemporal;

    @BeforeEach
    void setUp( ) throws Exception
    {
        papas = new ProductoMenu( "papas", 2000 );
        hamburguesa = new ProductoMenu( "hamburguesa", 8000 );

        pedido = new Pedido( "Esteban", "Calle 123" );
        pedido.agregarProducto( papas );
        pedido.agregarProducto( hamburguesa );
        pedidoVacio = new Pedido( "Ana", "Carrera 7" );
    }


    @Test
    void testGetNombreCliente( )
    {
    	assertEquals( "Esteban", pedido.getNombreCliente( ), "El nombre del cliente no es el esperado." );
    }

    @Test
    void testIdPedidoConsecutivo( )
    {
    	assertEquals( pedido.getIdPedido()+1, pedidoVacio.getIdPedido( ), "El ID del pedido no es el esperado." );
    }


    @Test
    void testPrecioTotalPedido( )
    {
        assertEquals( 11900, pedido.getPrecioTotalPedido( ), "El precio total debe ser el neto más el IVA." );
    }

    @Test
    void testPrecioTotalPedidoVacio( )
    {
        assertEquals( 0, pedidoVacio.getPrecioTotalPedido( ), "Un pedido sin productos debe costar 0." );
    }

    @Test
    void testAgregarProducto( )
    {
        pedidoVacio.agregarProducto( papas );
        assertEquals( 2380, pedidoVacio.getPrecioTotalPedido( ), "El precio no cambió al agregar el producto." );
        assertTrue( pedidoVacio.generarTextoFactura( ).contains( "papas" ), "El producto agregado no aparece en la factura." );
    }

    @Test
    void testFacturaDatosCliente( )
    {
        String factura = pedido.generarTextoFactura( );
        assertTrue( factura.contains( "Esteban" ), "La factura debe tener el nombre del cliente." );
        assertTrue( factura.contains( "Calle 123" ), "La factura debe tener la dirección del cliente." );
    }

    @Test
    void testFacturaProductos( )
    {
        String factura = pedido.generarTextoFactura( );
        assertTrue( factura.contains( "papas" ), "La factura debe tener el nombre de las papas." );
        assertTrue( factura.contains( "2000" ), "La factura debe tener el precio de las papas." );
        assertTrue( factura.contains( "hamburguesa" ), "La factura debe tener el nombre de la hamburguesa." );
        assertTrue( factura.contains( "8000" ), "La factura debe tener el precio de la hamburguesa." );
        assertTrue( factura.contains( papas.generarTextoFactura( ) ), "El texto de las papas no coincide." );
        assertTrue( factura.contains( hamburguesa.generarTextoFactura( ) ), "El texto de la hamburguesa no coincide." );
    }

    @Test
    void testFacturaValores( )
    {
        String factura = pedido.generarTextoFactura( );
        assertTrue( factura.contains( "10000" ), "La factura debe tener el precio neto." );
        assertTrue( factura.contains( "1900" ), "La factura debe tener el IVA." );
        assertTrue( factura.contains( "11900" ), "La factura debe tener el precio total." );
    }

    @Test
    void testFacturaPedidoVacio( )
    {
        String factura = pedidoVacio.generarTextoFactura( );
        assertTrue( factura.contains( "Ana" ), "La factura debe tener el nombre del cliente." );
        assertTrue( factura.contains( "Carrera 7" ), "La factura debe tener la dirección del cliente." );
        assertTrue( factura.contains( "Precio Neto:  0" ), "El precio neto de un pedido vacío debe ser 0." );
        assertTrue( factura.contains( "IVA:          0" ), "El IVA de un pedido vacío debe ser 0." );
        assertTrue( factura.contains( "Precio Total: 0" ), "El total de un pedido vacío debe ser 0." );
    }

    @Test
    void testGuardarFactura( ) throws Exception
    {
        File archivo = carpetaTemporal.resolve( "factura.txt" ).toFile( );
        pedido.guardarFactura( archivo );

        assertTrue( archivo.exists( ), "El archivo de la factura no se creó." );

        String contenido = Files.readString( archivo.toPath( ) );
        assertEquals( pedido.generarTextoFactura( ), contenido, "El archivo no tiene el mismo texto de la factura." );
    }

    @Test
    void testGuardarFacturaRutaInvalida( )
    {
        File archivo = new File( carpetaTemporal.toFile( ), "noExiste/factura.txt" );
        assertThrows( FileNotFoundException.class, ( ) -> pedido.guardarFactura( archivo ),
                "Debe lanzar FileNotFoundException si no se puede crear el archivo." );
    }
}
