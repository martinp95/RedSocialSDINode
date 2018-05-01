package redSocial.tests;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import redSocial.tests.pageobjects.PO_HomeView;
import redSocial.tests.pageobjects.PO_LoginView;
import redSocial.tests.pageobjects.PO_NavView;
import redSocial.tests.pageobjects.PO_RegisterView;
import redSocial.tests.pageobjects.PO_View;
import redSocial.tests.utils.SeleniumUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RedSocialSDITests {

	static String PathFirefox = "C:\\Users\\gemma\\Desktop\\RedSocialSDINode\\Firefox46.win\\FirefoxPortable.exe";
	static WebDriver driver = getDriver(PathFirefox);
	static String URL = "http://localhost:8081";

	public static WebDriver getDriver(String PathFirefox) {
		System.setProperty("webdriver.firefox.bin", PathFirefox);
		WebDriver driver = new FirefoxDriver();
		return driver;
	}

	// Antes de cada prueba se navega al URL home de la aplicaciÃ³nn
	@Before
	public void setUp() {
		driver.navigate().to(URL);
	}

	// DespuÃ©s de cada prueba se borran las cookies del navegador
	@After
	public void tearDown() {
		driver.manage().deleteAllCookies();
	}

	// Antes de la primera prueba
	@BeforeClass
	static public void begin() {
		driver.navigate().to("http://localhost:8081/borrarBD");
	} // Al finalizar la Ãºltima prueba

	@AfterClass
	static public void end() {
		// Cerramos el navegador al finalizar las pruebas
		driver.quit();
	}

	// 1.1 Registro de Usuario con datos válidos
	@Test
	public void RegVal() {
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonSignup", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();
		PO_RegisterView.fillForm(driver, Math.floor(Math.random() * 50000 + 1) + "@example.com", "Pepe", "1234",
				"1234");
		PO_View.checkElement(driver, "text", "Nombre");
	}

	// 1.2 Registro de Usuario con datos inválidos (repetición de contraseña
	// inválida)
	@Test
	public void RegInval() {
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonSignup", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();
		PO_RegisterView.fillForm(driver, "example@example.com", "Josefo", "1234", "12345");
		PO_View.checkElement(driver, "text", "Las constraseñas no coinciden.");
	}

	// 2.1 Inicio de sesión con datos válidos
	@Test
	public void InVal() {
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonLogin", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();
		PO_LoginView.fillForm(driver, "gemma@example.com", "1234");
		PO_View.checkElement(driver, "text", "Nombre");
	}

	// 2.2 Inicio de sesión con datos inválidos (usuario no existente en la
	// aplicación)
	@Test
	public void InInVal() {
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonLogin", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();
		PO_LoginView.fillForm(driver, "noExiste@example.com", "1234");
		PO_View.checkElement(driver, "text", "Email o password incorrecto");
	}

	// 3.1 Acceso al listado de usuarios desde un usuario en sesión
	@Test
	public void LisUsrVal() {
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonLogin", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();
		PO_LoginView.fillForm(driver, "gemma@example.com", "1234");
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "mListaUsuarios", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();
		PO_View.checkElement(driver, "text", "Nombre");
	}

	// 3.2 Intento de acceso con URL desde un usuario no identificado al listado de
	// usuarios desde un usuario en sesión. Debe producirse un acceso no permitido a
	// vistas privadas
	@Test
	public void LisUsrInVal() {
		driver.navigate().to("http://localhost:8081/listUsers");
		PO_View.checkElement(driver, "text", "Identificación de usuario");
	}

	// 4.1 Realizar una búsqueda valida en el listado de usuarios desde un usuario
	// en sesión
	@Test
	public void BusUsrVal() {
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonLogin", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();
		PO_LoginView.fillForm(driver, "gemma@example.com", "1234");
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "mListaUsuarios", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();
		WebElement search = driver.findElement(By.id("searchUsers"));
		search.click();
		search.clear();
		search.sendKeys("gmail");
		By boton = By.className("btn");
		driver.findElement(boton).click();
		PO_View.checkElement(driver, "text", "Hugo");
	}

	// 4.2 Intento de acceso con URL a la búsqueda de usuarios desde un usuario no
	// identificado. Debe producirse un acceso no permitido a vistas privadas

	@Test
	public void BusUsrInVal() {
		driver.navigate().to("http://localhost:8081/listUsers?busqueda=gmail");
		PO_View.checkElement(driver, "text", "Identificación de usuario");
	}

	// 5.1 Enviar una invitación de amistad a un usuario de forma valida
	@Test
	public void InvVal() {
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonLogin", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();
		PO_LoginView.fillForm(driver, "gemma@example.com", "1234");

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonAgregarAmigogema@gmail.com",
				PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		PO_View.checkElement(driver, "text", "Se ha enviado la petición de amistad");
	}

	// 5.2 Enviar una invitación de amistad a un usuario al que ya le habíamos
	// envíado la invitación previamente. No debería dejarnos enviar la invitación,
	// se podría ocultar el botón de enviar invitación o notificar que ya había sido
	// enviada previamente.
	@Test
	public void InvInval() {
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonLogin", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();
		PO_LoginView.fillForm(driver, "gemma@example.com", "1234");

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonAgregarAmigoprueba@prueba.com",
				PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonAgregarAmigoprueba@prueba.com",
				PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		PO_View.checkElement(driver, "text", "Error, peticion ya enviada.");
	}

	// 6.1 Listar las invitaciones recibidas por un usuario, realizar la
	// comprobación con una lista que al menos tenga una invitación recibida.
	@Test
	public void LisInvVal() {
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonLogin", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();
		PO_LoginView.fillForm(driver, "nacho@gmail.com", "123456");

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonAgregarAmigomartin@gmail.com",
				PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonLogout", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		PO_LoginView.fillForm(driver, "martin@gmail.com", "123456");

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "mListaPeticiones", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		PO_View.checkElement(driver, "text", "Nacho");
	}

	// 7.1 [AcepInvVal] Aceptar una invitación recibida.
	@Test
	public void AcepInvVal() {
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonLogin", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();
		PO_LoginView.fillForm(driver, "gemma@example.com", "1234");

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonAgregarAmigonacho@gmail.com",
				PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		SeleniumUtils.esperarSegundos(driver, 3);

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonAgregarAmigomartin@gmail.com",
				PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		SeleniumUtils.esperarSegundos(driver, 3);

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonAgregarAmigoraul@gmail.com",
				PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonLogout", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		PO_LoginView.fillForm(driver, "nacho@gmail.com", "123456");

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "mListaPeticiones", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonAceptarAmigogemma@example.com",
				PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		SeleniumUtils.textoNoPresentePagina(driver, "Gemma");

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonLogout", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		PO_LoginView.fillForm(driver, "martin@gmail.com", "123456");

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "mListaPeticiones", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonAceptarAmigogemma@example.com",
				PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		SeleniumUtils.textoNoPresentePagina(driver, "Gemma");

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonLogout", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		PO_LoginView.fillForm(driver, "raul@gmail.com", "123456");

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "mListaPeticiones", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonAceptarAmigogemma@example.com",
				PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "mListaAmistades", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		PO_View.checkElement(driver, "text", "Gemma");
	}

	// 8.1 Listar los amigos de un usuario, realizar la comprobación con una lista
	// que al menos tenga un amigo.
	@Test
	public void ListAmiVal() {
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonLogin", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();
		PO_LoginView.fillForm(driver, "gemma@example.com", "1234");

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "mListaAmistades", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

		PO_View.checkElement(driver, "text", "Raul");
		PO_View.checkElement(driver, "text", "Nacho");
		PO_View.checkElement(driver, "text", "Martin");
	}

	// C1.1 Inicio de sesión con datos válidos
	@Test
	public void CInVal() {
		driver.navigate().to("http://localhost:8081/cliente.html");

		WebElement email = driver.findElement(By.name("email"));
		email.click();
		email.clear();
		email.sendKeys("gemma@example.com");
		WebElement password = driver.findElement(By.name("password"));
		password.click();
		password.clear();
		password.sendKeys("1234");
		By boton = By.id("boton-login");
		driver.findElement(boton).click();

		PO_View.checkElement(driver, "text", "Nombre");
	}

	// C1.2 Inicio de sesión con datos inválidos (usuario no existente en la
	// aplicación)
	@Test
	public void CInInVal() {
		driver.navigate().to("http://localhost:8081/cliente.html");

		WebElement email = driver.findElement(By.name("email"));
		email.click();
		email.clear();
		email.sendKeys("gemm@example.com");
		WebElement password = driver.findElement(By.name("password"));
		password.click();
		password.clear();
		password.sendKeys("1234");
		By boton = By.id("boton-login");
		driver.findElement(boton).click();

		PO_View.checkElement(driver, "text", "Usuario no encontrado");
	}

	// C2.1 Acceder a la lista de amigos de un usuario, que al menos tenga tres
	// amigos
	@Test
	public void CListAmiVal() {
		driver.navigate().to("http://localhost:8081/cliente.html");

		WebElement email = driver.findElement(By.name("email"));
		email.click();
		email.clear();
		email.sendKeys("gemma@example.com");
		WebElement password = driver.findElement(By.name("password"));
		password.click();
		password.clear();
		password.sendKeys("1234");
		By boton = By.id("boton-login");
		driver.findElement(boton).click();

		SeleniumUtils.esperarSegundos(driver, 5);

		PO_View.checkElement(driver, "text", "Raul");
		PO_View.checkElement(driver, "text", "Nacho");
		PO_View.checkElement(driver, "text", "Martin");
	}

	// C2.2 Acceder a la lista de amigos de un usuario, y realizar un filtrado para
	// encontrar a un amigo concreto, el nombre a buscar debe coindicir con el de un
	// amigo
	@Test
	public void CListAmiFil() {
		driver.navigate().to("http://localhost:8081/cliente.html");

		WebElement email = driver.findElement(By.name("email"));
		email.click();
		email.clear();
		email.sendKeys("gemma@example.com");
		WebElement password = driver.findElement(By.name("password"));
		password.click();
		password.clear();
		password.sendKeys("1234");
		By boton = By.id("boton-login");
		driver.findElement(boton).click();

		SeleniumUtils.esperarSegundos(driver, 5);

		WebElement filtro = driver.findElement(By.id("filtro-nombre"));
		filtro.click();
		filtro.clear();
		filtro.sendKeys("Nacho");

		SeleniumUtils.textoNoPresentePagina(driver, "Raul");
		PO_View.checkElement(driver, "text", "Nacho");
	}

	// C3.1 Acceder a la lista de mensajes de un amigo "chat", la lista debe
	// contener al menos tres mensajes
	@Test
	public void CListMenVal() {
		driver.navigate().to("http://localhost:8081/cliente.html");

		WebElement email = driver.findElement(By.name("email"));
		email.click();
		email.clear();
		email.sendKeys("gemma@example.com");
		WebElement password = driver.findElement(By.name("password"));
		password.click();
		password.clear();
		password.sendKeys("1234");
		By boton = By.id("boton-login");
		driver.findElement(boton).click();

		SeleniumUtils.esperarSegundos(driver, 8);

		WebElement amigo = driver.findElement(By.linkText("Nacho"));
		amigo.click();

		SeleniumUtils.esperarSegundos(driver, 3);
		
		PO_View.checkElement(driver, "text", "Chat");
		PO_View.checkElement(driver, "text", "Mensaje para test CCrearMenVal1");
		PO_View.checkElement(driver, "text", "Mensaje para test CCrearMenVal2");
		PO_View.checkElement(driver, "text", "Mensaje para test CCrearMenVal3");

	}

	// C4.1 Acceder a la lista de mensajes de un amigo "chat" y crear un nuevo
	// mensaje, validar que el mensaje aparece en la lista de mensajes
	@Test
	public void CCrearMenVal() {
		driver.navigate().to("http://localhost:8081/cliente.html");

		WebElement email = driver.findElement(By.name("email"));
		email.click();
		email.clear();
		email.sendKeys("gemma@example.com");
		WebElement password = driver.findElement(By.name("password"));
		password.click();
		password.clear();
		password.sendKeys("1234");
		By boton = By.id("boton-login");
		driver.findElement(boton).click();

		SeleniumUtils.esperarSegundos(driver, 8);

		WebElement amigo = driver.findElement(By.linkText("Nacho"));
		amigo.click();

		SeleniumUtils.esperarSegundos(driver, 4);

		WebElement mensaje = driver.findElement(By.id("agregar-texto"));
		mensaje.click();
		mensaje.clear();
		mensaje.sendKeys("Mensaje para test CCrearMenVal1");
		boton = By.id("boton-agregar");
		driver.findElement(boton).click();
		SeleniumUtils.esperarSegundos(driver, 5);
		PO_View.checkElement(driver, "text", "Mensaje para test CCrearMenVal1");

		mensaje = driver.findElement(By.id("agregar-texto"));
		mensaje.click();
		mensaje.clear();
		mensaje.sendKeys("Mensaje para test CCrearMenVal2");
		boton = By.id("boton-agregar");
		driver.findElement(boton).click();
		SeleniumUtils.esperarSegundos(driver, 5);

		PO_View.checkElement(driver, "text", "Mensaje para test CCrearMenVal2");

		SeleniumUtils.esperarSegundos(driver, 3);
		
		mensaje = driver.findElement(By.id("agregar-texto"));
		mensaje.click();
		mensaje.clear();
		mensaje.sendKeys("Mensaje para test CCrearMenVal3");
		boton = By.id("boton-agregar");
		driver.findElement(boton).click();
		SeleniumUtils.esperarSegundos(driver, 8);

		PO_View.checkElement(driver, "text", "Mensaje para test CCrearMenVal3");
	}

	// C5.1 Identificarse en la aplicación y enviar un mensaje a un amigo, validar
	// que el mensaje enviado aparece en el chat. Identificarse después con el
	// usuario que ha recibido el mensaje y validar que tiene un mensaje sin leer,
	// entrar en el chat y comprobar que el mensaje pasa a tener estado leído
	@Test
	public void CMenLeidoVal() {
		driver.navigate().to("http://localhost:8081/cliente.html");

		WebElement email = driver.findElement(By.name("email"));
		email.click();
		email.clear();
		email.sendKeys("gemma@example.com");
		WebElement password = driver.findElement(By.name("password"));
		password.click();
		password.clear();
		password.sendKeys("1234");
		By boton = By.id("boton-login");
		driver.findElement(boton).click();

		SeleniumUtils.esperarSegundos(driver, 8);

		WebElement amigo = driver.findElement(By.linkText("Raul"));
		amigo.click();

		SeleniumUtils.esperarSegundos(driver, 4);
		
		WebElement mensaje = driver.findElement(By.id("agregar-texto"));
		mensaje.click();
		mensaje.clear();
		mensaje.sendKeys("Mensaje para test CMenLeidoVal");
		boton = By.id("boton-agregar");
		driver.findElement(boton).click();
		SeleniumUtils.esperarSegundos(driver, 5);
		PO_View.checkElement(driver, "text", "Mensaje para test CMenLeidoVal");
		SeleniumUtils.esperarSegundos(driver, 3);
		
		driver.navigate().to("http://localhost:8081/cliente.html?w=login");
		
		email = driver.findElement(By.name("email"));
		email.click();
		email.clear();
		email.sendKeys("raul@gmail.com");
		password = driver.findElement(By.name("password"));
		password.click();
		password.clear();
		password.sendKeys("123456");
		boton = By.id("boton-login");
		driver.findElement(boton).click();

		SeleniumUtils.esperarSegundos(driver, 8);
		
		PO_View.checkElement(driver, "text", "1 mensajes sin leer");
		
		amigo = driver.findElement(By.linkText("Gemma"));
		amigo.click();

		SeleniumUtils.esperarSegundos(driver, 4);
		
		PO_View.checkElement(driver, "text", "true");
	}

	// C6.1 Identificarse en la aplicación y enviar tres mensajes a un amigo,
	// validar que los mensajes enviados aparecen en el chat. Identificarse después
	// con el usuario que ha recibido el mensaje y validar que el número de mensajes
	// sin leer aparece en la propia lista de amigos
	@Test
	public void CListaMenNoLeidoVal() {
		driver.navigate().to("http://localhost:8081/cliente.html");

		WebElement email = driver.findElement(By.name("email"));
		email.click();
		email.clear();
		email.sendKeys("gemma@example.com");
		WebElement password = driver.findElement(By.name("password"));
		password.click();
		password.clear();
		password.sendKeys("1234");
		By boton = By.id("boton-login");
		driver.findElement(boton).click();

		SeleniumUtils.esperarSegundos(driver, 8);

		WebElement amigo = driver.findElement(By.linkText("Martin"));
		amigo.click();

		SeleniumUtils.esperarSegundos(driver, 4);
		
		WebElement mensaje = driver.findElement(By.id("agregar-texto"));
		mensaje.click();
		mensaje.clear();
		mensaje.sendKeys("Mensaje para test CListaMenNoLeidoVal1");
		boton = By.id("boton-agregar");
		driver.findElement(boton).click();
		SeleniumUtils.esperarSegundos(driver, 5);
		PO_View.checkElement(driver, "text", "Mensaje para test CListaMenNoLeidoVal1");
		SeleniumUtils.esperarSegundos(driver, 3);
		
		mensaje = driver.findElement(By.id("agregar-texto"));
		mensaje.click();
		mensaje.clear();
		mensaje.sendKeys("Mensaje para test CListaMenNoLeidoVal2");
		boton = By.id("boton-agregar");
		driver.findElement(boton).click();
		SeleniumUtils.esperarSegundos(driver, 5);
		PO_View.checkElement(driver, "text", "Mensaje para test CListaMenNoLeidoVal2");
		SeleniumUtils.esperarSegundos(driver, 3);
		
		mensaje = driver.findElement(By.id("agregar-texto"));
		mensaje.click();
		mensaje.clear();
		mensaje.sendKeys("Mensaje para test CListaMenNoLeidoVal3");
		boton = By.id("boton-agregar");
		driver.findElement(boton).click();
		SeleniumUtils.esperarSegundos(driver, 5);
		PO_View.checkElement(driver, "text", "Mensaje para test CListaMenNoLeidoVal3");
		SeleniumUtils.esperarSegundos(driver, 3);
		
		driver.navigate().to("http://localhost:8081/cliente.html?w=login");
		
		email = driver.findElement(By.name("email"));
		email.click();
		email.clear();
		email.sendKeys("martin@gmail.com");
		password = driver.findElement(By.name("password"));
		password.click();
		password.clear();
		password.sendKeys("123456");
		boton = By.id("boton-login");
		driver.findElement(boton).click();

		SeleniumUtils.esperarSegundos(driver, 5);
		
		PO_View.checkElement(driver, "text", "3 mensajes sin leer");
	}

	// C7.1 Identificarse con un usuario A que al menos tenga 3 amigos, ir al chat
	// del último amigo de la lista y enviarle un mensaje, volver a la lista de
	// amigos y comprobar que el usuario al que se le ha enviado el mensaje está en
	// primera posición. Identificarse con el usuario B y enviarle un mensaje al
	// usuario A. Volver a identificarse con el ususario A y ver que el ususario que
	// acaba de mandarle el mensaje es el primero en su lista de amigos
	@Test
	public void COrdenMenVal() {
		// TODO
	}
}
