import React from 'react';
import i18next from 'i18next';

const LanguageSwitcher = () => {
  const handleLanguageChange = (lang) => {
    i18next.changeLanguage(lang);
  };

  return (
    <div>
      <button onClick={() => handleLanguageChange('en')}>English</button>
      <button onClick={() => handleLanguageChange('fr')}>Français</button>
    </div>
  );
};

export default LanguageSwitcher;
