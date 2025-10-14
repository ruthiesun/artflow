import { Navigate } from "react-router-dom";
import { useAuth } from "./AuthContext.tsx"

// can only access when logged out
export const PublicRoute = ({ children }: { children: JSX.Element }) => {
    const { isAuthenticated, isLoading } = useAuth();
    if (isLoading) return null;

    if (isAuthenticated) {
        return <Navigate to="/projects" replace />;
    }
    return children;
};
