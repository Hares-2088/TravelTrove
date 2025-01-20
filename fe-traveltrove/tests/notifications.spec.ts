import { test, expect } from '@playwright/test';

test('get all notifications', async ({ page }) => {
    await page.goto('http://localhost:3000/home');
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByLabel('Email address').click();
    await page.getByLabel('Email address').fill('admin@traveltrove.com');
    await page.getByLabel('Password').click();
    await page.getByLabel('Password').fill('Admin@123');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('link', { name: 'Dashboard' }).click();
    await page.getByRole('tab', { name: 'Notifications' }).click();
    await expect(page.getByLabel('Notifications').locator('tbody')).toContainText('Welcome to Travel Trove!');
    await expect(page.getByLabel('Notifications').locator('tbody')).toContainText('Booking Confirmation');
    await expect(page.getByLabel('Notifications').locator('tbody')).toContainText('Tour Update');
    await expect(page.getByLabel('Notifications').locator('tbody')).toContainText('Feedback Request');
    await expect(page.getByLabel('Notifications').locator('tbody')).toContainText('Spots Running Out!');
    await expect(page.getByLabel('Notifications').locator('tbody')).toContainText('Special Offer');
    await expect(page.getByLabel('Notifications').locator('tbody')).toContainText('Booking Reminder');
    await expect(page.getByLabel('Notifications').locator('tbody')).toContainText('Payment Confirmation');
    await expect(page.getByLabel('Notifications').locator('tbody')).toContainText('New Destinations Added');
    await expect(page.getByLabel('Notifications').locator('tbody')).toContainText('Welcome Back!');
    await page.close()
});

test('get notification by id', async ({ page }) => {
    await page.goto('http://localhost:3000/home');
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByLabel('Email address').click();
    await page.getByLabel('Email address').fill('admin@traveltrove.com');
    await page.getByLabel('Password').click();
    await page.getByLabel('Password').fill('Admin@123');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('link', { name: 'Dashboard' }).click();
    await page.getByRole('tab', { name: 'Notifications' }).click();
    await expect(page.getByLabel('Notifications').locator('tbody')).toContainText('Welcome to Travel Trove!');
    await page.getByRole('cell', { name: 'Welcome to Travel Trove!' }).click();
    await expect(page.getByLabel('Notifications').getByRole('heading')).toContainText('Welcome to Travel Trove!');
    await expect(page.getByLabel('Notifications')).toContainText('To: amelia.clark@traveltrove.com');
    await expect(page.getByLabel('Notifications')).toContainText('Message: Thank you for joining Travel Trove!');
    await expect(page.getByLabel('Notifications')).toContainText('Sent At: 2025-01-01T09:00:00');
    await page.getByRole('button', { name: '← Back to Notifications List' }).click();
    await expect(page.getByLabel('Notifications').locator('tbody')).toContainText('Welcome to Travel Trove!');
    await page.close()
});

test('create and delete notification', async ({ page }) => {
    await page.goto('http://localhost:3000/home');
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByLabel('Email address').fill('admin@traveltrove.com');
    await page.getByLabel('Password').click();
    await page.getByLabel('Password').fill('Admin@123');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('link', { name: 'Dashboard' }).click();
    await page.getByRole('tab', { name: 'Notifications' }).click();
    await page.getByRole('button', { name: 'Create' }).click();
    await page.locator('input[type="email"]').click();
    await page.locator('input[type="email"]').fill('test@example.com');
    await page.locator('input[type="text"]').click();
    await page.locator('input[type="text"]').fill('Test');
    await page.locator('textarea').click();
    await page.locator('textarea').fill('Test');
    await page.getByRole('button', { name: 'Save' }).click();
    await expect(page.getByLabel('Notifications').locator('tbody')).toContainText('Test');
    await page.getByRole('row', { name: 'Test Delete' }).getByRole('button').click();
    await page.getByRole('button', { name: 'Confirm' }).click();
    await page.close()
});