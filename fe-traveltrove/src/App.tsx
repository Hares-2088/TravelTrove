import "./App.css";
import { RouterProvider } from "react-router-dom";
import router from "../src/router";

function App(): JSX.Element {
  return <RouterProvider router={router} />;
}

export default App;
