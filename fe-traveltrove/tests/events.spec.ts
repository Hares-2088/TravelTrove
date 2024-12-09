import { test, expect } from '@playwright/test';

test('get all events', async ({ page }) => {
    // Go to the dashboard page
    await page.goto('http://localhost:3000/dashboard');
    await page.getByRole('tab', { name: 'Events' }).click();
    await expect(page.getByRole('cell', { name: 'Spring Festival' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Wine Tasting Retreat' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Music Gala Night' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'City Marathon' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Tech Expo' })).toBeVisible();


    await page.close()
});