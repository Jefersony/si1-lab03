import static org.junit.Assert.*;
import groovy.transform.AutoClone;

import org.junit.*;
import org.junit.Before;

import controllers.Application;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;

public class ApplicationTest extends FunctionalTest {
	// colocado recentemente
	@SuppressWarnings("deprecation")
    @Before
    public void setup() {
        Fixtures.deleteAll();
    }
	
    @Test
    public void testaSeIndexFunciona() {
        Response response = GET("/");
        assertIsOk(response);
        assertContentType("text/html", response);
        assertCharset(play.Play.defaultWebEncoding, response);
    }
    
    @Test
    public void testaAdminSecurity() {
        Response response = GET("/admin");
        assertStatus(302, response);
        assertHeaderEquals("Location", "/login", response);
    }
    
    @Test
	public void testaSePaginasHTMLFuncionam() {
		Response pgBuscar = GET("/buscar");
		Response pgNewUser = GET("/newuser");
		Response captcha = GET("/captcha");
		
		// Verifica se pgs respondem
		assertIsOk(pgBuscar);
		assertIsOk(pgNewUser);
		assertIsOk(captcha);
		
		// Assegura tipo de conteudo
		assertContentType("text/html", pgBuscar);
		assertContentType("text/html", pgNewUser);
		assertContentType("image/png", captcha);
	}
}