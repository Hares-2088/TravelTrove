import { test, expect } from "@playwright/test";


test("get all travelers", async ({ page }) => {
    await page.goto("http://localhost:3000/dashboard");
    
    await page.getByRole('tab', { name: 'Travelers' }).click();
    await expect(page.getByRole('cell', { name: 'John Doe' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Maria Rossi' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Yuki Tanaka' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Carlos Silva' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Liam Brown' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Wei Li' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Amit Sharma' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Hans Müller' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Jane Smith' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Adem Bessam' })).toBeVisible();

    await page.close();
});

test("get traveler by id", async ({ page }) => {
    await page.goto("http://localhost:3000/dashboard");


    await page.getByRole('tab', { name: 'Travelers' }).click();
    await page.getByRole('cell', { name: 'John Doe' }).click();
    await expect(page.getByRole('heading', { name: 'John Doe' })).toBeVisible();
    await expect(page.getByText('address: 456 Elm St, 654 Elm')).toBeVisible();
    await expect(page.getByText('email: johndoe@gmail.com')).toBeVisible();
    await expect(page.getByLabel('Travelers')).toBeVisible();

    await page.close();
});