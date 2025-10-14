import { StrictMode } from 'react'
import './index.css'
import App from './App.tsx'
import { AuthProvider } from "./AuthContext.tsx"
import { createRoot } from "react-dom/client";


createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <AuthProvider>
      <App />
    </AuthProvider>
  </StrictMode>,
);
