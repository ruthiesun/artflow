// AuthContext.tsx
import { createContext, useContext, useState, useEffect } from "react";
import {jwtDecode} from "jwt-decode";

interface TokenPayload {
  exp: number;
  sub: string;
}

type AuthContextType = {
  isAuthenticated: boolean;
  setAuth: (token: string) => void;
  removeAuth: () => void;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);

  useEffect(() => {
    const token = localStorage.getItem("authToken");

    if (!token || !isTokenValid(token)) {
      removeAuth();
    } else {
      setIsAuthenticated(true);
    }
  }, []);


  const setAuth = (token: string) => {
    localStorage.setItem("authToken", token);
    setIsAuthenticated(true);
  };

  const removeAuth = () => {
    localStorage.removeItem("authToken");
    setIsAuthenticated(false);
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, setAuth, removeAuth }}>
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

