import { test, expect } from '@playwright/test';

test('get all travelers', async ({ page }) => {
  await page.goto('http://localhost:3000/dashboard');

  await page.getByRole('tab', { name: 'Travelers' }).click();
  await expect(page.getByRole('cell', { name: 'John Doe' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Maria Rossi' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Yuki Tanaka' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Carlos Silva' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Liam Brown' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Wei Li' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Amit Sharma' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Hans Müller' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Jane Smith' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Adem Bessam' })).toBeVisible();

  await page.close();
});

test('get traveler by id', async ({ page }) => {
  await page.goto('http://localhost:3000/dashboard');

  await page.getByRole('tab', { name: 'Travelers' }).click();
  await page.getByRole('cell', { name: 'John Doe' }).click();
  await expect(page.getByRole('heading', { name: 'John Doe' })).toBeVisible();
  await expect(page.getByText('address: 456 Elm St, 654 Elm')).toBeVisible();
  await expect(page.getByText('email: johndoe@gmail.com')).toBeVisible();
  await expect(page.getByLabel('Travelers')).toBeVisible();

  await page.close();
});

test('add a new traveler and delete it', async ({ page }) => {
  await page.goto('http://localhost:3000/dashboard');
  await page.getByRole('button', { name: 'Sign in' }).click();

  await page.getByLabel('Email address').click();
  await page.getByLabel('Email address').fill('admin@traveltrove.com');

  await page.getByLabel('Email address').click();
  await page.getByLabel('Password').click();
  await page.getByLabel('Password').fill('Admin@123');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await page.getByRole('link', { name: 'Dashboard' }).click();
  await page.getByRole('tab', { name: 'Travelers' }).click();
  await page.getByRole('button', { name: 'Create' }).click();
  await page
    .locator('div')
    .filter({ hasText: /^firstNamefirstNameRequired$/ })
    .getByRole('textbox')
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^firstNamefirstNameRequired$/ })
    .getByRole('textbox')
    .fill('Adem');
  await page
    .locator('div')
    .filter({ hasText: /^lastNamelastNameRequired$/ })
    .getByRole('textbox')
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^lastNamelastNameRequired$/ })
    .getByRole('textbox')
    .fill('Bessam');
  await page
    .locator('div')
    .filter({ hasText: /^addressLine1addressLine1Required$/ })
    .getByRole('textbox')
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^addressLine1addressLine1Required$/ })
    .getByRole('textbox')
    .fill('there');
  await page
    .locator('div')
    .filter({ hasText: /^addressLine2$/ })
    .getByRole('textbox')
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^addressLine2$/ })
    .getByRole('textbox')
    .fill('here');
  await page
    .locator('div')
    .filter({ hasText: /^CitycityRequired$/ })
    .getByRole('textbox')
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^CitycityRequired$/ })
    .getByRole('textbox')
    .fill('Here and there');
  await page
    .locator('div')
    .filter({ hasText: /^statestateRequired$/ })
    .getByRole('textbox')
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^statestateRequired$/ })
    .getByRole('textbox')
    .fill('Qc');
  await page.locator('input[type="email"]').click();
  await page.locator('input[type="email"]').fill('adembessam@gmail.com');
  await page.locator('input[type="email"]').press('Tab');
  await page
    .getByRole('combobox')
    .selectOption('3cd2ad86-26cc-42ad-8b20-8b0b6e6d2a2e');

  await page.getByRole('button', { name: 'Save' }).click();
  await expect(
    page.getByRole('cell', { name: 'Adem Bessam' }).nth(1)
  ).toBeVisible();
  await page.locator('tr:nth-child(11) > td:nth-child(2) > .ms-2').click();
  await page
    .getByRole('dialog')
    .getByRole('button', { name: 'Delete' })
    .click();
  await page
    .getByRole('button', { name: 'admin@traveltrove.com admin@' })
    .click();
  await page.getByRole('button', { name: 'Log out' }).click();
  await page.close();
});

test('edit', async ({ page }) => {
  await page.goto('http://localhost:3000/dashboard');
  await page.getByRole('button', { name: 'Sign in' }).click();

  await page.getByLabel('Email address').click();
  await page.getByLabel('Email address').fill('admin@traveltrove.com');
  await page.getByLabel('Password').click();
  await page.getByLabel('Password').fill('Admin@123');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await page.getByRole('link', { name: 'Dashboard' }).click();
  await page.getByRole('tab', { name: 'Travelers' }).click();
  await expect(page.getByRole('cell', { name: 'Amit Sharma' })).toBeVisible();
  await page
    .getByRole('row', { name: 'Amit Sharma Edit Delete' })
    .getByRole('button')
    .first()
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^lastNamelastNameRequired$/ })
    .getByRole('textbox')
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^lastNamelastNameRequired$/ })
    .getByRole('textbox')
    .fill('Sharma2');
  await page.getByRole('button', { name: 'Save' }).click();
  await expect(page.getByRole('cell', { name: 'Amit Sharma2' })).toBeVisible();
  await page
    .getByRole('row', { name: 'Amit Sharma2 Edit Delete' })
    .getByRole('button')
    .first()
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^lastNamelastNameRequired$/ })
    .getByRole('textbox')
    .click();
  await page
    .locator('div')
    .filter({ hasText: /^lastNamelastNameRequired$/ })
    .getByRole('textbox')
    .fill('Sharma');
  await page.getByRole('button', { name: 'Save' }).click();
  await expect(page.getByRole('cell', { name: 'Amit Sharma' })).toBeVisible();
  await page
    .getByRole('button', { name: 'admin@traveltrove.com admin@' })
    .click();
  await page.getByRole('button', { name: 'Log out' }).click();
  await page.close();
});
