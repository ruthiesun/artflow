import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import axios from "axios";

export function RegisterPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmedPassword, setConfirmedPassword] = useState('');
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

        axios
                .post("http://localhost:8080/api/auth/register", {email: email, password: password})
                .then((res) => {
                    localStorage.setItem('authToken', res.data.token);
                    nav('/projects');
                }).catch(err => setError(err.message))
    };

    return (
        <div>
            <div>
                <form onSubmit={handleSubmit}>
                    <div>
                        <label>Email</label>
                        <input
                            type="email"
                            value={email}
                            onChange={e => setEmail(e.target.value)}
                        />
                    </div>

                    <div>
                        <label>Password</label>
                        <input
                            type="password"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                        />
                    </div>

                    <div>
                        <label>Confirm Password</label>
                        <input
                            type="password"
                            value={confirmedPassword}
                            onChange={e => setConfirmedPassword(e.target.value)}
                        />
                    </div>

                    {error && <p>{error}</p>}

                    <button type="submit">
                        Register
                    </button>
                </form>
            </div>
            <div>

            </div>
        </div>
    );
}
