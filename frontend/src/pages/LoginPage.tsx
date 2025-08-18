import {useEffect, useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {login} from "../api/auth.ts";
import {navToErrorPage} from "./ErrorPage.tsx";
import {useAuth} from "../AuthContext.tsx"
import {EmailInput} from "../components/business/EmailInput.tsx";
import {PasswordInput} from "../components/business/PasswordInput.tsx";
import {PrimaryButton} from "../components/ui/Button.tsx";
import {BackgroundNoNav, BackgroundBorderSm, EdgePadding} from "../components/ui/Background.tsx";
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
                setAuth(res.data.token, res.data.username);
                nav("/" + res.data.username + "/projects");
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
        <BackgroundNoNav>
            <BackgroundBorderSm>
                <EdgePadding>
                    <H1 content="Artflow" />
                    <form onSubmit={handleSubmit}>
                        <EmailInput email={email} setEmail={setEmail} />
                        <PasswordInput password={password} setPassword={setPassword} />

                        {error && <ErrorText className="mb-4" content={error} />}

                        <PrimaryButton disabled={email.trim() === "" || password.trim() === ""} type="submit" text="Log In" />
                    </form>
                    <div>
                        <Link to="/register" className="text-primary">
                            Create an account
                        </Link>
                    </div>
                </EdgePadding>
            </BackgroundBorderSm>
        </BackgroundNoNav>
    );
}
