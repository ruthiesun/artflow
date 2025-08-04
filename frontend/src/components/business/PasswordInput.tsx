import {Input} from "../ui/Input.tsx";

type PasswordInputProps = {
    password: string;
    setPassword: ((newValue: string) => void)
};

export function PasswordInput({ password, setPassword }: PasswordInputProps) {
    return GenericPasswordInput({password: password, setPassword: setPassword, label: "Password"});
}

export function ConfirmPasswordInput({ password, setPassword }: PasswordInputProps) {
    return GenericPasswordInput({password: password, setPassword: setPassword, label: "Confirm Password"});
}

function GenericPasswordInput({ password, setPassword, label}) {
    return (<Input label={label} type="password" value={password} setValue={setPassword} />)
}
