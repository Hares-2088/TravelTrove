import { test, expect } from "@playwright/test";


test("upload a picture", async ({ page }) => {
    await page.goto('http://localhost:3000/home');
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByLabel('Email address').fill('Admin@traveltrove.com');
    await page.getByLabel('Email address').press('Tab');
    await page.getByLabel('Password').fill('Admin@123');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('link', { name: 'Dashboard' }).click();
    await page.getByRole('row', { name: 'German Castle Escape noImage' }).getByRole('button').nth(1).click();
    await page.getByRole('textbox').click();
    await page.getByRole('textbox').setInputFiles('hero-image.jpg');
    await page.getByRole('button', { name: 'Upload' }).click();
    await page.goto('http://localhost:3000/dashboard');
    await expect(page.getByRole('img', { name: 'Tour' })).toBeVisible();
    await page.close();
});