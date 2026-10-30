package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import uniandes.dpoo.hamburguesas.mundo.ProductoMenu; 

public class ProductoMenuTest {
	private ProductoMenu productoMenu;
	
	@BeforeEach
    void setUp( ) throws Exception
    {
        productoMenu = new ProductoMenu( "papas", 1000 );
    }
	
	@Test
	void testGetNombre()
	{
		assertEquals( "papas", productoMenu.getNombre( ), "El nombre del producto no es el esperado." );	
	}
	
	@Test
    void testGetPrecio( )
    {
        assertEquals( 1000, productoMenu.getPrecio( ), "El precio del producto no es el esperado." );
    }
	
	@Test
    void testFactura( )
    {
        assertTrue( this.productoMenu.generarTextoFactura().contains(this.productoMenu.getNombre()) &&
        		this.productoMenu.generarTextoFactura().contains(String.valueOf(this.productoMenu.getPrecio())));
    }
	
	

}
