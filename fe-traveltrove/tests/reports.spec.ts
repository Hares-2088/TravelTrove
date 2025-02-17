import { test, expect } from "@playwright/test";

test("download monthly booking reports pdf", async ({ page }) => {
    await page.goto('http://localhost:3000/home');
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByLabel('Email address').fill('admin@traveltrove.com');
    await page.getByLabel('Password').click();
    await page.getByLabel('Password').fill('Admin@123');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('link', { name: 'Contact Us' }).click();
    await page.getByRole('link', { name: 'Reports' }).click();
    await expect(page.getByRole('main')).toContainText('Booking Reports');
    await page.getByRole('button', { name: 'Booking Reports' }).click();
    await expect(page.getByRole('main')).toContainText('Booking Reports');
    await expect(page.getByRole('paragraph')).toContainText('Select the period and download a PDF or CSV file of booking reports.');
    await expect(page.getByRole('main')).toContainText('Download PDF');
    const downloadPromise = page.waitForEvent('download');
    await page.getByRole('button', { name: 'Download PDF' }).click();
    const download = await downloadPromise;
    await expect(download.suggestedFilename()).toMatch(/booking-report.pdf/);
    await page.close()
});

test("download monthly booking reports csv", async ({ page }) => {
    await page.goto('http://localhost:3000/home');
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByLabel('Email address').fill('admin@traveltrove.com');
    await page.getByLabel('Password').click();
    await page.getByLabel('Password').fill('Admin@123');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('link', { name: 'Contact Us' }).click();
    await page.getByRole('link', { name: 'Reports' }).click();
    await expect(page.getByRole('main')).toContainText('Booking Reports');
    await page.getByRole('button', { name: 'Booking Reports' }).click();
    await expect(page.getByRole('main')).toContainText('Booking Reports');
    await expect(page.getByRole('paragraph')).toContainText('Select the period and download a PDF or CSV file of booking reports.');
    await expect(page.getByRole('main')).toContainText('Download CSV');
    const download1Promise = page.waitForEvent('download');
    await page.getByRole('button', { name: 'Download CSV' }).click();
    const download1 = await download1Promise;
    await expect(download1.suggestedFilename()).toMatch(/booking-report.csv/);
    await page.close()
});