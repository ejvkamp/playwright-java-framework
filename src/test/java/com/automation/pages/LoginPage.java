package com.automation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Allure;

import static org.testng.Assert.assertTrue;

public class LoginPage {
	private static final double LOGIN_OUTCOME_TIMEOUT_MS = 15_000;

	private final Locator emailInput;
	private final Locator passwordInput;
	private final Locator loginBtn;

	// Outcome signals (Used for deterministic waiting)
	private final Locator alertDanger;
	private final Locator myAccountHeading;

	public LoginPage(Page page) {
		// Input Fields (Bullseye: User-Facing Labels)
		this.emailInput = page.getByLabel("E-Mail Address");
		this.passwordInput = page.getByLabel("Password");
		this.loginBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login"));

		// Status Elements (For validation)
		this.alertDanger = page.locator(".alert-danger");
		this.myAccountHeading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("My Account"));
	}

	public void login(String email, String password) {
		Allure.step("Login with configured user", () -> {
			emailInput.fill(email);
			passwordInput.fill(password);
			loginBtn.click();
			// ROBUST WAIT STRATEGY:
			// Wait for either Success OR Failure to appear.
			// .or() creates a meta-locator that matches either condition.
			myAccountHeading.or(alertDanger).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE)
					.setTimeout(LOGIN_OUTCOME_TIMEOUT_MS));
		});
	}

	public void verifyLoginSuccess() {
		Allure.step("Verify successful login", () -> {
			// Defensive Wait: Ensure page has settled before checking state.
			myAccountHeading.or(alertDanger).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE)
					.setTimeout(LOGIN_OUTCOME_TIMEOUT_MS));

			// 1. Check for application errors first
			if (alertDanger.isVisible()) {
				String error = alertDanger.textContent();
				throw new AssertionError(
						"Login failed with application error: " + (error == null ? "<no message>" : error.trim()));
			}

			// 2. Verify Success
			assertTrue(myAccountHeading.isVisible(), "My Account header should be visible after login");
		});
	}

} // end of LoginPage
