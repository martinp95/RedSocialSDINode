package redSocial.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_RegisterView extends PO_NavView {
    static public void fillForm(WebDriver driver, String emailp, String namep, String passwordp, String passwordconfp) {
	WebElement dni = driver.findElement(By.name("email"));
	dni.click();
	dni.clear();
	dni.sendKeys(emailp);
	WebElement name = driver.findElement(By.name("name"));
	name.click();
	name.clear();
	name.sendKeys(namep);
	WebElement password = driver.findElement(By.name("password"));
	password.click();
	password.clear();
	password.sendKeys(passwordp);
	WebElement passwordConfirm = driver.findElement(By.name("repetedPassword"));
	passwordConfirm.click();
	passwordConfirm.clear();
	passwordConfirm.sendKeys(passwordconfp);
	// Pulsar el boton de Alta.
	By boton = By.className("btn");
	driver.findElement(boton).click();
    }
}
