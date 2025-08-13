import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {register} from "../api/auth.ts";
import {useAuth} from "../AuthContext.tsx"
import {EmailInput} from "../components/business/EmailInput.tsx";
import {ConfirmPasswordInput, PasswordInput} from "../components/business/PasswordInput.tsx";
import {Background, BackgroundBorder} from "../components/ui/Background.tsx";
import {ErrorText, H1} from "../components/ui/Text.tsx";
import {PrimaryButton} from "../components/ui/Button.tsx";

export function RegisterPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmedPassword, setConfirmedPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate();
    const {setAuth} = useAuth();

    const handleSubmit = (e) => {
        e.preventDefault();
        setError(null);

        if (!email || !password || !confirmedPassword) {
            setError("Please fill in all fields.");
            return;
        }

        if (password != confirmedPassword) {
            setError("Passwords do not match.");
            return;
        }

        register(email, password)
            .then((res) => {
                setAuth(res.data.token);
                nav("/projects");
            }).catch(err => setError(err.message))
    };

    return (
        <Background className="px-4" content={
            <BackgroundBorder className="max-w-md" content={
                <div>
                    <H1 content="Artflow" />
                    <form onSubmit={handleSubmit}>
                        <EmailInput email={email} setEmail={setEmail} />
                        <PasswordInput password={password} setPassword={setPassword} />
                        <ConfirmPasswordInput password={confirmedPassword} setPassword={setConfirmedPassword} />

                        {error && <ErrorText className="mb-4" content={error} />}

                        <PrimaryButton disabled={email.trim() === "" || password.trim() === ""} type="submit" text="Register" />
                    </form>
                </div>
            } />
        } />
    );
}
