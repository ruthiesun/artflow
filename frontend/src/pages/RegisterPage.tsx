import {useState} from "react";
import {useNavigate} from "react-router-dom";
import {EmailInput} from "../components/EmailInput.tsx";
import {ConfirmPasswordInput, PasswordInput} from "../components/PasswordInput.tsx";
import {register} from "../api/auth.ts";

export function RegisterPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmedPassword, setConfirmedPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate();
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
                localStorage.setItem("authToken", res.data.token);
                nav("/projects");
            }).catch(err => setError(err.message))
    };

    return (
        <div>
            <form onSubmit={handleSubmit}>
                <EmailInput email={email} setEmail={setEmail} />
                <PasswordInput password={password} setPassword={setPassword} />
                <ConfirmPasswordInput password={password} setPassword={setPassword} />

                {error && <p>{error}</p>}

                <button type="submit">
                    Register
                </button>
            </form>
        </div>
    );
}
