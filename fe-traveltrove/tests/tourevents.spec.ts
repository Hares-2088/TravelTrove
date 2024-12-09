import { test, expect } from "@playwright/test";

test("get a tour event", async ({ page }) => {
    await page.goto('http://localhost:3000/dashboard');
    await page.getByRole('button', { name: '← Back to List' }).click();
    await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
    await page.getByText('Tour EventsCreate').click();
    await page.getByRole('button', { name: '← Back to List' }).click();
    await page.getByRole('cell', { name: 'Italian Culinary Retreat' }).click();
    await page.getByRole('button', { name: '← Back to List' }).click();
    await page.getByRole('cell', { name: 'Brazilian Amazon Adventure' }).click();
    await page.getByRole('button', { name: '← Back to List' }).click();
    await page.getByRole('cell', { name: 'Japan Cherry Blossom Tour' }).click();
    await page.getByRole('button', { name: '← Back to List' }).click();
    await page.getByRole('cell', { name: 'Australian Outback Expedition' }).click();
    await page.getByRole('button', { name: '← Back to List' }).click();
    await page.getByRole('cell', { name: 'Indian Heritage Journey' }).click();
    await page.getByRole('button', { name: '← Back to List' }).click();
    await page.getByRole('cell', { name: 'Chinese Silk Road Adventure' }).click();
    await page.getByRole('button', { name: '← Back to List' }).click();
    await page.getByRole('cell', { name: 'German Castle Escape' }).click();
    await page.getByRole('button', { name: '← Back to List' }).click();
    await page.getByRole('cell', { name: 'Canadian Rockies Expedition' }).click();
    await page.getByRole('button', { name: '← Back to List' }).click();
    await page.getByRole('cell', { name: 'Parisian Art & Culture Tour' }).click();
    await page.getByRole('button', { name: '← Back to List' }).click();
    await page.close();
});


test("add a tour event", async ({ page }) => {
    await page.goto('http://localhost:3000/dashboard');
    await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
    await page.getByRole('button', { name: 'Create' }).click();
    await page.getByRole('textbox').click();
    await page.getByRole('combobox').selectOption('91c940b1-24e8-463f-96ef-f54f7e4aaf1d');
    await page.getByRole('button', { name: 'Save' }).click();
    await page.getByRole('button', { name: '← Back to List' }).click();
    await page.close();
});



test("edit a tour event", async ({ page }) => {
    await page.goto('http://localhost:3000/dashboard');
    await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
    await page.getByRole('button', { name: 'Edit' }).first().click();
    await page.getByRole('combobox').selectOption('87b3b478-031c-49d2-b64e-b54a49578d8c');
    await page.getByRole('button', { name: 'Save' }).click();
    await page.getByRole('button', { name: 'Edit' }).first().click();
    await page.getByRole('spinbutton').click();
    await page.getByRole('textbox').click();
    await page.getByRole('textbox').fill('Start at New York Summer Carival');
    await page.getByRole('button', { name: 'Save' }).click();
    await page.getByRole('button', { name: '← Back to List' }).click();
    await page.close();
});


test("delete a tour event", async ({ page }) => {
    await page.goto('http://localhost:3000/dashboard');
    await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
    await page.getByRole('button', { name: 'Delete' }).first().click();
    await page.getByRole('button', { name: 'Confirm' }).click();
    await page.getByRole('button', { name: 'Delete' }).click();
    await page.getByRole('button', { name: 'Confirm' }).click();
    await page.getByRole('button', { name: '← Back to List' }).click();
    await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
    await page.getByRole('button', { name: '← Back to List' }).click();
    await page.close();
});