package redSocial.tests.pageobjects;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import redSocial.tests.utils.SeleniumUtils;

public class PO_NavView extends PO_View {
    /**
     * CLicka una de las opciones principales (a href) y comprueba que se vaya a la
     * vista con el elemento de tipo type con el texto Destino
     * 
     * @param driver:
     *            apuntando al navegador abierto actualmente.
     * @param textOption:
     *            Texto de la opci�n principal.
     * @param criterio:
     *            "id" or "class" or "text" or "@attribute" or "free". Si el valor
     *            de criterio es free es una expresion xpath completa.
     * @param textoDestino:
     *            texto correspondiente a la b�squeda de la p�gina destino.
     */
    public static void clickOption(WebDriver driver, String textOption, String criterio, String textoDestino) {
	// CLickamos en la opci�n de registro y esperamos a que se cargue el
	// enlace de Registro.
	List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "@href", textOption, getTimeout());
	// Tiene que haber un s�lo elemento.
	assertTrue(elementos.size() == 1);
	// Ahora lo clickamos
	elementos.get(0).click();
	// Esperamos a que sea visible un elemento
	// concreto
	elementos = SeleniumUtils.EsperaCargaPagina(driver, criterio, textoDestino, getTimeout());
	// Tiene que haber un s�lo elemento.
	assertTrue(elementos.size() == 1);
    }

    /**
     * Selecciona el enlace de idioma correspondiente al texto textLanguage
     * 
     * @param driver:
     *            apuntando al navegador abierto actualmente.
     * @param textLanguage:
     *            el texto que aparece en el enlace de idioma ("English" o
     *            "Spanish")
     */
    public static void changeIdiom(WebDriver driver, String textLanguage) {
	// clickamos la opci�n Idioma.
	List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "btnLanguage", getTimeout());
	elementos.get(0).click();
	// Esperamos a que aparezca el men� de opciones.
	elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "languageDropdownMenuButton", getTimeout());
	// SeleniumUtils.esperarSegundos(driver, 2);
	// CLickamos la opci�n Ingl�s partiendo de la opci�n Espa�ol
	elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", textLanguage, getTimeout());
	elementos.get(0).click();
    }
}
