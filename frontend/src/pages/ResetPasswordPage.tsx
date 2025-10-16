import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { ConfirmPasswordInput, PasswordInput } from "../components/business/PasswordInput.tsx";
import { BackgroundNoNav, BackgroundBorderSm, EdgePadding } from "../components/ui/Background.tsx";
import { ErrorText, H1 } from "../components/ui/Text.tsx";
import { PrimaryButton } from "../components/ui/Button.tsx";
import { Validator } from "../Validator.ts";
import { navToErrorPage } from "./ErrorPage.tsx";
import { reset } from "../api/auth/auth.ts";

export function ResetPasswordPage() {
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token");
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

        if (!password || !confirmedPassword) {
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

        if (!(new RegExp(validator.getPasswordRegex()).test(password))) {
            setError(validator.getPasswordMessage());
            return;
        }

        if (!token) {
            return;
        }

        reset(password, token)
            .then(() => {
                nav("/login");
            }).catch(err => setError(err.response.data.error));
    };

    return (
        <BackgroundNoNav>
            <BackgroundBorderSm>
                <EdgePadding>
                    <H1 content="Artflow" />
                    <form onSubmit={handleSubmit}>
                        <div className="mb-2">
                            <PasswordInput password={password} setPassword={setPassword} />
                        </div>
                        <div className="mb-2">
                            <ConfirmPasswordInput password={confirmedPassword} setPassword={setConfirmedPassword} />
                        </div>

                        {error && <ErrorText className="mb-4" content={error} />}

                        <div className="mb-2">
                            <PrimaryButton disabled={(token == undefined || password.trim() === "") && validator !== undefined} type="submit" text="Update password" />
                        </div>
                    </form>
                </EdgePadding>
            </BackgroundBorderSm>
        </BackgroundNoNav>
    );
}
