import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {register} from "../api/auth.ts";
import {useAuth} from "../AuthContext.tsx"
import {EmailInput} from "../components/business/EmailInput.tsx";
import {ConfirmPasswordInput, PasswordInput} from "../components/business/PasswordInput.tsx";
import {BackgroundNoNav, BackgroundBorderSm, EdgePadding} from "../components/ui/Background.tsx";
import {ErrorText, H1} from "../components/ui/Text.tsx";
import {PrimaryButton} from "../components/ui/Button.tsx";
import { Input } from "../components/ui/Input.tsx";

export function RegisterPage() {
    const [email, setEmail] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [confirmedPassword, setConfirmedPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate();
    const {setAuth} = useAuth();

    const handleSubmit = (e) => {
        e.preventDefault();
        setError(null);

        if (!email || !username || !password || !confirmedPassword) {
            setError("Please fill in all fields.");
            return;
        }

        if (password != confirmedPassword) {
            setError("Passwords do not match.");
            return;
        }

        register(email, username, password)
            .then((res) => {
                setAuth(res.data.token, username);
                nav("/" + username + "/projects");
            }).catch(err => setError(err.message))
    };

    return (
        <BackgroundNoNav>
            <BackgroundBorderSm>
                <EdgePadding>
                    <H1 content="Artflow" />
                    <form onSubmit={handleSubmit}>
                        <EmailInput email={email} setEmail={setEmail} />
                        <Input label="Username" type="text" value={username} setValue={setUsername} />
                        <PasswordInput password={password} setPassword={setPassword} />
                        <ConfirmPasswordInput password={confirmedPassword} setPassword={setConfirmedPassword} />

                        {error && <ErrorText className="mb-4" content={error} />}

                        <PrimaryButton disabled={email.trim() === "" || password.trim() === ""} type="submit" text="Register" />
                    </form>
                </EdgePadding>
            </BackgroundBorderSm>
        </BackgroundNoNav>
    );
}
