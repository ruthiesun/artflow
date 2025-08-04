import {Input} from "../ui/Input.tsx";

type ProjectDescriptionInputProps = {
    description: string;
    setDescription: ((newValue: string) => void)
};

export function ProjectDescriptionInput({ description, setDescription }: ProjectDescriptionInputProps) {
    return (<Input label="Description" type="text" value={description} setValue={setDescription} />)
}
