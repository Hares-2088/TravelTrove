import { Auth0Provider } from '@auth0/auth0-react';
import React from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import 'bootstrap/dist/css/bootstrap.min.css';
import { auth0Config } from './auth/auth0-config';
import { AppRoutes } from './shared/models/app.routes';
import './i18n';

const elfsightScript = document.createElement('script');
elfsightScript.src = 'https://static.elfsight.com/platform/platform.js';
elfsightScript.async = true;
document.body.appendChild(elfsightScript);

const onRedirectCallback = (): void => {
  window.location.replace(AppRoutes.Callback);
};

const root = createRoot(document.getElementById('root') as HTMLElement);

root.render(
  <React.StrictMode>
    <Auth0Provider
      domain={auth0Config.domain}
      clientId={auth0Config.clientId}
      authorizationParams={{
        redirect_uri: auth0Config.callback,
        audience: auth0Config.audience,
        scope: 'openid profile email',
      }}
      onRedirectCallback={onRedirectCallback}
      cacheLocation="localstorage"
    >
      <App />
    </Auth0Provider>
  </React.StrictMode>
);

reportWebVitals();
