package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoAjustadoTest
{
    private ProductoMenu corral;
    private Ingrediente queso;
    private Ingrediente cebolla;

    private ProductoAjustado sinAjustes;
    private ProductoAjustado conQueso;
    private ProductoAjustado sinCebolla;
    private ProductoAjustado dobleQueso;

    @BeforeEach
    void setUp( ) throws Exception
    {
        corral = new ProductoMenu( "corral", 15000 );
        queso = new Ingrediente( "queso", 2500 );
        cebolla = new Ingrediente( "cebolla", 1000 );
        sinAjustes = new ProductoAjustado( corral );

        conQueso = new ProductoAjustado( corral );
        meterEnLista( conQueso, "agregados", queso );
        
        sinCebolla = new ProductoAjustado( corral );
        meterEnLista( sinCebolla, "eliminados", cebolla );

        dobleQueso = new ProductoAjustado( corral );
        meterEnLista( dobleQueso, "agregados", queso );
        meterEnLista( dobleQueso, "agregados", queso );
    }

    /**
     * Agrega un ingrediente a la lista privada "agregados" o "eliminados" usando reflexión,
     * porque ProductoAjustado no tiene métodos públicos para hacerlo.
     */
    @SuppressWarnings( "unchecked" )
    private void meterEnLista( ProductoAjustado p, String campo, Ingrediente ing ) throws Exception
    {
        Field f = ProductoAjustado.class.getDeclaredField( campo );
        f.setAccessible( true );
        ( ( ArrayList<Ingrediente> )f.get( p ) ).add( ing );
    }

    @Test
    void testGetNombre( )
    {
        assertEquals( "corral", sinAjustes.getNombre( ), "El nombre debe ser el del producto base." );
        assertEquals( "corral", conQueso.getNombre( ), "Agregar ingredientes no debe cambiar el nombre." );
    }

    @Test
    void testGetPrecioSinAjustes( )
    {
        assertEquals( 15000, sinAjustes.getPrecio( ), "Sin ajustes el precio debe ser el del producto base." );
    }

    @Test
    void testGetPrecioConAgregados( )
    {
        assertEquals( 17500, conQueso.getPrecio( ), "El precio debe ser el base más los ingredientes agregados." );
    }

    @Test
    void testGetPrecioConEliminados( )
    {
        assertEquals( 15000, sinCebolla.getPrecio( ), "Quitar ingredientes no debe reducir el precio." );
    }

    @Test
    void testGetPrecioIngredienteRepetido( )
    {
        assertEquals( 20000, dobleQueso.getPrecio( ), "Un ingrediente agregado dos veces se debe cobrar dos veces." );
    }

    @Test
    void testFacturaSinAjustes( )
    {
        String factura = sinAjustes.generarTextoFactura( );
        assertTrue( factura.contains( "corral" ), "La factura debe tener el nombre del producto base." );
        assertTrue( factura.contains( "15000" ), "La factura debe tener el precio." );
        assertFalse( factura.contains( "@" ), "La factura muestra el objeto (ProductoMenu@...) en vez del nombre." );
    }

    @Test
    void testFacturaConAgregados( )
    {
        String factura = conQueso.generarTextoFactura( );
        assertTrue( factura.contains( "corral" ), "La factura debe tener el nombre del producto base." );
        assertTrue( factura.contains( "+queso" ), "La factura debe mostrar el ingrediente agregado." );
        assertTrue( factura.contains( "2500" ), "La factura debe mostrar el costo del ingrediente agregado." );
        assertTrue( factura.contains( "17500" ), "La factura debe mostrar el precio total." );
    }

    @Test
    void testFacturaConEliminados( )
    {
        String factura = sinCebolla.generarTextoFactura( );
        assertTrue( factura.contains( "corral" ), "La factura debe tener el nombre del producto base." );
        assertTrue( factura.contains( "-cebolla" ), "La factura debe mostrar el ingrediente eliminado." );
        assertTrue( factura.contains( "15000" ), "La factura debe mostrar el precio sin descuento por eliminar." );
    }
}
