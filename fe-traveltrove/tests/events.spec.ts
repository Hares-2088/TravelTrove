import { test, expect } from '@playwright/test';

test('get all events', async ({ page }) => {
  await page.goto('http://localhost:3000/dashboard');
  await page.getByRole('tab', { name: 'Events' }).click();

  await expect(
    page.getByRole('cell', { name: 'Berlin Tech Conference' })
  ).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Rio Carnival' })).toBeVisible();
  await expect(
    page.getByRole('cell', { name: 'Rome Food & Wine Festival' })
  ).toBeVisible();
  await expect(
    page.getByRole('cell', { name: 'New York Summer Carnival' })
  ).toBeVisible();
  await expect(
    page.getByRole('cell', { name: 'Beijing Dragon Boat Festival' })
  ).toBeVisible();
  await expect(
    page.getByRole('cell', { name: 'Sydney New Year’s Eve' })
  ).toBeVisible();
  await expect(
    page.getByRole('cell', { name: 'Tokyo Cherry Blossom Festival' })
  ).toBeVisible();
  await expect(
    page.getByRole('cell', { name: 'Toronto Winter Lights Festival' })
  ).toBeVisible();

  await page.close();
});

test('add new event', async ({ page }) => {
  await page.goto('http://localhost:3000/dashboard');

  await page.getByRole('tab', { name: 'Events' }).click();
  await page.getByRole('button', { name: 'Create' }).click();
  await page
    .locator('div')
    .filter({ hasText: /^Event NameEvent name is required\.$/ })
    .getByRole('textbox')
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^Event NameEvent name is required\.$/ })
    .getByRole('textbox')
    .fill('Hiking');
  await page.locator('textarea').click();
  await page.locator('textarea').fill('Hiking');

  await page
    .locator('div')
    .filter({ hasText: /^Image URL$/ })
    .getByRole('textbox')
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^Image URL$/ })
    .getByRole('textbox')
    .fill('image.png');
  await page.getByRole('button', { name: 'Save' }).click();
  await page
    .getByRole('row', { name: 'Hiking Edit Delete' })
    .getByRole('button')
    .nth(1)
    .click();
  await page.getByRole('button', { name: 'Confirm' }).click();
  await page.close();
});

test('edit event', async ({ page }) => {
  await page.goto('http://localhost:3000/dashboard');
  await page.getByRole('tab', { name: 'Events' }).click();
  await expect(page.getByRole('cell', { name: 'Rio Carnival' })).toBeVisible();
  await page
    .getByRole('row', { name: 'Rio Carnival Edit Delete' })
    .getByRole('button')
    .first()
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^Event NameEvent name is required\.$/ })
    .getByRole('textbox')
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^Event NameEvent name is required\.$/ })
    .getByRole('textbox')
    .fill('Rio Carnival ');
  await page.getByRole('button', { name: 'Save' }).click();
  await page.goto('http://localhost:3000/dashboard');
  await page.getByRole('tab', { name: 'Events' }).click();
  await expect(page.getByRole('cell', { name: 'Rio Carnival' })).toBeVisible();
  await page
    .getByRole('row', { name: 'Rio Carnival Edit Delete' })
    .getByRole('button')
    .first()
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^Event NameEvent name is required\.$/ })
    .getByRole('textbox')
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^Event NameEvent name is required\.$/ })
    .getByRole('textbox')
    .fill('Rio Carnival ');
  await page.getByRole('button', { name: 'Save' }).click();
  await page.getByRole('tab', { name: 'Events' }).click();
  await expect(page.getByRole('cell', { name: 'Rio Carnival' })).toBeVisible();

  await page.close();
});

test('delete event', async ({ page }) => {
  await page.goto('http://localhost:3000/dashboard');
  await page.getByRole('tab', { name: 'Events' }).click();

  await expect(
    page.getByRole('cell', { name: 'Rome Food & Wine Festival' })
  ).toBeVisible();
  await page
    .getByRole('row', { name: 'Rome Food & Wine Festival' })
    .getByRole('button')
    .nth(1)
    .click();
  await page.getByRole('button', { name: 'Confirm' }).click();
  await page.getByRole('button', { name: 'Create' }).click();
  await page
    .locator('div')
    .filter({ hasText: /^Event NameEvent name is required\.$/ })
    .getByRole('textbox')
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^Event NameEvent name is required\.$/ })
    .getByRole('textbox')
    .fill('Rome Food & Wine Festival');
  await page
    .locator('textarea')
    .fill(
      'Indulge in authentic Italian cuisine, world-class wines, and cultural performances in the heart of Rome.'
    );

  await page
    .getByRole('dialog')
    .locator('form div')
    .filter({ hasText: 'CitySelect a CityNew' })
    .getByRole('combobox')
    .selectOption('7f15fafc-85f4-4ba5-822b-27b7ddce6c37');

  await page
    .getByRole('dialog')
    .locator('form div')
    .filter({ hasText: 'CountrySelect a CountryUnited' })
    .getByRole('combobox')
    .selectOption('dde33653-fc58-457c-9d33-a322a2a82835');
  await page
    .locator('div')
    .filter({ hasText: /^Image URL$/ })
    .getByRole('textbox')
    .click();
  await page.getByRole('button', { name: 'Save' }).click();
  await page.close();
});
