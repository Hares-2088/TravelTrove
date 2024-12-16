import { test, expect } from '@playwright/test';

test('get all tour events', async ({ page }) => {
    await page.goto('http://localhost:3000/home');
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByLabel('Email address').click();
    await page.getByLabel('Email address').fill('a');
    await page.getByLabel('Password').click();
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.goto('http://localhost:3000/dashboard');
    await page.getByRole('cell', { name: 'Parisian Art & Culture Tour' }).click();
    await page.getByRole('cell', { name: 'Sequence' }).click();
    await page.getByRole('cell', { name: '1', exact: true }).click();
    await page.close()
});


test('add a tour event', async ({ page }) => {
    await page.goto('http://localhost:3000/home');
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByLabel('Email address').click();
    await page.getByLabel('Email address').fill('a');
    await page.getByLabel('Password').click();
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.goto('http://localhost:3000/dashboard');
    await page.getByRole('row', { name: 'Parisian Art & Culture Tour' }).getByRole('cell').nth(1).click();
    await page.getByRole('cell', { name: 'Parisian Art & Culture Tour' }).click();
    await page.getByText('Tour EventsCreate').click();
    await page.getByRole('button', { name: 'Create' }).click();
    await page.getByRole('textbox').click();
    await page.getByRole('textbox').fill('g');
    await page.locator('form div').filter({ hasText: 'EventselectEventNew York' }).getByRole('combobox').selectOption('91c940b1-24e8-463f-96ef-f54f7e4aaf1d');
    await page.locator('form div').filter({ hasText: 'hotelLabelselectHotelHôtel' }).getByRole('combobox').selectOption('47c8f2e7-3d6b-402c-93b7-8a92ef43e6ab');
    await page.getByRole('button', { name: 'Save' }).click();    
    await page.close()
});



