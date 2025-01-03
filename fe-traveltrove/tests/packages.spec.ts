import { test, expect } from "@playwright/test";


test("get the packages for a tour", async ({ page }) => {
    await page.goto('http://localhost:3000/dashboard');

    await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
    await expect(page.getByRole('cell', { name: 'New York Adventure Package' })).toBeVisible();
    await page.close();
});

test("add a package to a tour and then delete it", async ({ page }) => {
    await page.goto('http://localhost:3000/dashboard');
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByLabel('Email address').fill('admin@traveltrove.com');
    await page.getByLabel('Password').click();
    await page.getByLabel('Password').fill('Admin@123');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('link', { name: 'Dashboard' }).click();
    await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
    await page.getByRole('button', { name: 'Create Package' }).click();
    await page.locator('input[type="text"]').click();
    await page.locator('input[type="text"]').fill('Adem is having fun');
    await page.locator('textarea').click();
    await page.locator('textarea').fill('yes i really do');
    await page.locator('div').filter({ hasText: /^startDatestartDateBeforeEndDate$/ }).getByRole('textbox').fill('2024-12-18');
    await page.locator('div').filter({ hasText: /^endDatestartDateBeforeEndDate$/ }).getByRole('textbox').fill('2025-01-02');
    await page.locator('div').filter({ hasText: /^priceSinglepriceSingleRequired$/ }).getByRole('spinbutton').click();
    await page.locator('div').filter({ hasText: /^priceDouble$/ }).getByRole('spinbutton').click();
    await page.locator('div').filter({ hasText: /^priceTriple$/ }).getByRole('spinbutton').click();
    await page.getByRole('combobox').selectOption('9273ecac-b84d-41e9-9d5f-28f0bd1e467b');

    await page.getByRole('button', { name: 'Save' }).click();
    await expect(page.getByRole('cell', { name: 'Adem is having fun' })).toBeVisible();
    await page.getByRole('cell', { name: 'Adem is having fun' }).click();
    await expect(page.getByText('packageName: Adem is having funpackageDescription: yes i really dostartDate:')).toBeVisible();
    await page.getByLabel('Close').click();
    await page.getByRole('button', { name: 'Delete Package' }).nth(1).click();
    await page.getByRole('button', { name: 'Confirm' }).click();

    await page.close();
});

test("edit a package for a tour", async ({ page }) => {
    await page.goto('http://localhost:3000/dashboard');    

    await page.getByRole('button', { name: 'Sign in' }).click();

    await page.getByLabel('Email address').click();
    await page.getByLabel('Email address').fill('admin@traveltrove.com');

    await page.getByLabel('Password').click();
    await page.getByLabel('Password').fill('Admin@123');

    await page.getByLabel('Password').click();
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('link', { name: 'Dashboard' }).click();
    await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
    await page.getByRole('button', { name: 'Edit Package' }).click();
    await page.locator('input[type="text"]').click();
    await page.locator('input[type="text"]').fill('New York Adventure Package2');
    await page.getByRole('button', { name: 'Save' }).click();
    await expect(page.getByRole('cell', { name: 'New York Adventure Package2' })).toBeVisible();
    await page.getByRole('button', { name: 'Edit Package' }).click();
    await page.locator('input[type="text"]').click();
    await page.locator('input[type="text"]').fill('New York Adventure Package');
    await page.getByRole('button', { name: 'Save' }).click();

    await expect(page.getByRole('cell', { name: 'New York Adventure Package' })).toBeVisible();    
    await page.close();
});