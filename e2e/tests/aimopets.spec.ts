// const { test, expect, describe, beforeEach }  = require("@playwright/test")
import { test, expect } from "@playwright/test";

test.describe("Users with Pets E2E", () => {
  const baseUrl = "http://localhost:5173/";

  test.beforeEach(async ({ page }) => {
    await page.goto(baseUrl);
    await expect(
      page.getByRole("heading", { name: "Users with their Pets" })
    ).toBeVisible();
  });

  test("has title", async ({ page }) => {

    await expect(page).toHaveTitle("Pets");
  });

  test("Display user board", async ({ page }) => {
    await expect(
      page.getByRole("heading", { name: "Users with their Pets" })
    ).toBeVisible();
    await expect(
      page.getByText(
        "Browse users from different countries and see their beloved pets."
      )
    ).toBeVisible();
  });

  test("fetch users and verify at least one image", async ({ page }) => {
    // Click the fetch button (assuming you have a button in UserFilter)
    await page.getByRole("button", { name: /fetch/i }).click();

    // Wait for the first user card image to appear
    const firstImg = page.locator(".user-card img").first();
    await expect(firstImg).toBeVisible();

    // Validate image has a src
    const src = await firstImg.getAttribute("src");
    expect(src).toContain("https://");
  });

  test("update result count and check displayed users", async ({ page }) => {
    // Change the number of users to 10 (assuming input with placeholder or label)
    await page.locator('input[name="count"]').fill("10");

    // Fetch users
    await page.getByRole("button", { name: /fetch/i }).click();

    // Wait for all user cards
    const cards = page.locator(".user-card");
    await expect(cards).toHaveCount(10);
  });

  test("country selection filters users", async ({ page }) => {
    // Select a country from dropdown, e.g., US
    await page.locator('select[name="country"]').selectOption("US");

    // Fetch users
    await page.getByRole("button", { name: /fetch/i }).click();

    // Verify all user cards have country US
    const cards = page.locator(".user-card");
    const count = await cards.count();
    for (let i = 0; i < count; i++) {
      const countryText = await cards
        .nth(i)
        .locator(".user-card-gender-country")
        .textContent();
      expect(countryText).toContain("US");
    }
  });

  test('select country and validate user cards', async ({ page }) => {
    const selectedCountry = 'FI';

    await page.locator('input[name="count"]').fill("10");
    // Select the country from dropdown
    await page.locator('select[name="country"]').selectOption(selectedCountry);

    // Click the fetch button
    await page.getByRole('button', { name: /fetch/i }).click();

    // Wait for the user cards to appear
    const cards = page.locator('.user-card');
    await expect(cards).toHaveCount(10);  

    // Validate that each card has the correct country
    const count = await cards.count();
    for (let i = 0; i < count; i++) {
      const countryText = await cards.nth(i)
        .locator('.user-card-gender-country')
        .textContent();
      expect(countryText).toContain(selectedCountry);
    }
  });

  test("pagination works correctly", async ({ page }) => {
   
    await page.locator('input[name="count"]').fill("120");
    await page.getByRole("button", { name: /fetch/i }).click();

    // Page 1: first 5 users
    const cardsPage1 = page.locator(".user-card");
    await expect(cardsPage1).toHaveCount(25);

    // Click next page
    await page.getByRole("button", { name: /next/i }).click();
    const cardsPage2 = page.locator(".user-card");
    await expect(cardsPage2).toHaveCount(25);

    await page.getByRole("button", { name: /next/i }).click();
    const cardsPage3 = page.locator(".user-card");
    await expect(cardsPage3).toHaveCount(25);

    await page.getByRole("button", { name: /next/i }).click();
    const cardsPage4 = page.locator(".user-card");
    await expect(cardsPage4).toHaveCount(25);

    // Click next page again (page 3)
    await page.getByRole("button", { name: /next/i }).click();
    const cardsPage5 = page.locator(".user-card");
    await expect(cardsPage5).toHaveCount(20); // last 2 users
  });
});
