import { test, expect } from "@playwright/test";
import fs from "fs";
import path from "path";

test("download payments csv", async ({ page }) => {
  await page.goto("http://localhost:3000/home");
  await page.getByRole("button", { name: "Sign in" }).click();
  await page.getByLabel("Email address").click();
  await page.getByLabel("Email address").fill("admin@traveltrove.com");
  await page.getByLabel("Password").click();
  await page.getByLabel("Password").fill("Admin@123");
  await page.getByRole("button", { name: "Continue", exact: true }).click();
  await expect(page.locator("#basic-navbar-nav")).toContainText("Reports");
  await page.getByRole("link", { name: "Reports" }).click();
  await page.getByRole("button", { name: "Payment Reports" }).click();
  await expect(page.locator("form")).toContainText("Download Report");
  const downloadPromise = page.waitForEvent("download");
  await page.getByRole("button", { name: "Download Report" }).click();
  const download = await downloadPromise;

  const suggestedFilename = download.suggestedFilename();
  console.log("Downloaded file:", suggestedFilename);
  expect(suggestedFilename).toMatch(/revenue-report-\d{4}-?.*\.csv/);

  const savePath = path.join(__dirname, suggestedFilename);

  // Save the file
  await download.saveAs(savePath);
  console.log(`File saved at: ${savePath}`);

  // Verify that the file exists
  expect(fs.existsSync(savePath)).toBeTruthy();

  // Read the file content
  const fileContent = fs.readFileSync(savePath, "utf-8");

  expect(fileContent).toContain(
    "Payment ID,Amount (USD),Currency,Status,Payment Date"
  );

  fs.unlinkSync(savePath);
  console.log("File deleted after verification");

  await page.close();
});
