import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { register } from "../api/auth/auth.ts";
import { EmailInput } from "../components/business/EmailInput.tsx";
import { ConfirmPasswordInput, PasswordInput } from "../components/business/PasswordInput.tsx";
import { BackgroundNoNav, BackgroundBorderSm, EdgePadding } from "../components/ui/Background.tsx";
import { ErrorText, H1 } from "../components/ui/Text.tsx";
import { PrimaryButton } from "../components/ui/Button.tsx";
import { Input } from "../components/ui/Input.tsx";
import { Validator } from "../Validator.ts";
import { navToErrorPage } from "./ErrorPage.tsx";

export function RegisterPage() {
    const [email, setEmail] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [confirmedPassword, setConfirmedPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const [validator, setValidator] = useState<Validator>();
    const nav = useNavigate();

    useEffect(() => {
        Validator.getInstance()
            .then((res) => {
                setValidator(res);
            })
            .catch((err) => {
                navToErrorPage({ nav, err });
            });
    }, []);

    const handleSubmit = (e: any) => {
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

        if (validator === undefined) {
            return;
        }

        if (!(new RegExp(validator.getUsernameRegex()).test(username))) {
            setError(validator.getUsernameMessage());
            return;
        }

        if (!(new RegExp(validator.getPasswordRegex()).test(password))) {
            setError(validator.getPasswordMessage());
            return;
        }

        register(email, username, password)
            .then(() => {
                nav("/register-success");
            }).catch(err => setError(err.response.data.error));
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
                            <PrimaryButton disabled={(email.trim() === "" || password.trim() === "") && validator !== undefined} type="submit" text="Register" />
                        </div>
                    </form>
                </EdgePadding>
            </BackgroundBorderSm>
        </BackgroundNoNav>
    );
}
