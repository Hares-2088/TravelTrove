import { test, expect } from '@playwright/test';

test('get all hotels', async ({ page }) => {
  await page.goto('http://localhost:3000/home');
  await page.getByRole('button', { name: 'Sign in' }).click();
  await page.getByLabel('Email address').click();
  await page.getByLabel('Email address').fill('admin@traveltrove.com');
  await page.getByLabel('Password').click();
  await page.getByLabel('Password').fill('Admin@123');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await page.goto('http://localhost:3000/dashboard');
  await page.getByRole('tab', { name: 'Hotels' }).click();
  await expect(page.getByLabel('Hotels').locator('tbody')).toContainText(
    'The Plaza Hotel'
  );
  await expect(page.getByLabel('Hotels').locator('tbody')).toContainText(
    'Shangri-La Hotel, Sydney'
  );
  await expect(page.getByLabel('Hotels').locator('tbody')).toContainText(
    'The Leela Mumbai'
  );
  await expect(page.getByLabel('Hotels').locator('tbody')).toContainText(
    'Waldorf Astoria Beijing'
  );
  await expect(page.getByLabel('Hotels').locator('tbody')).toContainText(
    'The Ritz-Carlton, Toronto'
  );
  await expect(page.getByLabel('Hotels').locator('tbody')).toContainText(
    'Hotel Hassler Roma'
  );
  await expect(page.getByLabel('Hotels').locator('tbody')).toContainText(
    'Hotel Nikko Narita'
  );
  await expect(page.getByLabel('Hotels').locator('tbody')).toContainText(
    "Hôtel Barrière Le Fouquet's"
  );
  await expect(page.getByLabel('Hotels').locator('tbody')).toContainText(
    'Belmond Copacabana Palace'
  );
  await expect(page.getByLabel('Hotels').locator('tbody')).toContainText(
    'Hotel Adlon Kempinski'
  );
  await page.close();
});

test('view hotel details page', async ({ page }) => {
  await page.goto('http://localhost:3000/home');
  await page.getByRole('button', { name: 'Sign in' }).click();
  await page.getByLabel('Email address').click();
  await page.getByLabel('Email address').fill('admin@traveltrove.com');
  await page.getByLabel('Password').click();
  await page.getByLabel('Password').fill('Admin@123');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await page.goto('http://localhost:3000/dashboard');
  await page.getByRole('tab', { name: 'Hotels' }).click();
  await page.getByRole('cell', { name: 'The Plaza Hotel' }).click();
  await expect(page.getByLabel('Hotels').getByRole('heading')).toContainText(
    'The Plaza Hotel'
  );
  await expect(page.getByLabel('Hotels')).toContainText(
    'Hotel url: https://www.theplazany.com/'
  );
  await expect(page.getByLabel('Hotels')).toContainText('City: New York');
  await page.getByRole('button', { name: '← Back to List' }).click();
  await page.close();
});

test('add a hotel', async ({ page }) => {
  await page.goto('http://localhost:3000/home');
  await page.getByRole('button', { name: 'Sign in' }).click();
  await page.getByLabel('Email address').click();
  await page.getByLabel('Email address').fill('admin@traveltrove.com');
  await page.getByLabel('Password').click();
  await page.getByLabel('Password').fill('Admin@123');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await page.goto('http://localhost:3000/dashboard');
  await page.getByRole('tab', { name: 'Hotels' }).click();
  await page.getByRole('button', { name: 'Create' }).click();
  await page
    .locator('div')
    .filter({ hasText: /^Hotel NameHotel name is required\.$/ })
    .getByRole('textbox')
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^Hotel NameHotel name is required\.$/ })
    .getByRole('textbox')
    .fill('Sample Hotel 1');
  await page
    .locator('div')
    .filter({ hasText: /^Hotel urlHotel url is required\.$/ })
    .getByRole('textbox')
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^Hotel urlHotel url is required\.$/ })
    .getByRole('textbox')
    .fill('http://sample1.example');
  await page
    .getByRole('combobox')
    .selectOption('b713c09a-9c3e-4b30-872a-4d89089badd0');
  await page.getByRole('button', { name: 'Save' }).click();
  await expect(page.getByLabel('Hotels').locator('tbody')).toContainText(
    'Sample Hotel 1'
  );
  await page
    .getByRole('row', { name: 'Sample Hotel 1 Edit Delete' })
    .getByRole('button')
    .nth(1)
    .click();
  await page.getByRole('button', { name: 'Confirm' }).click();
  await page.close();
});

test('edit a hotel', async ({ page }) => {
  await page.goto('http://localhost:3000/home');
  await page.getByRole('button', { name: 'Sign in' }).click();
  await page.getByLabel('Email address').click();
  await page.getByLabel('Email address').fill('admin@traveltrove.com');
  await page.getByLabel('Password').click();
  await page.getByLabel('Password').fill('Admin@123');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await page.goto('http://localhost:3000/dashboard');
  await page.getByRole('tab', { name: 'Hotels' }).click();
  await page
    .getByRole('row', { name: 'The Plaza Hotel Edit Delete' })
    .getByRole('button')
    .first()
    .click();
  await page
    .getByRole('combobox')
    .selectOption('000f3f3a-8ee2-4690-be4d-a5bd38a5f06f');
  await page.getByRole('button', { name: 'Save' }).click();
  await page
    .getByRole('row', { name: 'The Plaza Hotel Edit Delete' })
    .getByRole('button')
    .first()
    .click();
  await page
    .getByRole('combobox')
    .selectOption('b713c09a-9c3e-4b30-872a-4d89089badd0');
  await page.getByRole('button', { name: 'Save' }).click();
  await page.close();
});

test('delete a hotel', async ({ page }) => {
  await page.goto('http://localhost:3000/home');
  await page.getByRole('button', { name: 'Sign in' }).click();
  await page.getByLabel('Email address').click();
  await page.getByLabel('Email address').fill('admin@traveltrove.com');
  await page.getByLabel('Password').click();
  await page.getByLabel('Password').fill('Admin@123');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await page.goto('http://localhost:3000/dashboard');
  await page.getByRole('tab', { name: 'Hotels' }).click();
  await page.getByRole('button', { name: 'Create' }).click();
  await page
    .locator('div')
    .filter({ hasText: /^Hotel NameHotel name is required\.$/ })
    .getByRole('textbox')
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^Hotel NameHotel name is required\.$/ })
    .getByRole('textbox')
    .fill('toBeDeleted');
  await page
    .locator('div')
    .filter({ hasText: /^Hotel urlHotel url is required\.$/ })
    .getByRole('textbox')
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^Hotel urlHotel url is required\.$/ })
    .getByRole('textbox')
    .fill('temp');
  await page
    .getByRole('combobox')
    .selectOption('b713c09a-9c3e-4b30-872a-4d89089badd0');
  await page.getByRole('button', { name: 'Save' }).click();
  await expect(page.getByLabel('Hotels').locator('tbody')).toContainText(
    'toBeDeleted'
  );
  await page
    .getByRole('row', { name: 'toBeDeleted Edit Delete' })
    .getByRole('button')
    .nth(1)
    .click();
  await page.getByRole('button', { name: 'Confirm' }).click();
  await page.close();
});
