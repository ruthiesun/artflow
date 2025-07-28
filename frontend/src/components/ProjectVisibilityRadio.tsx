import {RadioInput, labelClass} from "./Input.tsx";

type ProjectVisibilityRadioProps = {
    visibility: "public" | "private";
    setVisibility: ((newValue: "public" | "private") => void)
};

export function ProjectVisibilityRadio({ visibility, setVisibility }: ProjectVisibilityRadioProps) {
    return (
        <div className="mb-4">
            <label className={labelClass}>Visibility</label>
            <RadioInput label="Public" name="visibility" value={visibility} checked={visibility == "public"} onChange={() => setVisibility("public")} />
            <RadioInput label="Private" name="visibility" value={visibility} checked={visibility == "private"} onChange={() => setVisibility("private")} />
        </div>
    )
}
