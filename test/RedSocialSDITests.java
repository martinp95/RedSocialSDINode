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

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonAgregarAmigogema@gmail.com", PO_View.getTimeout());
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
		PO_LoginView.fillForm(driver, "gemma@example.com", "1234");

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

		PO_View.checkElement(driver, "text", "Gemma");
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
	}

	// 8.1 Listar los amigos de un usuario, realizar la comprobación con una lista
	// que al menos tenga un amigo.
	@Test
	public void ListAmiVal() {
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonLogin", PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();
		PO_LoginView.fillForm(driver, "gemma@example.com", "1234");

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "botonAgregarAmigoraul@gmail.com",
				PO_View.getTimeout());
		assertTrue(elementos.size() == 1);
		elementos.get(0).click();

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

}
