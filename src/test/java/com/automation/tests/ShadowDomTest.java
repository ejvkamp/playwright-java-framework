package com.automation.tests;

import com.automation.base.BaseTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ShadowDomTest extends BaseTest {

	@Test
	public void interactWithShadowDom() {
		// 1. Navigate to the Shadow DOM page
		page.navigate("https://www.lambdatest.com/selenium-playground/shadow-dom");

		// 2. Interact with an element INSIDE a Shadow Root
		// The 'Name' input is inside a #shadow-root (open).
		// In Selenium, you would need to locate the host,
		// get the shadow root, then find the element.
		// In Playwright, you just ask for the element directly.

		System.out.println("Typing into Shadow DOM input...");
		Locator nameInput = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name"));

		nameInput.fill("Playwright Tester");

		// 3. Verify the value
		assertThat(nameInput).hasValue("Playwright Tester");

		System.out.println("Successfully pierced the Shadow DOM!");
	}
}
