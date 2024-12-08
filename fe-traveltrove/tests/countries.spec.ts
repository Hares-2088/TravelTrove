import { test, expect } from '@playwright/test';

test('get all countries', async ({ page }) => {
await page.goto('http://localhost:3000/dashboard');
await page.getByRole('tab', { name: 'Countries' }).click();
await expect(page.getByLabel('Countries').locator('tbody')).toContainText('United States');
await expect(page.getByLabel('Countries').locator('tbody')).toContainText('France');
await expect(page.getByLabel('Countries').locator('tbody')).toContainText('Italy');
await expect(page.getByLabel('Countries').locator('tbody')).toContainText('Brazil');
await expect(page.getByLabel('Countries').locator('tbody')).toContainText('Australia');
await expect(page.getByLabel('Countries').locator('tbody')).toContainText('India');
await expect(page.getByLabel('Countries').locator('tbody')).toContainText('Japan');
await expect(page.getByLabel('Countries').locator('tbody')).toContainText('China');
await expect(page.getByLabel('Countries').locator('tbody')).toContainText('Canada');
await expect(page.getByLabel('Countries').locator('tbody')).toContainText('Germany');
await page.close()
});

test('add a country', async ({ page }) => {
await page.goto('http://localhost:3000/dashboard');
await page.getByRole('tab', { name: 'Countries' }).click();
await page.getByRole('button', { name: 'Create' }).click();
await page.locator('div').filter({ hasText: /^Country Name$/ }).getByRole('textbox').click();
await page.locator('div').filter({ hasText: /^Country Name$/ }).getByRole('textbox').fill('Peru');
await page.locator('div').filter({ hasText: /^Country Image URL$/ }).getByRole('textbox').click();
await page.locator('div').filter({ hasText: /^Country Image URL$/ }).getByRole('textbox').fill('peru.png');
await page.getByRole('button', { name: 'Save' }).click();
await expect(page.getByLabel('Countries').locator('tbody')).toContainText('Peru');
await page.close()
});

test('get country by countryId', async ({ page }) => {
await page.goto('http://localhost:3000/dashboard');
await page.getByRole('tab', { name: 'Countries' }).click();
await page.getByRole('cell', { name: 'United States' }).click();
await expect(page.getByLabel('Countries').getByRole('heading')).toContainText('United States');
await expect(page.getByLabel('Countries')).toContainText('Country ID: ad633b50-83d4-41f3-866a-26452bdd6f33');
await expect(page.getByLabel('Countries')).toContainText('Image: usa.png');
await page.close()
});

test('edit a country', async ({ page }) => {
await page.goto('http://localhost:3000/dashboard');
await page.getByRole('tab', { name: 'Countries' }).click();
await page.getByRole('row', { name: 'United States Edit Delete' }).getByRole('button').first().click();
await page.locator('div').filter({ hasText: /^Country Name$/ }).getByRole('textbox').click();
await page.locator('div').filter({ hasText: /^Country Name$/ }).getByRole('textbox').fill('United States of America');
await page.locator('div').filter({ hasText: /^Country Image URL$/ }).getByRole('textbox').click();
await page.locator('div').filter({ hasText: /^Country Image URL$/ }).getByRole('textbox').fill('united-states.png');
await page.getByRole('button', { name: 'Save' }).click();
await expect(page.getByLabel('Countries').locator('tbody')).toContainText('United States of America');
await page.getByRole('row', { name: 'United States of America Edit' }).getByRole('button').first().click();
await page.locator('div').filter({ hasText: /^Country Name$/ }).getByRole('textbox').click();
await page.locator('div').filter({ hasText: /^Country Name$/ }).getByRole('textbox').fill('United States');
await page.locator('div').filter({ hasText: /^Country Image URL$/ }).getByRole('textbox').click();
await page.locator('div').filter({ hasText: /^Country Image URL$/ }).getByRole('textbox').fill('united-states.png');
await page.getByRole('button', { name: 'Save' }).click();
await expect(page.getByLabel('Countries').locator('tbody')).toContainText('United States');
await page.close()
});

test('delete a country', async ({ page }) => {
await page.goto('http://localhost:3000/dashboard');
await page.getByRole('tab', { name: 'Countries' }).click();
await page.getByRole('row', { name: 'United States Edit Delete' }).getByRole('button').nth(1).click();
await page.getByRole('button', { name: 'Confirm' }).click();
await page.getByRole('button', { name: 'Create' }).click();
await page.locator('div').filter({ hasText: /^Country Name$/ }).getByRole('textbox').click();
await page.locator('div').filter({ hasText: /^Country Name$/ }).getByRole('textbox').fill('United States');
await page.locator('div').filter({ hasText: /^Country Image URL$/ }).getByRole('textbox').click();
await page.locator('div').filter({ hasText: /^Country Image URL$/ }).getByRole('textbox').fill('usa.png');
await page.getByRole('button', { name: 'Save' }).click();
await page.close()
});