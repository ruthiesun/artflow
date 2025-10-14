import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { SmallModal } from "../ui/Modal.tsx";
import { PrimaryButton } from "../ui/Button.tsx";
import { ErrorText, Text } from "../ui/Text.tsx";
import { requestReset } from "../../api/auth/auth.ts";
import { EmailInput } from "./EmailInput.tsx";

type ConfirmPasswordResetProps = {
    onClose: () => void;
};

export function ConfirmPasswordResetModal({ onClose }: ConfirmPasswordResetProps) {
    const [email, setEmail] = useState<string>("")
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate();

    const handleSubmit = (e: any) => {
        e.preventDefault();
        setError(null);

        requestReset(email)
            .then(() => {
                nav("/reset-request");
            })
            .catch(err => setError(err.message));
    };

    return (
        <SmallModal content={
            (
                <div>
                    <Text content="Type the email associated with your account. A link will be sent to the email, if the account exists." />
                    <form onSubmit={handleSubmit}>
                        <div className="mb-2">
                            <EmailInput email={email} setEmail={setEmail} />
                        </div>
                        {error && <ErrorText className="mb-4" content={error} />}
                        <PrimaryButton type="submit" text="Confirm" />
                    </form>
                </div>
            )
        } onClose={onClose} />
    )
}