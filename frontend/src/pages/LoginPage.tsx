import {useEffect, useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {login} from "../api/auth/auth.ts";
import {navToErrorPage} from "./ErrorPage.tsx";
import {EmailInput} from "../components/business/EmailInput.tsx";
import {PasswordInput} from "../components/business/PasswordInput.tsx";
import {PrimaryButton, SecondaryButton} from "../components/ui/Button.tsx";
import {BackgroundNoNav, BackgroundBorderSm, EdgePadding} from "../components/ui/Background.tsx";
import {ErrorText, H1} from "../components/ui/Text.tsx";
import { ConfirmPasswordResetModal } from "../components/business/ConfirmPasswordResetModal.tsx";

export function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [showModal, setShowModal] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate();

    const handleSubmit = (e) => {
        e.preventDefault();
        setError(null);

        login(email, password)
            .then((username: string) => {
                nav("/" + username + "/projects");
            })
            .catch(err => {
                setError(err.response.data.error);
            });
    };

    return (
        <BackgroundNoNav>
            <BackgroundBorderSm>
                <EdgePadding>
                    <H1 content="Artflow" />
                    <form onSubmit={handleSubmit}>
                        <div className="mb-2">
                            <EmailInput email={email} setEmail={setEmail} />
                        </div>
                        <div className="mb-2">
                            <PasswordInput password={password} setPassword={setPassword} />
                        </div>
                    
                        {error && <ErrorText className="mb-4" content={error} />}

                        <div className="mb-2">
                            <PrimaryButton disabled={email.trim() === "" || password.trim() === ""} type="submit" text="Log In" />
                        </div>
                    </form>
                    <div>
                        <div className="mb-2">
                            <SecondaryButton type="button" text="Forgot your password?" onClick={() => setShowModal(true)}/>
                        </div>
                        <Link to="/register" className="text-primary">
                            Create an account
                        </Link>
                    </div>
                </EdgePadding>
                {showModal && (
                    <ConfirmPasswordResetModal onClose={() => setShowModal(false)}/>
                )}
            </BackgroundBorderSm>
        </BackgroundNoNav>
    );
}
