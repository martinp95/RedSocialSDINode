package redSocial.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_LoginView extends PO_NavView {

    public static void fillForm(WebDriver driver, String dnip, String passwordp) {
	WebElement dni = driver.findElement(By.name("email"));
	dni.click();
	dni.clear();
	dni.sendKeys(dnip);
	WebElement password = driver.findElement(By.name("password"));
	password.click();
	password.clear();
	password.sendKeys(passwordp);
	By boton = By.className("btn");
	driver.findElement(boton).click();
    }

}
