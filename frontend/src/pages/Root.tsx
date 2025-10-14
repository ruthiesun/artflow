import { Navigate } from "react-router-dom";
import { useAuth } from "../AuthContext.tsx"

export const Root = () => {
    const { isAuthenticated, getUsername } = useAuth();

    if (isAuthenticated) {
        const username = getUsername();
        const uri = "/" + username + "/projects";
        return <Navigate to={uri} replace />;
    }
    else {
        return <Navigate to="/login" replace />;
    }
};