import {useState} from "react";
import {SecondaryButton} from "../ui/Button.tsx";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../.././AuthContext.tsx";
import {H1, Text} from "./Text.tsx";

type BgProps = {
    className: string;
    content: ReactNode;
};

export function Background({className, content}: BgProps) {
    const [isDropDownOpen, setIsDropDownOpen] = useState<boolean>(false);
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
            <nav className="flex items-center justify-between p-4 shadow-md bg-surface">
                {/* Left side */}
                <H1 content="Artflow" />

                {/* Right side */}
                <div className="block sm:hidden">
                    <img src="/src/assets/icons/hamburger.svg" alt="menu" className="w-8 h-8 cursor-pointer" onClick={() => setIsDropDownOpen(!isDropDownOpen)} />
                </div>
                <div className="hidden sm:block flex space-x-6">

                    <SecondaryButton type="button" text="Home" onClick={() => nav("/projects")} />
                    <SecondaryButton type="button" text={`${authButtonText}`} onClick={handleAuthButton} />
                </div>
            </nav>

            <div className={`flex grow items-center justify-center ${className}`}>
                {isDropDownOpen && (
                    <div className="flex flex-col bg-background-50 w-full h-full items-center">
                        <NavDropDownRow content={<Text content="Home" />} onClick={() => nav("/projects")}/>
                        <NavDropDownRow content={<Text content={`${authButtonText}`} />} onClick={handleAuthButton} />
                    </div>
                )}
                {!isDropDownOpen && content}
            </div>
        </div>

    )
}

type NavDropDownRowProps = {
    onClick: () => void;
    content: ReactNode;
};


    function NavDropDownRow({content, onClick}: NavDropDownRowProps)  {
        return (
            <div className="hover:opacity-50 cursor-pointer p-2" onClick={onClick}>
                {content}
            </div>
        );
    }

export function BackgroundNoNav({className, content}: BgProps) {
    return (
        <div className={`min-h-screen bg-background flex items-center justify-center ${className}`}>
            {content}
        </div>
    )
}

export function BackgroundBorder({className, content}: BgProps) {
    return (
        <div className={`w-full rounded-2xl shadow-lg p-8 bg-background-50 ${className}`}>
            {content}
        </div>
    )
}


