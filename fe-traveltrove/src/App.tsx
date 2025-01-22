import "./App.css";
import { RouterProvider } from "react-router-dom";
import router from "../src/router";
import "./App.css";
import { Auth0Provider } from "@auth0/auth0-react";
import { UserProvider } from "./context/UserContext";
import { auth0Config } from "./auth/auth0-config";
import { AppRoutes } from "./shared/models/app.routes";

const onRedirectCallback = () => {
  window.location.replace(AppRoutes.Callback);
};

function App(): JSX.Element {
  return (
    <UserProvider>
      <RouterProvider router={router} />
    </UserProvider>
  );
}

export default App;
