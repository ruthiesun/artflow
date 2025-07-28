import {useState} from "react";
import {Input, labelClass} from "./Input.tsx";
import {DisplayOnlyTagButton} from "./Button.tsx";


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
        <div className="mb-4">
            <Input label="Tags" type="text" value={currTag} setValue={setCurrTag}
            placeholder="Type a tag and press Enter" onKeyDown={addTag}/>

            <div>
                {tags.map(tag => (
                    <div key={tag} className="mb-1">
                        <DisplayOnlyTagButton type="text" text={tag} />
                        <button
                            onClick={() => removeTag(tag)}
                            className="text-delete-button hover:text-delete-button-hover"
                        >
                            ×
                        </button>
                    </div>
                ))}
            </div>
        </div>
    )
}
