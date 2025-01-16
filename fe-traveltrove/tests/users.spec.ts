import { test, expect } from '@playwright/test';

test('login as user', async ({ page }) => {
  await page.goto('http://localhost:3000/home');
  await page.getByRole('button', { name: 'Sign in' }).click();
  await page.getByLabel('Email address').fill('admin@traveltrove.com');
  await page.getByLabel('Password').click();
  await page.getByLabel('Password').fill('Admin@123');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await expect(page.locator('#user-dropdown')).toContainText(
    'admin@traveltrove.com'
  );
  await page.close();
});

test('sign up as user', async ({ page }) => {
  await page.goto('http://localhost:3000/home');
  await page.getByRole('button', { name: 'Sign up' }).click();
  await page.getByLabel('Email address').click();
  await page.getByLabel('Email address').fill('test@signup.com');
  await page.getByLabel('Password').click();
  await page.getByLabel('Password').fill('Test@123');
  await expect(page.locator('section')).toContainText(
    'Your password must contain: At least 8 characters At least 3 of the following: Lower case letters (a-z) Upper case letters (A-Z) Numbers (0-9) Special characters (e.g. !@#$%^&*)'
  );
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await expect(page.getByRole('main')).toContainText(
    'Authorize App Hi test@signup.com, TravelTrove Web Application is requesting access to your dev-traveltrove account. profile: access to your profile and email Accept Decline'
  );
  await page.getByRole('button', { name: 'Accept' }).click();
  await expect(page.locator('#user-dropdown')).toContainText('test@signup.com');
  await page.close();
});
