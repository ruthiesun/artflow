import { Input } from "../ui/Input.tsx";

type ProjectNameInputProps = {
    name: string;
    setName: ((newValue: string) => void)
};

export function ProjectNameInput({ name, setName }: ProjectNameInputProps) {
    return (<Input label="Name" type="text" value={name} setValue={setName} />)
}
