import {useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {EmailInput} from "../components/EmailInput.tsx";
import {PasswordInput} from "../components/PasswordInput.tsx";
import {login} from "../api/auth.ts";

export function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate();

    const handleSubmit = (e) => {
        e.preventDefault();
        setError(null);

        if (!email || !password) {
            setError("Please fill in both fields.");
            return;
        }

        login(email, password)
            .then((res) => {
                localStorage.setItem("authToken", res.data.token);
                nav("/projects");
            })
            .catch(err => setError(err.message));
    };

    return (
        <div>
            <div>
                <form onSubmit={handleSubmit}>
                    <EmailInput email={email} setEmail={setEmail} />
                    <PasswordInput password={password} setPassword={setPassword} />

                    {error && <p>{error}</p>}

                    <button type="submit">
                        Log In
                    </button>
                </form>
            </div>
            <div>
                <Link to='register'>
                    Create an account
                </Link>
            </div>
        </div>
    );
}
