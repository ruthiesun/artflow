import {useState} from "react";
import {SecondaryButton} from "../ui/Button.tsx";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../.././AuthContext.tsx";
import {H1, Text} from "./Text.tsx";

type BgProps = {
    className?: string;
    children: React.ReactNode;
};

export function Background({className, children}: BgProps) {
    const [isDropDownOpen, setIsDropDownOpen] = useState<boolean>(false);
    const {isAuthenticated, removeAuth} = useAuth();
    const nav = useNavigate();
    const isMobile = window.matchMedia("(max-width: 767px)").matches;

    const handleAuthButton = (() => {
        if (isAuthenticated) {
            removeAuth();
        }
        nav("/login");
    });


    const authButtonText = isAuthenticated ? "Logout" : "Login";

    return (
        <div className={`min-h-screen flex-col bg-background`}>
            <nav className="flex items-center justify-between p-4 shadow-lg bg-surface">
                {/* Left side */}
                <H1 content="Artflow" />

                {/* Right side */}
                <div className="block sm:hidden">
                    <img src="/src/assets/icons/hamburger.svg" alt="menu" className="w-8 h-8 cursor-pointer" onClick={() => setIsDropDownOpen(!isDropDownOpen)} />
                </div>
                <div className="hidden sm:block flex space-x-6">
                    <SecondaryButton type="button" text="Home" onClick={() => nav("/")} />
                    <SecondaryButton type="button" text={`${authButtonText}`} onClick={handleAuthButton} />
                </div>
            </nav>

            <div className="relative flex items-center justify-center pt-1">
                {isDropDownOpen && isMobile && (
                    <div className="absolute z-10 sm:hidden flex flex-col bg-background-50 w-full h-full items-center">
                        <NavDropDownRow children={<Text content="Home" />} onClick={() => {
                            setIsDropDownOpen(!isDropDownOpen);
                            nav("/projects");
                        }}/>
                        <NavDropDownRow children={<Text content={`${authButtonText}`} />} onClick={handleAuthButton} />
                    </div>
                )}
                {children}
            </div>
        </div>

    )
}

type NavDropDownRowProps = {
    onClick: () => void;
    children: React.ReactNode;
};


    function NavDropDownRow({children, onClick}: NavDropDownRowProps)  {
        return (
            <div className="hover:opacity-50 cursor-pointer p-2" onClick={onClick}>
                {children}
            </div>
        );
    }

export function BackgroundNoNav({className, children}: BgProps) {
    return (
        <div className={`min-h-screen bg-background flex items-center justify-center px-4 ${className}`}>
            {children}
        </div>
    )
}

export function BackgroundBorder({className, children}: BgProps) {
    return (
        <div className={`${className} w-full shadow-lg bg-background-50`}>
            {children}
        </div>
    )
}

export function BackgroundBorderSm({className, children}: BgProps) {
    return (
        <div className={`${className} w-full rounded-xl shadow-lg bg-background-50 max-w-md`}>
            {children}
        </div>
    )
}

export function EdgePadding({className, children}: BgProps) {
    return (
        <div className={`${className} p-2 md:p-4 lg:p-8`}>
            {children}
        </div>
    );
}


