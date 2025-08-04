import {Navigate} from "react-router-dom";
import {useAuth} from "../AuthContext.tsx"

export const Root = () => {
    const { isAuthenticated } = useAuth();

    if (isAuthenticated) {
        return <Navigate to="/projects" replace />;
    }
    else {
        return <Navigate to="/login" replace />;
    }
};