import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import HttpApi from 'i18next-http-backend';
import LanguageDetector from 'i18next-browser-languagedetector';

// Initialize i18n
i18n
  .use(HttpApi) // Loads translations via HTTP
  .use(LanguageDetector) // Detects user language
  .use(initReactI18next) // React integration
  .init({
    fallbackLng: 'en', // Default language if detection fails
    lng: "en",
    debug: true, // Enable debug mode for development
    backend: {
      loadPath: '/locales/{{lng}}/translation.json', // Path to translation files
    },
    detection: {
      order: ['querystring', 'localStorage', 'navigator', 'htmlTag', 'cookie'],
      caches: ['localStorage'], // Cache the selected language
    },
    interpolation: {
      escapeValue: false, // React already escapes values
    },
  });

export default i18n;

