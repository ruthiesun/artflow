import {useEffect, useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {login} from "../api/auth.ts";
import {navToErrorPage} from "./ErrorPage.tsx";
import {useAuth} from "../AuthContext.tsx"
import {EmailInput} from "../components/business/EmailInput.tsx";
import {PasswordInput} from "../components/business/PasswordInput.tsx";
import {SubmitButton} from "../components/ui/Button.tsx";
import {Background, BackgroundBorder} from "../components/ui/Background.tsx";
import {ErrorText, H1} from "../components/ui/Text.tsx";

export function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate();
    const {setAuth} = useAuth();

    const handleSubmit = (e) => {
        e.preventDefault();
        setError(null);

        login(email, password)
            .then((res) => {
                setAuth(res.data.token);
                nav("/projects");
            })
            .catch(err => {
                if (err.response?.status === 401) {
                    setError("Invalid login credentials");
                }
                else {
                    navToErrorPage(nav, err);
                }
            });
    };

    return (
        <Background className="px-4" content={
            <BackgroundBorder className="max-w-md" content={
                <div>
                    <H1 content="Artflow" />
                    <form onSubmit={handleSubmit}>
                        <EmailInput email={email} setEmail={setEmail} />
                        <PasswordInput password={password} setPassword={setPassword} />

                        {error && <ErrorText className="mb-4" content={error} />}

                        <SubmitButton disabled={email.trim() === "" || password.trim() === ""} type="submit" text="Log In" />
                    </form>
                    <div>
                        <Link to="register" className="hover:text-primary-400 text-primary cursor-pointer">
                            Create an account
                        </Link>
                    </div>
                </div>
            } />
        } />
    );
}
