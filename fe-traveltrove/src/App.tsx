import './App.css';
import { RouterProvider } from 'react-router-dom';
import router from '../src/router';
import { Auth0Provider } from '@auth0/auth0-react';
import { UserProvider } from './context/UserContext';
import { auth0Config } from './auth/auth0-config';
import { AppRoutes } from './shared/models/app.routes';

const onRedirectCallback = () => {
  window.location.replace(AppRoutes.Callback);
};

function App(): JSX.Element {
  return (
    <Auth0Provider
      domain={auth0Config.domain} 
      clientId={auth0Config.clientId} 
      authorizationParams={{
        redirect_uri: auth0Config.callback,
        audience: auth0Config.audience, 
        scope: "openid profile email", 
      }}
      onRedirectCallback={onRedirectCallback}
      cacheLocation="localstorage"
    >
      <UserProvider>
        <RouterProvider router={router} />
      </UserProvider>
    </Auth0Provider>
  );
}

export default App;
