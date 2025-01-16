import { test, expect } from '@playwright/test';

test('get all cities', async ({ page }) => {
  await page.goto('http://localhost:3000/dashboard');
  await page.getByRole('tab', { name: 'Cities' }).click();
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'New York'
  );
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'Paris'
  );
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'Rome'
  );
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'Tokyo'
  );
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'Toronto'
  );
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'Berlin'
  );
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'Sydney'
  );
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'Mumbai'
  );
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'Beijing'
  );
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'Rio de Janeiro'
  );
  await page.close();
});

test('view city details page', async ({ page }) => {
  await page.goto('http://localhost:3000/dashboard');
  await page.getByRole('tab', { name: 'Cities' }).click();
  await page.getByRole('cell', { name: 'New York' }).click();
  await page.close();
});

test('add a city', async ({ page }) => {
  await page.goto('http://localhost:3000/dashboard');
  await page.getByRole('tab', { name: 'Cities' }).click();
  await page.getByRole('button', { name: 'Create' }).click();
  await page.getByRole('textbox').click();
  await page.getByRole('textbox').fill('Longueil');
  await page
    .getByRole('combobox')
    .selectOption('b1db23af-4f2d-4138-b623-e906f5287e90');
  await page.getByRole('button', { name: 'Save' }).click();
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'Longueil'
  );
  await page
    .getByRole('row', { name: 'Longueil Edit Delete' })
    .getByRole('button')
    .nth(1)
    .click();
  await page.getByRole('button', { name: 'Confirm' }).click();
  await page.close();
});

test('edit a city', async ({ page }) => {
  await page.goto('http://localhost:3000/dashboard');
  await page.getByRole('tab', { name: 'Cities' }).click();
  await page
    .getByRole('row', { name: 'New York Edit Delete' })
    .getByRole('button')
    .first()
    .click();
  await page.getByRole('textbox').click();
  await page.getByRole('textbox').fill('New Yorks');
  await page.getByRole('button', { name: 'Save' }).click();
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'New Yorks'
  );
  await page
    .getByRole('row', { name: 'New Yorks Edit Delete' })
    .getByRole('button')
    .first()
    .click();
  await page.getByRole('textbox').click();
  await page.getByRole('textbox').fill('New York');
  await page.getByRole('button', { name: 'Save' }).click();
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'New York'
  );
  await page.close();
});

test('delete a city', async ({ page }) => {
  await page.goto('http://localhost:3000/dashboard');
  await page.getByRole('tab', { name: 'Cities' }).click();
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'New York'
  );
  await page
    .getByRole('row', { name: 'New York Edit Delete' })
    .getByRole('button')
    .nth(1)
    .click();
  await page.getByRole('button', { name: 'Confirm' }).click();
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'Paris'
  );
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'Rome'
  );
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'Tokyo'
  );
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'Toronto'
  );
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'Berlin'
  );
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'Sydney'
  );
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'Mumbai'
  );
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'Beijing'
  );
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'Rio de Janeiro'
  );
  await page.getByRole('button', { name: 'Create' }).click();
  await page.getByRole('textbox').click();
  await page.getByRole('textbox').fill('New York');
  await page
    .getByRole('combobox')
    .selectOption('ad633b50-83d4-41f3-866a-26452bdd6f33');
  await page.getByRole('button', { name: 'Save' }).click();
  await expect(page.getByLabel('Cities').locator('tbody')).toContainText(
    'New York'
  );
  await page.close();
});
