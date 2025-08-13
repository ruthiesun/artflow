import {SecondaryButton} from "../ui/Button.tsx";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../.././AuthContext.tsx"

type Props = {
    className: string;
    content: ReactNode;
};

export function Background({className, content}: Props) {
    const {isAuthenticated, removeAuth} = useAuth();
    const nav = useNavigate();

    const handleAuthButton = (() => {
        if (isAuthenticated) {
            removeAuth();
        }
        nav("/login");
    });

    const authButtonText = isAuthenticated ? "Logout" : "Login";

    return (
        <div className={`min-h-screen flex-col bg-background`}>
            <div className="flex justify-end pt-2 bg-surface-100">
                <SecondaryButton type="button" text={`${authButtonText}`} onClick={handleAuthButton} />
            </div>
            <div className={`flex grow items-center justify-center ${className}`}>
                {content}
            </div>
        </div>

    )
}

export function BackgroundNoNav({className, content}: Props) {
    return (
        <div className={`min-h-screen bg-background flex items-center justify-center ${className}`}>
            {content}
        </div>
    )
}

export function BackgroundBorder({className, content}: Props) {
    return (
        <div className={`w-full rounded-2xl shadow-lg p-8 bg-background-50 ${className}`}>
            {content}
        </div>
    )
}


