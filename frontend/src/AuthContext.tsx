// AuthContext.tsx
import { createContext, useContext, useState, useEffect } from "react";
import {jwtDecode} from "jwt-decode";

interface TokenPayload {
  exp: number;
  sub: string;
}

type AuthContextType = {
  isAuthenticated: boolean;
  isLoading: boolean;
  getUsername: () => string | null;
  setAuth: (token: string, username: string) => void;
  removeAuth: () => void;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    const token = localStorage.getItem("authToken");
    const username = localStorage.getItem("username");

    if (!token || !isTokenValid(token) || !username) {
      removeAuth();
    } else {
      setIsAuthenticated(true);
    }

    setIsLoading(false);
  }, []);

  const getUsername = () => {
    return localStorage.getItem("username");
  }

  const setAuth = (token: string, username: string) => {
    localStorage.setItem("authToken", token);
    localStorage.setItem("username", username);
    setIsAuthenticated(true);
  };

  const removeAuth = () => {
    localStorage.removeItem("authToken");
    localStorage.removeItem("username");
    setIsAuthenticated(false);
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, isLoading, getUsername, setAuth, removeAuth }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
};


export const isTokenValid = (token: string): boolean => {
  try {
    const decoded: TokenPayload = jwtDecode<TokenPayload>(token);
    return decoded.exp * 1000 > Date.now();
  } catch {
    return false;
  }
};

