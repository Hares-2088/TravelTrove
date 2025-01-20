import { test, expect } from "@playwright/test";

test("verify role changed", async ({ page }) => {
  await page.goto("http://localhost:3000/home");
  await page.getByRole("button", { name: "Sign in" }).click();
  await page.getByLabel("Email address").click();
  await page.getByLabel("Email address").fill("admin@traveltrove.com");
  await page.getByLabel("Password").click();
  await page.getByLabel("Password").fill("Admin@123");
  await page.getByRole("button", { name: "Continue", exact: true }).click();
  await page.getByRole("link", { name: "" }).click();
  await page
    .getByRole("row", { name: "Amelia Clark amelia.clark@" })
    .getByRole("button")
    .click();
  await page
    .locator('select[name="roleId"]')
    .selectOption("rol_bGEYlXT5XYsHGhcQ");
  await page.getByRole("button", { name: "Save Changes" }).click();
  await expect(page.locator("tbody")).toContainText("Customer");

  await page.close();
});

test("verify get user by id", async ({ page }) => {
  await page.goto("http://localhost:3000/home");
  await page.getByRole("button", { name: "Sign in" }).click();
  await page.getByLabel("Email address").click();
  await page.getByLabel("Email address").fill("admin@traveltrove.com");
  await page.getByLabel("Password").click();
  await page.getByLabel("Password").fill("Admin@123");
  await page.getByRole("button", { name: "Continue", exact: true }).click();
  await page.getByRole("link", { name: "" }).click();
  await page.getByRole("link", { name: "Amelia Clark" }).click();
  await expect(page.getByRole("heading")).toContainText("Amelia Clark");
  await page.getByRole("button", { name: "Back" }).click();

  await page.close();
});
