import { createContext, useContext, useState, useEffect } from "react";
import { onAuthStateChanged, type User } from "firebase/auth";
import { auth } from "./api/auth/firebase";

type AuthContextType = {
  isAuthenticated: boolean;
  isLoading: boolean;
  getUsername: () => string | null;
  setIsLoading: (newIsLoading: boolean) => void;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (user) => {
      setCurrentUser(user);

      if (currentUser == null) {
        setIsAuthenticated(false);
      } else {
        setIsAuthenticated(true);
      }

      setIsLoading(false);
    });

    return unsubscribe;
  }, [auth, auth.currentUser]);

  const getUsername = () => {
    if (currentUser == null) {
      return null;
    }
    else {
      return currentUser.displayName;
    }
  }

  return (
    <AuthContext.Provider value={{ isAuthenticated, isLoading, getUsername, setIsLoading }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
};

