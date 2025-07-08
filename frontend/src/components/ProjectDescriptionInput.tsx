type ProjectDescriptionInputProps = {
    description: string;
    setDescription: ((newValue: string) => void)
};

export function ProjectDescriptionInput({ description, setDescription }: ProjectDescriptionInputProps) {
    return (
        <div>
            <label>Description</label>
            <input
                type="text"
                value={description}
                onChange={e => setDescription(e.target.value)}
            />
        </div>
    )
}
