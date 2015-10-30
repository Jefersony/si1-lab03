import static org.junit.Assert.*;

import org.junit.*;

import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;

public class ApplicationTest extends FunctionalTest {

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
		assertContentType("text/html", pgBuscar);
		assertContentType("text/html", pgNewUser);
		assertContentType("image/png", captcha);
	}
    
}