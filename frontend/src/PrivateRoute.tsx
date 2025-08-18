import {useEffect, useState} from "react";
import { useNavigate, useParams } from "react-router-dom";
import {useAuth} from "./AuthContext.tsx"


export const PrivateRoute = ({ children }: { children: JSX.Element }) => {
    const { isAuthenticated, isLoading, getUsername } = useAuth();
    const {username} = useParams<{ username: string }>();
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate();

    useEffect(() => {
        if (!isLoading && !isAuthenticated) {
            nav("/login");
        }
    }, [isLoading, isAuthenticated]);

    useEffect(() => {
        if (!isLoading && username && username !== getUsername()) {
            setError("Other users' projects are view-only.");
            nav("/error");
        }
    }, [isLoading, username]);

    if (isLoading) return null;
    
    return children;
}


