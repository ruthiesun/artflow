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

// Username regex
const usernameRegex = /^(?=.*[a-z])[a-z0-9_-]+$/;

// Password regex
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

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

        if (!usernameRegex.test(username)) {
            setError("Username must be URL-friendly (lowercase letters, numbers, - and _ allowed)");
            return;
        }

        if (!(username.length >= 3) || !(username.length <= 20)) {
            setError("Username must be between 3 and 20 characters (inclusive)");
            return;
        }

        if (!passwordRegex.test(password)) {
            setError("Password must be 8+ chars and include uppercase, lowercase, number, special char");
            return;
        }
            
        register(email, username, password)
            .then(() => {
                nav("/register-success");
            }).catch(err => setError(err.message))
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
                            <Input label="Username" type="text" value={username} setValue={setUsername} />
                        </div>
                        <div className="mb-2">
                            <PasswordInput password={password} setPassword={setPassword} />
                        </div>
                        <div className="mb-2">
                            <ConfirmPasswordInput password={confirmedPassword} setPassword={setConfirmedPassword} />
                        </div>
                        
                        {error && <ErrorText className="mb-4" content={error} />}

                        <div className="mb-2">
                            <PrimaryButton disabled={email.trim() === "" || password.trim() === ""} type="submit" text="Register" />
                        </div>
                    </form>
                </EdgePadding>
            </BackgroundBorderSm>
        </BackgroundNoNav>
    );
}
