import { test, expect } from "@playwright/test";


test("get all airports", async ({ page }) => {

  await page.goto("http://localhost:3000/dashboard");
  await page.getByRole("tab", { name: "Airports" }).click();

  await expect(page.getByLabel("Airports").locator("tbody")).toContainText(
    "Rio de Janeiro–Galeão International Airport"
  );
  await expect(page.getByLabel("Airports").locator("tbody")).toContainText(
    "Charles de Gaulle Airport"
  );
  await page.close();
});

test("get airport by id", async ({ page }) => {
  await page.goto("http://localhost:3000/dashboard");
  await page.getByRole("tab", { name: "Airports" }).click();
  await expect(page.getByLabel("Airports").locator("tbody")).toContainText(
    "Rio de Janeiro–Galeão International Airport"
  );
  await page
    .getByRole("cell", { name: "Rio de Janeiro–Galeão International Airport" })
    .click();
  await expect(
    page.getByRole("heading", { name: "Rio de Janeiro–Galeão International Airport" })
  ).toBeVisible();
  await page.close();
});

test("create an airport", async ({ page }) => {
  await page.goto("http://localhost:3000/dashboard");

  // Click on the 'Airports' tab
  await page.getByRole("tab", { name: "Airports" }).click();

  // Verify the 'Create' button is visible
  await expect(page.getByRole("button", { name: "Create" })).toBeVisible();

  // Click the 'Create' button
  await page.getByRole("button", { name: "Create" }).click();

  // Fill in the form
  await page.getByRole("textbox").fill("test");
  await page
    .getByRole("combobox")
    .selectOption("b713c09a-9c3e-4b30-872a-4d89089badd0");
  await page.getByRole("button", { name: "Save" }).click();

  // Close the page
  await page.close();
});

test("edit an airport", async ({ page }) => {
  await page.goto("http://localhost:3000/dashboard");
  await page.getByRole("tab", { name: "Airports" }).click();
  await expect(
    page
      .getByRole("row", { name: "John F. Kennedy International" })
      .getByRole("button", { name: "Edit" })
  ).toBeVisible();
  await page
    .getByRole("row", { name: "John F. Kennedy International" })
    .getByRole("button")
    .first()
    .click();
  await page.getByRole("textbox").click();
  await page
    .getByRole("textbox")
    .fill("John F. Kennedy International A123irport1");
  await page
    .getByRole("combobox")
    .selectOption("b713c09a-9c3e-4b30-872a-4d89089badd0");
  await page.getByRole("button", { name: "Save" }).click();
  await page.close();
});

test("delete an airport", async ({ page }) => {
  await page.goto("http://localhost:3000/dashboard");
  await page.getByRole("tab", { name: "Airports" }).click();

  // Verify the Delete button is visible
  await expect(
    page
      .getByRole("row", { name: "Berlin Brandenburg Airport" })
      .getByRole("button", { name: "Delete" })
  ).toBeVisible();

  // Click the Delete button
  await page
    .getByRole("row", { name: "Berlin Brandenburg Airport" })
    .getByRole("button", { name: "Delete" })
    .click();

  // Confirm the deletion
  await page.getByRole("button", { name: "Confirm" }).click();

  // Verify the row is removed
  await expect(
    page.getByRole("row", { name: "Berlin Brandenburg Airport" })
  ).toHaveCount(0);

  // Create a new airport
  await page.getByRole("button", { name: "Create" }).click();
  await page.getByRole("textbox").fill("Berlin Brandenburg Airport");
  await page
    .getByRole("combobox")
    .selectOption("000f3f3a-8ee2-4690-be4d-a5bd38a5f06f");
  await page.getByRole("button", { name: "Save" }).click();

  // Verify the new airport is created
  await expect(
    page.getByRole("row", { name: "Berlin Brandenburg Airport" })
  ).toBeVisible();

  await page.close();
});
