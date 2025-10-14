import { Input } from "../ui/Input.tsx";

type EmailInputProps = {
    email: string;
    setEmail: ((newValue: string) => void)
};

export function EmailInput({ email, setEmail }: EmailInputProps) {
    return (<Input label="Email" type="email" value={email} setValue={setEmail} />)
}
