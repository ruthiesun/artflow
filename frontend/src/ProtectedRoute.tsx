import { useAuth } from "./AuthContext.tsx"

// can access while logged in or out
export const ProtectedRoute = ({ children }: { children: JSX.Element }) => {
    const { isLoading } = useAuth();
    if (isLoading) return null;

    return children;
}
