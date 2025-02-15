import { test, expect } from "@playwright/test";

test("pay for package success", async ({ page }) => {
  await page.goto('http://localhost:3000/home');
  // Login
  await page.getByRole('button', { name: 'Sign in' }).click();
  await page.getByLabel('Email address').fill('Admin@traveltrove.com');
  await page.getByLabel('Password').fill('Admin@123');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();

  // Navigate to package
  await page.getByRole('link', { name: 'Trips' }).click();
  await page.getByRole('link', { name: 'Grand American Adventure' }).click();
  await page.getByRole('link', { name: 'New York Adventure Package' }).click();
  await page.getByRole('button', { name: 'Book Now' }).click();

  // Add traveler details
  await page.getByRole('button', { name: 'Add Traveler' }).click();
  await page.getByPlaceholder('First Name').fill('testte');
  await page.getByPlaceholder('Last Name').fill('stte');
  await page.getByPlaceholder('Email').fill('st@email.com');
  await page.getByPlaceholder('Address Line 1').fill('123');
  await page.getByRole('button', { name: 'Confirm Booking' }).click();

  // Enter payment details
  await page.getByLabel('Email').fill('test@email.com');

  // Fill card information (inside iframe if applicable)
  await page.locator('input[autocomplete="cc-number"]').fill('4242 4242 4242 4242');
  await page.locator('input[autocomplete="cc-exp"]').fill('04 / 44');
  await page.locator('input[autocomplete="cc-csc"]').fill('123');

  // Fill cardholder name
  await page.locator('input[autocomplete="cc-name"]').fill('John Doe');

  // Fill postal code
  await page.locator('input[autocomplete="billing postal-code"]').fill('123123');

  // Submit payment
  await page.getByTestId('hosted-payment-submit-button').click();

  await page.waitForTimeout(10000);

// Wait for the success message
await expect(page.getByRole('heading')).toContainText('✅ Payment Successful!');
  await page.close();
});


test("pay for package cancelled", async ({ page }) => {
  await page.goto('http://localhost:3000/home');
  await page.getByRole('button', { name: 'Sign in' }).click();    
  await page.getByLabel('Email address').click();
  await page.getByLabel('Email address').fill('admin@traveltrove.com');
  await page.getByLabel('Password').click();
  await page.getByLabel('Password').fill('Admin@123');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();

  // Navigate to package
  await page.getByRole('link', { name: 'Trips' }).click();
  await page.getByRole('link', { name: 'Grand American Adventure' }).click();
  await page.getByRole('link', { name: 'New York Adventure Package' }).click();
  
  await page.getByRole('button', { name: 'Book Now' }).click();
  await page.getByRole('button', { name: 'Add Traveler' }).click();
  await page.getByPlaceholder('First Name').click();
  await page.getByPlaceholder('First Name').fill('11');
  await page.getByPlaceholder('Last Name').click();
  await page.getByPlaceholder('Last Name').fill('1');
  await page.getByPlaceholder('Address Line 1').click();
  await page.getByPlaceholder('Email').click();
  await page.getByPlaceholder('Email').fill('1');
  await page.getByPlaceholder('Address Line 1').click();
  await page.getByPlaceholder('Address Line 1').fill('1');
  await page.getByRole('button', { name: 'Confirm Booking' }).click();
  await page.getByLabel('Back to github.com').click();
  await expect(page.getByRole('heading')).toContainText('❌ Payment Canceled');
  await page.close();
});

test("verify availability of payments of a tour and booking", async ({ page }) => {
  await page.goto('http://localhost:3000/home');
  await page.getByRole('button', { name: 'Sign in' }).click();    
  await page.getByLabel('Email address').click();
  await page.getByLabel('Email address').fill('admin@traveltrove.com');
  await page.getByLabel('Password').click();
  await page.getByLabel('Password').fill('Admin@123');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();

  await page.getByRole('link', { name: 'Dashboard' }).click();
  await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
  await expect(page.getByLabel('Tours')).toContainText('Revenue: $9050.00');
  await page.getByRole('button', { name: 'View Bookings' }).click();
  await expect(page.locator('tbody')).toContainText('$1900.00 (USD)');
  await page.getByRole('button', { name: 'admin@traveltrove.com admin@' }).click();
  await page.getByRole('button', { name: 'Log out' }).click();
  
  await page.close();
});