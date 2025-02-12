import { test, expect } from "@playwright/test";


// test("get the packages for a tour", async ({ page }) => {
//     await page.goto('http://localhost:3000/dashboard');

//     await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
//     await expect(page.getByRole('cell', { name: 'New York Adventure Package' })).toBeVisible();
//     await page.close();
// });

// test("add a package to a tour and then delete it", async ({ page }) => {
//     await page.goto('http://localhost:3000/dashboard');
//     await page.getByRole('button', { name: 'Sign in' }).click();
//     await page.getByLabel('Email address').fill('admin@traveltrove.com');
//     await page.getByLabel('Password').click();
//     await page.getByLabel('Password').fill('Admin@123');
//     await page.getByRole('button', { name: 'Continue', exact: true }).click();
//     await page.getByRole('link', { name: 'Dashboard' }).click();
//     await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
//     await page.getByRole('button', { name: 'Create Package' }).click();
//     await page.locator('input[type="text"]').click();
//     await page.locator('input[type="text"]').fill('Adem is having fun');
//     await page.locator('textarea').click();
//     await page.locator('textarea').fill('yes i really do');
//     await page.locator('div').filter({ hasText: /^startDatestartDateBeforeEndDate$/ }).getByRole('textbox').fill('2024-12-18');
//     await page.locator('div').filter({ hasText: /^endDatestartDateBeforeEndDate$/ }).getByRole('textbox').fill('2025-01-02');
//     await page.locator('div').filter({ hasText: /^priceSinglepriceSingleRequired$/ }).getByRole('spinbutton').click();
//     await page.locator('div').filter({ hasText: /^priceDouble$/ }).getByRole('spinbutton').click();
//     await page.locator('div').filter({ hasText: /^priceTriple$/ }).getByRole('spinbutton').click();
//     await page.getByRole('combobox').selectOption('9273ecac-b84d-41e9-9d5f-28f0bd1e467b');
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').click();
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').press('ArrowLeft');
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').fill('200');

//     await page.getByRole('button', { name: 'Save' }).click();
//     await expect(page.getByRole('cell', { name: 'Adem is having fun' })).toBeVisible();
//     await page.getByRole('cell', { name: 'Adem is having fun' }).click();
//     await expect(page.getByText('packageName: Adem is having funpackageDescription: yes i really dostartDate:')).toBeVisible();
//     await page.getByLabel('Close').click();
//     await page.getByRole('button', { name: 'Delete Package' }).nth(1).click();
//     await page.getByRole('button', { name: 'Confirm' }).click();

//     await page.close();
// });

// test("edit a package for a tour", async ({ page }) => {
//     await page.goto('http://localhost:3000/dashboard');

//     await page.getByRole('button', { name: 'Sign in' }).click();

//     await page.getByLabel('Email address').click();
//     await page.getByLabel('Email address').fill('admin@traveltrove.com');

//     await page.getByLabel('Password').click();
//     await page.getByLabel('Password').fill('Admin@123');

//     await page.getByLabel('Password').click();
//     await page.getByRole('button', { name: 'Continue', exact: true }).click();
//     await page.getByRole('link', { name: 'Dashboard' }).click();
//     await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
//     await page.getByRole('button', { name: 'Edit Package' }).click();
//     await page.locator('input[type="text"]').click();
//     await page.locator('input[type="text"]').fill('New York Adventure Package2');
//     await page.getByRole('button', { name: 'Save' }).click();
//     await expect(page.getByRole('cell', { name: 'New York Adventure Package2' })).toBeVisible();
//     await page.getByRole('button', { name: 'Edit Package' }).click();
//     await page.locator('input[type="text"]').click();
//     await page.locator('input[type="text"]').fill('New York Adventure Package');
//     await page.getByRole('button', { name: 'Save' }).click();

//     await expect(page.getByRole('cell', { name: 'New York Adventure Package' })).toBeVisible();
//     await page.close();
// });

// test("delete a package for a tour", async ({ page }) => {
//     await page.goto('http://localhost:3000/dashboard');
//     await page.getByRole('button', { name: 'Sign in' }).click();
//     await page.getByLabel('Email address').click();
//     await page.getByLabel('Email address').fill('admin@traveltrove.com');
//     await page.getByLabel('Password').click();
//     await page.getByLabel('Password').fill('Admin@123');
//     await page.getByLabel('Password').click();
//     await page.getByRole('button', { name: 'Continue', exact: true }).click();
//     await page.getByRole('link', { name: 'Dashboard' }).click();
//     await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
//     await page.getByRole('button', { name: 'Create Package' }).click();
//     await page.locator('input[type="text"]').click();
//     await page.locator('input[type="text"]').fill('tempPackage');
//     await page.locator('textarea').click();
//     await page.locator('textarea').fill('tempDescription');
//     await page.locator('div').filter({ hasText: /^startDatestartDateBeforeEndDate$/ }).getByRole('textbox').fill('2025-01-09');
//     await page.locator('div').filter({ hasText: /^endDatestartDateBeforeEndDate$/ }).getByRole('textbox').fill('2025-01-10');
//     await page.locator('div').filter({ hasText: /^priceSinglepriceSingleRequired$/ }).getByRole('spinbutton').click();
//     await page.locator('div').filter({ hasText: /^priceSinglepriceSingleRequired$/ }).getByRole('spinbutton').press('ArrowLeft');
//     await page.locator('div').filter({ hasText: /^priceSinglepriceSingleRequired$/ }).getByRole('spinbutton').fill('100');
//     await page.locator('div').filter({ hasText: /^priceDouble$/ }).getByRole('spinbutton').click();
//     await page.locator('div').filter({ hasText: /^priceDouble$/ }).getByRole('spinbutton').press('ArrowLeft');
//     await page.locator('div').filter({ hasText: /^priceDouble$/ }).getByRole('spinbutton').fill('90');
//     await page.locator('div').filter({ hasText: /^priceTriple$/ }).getByRole('spinbutton').click();
//     await page.locator('div').filter({ hasText: /^priceTriple$/ }).getByRole('spinbutton').press('ArrowLeft');
//     await page.locator('div').filter({ hasText: /^priceTriple$/ }).getByRole('spinbutton').fill('80');
//     await page.getByRole('combobox').selectOption('e8f314c7-716b-4f19-a1d6-fc376b8c81ad');
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').click();
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').press('ArrowLeft');
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').fill('200');
//     await page.getByRole('button', { name: 'Save' }).click();
//     await expect(page.getByRole('cell', { name: 'tempPackage' })).toBeVisible();
//     await page.getByRole('button', { name: 'Delete Package' }).nth(1).click();
//     await page.getByRole('button', { name: 'Confirm' }).click();
//     await page.close();
// });

// test("package status change testing", async ({ page }) => {
//     await page.goto('http://localhost:3000/dashboard');
//     await page.getByRole('button', { name: 'Sign in' }).click();
//     await page.getByLabel('Email address').click();
//     await page.getByLabel('Email address').fill('admin@traveltrove.com');
//     await page.getByLabel('Password').click();
//     await page.getByLabel('Password').fill('Admin@123');
//     await page.getByLabel('Password').click();
//     await page.getByRole('button', { name: 'Continue', exact: true }).click();
//     await page.getByRole('link', { name: 'Dashboard' }).click();
//     await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
//     await expect(page.getByRole('cell', { name: 'New York Adventure Package' })).toBeVisible();
//     await page.getByRole('button', { name: 'Edit Package' }).first().click();
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').click();
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').fill('');
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').press('ArrowLeft');
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').fill('10');
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').press('ArrowRight');
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').fill('100');
//     await page.getByRole('button', { name: 'Save' }).click();
//     await expect(page.getByRole('cell', { name: 'AVAILABLE' }).first()).toBeVisible();
//     await page.getByRole('button', { name: 'Edit Package' }).first().click();
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').click();
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').press('ArrowLeft');
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').press('ArrowLeft');
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').fill('500');
//     await page.getByRole('button', { name: 'Save' }).click();
//     await expect(page.getByRole('cell', { name: 'NEAR_CAPACITY' })).toBeVisible();
//     await page.getByRole('button', { name: 'Edit Package' }).first().click();
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').click();
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').fill('');
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').press('ArrowLeft');
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').fill('10');
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').press('ArrowRight');
//     await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').fill('100');
//     await page.getByRole('button', { name: 'Save' }).click();
//     await page.close();
// });

//------------------------------------------------------------------------------------------------------------------------------------

test("package status when adding a new package is upcoming and i can't book the package", async ({ page }) => {
    await page.goto('http://localhost:3000/home');
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByLabel('Email address').fill('Admin@traveltrove.com');
    await page.getByLabel('Password').click();
    await page.getByLabel('Password').fill('Admin@123');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('link', { name: 'Dashboard' }).click();
    await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
    await expect(page.getByRole('cell', { name: 'UPCOMING' }).first()).toBeVisible();
    await page.getByRole('link', { name: 'Trips' }).click();
    await expect(page.getByRole('link', { name: 'Grand American Adventure' })).toBeVisible();
    await page.getByRole('link', { name: 'Grand American Adventure' }).click();
    await page.getByRole('link', { name: 'New York Adventure Package' }).click();
    await expect(page.getByText('This package is upcoming.')).toBeVisible();
    await page.close();
});

test("create new package and change it to completed and make sure it is irriversible", async ({ page }) => {
    await page.goto('http://localhost:3000/home');
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByLabel('Email address').fill('Admin@traveltrove.com');
    await page.getByLabel('Email address').press('Tab');
    await page.goto('http://localhost:3000/home');
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.goto('chrome-error://chromewebdata/');('Tab');
    await page.getByLabel('Password').fill('Admin@123');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('link', { name: 'Dashboard' }).click();
    await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
    await page.getByRole('button', { name: 'Create Package' }).click();
    await page.locator('input[type="text"]').click();
    await page.locator('input[type="text"]').fill('Completed test');
    await page.locator('textarea').click();
    await page.locator('textarea').fill('test desc');
    await page.locator('div').filter({ hasText: /^startDatestartDateBeforeEndDate$/ }).getByRole('textbox').fill('2033-03-03');
    await page.locator('div').filter({ hasText: /^endDatestartDateBeforeEndDate$/ }).getByRole('textbox').fill('2222-02-02');
    await page.locator('div').filter({ hasText: /^priceSinglepriceSingleRequired$/ }).getByRole('spinbutton').click();
    await page.locator('div').filter({ hasText: /^priceSinglepriceSingleRequired$/ }).getByRole('spinbutton').fill('033');
    await page.locator('div').filter({ hasText: /^priceDouble$/ }).getByRole('spinbutton').click();
    await page.locator('div').filter({ hasText: /^priceDouble$/ }).getByRole('spinbutton').fill('022');
    await page.locator('div').filter({ hasText: /^priceTriple$/ }).getByRole('spinbutton').click();
    await page.locator('div').filter({ hasText: /^priceTriple$/ }).getByRole('spinbutton').fill('011');
    await page.getByRole('combobox').selectOption('aa12fc4b-619e-4d8e-8563-5a09bc6f1ae1');
    await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').click();
    await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').fill('022');
    await page.getByRole('button', { name: 'Save' }).click();
    await page.getByRole('button', { name: 'Change Status' }).click();
    await page.getByRole('combobox').selectOption('COMPLETED');
    await page.getByRole('dialog').getByRole('button', { name: 'Change Status' }).click();
    await expect(page.getByLabel('Tours')).toContainText('Edit PackageView BookingsView All Reviews');
    await page.close();
});


test("package status when a package is booking open i can book the package", async ({ page }) => {
    await page.goto('http://localhost:3000/home');
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByLabel('Email address').fill('Admin@traveltrove.com');
    await page.getByLabel('Email address').press('Tab');
    await page.getByLabel('Password').fill('Admin@123');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.getByRole('link', { name: 'Dashboard' }).click();


    await page.getByRole('cell', { name: 'Italian Culinary Retreat' }).click();
    await page.getByRole('button', { name: 'Create Package' }).click();
    await page.locator('input[type="text"]').click();
    await page.locator('input[type="text"]').fill('test');
    await page.locator('textarea').click();
    await page.locator('textarea').fill('new');
    await page.locator('div').filter({ hasText: /^endDatestartDateBeforeEndDate$/ }).getByRole('textbox').fill('2222-03-03');
    await page.locator('div').filter({ hasText: /^priceSinglepriceSingleRequired$/ }).getByRole('spinbutton').click();
    await page.locator('div').filter({ hasText: /^priceSinglepriceSingleRequired$/ }).getByRole('spinbutton').fill('03');
    await page.locator('div').filter({ hasText: /^priceDouble$/ }).getByRole('spinbutton').click();
    await page.locator('div').filter({ hasText: /^priceDouble$/ }).getByRole('spinbutton').fill('02');
    await page.locator('div').filter({ hasText: /^priceTriple$/ }).getByRole('spinbutton').click();
    await page.locator('div').filter({ hasText: /^priceTriple$/ }).getByRole('spinbutton').fill('01');
    await page.getByRole('combobox').selectOption('b2c4f9ac-0fd6-4327-8e3a-f3c68bd3d62a');
    await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').click();
    await page.locator('div').filter({ hasText: /^totalSeatstotalSeatsRequired$/ }).getByRole('spinbutton').fill('0100');
    await page.getByRole('button', { name: 'Save' }).click();
    await page.locator('div').filter({ hasText: /^startDatestartDateBeforeEndDate$/ }).getByRole('textbox').fill('2030-02-02');
    await page.getByRole('button', { name: 'Save' }).click();
    await page.getByRole('button', { name: 'Change Status' }).click();
    await page.getByRole('combobox').selectOption('BOOKING_CLOSED');
    await page.getByRole('combobox').selectOption('BOOKING_OPEN');
    await page.getByRole('dialog').getByRole('button', { name: 'Change Status' }).click();
    await page.getByRole('link', { name: 'Trips' }).click();
    await page.getByRole('link', { name: 'Italian Culinary Retreat' }).click();
    await page.getByRole('link', { name: 'test new' }).click();
    await expect(page.getByRole('button', { name: 'Book Now' })).toBeVisible();
    await page.close();
});

test("edit a package for a tour", async ({ page }) => {
    await page.goto('http://localhost:3000/home');
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.getByLabel('Email address').fill('Admin@traveltrove.com');
    await page.getByLabel('Password').click();
    await page.getByLabel('Password').fill('Admin@123');
    await page.getByRole('button', { name: 'Continue', exact: true }).click();
    await page.waitForTimeout(10000);
    await expect(page.getByRole('button', { name: 'admin@traveltrove.com admin@' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Dashboard' })).toBeVisible();
    await page.getByRole('link', { name: 'Dashboard' }).click();
    await page.getByRole('cell', { name: 'Grand American Adventure' }).click();
    await page.getByRole('button', { name: 'Edit Package' }).click();
    await page.locator('input[type="text"]').click();
    await page.locator('input[type="text"]').fill('New York Adventure Package.');
    await page.locator('div').filter({ hasText: /^notificationMessage$/ }).getByRole('textbox').click();
    await page.locator('div').filter({ hasText: /^notificationMessage$/ }).getByRole('textbox').fill('Name Change');
    await page.getByRole('button', { name: 'Save' }).click();
    await expect(page.getByText('Package changes recorded')).toBeVisible();
    await expect(page.getByRole('cell', { name: 'New York Adventure Package.' })).toBeVisible();
    await page.getByRole('button', { name: 'Edit Package' }).click();
    await page.locator('input[type="text"]').click();
    await page.locator('input[type="text"]').fill('New York Adventure Package');
    await page.getByRole('button', { name: 'Save' }).click();
    await page.close();
});