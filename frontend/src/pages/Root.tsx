import {useAuth} from "../AuthContext.tsx"
import {Navigate} from "react-router-dom";

export const Root = () => {
    const { isAuthenticated } = useAuth();

    if (isAuthenticated) {
        return <Navigate to="/projects" replace />;
    }
    else {
        return <Navigate to="/login" replace />;
    }
};