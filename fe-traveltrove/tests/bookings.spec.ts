import { test, expect } from '@playwright/test';


test("if booking is confirmed, the only next option should be refund", async ({ page }) => {
    await page.goto('http://localhost:3000/home');
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByLabel('Email address').fill('Admin@traveltrove.com');
    await page.getByLabel('Email address').press('Tab');

    await page.getByLabel('Password').fill('Admin@123');

    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('link', { name: 'Dashboard' }).click();
    await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
    await page.getByRole('button', { name: 'View Bookings' }).click();
    await expect(page.getByRole('cell', { name: 'Booking Confirmed' }).first()).toBeVisible();
    await page.getByRole('row', { name: 'Sophia Johnson 1600 Booking' }).getByRole('button').first().click();
    await expect(page.getByRole('combobox')).toHaveValue('BOOKING_CONFIRMED');
    await expect(page.getByRole('combobox')).toMatchAriaSnapshot(`
    - combobox:
      - option "Booking Confirmed" [disabled] [selected]
      - option "Refunded"
    `);
    await page.close();
});

test("if booking status is payment pending the next option should be payment attempt2. failed or confirmed ", async ({ page }) => {
    await page.goto('http://localhost:3000/home');
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByLabel('Email address').fill('Admin@traveltrove.com');
    await page.getByLabel('Email address').press('Tab');

    await page.getByLabel('Password').fill('Admin@123');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();


    await page.getByRole('link', { name: 'Dashboard' }).click();
    await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
    await page.getByRole('button', { name: 'View Bookings' }).click();

    await expect(page.getByRole('cell', { name: 'Payment Pending' })).toBeVisible();
    await page.getByRole('row', { name: 'Sophia Johnson 1600 Payment' }).getByRole('button').first().click();
    await expect(page.getByRole('combobox')).toMatchAriaSnapshot(`
      - combobox:
        - option "Payment Pending" [disabled] [selected]
        - option "Payment Pending"
        - option "Payment Attempt2 Pending"
        - option "Booking Failed"
        - option "Booking Confirmed"
      `);
    await page.close();
});

test("if booking status is refunded we shouldnt be able to change status anymore", async ({ page }) => {
    await page.goto('http://localhost:3000/home');
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByLabel('Email address').fill('Admin@traveltrove.com');
    await page.getByLabel('Email address').press('Tab');

    await page.getByLabel('Password').fill('Admin@123');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();


    await page.getByRole('link', { name: 'Dashboard' }).click();
    await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
    await page.getByRole('button', { name: 'View Bookings' }).click();


    await expect(page.getByRole('cell', { name: 'Booking Confirmed' }).nth(1)).toBeVisible();
    await page.getByRole('row', { name: 'James Williams 1600 Booking' }).getByRole('button').first().click();
    await page.getByRole('combobox').selectOption('REFUNDED');
    await page.getByRole('button', { name: 'Save' }).click();
    await expect(page.locator('tbody')).toMatchAriaSnapshot(`
      - cell "Show Travelers":
        - button "Show Travelers"
      `);
    await page.close();
});