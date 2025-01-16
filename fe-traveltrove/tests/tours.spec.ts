import { test, expect } from '@playwright/test';

test('get all tours', async ({ page }) => {
  await page.goto('http://localhost:3000/dashboard');

  await page.getByRole('tab', { name: 'Tours' }).click();
  await expect(page.getByLabel('Tours').locator('tbody')).toContainText(
    'Arabian Desert Safari'
  );
  await expect(page.getByLabel('Tours').locator('tbody')).toContainText(
    'Qatar Cultural Retreat'
  );
  await expect(page.getByLabel('Tours').locator('tbody')).toContainText(
    'Oman’s Hidden Gems'
  );
  await expect(page.getByLabel('Tours').locator('tbody')).toContainText(
    'Abu Dhabi Royal Experience'
  );
  await expect(page.getByLabel('Tours').locator('tbody')).toContainText(
    'Petra and Dead Sea Luxury Journey'
  );
  await expect(page.getByLabel('Tours').locator('tbody')).toContainText(
    'Riyadh and the Edge of the World'
  );
  await expect(page.getByLabel('Tours').locator('tbody')).toContainText(
    'Luxury Nile Cruise'
  );
  await expect(page.getByLabel('Tours').locator('tbody')).toContainText(
    'Kuwait City Highlights'
  );
  await expect(page.getByLabel('Tours').locator('tbody')).toContainText(
    'Moroccan Royal Adventure'
  );
  await expect(page.getByLabel('Tours').locator('tbody')).toContainText(
    'Splendors of Dubai'
  );
  await page.close();
});

test('get tour by tour id', async ({ page }) => {
  await page.goto('http://localhost:3000/dashboard');
  await expect(page.getByLabel('Tours').locator('tbody')).toContainText(
    'Arabian Desert Safari'
  );
  await page.getByRole('cell', { name: 'Arabian Desert Safari' }).click();
  await page.getByRole('button', { name: '← Back to List' }).click();
  await expect(page.getByLabel('Tours').locator('tbody')).toContainText(
    'Arabian Desert Safari'
  );

  await page.close();
});

test('add a tour', async ({ page }) => {
  await page.goto('http://localhost:3000/dashboard');
  await page.getByRole('tab', { name: 'Tours' }).click();
  await page.getByRole('button', { name: 'Create' }).click();
  await page.getByRole('dialog').locator('input[type="text"]').click();
  await page.getByRole('dialog').locator('input[type="text"]').fill('G');
  await page.locator('textarea').click();
  await page.locator('textarea').fill('D');
  await page.getByRole('button', { name: 'Save' }).click();

  await page.close();
});

test('edit a tour', async ({ page }) => {
  await page.goto('http://localhost:3000/dashboard');
  await page.getByRole('tab', { name: 'Tours' }).click();
  await page
    .getByRole('row', { name: 'Arabian Desert Safari Edit' })
    .getByRole('button')
    .first()
    .click();
  await page.getByRole('dialog').locator('input[type="text"]').click();
  await page
    .getByRole('dialog')
    .locator('input[type="text"]')
    .fill('Arabian Desert Safari ');
  await page.getByText('Embark on a luxurious desert').click();
  await page
    .getByText('Embark on a luxurious desert')
    .fill(
      'Embark on a luxurious desert adventure with private 4x4 rides, camel treks, and an evening of Tstargazing in Bedouin-style camps.'
    );
  await page.getByRole('button', { name: 'Save' }).click();
  await page
    .getByRole('row', { name: 'Petra and Dead Sea Luxury' })
    .getByRole('button')
    .first()
    .click();
  await page.getByRole('dialog').locator('input[type="text"]').click();
  await page
    .getByRole('dialog')
    .locator('input[type="text"]')
    .fill('Petra and Dead Sea Luxury Jourtney');
  await page.getByText('Uncover Jordan’s wonders with').click();
  await page
    .getByText('Uncover Jordan’s wonders with')
    .fill(
      'Uncover Jordan’s wonders with guided tours of Petra, the Dead Sea, and luxurious stays itn 5-star desert resorts.'
    );
  await page.getByRole('button', { name: 'Save' }).click();

  await page.close();
});

test('delete a tour', async ({ page }) => {
  await page.goto('http://localhost:3000/dashboard');
  await page.getByRole('tab', { name: 'Tours' }).click();
  await page
    .getByRole('row', { name: 'Arabian Desert Safari' })
    .getByRole('button')
    .nth(1)
    .click();
  await page.getByRole('button', { name: 'Confirm' }).click();
  await page.getByRole('button', { name: 'Create' }).click();
  await page.getByRole('dialog').locator('input[type="text"]').click();
  await page
    .getByRole('dialog')
    .locator('input[type="text"]')
    .fill('Arabian Desert Safari');
  await page.locator('textarea').click();
  await page
    .locator('textarea')
    .fill(
      'Embark on a luxurious desert adventure with private 4x4 rides, camel treks, and an evening of stargazing in Bedouin-style camps.'
    );
  await page.getByRole('button', { name: 'Save' }).click();

  await page.close();
});
