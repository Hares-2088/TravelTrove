import { Auth0Provider } from "@auth0/auth0-react";
import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import App from "./App";
import reportWebVitals from "./reportWebVitals";
import "bootstrap/dist/css/bootstrap.min.css";
import { auth0Config } from "./auth/auth0-config";
import { AppRoutes } from "./shared/models/app.routes";
import "./i18n";
import { UserProvider } from "./context/UserContext";
import { ToastContainer } from 'react-toastify'; // Import ToastContainer

const elfsightScript = document.createElement("script");
elfsightScript.src = "https://static.elfsight.com/platform/platform.js";
elfsightScript.async = true;
document.body.appendChild(elfsightScript);

window.addEventListener('error', (e) => {
  if (e.message === 'ResizeObserver loop completed with undelivered notifications') {
    e.stopImmediatePropagation();
  }
}, true);

const onRedirectCallback = () => {
  window.location.replace(AppRoutes.Callback);
};

const root = ReactDOM.createRoot(
  document.getElementById("root") as HTMLElement
);

<link
  rel="stylesheet"
  href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css"
/>;

root.render(
  <React.StrictMode>
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
        <App />
        <ToastContainer /> {/* Add ToastContainer */}
      </UserProvider>
    </Auth0Provider>
  </React.StrictMode>
);

reportWebVitals();
