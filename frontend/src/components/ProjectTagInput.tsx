import {useState} from "react";

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
            <div>
                {tags.map(tag => (
                    <div key={tag}>
                        {tag}
                        <button
                            onClick={() => removeTag(tag)}
                            className="ml-2 text-red-500 hover:text-red-700"
                        >
                            ×
                        </button>
                    </div>
                ))}
            </div>

            <input
                className="border px-3 py-2 rounded w-full"
                placeholder="Type a tag and press Enter"
                value={currTag}
                onChange={e => setCurrTag(e.target.value)}
                onKeyDown={addTag}
            />
        </div>
    )
}
