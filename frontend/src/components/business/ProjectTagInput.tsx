import {useState} from "react";
import {Input, labelClass} from "../ui/Input.tsx";
import {DisplayOnlyTagButton} from "../ui/Button.tsx";


type ProjectTagInputProps = {
    tags: string[]
    setTags: ((newValue: string[]) => void)
};

export function ProjectTagInput({ tags, setTags }: ProjectTagInputProps) {
    const [currTag, setCurrTag] = useState<string>("");

    const addTag = (e) => {
        if ((e.key === "Enter" || e.key === ",") && currTag.trim() !== "") {
            e.preventDefault();
            const newTag = currTag.trim();
            if (!tags.includes(newTag)) {
                setTags([...tags, newTag]);
            }
            setCurrTag("");
        }
    };

    const removeTag = (tagToRemove: string) => {
        setTags(tags.filter(tag => tag !== tagToRemove));
    };

    return (
        <div>
            <Input label="Tags" type="text" value={currTag} setValue={setCurrTag} placeholder="Type a tag and press Enter" onKeyDown={addTag}/>

            <div className="mt-2">
                {tags.map(tag => (
                    <div key={tag} className="mb-1">
                        <DisplayOnlyTagButton type="text" text={tag} />
                        <button
                            onClick={() => removeTag(tag)}
                            className="text-warning cursor-pointer"
                        >
                            ×
                        </button>
                    </div>
                ))}
            </div>
        </div>
    )
}
