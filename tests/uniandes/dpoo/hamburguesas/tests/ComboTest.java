package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ComboTest {
    private Combo combo;
    private ProductoMenu productoMenu1;
    private ProductoMenu productoMenu2;
    private ArrayList<ProductoMenu> items;
	
	@BeforeEach
    void setUp( ) throws Exception
    {
		productoMenu1 = new ProductoMenu( "papas", 1000 );
		productoMenu2 = new ProductoMenu( "hamburguesa", 10000 );
        items = new ArrayList<>();
        items.add(productoMenu1);
        items.add(productoMenu2);
        combo = new Combo( "Combo Hamburguesa", 0.1, items );
    }
	
	@Test
	void testGetNombre()
	{
		assertEquals( "Combo Hamburguesa", combo.getNombre( ), "El nombre del producto no es el esperado." );
	}
	
	@Test
    void testGetPrecio( )
    {
        assertEquals( 9900, combo.getPrecio( ), "El precio debe tener aplicado el descuento." );
    }

    @Test
    void testGetPrecioSinDescuento( )
    {
        Combo sinDescuento = new Combo( "Combo normal", 0, items );
        assertEquals( 11000, sinDescuento.getPrecio( ), "Con descuento 0 el precio debe ser la suma de los productos." );
    }

    @Test
    void testFactura( )
    {
        String factura = combo.generarTextoFactura( );

        assertTrue( factura.contains( "Combo Hamburguesa" ), "La factura debe tener el nombre del combo." );
        assertTrue( factura.contains( "0.1" ), "La factura debe mostrar el descuento." );
        assertTrue( factura.contains( "9900" ), "La factura debe mostrar el precio con descuento." );
    }
	

}