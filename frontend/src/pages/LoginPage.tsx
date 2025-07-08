import {useEffect, useState} from 'react';
import axios from "axios";
import {Link, useNavigate} from "react-router-dom";

export function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate();
    const handleLoginSubmit = (e) => {
        e.preventDefault();
        setError(null);

        if (!email || !password) {
            setError("Please fill in both fields.");
            return;
        }

        axios
                .post("http://localhost:8080/api/auth/login", {email: email, password: password})
                .then((res) => {
                        localStorage.setItem('authToken', res.data.token);
                        nav('/projects');
                }).catch(err => setError(err.message))
    };

    return (
        <div>
            <div>
                <form onSubmit={handleLoginSubmit}>
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
