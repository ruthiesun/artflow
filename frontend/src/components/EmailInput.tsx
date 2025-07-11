type EmailInputProps = {
    email: string;
    setEmail: ((newValue: string) => void)
};

export function EmailInput({ email, setEmail }: EmailInputProps) {
    return (
        <div>
            <label>Email</label>
            <input
                type="email"
                value={email}
                onChange={e => setEmail(e.target.value)}
            />
        </div>
    )
}
