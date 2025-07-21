import {useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {EmailInput} from "../components/EmailInput.tsx";
import {PasswordInput} from "../components/PasswordInput.tsx";
import {login} from "../api/auth.ts";
import {SubmitButton} from "../components/Button.tsx";
import {Background, BackgroundBorder} from "../components/Background.tsx";
import {ErrorText, H1} from "../components/Text.tsx";

export function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate();

    const handleSubmit = (e) => {
        e.preventDefault();
        setError(null);

        login(email, password)
            .then((res) => {
                localStorage.setItem("authToken", res.data.token);
                nav("/projects");
            })
            .catch(err => {
                if (err.response?.status === 401) {
                    setError("Invalid login credentials")
                }
                else {
                    nav("/error", {
                        state: {
                            status: err.response?.status,
                            message: err.message,
                            details: err.response?.data
                        }
                    });
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
                        <Link to="register" className="hover:text-link-hover text-link">
                            Create an account
                        </Link>
                    </div>
                </div>
            } />
        } />
    );
}
