import { test, expect } from "@playwright/test";


test('get all airports', async ({page}) => {
await page.goto('http://localhost:3000/dashboard');
await expect(page.locator('[id="react-aria863872929-\\:r1\\:-tab-airports"]')).toContainText('Airports');
await page.getByRole('tab', { name: 'Airports' }).click();
await expect(page.getByLabel('Airports').locator('tbody')).toContainText('John F. Kennedy International Airport');
await expect(page.getByLabel('Airports').locator('tbody')).toContainText('Berlin Brandenburg Airport');
await expect(page.getByLabel('Airports').locator('tbody')).toContainText('Leonardo da Vinci–Fiumicino Airport');
await expect(page.getByLabel('Airports').locator('tbody')).toContainText('Narita International Airport');
await expect(page.getByLabel('Airports').locator('tbody')).toContainText('Toronto Pearson International Airport');
await expect(page.getByLabel('Airports').locator('tbody')).toContainText('Chhatrapati Shivaji Maharaj International Airport');
await expect(page.getByLabel('Airports').locator('tbody')).toContainText('Sydney Kingsford Smith Airport');
await expect(page.getByLabel('Airports').locator('tbody')).toContainText('Beijing Capital International Airport');
await expect(page.getByLabel('Airports').locator('tbody')).toContainText('Rio de Janeiro–Galeão International Airport');
await expect(page.getByLabel('Airports').locator('tbody')).toContainText('Charles de Gaulle Airport');
await page.close();
});