package com.automation.tests;

import com.automation.base.BaseTest;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class IFrameTest extends BaseTest {

	@Test
	public void interactWithIFrame() {
		// 1. Navigate to the real-world demo
		page.navigate("https://www.lambdatest.com/selenium-playground/iframe-demo");

		// 2. Locate the Frame itself ("The play within a play")
		// We find the <iframe> element by its ID
		FrameLocator editorFrame = page.frameLocator("#iFrame1");

		// 3. Locate elements INSIDE the frame
		// The text editor body is inside the frame.
		// We use this CSS attribute selector because the editor doesn't use
		// a standard input field - it's a div with contenteditable attribute
		Locator editorInput = editorFrame.locator("[contenteditable=true]");

		// 4. Interact as normal
		// The editor has default text. We click to focus, clear, and type.
		editorInput.click();
		editorInput.clear();
		editorInput.fill("Hello from Playwright! This text is inside an iFrame.");

		// 5. Verify the interaction
		assertThat(editorInput).containsText("Hello from Playwright!");

		System.out.println("Successfully interacted with the iFrame!");
	}
}
