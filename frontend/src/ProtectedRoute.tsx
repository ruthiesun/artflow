import { Navigate } from "react-router-dom";
import {useAuth} from "./AuthContext.tsx"

// can access while logged in or out
export const ProtectedRoute = ({ children }: { children: JSX.Element }) => {
    const { isAuthenticated, isLoading } = useAuth();
    if (isLoading) return null;

    // if (!isAuthenticated) {
    //     return <Navigate to="/login" replace />;
    // }
    return children;
}
